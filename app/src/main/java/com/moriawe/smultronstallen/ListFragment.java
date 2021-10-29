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

        menuChoiceViewModel.getSelectedBtnValueChange().observe(getViewLifecycleOwner(), filterLocationsChoice -> {
            Toast.makeText(getContext(), "change observer" + filterLocationsChoice, Toast.LENGTH_SHORT).show();
            LocationsProvider.getInstance(getContext()).getLocations(locations -> {
                List<LocationsProvider.LocationClass> sortedList = new ArrayList<>();
                if (filterLocationsChoice.equals(Constants.MENU_BTN_CHOICE_ALL_LOCATIONS)) {
                    sortedList.addAll(locations);
                } else if (filterLocationsChoice.equals(Constants.MENU_BTN_CHOICE_PRIVATE_LOCATIONS)) {
                    sortedList.addAll(filterAllFriendsOwn(locations, Constants.MENU_BTN_CHOICE_PRIVATE_LOCATIONS));
                } else if (filterLocationsChoice.equals(Constants.MENU_BTN_CHOICE_FRIENDS_LOCATIONS)) {
                    sortedList.addAll(filterAllFriendsOwn(locations, Constants.MENU_BTN_CHOICE_FRIENDS_LOCATIONS));
                }
                createListAdaptedToRecyclerView(sortedList);
                buildRecyclerView();
            });//end Provider
        });

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

    private List<LocationsProvider.LocationClass> filterAllFriendsOwn(List<LocationsProvider.LocationClass> locationsList, String text) {
        List<LocationsProvider.LocationClass> filteredList = new ArrayList<>();
        for (LocationsProvider.LocationClass item : locationsList) {
            if (item.getOwner().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        return filteredList;
    }


    private void createListAdaptedToRecyclerView(List<LocationsProvider.LocationClass> locations) {
        locationsList = new ArrayList<>();
        for (LocationsProvider.LocationClass location : locations) {
            locationsList.add(new ListItem(location.getName(), location.getDate(), location.getImage(), location.getLocation().toString()));
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
        Toast.makeText(getContext(), item.toString(), Toast.LENGTH_SHORT).show();
    }

}