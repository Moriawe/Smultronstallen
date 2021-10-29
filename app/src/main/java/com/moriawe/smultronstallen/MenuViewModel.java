package com.moriawe.smultronstallen;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

//Enables data to be set and shared between activities/fragments
public class MenuViewModel extends ViewModel {

    private final MutableLiveData<String> selectedBtn = new MutableLiveData<String>(Constants.MENU_BTN_CHOICE_ALL_LOCATIONS);

    public void setData(String filterLocationsChoice) {
        selectedBtn.setValue(filterLocationsChoice);
    }

    public LiveData<String> getSelectedBtnValueChange() {
        return selectedBtn;
    }
}




