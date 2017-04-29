package fi.aalto.msp2017.shoppinglist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.firebase.client.Firebase;

/*
 * Class - Fireactivity
 * Sets the android context
 */

public class FireActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire);

        Firebase.setAndroidContext(this);
    }
}
