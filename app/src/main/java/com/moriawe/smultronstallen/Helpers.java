package com.moriawe.smultronstallen;

import android.icu.text.SimpleDateFormat;
import android.util.Log;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class Helpers {

    public static class SortByDate implements Comparator<LocationsProvider.LocationClass> {
        @Override
        public int compare(LocationsProvider.LocationClass a, LocationsProvider.LocationClass b) {
            return b.getDateCreated().compareTo(a.getDateCreated());
        }
    }

    public static List<LocationsProvider.LocationClass> returnSortedArr(List<LocationsProvider.LocationClass> dateList) {
        Collections.sort(dateList, new SortByDate());
        return dateList;
    }

    public static List<LocationsProvider.LocationClass> getNewLocations(List<LocationsProvider.LocationClass> locations, String latestTimeStamp) {
//        Log.d("latestTimeStamp in helper", latestTimeStamp);
        List<LocationsProvider.LocationClass> futureLocations = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); // here use your response date format
//        locations.clear();
//        locations.addAll(locations);

        Date fromTimeStamp = null;
        try {
            fromTimeStamp = formatter.parse(latestTimeStamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for(LocationsProvider.LocationClass location : locations) {
            Date date = null;
            try {
                date = formatter.parse(location.getDateCreated());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(fromTimeStamp.before(date)) {
                futureLocations.add(location);
            }
        }

        Collections.sort(futureLocations, new SortByDate());

        for (LocationsProvider.LocationClass date : futureLocations) {
            System.out.println("muu" + date.getDateCreated());
        }

        Log.d("futureLocations size", String.valueOf(futureLocations.size()));
        Log.d("futureLocations return" , futureLocations.toString());
//        System.out.println("muu" + futureLocations);
        return futureLocations;
    }

    public static List<LocationsProvider.LocationClass> getSharedFriends (List<LocationsProvider.LocationClass> list) {
        List<LocationsProvider.LocationClass> filteredList = new ArrayList<>();
        for (LocationsProvider.LocationClass item : list) {
//            if (!item.getCreatorsUserID().equals(currentUserID)) {
            filteredList.add(item);
//            }
        }
        return filteredList;
    }

}
