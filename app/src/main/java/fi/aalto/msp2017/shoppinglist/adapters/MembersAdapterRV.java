package fi.aalto.msp2017.shoppinglist.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import fi.aalto.msp2017.shoppinglist.R;
import fi.aalto.msp2017.shoppinglist.models.User;

/**
 * Created by sunil on 18-04-2017.
 */

public class MembersAdapterRV extends RecyclerView.Adapter<MembersAdapterRV.ListItemHolder> {
    private Context context;
    private List<User> memberItems;
    DatabaseReference listItemRef;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    static class ListItemHolder extends RecyclerView.ViewHolder {
        ImageView mcv_image;
        TextView name;
        CardView cv;

        public ListItemHolder(View itemView) {
            super(itemView);
            itemView.getContext();
//            cv = (CardView) itemView.findViewById(R.id.cv);
            name = (TextView) itemView.findViewById(R.id.item_name);
            mcv_image = (ImageView) itemView.findViewById(R.id.icon);
            cv = (CardView) itemView.findViewById(R.id.cv);
        }
    }
    private static final String LOG_TAG = ListItemAdapter.class.getSimpleName();
    final String listId = "-KhhVOK1epo5xzo_E4vY";

    public MembersAdapterRV(Context context,
                            List<User> memberItems) {
        this.context = context;
        this.memberItems = memberItems;
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
        final User memberItem = memberItems.get(i);
//        viewHolder.mcv_image.setImageResource(context.getResources().getIdentifier(memberItem.get(), "drawable", context.getPackageName()));
        viewHolder.name.setText(memberItem.getName());
        Glide.with(context)
                .load(memberItem.getImageUrl())
                .override(64,64)
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .crossFade()
                .into(viewHolder.mcv_image);
        viewHolder.cv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                DatabaseReference shoppingListMemberRef =database.getReference("shoppinglist").child(listId).child("members");
                shoppingListMemberRef.orderByChild("email").equalTo(memberItem.getEmail()).addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    if(ds.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                        Toast.makeText(context, "Cannot remove owner", Toast.LENGTH_SHORT).show();
                                        break;
                                    }
                                    else {
                                        ds.getRef().setValue(null);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w("TodoApp", "getUser:onCancelled", databaseError.toException());
                            }

                        });

                return true;

            }});
    }
    @Override
    public int getItemCount() {
        return memberItems.size();
    }
}
