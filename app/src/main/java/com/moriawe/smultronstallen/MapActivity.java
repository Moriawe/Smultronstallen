package com.moriawe.smultronstallen;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.ImageView;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

//Search in map
import androidx.appcompat.widget.SearchView;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    FragmentManager fragmentManager = getSupportFragmentManager();
    private static final String FIREBASE_LOCATIONS_COLLECTION = "Locations";
    private FirebaseFirestore fireStore;
    private MenuViewModel menuChoiceViewModel;
    private SupportMapFragment mapFragment;
    private ListFragment menuFragment;
    private FragmentTransaction fragTransaction;

    //Map vars
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Boolean locationPermissionsGranted = false;
    private static final int LOCATION_PERMISSION_CODE = 101;
    private final float DEFAULT_ZOOM = 15f;

    SearchView searchView;
    private ImageView mGps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fireStore = FirebaseFirestore.getInstance();
        menuChoiceViewModel = new ViewModelProvider(this).get(MenuViewModel.class);

        setContentView(R.layout.activity_map);
        searchView = findViewById(R.id.map_search_bar);
        mGps = (ImageView) findViewById(R.id.map_location_button);

        //Asking for permission to use gps and initializing map
        getLocationPermission();

        //Set initial value on ListFragment to hidden
        initMenuFragment();

        //Observes showHideListMenuBtn-click
        menuChoiceViewModel.getShowHideListValue().observe(this, showHideList -> {
            showHideList(showHideList);
        });


        LocationsProvider.getInstance(this).getLocations(locations -> {
            menuChoiceViewModel.getSelectedBtnValue().observe(this, filterLocationsChoice -> {
                List<LocationsProvider.LocationClass> sortedList = new ArrayList<>();
                switch (filterLocationsChoice) {
                    case Constants.MENU_BTN_CHOICE_ALL_LOCATIONS:
                        sortedList.addAll(locations);
                        break;
                    case Constants.MENU_BTN_CHOICE_PRIVATE_LOCATIONS:
                        sortedList.addAll(filterAllFriendsOwn(locations, Constants.MENU_BTN_CHOICE_PRIVATE_LOCATIONS));
                        break;
                    case Constants.MENU_BTN_CHOICE_FRIENDS_LOCATIONS:
                        sortedList.addAll(filterAllFriendsOwn(locations, Constants.MENU_BTN_CHOICE_FRIENDS_LOCATIONS));
                        break;
                }

                //Updates map
                mapFragment.getMapAsync(googleMap -> {
                    googleMap.clear();
                    for (LocationsProvider.LocationClass sortedLocation : sortedList) {
                        MarkerOptions markerOption = new MarkerOptions();
                        markerOption.position(convertGeoToLatLng(sortedLocation.getLocation()));
                        markerOption.title(sortedLocation.getName());
                        googleMap.addMarker(markerOption);
                    }

                });

            });//end menuChoiceViewModel

        });//end LocationsProvider



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

    }//end onCreate

    private List<LocationsProvider.LocationClass> filterAllFriendsOwn(List<LocationsProvider.LocationClass> locationsList, String text) {
        List<LocationsProvider.LocationClass> filteredList = new ArrayList<>();
        for (LocationsProvider.LocationClass item : locationsList) {
            if (item.getOwner().toLowerCase().contains(text.toLowerCase())) {

                filteredList.add(item);

            }
        }
        return filteredList;
    }



    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (locationPermissionsGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false); //this can't be manually positioned. Making a custom.
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);//Type of map. Can be changed to satellite etc.

        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                Toast.makeText(MapActivity.this, "onMapClick:\n" + latLng.latitude + " : " + latLng.longitude, Toast.LENGTH_SHORT).show();
            }
        });

        //Triggers onLongClick-method gets LatLng from maps
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull LatLng latLng) {
                onLongClick(latLng);
            }
        });

    }

    //Jennie, notice commented out code in onLongClick-method to start AddPlaceActivity-intent sending LatLng values with the intent
    //Generates/sets values and uploading Location-item to firebase
    private void onLongClick(LatLng latLng) {
        //Go to add event activity, sending LatLng with event
//        goToAddPlaceActivity(latLng);

        //Generate randoms to make give random values to location when adding marker on map
        final int min = 10, max = 100;
        final int randomNumber = new Random().nextInt((max - min) + 1) + min;
        Random randomOwner = new Random();
        //End generate randoms

        //Set values loacationitem
        String name = "Rubrik" + randomNumber;
        String date = (DateFormat.format("dd-MM-yyyy hh:mm:ss", new java.util.Date()).toString());
        String image = "Imageurl" + randomNumber;
        GeoPoint gp = new GeoPoint(latLng.latitude, latLng.longitude);
        String owner = randomOwner.nextBoolean() ? Constants.MENU_BTN_CHOICE_PRIVATE_LOCATIONS : Constants.MENU_BTN_CHOICE_FRIENDS_LOCATIONS;

        //Create locationitem
        LocationsProvider.LocationClass locationToSave = new LocationsProvider.LocationClass();

        //Add values to locationitem
        locationToSave.setName(name);
        locationToSave.setDate(date);
        locationToSave.setImage(image);
        locationToSave.setLocation(gp);
        locationToSave.setOwner(owner);

        //Upload locationitem do database
        Task<DocumentReference> task = fireStore.collection(FIREBASE_LOCATIONS_COLLECTION).add(locationToSave);
        task.addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                onLocationSaveComplete(task);
            }
        });
    }//End onLongClick


    //Go to add event activity, sending LatLng with event
