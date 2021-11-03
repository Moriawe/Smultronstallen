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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class CreateAccountActivity extends AppCompatActivity {

    String TAG = "Error in Create Account Activity";

    // Instances
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    DateTimeFormatter dtf;
    LocalDateTime now;

    // Views in XML
    EditText userEmailET;
    EditText newPasswordET;
    EditText confirmPasswordET;
    EditText userNickNameET;
    Button createAccButton;

    // Variables
    private String nickName;
    private String email;

    private AppUser appUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Makes an instance of Date&Time class
        dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        now = LocalDateTime.now();

        // Views in XML
        userEmailET =findViewById(R.id.userEmailET);
        newPasswordET = findViewById(R.id.newPasswordET);
        confirmPasswordET = findViewById(R.id.confirmPasswordET);
        createAccButton = findViewById(R.id.createAccBtn);
        userNickNameET = findViewById(R.id.userNickNameET);

    }

    // Called for when you press the Create Account Button
    public void createAccount(View view) {

        // Check if the password is the same on both fields and if it is long enough.
        if (checkPassword()) {

            // Turn the editText into a string
            String password = newPasswordET.getText().toString();
            email = userEmailET.getText().toString();

            // Send email and password to mAuth to create a new account
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                createNewUser();
                                goToMap(user); // Changed places on goToMap and CreateNewUser since there was a delay when pushing the button [Jennie]
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(CreateAccountActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }
    }

    // Creates a new user in the database for keeping information about the user, like nickname and profilepicture etc.
    private void createNewUser() {

        String userID = mAuth.getUid(); // Retrieves the userID from the current user.
        nickName = userNickNameET.getText().toString(); // Reads in the nickname from the textView

        appUser = new AppUser(nickName, email, dtf.format(now), dtf.format(now));

        // Add a new document named with the AuthUser ID AppUsers collection
        db.collection("AppUsers").document(userID)
                .set(appUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(CreateAccountActivity.this, "Profile information was saved.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateAccountActivity.this, "ERROR! Profile information was NOT saved!", Toast.LENGTH_SHORT).show();
                        Log.w(TAG, e.toString());
                    }
                });

    }

    // Updates the lastLoggedIn key with date and time now.
    private void updateLogIn() {

        String userID = mAuth.getUid(); // Retrieves the userID from the current user.

        // Tells the database to update lastLoggedIn with todays date and time
        db.collection("AppUsers").document(userID)
                .update("lastLoggedIn", dtf.format(now))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(CreateAccountActivity.this, "Time logged.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateAccountActivity.this, "ERROR! Time not logged.", Toast.LENGTH_SHORT).show();
                        Log.w(TAG, e.toString());
                    }
                });

    }


    // If the user successfully logs in they are sent to MapActivity, otherwise there will be an error message.
    private void goToMap(FirebaseUser user) {

        if (user != null) {

            updateLogIn();

            Intent intent = new Intent(getApplicationContext(), MapActivity.class);
            startActivity(intent);

        } else {

            Toast.makeText(this, "User could not be created.", Toast.LENGTH_SHORT).show();
        }
    }


    // Password check if the passwords match and are long enough.
    private boolean checkPassword() {

        if (validateForm()) {

            String stringNewPass = newPasswordET.getText().toString();
            String stringConfirmPass = confirmPasswordET.getText().toString();

            if (stringNewPass.length() > 6) {

                if (stringNewPass.equals(stringConfirmPass)) {
                    return true;
                } else {
                    Toast.makeText(this, "The passwords doesn't match. Try again.", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } else {
                Toast.makeText(this, "Password needs to contain at least 6 characters. Try again.", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            return false;
        }
    }


    // Runs in checkPassword to see if the user have filled all fields, if not they will be reminded to do so.
    private boolean validateForm() {
        boolean valid = true;

        nickName = userEmailET.getText().toString();
        if (TextUtils.isEmpty(email)) {
            userEmailET.setError("Required.");
            valid = false;
        } else {
            userEmailET.setError(null);
        }

        email = userEmailET.getText().toString();
        if (TextUtils.isEmpty(email)) {
            userEmailET.setError("Required.");
            valid = false;
        } else {
            userEmailET.setError(null);
        }

        String newPassword = newPasswordET.getText().toString();
        if (TextUtils.isEmpty(newPassword)) {
            newPasswordET.setError("Required.");
            valid = false;
        } else {
            newPasswordET.setError(null);
        }

        String confirmPassword = confirmPasswordET.getText().toString();
        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordET.setError("Required.");
            valid = false;
        } else {
            confirmPasswordET.setError(null);
        }

        return valid;
    }


}