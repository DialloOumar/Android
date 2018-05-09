package com.oumardiallo636.gtuc.troskymate.Map;

/**
 * Created by oumar on 9/10/17.
 */

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.oumardiallo636.gtuc.troskymate.Animation.MapAnimator;
import com.oumardiallo636.gtuc.troskymate.Entities.CloseBusStop.BusStop;
import com.oumardiallo636.gtuc.troskymate.Entities.CloseBusStop.CloseStops;
import com.oumardiallo636.gtuc.troskymate.R;
import com.oumardiallo636.gtuc.troskymate.Utility.MyFragmentList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Using location settings.
 * <p/>
 * Uses the {@link com.google.android.gms.location.SettingsApi} to ensure that the device's system
 * settings are properly configured for the app's location needs. When making a request to
 * Location services, the device's system settings may be in a state that prevents the app from
 * obtaining the location data that it needs. For example, GPS or Wi-Fi scanning may be switched
 * off. The {@code SettingsApi} makes it possible to determine if a device's system settings are
 * adequate for the location request, and to optionally invoke a dialog that allows the user to
 * enable the necessary settings.
 * <p/>
 * This sample allows the user to request location updates using the ACCESS_FINE_LOCATION setting
 * (as specified in AndroidManifest.xml).
 */

public class MainActivity extends BaseActivity implements
        OnMapReadyCallback,
        MapActivityMVP.View,
        NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static MapActivityMVP.View mView;

    private GoogleMap mMap;
    private Marker mMaker;
    private boolean mCameraOnLocation = true;


    private List<List<String>> mPolylineList;
    private List<Polyline> mStraightPolyline = new ArrayList<>();
    private List<Polyline> mDotedPolylines = new ArrayList<>();
    private Queue<Integer> color = new LinkedList<>();


    private String destinationName;
    private Fragment mCurrentFragment;
    private int mMapPadding;

    ProgressDialog mDialog;


    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.tv_search)
    TextView tvSearch;

    /**
     * Get reference to the presenter which contain the business logic of the app
     **/
    Presenter presenter;


    public static MapActivityMVP.View getInstance(){
        return mView;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        color.add(Color.BLUE);
        color.add(Color.GREEN);
        color.add(Color.RED);
        color.add(Color.BLACK);

        ButterKnife.bind(this);
        mView = this;

        activateToolbar(true);

        Log.d(TAG, "onCreate: starts");

        presenter = new Presenter();

        // Update values using data stored in the Bundle.
        presenter.updateValuesFromBundle(savedInstanceState);

        // Kick off the process of building the LocationCallback, LocationRequest, and
        // LocationSettingsRequest objects.
        presenter.createLocationCallback();
        presenter.createLocationRequest();
        presenter.buildLocationSettingsRequest();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Log.d(TAG, "onCreate: ends");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, getToolbar(), R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mDialog= new ProgressDialog(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: starts");
        // Within {@code onPause()}, we remove location updates. Here, we resume receiving
        // location updates if the user has requested them.
        if (presenter.getRequestingLocationUpdates() && presenter.checkPermissions()) {
            presenter.startLocationUpdates();
        } else if (!presenter.checkPermissions()) {
            presenter.requestPermissions();
        }
        presenter.startLocationUpdates();
        Log.d(TAG, "onResume: ends");
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Remove location updates to save battery.
        presenter.stopLocationUpdates();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: starts");
        mMap = googleMap;

        showCurrentLocation();
        mMap.setBuildingsEnabled(true);
        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

    }

//    @OnClick(R.id.btn_show_direction)
//    public void submit() {
//        Log.d(TAG, "submit: start");
//        displayWakingPath();
//
////        ConstraintLayout layout = findViewById(R.id.layout_direction);
////        layout.removeView(tvBuses);
////        layout.removeView(btnShowDirection);

//    }

//    @OnClick(R.id.fab)
//    public void fabClick() {
//        //move the camera to the current location
//        LatLng location = new LatLng(presenter.getCurrentLocation().getLatitude(),
//                presenter.getCurrentLocation().getLongitude());
//        moveCameraToLocation(location);
//    }

//    @OnClick(R.id.tv_cancel_route)
//    public void cancelClick() {
//        if (mPolyline != null) mPolyline.remove();
////        layoutDirection.setVisibility(View.INVISIBLE);
//        tvSearch.setText("Search");
//        mMap.setPadding(0, 0, 0, 0); // restore the padding of the map to 0
//        mMaker.remove();
//    }


    public void searchDirection(String origin){

        String destination = presenter.getStringDestination();

        presenter.getDirection(origin, destination.toString());
    }
    /**
     *This method is called when user clicks on toolbar's search field .
     */
    @Override
    public void searchPlace(View view) {
        Log.d(TAG, "searchPlace: starts");

        clearMap();
        presenter.findDestination();
    }

    public void clearMap(){
        mMap.clear();
        hideFragment();
        tvSearch.setText("Search");
        changeMapPadding(0);
    }

    /**
     * Sets the value of the UI fields for the location latitude, longitude and last update time.
     */
    @Override
    public void updateLocationUI(Location currentLocation) {
        Log.d(TAG, "updateLocationUI: starts");
        Log.d(TAG, "updateLocationUI: " + currentLocation);
        if (currentLocation != null) {

            LatLng currentPosition = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            showCurrentLocation();
            if (mCameraOnLocation) {
                moveCameraToLocation(currentPosition);
                mCameraOnLocation = false;
            }
        } else {
            Toast.makeText(this, "location is null", Toast.LENGTH_SHORT).show();
        }
        Log.d(TAG, "updateLocationUI: ends");
    }

    /**
     * Stores activity data in the Bundle.
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(KEY_REQUESTING_LOCATION_UPDATES, presenter.getRequestingLocationUpdates());
        savedInstanceState.putParcelable(KEY_LOCATION, presenter.getCurrentLocation());
        savedInstanceState.putString(KEY_LAST_UPDATED_TIME_STRING, presenter.getLastUpdateTime());
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    public void showSnackbar(final int mainTextStringId, final int actionStringId,
                             View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }


    @Override
    public void displayWakingPath(List<PolylineOptions> dotedPolylines) {
        Log.d(TAG, "displayWakingPath: starts");


        List<PatternItem> pattern = Arrays.<PatternItem>asList(
                new Dot(), new Gap(20), new Dash(30), new Gap(20));

//        if (mStraightPolyline.size() != 0 ){
//            for(Polyline polyline : mStraightPolyline){
//                polyline.remove(); // Remove existing polyline from the map
//            }
//        }

        if (mDotedPolylines.size() != 0 ){
            for(Polyline polyline : mDotedPolylines){
                polyline.remove(); // Remove existing polyline from the map
            }
        }

//        for (List<String> polyline : polylineList) {
//
//            PolylineOptions options = new PolylineOptions();
//
//            for(String p : polyline){
//                options.addAll(PolyUtil.decode(p));
//            }
////            route.addAll(PolyUtil.decode(p));
//            mStraightPolyline.add(mMap.addPolyline(options));
//        }

        for (PolylineOptions polylineOption: dotedPolylines){
            Polyline p = mMap.addPolyline(polylineOption);
            p.setPattern(pattern);
            mDotedPolylines.add(p);
        }

//        displayWakingPath();

        Bundle bundle = new Bundle();
        bundle.putString("s","hello world");

        switchFragment(null,MyFragmentList.START_DIRECTION, bundle);

        Log.d(TAG, "displayWakingPath: ends");
    }

    @Override
    public void drawPolyline(List<String> route) {

        PolylineOptions options = new PolylineOptions();

        for(String p : route){
                options.addAll(PolyUtil.decode(p));
        }
//            route.addAll(PolyUtil.decode(p));
        Polyline polyline = mMap.addPolyline(options);
        polyline.setColor(Color.BLUE);
        mStraightPolyline.add(polyline);
    }

    @Override
    public void drawMarkers(List<MarkerOptions> markerOptions) {

        //remove destination marker
        mMaker.remove();

        Log.d(TAG, "drawMarkers: starts");
        for (MarkerOptions m : markerOptions){
            Log.d(TAG, "drawMarkers: "+ m);
            mMap.addMarker(m);
        }

        Log.d(TAG, "drawMarkers: ends");
    }

    @Override
    public void moveCameraToLocation(LatLng location) {
//        LatLng currentLocation = new LatLng(location.getAltitude(),location.getLongitude());
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        CameraPosition cameraPosition = CameraPosition.builder()
                .target(location)
                .zoom(13)
                .bearing(90)
                .build();
        // Animate the change in camera view over 2 seconds
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                2000, null);
    }

    public void hideFragment(){

        if (mCurrentFragment instanceof StartFragment){
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.start_slide_in,R.anim.start_slide_out)
                    .hide(mCurrentFragment)
                    .commitAllowingStateLoss();
        } else if (mCurrentFragment instanceof ShowDirection){
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in,R.anim.slide_out)
                    .hide(mCurrentFragment)
                    .commitAllowingStateLoss();
        }

    }

    @Override
    public void switchFragment(Fragment fromFragment, String toFragment, Bundle bundle) {

        Log.d(TAG, "switchFragment: starts");
        //animate hiding of the old fragment

        if (mCurrentFragment != null){
            hideFragment();
        }
        switch (toFragment){
            case MyFragmentList.START_DIRECTION:
                Log.d(TAG, "switchFragment: called");

                StartFragment startFragment = new StartFragment();
                mCurrentFragment = startFragment;

                startFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.start_slide_in,R.anim.start_slide_out)
                        .add(R.id.container_layout, startFragment)
                        .commitAllowingStateLoss();
                break;

            case MyFragmentList.SHOW_DIRECTION:

                ShowDirection showDirection = new ShowDirection();
                mCurrentFragment = showDirection;
                showDirection.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in,R.anim.slide_out)
                        .replace(R.id.container_layout, showDirection)
                        .commitAllowingStateLoss();
                break;

            case MyFragmentList.BUS_STOP_FRAG:
                break;
            default:
                break;
        }
        Log.d(TAG, "switchFragment: ends");
    }

    /**
     * Callback received when a list of close bus stops have been returned.
     */
    @Override
    public void showCloseStops(CloseStops stops) {

        Bundle bundle = new Bundle();

        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> locations = new ArrayList<>();

        List<BusStop> busStops = stops.getBusStop();
        if (busStops.size() > 0) {

            for (BusStop stop : busStops) {
                locations.add(stop.getStopLocation());
                names.add(stop.getStopName());
            }

        }

        bundle.putStringArrayList("stop_names", names);
        bundle.putStringArrayList("stop_location", locations);

        switchFragment(null,MyFragmentList.SHOW_DIRECTION, bundle);

//        mBusStopsFragment.displayCloseStops(stops,this);

    }

    @Override
    public void showErrorGettingCloseStops() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: starts");
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User chose not to make required location settings changes.");
//                        mRequestingLocationUpdates = false;
////                        updateUI();
//                        startLocationUpdates();
                        break;
                }
                break;
            case PLACE_AUTOCOMPLETE_REQUEST_CODE:

                if (resultCode == RESULT_OK) {
                    Place place = PlaceAutocomplete.getPlace(this, data);
                    destinationName = (String) place.getName();
                    tvSearch.setText(destinationName);
                    double destinationLat = place.getLatLng().latitude;
                    double destinationLng = place.getLatLng().longitude;

                    presenter.getClosestStops(destinationLat, destinationLng);

                    startProgressBar("Loading");
                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    Status status = PlaceAutocomplete.getStatus(this, data);
                    // TODO: Handle the error.
                    Log.i(TAG, status.getStatusMessage());

                } else if (resultCode == RESULT_CANCELED) {
                    // The user canceled the operation.
                }
        }
    }


    @Override
    public void changeMapPadding(int padding){
        mMapPadding = padding;
        ValueAnimator animator = ValueAnimator.ofInt(mMapPadding, padding);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator){
                mMap.setPadding(0, 0, 0, (Integer) valueAnimator.getAnimatedValue());
            }
        });

        animator.setDuration(5000);
        animator.start();
    }


    public String getDestinationName(){
        return destinationName;
    }


    private void startAnim(List<LatLng> route) {
        if (mMap != null) {
            MapAnimator.getInstance().animateRoute(mMap, route);
        } else {
            Toast.makeText(getApplicationContext(), "Map not ready", Toast.LENGTH_LONG).show();
        }
    }

    private void showCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }
    public void startProgressBar(String message){

        mDialog.setMessage(message);
        mDialog.setCancelable(false);
        mDialog.setInverseBackgroundForced(false);
        mDialog.show();
    }

    /**
     * This function stops the progress bar then move the map camera to the destination of the user
     * **/
    public void stopProgressBar(){

        mDialog.hide();

        LatLng destination = presenter.getDestination();

        if (mMaker != null) mMaker.remove(); // remove any existing destination marker from the map
        mMaker = mMap.addMarker(new MarkerOptions() // add a new destination marker to the map
                .title(destinationName)
                .position(destination));

//        mMap.setPadding(0, 0, 0, padding); // add padding to prevent obscuring the google logo
        moveCameraToLocation(presenter.getDestination());

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}