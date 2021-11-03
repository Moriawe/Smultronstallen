package com.moriawe.smultronstallen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.GeoPoint;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ShowPlaceActivity extends AppCompatActivity {

    TextView title;
    TextView address;
    TextView comments;

    ImageView icon;

    Button changeInfo;
    Button deletePlace;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_place);

        title = findViewById(R.id.headlineTV);
        address = findViewById(R.id.addressTV);
        comments = findViewById(R.id.commentsTV);

        icon = findViewById(R.id.iconIV);

        changeInfo = findViewById(R.id.change_button);
        deletePlace = findViewById(R.id.delete_button);



        Intent intent = getIntent();
        //Getting LatLng values from putextas as a ArrayList<Double>
        ArrayList<Double> latLngArr = (ArrayList<Double>) intent.getSerializableExtra("latLng");
        // Read in lat and long from map and puts into adress.
        double latitude = latLngArr.get(0);
        double longitude = latLngArr.get(1);
        GeoPoint address = new GeoPoint(latLngArr.get(0),latLngArr.get(1));

    }

}