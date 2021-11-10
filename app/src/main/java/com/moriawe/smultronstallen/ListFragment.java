package com.moriawe.smultronstallen;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment implements ListAdapter.OnClickListItemListener {

    private View view;
    private List<LocationsProvider.LocationClass> locationsList;
    private RecyclerView listRecyclerView;
    private ListAdapter listAdapter;
    private RecyclerView.LayoutManager listLayoutManager;
    private MenuViewModel menuChoiceViewModel;
    public SpaceDecorator spaceDecorator;

    List<LocationsProvider.LocationClass> latestLocationsList;

    private FirebaseAuth mAuth;
    String currentUserID;
    String latestTimesStamp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_list, container, false);
        menuChoiceViewModel = new ViewModelProvider(getActivity()).get(MenuViewModel.class);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        MapActivity activity = (MapActivity) getActivity();
        latestTimesStamp = activity.getLatestTimestampFromMapActivity();

        //Get latest values from firebase, listening to updates
        LocationsProvider.getInstance(getContext()).getLocations(locations -> {
            latestLocationsList = new ArrayList<>();
            latestLocationsList.addAll(Helpers.getNewLocations(Helpers.getSharedFriends(locations), latestTimesStamp));

            //Get selected value from menuBtns, listening to btnClicks
            menuChoiceViewModel.getSelectedBtnValue().observe(getViewLifecycleOwner(), filterLocationsChoice -> {
                //Declaring empty array to store filtered list in
                locationsList = new ArrayList<>();
                //Adding filtered array from method: filterListMenuChoice()
                locationsList.addAll(filterListMenuChoice(locations, filterLocationsChoice));
                buildRecyclerView();

                EditText editText = view.findViewById(R.id.edittext);
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                    @Override
                    public void afterTextChanged(Editable s) {
                        searchFilter(s.toString(), filterLocationsChoice);
                    }
                });
            });//end ChoiceViewModel
        });//end LocationsProvider



        return view;

    }//end onCreateView

    private void searchFilter(String text, String filterLocationsChoice) {
        List<LocationsProvider.LocationClass> filteredList = new ArrayList<>();
        if (filterLocationsChoice.equals(Constants.MENU_BTN_CHOICE_FRIENDS_LOCATIONS)) {
            for (LocationsProvider.LocationClass item : locationsList) {
                if (item.getName().toLowerCase().contains(text.toLowerCase()) || item.getAddedBy().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        } else {
            for (LocationsProvider.LocationClass item : locationsList) {
                if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        }

//        Helpers.returnSortedArr(filteredList);
        listAdapter.filterList(filteredList);
    }

    private List<LocationsProvider.LocationClass> filterListMenuChoice(List<LocationsProvider.LocationClass> locationsList, String filterLocationsChoice) {
        List<LocationsProvider.LocationClass> filteredList = new ArrayList<>();
        List<LocationsProvider.LocationClass> filteredAndSortedList = new ArrayList<>();

        switch (filterLocationsChoice) {
            case Constants.MENU_BTN_CHOICE_ALL_LOCATIONS:
                for (LocationsProvider.LocationClass item : locationsList) {
                    if (item.getCreatorsUserID().equals(currentUserID)|| item.getShared() == true) {
                        filteredList.add(item);
                    }
                }
                filteredAndSortedList.addAll(Helpers.returnSortedArr(filteredList));
                break;
            case Constants.MENU_BTN_CHOICE_FRIENDS_LOCATIONS:
                for (LocationsProvider.LocationClass item : locationsList) {
                    if (!item.getCreatorsUserID().equals(currentUserID) && item.getShared() == true) {
                        filteredList.add(item);
                    }
                }
                filteredAndSortedList.addAll(Helpers.returnSortedArr(filteredList));
                break;
            case Constants.MENU_BTN_CHOICE_PRIVATE_LOCATIONS:
                for (LocationsProvider.LocationClass item : locationsList) {
                    if (item.getCreatorsUserID().equals(currentUserID)) {
                        filteredList.add(item);
                    }
                }
                filteredAndSortedList.addAll(Helpers.returnSortedArr(filteredList));
                break;
            case Constants.MENU_BTN_NOTIFICATIONS:
                    filteredAndSortedList.addAll(latestLocationsList);
                break;
        }

        return filteredAndSortedList;
    }


    private void buildRecyclerView() {
        listRecyclerView = view.findViewById(R.id.recycler_view);
        listRecyclerView.setHasFixedSize(true);
        listLayoutManager = new LinearLayoutManager(getContext());
        listAdapter = new ListAdapter(getContext(), locationsList, this);
        listRecyclerView.setLayoutManager(listLayoutManager);
        listRecyclerView.setItemAnimator(new DefaultItemAnimator());
        if(spaceDecorator != null){
            listRecyclerView.removeItemDecoration(spaceDecorator);
        }
        spaceDecorator = new SpaceDecorator(50);
        listRecyclerView.addItemDecoration(spaceDecorator);
        listRecyclerView.setAdapter(listAdapter);
    }

    @Override
    public void onItemClick(LocationsProvider.LocationClass item) {
        //To do? get location data from listitemclick, close fragment and move mapCamera to clicked location or marker?
//        Toast.makeText(getContext(), item.getGeoAddress().toString(), Toast.LENGTH_SHORT).show();
//        Toast.makeText(getActivity(), "Show/hide list", Toast.LENGTH_SHORT).show();
//        menuChoiceViewModel.setShowHideListValueFromListFragment(false);
//        menuChoiceViewModel.setSelectLocationFromList(item.getGeoAddress());
    }

    @Override
    public void onSmultronImageClick(LocationsProvider.LocationClass item) {
        Toast.makeText(getContext(), item.getGeoAddress().toString(), Toast.LENGTH_SHORT).show();
        Toast.makeText(getActivity(), "Show/hide list", Toast.LENGTH_SHORT).show();
        menuChoiceViewModel.setShowHideListValueFromListFragment(false);
        menuChoiceViewModel.setSelectLocationFromList(item.getGeoAddress());
    }



}