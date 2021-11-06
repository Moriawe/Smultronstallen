package com.moriawe.smultronstallen;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.GeoPoint;

//Enables data to be set and shared between activities/fragments
public class MenuViewModel extends ViewModel {

    private final MutableLiveData<String> selectedBtnChoice = new MutableLiveData<String>(Constants.MENU_BTN_CHOICE_ALL_LOCATIONS);
    private final MutableLiveData<Boolean> showHideList = new MutableLiveData<Boolean>(true);
    private final MutableLiveData<Boolean> showHideListFromListFragment = new MutableLiveData<Boolean>(false);
    private final MutableLiveData<GeoPoint> selectLocationFromList = new MutableLiveData<GeoPoint>();

    private final MutableLiveData<String> notificationCount = new MutableLiveData<String>();



    public void setNotificationCount(String count) {
        notificationCount.setValue(count);
    }

    public LiveData<String> getNotificationCount() {
        return notificationCount;
    }




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

    public void setSelectLocationFromList(GeoPoint showHideListFragment) {
        selectLocationFromList.setValue(showHideListFragment);
    }
    public LiveData<GeoPoint> getSelectLocationFromList() {
        return selectLocationFromList;
    }
}




