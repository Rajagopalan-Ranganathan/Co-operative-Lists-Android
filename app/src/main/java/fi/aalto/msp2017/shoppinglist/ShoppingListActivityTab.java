package fi.aalto.msp2017.shoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

import fi.aalto.msp2017.shoppinglist.adapters.PagerAdapter;


public class ShoppingListActivityTab extends AppCompatActivity {

    private String listID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list_tab);

//        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(myToolbar);
                TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("List"));
        tabLayout.addTab(tabLayout.newTab().setText("Add"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        Intent i = getIntent();
        Bundle extras = i.getExtras();

        listID = extras.get("LISTID").toString();
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.shopping_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_members:
                Intent intent = new Intent(ShoppingListActivityTab.this, ShoppingListMemberActivity.class);
                intent.putExtra("LISTID", listID);
                intent.setFlags(intent.getFlags());
                startActivity(intent);
                return true;
            case R.id.action_logout:
                try {
                LoginManager.getInstance().logOut();
                    FirebaseAuth.getInstance().signOut();

                }
                catch(Exception ex){}
                Intent mainintent = new Intent(ShoppingListActivityTab.this, MainActivity.class);
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
