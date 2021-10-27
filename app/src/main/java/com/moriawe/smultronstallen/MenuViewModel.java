package com.moriawe.smultronstallen;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

//Enables data to be set and shared between activities/fragments
public class MenuViewModel extends ViewModel {
    private static final String MENU_BTN_CHOICE_ALL_LOCATIONS = "all";

    private final MutableLiveData<String> selectedBtn = new MutableLiveData<String>(MENU_BTN_CHOICE_ALL_LOCATIONS);

    public void setData(String filterLocationsChoice) {
        selectedBtn.setValue(filterLocationsChoice);
    }

    public LiveData<String> getSelectedBtnValueChange() {
        return selectedBtn;
    }
}

//public class MenuViewModel extends ViewModel {
//
//    private final MutableLiveData<String>  selectedItem = new MutableLiveData<String>();
//
//    public void setData(String item) {
//        selectedItem.setValue(item);
//    }
//
//    public LiveData<String> getSelectedItem() {
//        return selectedItem;
//    }
//
//}


