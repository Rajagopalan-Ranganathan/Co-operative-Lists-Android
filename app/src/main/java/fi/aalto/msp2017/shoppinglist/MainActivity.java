package fi.aalto.msp2017.shoppinglist;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

import static fi.aalto.msp2017.shoppinglist.R.menu.profile;

/*
 * Class - MainActivity
 * Handles the Login activity
 */

public class MainActivity extends AppCompatActivity {

    private EditText login_email, login_password;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference mDatabase;
    GoogleApiClient mGoogleApiClient;
    CallbackManager mCallbackManager;
    Button btnSignup, btnSignin;
    SignInButton btnG;
    TextView reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FacebookSdk.sdkInitialize(getApplicationContext());
        mAuth= FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mCallbackManager = CallbackManager.Factory.create();

        btnSignup=(Button) findViewById(R.id.btnSignup);
        btnSignin=(Button) findViewById(R.id.btnSignIn);
        login_email=(EditText)findViewById(R.id.etSignupEmail);
        login_password=(EditText) findViewById(R.id.etSignupPassword);
        reset=(TextView) findViewById(R.id.passwordReset);
        btnG=(SignInButton) findViewById(R.id.gButton);
        btnG.setSize(SignInButton.SIZE_WIDE);
        btnG.setColorScheme(0);
        LoginButton mFacebookSignInButton = (LoginButton) findViewById(R.id.fbButton);
        mFacebookSignInButton.setReadPermissions("email", "public_profile");



        //attaching listener to LogIn button
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        //attaching listener to LogIn button
        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailLogin();
            }
        });



        // GOOGLE Sign In integration
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // GOOGLE Sign In integration
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(MainActivity.this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(MainActivity.this, "Login error", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // FACEBOOK Sign It integration
        mFacebookSignInButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.wtf("myTag", "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.wtf("myTag", "facebook:onError", error);
                // ...
            }
        });


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Profile profile = Profile.getCurrentProfile();
                if (firebaseAuth.getCurrentUser() != null) {
                    if (user.isEmailVerified()|| profile!=null) {
                        Log.wtf("User", firebaseAuth.getCurrentUser().getEmail());
                        Intent intent = new Intent(MainActivity.this, ListActivity.class);
                        startActivity(intent);
                    }
                }
            }
        };

        //Google
        btnG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                googleSignIn();
            }
        });

        //Password reset
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = login_email.getText().toString();
                if(TextUtils.isEmpty(email)) {
                    Toast.makeText(MainActivity.this, "Please enter the email", Toast.LENGTH_SHORT).show();

                }
                else{
                    mAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("D", "Email sent.");
                                        Toast.makeText(MainActivity.this, "Email with password reset was sent", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                        Toast.makeText(MainActivity.this, "Your email is not in our list", Toast.LENGTH_SHORT).show();
                                }
                            });
                }}
        });
    }

    //method to LOG IN with email:password
    private void emailLogin(){

        String email = login_email.getText().toString();
        String password = login_password.getText().toString();
        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(MainActivity.this, "Fill all the fields", Toast.LENGTH_SHORT).show();

        } else {
            final ACProgressFlower dialog = new ACProgressFlower.Builder(this)
                    .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                    .themeColor(Color.WHITE)
                    .text("Please wait...")
                            .fadeColor(Color.DKGRAY).build();
            dialog.show();
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        dialog.dismiss();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user.isEmailVerified())
                        {
                            Intent intent = new Intent(MainActivity.this, ListActivity.class);
                            Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Verify your email", Toast.LENGTH_LONG).show();
                        }

                    } else {
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, "Wrong email or password", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }



    //method to sign in with GOOGLE account
    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, 1);
    }

    // GOOGLE Sign In integration
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 1) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        } else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    // GOOGLE Sign In integration
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        final ACProgressFlower mProgress = new ACProgressFlower.Builder(MainActivity.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please wait...")
                .fadeColor(Color.DKGRAY).build();
        mProgress.show();
        final AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.wtf("myTag", "signInWithCredential:onComplete:" + task.isSuccessful());

                        String username = mAuth.getCurrentUser().getDisplayName();;
                        String userId = mAuth.getCurrentUser().getUid();
                        String imageUrl = String.valueOf(mAuth.getCurrentUser().getPhotoUrl());
                        imageUrl = imageUrl.replace("/s96-c/","/s450-c/");
                        DatabaseReference currentUserDb = mDatabase.child(userId);
                        currentUserDb.child("name").setValue(username);
                        currentUserDb.child("imageUrl").setValue(imageUrl);
                        currentUserDb.child("email").setValue(mAuth.getCurrentUser().getEmail());

                        mProgress.dismiss();
                        Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        Log.wtf("sometext", "login successful");
                        Intent intent = new Intent(MainActivity.this, ListActivity.class);
                        startActivity(intent);
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.wtf("myTag", "signInWithCredential", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }

    // FACEBOOK Sing In integration
    private void handleFacebookAccessToken(AccessToken token) {
        Log.wtf("myTag", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.wtf("myTag", "signInWithCredential:onComplete:" + task.isSuccessful());
                        String username = mAuth.getCurrentUser().getDisplayName();;
                        String userId = mAuth.getCurrentUser().getUid();
                        String facebookId = "";
                        // find the Facebook profile and get the user's id
                        for(UserInfo profile : mAuth.getCurrentUser().getProviderData()) {
                            // check if the provider id matches "facebook.com"
                            if(profile.getProviderId().equals(getString(R.string.facebook_provider_id))) {
                                facebookId = profile.getUid();
                            }
                        }
                        String imageUrl = "https://graph.facebook.com/" + facebookId + "/picture?width=450&height=450";
                        DatabaseReference currentUserDb = mDatabase.child(userId);
                        currentUserDb.child("name").setValue(username);
                        currentUserDb.child("imageUrl").setValue(imageUrl);
                        currentUserDb.child("email").setValue(mAuth.getCurrentUser().getEmail());



                        Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, ListActivity.class);
                        startActivity(intent);

                        if (!task.isSuccessful()) {
                            Log.wtf("myTag", "signInWithCredential", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }
}
