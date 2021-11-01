package com.moriawe.smultronstallen;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public interface LocationsProvider {

    // This is what LocationProvider returns from instance Example: LocationsProvider.getInstance(this).getLocations(locations -> {}
    static LocationsProvider getInstance(Context context) {
        Log.d("Context in Locationsproveder", context.toString());
//        if (BuildConfig.DEBUG) {
//           return new MockedLocationsNotFromFirestore(context);
//        } else {
            return new FireStoreLocations(context);
//        }
    }

    interface Callback { void onLocations(List<LocationClass> locations);}
    void getLocations(Callback callback);


    //Class FireStoreLocations
    class FireStoreLocations implements LocationsProvider, EventListener<QuerySnapshot> {
        private static final String TAG = FireStoreLocations.class.getSimpleName();;
        FirebaseFirestore store;
        List<Callback> subscribers = new ArrayList<>();
        Context receivedContext;

        // FireStoreLocations Constructor
        FireStoreLocations(Context context) {
            store = FirebaseFirestore.getInstance();
            store.collection("Locations").addSnapshotListener(this);
            receivedContext = context;
        }

        @Override
        public void getLocations(Callback callback) {
            this.subscribers.add(callback);
        }

        @Override
        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {

            if (e != null) {

                Log.w(TAG, "Listen failed.", e);

                return;

            }

            if (value != null) {

                Log.d(TAG, "Listen succeeded"); //Log.d(TAG, "Current data: " + value.getDocuments());
                Toast.makeText(receivedContext, "Listen succeeded", Toast.LENGTH_SHORT).show();

                List<LocationClass> updates = parseDocuments(value.getDocuments());
                for (Callback subscriber : subscribers) {

                    subscriber.onLocations(updates);

                }

            } else {

                Log.d(TAG, "Listen succeeded but current data is null");

            }

        }// end onEvent

        private List<LocationClass> parseDocuments(List<DocumentSnapshot> documents) {
            List<LocationClass> locations = new ArrayList<>();
            for (DocumentSnapshot document : documents) {
                String name = document.getString("name");
                String image = document.getString("image");
                String date = document.getString("date");
                GeoPoint gp = document.getGeoPoint("location");
                String owner = document.getString("owner");
                LocationClass location = new LocationClass();
                location.setLocation(gp);
                location.setName(name);
                location.setDate(date);
                location.setImage(image);
                location.setOwner(owner);
                locations.add(location);
            }
            return locations;
        }
    }//end LocationsProvider


    //If we want to provide something  else
    /*
    class MockedLocationsNotFromFirestore implements LocationsProvider {
        MockedLocationsNotFromFirestore(Context context) {

        }
        @Override
        public void getLocations(Callback callback) {

        }
    }
    */


    static class LocationClass {
        private String name;
        private String image;
        private String date;
        private GeoPoint location;
        private String owner;

        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }

        public String getImage() { return image; }
        public void setImage(String image) { this.image = image; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public GeoPoint getLocation() { return location; }
        public void setLocation(GeoPoint location) { this.location = location; }

        public String getOwner() { return owner; }
        public void setOwner(String owner) { this.owner = owner; }
    }//end LocationClass

}//end interface LocationsProvider