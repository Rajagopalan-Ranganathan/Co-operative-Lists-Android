package fi.aalto.msp2017.shoppinglist;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Callback;

import java.util.Map;

public class AccountActivity extends AppCompatActivity {

    RelativeLayout profileContent;
    TextView tvMainName;
    ImageView mainUserpic;
    String provider;
    Button btnUpd;
    EditText pass;

    //Firebase authentication objects
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        profileContent = (RelativeLayout) findViewById(R.id.profileContent);
        tvMainName = (TextView) findViewById(R.id.tvProfileName);
        mainUserpic = (ImageView) findViewById(R.id.profileUserpic);
        mAuth = FirebaseAuth.getInstance();
        btnUpd=(Button) findViewById(R.id.btnUpdate);
        pass=(EditText) findViewById(R.id.etSignupPassword);

        final Callback callback = new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
            }
        };

        String userId = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> map = (Map) dataSnapshot.getValue();
                String name = map.get("name");
                String imageUrl = map.get("imageUrl");
                for (UserInfo user: FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
                    provider=user.getProviderId();
                }
                tvMainName.setText(name);
                if (provider.equals("password"))
                {
                    mainUserpic.setImageResource(R.drawable.user);
                }
                else
                {
                    Picasso.with(AccountActivity.this).load(imageUrl).fit().centerCrop()
                            .transform(new CircleTransform()).into(mainUserpic, callback);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // menu items
        switch (id) {
            case R.id.action_account:
                Intent intent1 = new Intent(AccountActivity.this, AccountActivity.class);
                startActivity(intent1);
                btnUpd.setVisibility(View.INVISIBLE);
                pass.setVisibility(View.INVISIBLE);
            case R.id.action_upd:
                btnUpd.setVisibility(View.VISIBLE);
                pass.setVisibility(View.VISIBLE);
                btnUpd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String new_password = pass.getText().toString();
                        user.updatePassword(new_password)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(AccountActivity.this, "User password updated.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                });
                break;

            case R.id.action_logout:
                // sign out
                mAuth= FirebaseAuth.getInstance();
                mAuth.signOut();
                LoginManager.getInstance().logOut();
                Intent intent = new Intent(AccountActivity.this, MainActivity.class);
                Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

