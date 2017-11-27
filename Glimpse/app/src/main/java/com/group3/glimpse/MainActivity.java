package com.group3.glimpse;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
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
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static FirebaseAuth uAuth = FirebaseAuth.getInstance();
    private CallbackManager cbm;
    private GoogleApiClient gc;
    private static final int RC_SIGN_IN = 123;
    private static ArrayList<Integer> trackedIDs = new ArrayList<>(), nIDs = new ArrayList<>();
    private static ArrayList<Long> notifications = new ArrayList<>();

    // Super stupid, but we're on a deadline
    public static User user = new User(uAuth, trackedIDs, notifications, nIDs);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText username = (EditText) findViewById(R.id.user);
        EditText password = (EditText) findViewById(R.id.pass);

        Button newUserBtn = (Button) findViewById(R.id.newUser);
        Button loginBtn = (Button) findViewById(R.id.login);
        LoginButton facebookBtn = (LoginButton) findViewById(R.id.fb_login);
        SignInButton googleBtn = (SignInButton) findViewById(R.id.google_login);
        googleBtn.setSize(SignInButton.SIZE_STANDARD);

        // Callback manager for Facebook login
        cbm = CallbackManager.Factory.create();

        // What we ask users for from their Facebook accounts
        facebookBtn.setReadPermissions("email", "public_profile");

        // Callback to check that user login to facebook was successful
        facebookBtn.registerCallback(cbm, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                System.out.println("Facebook login attempt success! " + loginResult);

                // Pass user credentials to firebase to ensure a new account is created in the
                // database or the credentials are used to log in to an existing account
                handleFBAccessToken(loginResult.getAccessToken());
            }

            // If the facebook login is cancelled, let the user know
            @Override
            public void onCancel() {

                System.out.println("Facebook login cancelled!");
                Toast.makeText(getApplication(), "Facebook login cancelled.",
                        Toast.LENGTH_SHORT).show();
            }
            // If there is a problem logging with facebook, let the user know
            @Override
            public void onError(FacebookException error) {
                System.out.println("Facebook login error! " + error);
                Toast.makeText(getApplication(), "Facebook login error.\nPlease try again" +
                                " or use another login method",
                        Toast.LENGTH_LONG).show();
            }
        });

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Make a new API client
        gc = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    // Inform users of failed connections
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        System.out.println("Error connection failed to GoogleAPI!");
                        Toast.makeText(getApplicationContext(), "Connection failed.\n"
                                + connectionResult.getErrorMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        googleBtn.setOnClickListener(v -> googleSignIn());
        googleBtn.setVisibility(View.INVISIBLE);
        facebookBtn.setVisibility(View.INVISIBLE);

        newUserBtn.setOnClickListener(v -> {
            newUserBtn.setVisibility(View.INVISIBLE);
            googleBtn.setVisibility(View.VISIBLE);
            facebookBtn.setVisibility(View.VISIBLE);
        });

        loginBtn.setOnClickListener(v -> {

            String user = username.getText().toString();
            String pw = password.getText().toString();

            System.out.println("Attempting login");

            try {
                // If newUserBtn was NOT clicked, regular login attempt
                if (newUserBtn.getVisibility() != View.INVISIBLE) {

                    uAuth.signInWithEmailAndPassword(user, pw)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    System.out.println("Task is " + task.getResult());

                                    if (task.isSuccessful()) {
                                        System.out.println("User logged in successfully!");
                                        launchHomePage();
                                    } else {
                                        System.out.println("Login failed!");
                                        Toast.makeText(getApplicationContext(), "Login failed.\n"
                                                        + task.getException().getLocalizedMessage(),
                                                Toast.LENGTH_LONG).show();
                                    }
                                }

                            });
                }

                // If they selected new user, create new account first
                else {
                    // Prevent account creation with weak passwords
                    if(pw.toLowerCase().compareTo("password") != 0) {
                        uAuth.createUserWithEmailAndPassword(user, pw)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            System.out.println("Account created successfully!");
                                            launchHomePage();
                                        } else {
                                            System.out.println("Account creation failed!");
                                            Toast.makeText(getBaseContext(), "Account creation " +
                                                            "failed.\n"
                                                            + task.getException().getLocalizedMessage(),
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    }

                    else {
                        Toast.makeText(getApplicationContext(), "Please use a stronger password." +
                                "\nTry including letters, numbers, and symbols.",
                                Toast.LENGTH_LONG).show();
                    }
                }

            } catch (IllegalArgumentException err) {
                System.out.println("[ERROR] Illegal value passed!");
                Toast.makeText(getApplicationContext(), "Username or password not recognized.\n" +
                        "Username should be a valid email address,\nand password should be " +
                        "at least 6 characters.", Toast.LENGTH_LONG).show();
            }
        });

        username.setOnClickListener(v -> username.setText(""));
    }

    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(gc);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // Handles passing info back to the Facebook SDK
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // If request is coming from google login
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount acc = result.getSignInAccount();
                firebaseAuthWithGoogle(acc);
            } else
                System.out.println("Google sign in succeeded!");
        }

        // Else request is from facebook
        else
            cbm.onActivityResult(requestCode, resultCode, data);
    }

    // Connects Google+ logins with Firebase database users
    private void firebaseAuthWithGoogle(GoogleSignInAccount acc) {
        AuthCredential cred = GoogleAuthProvider.getCredential(acc.getIdToken(), null);

        uAuth.signInWithCredential(cred).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    System.out.println("User logged in to Glimpse w/ Google successfully!");
                    launchHomePage();
                }
                else {
                    System.out.println("User login to Glimpse w/ Google failed!");
                    Toast.makeText(getApplicationContext(), "Google login failed.\n"
                            + task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // Connects Facebook logins with Firebase database users
    private void handleFBAccessToken(AccessToken token) {
        System.out.println("Handling token: " + token);

        AuthCredential cred = FacebookAuthProvider.getCredential(token.getToken());
        uAuth.signInWithCredential(cred).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    System.out.println("User signed in via Facebook successfully!");
                    launchHomePage();
                }
                else {
                    System.out.println("Facebook login failed!");
                    Toast.makeText(getApplicationContext(), "Facebook login failed.\n"
                    + task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // Activity switcher
    private void launchHomePage()
    {
        Intent intent = new Intent(this, HomePage.class);
        startActivity(intent);
    }
}