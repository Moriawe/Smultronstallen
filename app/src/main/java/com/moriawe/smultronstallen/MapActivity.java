package com.moriawe.smultronstallen;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//Google Maps
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

//Search in map
import android.location.Address;
import android.location.Geocoder;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentActivity;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    FragmentManager fragmentManager = getSupportFragmentManager();
    TextView fragmentText;
    private MenuViewModel viewModel;

    //Map vars
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Boolean locationPermissionsGranted = false;
    private static final int LOCATION_PERMISSION_CODE = 101;
    private final float DEFAULT_ZOOM = 15f;
    SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        searchView = findViewById(R.id.map_search_bar);

        //Asking for permission to use gps and initializing map
        getLocationPermission();

        //Search in map and move camera to searched location
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;
                if(location != null || location.equals("")) {
                    Geocoder geocoder = new Geocoder(MapActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    }catch(IOException e) {
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), 15f);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        //Get the intent that started this activity(LoginActivity) and extract the username(from editText in LoginActivity)
        Intent intent = getIntent();
        String userNameFromLoginActivity = intent.getStringExtra(LoginActivity.USERNAME);
        //Capture LoginActivity's(this activity) TextView and set the string from MainActivity into the TextView in LoginActivity(this activity)
        /*TextView textView = (TextView) findViewById(R.id.userName);
        textView.setText(userNameFromLoginActivity);*/

        //Set ListFragment to hide from start
        ListFragment menuFragment = (ListFragment) fragmentManager.findFragmentById(R.id.listFragment);
        FragmentTransaction fragTransaction = fragmentManager.beginTransaction();
        fragTransaction.hide(menuFragment);
        fragTransaction.commit();

        //Set textview, string from fragment, (viewmodel, observe)
        //fragmentText = findViewById(R.id.testTextMap);
        viewModel = new ViewModelProvider(this).get(MenuViewModel.class);
        viewModel.getSelectedItem().observe(this, item ->{
            fragmentText.setText(item);
        });

    }//end onCreate

    //Show hide listfragment method is called from MenuFragment
        void showHideList() {
        Fragment fragment = (Fragment) fragmentManager.findFragmentById(R.id.listFragment);
        FragmentTransaction fragTransaction = fragmentManager.beginTransaction();
        fragTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        if (fragment.isHidden()) {
            fragTransaction.show(fragment);
        } else {
            fragTransaction.hide(fragment);
        }
        fragTransaction.commit();
    }//end addShowHideListener

    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        mMap = googleMap;

        if (locationPermissionsGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true); //this default "myLocationButton" can't be manually positioned
            // mMap.getUiSettings().setMyLocationButtonEnabled(false); //TO DO: Can make one manually later and hide the default with this command

            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);//Type of map. Can be changed to satellite etc.
        }

    }

    //Sets camera to current location. Runs method to ask for permission.
    private void getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(locationPermissionsGranted){

                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM);

                        } else {
                            Log.d(TAG, "Oncomplete: current location is null");
                            Toast.makeText(MapActivity.this, "Can't get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityExeption: " + e.getMessage());
        }
       if (locationPermissionsGranted) {
            @SuppressLint("MissingPermission") //Permission Check is done!
            Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: found location");
                        Location currentLocation = (Location) task.getResult();
                        moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                15f);
                    } else {
                        Log.d(TAG, "onComplete: found location");
                        Toast.makeText(MapActivity.this, "Unable to find location", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    //Initialize the map
    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    //Move camera
    private void moveCamera(LatLng latLng, float zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    //Checks permission to use gps and runs map if permission is granted.
    //If not, call onRequestPermissionResult o ask for permission.
    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationPermissionsGranted = true;
                initMap();

            }else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_CODE);
            }

        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_CODE);
        }
    }

    //Asks for permission to use gps and runs initialize map if permission is granted
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            locationPermissionsGranted = false;
                            return;
                        }
                    }
                    locationPermissionsGranted = true;
                    initMap();
                }
            }
        }
    }

}