package fi.aalto.msp2017.shoppinglist.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import java.util.ArrayList;
import java.util.List;

import fi.aalto.msp2017.shoppinglist.ListItemDetailsActivity;
import fi.aalto.msp2017.shoppinglist.R;
import fi.aalto.msp2017.shoppinglist.models.Adverts;
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
    String listId;
    static class ListItemHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        ImageView advert;
        TextView text;
        TextView txtDetails;
        TextView txtStatus;
        CardView cv;

        public ListItemHolder(View itemView) {
            super(itemView);
            itemView.getContext();
//            cv = (CardView) itemView.findViewById(R.id.cv);
            text = (TextView) itemView.findViewById(R.id.item_name);
            thumbnail = (ImageView) itemView.findViewById(R.id.icon);
            advert = (ImageView) itemView.findViewById(R.id.advert);

            txtDetails = (TextView) itemView.findViewById(R.id.item_details);
            txtStatus = (TextView) itemView.findViewById(R.id.item_status);
            cv = (CardView) itemView.findViewById(R.id.cv);
        }

    }
    private static final String LOG_TAG = ListItemAdapter.class.getSimpleName();
    String type;
    String ownerId;
    public List<Adverts> advertItems = new ArrayList<>();
    Double latitude, longitude;
    public ListItemAdapterRV(Context context,
                             List<IItem> listItems, String type, String listID, String ownerId) {
        this.context = context;
        this.listItems = listItems;
        this.type = type;
        this.listId = listID;
        this.ownerId = ownerId;
    }
    public ListItemAdapterRV(Context context,
                             List<IItem> listItems, String type, String listID, String ownerId, Double latitude, Double longitude) {
        this.context = context;
        this.listItems = listItems;
        this.type = type;
        this.listId = listID;
        this.ownerId = ownerId;
        this.latitude = latitude;
        this.longitude = longitude;
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

        viewHolder.thumbnail.setImageResource(context.getResources().getIdentifier(listItemEntry.getImageName(), "drawable", context.getPackageName()));
        viewHolder.text.setText(listItemEntry.getItemName());
        viewHolder.txtDetails.setText(listItemEntry.GetMoreDetails());
        viewHolder.txtStatus.setText(listItemEntry.getStatus());
//        Log.d(LOG_TAG, "advert:"+advertItems.size());

        for (Adverts ad : advertItems)
        {
            if (ad.getKeyword().toLowerCase().equals(listItemEntry.getItemName().toLowerCase()))  {
                Location startPoint=new Location("locationA");
                startPoint.setLatitude(ad.getLatitude());
                startPoint.setLongitude(ad.getLongitude());
                Location endPoint=new Location("locationB");
                endPoint.setLatitude(latitude);
                endPoint.setLongitude(longitude);
                double distance=startPoint.distanceTo(endPoint);
//                Log.d(LOG_TAG,"Distance:" + distance);
                if(distance<1000) {
                    Log.d(LOG_TAG, "advert:"+ad.getKeyword()+":"+listItemEntry.getItemName().toLowerCase());
//                    viewHolder.advert.setVisibility(View.VISIBLE);
                    viewHolder.advert.setImageResource(context.getResources().getIdentifier(ad.getCompany().toLowerCase(), "drawable", context.getPackageName()));
                }
            }
//            else {
//                viewHolder.advert.setVisibility(View.GONE);
//            }

        }


        Log.d(LOG_TAG, listItemEntry.getStatus());
        if(listItemEntry.getStatus().equals("(Purchased)")) {
            viewHolder.txtStatus.setTextColor(Color.parseColor("#8BC34A"));
        }
        else {
            viewHolder.txtStatus.setTextColor(Color.parseColor("#ff0000"));
        }
        viewHolder.cv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
               if(type.equals("INLIST")) {

                   PopupMenu popup = new PopupMenu(context, view);
                   MenuInflater inflater = popup.getMenuInflater();
                   inflater.inflate(R.menu.item_menu, popup.getMenu());
                   ListItem selectedItem = (ListItem) listItemEntry;
                   popup.setOnMenuItemClickListener(new MyMenuItemClickListener(selectedItem));
                   popup.show();


                   //                   if(ownerId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
//                       ListItem selectedItem = (ListItem) listItemEntry;
//                       Log.d(LOG_TAG, "Deleted Item : " + selectedItem.getItemKey());
//                       listItemRef = database.getReference("shoppinglist").child(listId).child("items");
//                       listItemRef.orderByChild("itemKey").equalTo(selectedItem.getItemKey()).addListenerForSingleValueEvent(
//                               new ValueEventListener() {
//                                   @Override
//                                   public void onDataChange(DataSnapshot dataSnapshot) {
//                                       for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                                           ds.getRef().setValue(null);
//                                       }
//                                   }
//
//                                   @Override
//                                   public void onCancelled(DatabaseError databaseError) {
//                                       Log.w("TodoApp", "getUser:onCancelled", databaseError.toException());
//                                   }
//
//                               });
//                   }
//                   else {
//                       Toast.makeText(context,"Only owner can remove items", Toast.LENGTH_SHORT).show();
//                   }
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
                    li.setImageName(selectedItem.getImageName());
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
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        ListItem li ;

        public MyMenuItemClickListener(ListItem li) {
            this.li = li;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_assigntome:
                    listItemRef = database.getReference("shoppinglist").child(listId).child("items").child(li.getItemKey()).child("owner");
                    DatabaseReference selfRef = database.getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("name");

                    selfRef.addListenerForSingleValueEvent(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Log.d(LOG_TAG,"o "+dataSnapshot.getValue().toString());
                                    String name = dataSnapshot.getValue(String.class);
                                    listItemRef.setValue(name);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.w("TodoApp", "getUser:onCancelled", databaseError.toException());
                                }

                            });
                    return true;
                case R.id.action_purchase:
                    DatabaseReference itemOwner = database.getReference("shoppinglist").child(listId).child("items").child(li.getItemKey()).child("status");
                    itemOwner.setValue("(Purchased)");
                    Toast.makeText(context, "Play next", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_delete:
                    if(ownerId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                       Log.d(LOG_TAG, "Deleted Item : " + li.getItemKey());
                       listItemRef = database.getReference("shoppinglist").child(listId).child("items").child(li.getItemKey());
                       listItemRef.setValue(null);
                   }
                   else {
                       Toast.makeText(context,"Only owner can remove items", Toast.LENGTH_SHORT).show();
                   }
                    return true;
            }
            return false;
        }
    }
}
