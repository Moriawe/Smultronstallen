package com.moriawe.smultronstallen;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;


import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddPlaceActivity extends Activity {

    String TAG = "Error in AddPlace Activity";

    // Instances
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    DateTimeFormatter dtf;
    LocalDateTime now;

    // Objects
    Smultronstalle smultronstalle;

    // Info about the new place
    String nameText;
    String commentsText;
    String addedBy;

    //GeoPoint testGeoFromMapActivity;
    GeoPoint adress;
    // Lat/long to use in getAdress method.
    double latitude;
    double longitude;


    // Views in XML
    TextView nyttStalle;
    EditText nameView;
    EditText commentsView;
    Button submitButton;
    Switch shareSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        // Sets size for pop-up window
        getWindow().setLayout((int)(width*.8), (int)(height*.7));

        // set's window in the center of the screen
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;
        getWindow().setAttributes(params);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Makes an instance of Date&Time class
        dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        now = LocalDateTime.now();

        // New object - Smultronstalle
        smultronstalle = new Smultronstalle();


        //Get intent from MapActivity with LatLng values in array(cant send pure LatLngs in put getexta Intent?)
        Intent intent = getIntent();
        //Getting LatLng values from putextas as a ArrayList<Double>
        ArrayList<Double> latLngArr = (ArrayList<Double>) intent.getSerializableExtra("latLng");
        // Read in lat and long from map and puts into adress.
        latitude = latLngArr.get(0);
        longitude = latLngArr.get(1);
        adress = new GeoPoint(latLngArr.get(0),latLngArr.get(1));


        // TestString from Tobbe
        //Put lattngarrdata in geopoint
        //testGeoFromMapActivity = new GeoPoint(latLngArr.get(0),latLngArr.get(1));
        //Show created geopoint in toast
        //Toast.makeText(this, testGeoFromMapActivity.toString(), Toast.LENGTH_SHORT).show();


        // Views in XML
        nyttStalle = (TextView) findViewById(R.id.nyttStalle);
        nameView = (EditText) findViewById(R.id.nameOfPlaceET);
        commentsView = (EditText) findViewById(R.id.commentsOfPlaceET);
        submitButton = (Button) findViewById(R.id.submitButton);
        shareSwitch = (Switch) findViewById(R.id.share_switch);


        // Writes out the text seen on top. TODO change Geopoint to a correct adress if possible [Jennie]
        getAddressFromGeo();


    }


    // Method that runs when you push the SUBMIT button in the activity.
    public void submitPlace(View view) {

        // PART 1 - CHECK IF ALL IMPORTANT FIELDS (NAME AND COMMENTS) ARE FILLED IN
        if (validateForm()) {

            // PART 2 - FIND CURRENT USER AND MAKE A OBJECT OF APPUSER WITH CURRENT USER INFO
            // Get's  the current users ID
            String userID = mAuth.getCurrentUser().getUid();

            // Tells the program where to look for information. In the collection AppUsers, the document named "userID"
            db.collection("AppUsers").document(userID)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            AppUser user = documentSnapshot.toObject(AppUser.class); // Object must be made here, don't ask me why. [Jennie]
                            addedBy = user.getNickName();
                            checkVisibility();
                            savePlace(); // Calls for next method once the name is saved.

                        }
                    });
        }
    }


    // Saves the new Smultronstalle to the database once we have picked out the User's name from the submitPlace-method
    private void savePlace() {

        // PART 3 - GET INFO ABOUT THE NEW SMULTRONSTALLE AND PUT IT INTO OBJECT.
        smultronstalle.setName(nameText);
        smultronstalle.setComment(commentsText);
        smultronstalle.setAdress(adress); // comes from the intent from MapActivity
        smultronstalle.setDateCreated(dtf.format(now));
        smultronstalle.setAddedBy(addedBy);
        //smultronstalle.setShared(); - get's set in the CheckVisibility method

        // PART 4 - LOAD THE OBJECT INTO THE DATABASE
        // Makes a new document with a generated ID in the database and checks if the document was successfully created.
        db.collection("Smultronstalle")
                .add(smultronstalle)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        Toast.makeText(AddPlaceActivity.this, "Smultronstalle info saved to the database", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        Toast.makeText(AddPlaceActivity.this, "Smultronstalle info did not get saved correctly", Toast.LENGTH_SHORT).show();
                    }
                });

        goBackToMap();

    }


    // Fetches RL addresses from the lat/long coordinates.
    private void getAddressFromGeo() {

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

            nyttStalle.setText("Lägg till ett nytt Smultronställe på ´\n´" + address);

        } catch (IOException e) {
            e.printStackTrace();
        }



    }


    // Checks what the switch is set to.
    private void checkVisibility() {

        //Switch shareSwitch = (ToggleButton) findViewById(R.id.share_switch);
        shareSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    smultronstalle.setShared(true);
                } else {
                    smultronstalle.setShared(false);
                }
            }
        });

    }


    // Checks so that all (name and comments) fields are filled in.
    private boolean validateForm() {
        boolean valid = true;

        nameText = nameView.getText().toString();
        if (TextUtils.isEmpty(nameText)) {
            nameView.setError("Required.");
            valid = false;
        } else {
            nameView.setError(null);
        }

        commentsText = commentsView.getText().toString();
        if (TextUtils.isEmpty(commentsText)) {
            commentsView.setError("Required.");
            valid = false;
        } else {
            commentsView.setError(null);
        }

        return valid;
    }

    // Sends the user back to MapActivity after submitting a new place
    private void goBackToMap() {

            Intent goToMapActivityIntent = new Intent(this, MapActivity.class);
            startActivity(goToMapActivityIntent);

    }

}