//    private void goToAddPlaceActivity(LatLng latLng) {
//        if (latLng != null) {
//            Intent goToAddPlaceActivityIntent = new Intent(this, AddPlaceActivity.class);
//            goToAddPlaceActivityIntent.putExtra("latLng", convertLatLngToDoubleArray(latLng) );
//            startActivity(goToAddPlaceActivityIntent);
//        } else {
//            Toast.makeText(this, "No LatLng provided", Toast.LENGTH_SHORT).show();
//        }
//    }
    // Convert LatLng so it can bes sent to AddPlaceActivity
//    public ArrayList<Double> convertLatLngToDoubleArray (LatLng latLng) {
//        ArrayList<Double> latLngArr = new ArrayList<>();
//        double lat = latLng.latitude;
//        double lng = latLng.longitude;
//        latLngArr.add(lat);
//        latLngArr.add(lng);
//        return latLngArr;
//    }


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
    }

    //Initialize the map
    private void initMap() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeviceLocation();
            }
        });
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



    public void initMenuFragment() {
        menuFragment = (ListFragment) fragmentManager.findFragmentById(R.id.listFragment);
        fragTransaction = fragmentManager.beginTransaction();
//        fragTransaction.hide(menuFragment);
        fragTransaction.commit();
    }

    //Show hide listfragment method is called from MenuFragment
    void showHideList(Boolean showHide) {
        fragTransaction = fragmentManager.beginTransaction();
        fragTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        if (showHide) {
            fragTransaction.hide(menuFragment);
        } else {
            fragTransaction.show(menuFragment);
        }
        fragTransaction.commit();
    }//end addShowHideListener

    //Helper methods converters, toasts
    private static LatLng convertGeoToLatLng(GeoPoint gp) {
        return new LatLng(gp.getLatitude(), gp.getLongitude());
    }

    private void onLocationSaveComplete(Task<DocumentReference> task) {
        Toast.makeText(MapActivity.this, "Uppladdning gick bra", Toast.LENGTH_SHORT).show();
    }


    //Actionbar Overflow menu Inflate
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //Actionbar Overflow menu Click method
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (item.getItemId()) {
            case R.id.action_logout:
                signOut();
                return true;
            case R.id.action_info: //TODO: We need an activity with our information
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //SIGN OUT METHOD
    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(this, "Signed out", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
    }

}