package com.moriawe.smultronstallen;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class AddPlaceActivity extends Activity {

    String TAG = "Error in AddPlace Activity";

    // Instances
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DateTimeFormatter dtf;
    private LocalDateTime now;

    // Objects
    private Smultronstalle smultronstalle;

    // Info about the new place
    private String nameText;
    private String commentsText;
    private String addressText;
    private GeoPoint geoAddress;
    private String addedBy;
    private String creatorUserID;

    // Lat/long to use in getAddress method.
    private double latitude;
    private double longitude;

    // Views in XML
    TextView nyttStalle;
    EditText nameView;
    EditText commentsView;
    Button submitButton;
    Switch shareSwitch;
    ImageView addPictureView;
    Button buttonGallery;

    // Add picture
    private StorageReference storageRef;
    private static final int PICK_IMAGE_REQUEST = 1;
    private String pictureStorageUrl;
    public Uri pictureDeviceUri;

    @SuppressLint("ClickableViewAccessibility") //Ignore adaption to visual impairment
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        // Sets size for pop-up window
        getWindow().setLayout((int)(width*.9), (int)(height*.83));
        getWindow().setBackgroundDrawableResource(R.drawable.transparent);

        // set's window in the center of the screen
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -40;
        params.dimAmount = 0.19f;
        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        getWindow().setAttributes(params);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        // Get's  the current users ID
        creatorUserID = mAuth.getCurrentUser().getUid();

        // Firebase Storage
        storageRef = FirebaseStorage.getInstance().getReference("images_stalle/");

        // Makes an instance of Date&Time class
        dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        now = LocalDateTime.now();

        // New object - Smultronstalle
        smultronstalle = new Smultronstalle();

        // Views in XML
        nyttStalle = (TextView) findViewById(R.id.nyttStalle);
        nameView = (EditText) findViewById(R.id.nameOfPlaceET);
        commentsView = (EditText) findViewById(R.id.commentsOfPlaceET);
        submitButton = (Button) findViewById(R.id.submitButton);
        shareSwitch = (Switch) findViewById(R.id.share_switch);
        addPictureView = (ImageView) findViewById(R.id.new_place_image);
        buttonGallery = (Button) findViewById(R.id.gallery_btn);


        // Scroll in comments view
        commentsView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                if (commentsView.hasFocus()) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_SCROLL) {
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        return true;
                    }
                }
                return false;
            }
        });

        getAddress();

        // Sets default image to logo
        addPictureView.setImageResource(R.drawable.ic_logo_text);

        // Launch gallery and run choosePicture() - go to gallery
        buttonGallery.setOnClickListener(view -> choosePicture());

    }


    // Get's the correct address and geopoint from the new place.
    private void getAddress() {

        //Get intent from MapActivity with LatLng values in array(cant send pure LatLngs in put getexta Intent?)
        Intent intent = getIntent();
        //Getting LatLng values from putextas as a ArrayList<Double>
        ArrayList<Double> latLngArr = (ArrayList<Double>) intent.getSerializableExtra("latLng");
        // Read in lat and long from map and puts into address.
        latitude = latLngArr.get(0);
        longitude = latLngArr.get(1);
        geoAddress = new GeoPoint(latLngArr.get(0),latLngArr.get(1));
        smultronstalle.setGeoAddress(geoAddress); // set the geoaddress from the intent info
        nyttStalle.setText(smultronstalle.getAddressFromGeo(this)); // gets the streetaddress from the coordinates and returns as string

    }

    // Method that runs when you push the SUBMIT button in the activity.
    public void submitPlace(View view) {

        // PART 1 - CHECK IF ALL IMPORTANT FIELDS (NAME AND COMMENTS) ARE FILLED IN
        if (validateForm()) {

            // Tells the program where to look for information. In the collection AppUsers, the document named "userID"
            db.collection("AppUsers").document(creatorUserID)
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
        //First four reads what the user has typed in
        smultronstalle.setName(nameText);
        smultronstalle.setComment(commentsText);
        smultronstalle.setAddress(addressText);
        smultronstalle.setGeoAddress(geoAddress); // set the geoaddress from the intent info
        smultronstalle.setPicture(pictureStorageUrl);

        smultronstalle.setDateCreated(dtf.format(now));
        smultronstalle.setAddedBy(addedBy);
        smultronstalle.setCreatorsUserID(creatorUserID);
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

        addressText = nyttStalle.getText().toString();
        if (TextUtils.isEmpty(addressText)) {
            nyttStalle.setError("Required.");
            valid = false;
        } else {
            nyttStalle.setError(null);
        }

        return valid;
    }


    // Sends the user back to MapActivity after submitting a new place
    private void goBackToMap() {
        finish();
    }


    // Launch gallery and make images clickable
    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    // Pick an image from gallery and put it into the image view
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Checks if user clicks an image, and not cancelling action.
        if((requestCode == PICK_IMAGE_REQUEST) && (resultCode == RESULT_OK) && (data !=null)) {
            pictureDeviceUri = data.getData();
            addPictureView.setImageURI(pictureDeviceUri);
            uploadPicture();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }


    // Uploads Picture to Firebase Storage.
    //TODO: Add a progressbar [Pernilla]
    private void uploadPicture() {
        if(pictureDeviceUri !=null) {

            // Gives picture a unique name
            StorageReference fileReference = storageRef.child(System.currentTimeMillis()
            + "." + getFileExtension(pictureDeviceUri));

            // Upload picture to Storage
            UploadTask uploadTask = fileReference.putFile(pictureDeviceUri);
                    uploadTask.addOnSuccessListener((new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(AddPlaceActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();

                            // Fetches the url given to picture in Storage, and puts it in variable for upload to Firestore
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                pictureStorageUrl = uri.toString();
                                System.out.print(pictureStorageUrl);
                                }
                            });
                        }
                    }))

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(AddPlaceActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }else{
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }

    }

}