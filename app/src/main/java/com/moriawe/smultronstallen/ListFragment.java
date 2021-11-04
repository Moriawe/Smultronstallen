package com.moriawe.smultronstallen;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

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


//    private Boolean;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_list, container, false);
        menuChoiceViewModel = new ViewModelProvider(getActivity()).get(MenuViewModel.class);


        //Get latest values from firebase, listening to updates
        LocationsProvider.getInstance(getContext()).getLocations(locations -> {
            //Get selected value from menuBtns, listening to btnClicks
            menuChoiceViewModel.getSelectedBtnValue().observe(getViewLifecycleOwner(), filterLocationsChoice -> {
                //Declaring empty array to store filtered list in
                locationsList = new ArrayList<>();
                //Adding filtered array from method: filterListMenuChoice()
//                locationsList.addAll(filterListMenuChoice(locations, filterLocationsChoice));
                locationsList.addAll(locations);
//                locationsList.addAll(sortedList);
//                createListAdaptedToRecyclerView(sortedList);
                buildRecyclerView();
            });//end ChoiceViewModel
        });//end LocationsProvider

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
                searchFilter(s.toString());
            }
        });

        return view;

    }//end onCreateView

    private void searchFilter(String text) {
        List<LocationsProvider.LocationClass> filteredList = new ArrayList<>();
        for (LocationsProvider.LocationClass item : locationsList) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        listAdapter.filterList(filteredList);
    }

//    private List<LocationsProvider.LocationClass> filterListMenuChoice(List<LocationsProvider.LocationClass> locationsList, String filterLocationsChoice) {
//        List<LocationsProvider.LocationClass> filteredList = new ArrayList<>();
//
////        String userID = mAuth.getCurrentUser().getUid(); //[Jennie] This is all the code needed to check if it's the right user
//
//        switch (filterLocationsChoice) {
//            case Constants.MENU_BTN_CHOICE_ALL_LOCATIONS:
//                for (LocationsProvider.LocationClass item : locationsList) {
//                    if (item.getCreatorsUserID().equals("4aQpxrZGRwZI2cjzxREz")|| item.getShared() == true) {
//                        filteredList.add(item);
//                    }
//                }
//                break;
//            case Constants.MENU_BTN_CHOICE_FRIENDS_LOCATIONS:
//                for (LocationsProvider.LocationClass item : locationsList) {
//                    if (item.getCreatorsUserID().equals("4aQpxrZGRwZI2cjzxREz") && item.getShared() == true) {
//                        filteredList.add(item);
//                    }
//                }
//                break;
//            case Constants.MENU_BTN_CHOICE_PRIVATE_LOCATIONS:
//                for (LocationsProvider.LocationClass item : locationsList) {
//                    if (item.getCreatorsUserID().equals("4aQpxrZGRwZI2cjzxREz")) {
//                        filteredList.add(item);
//                    }
//                }
//                break;
//        }
//
//        return filteredList;
//    }


//    private void createListAdaptedToRecyclerView(List<LocationsProvider.LocationClass> locations) {
//        locationsList = new ArrayList<>();
//        for (LocationsProvider.LocationClass location : locations) {
//            locationsList.add(new ListItem(location.getName(), location.getDateCreated(), location.getPicture(), location.getAdress()));
//        }
//    }

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
        spaceDecorator = new SpaceDecorator(30);
        listRecyclerView.addItemDecoration(spaceDecorator);
        listRecyclerView.setAdapter(listAdapter);
    }

    @Override
    public void onItemClick(LocationsProvider.LocationClass item) {
        //To do? get location data from listitemclick, close fragment and move mapCamera to clicked location or marker?
        Toast.makeText(getContext(), item.getGeoAddress().toString(), Toast.LENGTH_SHORT).show();
        Toast.makeText(getActivity(), "Show/hide list", Toast.LENGTH_SHORT).show();
        menuChoiceViewModel.setShowHideListValueFromListFragment(false);
        menuChoiceViewModel.setSelectLocationFromList(item.getGeoAddress());
    }

    @Override
    public void onChatItemClick(LocationsProvider.LocationClass item) {
        Toast.makeText(getContext(), item.getGeoAddress().toString(), Toast.LENGTH_SHORT).show();
        Toast.makeText(getActivity(), "Show/hide list", Toast.LENGTH_SHORT).show();
        menuChoiceViewModel.setShowHideListValueFromListFragment(false);
        menuChoiceViewModel.setSelectLocationFromList(item.getGeoAddress());
    }

    @Override
    public void onAbsenceItemClick(LocationsProvider.LocationClass item) {
        Toast.makeText(getContext(), item.getGeoAddress().toString(), Toast.LENGTH_SHORT).show();
        Toast.makeText(getActivity(), "Show/hide list", Toast.LENGTH_SHORT).show();
        menuChoiceViewModel.setShowHideListValueFromListFragment(false);
        menuChoiceViewModel.setSelectLocationFromList(item.getGeoAddress());
    }

}