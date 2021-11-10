package com.moriawe.smultronstallen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class ShowPlaceActivity extends AppCompatActivity {

    private String TAG = "ShowPlaceActivity";

    // Instances
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DateTimeFormatter dtf;
    private LocalDateTime now;

    // Download picture
    StorageReference storageReference;

    //Test download picture
    String image = "https://res.cloudinary.com/demo/image/upload/w_500/sample.jpg";
    // Objects
    private Smultronstalle smultronstalle;

    //GeoPoint testGeoFromMapActivity;
    private GeoPoint geoAddress;
    // Lat/long to use in getAdress method.
    private double latitude;
    private double longitude;

    private String documentID;

    private TextView titleTV;
    private TextView addressTV;
    private TextView commentsTV;

    private EditText titleET;
    private EditText addressET;
    private EditText commentsET;

    private ImageView icon;
    private ImageView kryss;

    private Button changeInfo;
    private Button saveInfo;
    private Button deletePlace;


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

        // Download picture
        storageReference = FirebaseStorage.getInstance().getReference();

        // New object - Smultronstalle
        //smultronstalle = new Smultronstalle();


        titleTV = (TextView) findViewById(R.id.headlineTV);
        addressTV = (TextView) findViewById(R.id.addressTV);
        commentsTV = (TextView) findViewById(R.id.commentsTV);

        titleET = (EditText) findViewById(R.id.headlineET);
        addressET = (EditText) findViewById(R.id.addressET);
        commentsET = (EditText) findViewById(R.id.commentsET);


        icon = (ImageView) findViewById(R.id.iconIV);
        kryss = (ImageView) findViewById(R.id.closeIV);

        changeInfo = (Button) findViewById(R.id.change_button);
        saveInfo = (Button) findViewById(R.id.save_button);
        deletePlace = (Button) findViewById(R.id.delete_button);

        Intent intent = getIntent();
        //Getting LatLng values from putextas as a ArrayList<Double>
        ArrayList<Double> latLngArr = (ArrayList<Double>) intent.getSerializableExtra("latLng");
        // Read in lat and long from map and puts into adress.
        latitude = latLngArr.get(0);
        longitude = latLngArr.get(1);
        geoAddress = new GeoPoint(latLngArr.get(0),latLngArr.get(1));

        textModeVisible();
        findInfo();

    }

    private void textModeVisible() {

        titleTV.setVisibility(View.VISIBLE);
        addressTV.setVisibility(View.VISIBLE);
        commentsTV.setVisibility(View.VISIBLE);

        titleET.setVisibility(View.INVISIBLE);
        addressET.setVisibility(View.INVISIBLE);
        commentsET.setVisibility(View.INVISIBLE);

        changeInfo.setVisibility(View.VISIBLE);
        saveInfo.setVisibility(View.INVISIBLE);

    }

    private void editModeVisible() {

        titleTV.setVisibility(View.INVISIBLE);
        addressTV.setVisibility(View.INVISIBLE);
        commentsTV.setVisibility(View.INVISIBLE);

        titleET.setVisibility(View.VISIBLE);
        addressET.setVisibility(View.VISIBLE);
        commentsET.setVisibility(View.VISIBLE);

        changeInfo.setVisibility(View.INVISIBLE);
        saveInfo.setVisibility(View.VISIBLE);

    }

    private void findInfo() {

        db.collection("Smultronstalle")
                .whereEqualTo("geoAddress", geoAddress)
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
                                downloadPicture();
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
        titleET.setText(smultronstalle.getName());
        addressTV.setText(smultronstalle.getAddress());
        addressET.setText(smultronstalle.getAddress());
        commentsTV.setText(smultronstalle.getComment());
        commentsET.setText(smultronstalle.getComment());

    }


    //Checks if the user is creator of place and then shows buttons, otherwise it hides them.
    private void checkUser() {

        String userID = mAuth.getCurrentUser().getUid();

        if (userID.equals(smultronstalle.getCreatorsUserID())) {
            changeInfo.setVisibility(View.VISIBLE);
            deletePlace.setVisibility(View.VISIBLE);
        } else {
            changeInfo.setVisibility(View.GONE);
            deletePlace.setVisibility(View.GONE);
        }

    }

    // Download picture
    public void downloadPicture() {
        Glide.with(this)
                .load(smultronstalle.getPicture())
                .into(icon);
    }


    //ON CLICK METHODS

    public void closeActivity(View view) {
        finish();
    }


    public void changeInfo(View view) {

        editModeVisible();

    }

    public void saveInfo(View view) {

        String newTitle = titleET.getText().toString();
        String newAddress = addressET.getText().toString();
        String newComments = commentsET.getText().toString();

        if (TextUtils.isEmpty(newTitle)) {
            titleET.setError("Required.");
        } else if (TextUtils.isEmpty(newAddress)) {
            addressET.setError("Required.");
        } else if (TextUtils.isEmpty(newComments)) {
            commentsET.setError("Required.");
        } else {

            smultronstalle.setName(newTitle);
            smultronstalle.setAddress(newAddress);
            smultronstalle.setComment(newComments);

            // Updates the document in database

            // Get a new write batch
            WriteBatch batch = db.batch();

            DocumentReference smultronNameRef = db.collection("Smultronstalle").document(documentID);
            batch.update(smultronNameRef, "name", newTitle);

            DocumentReference smultronAddressRef = db.collection("Smultronstalle").document(documentID);
            batch.update(smultronAddressRef, "address", newAddress);

            DocumentReference smultronCommentRef = db.collection("Smultronstalle").document(documentID);
            batch.update(smultronCommentRef, "comment", newComments);

            // Commit the batch
            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Smultronstalle was successfully updated", task.getException());
                    } else {
                        Log.d(TAG, "Smultronstalle wasn't updated", task.getException());
                    }

                }


            });

            setText();
            textModeVisible();

        }

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