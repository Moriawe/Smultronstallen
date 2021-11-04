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
        private static final String FIREBASE_LOCATIONS_COLLECTION = "Smultronstalle";
        private static final String TAG = FireStoreLocations.class.getSimpleName();;
        FirebaseFirestore store;
        List<Callback> subscribers = new ArrayList<>();
        Context receivedContext;

        // FireStoreLocations Constructor
        FireStoreLocations(Context context) {
            store = FirebaseFirestore.getInstance();
            store.collection(FIREBASE_LOCATIONS_COLLECTION).addSnapshotListener(this);
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
                GeoPoint geoAddress = document.getGeoPoint("geoAddress");
                String name = document.getString("name");
                String comment = document.getString("comment");
                String picture = document.getString("picture");
                String date = document.getString("dateCreated");
                Boolean shared = document.getBoolean("shared");
                String addedBy = document.getString("addedBy");
                String address = document.getString("address");
                String creatorsUserID = document.getString("creatorsUserID");


                LocationClass location = new LocationClass();

                location.setGeoAddress(geoAddress);
                location.setName(name);
                location.setComment(comment);
                location.setPicture(picture);
                location.setDateCreated(date);
                location.setShared(shared);
                location.setAddedBy(addedBy);
                location.setAddress(address);
                location.setCreatorsUserID(creatorsUserID);

                //Add values to location item
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
        private GeoPoint geoAddress;
        private String name;
        private String comment;

        private String address;

        private String picture;
        private String dateCreated;
        private Boolean shared;

        private String addedBy;
        private String creatorsUserID;


        public String getComment() {
            return comment;
        }
        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getAddedBy() {
            return addedBy;
        }
        public void setAddedBy(String addedBy) {
            this.addedBy = addedBy;
        }

        public String getDateCreated() { return dateCreated; }
        public void setDateCreated(String dateCreated) { this.dateCreated = dateCreated; }

        public String getPicture() { return picture; }
        public void setPicture(String picture) { this.picture = picture; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public GeoPoint getGeoAddress() { return geoAddress; }
        public void setGeoAddress(GeoPoint geoAddress) { this.geoAddress = geoAddress; }

        public Boolean getShared() { return shared; }
        public void setShared(Boolean shared) { this.shared = shared; }

        public String getCreatorsUserID() { return creatorsUserID; }
        public void setCreatorsUserID(String creatorsUserID) { this.creatorsUserID = creatorsUserID; }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }

    }//end LocationClass
}//end interface LocationsProvider