package com.moriawe.smultronstallen;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserSettingsActivity extends AppCompatActivity {

    // Instances
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

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
        settingsUser = MapActivity.currentUser; //TODO Test, ge info från en user till en annan


        profilePicture = (ImageView) findViewById(R.id.profile_picture);

        nicknameTV = (TextView) findViewById(R.id.nick_name);
        emailTV = (TextView) findViewById(R.id.email);
        passwordTV = (TextView) findViewById(R.id.password);
        //friendsTV = (TextView) findViewById(R.id.friends);

        changeProfilePictureBtn = (Button) findViewById(R.id.button_change_profile_picture);
        changeNickNameBtn = (Button) findViewById(R.id.button_change_nickname);
        changeEmailBtn = (Button) findViewById(R.id.button_change_email);
        changePasswordBtn = (Button) findViewById(R.id.button_change_password);
        //addFriendsBtn = (Button) findViewById(R.id.button_add_friends);

        editNickName = (EditText) findViewById(R.id.edit_nick_name);
        editEmail = (EditText) findViewById(R.id.edit_email);
        editPassword = (EditText) findViewById(R.id.edit_password);

        saveProfilePictureBtn = (Button) findViewById(R.id.button_save_profile_picture);
        saveNickNameBtn = (Button) findViewById(R.id.button_save_nickname);
        saveEmailBtn = (Button) findViewById(R.id.button_save_email);
        savePasswordBtn = (Button) findViewById(R.id.button_save_password);

    }

    // Reads info about user and puts into view
    private void init() {

        nicknameTV.setText(settingsUser.getNickName());
        emailTV.setText(settingsUser.getEmail());
        //passwordTV.setText();
        //friendsTV.setText(R.string.friends);

    }


    // Change Button onClicks

    public void changeProfilePicture(View view) {
        //Buttons
        changeProfilePictureBtn.setVisibility(View.GONE);
        saveProfilePictureBtn.setVisibility(View.VISIBLE);
    }

    public void changeNickName(View view) {
        editNickName.setText(settingsUser.getNickName());
        //Buttons
        changeNickNameBtn.setVisibility(View.GONE);
        saveNickNameBtn.setVisibility(View.VISIBLE);
        //Textviews
        nicknameTV.setVisibility(View.GONE);
        editNickName.setVisibility(View.VISIBLE);
    }

    public void changeEmail(View view) {
        editEmail.setText(settingsUser.getEmail());
        //Buttons
        changeEmailBtn.setVisibility(View.GONE);
        saveEmailBtn.setVisibility(View.VISIBLE);
        //Textviews
        emailTV.setVisibility(View.GONE);
        editEmail.setVisibility(View.VISIBLE);

    }

    public void changePassword(View view) {
        //editPassword.setText();
        //Buttons
        changePasswordBtn.setVisibility(View.GONE);
        savePasswordBtn.setVisibility(View.VISIBLE);
        //Textviews
        passwordTV.setVisibility(View.GONE);
        editPassword.setVisibility(View.VISIBLE);
    }

    public void addFriends(View view) {
    }


    // Save Button OnClicks

    public void saveProfilePicture(View view) {

    }

    public void saveNickName(View view) {
        nicknameTV.setText(settingsUser.getNickName());
        //Buttons
        saveProfilePictureBtn.setVisibility(View.GONE);
        changeProfilePictureBtn.setVisibility(View.VISIBLE);
        //Textviews
        editNickName.setVisibility(View.GONE);
        nicknameTV.setVisibility(View.VISIBLE);
    }

    public void saveEmail(View view) {
        emailTV.setText(settingsUser.getEmail());
        //Buttons
        saveNickNameBtn.setVisibility(View.GONE);
        changeNickNameBtn.setVisibility(View.VISIBLE);
        //Textviews
        editEmail.setVisibility(View.GONE);
        emailTV.setVisibility(View.VISIBLE);
    }

    public void savePassword(View view) {
        //passwordTV.setText();
        //Buttons
        saveEmailBtn.setVisibility(View.GONE);
        changeEmailBtn.setVisibility(View.VISIBLE);
        //Textviews
        editPassword.setVisibility(View.GONE);
        passwordTV.setVisibility(View.VISIBLE);
    }

}