package com.moriawe.smultronstallen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


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

    String documentID;

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
                                documentID = document.getId();
                                smultronstalle = document.toObject(Smultronstalle.class);
                                setText();
                                checkUser();
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    //Puts info in the TextViews
    private void setText() {

        titleTV.setText(smultronstalle.getName());
        addressTV.setText(smultronstalle.getAddress());
        commentsTV.setText(smultronstalle.getComment());

    }


    //Checks if the user is creator of place and then shows buttons, otherwise it hides them.
    private void checkUser() {

        String userID = mAuth.getCurrentUser().getUid();

        if (userID.equals(smultronstalle.getUserID())) {
            changeInfo.setVisibility(View.VISIBLE);
            deletePlace.setVisibility(View.VISIBLE);
        } else {
            changeInfo.setVisibility(View.GONE);
            deletePlace.setVisibility(View.GONE);
        }



    }


    //ON CLICK METHODS

    public void closeActivity(View view) {
        finish();
    }


    public void changeInfo(View view) {
    }


    public void deletePlace(View view) {

        db.collection("Smultronstalle").document(documentID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });

    }
}