package fi.aalto.msp2017.shoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import fi.aalto.msp2017.shoppinglist.adapters.MembersAdapterRV;
import fi.aalto.msp2017.shoppinglist.models.User;

/*
 * Class - shoppingListMemberActivity
 * Handles the shopping list members
 */
public class ShoppingListMemberActivity extends AppCompatActivity {
    DatabaseReference shoppingListMemberRef;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    protected static List<User> memberItems = new ArrayList<>();
    String listId;
    protected static final String LOG_TAG = "Shopping List Member";
    private RecyclerView rv;
    MembersAdapterRV rvadapter;
    TextView tv_Email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list_member);
        Intent i = getIntent();
        Bundle extras = i.getExtras();
        listId = extras.get("LISTID").toString();

        rv = (RecyclerView) findViewById(R.id.rv_ShoppingListMembers);
        tv_Email = (TextView) findViewById(R.id.tv_members_add);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);


        PopulateMembers();

        findViewById(R.id.members_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference listItemRef = database.getReference("masteritems");
                shoppingListMemberRef =database.getReference(getString(R.string.FBDB_SHOPPINGLIST)).child(listId).child("members");
                shoppingListMemberRef.orderByChild("email").equalTo(tv_Email.getText().toString()).addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if( dataSnapshot.getValue() == null) {
                                    AddUser(tv_Email.getText().toString());
                                }
                                else {
                                    Toast.makeText(getApplicationContext(),"User already a member", Toast.LENGTH_SHORT ).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w("TodoApp", "getUser:onCancelled", databaseError.toException());
                            }

                        });
            }
        });
    }

    private void AddUser(String email) {

        final DatabaseReference userRef = database.getReference("users");
        userRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if( dataSnapshot.getValue() == null) {
                            Toast.makeText(getApplicationContext(),"User not available", Toast.LENGTH_SHORT ).show();
                        }
                        else {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                User member = ds.getValue(User.class);
                                shoppingListMemberRef =database.getReference(getString(R.string.FBDB_SHOPPINGLIST)).child(listId).child("members");
                                shoppingListMemberRef.child(ds.getKey().toString()).setValue(member);
                                Log.d(LOG_TAG, ds.getValue().toString());
                            }
                            Log.d(LOG_TAG,dataSnapshot.getKey().toString());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("TodoApp", "getUser:onCancelled", databaseError.toException());
                    }

                });

    }

    private void PopulateMembers() {
        rvadapter = new MembersAdapterRV(this, memberItems, listId);
        shoppingListMemberRef =database.getReference(getString(R.string.FBDB_SHOPPINGLIST)).child(listId).child("members");

        shoppingListMemberRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                memberItems.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User item = snapshot.getValue(User.class);
                    memberItems.add(item);
                }
                rvadapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        rv.setAdapter(rvadapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                try {
                    LoginManager.getInstance().logOut();
                    FirebaseAuth.getInstance().signOut();

                }
                catch(Exception ex){}
                Intent mainintent = new Intent(ShoppingListMemberActivity.this, MainActivity.class);
                mainintent.setFlags(mainintent.getFlags());
                startActivity(mainintent);
                finish();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
}
