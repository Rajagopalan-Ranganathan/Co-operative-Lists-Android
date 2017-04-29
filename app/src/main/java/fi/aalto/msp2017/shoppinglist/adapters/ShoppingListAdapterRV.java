package fi.aalto.msp2017.shoppinglist.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import fi.aalto.msp2017.shoppinglist.R;
import fi.aalto.msp2017.shoppinglist.ShoppingListActivityTab;
import fi.aalto.msp2017.shoppinglist.models.ShoppingList;
import fi.aalto.msp2017.shoppinglist.models.User;

/**
 * Created by raj on 18/4/17.
 */

/*
 * Shopping list - Recycler view
 */

public class ShoppingListAdapterRV extends RecyclerView.Adapter<ShoppingListAdapterRV.ListHolder> {
    private Context context;
    private List<ShoppingList> listItems;
    DatabaseReference shoppingListRef;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    static class ListHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView text;
        CardView cv;

        public ListHolder(View itemView) {
            super(itemView);
            itemView.getContext();
            text = (TextView) itemView.findViewById(R.id.slcv_list_name);
            thumbnail = (ImageView) itemView.findViewById(R.id.slcv_icon);

            cv = (CardView) itemView.findViewById(R.id.slcv);
        }
    }
    private static final String LOG_TAG = ShoppingListAdapterRV.class.getSimpleName();
    public ShoppingListAdapterRV(Context context,
                             List<ShoppingList> listItems) {
        this.context = context;
        this.listItems = listItems;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public ListHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shopping_list_cv, viewGroup, false);
        ListHolder pvh = new ListHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(final ListHolder viewHolder, final int i) {
        final ShoppingList listItemEntry = listItems.get(i);
        viewHolder.thumbnail.setImageResource(context.getResources().getIdentifier(listItemEntry.getImageName(), "drawable", context.getPackageName()));
        viewHolder.text.setText(listItemEntry.getListName());
        viewHolder.cv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete List")
                        .setMessage("Do you really want to delete the shopping list?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                DeleteShoppingList(listItemEntry);
                            }})
                        .setNegativeButton(android.R.string.no, null).show();


                return true;
            }
        });

        // on single lick - new intent displaying the list clicked
        viewHolder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShoppingList selectedItem = (ShoppingList) listItemEntry;
                if(TextUtils.isEmpty(selectedItem.getListID())) {
                    DatabaseReference listRef = database.getReference("shoppinglist");
                    String listID = listRef.push().getKey();
                    selectedItem.setListID(listID);
                    listRef.child(listID).setValue(selectedItem);
                    AddUser(listID, selectedItem.getOwner());
                }
                Intent listItemIntent = new     Intent(context, ShoppingListActivityTab.class);
                listItemIntent.putExtra("LISTID", selectedItem.getListID());
                Log.d(LOG_TAG, selectedItem.getListID()+":"+selectedItem.getOwner());
                listItemIntent.putExtra("OWNERID", selectedItem.getOwner());
                context.startActivity(listItemIntent);
                }});
    }

    // Delete the shopping list method
    private void DeleteShoppingList(ShoppingList listItemEntry) {
        ShoppingList selectedItem = listItemEntry;
        Log.d(LOG_TAG, "Deleted Item : " + selectedItem.getListName());
        if(selectedItem.getOwner().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            DatabaseReference listRef = database.getReference("shoppinglist").child(selectedItem.getListID());
            listRef.setValue(null);
        }
        else {
            Log.d(LOG_TAG, "Cannot delete list : " + selectedItem.getListName());
            Toast.makeText(context,"Only owner can delete the shopping list", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    //Add an user to the shopping list
    private void AddUser(String listID, final String ownerID) {

        final DatabaseReference userRef = database.getReference("users").child(ownerID);
        final DatabaseReference shoppingListMemberRef =database.getReference("shoppinglist").child(listID).child("members");

        userRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if( dataSnapshot.getValue() == null) {
                            Log.d(LOG_TAG,"User not available");
                        }
                        else {
                                User member = dataSnapshot.getValue(User.class);
                                shoppingListMemberRef.child(ownerID).setValue(member);
                                Log.d(LOG_TAG, dataSnapshot.getValue().toString());

                            Log.d(LOG_TAG,dataSnapshot.getKey().toString());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("TodoApp", "getUser:onCancelled", databaseError.toException());
                    }

                });
    }

}
