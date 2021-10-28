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
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

//Search in map
import android.location.Address;
import android.location.Geocoder;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentActivity;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    //Tobias
    private static final String NAME_OF_COLLECTION = "Locations";
    private static final String MENU_BTN_CHOICE_ALL_LOCATIONS = "all";
    private static final String MENU_BTN_CHOICE_PRIVATE_LOCATIONS = "me";
    private static final String MENU_BTN_CHOICE_FRIENDS_LOCATIONS = "friend";
    private FirebaseFirestore fireStore;
    private MenuViewModel menuChoiceViewModel;
    //Tobias

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
        //TOBIAS
        fireStore = FirebaseFirestore.getInstance();
        menuChoiceViewModel = new ViewModelProvider(this).get(MenuViewModel.class);
        //TOBIAS

        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        searchView = findViewById(R.id.map_search_bar);

        //Asking for permission to use gps and initializing map
        getLocationPermission();

        //TOBIAS
        menuChoiceViewModel.getSelectedBtnValueChange().observe(this, filterLocationsChoice -> {
            LocationsProvider.getInstance(this).getLocations(locations -> {
                List<LocationsProvider.LocationClass> sortedList = new ArrayList<>();
                if (filterLocationsChoice.equals(MENU_BTN_CHOICE_ALL_LOCATIONS)) {
                    sortedList.addAll(locations);
                } else if (filterLocationsChoice.equals(MENU_BTN_CHOICE_PRIVATE_LOCATIONS)) {
                    sortedList.addAll(filterAllFriendsOwn(locations, MENU_BTN_CHOICE_PRIVATE_LOCATIONS));
                } else if (filterLocationsChoice.equals(MENU_BTN_CHOICE_FRIENDS_LOCATIONS)) {
                    sortedList.addAll(filterAllFriendsOwn(locations, MENU_BTN_CHOICE_FRIENDS_LOCATIONS));
                }
                mapFragment.getMapAsync(googleMap -> {
                    googleMap.clear();
                    for (LocationsProvider.LocationClass sortedLocation : sortedList) {
                        MarkerOptions markerOption = new MarkerOptions();
                        markerOption.position(convertGeoToLatLng(sortedLocation.getLocation()));
                        markerOption.title(sortedLocation.getName());
                        googleMap.addMarker(markerOption);
                    }
                });

            });//end Provider
        });
        //TOBIAS
        //TOBIAS


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

        //Set ListFragment to hide from start
        ListFragment menuFragment = (ListFragment) fragmentManager.findFragmentById(R.id.listFragment);
        FragmentTransaction fragTransaction = fragmentManager.beginTransaction();
        fragTransaction.hide(menuFragment);
        fragTransaction.commit();

    }//end onCreate

    private static LatLng convertGeoToLatLng(GeoPoint gp) {
        return new LatLng(gp.getLatitude(), gp.getLongitude());
    }
    //TOBIAS

    //TOBIAS
    private List<LocationsProvider.LocationClass> filterAllFriendsOwn(List<LocationsProvider.LocationClass> locationsList, String text) {
        List<LocationsProvider.LocationClass> filteredList = new ArrayList<>();
        for (LocationsProvider.LocationClass item : locationsList) {
            if (item.getOwner().toLowerCase().contains(text.toLowerCase())) {

                filteredList.add(item);

            }
        }
        return filteredList;
    }
    //TOBIAS

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

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                Toast.makeText(MapActivity.this, "onMapClick:\n" + latLng.latitude + " : " + latLng.longitude, Toast.LENGTH_SHORT).show();

            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull LatLng latLng) {

                onLongClick(latLng);

            }
        });

    }

    private void onLongClick(LatLng latLng) {

        //Go to add event activity, sending LatLng with event
//        goToAddPlaceActivity(latLng);

        //Generate randoms to make give random values to location when adding marker on map
        final int min = 10, max = 100;
        final int randomNumber = new Random().nextInt((max - min) + 1) + min;
        Random randomOwner = new Random();
        //End generate randoms
//        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
//        LocalDateTime now = LocalDateTime.now();

        //Set values for loacationitem
        String name = "Rubrik" + randomNumber;
        String date = (DateFormat.format("dd-MM-yyyy hh:mm:ss", new java.util.Date()).toString());
        String image = "Imageurl" + randomNumber;
        GeoPoint gp = new GeoPoint(latLng.latitude, latLng.longitude);
        String owner = randomOwner.nextBoolean() ? "me" : "friend";

        //Create Locationitem
        LocationsProvider.LocationClass locationToSave = new LocationsProvider.LocationClass();

        //Add values to locationitem
        locationToSave.setName(name);
        locationToSave.setDate(date);
        locationToSave.setImage(image);
        locationToSave.setLocation(gp);
        locationToSave.setOwner(owner);

        //Upload locationitem do database
        Task<DocumentReference> task = fireStore.collection(NAME_OF_COLLECTION).add(locationToSave);
        task.addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                onLocationSaveComplete(task);
            }
        });
    }//End onLongClick

    //Go to add event activity, sending LatLng with event

    private void goToAddPlaceActivity(LatLng latLng) {
        if (latLng != null) {
            Intent goToAddPlaceActivityIntent = new Intent(this, AddPlaceActivity.class);
            goToAddPlaceActivityIntent.putExtra("latLng", convertLatLngToDoubleArray(latLng) );
            startActivity(goToAddPlaceActivityIntent);
        } else {
            Toast.makeText(this, "No LatLng provided", Toast.LENGTH_SHORT).show();
        }
    }

    public ArrayList<Double> convertLatLngToDoubleArray (LatLng latLng) {
        ArrayList<Double> latLngArr = new ArrayList<>();
        double lat = latLng.latitude;
        double lng = latLng.longitude;
        latLngArr.add(lat);
        latLngArr.add(lng);
        return latLngArr;
    }

    private void onLocationSaveComplete(Task<DocumentReference> task) {
        Toast.makeText(MapActivity.this, "Uppladdning gick bra", Toast.LENGTH_SHORT).show();
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
        //Tobias Kommenterat ut för att kunna köra appen, samma vilkor i try/cath som i if: om (locationPermissionsGranted) är true körs båda eller?
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

    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
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