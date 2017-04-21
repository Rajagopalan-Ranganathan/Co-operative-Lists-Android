package fi.aalto.msp2017.shoppinglist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import fi.aalto.msp2017.shoppinglist.adapters.MembersAdapterRV;
import fi.aalto.msp2017.shoppinglist.models.User;

public class ShoppingListMemberActivity extends AppCompatActivity {
    DatabaseReference shoppingListMemberRef;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    protected static List<User> memberItems = new ArrayList<>();
    final String listId = "-KhhVOK1epo5xzo_E4vY";
    protected static final String LOG_TAG = "TabFragment 1";
    private RecyclerView rv;
    MembersAdapterRV rvadapter;
    TextView tv_Email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list_member);

        rv = (RecyclerView) findViewById(R.id.rv_ShoppingListMembers);
        tv_Email = (TextView) findViewById(R.id.tv_members_add);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);
//        shoppingListMemberRef =database.getReference(getString(R.string.FBDB_SHOPPINGLIST)).child(listId).child("members");
//        shoppingListMemberRef.push().setValue(new User("Sunil Kumar Mohanty", "sunilkumarmohanty@gmail.com","url"));

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
        rvadapter = new MembersAdapterRV(this, memberItems);
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
}
