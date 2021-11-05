package com.moriawe.smultronstallen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserSettingsActivity extends AppCompatActivity {

    // Instances
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    FirebaseUser firebaseUser;
    AppUser settingsUser;


    ImageView profilePicture;

    TextView nicknameTV;
    TextView emailTV;
    TextView passwordTV;
    TextView friendsTV;

    Button changeProfilePictureBtn;
    Button changeNickNameBtn;
    Button changeEmailBtn;
    Button changePasswordBtn;
    Button addFriendsBtn;

    EditText editNickName;
    EditText editEmail;
    EditText editPassword;
    EditText repeatPassword;

    Button saveProfilePictureBtn;
    Button saveNickNameBtn;
    Button saveEmailBtn;
    Button savePasswordBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        settingsUser = new AppUser();
        settingsUser = MapActivity.currentUser;

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        profilePicture = (ImageView) findViewById(R.id.profile_picture);

        nicknameTV = (TextView) findViewById(R.id.nick_name);
        emailTV = (TextView) findViewById(R.id.email);
        passwordTV = (TextView) findViewById(R.id.password);
        //friendsTV = (TextView) findViewById(R.id.friends);

        //changeProfilePictureBtn = (Button) findViewById(R.id.button_change_profile_picture);
        changeNickNameBtn = (Button) findViewById(R.id.button_change_nickname);
        changeEmailBtn = (Button) findViewById(R.id.button_change_email);
        changePasswordBtn = (Button) findViewById(R.id.button_change_password);
        //addFriendsBtn = (Button) findViewById(R.id.button_add_friends);

        editNickName = (EditText) findViewById(R.id.edit_nick_name);
        editEmail = (EditText) findViewById(R.id.edit_email);
        editPassword = (EditText) findViewById(R.id.edit_password);
        repeatPassword = (EditText) findViewById(R.id.edit_repeat_password);

        //saveProfilePictureBtn = (Button) findViewById(R.id.button_save_profile_picture);
        saveNickNameBtn = (Button) findViewById(R.id.button_save_nickname);
        saveEmailBtn = (Button) findViewById(R.id.button_save_email);
        savePasswordBtn = (Button) findViewById(R.id.button_save_password);

        init();

    }

    // Reads info about user and puts into view
    private void init() {

        nicknameTV.setText(settingsUser.getNickName());
        emailTV.setText(settingsUser.getEmail());
        //passwordTV.setText();
        //friendsTV.setText(R.string.friends);

        nicknameTV.setVisibility(View.VISIBLE);
        emailTV.setVisibility(View.VISIBLE);
        passwordTV.setVisibility(View.VISIBLE);

        changeNickNameBtn.setVisibility(View.VISIBLE);
        changeEmailBtn.setVisibility(View.VISIBLE);
        changePasswordBtn.setVisibility(View.VISIBLE);


        editNickName.setVisibility(View.INVISIBLE);
        editEmail.setVisibility(View.INVISIBLE);
        editPassword.setVisibility(View.INVISIBLE);
        repeatPassword.setVisibility(View.INVISIBLE);

        saveNickNameBtn.setVisibility(View.INVISIBLE);
        saveEmailBtn.setVisibility(View.INVISIBLE);
        savePasswordBtn.setVisibility(View.INVISIBLE);

    }


    // Change Button onClicks

    public void changeProfilePicture(View view) {
        //Buttons
        changeProfilePictureBtn.setVisibility(View.INVISIBLE);
        saveProfilePictureBtn.setVisibility(View.VISIBLE);
    }

    public void changeNickName(View view) {
        editNickName.setText(settingsUser.getNickName());
        //Buttons
        changeNickNameBtn.setVisibility(View.INVISIBLE);
        saveNickNameBtn.setVisibility(View.VISIBLE);
        //Textviews
        nicknameTV.setVisibility(View.INVISIBLE);
        editNickName.setVisibility(View.VISIBLE);
    }

    public void changeEmail(View view) {
        editEmail.setText(settingsUser.getEmail());
        //Buttons
        changeEmailBtn.setVisibility(View.INVISIBLE);
        saveEmailBtn.setVisibility(View.VISIBLE);
        //Textviews
        emailTV.setVisibility(View.INVISIBLE);
        editEmail.setVisibility(View.VISIBLE);
    }

    public void changePassword(View view) {
        //editPassword.setText();
        //Buttons
        changePasswordBtn.setVisibility(View.INVISIBLE);
        savePasswordBtn.setVisibility(View.VISIBLE);
        //Textviews
        passwordTV.setVisibility(View.INVISIBLE);
        editPassword.setVisibility(View.VISIBLE);
        repeatPassword.setVisibility(View.VISIBLE);
    }

    public void addFriends(View view) {
    }


    // Save Button OnClicks


    public void saveProfilePicture(View view) {

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                .build();

        firebaseUser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("SaveProfilePicture", "User profile updated.");
                        }
                    }
                });

    }


    public void saveNickName(View view) {
        nicknameTV.setText(settingsUser.getNickName());
        //Buttons
        saveNickNameBtn.setVisibility(View.INVISIBLE);
        changeNickNameBtn.setVisibility(View.VISIBLE);
        //Textviews
        editNickName.setVisibility(View.INVISIBLE);
        nicknameTV.setVisibility(View.VISIBLE);

        String newNickName = editNickName.getText().toString();

        if (TextUtils.isEmpty(newNickName)) {
            editEmail.setError("Required.");
        } else {

            settingsUser.setNickName(newNickName); //for reading into TextView
            updateNickName(newNickName);

            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(newNickName)
                    .build();

            firebaseUser.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("SaveNickName", "User profile updated.");
                            }
                        }
                    });

        }
    }


    public void saveEmail(View view) {
        emailTV.setText(settingsUser.getEmail());
        //Buttons
        saveEmailBtn.setVisibility(View.INVISIBLE);
        changeEmailBtn.setVisibility(View.VISIBLE);
        //Textviews
        editEmail.setVisibility(View.INVISIBLE);
        emailTV.setVisibility(View.VISIBLE);

        String newEmail = editEmail.getText().toString();

        if (TextUtils.isEmpty(newEmail)) {
            editEmail.setError("Required.");
        } else {

            settingsUser.setEmail(newEmail); //for reading into TextView
            updateEmail(newEmail);

            //Updates firebase Auth
            firebaseUser.updateEmail(newEmail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("SaveEmail", "User email address updated.");
                            }
                        }
                    });
        }
    }


    public void savePassword(View view) {

        if (checkPassword()) {

            String newPassword = editPassword.getText().toString();

            editPassword.setError(null);
            firebaseUser.updatePassword(newPassword)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("SavePassword", "User password updated.");
                            }
                        }
                    });

            //Buttons
            savePasswordBtn.setVisibility(View.INVISIBLE);
            changePasswordBtn.setVisibility(View.VISIBLE);
            //Textviews
            editPassword.setVisibility(View.INVISIBLE);
            repeatPassword.setVisibility(View.INVISIBLE);
            passwordTV.setVisibility(View.VISIBLE);

        }

    }


    private boolean checkPassword() {

        if (validateForm()) {

            String newPassword = editPassword.getText().toString();
            String confirmPass = repeatPassword.getText().toString();

            if (newPassword.length() > 6) {

                if (newPassword.equals(confirmPass)) {
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


    private boolean validateForm() {
        boolean valid = true;

        String newPassword = editPassword.getText().toString();
        if (TextUtils.isEmpty(newPassword)) {
            editPassword.setError("Required.");
            valid = false;
        } else {
            editPassword.setError(null);
        }

        String confirmPassword = repeatPassword.getText().toString();
        if (TextUtils.isEmpty(confirmPassword)) {
            repeatPassword.setError("Required.");
            valid = false;
        } else {
            repeatPassword.setError(null);
        }

        return valid;
    }


    private void updateNickName(String nickname) {

        String userID = mAuth.getUid(); // Retrieves the userID from the current user.

        // Tells the database to update lastLoggedIn with todays date and time
        db.collection("AppUsers").document(userID)
                .update("nickName", nickname)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UserSettingsActivity.this, "New nickname info saved", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UserSettingsActivity.this, "ERROR! New nickname info not saved.", Toast.LENGTH_SHORT).show();
                        Log.w("updateNickName", e.toString());
                    }
                });

    }

    private void updateEmail(String email) {

        String userID = mAuth.getUid(); // Retrieves the userID from the current user.

        // Tells the database to update lastLoggedIn with todays date and time
        db.collection("AppUsers").document(userID)
                .update("email", email)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UserSettingsActivity.this, "New email info saved", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UserSettingsActivity.this, "ERROR! New email info not saved.", Toast.LENGTH_SHORT).show();
                        Log.w("updateEmail", e.toString());
                    }
                });

    }

    public void closeActivity(View view) {
        finish();
    }

}