package com.moriawe.smultronstallen;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MenuFragment extends Fragment implements View.OnClickListener{
    MenuViewModel menuViewModel;
    public ToggleButton showHideBtn;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        //MenuViewModel enables us to send data between activities/fragments,
        //livedata with observer-functionality(update UI in activities/fragments when value is changed)
        menuViewModel = new ViewModelProvider(requireActivity()).get(MenuViewModel.class);

        //Radiobuttons, enables you to see which button(choices Alla, Egna, Privata) is highlighted/clicked
        RadioButton completeListBtn = (RadioButton) view.findViewById(R.id.completeListBtn);
        completeListBtn.setOnClickListener(this);

        RadioButton friendsListBtn = (RadioButton) view.findViewById(R.id.friendsListBtn);
        friendsListBtn.setOnClickListener(this);

        RadioButton personalListBtn = (RadioButton) view.findViewById(R.id.personalListBtn);
        personalListBtn.setOnClickListener(this);

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
                break;
            case R.id.friendsListBtn:
                if (checked)
                    menuViewModel.setSelectedMenuBtnValue(Constants.MENU_BTN_CHOICE_PRIVATE_LOCATIONS);
                break;
            case R.id.personalListBtn:
                if (checked)
                    menuViewModel.setSelectedMenuBtnValue(Constants.MENU_BTN_CHOICE_FRIENDS_LOCATIONS);
                break;
        }
    }

}

//Generated code from Android studio when Fragment was created
//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//    public MenuFragment() {
//        // Required empty public constructor
//    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment MenuFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static MenuFragment newInstance(String param1, String param2) {
//        MenuFragment fragment = new MenuFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }