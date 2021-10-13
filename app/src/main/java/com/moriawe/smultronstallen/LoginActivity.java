package com.moriawe.smultronstallen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Get the intent that started this activity(MainActivity) and extract the teststring(from editText in MainActivity)
        Intent intent = getIntent();
        String messageFromMainActivity = intent.getStringExtra(MainActivity.EXTRA_MESSAGE_KEY_FROM_MAIN_ACTIVITY);

        //Capture LoginActivity's(this activity) TextView and set the string from MainActivity into the TextView in LoginActivity(this activity)
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(messageFromMainActivity);

        ////Capture login-btn, set a click-listener on it
        Button loginBtn = (Button) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(view);
            }
        });

    }

    public void login (View view) {
        Intent goToMapActivityIntent = new Intent(this, MapActivity.class);
        EditText userName = (EditText) findViewById(R.id.userName);
        String userNameStringToMapActivity = userName.getText().toString();
        goToMapActivityIntent.putExtra(USERNAME,userNameStringToMapActivity);
        startActivity(goToMapActivityIntent);
    }

}