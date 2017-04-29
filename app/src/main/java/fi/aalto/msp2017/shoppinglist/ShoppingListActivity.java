package fi.aalto.msp2017.shoppinglist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
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

/*
 * Class - ShoppingListActivity
 * Hanles the different shopping lists created by the user
 */

public class ShoppingListActivity extends AppCompatActivity {
    private EditText edit_Title, edit_Search_Item;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final String listId = "-KhhVOK1epo5xzo_E4vY";
    private List<IItem> listItems = new ArrayList<>();
    private List<IItem> unassignedItems = new ArrayList<>();
    private List<IItem> assignedItems = new ArrayList<>();
    private GridView gvNotInList, gvUnassigned, gvAssigned;
    private ListItemAdapter notInListItemAdapter, unassignedAdapter, assignedAdapter;
    private static final String LOG_TAG = ShoppingListActivity.class.getSimpleName();
    DatabaseReference masterItemRef;
    DatabaseReference listItemRef;
    DatabaseReference selfRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);
        masterItemRef = database.getReference(getString(R.string.FBDB_MASTERITEMS));
        selfRef = database.getReference(getString(R.string.FBDB_USERS)).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        listItemRef = database.getReference(getString(R.string.FBDB_SHOPPINGLIST)).child(listId).child(getString(R.string.FBDB_ITEMS));
        edit_Title = (EditText) findViewById(R.id.title);
        edit_Search_Item = (EditText) findViewById(R.id.search);

        gvNotInList = (GridView) findViewById(R.id.gvNotInList);
        gvUnassigned = (GridView) findViewById(R.id.gvUnassigned);
        gvAssigned = (GridView) findViewById(R.id.gvAssigned);

        PopulateGVUnassigned();
        PopulateGVNotInList();

        edit_Search_Item.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                PopulateGVUnassigned();
                PopulateGVNotInList();
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
        findViewById(R.id.btnAssigned).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gvAssigned.setVisibility( gvAssigned.isShown()
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
//                listItemRef.push().setValue(new ListItem(edit_Title.getText().toString(), null));
            }
        });
        ((GridView) findViewById(R.id.gvUnassigned)).setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the GridView selected/clicked item text
                RemoveItemFromList(parent, position);

                return true;
            }
        });

        ((GridView) findViewById(R.id.gvAssigned)).setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the GridView selected/clicked item text
                RemoveItemFromList(parent, position);
                return true;
            }
        });
        ((GridView) findViewById(R.id.gvNotInList)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the GridView selected/clicked item text
                Log.d(LOG_TAG,""+position);
                MasterItem selectedItem = (MasterItem) parent.getItemAtPosition(position);
                Toast.makeText(ShoppingListActivity.this,selectedItem.getItemName(), Toast.LENGTH_SHORT).show();
                if(TextUtils.isEmpty(selectedItem.getItemKey())) {
                    String key = masterItemRef.push().getKey();
                    selfRef.child("items").child(key).setValue(selectedItem);
                    selectedItem.setItemKey(key);
                }
                listItemRef.push().setValue(new ListItem(selectedItem.getItemName(), null, selectedItem.getItemKey()));
                listItems.remove(selectedItem);
                notInListItemAdapter.notifyDataSetChanged();
            }
        });
    }

    private void RemoveItemFromList(AdapterView<?> parent, int position) {
        ListItem selectedItem = (ListItem) parent.getItemAtPosition(position);
        Log.d(LOG_TAG,"Deleted Item : " + selectedItem.getItemKey());
        listItemRef.orderByChild("itemKey").equalTo(selectedItem.getItemKey()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
                            ds.getRef().setValue(null);
                            PopulateGVNotInList();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("TodoApp", "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    private void PopulateGVUnassigned(){
        Log.d(LOG_TAG, "PopulateGVUnassigned: ");
        unassignedAdapter = new ListItemAdapter(this, R.layout.list_item_view, unassignedItems);
        assignedAdapter = new ListItemAdapter(this, R.layout.list_item_view, assignedItems);
        final String searchtext = edit_Search_Item.getText().toString().trim();
        listItemRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                unassignedItems.clear();
                assignedItems.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ListItem item = snapshot.getValue(ListItem.class);
                    if(item.getSearchResult(searchtext)) {
                        if(TextUtils.isEmpty(item.getOwner())) {
                            unassignedItems.add(item);
                        }
                        else {
                            assignedItems.add(item);
                        }
                    }
                }
                unassignedAdapter.notifyDataSetChanged();
                assignedAdapter.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        gvUnassigned.setAdapter(unassignedAdapter);
        gvAssigned.setAdapter(assignedAdapter);
    }
    private void PopulateGVNotInList() {
        Log.d(LOG_TAG, "PopulateGVNotInList: ");
        notInListItemAdapter = new ListItemAdapter(this, R.layout.list_item_view, listItems);
        final String searchtext = edit_Search_Item.getText().toString().trim();
        final List<IItem> myItems = new ArrayList<>();
        masterItemRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listItems.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MasterItem item = snapshot.getValue(MasterItem.class);
                    item.setItemKey(snapshot.getKey());
                    if(item.getSearchResult(searchtext)) {
                        boolean present = isPresentInList(item);
                        if(!present) {
                            listItems.add(item);
                        }
                    }
                }
                notInListItemAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        selfRef.child("items").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MasterItem item = snapshot.getValue(MasterItem.class);
                    item.setItemKey(snapshot.getKey());
                    boolean inMasterList = false;
                    if(item.getSearchResult(searchtext)) {
                        for(final IItem li : listItems){
                            if(li.getItemKey().equals((item.getItemKey()))) {
                                inMasterList = true;
                                break;
                            }

                        }
                        boolean present = isPresentInList(item);
                        if(!present && !inMasterList) {
                            listItems.add(item);
                        }
                    }
                }
                if(listItems.size()==0 && !searchtext.isEmpty()) {
                    MasterItem item = new MasterItem(searchtext);
                    listItems.add(item);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        gvNotInList.setAdapter(notInListItemAdapter);
    }

    private boolean isPresentInList(MasterItem item) {
        for(final IItem li : unassignedItems) {
            if(li.getItemKey().equals(item.getItemKey())) {
                return true;
            }
        }
        return false;
    }
}