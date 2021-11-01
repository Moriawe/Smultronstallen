package com.moriawe.smultronstallen;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

//Enables data to be set and shared between activities/fragments
public class MenuViewModel extends ViewModel {

    private final MutableLiveData<String> selectedBtnChoice = new MutableLiveData<String>(Constants.MENU_BTN_CHOICE_ALL_LOCATIONS);
    private final MutableLiveData<Boolean> showHideList = new MutableLiveData<Boolean>(true);
    private final MutableLiveData<Boolean> showHideListFromListFragment = new MutableLiveData<Boolean>(false);


    public void setSelectedMenuBtnValue(String filterLocationsChoice) {
        selectedBtnChoice.setValue(filterLocationsChoice);
    }
    public LiveData<String> getSelectedBtnValue() {
        return selectedBtnChoice;
    }


    public void setShowHideList(Boolean filterLocationsChoice) {
        showHideList.setValue(filterLocationsChoice);
    }
    public LiveData<Boolean> getShowHideListValue() {
        return showHideList;
    }


    public void setShowHideListValueFromListFragment(Boolean showHideListFragment) {
        showHideListFromListFragment.setValue(showHideListFragment);
    }
    public LiveData<Boolean> getShowHideListValueFromListFragment() {
        return showHideListFromListFragment;
    }
}




