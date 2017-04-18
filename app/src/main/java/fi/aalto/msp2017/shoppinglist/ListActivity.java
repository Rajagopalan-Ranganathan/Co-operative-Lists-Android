package fi.aalto.msp2017.shoppinglist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import fi.aalto.msp2017.shoppinglist.adapters.ListItemAdapterRV;
import fi.aalto.msp2017.shoppinglist.models.IItem;
import fi.aalto.msp2017.shoppinglist.models.ListItem;

public class ListActivity extends AppCompatActivity {

    private RecyclerView rv;
    ListItemAdapterRV rvadapter;
    DatabaseReference masterItemRef;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    protected static List<IItem> inListItems = new ArrayList<>();
    private TextView searchTxt;
    DatabaseReference listItemRef;
    protected static final String LOG_TAG = "TabFragment 1";
    final String listId = "-KhhVOK1epo5xzo_E4vY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        masterItemRef = database.getReference(getString(R.string.FBDB_MASTERITEMS));
        listItemRef = database.getReference(getString(R.string.FBDB_SHOPPINGLIST)).child(listId).child(getString(R.string.FBDB_ITEMS));
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

        rvadapter = new ListItemAdapterRV(this, inListItems,"INLIST");
        final String searchtext = searchTxt.getText().toString();
        Log.d(LOG_TAG, "SearchText: "+searchtext);
        listItemRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                inListItems.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ListItem item = snapshot.getValue(ListItem.class);
                    if(item.getSearchResult(searchtext)) {
                        inListItems.add(item);

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
}


