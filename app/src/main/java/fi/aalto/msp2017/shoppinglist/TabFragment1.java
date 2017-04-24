package fi.aalto.msp2017.shoppinglist;

import android.content.Intent;
import android.location.Location;
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

import com.google.android.gms.common.api.GoogleApiClient;
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


/**

 */
public class TabFragment1 extends Fragment {
    private RecyclerView rv;
    ListItemAdapterRV rvadapter;
    DatabaseReference masterItemRef;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;

    protected static List<IItem> inListItems = new ArrayList<>();
    private TextView searchTxt;
    DatabaseReference listItemRef;
    protected static final String LOG_TAG = "TabFragment 1";
    String listId = "";
    private String ownerId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tab_fragment1, container, false);


        Intent i = getActivity().getIntent();
        Bundle extras = i.getExtras();
        listId = extras.get("LISTID").toString();
        ownerId = extras.get("OWNERID").toString();
        masterItemRef = database.getReference(getString(R.string.FBDB_MASTERITEMS));
        listItemRef = database.getReference(getString(R.string.FBDB_SHOPPINGLIST)).child(listId).child(getString(R.string.FBDB_ITEMS));
        searchTxt = (TextView)getActivity().findViewById(R.id.search);
        rv = (RecyclerView) v.findViewById(R.id.rv_List);
        LinearLayoutManager llm = new LinearLayoutManager(v.getContext());
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

        return v;
    }

    private void PopulateGVInList(){

        rvadapter = new ListItemAdapterRV(this.getContext(), inListItems,"INLIST", listId, ownerId);
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