package com.moriawe.smultronstallen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ShowPlaceActivity extends AppCompatActivity {

    String TAG = "Error in ShowPlaceActivity";

    // Instances
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    DateTimeFormatter dtf;
    LocalDateTime now;

    // Objects
    Smultronstalle smultronstalle;

    //GeoPoint testGeoFromMapActivity;
    GeoPoint geoAddress;
    // Lat/long to use in getAdress method.
    double latitude;
    double longitude;

    TextView titleTV;
    TextView addressTV;
    TextView commentsTV;

    ImageView icon;
    ImageView kryss;

    Button changeInfo;
    Button deletePlace;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_place);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Makes an instance of Date&Time class
        dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        now = LocalDateTime.now();

        // New object - Smultronstalle
        smultronstalle = new Smultronstalle();


        titleTV = findViewById(R.id.headlineTV);
        addressTV = findViewById(R.id.addressTV);
        commentsTV = findViewById(R.id.commentsTV);

        icon = findViewById(R.id.iconIV);
        kryss = findViewById(R.id.closeIV);

        changeInfo = findViewById(R.id.change_button);
        deletePlace = findViewById(R.id.delete_button);

        Intent intent = getIntent();
        //Getting LatLng values from putextas as a ArrayList<Double>
        ArrayList<Double> latLngArr = (ArrayList<Double>) intent.getSerializableExtra("latLng");
        // Read in lat and long from map and puts into adress.
        latitude = latLngArr.get(0);
        longitude = latLngArr.get(1);
        geoAddress = new GeoPoint(latLngArr.get(0),latLngArr.get(1));

        findInfo();

    }

    private void findInfo() {

        db.collection("Smultronstalle")
                .whereEqualTo("adress", geoAddress)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                smultronstalle = document.toObject(Smultronstalle.class);
                                setText();
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void setText() {

        titleTV.setText(smultronstalle.getName());
        addressTV.setText(getAddressFromGeo());
        commentsTV.setText(smultronstalle.getComment());

    }



    // Fetches RL addresses from the lat/long coordinates.
    private String getAddressFromGeo() {

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        String placeAdress;

        try {

            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String street = addresses.get(0).getThoroughfare();
            String streetNum = addresses.get(0).getSubThoroughfare();
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();

            placeAdress = street + " " + streetNum;

        } catch (IOException e) {
            e.printStackTrace();
            placeAdress = "";
        }

        return placeAdress;
    }


    public void closeActivity(View view) {
        Intent intent = new Intent (this, MapActivity.class);
        startActivity(intent);

    }


}