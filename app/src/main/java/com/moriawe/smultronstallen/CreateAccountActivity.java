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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CreateAccountActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText userEmailET;
    private EditText newPasswordET;
    private EditText confirmPasswordET;
    private Button createAccButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mAuth = FirebaseAuth.getInstance();

        userEmailET =findViewById(R.id.userEmailET);
        newPasswordET = findViewById(R.id.newPasswordET);
        confirmPasswordET = findViewById(R.id.confirmPasswordET);
        createAccButton = findViewById(R.id.createAccBtn);

    }

    public void createAccount(View view) {

        String TAG = "Error in Create Account";

        if (checkPassword()) {

            String password = newPasswordET.getText().toString();
            String email = userEmailET.getText().toString();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(CreateAccountActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }

                        }
                    });
        }
    }

    private void updateUI(FirebaseUser user) {

        if (user != null) {

            Intent intent = new Intent(getApplicationContext(), MapActivity.class);
            startActivity(intent);

        } else {

            Toast.makeText(this, "No such user", Toast.LENGTH_SHORT).show();
        }
    }

    /* SIGN OUT METHOD
    private void signOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(this, "Signed out", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));

    } */

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
                Toast.makeText(this, "Password needs to be at least 6 long. Try again.", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            return false;
        }
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

        String password = newPasswordET.getText().toString();
        if (TextUtils.isEmpty(password)) {
            newPasswordET.setError("Required.");
            valid = false;
        } else {
            newPasswordET.setError(null);
        }

        return valid;
    }

}