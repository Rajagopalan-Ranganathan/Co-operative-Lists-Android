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
import android.view.MenuItem;
import android.widget.TextView;

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

/*
 * Class - ListActvity
 * Handles the shopping lists
 */
public class ListActivity extends AppCompatActivity {

    private RecyclerView rv;
    ShoppingListAdapterRV rvadapter;
    DatabaseReference shoppingListRef;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    protected static List<ShoppingList> listItems = new ArrayList<>();
    private TextView searchTxt;
    protected static final String LOG_TAG = "LISTACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        searchTxt = (TextView)findViewById(R.id.sl_search);
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
          Log.d(LOG_TAG,"USEREMAIL:"+FirebaseAuth.getInstance().getCurrentUser().getEmail());
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        shoppingListRef.orderByChild("members/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/email").equalTo(email).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listItems.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d(LOG_TAG,snapshot.getValue().toString());
                    ShoppingList item = snapshot.getValue(ShoppingList.class);
                    item.setListID(snapshot.getKey());
                    if(item.getSearchResult(searchtext)) {
                        listItems.add(item);
                    }
                }

                if(listItems.size()==0 && !searchtext.isEmpty()) {
                    ShoppingList item = new ShoppingList(searchtext, FirebaseAuth.getInstance().getCurrentUser().getUid());
                    listItems.add(item);
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
                Intent mainintent = new Intent(ListActivity.this, MainActivity.class);
                mainintent.setFlags(mainintent.getFlags());
                startActivity(mainintent);
                finish();
                return true;
            case R.id.action_account:
                Intent intent = new Intent(ListActivity.this, AccountActivity.class);
                startActivity(intent);
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
}


