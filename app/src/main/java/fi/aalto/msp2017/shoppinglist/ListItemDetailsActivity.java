package fi.aalto.msp2017.shoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import fi.aalto.msp2017.shoppinglist.models.ListItem;

public class ListItemDetailsActivity extends AppCompatActivity {
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final String LOG_TAG = ListItemDetailsActivity.class.getSimpleName();
    DatabaseReference masterItemRef;
    DatabaseReference listItemRef;
    DatabaseReference selfRef;
    private TextView tv_Owner, tv_ItemName;
    private EditText et_Quantity;
    private String listID;
    private String itemMasterKey;
    private String itemKey;
    private Button btnAssignToMe, btnPurchase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_item_details);
        Bundle extras = getIntent().getExtras();

        listID=extras.getString("LISTID");
        itemMasterKey=extras.getString("LISTITEM");
        Log.d(LOG_TAG,listID+":"+itemMasterKey);

        listItemRef = database.getReference("shoppinglist").child(listID).child("items");

        InitializeData();
        et_Quantity.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                Integer quantity = 1;
                try{
                    quantity = Integer.parseInt(et_Quantity.getText().toString().trim());
                    DatabaseReference itemOwner = database.getReference("shoppinglist").child(listID).child("items").child(itemKey).child("quantity");
                    itemOwner.setValue(quantity);
                }catch(Exception ex){
                    Log.d(LOG_TAG, ex.getMessage());
                }


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
        findViewById(R.id.lid_Purchase).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status ="(Not purchased)";
                btnPurchase = (Button)findViewById(R.id.lid_Purchase);
                Log.d(LOG_TAG,btnPurchase.getText().toString().toLowerCase());
                if(btnPurchase.getText().toString().toLowerCase().equals("mark as purchased")) {
                status = "(Purchased)";
            }
                final DatabaseReference itemOwner = database.getReference("shoppinglist").child(listID).child("items").child(itemKey).child("status");
                itemOwner.setValue(status);

                if (status.equals("(Purchased)")) {
                    btnPurchase.setText("Mark as not Purchased");
                }
                else {
                    btnPurchase.setText("Mark as Purchased");
                }
            }});
        findViewById(R.id.lid_AssignToMe).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference itemOwner = database.getReference("shoppinglist").child(listID).child("items").child(itemKey).child("owner");
                selfRef = database.getReference(getString(R.string.FBDB_USERS)).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("name");
                selfRef.addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Log.d(LOG_TAG,"o "+dataSnapshot.getValue().toString());
                                String name = dataSnapshot.getValue(String.class);
                                itemOwner.setValue(name);
                                tv_Owner.setText(name);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w("TodoApp", "getUser:onCancelled", databaseError.toException());
                            }

                        });
            }
        });

    }

    private void InitializeData() {

        tv_ItemName = (TextView)findViewById(R.id.lid_header);
        et_Quantity = (EditText)findViewById(R.id.lid_quantity);
        tv_Owner = (TextView)findViewById(R.id.lid_owner);
        btnPurchase = (Button)findViewById(R.id.lid_Purchase);
        listItemRef.orderByChild("itemKey").equalTo(itemMasterKey).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            ListItem item = ds.getValue(ListItem.class);
                            tv_ItemName.setText(item.getItemName());
                            itemKey = ds.getKey();
                          et_Quantity.setText(item.getQuantity().toString(), TextView.BufferType.EDITABLE);
                            tv_Owner.setText(item.getOwner());
                            if (item.getStatus().equals("(Purchased)")) {
                                btnPurchase.setText("Mark as not Purchased");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("TodoApp", "getUser:onCancelled", databaseError.toException());
                    }

                });



    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                try {
                    LoginManager.getInstance().logOut();
                    FirebaseAuth.getInstance().signOut();

                }
                catch(Exception ex){}
                Intent mainintent = new Intent(ListItemDetailsActivity.this, MainActivity.class);
                mainintent.setFlags(mainintent.getFlags());
                startActivity(mainintent);
                finish();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

}
