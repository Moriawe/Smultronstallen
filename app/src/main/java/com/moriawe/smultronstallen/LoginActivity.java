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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText userEmailET;
    private EditText userPasswordET;
    private TextView CreateNewAccountTV;
    private Button loginBtn;

    static String USERNAME = "Kalle"; //TODO Bara för att testskicka något vidare till MapActivity

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

        loginBtn = findViewById(R.id.loginBtn);
        userEmailET = findViewById(R.id.userEmailET);
        userPasswordET = findViewById(R.id.newPasswordET);
        CreateNewAccountTV = findViewById(R.id.CreateNewAccountTV);


    }

    /* //Checks if the user is already logged in! Should be in main activity [Jennie]
    @Override
    public void onStart() {
    super.onStart();
    // Check if user is signed in (non-null) and update UI accordingly.
    FirebaseUser currentUser = mAuth.getCurrentUser();
    updateUI(currentUser);
     */

    public void signIn(View view) {

        String TAG = "Error in SignIn";
        String email = userEmailET.getText().toString();
        String password = userPasswordET.getText().toString();

        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        /* //TESTVIEW
        TextView mailTest = (TextView) findViewById(R.id.TVEmailTest);
        TextView losenTest = (TextView) findViewById(R.id.TVPasswordTest);
        mailTest.setText(email);
        losenTest.setText(password); */

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


    private void sendUserToMap(FirebaseUser user) {

        if (user != null) {

            Intent goToMapActivityIntent = new Intent(this, MapActivity.class);

            //TODO Skicka med username or nickname
            /* EditText userName = (EditText) findViewById(R.id.userEmailET);
            String userNameStringToMapActivity = userName.getText().toString();
            goToMapActivityIntent.putExtra(USERNAME,userNameStringToMapActivity); */

            startActivity(goToMapActivityIntent);

        } else {

            Toast.makeText(this, "No such user", Toast.LENGTH_SHORT).show();
        }
    }

    public void createAccount(View view) {
        Intent intent = new Intent(getApplicationContext(), CreateAccountActivity.class);
        startActivity(intent);
    }


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

}