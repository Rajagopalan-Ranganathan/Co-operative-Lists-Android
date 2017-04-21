package fi.aalto.msp2017.shoppinglist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
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
                tvMainName.setText(name);
                Picasso.with(AccountActivity.this).load(imageUrl).fit().centerCrop()
                        .transform(new CircleTransform()).into(mainUserpic, callback);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // menu items
        switch (id) {
            case R.id.action_profile:
                Intent intent1 = new Intent(AccountActivity.this, AccountActivity.class);
                startActivity(intent1);
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

