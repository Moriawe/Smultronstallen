package com.moriawe.smultronstallen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        startActivity(goToLoginIntent); //Start next activity(LoginActivity)
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



    // SIGN OUT METHOD
    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(this, "Signed out", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
    }
}