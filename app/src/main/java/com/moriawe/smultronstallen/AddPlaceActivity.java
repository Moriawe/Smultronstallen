package com.moriawe.smultronstallen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddPlaceActivity extends AppCompatActivity {

    TextView nyttStalle;
    EditText nameView;
    EditText geopointView;
    EditText commentsView;
    Button submitButton;
    TextView hamtaStalleView;

    public static final String NAME_KEY = "Name";
    public static final String GEO_KEY = "Geopoint";
    public static final String COMMENT_KEY = "Comment";

    private DocumentReference mDocRef = FirebaseFirestore.getInstance().document("Smultronstalle/auto-ID");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);

        //Get intent from MapActivity with LatLng values in array(cant send pure LatLngs in put getexta Intent?)
//        Intent intent = getIntent();
//        //Getting LatLng values from putextas as a ArrayList<Double>
//        ArrayList<Double> latLngArr = (ArrayList<Double>) intent.getSerializableExtra("latLng");
//        Toast.makeText(this, latLngArr.get(0).toString() + " " +latLngArr.get(1).toString(), Toast.LENGTH_SHORT).show();

        nyttStalle = (TextView) findViewById(R.id.nyttStalle);
        nameView = (EditText) findViewById(R.id.nameOfPlaceET);
        geopointView = (EditText) findViewById(R.id.geoPointOfPlaceET);
        commentsView = (EditText) findViewById(R.id.commentsOfPlaceET);
        submitButton = (Button) findViewById(R.id.submitButton);
        hamtaStalleView = (TextView) findViewById(R.id.hamtaStalle);

    }


//    public void submitPlace(View view) {
//
//        String nameText = nameView.getText().toString();
//        String commentsText = commentsView.getText().toString();
//        String geopointText = geopointView.getText().toString();
//        String TAG = "Submit Place";
//
//        if (nameText.isEmpty() || commentsText.isEmpty() || geopointText.isEmpty()) {
//            return;
//        }
//        Map<String, Object> dataToSave = new HashMap<String, Object>();
//        dataToSave.put(NAME_KEY, nameText);
//        dataToSave.put(COMMENT_KEY, commentsText);
//        dataToSave.put(GEO_KEY, geopointText);
//        mDocRef.set(dataToSave).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Log.d(TAG, "Document has been saved.");
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.w(TAG, "Document was not saved!", e);
//            }
//        });
//
//    }
//
//    public void fetchPlace(View view) {
//        mDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                if (documentSnapshot.exists()) {
//
//                    String nameText = documentSnapshot.getString(NAME_KEY);
//                    String geopointText = documentSnapshot.getString(GEO_KEY);
//                    String commentsText = documentSnapshot.getString(COMMENT_KEY);
//
//                    hamtaStalleView.setText(nameText + '\n'
//                                            + commentsText + '\n'
//                                            + geopointText);
//
//                }
//            }
//        });
//    }


}