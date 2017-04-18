package fi.aalto.msp2017.shoppinglist.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import fi.aalto.msp2017.shoppinglist.ListItemDetailsActivity;
import fi.aalto.msp2017.shoppinglist.R;
import fi.aalto.msp2017.shoppinglist.models.IItem;
import fi.aalto.msp2017.shoppinglist.models.ListItem;
import fi.aalto.msp2017.shoppinglist.models.MasterItem;

/**
 * Created by sunil on 17-04-2017.
 */

public class ListItemAdapterRV extends RecyclerView.Adapter<ListItemAdapterRV.ListItemHolder> {
    private Context context;
    private List<IItem> listItems;
    DatabaseReference listItemRef;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    static class ListItemHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView text;
        TextView txtDetails;
        CardView cv;

        public ListItemHolder(View itemView) {
            super(itemView);
            itemView.getContext();
//            cv = (CardView) itemView.findViewById(R.id.cv);
            text = (TextView) itemView.findViewById(R.id.item_name);
            thumbnail = (ImageView) itemView.findViewById(R.id.icon);
            txtDetails = (TextView) itemView.findViewById(R.id.item_details);

            cv = (CardView) itemView.findViewById(R.id.cv);
        }
    }
    private static final String LOG_TAG = ListItemAdapter.class.getSimpleName();
    String type;
    public ListItemAdapterRV(Context context,
                           List<IItem> listItems, String type) {
        this.context = context;
        this.listItems = listItems;
        this.type = type;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public ListItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_cv_item, viewGroup, false);
        ListItemHolder pvh = new ListItemHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(final ListItemHolder viewHolder, final int i) {
        final IItem listItemEntry = listItems.get(i);
        final String listId = "-KhhVOK1epo5xzo_E4vY";
        viewHolder.thumbnail.setImageResource(context.getResources().getIdentifier(listItemEntry.getImageName(), "drawable", context.getPackageName()));
        viewHolder.text.setText(listItemEntry.getItemName());
        viewHolder.txtDetails.setText(listItemEntry.GetMoreDetails());
        viewHolder.cv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
               if(type.equals("INLIST")) {
                   ListItem selectedItem = (ListItem) listItemEntry;
                   Log.d(LOG_TAG, "Deleted Item : " + selectedItem.getItemKey());
                   listItemRef = database.getReference("shoppinglist").child(listId).child("items");
                   listItemRef.orderByChild("itemKey").equalTo(selectedItem.getItemKey()).addListenerForSingleValueEvent(
                           new ValueEventListener() {
                               @Override
                               public void onDataChange(DataSnapshot dataSnapshot) {
                                   for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                       ds.getRef().setValue(null);
                                   }
                               }

                               @Override
                               public void onCancelled(DatabaseError databaseError) {
                                   Log.w("TodoApp", "getUser:onCancelled", databaseError.toException());
                               }

                           });
               }
                return true;
            }
        });
        viewHolder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type.equals("NOTINLIST")) {
                    MasterItem selectedItem = (MasterItem) listItemEntry;
                    if(TextUtils.isEmpty(selectedItem.getItemKey())) {
                        selectedItem.SaveToDB();
                    }
                    ListItem li = new ListItem(selectedItem.getItemName(), null, selectedItem.getItemKey());
                    li.SaveToDB(listId);
                    listItems.remove(selectedItem);
            }
            else if(type.equals("INLIST")) {
                    Intent intent = new Intent(context, ListItemDetailsActivity.class);
                    ListItem li = (ListItem)listItemEntry;
                    intent.putExtra("LISTITEM", li.getItemKey());
                    intent.putExtra("LISTID", listId);
                    context.startActivity(intent);
                }
                }});
    }
    @Override
    public int getItemCount() {
        return listItems.size();
    }
}
