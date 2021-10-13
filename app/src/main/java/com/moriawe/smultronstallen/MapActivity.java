package com.moriawe.smultronstallen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class MapActivity extends AppCompatActivity {
    FragmentManager fragmentManager = getSupportFragmentManager();
    TextView fragmentText;
    private MenuViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //Get the intent that started this activity(LoginActivity) and extract the username(from editText in LoginActivity)
        Intent intent = getIntent();
        String userNameFromLoginActivity = intent.getStringExtra(LoginActivity.USERNAME);
        //Capture LoginActivity's(this activity) TextView and set the string from MainActivity into the TextView in LoginActivity(this activity)
        TextView textView = (TextView) findViewById(R.id.userName);
        textView.setText(userNameFromLoginActivity);

        //Set ListFragment to hide from start
        ListFragment menuFragment = (ListFragment) fragmentManager.findFragmentById(R.id.listFragment);
        FragmentTransaction fragTransaction = fragmentManager.beginTransaction();
        fragTransaction.hide(menuFragment);
        fragTransaction.commit();

        //Set textview, string from fragment, (viewmodel, observe)
        fragmentText = findViewById(R.id.testTextMap);
        viewModel = new ViewModelProvider(this).get(MenuViewModel.class);
        viewModel.getSelectedItem().observe(this, item ->{
            fragmentText.setText(item);
        });

    }//end onCreate

    //Show hide listfragment method is called from MenuFragment
        void showHideList() {
        Fragment fragment = (Fragment) fragmentManager.findFragmentById(R.id.listFragment);
        FragmentTransaction fragTransaction = fragmentManager.beginTransaction();
        fragTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        if (fragment.isHidden()) {
            fragTransaction.show(fragment);
        } else {
            fragTransaction.hide(fragment);
        }
        fragTransaction.commit();
    }//end addShowHideListener





}