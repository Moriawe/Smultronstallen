package com.moriawe.smultronstallen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    String TAG = "Error in LoginActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    DateTimeFormatter dtf;
    LocalDateTime now;

    private EditText userEmailET;
    private EditText userPasswordET;
    private TextView CreateNewAccountTV;
    private Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Get the intent that started this activity(MainActivity) and extract the teststring(from editText in MainActivity)
        Intent intent = getIntent();
        String messageFromMainActivity = intent.getStringExtra(MainActivity.EXTRA_MESSAGE_KEY_FROM_MAIN_ACTIVITY);

        //Capture LoginActivity's(this activity) TextView and set the string from MainActivity into the TextView in LoginActivity(this activity)
        TextView textView = findViewById(R.id.textView);
        textView.setText(messageFromMainActivity);


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Imports a date&Time class
        dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        now = LocalDateTime.now();

        loginBtn = findViewById(R.id.loginBtn);
        userEmailET = findViewById(R.id.userEmailET);
        userPasswordET = findViewById(R.id.newPasswordET);
        CreateNewAccountTV = findViewById(R.id.CreateNewAccountTV);


    }

    //TODO Checks if the user is already logged in! Should be in main activity?? [Jennie]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        sendUserToMap(currentUser);
    }

    // Sign in user
    public void signIn(View view) {

        String email = userEmailET.getText().toString();
        String password = userPasswordET.getText().toString();

        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        // Sends the email and password to auth to check.
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            sendUserToMap(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            sendUserToMap(null);
                        }

                    }
                });

    }


    // If the user is correctly logged in they are sent to MapActivity, otherwise there will be an error toast.
    private void sendUserToMap(FirebaseUser user) {

        if (user != null) {

            updateLogIn();
            Intent goToMapActivityIntent = new Intent(this, MapActivity.class);
            startActivity(goToMapActivityIntent);

        } else {

            Toast.makeText(this, "No such user", Toast.LENGTH_SHORT).show();
        }
    }

    // Updates the users LastLoggedIn information
    private void updateLogIn() {

        String userID = mAuth.getUid(); // Retrieves the userID from the current user.

        // Adds the following info to the new user in the database. (Could be done without hashmap and instead use the AppUser class, requires further research. [Jennie])
        Map<String, Object> appUser = new HashMap<>();
        appUser.put(getString(R.string.LASTLOG_KEY), dtf.format(now));

        // Add a new document named with the AuthUser ID AppUsers collection
        db.collection("AppUsers").document(userID)
                .update(appUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(LoginActivity.this, "Time logged.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "ERROR! Time not logged.", Toast.LENGTH_SHORT).show();
                        Log.w(TAG, e.toString());
                    }
                });

    }


    // Checks so that no fields are empty when trying to login.
    private boolean validateForm() {
        boolean valid = true;

        String email = userEmailET.getText().toString();
        if (TextUtils.isEmpty(email)) {
            userEmailET.setError("Required.");
            valid = false;
        } else {
            userEmailET.setError(null);
        }

        String password = userPasswordET.getText().toString();
        if (TextUtils.isEmpty(password)) {
            userPasswordET.setError("Required.");
            valid = false;
        } else {
            userPasswordET.setError(null);
        }

        return valid;
    }


    // Sends user to the create account activity
    public void createAccount(View view) {
        Intent intent = new Intent(getApplicationContext(), CreateAccountActivity.class);
        startActivity(intent);
    }


}