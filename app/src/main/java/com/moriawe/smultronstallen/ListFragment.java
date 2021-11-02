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

public class ListFragment extends Fragment implements ListAdapter.OnClickListItemListener{

    private View view;
    private ArrayList<ListItem> locationsList;
    private RecyclerView listRecyclerView;
    private ListAdapter listAdapter;
    private RecyclerView.LayoutManager listLayoutManager;
    private MenuViewModel menuChoiceViewModel;
//    private Boolean;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_list, container, false);
        menuChoiceViewModel = new ViewModelProvider(getActivity()).get(MenuViewModel.class);

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
                filter(s.toString());
            }
        });

        //Get latest values from firebase, listening to updates
        LocationsProvider.getInstance(getContext()).getLocations(locations -> {
            //Get selected value from menuBtns, listening to btnClicks
            menuChoiceViewModel.getSelectedBtnValue().observe(getViewLifecycleOwner(), filterLocationsChoice -> {
                //Declaring empty array to store filtered list in
                List<LocationsProvider.LocationClass> sortedList = new ArrayList<>();
                //Adding filtered array from method: filterListMenuChoice()
                sortedList.addAll(filterListMenuChoice(locations, filterLocationsChoice));

                createListAdaptedToRecyclerView(sortedList);
                buildRecyclerView();
            });//end ChoiceViewModel
        });//end LocationsProvider

        return view;

    }

    private void filter(String text) {
        ArrayList<ListItem> filteredList = new ArrayList<>();
        for (ListItem item : locationsList) {
            if (item.getTextName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        listAdapter.filterList(filteredList);
    }

    private List<LocationsProvider.LocationClass> filterListMenuChoice(List<LocationsProvider.LocationClass> locationsList, String filterLocationsChoice) {
        List<LocationsProvider.LocationClass> filteredList = new ArrayList<>();

        switch (filterLocationsChoice) {
            case Constants.MENU_BTN_CHOICE_ALL_LOCATIONS:
                for (LocationsProvider.LocationClass item : locationsList) {
                    if (item.getAddedBy().equals("Morot") || item.getShared() == true) {
                        filteredList.add(item);
                    }
                }
                break;
            case Constants.MENU_BTN_CHOICE_FRIENDS_LOCATIONS:
                for (LocationsProvider.LocationClass item : locationsList) {
                    if (!item.getAddedBy().equals("Morot") && item.getShared() == true) {
                        filteredList.add(item);
                    }
                }
                break;
            case Constants.MENU_BTN_CHOICE_PRIVATE_LOCATIONS:
                for (LocationsProvider.LocationClass item : locationsList) {
                    if (item.getAddedBy().equals("Morot")) {
                        filteredList.add(item);
                    }
                }
                break;
        }

        return filteredList;
    }


    private void createListAdaptedToRecyclerView(List<LocationsProvider.LocationClass> locations) {
        locationsList = new ArrayList<>();
        for (LocationsProvider.LocationClass location : locations) {
            locationsList.add(new ListItem(location.getName(), location.getDateCreated(), location.getPicture(), location.getAdress()));
        }
    }

    private void buildRecyclerView() {
        listRecyclerView = view.findViewById(R.id.recycler_view);
        listRecyclerView.setHasFixedSize(true);
        listLayoutManager = new LinearLayoutManager(getContext());
        listAdapter = new ListAdapter(getContext(), locationsList, this);
        listRecyclerView.setLayoutManager(listLayoutManager);
        listRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        listRecyclerView.addItemDecoration(new ListItemMargin(getContext(), DividerItemDecoration.VERTICAL, 36));
        listRecyclerView.setAdapter(listAdapter);
    }

    @Override
    public void onItemClick(ListItem item) {
        //To do? get location data from listitemclick, close fragment and move mapCamera to clicked location or marker?
        Toast.makeText(getContext(), item.getTextGeoPoint().toString(), Toast.LENGTH_SHORT).show();
        Toast.makeText(getActivity(), "Show/hide list", Toast.LENGTH_SHORT).show();
        menuChoiceViewModel.setShowHideListValueFromListFragment(false);
        //Hide list
    }

}