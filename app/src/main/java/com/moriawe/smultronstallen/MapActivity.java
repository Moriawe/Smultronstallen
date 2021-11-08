package com.moriawe.smultronstallen;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.firebase.auth.FirebaseAuth;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

//Search in map
import androidx.appcompat.widget.SearchView;


import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    FragmentManager fragmentManager = getSupportFragmentManager();
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

    private List<Marker> markersList;

    private FirebaseAuth mAuth;
    AppUser currentUser;
    String currentUserID;
    String latestTimesStamp;

    List<LocationsProvider.LocationClass> latestLocationsList;
    Integer notificationsValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_gradient));
        actionBar.setElevation(0);

        fireStore = FirebaseFirestore.getInstance();
        menuChoiceViewModel = new ViewModelProvider(this).get(MenuViewModel.class);

        setContentView(R.layout.activity_map);
        searchView = findViewById(R.id.map_search_bar);
        mGps = (ImageView) findViewById(R.id.map_location_button);

        // Reads in the CurrentUser that is logged in so that we can fetch name, email etc. Object of AppUser
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        if(getIntent().getExtras() != null) {
             currentUser = (AppUser) getIntent().getSerializableExtra("CurrentUser");
        }
        latestTimesStamp = currentUser.getLastLoggedIn();
        System.out.println("latestTimesStamp beginning in map" + latestTimesStamp);

        //Asking for permission to use gps and initializing map
        getLocationPermission();

        //Set initial value on ListFragment to hidden
        initMenuFragment();

        //Observes showHideListMenuBtn-click
        menuChoiceViewModel.getShowHideListValue().observe(this, showHideList -> {
            showHideList(showHideList);
        });

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


    private List<LocationsProvider.LocationClass> filterListMenuChoice(List<LocationsProvider.LocationClass> locationsList, String filterLocationsChoice) {
        List<LocationsProvider.LocationClass> filteredList = new ArrayList<>();

            switch (filterLocationsChoice) {
            case Constants.MENU_BTN_CHOICE_ALL_LOCATIONS:
                for (LocationsProvider.LocationClass item : locationsList) {
                    if (item.getCreatorsUserID().equals(currentUserID)|| item.getShared() == true) {
                        filteredList.add(item);
                    }
                }
                break;
            case Constants.MENU_BTN_CHOICE_FRIENDS_LOCATIONS:
                for (LocationsProvider.LocationClass item : locationsList) {
                    if (!item.getCreatorsUserID().equals(currentUserID) && item.getShared() == true) {
                        filteredList.add(item);
                    }
                }
                break;
            case Constants.MENU_BTN_CHOICE_PRIVATE_LOCATIONS:
                for (LocationsProvider.LocationClass item : locationsList) {
                    if (item.getCreatorsUserID().equals(currentUserID)) {
                        filteredList.add(item);
                    }
                }
                break;
                case Constants.MENU_BTN_NOTIFICATIONS:
                    filteredList.addAll(latestLocationsList);
                    break;
        }

        return filteredList;
    }


    //Runs map and moves camera to current location if permission is granted.
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

        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter((MapActivity.this)));

        LocationsProvider.getInstance(this).getLocations(locations -> {
            latestLocationsList = new ArrayList<>();
            latestLocationsList.addAll(Helpers.getNewLocations(Helpers.getSharedFriends(locations), latestTimesStamp));
            notificationsValue = latestLocationsList.size();
            menuChoiceViewModel.setNotificationCount(notificationsValue);
            System.out.println("Leength" + notificationsValue);


            //Get selected value from menuBtns, listening to btnClicks
            menuChoiceViewModel.getSelectedBtnValue().observe(this, filterLocationsChoice -> {
                //Declaring empty array to store filtered list in
                List<LocationsProvider.LocationClass> sortedList = new ArrayList<>();
                markersList = new ArrayList<>();
                //Adding filtered array from method: filterListMenuChoice()
                sortedList.addAll(filterListMenuChoice(locations, filterLocationsChoice));

                //Update map with filtered array
                mapFragment.getMapAsync(mMap -> {
                    markersList.clear();
                    //Clear map before setting new markers
                    mMap.clear();
                    // Reads in markers and places them with Title, Comment and Icon in Infowindow
                    for (LocationsProvider.LocationClass sortedLocation : sortedList) {
                        MarkerOptions markerOption = new MarkerOptions();
                        markerOption.position(convertGeoToLatLng(sortedLocation.getGeoAddress()));
                        markerOption.title(sortedLocation.getName());
                        markerOption.snippet(sortedLocation.getComment());
                        markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.straw_marker_small)); // If we place an icon in Smultronstalle.java we can fetch it from there instead. Has to be BITMAP [Jennie]
                        mMap.addMarker(markerOption);
                        //Add markers to separate list
                        markersList.add(mMap.addMarker(markerOption));
                    }
                    //ListFragment-listener, selecting and move camera on map to clicked Location from ListFragment
                    menuChoiceViewModel.getSelectLocationFromList().observe(this, geoPoint -> {
                        if(geoPoint != null) {
                            LatLng fromList = convertGeoToLatLng(geoPoint);
                            for (Marker marker : markersList) {
                                if(marker.getPosition().equals(fromList)) {
                                    marker.showInfoWindow();
                                    moveCamera(fromList, 15f);
                                }
                            }
                        }
                    });

                });
            });//end menuChoiceViewModel
        });//end LocationsProvider

        // OnClickListener for InfoWindow
        mMap.setOnInfoWindowClickListener(this);
    }


    //Generates/sets values and uploading Location-item to firebase
    private void onLongClick(LatLng latLng) {
        goToAddPlaceActivity(latLng);
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


    // Convert LatLng so it can bes sent to AddPlaceActivity
    public ArrayList<Double> convertLatLngToDoubleArray (LatLng latLng) {
        ArrayList<Double> latLngArr = new ArrayList<>();
        double lat = latLng.latitude;
        double lng = latLng.longitude;
        latLngArr.add(lat);
        latLngArr.add(lng);
        return latLngArr;
    }


    //Sets camera to current location. Runs method to check permission.
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

    //Initialize the map and custom made Location button
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

    // When the user press the InfoWindow
    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent openBigInfoWindow = new Intent(this, ShowPlaceActivity.class);
        LatLng latLng = marker.getPosition();
        openBigInfoWindow.putExtra("latLng", convertLatLngToDoubleArray(latLng) );
        startActivity(openBigInfoWindow);
    }


    //SIGN OUT METHOD
    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(this, "Signed out", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
    }

    public AppUser getUser() {
        return currentUser;
    }

    public String getLatestTimestampFromMapActivity() {
        return latestTimesStamp;
    }

}