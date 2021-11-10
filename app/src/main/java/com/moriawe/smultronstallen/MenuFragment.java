package com.moriawe.smultronstallen;

import android.app.Notification;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

public class MenuFragment extends Fragment implements View.OnClickListener{
    MenuViewModel menuViewModel;
    public ToggleButton showHideBtn;
    List<RadioButton> radioBtns;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        //MenuViewModel enables us to send data between activities/fragments,
        //livedata with observer-functionality(update UI in activities/fragments when value is changed)
        menuViewModel = new ViewModelProvider(requireActivity()).get(MenuViewModel.class);

        radioBtns = new ArrayList<>();

        //Radiobuttons, enables you to see which button(choices Alla, Egna, Privata) is highlighted/clicked
        RadioButton completeListBtn = (RadioButton) view.findViewById(R.id.completeListBtn);
        completeListBtn.setOnClickListener(this);

        RadioButton friendsListBtn = (RadioButton) view.findViewById(R.id.friendsListBtn);
        friendsListBtn.setOnClickListener(this);

        RadioButton personalListBtn = (RadioButton) view.findViewById(R.id.personalListBtn);
        personalListBtn.setOnClickListener(this);

        RadioButton notificationsListBtn = (RadioButton) view.findViewById(R.id.notificationsListBtn);
        notificationsListBtn.setOnClickListener(this);
        menuViewModel.getNotificationCount().observe(getViewLifecycleOwner(), notificationCount -> {
            if (notificationCount >= 1) {
                notificationsListBtn.setTextColor(Color.parseColor("#6EC6D9"));
            } else {
                if (notificationsListBtn.isChecked()) {
                    notificationsListBtn.setTextColor(Color.parseColor("#5D5D5D"));
                } else {
                    notificationsListBtn.setTextColor(Color.parseColor("#8c8c8c"));
                }

            }
            notificationsListBtn.setText("nya: " + notificationCount);
        });

        radioBtns.add(completeListBtn);
        radioBtns.add(friendsListBtn);
        radioBtns.add(personalListBtn);
        radioBtns.add(notificationsListBtn);

        changeTextColor();

        //ToggleButton, open or close LocationList
        showHideBtn = (ToggleButton) view.findViewById(R.id.showHideBtn);

        menuViewModel.getShowHideListValueFromListFragment().observe(getViewLifecycleOwner(), showHideListFromListFragment -> {
            showHideBtn.setChecked(showHideListFromListFragment);

            showHideBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Toast.makeText(getActivity(), "Show/hide list", Toast.LENGTH_SHORT).show();
                    menuViewModel.setShowHideList(!isChecked);
                }
            });
        });

        return view;
    }

    //Sending data to MapActivity and ListFragment to update/ui with sorted lists(Alla, Egna, Privata)
    @Override
    public void onClick(View v) {
        // Is the button now checked?
        boolean checked = ((RadioButton) v).isChecked();

        // Check which radio button was clicked
        switch(v.getId()) {
            case R.id.completeListBtn:
                if (checked)
                    menuViewModel.setSelectedMenuBtnValue(Constants.MENU_BTN_CHOICE_ALL_LOCATIONS);
                    changeTextColor();
                break;
            case R.id.friendsListBtn:
                if (checked)
                    menuViewModel.setSelectedMenuBtnValue(Constants.MENU_BTN_CHOICE_FRIENDS_LOCATIONS);
                    changeTextColor();
                break;
            case R.id.personalListBtn:
                if (checked)
                    menuViewModel.setSelectedMenuBtnValue(Constants.MENU_BTN_CHOICE_PRIVATE_LOCATIONS);
                    changeTextColor();
                break;
            case R.id.notificationsListBtn:
                if (checked)
                    menuViewModel.setSelectedMenuBtnValue(Constants.MENU_BTN_NOTIFICATIONS);
                    changeTextColor();
                break;
        }
    }

    public void changeTextColor() {
        for (RadioButton rb : radioBtns) {
            if (rb.isChecked()) {
                rb.setTextColor(Color.parseColor("#5D5D5D"));
            } else {
                rb.setTextColor(Color.parseColor("#8c8c8c"));
            }
        }
    }

}

