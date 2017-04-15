package fi.aalto.msp2017.shoppinglist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import fi.aalto.msp2017.shoppinglist.adapters.ListItemAdapter;
import fi.aalto.msp2017.shoppinglist.models.IItem;
import fi.aalto.msp2017.shoppinglist.models.ListItem;
import fi.aalto.msp2017.shoppinglist.models.MasterItem;
import fi.aalto.msp2017.shoppinglist.models.ShoppingList;

public class ShoppingListActivity extends AppCompatActivity {
    private EditText edit_Title, edit_Search_Item;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final String listId = "-KhhVOK1epo5xzo_E4vY";
    private List<IItem> listItems = new ArrayList<>();
    private List<IItem> unassignedItems = new ArrayList<>();
    private List<IItem> assignedItems = new ArrayList<>();
    private GridView gvNotInList, gvUnassigned;
    private ListItemAdapter notInListItemAdapter, unassignedAdapter, assignedAdapter;
    private static final String LOG_TAG = ShoppingListActivity.class.getSimpleName();
    DatabaseReference masterItemRef;
    DatabaseReference listItemRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);
        masterItemRef = database.getReference("masteritems");
        listItemRef = database.getReference("shoppinglist").child(listId).child("items");
        edit_Title = (EditText) findViewById(R.id.title);
        edit_Search_Item = (EditText) findViewById(R.id.search);

        gvNotInList = (GridView) findViewById(R.id.gvNotInList);
        gvUnassigned = (GridView) findViewById(R.id.gvUnassigned);
        PopulateGVUnassigned();
        PopulateGVNotInList();

        edit_Search_Item.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                PopulateGVNotInList();
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
            }
        });

        findViewById(R.id.btnUnassigned).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gvUnassigned.setVisibility( gvUnassigned.isShown()
                        ? View.GONE
                        : View.VISIBLE );
            }
        });
        findViewById(R.id.btnNotInList).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    gvNotInList.setVisibility( gvNotInList.isShown()
                            ? View.GONE
                            : View.VISIBLE );
            }
        });
        findViewById(R.id.createNew).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference listRef = database.getReference("shoppinglist");
                listRef.push().setValue(new ShoppingList(edit_Title.getText().toString(),  FirebaseAuth.getInstance().getCurrentUser().getUid()));
            }
        });

        findViewById(R.id.createNewItem).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference listItemRef = database.getReference("masteritems");
                listItemRef.push().setValue(new ListItem(edit_Title.getText().toString(), null));
            }
        });

        ((GridView) findViewById(R.id.gvNotInList)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the GridView selected/clicked item text
                Log.d(LOG_TAG,""+position);
                MasterItem selectedItem = (MasterItem) parent.getItemAtPosition(position);
                Toast.makeText(ShoppingListActivity.this,selectedItem.getItemName(), Toast.LENGTH_SHORT).show();
                listItemRef.push().setValue(new ListItem(selectedItem.getItemName(), null));
                listItems.remove(selectedItem);
                notInListItemAdapter.notifyDataSetChanged();
            }
        });


    }

    private void PopulateGVUnassigned(){
        unassignedAdapter = new ListItemAdapter(this, R.layout.list_item_view, unassignedItems);
        final String searchtext = edit_Search_Item.getText().toString().trim();
        listItemRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                unassignedItems.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ListItem item = snapshot.getValue(ListItem.class);
                    if(item.getSearchResult(searchtext))
                        unassignedItems.add(item);
                    Log.d(LOG_TAG, "List Item Added: " + unassignedItems.size());
                }
                unassignedAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        gvUnassigned.setAdapter(unassignedAdapter);
    }
    private void PopulateGVNotInList() {
        notInListItemAdapter = new ListItemAdapter(this, R.layout.list_item_view, listItems);
        final String searchtext = edit_Search_Item.getText().toString().trim();
        masterItemRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listItems.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MasterItem item = snapshot.getValue(MasterItem.class);
                    if(item.getSearchResult(searchtext)) {
                        boolean add = true;
                        for(final IItem li : unassignedItems) {
                            if(li.getItemName().equals(item.getItemName())) {
                                add = true;
                                break;
                            }
                        }
                        if(add) {
                            listItems.add(item);
                        }
                    }
                    //Log.d(LOG_TAG, "List Item Added: " + listItems.size());
                }
                if(listItems.size()==0 && !searchtext.isEmpty()) {
                    MasterItem item = new MasterItem(searchtext);
                    listItems.add(item);
                }
                notInListItemAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        gvNotInList.setAdapter(notInListItemAdapter);
        //historyListView.setAdapter(listItemAdapter);
    }
}
