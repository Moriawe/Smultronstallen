package com.moriawe.smultronstallen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    //define keys for intent extras, key to retrieve the text value in activity its send to
    public static final String EXTRA_MESSAGE_KEY_FROM_MAIN_ACTIVITY = "messageFromMainActivityKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println("Tobias was here");
        System.out.println("Pernilla oxå :D");


        //Capture gotToLogin-btn, set a click-listener on it
        Button goToLoginBtn = (Button) findViewById(R.id.goToLogin);
        goToLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToLogin(view);
            }
        });

    }

    //Method that executes when clicking goToLoginBtn
    public void goToLogin (View view) {
        Intent goToLoginIntent = new Intent(this, LoginActivity.class); //Create intent
        EditText testStringToSendToLoginActivity = (EditText) findViewById(R.id.editTextToSendToLoginActivity); //Capture EditText-view
        String messageStringToSendToLoginActivity = testStringToSendToLoginActivity.getText().toString(); //Capture value from EditText-view, convert it string
        goToLoginIntent.putExtra(EXTRA_MESSAGE_KEY_FROM_MAIN_ACTIVITY, messageStringToSendToLoginActivity); // Send value from EditText with key to next activity(LoginActivity)
        startActivity(goToLoginIntent); //Start next activity(LoginActivity)

    }
}