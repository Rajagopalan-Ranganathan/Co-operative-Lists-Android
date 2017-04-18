package fi.aalto.msp2017.shoppinglist.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import fi.aalto.msp2017.shoppinglist.R;
import fi.aalto.msp2017.shoppinglist.models.ShoppingList;

/**
 * Created by raj on 18/4/17.
 */


public class ShoppingListAdapterRV extends RecyclerView.Adapter<ShoppingListAdapterRV.ListHolder> {
    private Context context;
    private List<ShoppingList> listItems;
    DatabaseReference listItemRef;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    static class ListHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView text;
       // TextView txtDetails;
        CardView cv;

        public ListHolder(View itemView) {
            super(itemView);
            itemView.getContext();
            text = (TextView) itemView.findViewById(R.id.slcv_list_name);
            thumbnail = (ImageView) itemView.findViewById(R.id.slcv_icon);

            cv = (CardView) itemView.findViewById(R.id.slcv);
        }
    }
    private static final String LOG_TAG = ListItemAdapter.class.getSimpleName();
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
       // viewHolder.txtDetails.setText(listItemEntry.GetMoreDetails());
        viewHolder.cv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
//                if(type.equals("INLIST")) {
                    ShoppingList selectedItem = (ShoppingList) listItemEntry;
//                    Log.d(LOG_TAG, "Deleted Item : " + selectedItem.getListName());
//                    listItemRef = database.getReference("shoppinglist").child(listId).child("items");
//                    listItemRef.orderByChild("itemKey").equalTo(selectedItem.getItemKey()).addListenerForSingleValueEvent(
//                            new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                                        ds.getRef().setValue(null);
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//                                    Log.w("TodoApp", "getUser:onCancelled", databaseError.toException());
//                                }
//
//                            });
//                }
                return true;
            }
        });
//        viewHolder.cv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(type.equals("NOTINLIST")) {
//                    MasterItem selectedItem = (MasterItem) listItemEntry;
//                    if(TextUtils.isEmpty(selectedItem.getItemKey())) {
//                        selectedItem.SaveToDB();
//                    }
//                    ListItem li = new ListItem(selectedItem.getItemName(), null, selectedItem.getItemKey());
//                    li.SaveToDB(listId);
//                    listItems.remove(selectedItem);
//                }
//                else if(type.equals("INLIST")) {
//                    Intent intent = new Intent(context, ListItemDetailsActivity.class);
//                    ListItem li = (ListItem)listItemEntry;
//                    intent.putExtra("LISTITEM", li.getItemKey());
//                    intent.putExtra("LISTID", listId);
//                    context.startActivity(intent);
//                }
//            }});
    }
    @Override
    public int getItemCount() {
        return listItems.size();
    }
}
