package fi.aalto.msp2017.shoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
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
import fi.aalto.msp2017.shoppinglist.models.MasterItem;


/*
 * Class - TabFragment2
 */

public class TabFragment2 extends Fragment {
    private RecyclerView rv;
    ListItemAdapterRV rvadapter;
    DatabaseReference masterItemRef;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private List<IItem> notInlistItems = new ArrayList<>();
    private List<IItem> inListItems =  new ArrayList<>();
    private TextView searchTxt;
    private String ownerId;

    DatabaseReference listItemRef;
    DatabaseReference selfRef;

    protected static final String LOG_TAG = "TabFragment 2";
    String listId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tab_fragment2, container, false);
        Intent i = getActivity().getIntent();
        Bundle extras = i.getExtras();

        listId = extras.get("LISTID").toString();
        ownerId = extras.get("OWNERID").toString();
        masterItemRef = database.getReference(getString(R.string.FBDB_MASTERITEMS));
        listItemRef = database.getReference(getString(R.string.FBDB_SHOPPINGLIST)).child(listId).child(getString(R.string.FBDB_ITEMS));
        selfRef = database.getReference(getString(R.string.FBDB_USERS)).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        searchTxt = (TextView)getActivity().findViewById(R.id.search);

        rv = (RecyclerView) v.findViewById(R.id.rv_NotInList);
        LinearLayoutManager llm = new LinearLayoutManager(v.getContext());
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        GetAllItemsInList();

        PopulateGVNotInList();


        searchTxt.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                GetAllItemsInList();
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
        return v;
    }
    private void GetAllItemsInList()
    {
        listItemRef = database.getReference(getString(R.string.FBDB_SHOPPINGLIST)).child(listId).child(getString(R.string.FBDB_ITEMS));
        listItemRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                inListItems.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ListItem item = snapshot.getValue(ListItem.class);
                    inListItems.add(item);
                }
                Log.d(LOG_TAG,""+inListItems.size());
                PopulateGVNotInList();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void PopulateGVNotInList() {
        Log.d(LOG_TAG, "PopulateGVNotInList: ");
        rvadapter = new ListItemAdapterRV(this.getContext(), notInlistItems,"NOTINLIST", listId, ownerId);
        final String searchtext = searchTxt.getText().toString();
        final List<IItem> myItems = new ArrayList<>();

        masterItemRef = database.getReference("masteritems");
        masterItemRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                notInlistItems.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MasterItem item = snapshot.getValue(MasterItem.class);
                    item.setItemKey(snapshot.getKey());
                    if(item.getSearchResult(searchtext)) {
                        boolean present = isPresentInList(item);
                        if(!present) {
                            notInlistItems.add(item);
                        }
                    }
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        selfRef = database.getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        selfRef.child("items").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean present = false;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MasterItem item = snapshot.getValue(MasterItem.class);
                    item.setItemKey(snapshot.getKey());
                    boolean inMasterList = false;
                    if(item.getSearchResult(searchtext)) {
                        for(final IItem li : inListItems){
                            if(li.getItemKey().equals((item.getItemKey()))) {
                                inMasterList = true;
                                break;
                            }
                        }
                        Log.d(LOG_TAG,item.getItemName()+ present);
                        present = isPresentInList(item);
                        if(!present && !inMasterList) {
                            notInlistItems.add(item);
                        }
                    }
                }
                if(notInlistItems.size()==0 && !searchtext.isEmpty() && !present) {
                    MasterItem item = new MasterItem(searchtext);
                    notInlistItems.add(item);
                }
                rvadapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        rv.setAdapter(rvadapter);
    }
    private boolean isPresentInList(MasterItem item) {
        for(final IItem li : inListItems) {
            if(li.getItemKey().equals(item.getItemKey())) {
                return true;
            }
        }
        return false;
    }
}
