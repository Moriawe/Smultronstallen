package com.moriawe.smultronstallen;

import android.content.Context;

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
    static LocationsProvider getInstance(Context context) {
//        if (BuildConfig.DEBUG) {
//            return new MockedCats(context);
//        } else {
            return new FireStoreLocations(context);
//        }
    }

    interface Callback { void onLocationsCallback(List<LocationClass> locations);}
    void getLocations(Callback callback);


    class FireStoreLocations implements LocationsProvider, EventListener<QuerySnapshot> {
        FirebaseFirestore store;
        List<Callback> subscribers = new ArrayList<>();

        FireStoreLocations(Context context) {
            store = FirebaseFirestore.getInstance();
            store.collection("Locations").addSnapshotListener(this);
        }

        @Override
        public void getLocations(Callback callback) {
            this.subscribers.add(callback);
        }

        @Override
        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
            List<LocationClass> updates = parseDocuments(value.getDocuments());
            for (Callback subscriber : subscribers) {
                subscriber.onLocationsCallback(updates);
            }
        }

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
        };
    }//end LocationsProvider


//    class MockedCats implements CatLocationProvider {
//        public MockedCats(Context context) {
//
//        }
//        @Override
//        public void getCatLocations(Callback callback) {
//            callback.onCats(new ArrayList<>());
//        }
//    }//End mocked cats


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

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

        public GeoPoint getLocation() {
            return location;
        }
        public void setLocation(GeoPoint location) { this.location = location; }

        public String getOwner() { return owner; }
        public void setOwner(String owner) { this.owner = owner; }


    }

}