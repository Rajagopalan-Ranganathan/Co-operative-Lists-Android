package fi.aalto.msp2017.shoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import fi.aalto.msp2017.shoppinglist.adapters.ShoppingListAdapterRV;
import fi.aalto.msp2017.shoppinglist.models.ShoppingList;

public class ListActivity extends AppCompatActivity {

    private RecyclerView rv;
    ShoppingListAdapterRV rvadapter;
    DatabaseReference shoppingListRef;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    protected static List<ShoppingList> listItems = new ArrayList<>();
    private TextView searchTxt;
    protected static final String LOG_TAG = "TabFragment 1";
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        searchTxt = (TextView)findViewById(R.id.slcv_search);
        rv = (RecyclerView)findViewById(R.id.rv_shop_list);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);
        PopulateGVInList();
        searchTxt.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                PopulateGVInList();
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
            }
        });

    }

    private void PopulateGVInList(){

        rvadapter = new ShoppingListAdapterRV(this, listItems);
        final String searchtext = searchTxt.getText().toString();
        Log.d(LOG_TAG, "SearchText: "+searchtext);
        shoppingListRef = database.getReference(getString(R.string.FBDB_SHOPPINGLIST));

        shoppingListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listItems.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ShoppingList item = snapshot.getValue(ShoppingList.class);
                    if(item.getSearchResult(searchtext)) {
                        listItems.add(item);

                    }
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
                Intent intent1 = new Intent(ListActivity.this, AccountActivity.class);
                startActivity(intent1);
                break;
            case R.id.action_logout:
                // sign out
                mAuth= FirebaseAuth.getInstance();
                mAuth.signOut();
                LoginManager.getInstance().logOut();
                Intent intent = new Intent(ListActivity.this, MainActivity.class);
                Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}


