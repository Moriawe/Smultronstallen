package com.moriawe.smultronstallen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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

    AppUser currentUser;
    String userID;

    private EditText userEmailET;
    private EditText userPasswordET;
    private TextView CreateNewAccountTV;
    private Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_gradient));
        actionBar.setElevation(0);

        setContentView(R.layout.activity_login);

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

        userID = mAuth.getUid(); // Retrieves the userID from the current user.

    }

    //Actionbar Overflow menu Inflate
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_logout).setVisible(false);

        return true;
    }

    //Actionbar Overflow menu Click method
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (item.getItemId()) {
            case R.id.action_info:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser != null) {
            getCurrentUser();
        } else {
        Toast.makeText(this, "No such user", Toast.LENGTH_SHORT).show();
        }
    }


    // When user needs to Sign in with email and password and press SignIn button
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
                            FirebaseUser user = mAuth.getCurrentUser(); //TODO What does this do?! [Jennie]
                            getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }

    private void getCurrentUser() {
        db.collection("AppUsers").document(userID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        currentUser = documentSnapshot.toObject(AppUser.class);
                        updateLogIn();
                    }
                });

    }


    // Updates the users LastLoggedIn information
    private void updateLogIn() {

        // Updates the AppUser/userID document with a new lastLoggedIn
        db.collection("AppUsers").document(userID)
                .update("lastLoggedIn", dtf.format(now))
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

        sendUserToMap(currentUser);
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


    // If the user is correctly logged in they are sent to MapActivity, otherwise there will be an error toast.
    private void sendUserToMap(AppUser user) {
        Intent goToMapActivityIntent = new Intent(this, MapActivity.class);
        goToMapActivityIntent.putExtra("CurrentUser", user);
        startActivity(goToMapActivityIntent);
    }


    // Sends user to the create account activity
    public void createAccount(View view) {
        Intent intent = new Intent(getApplicationContext(), CreateAccountActivity.class);
        startActivity(intent);
    }


}