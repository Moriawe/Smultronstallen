package com.moriawe.smultronstallen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddPlaceActivity extends AppCompatActivity {

    String TAG = "Error in AddPlace Activity";

    // Instances
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    DateTimeFormatter dtf;
    LocalDateTime now;

    // Objects
    AppUser currentAppUser;
    Smultronstalle smultronstalle;

    // Info about the new place
    String nameText;
    String commentsText;
    GeoPoint adress = new GeoPoint(52, 12); //TODO Just for trying it out now
    boolean share = true; // TODO needs to read what the switch is
    String addedBy; //TODO Make it work so it reads in the user NickName

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


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Makes an instance of Date&Time class
        dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        now = LocalDateTime.now();

        // The current user DOESN'T WORK!
        //currentAppUser = new AppUser();

        // Views in XML

        //Get intent from MapActivity with LatLng values in array(cant send pure LatLngs in put getexta Intent?)
        //Intent intent = getIntent();
        //Getting LatLng values from putextas as a ArrayList<Double>
        //ArrayList<Double> latLngArr = (ArrayList<Double>) intent.getSerializableExtra("latLng");
        //Toast.makeText(this, latLngArr.get(0).toString() + " " +latLngArr.get(1).toString(), Toast.LENGTH_SHORT).show();


        nyttStalle = (TextView) findViewById(R.id.nyttStalle);
        nameView = (EditText) findViewById(R.id.nameOfPlaceET);
        commentsView = (EditText) findViewById(R.id.commentsOfPlaceET);
        submitButton = (Button) findViewById(R.id.submitButton);
        shareSwitch = (Switch) findViewById(R.id.share_switch);

        nyttStalle.setText("Lägg till ett nytt Smultronställe på ´\n´" + adress);

    }


    public void submitPlace(View view) {

        // PART 1 - CHECK IF ALL IMPORTANT FIELDS (NAME AND COMMENTS) ARE FILLED
        if (validateForm()) {

            // PART 2 - FIND CURRENT USER AND MAKE A OBJECT OF APPUSER WITH CURRENT USER INFO / /TODO MAKE INTO SEPARATE METHOD
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
                            savePlace(); // Calls for next method once the name is saved.

                        }
                    });
        }
    }

    private void savePlace() {

            // PART 3 - GET INFO ABOUT THE NEW SMULTRONSTALLE AND PUT IT INTO OBJECT.
            // adress = new GeoPoint() // TODO from intent - mapActivity
            // share = // from Switch
            smultronstalle = new Smultronstalle(nameText, commentsText, adress, dtf.format(now), share, addedBy);

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

    }

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

}