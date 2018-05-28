package com.oumardiallo636.gtuc.troskymate.Map;

/**
 * Created by oumar on 9/10/17.
 */

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.PolyUtil;
import com.oumardiallo636.gtuc.troskymate.Animation.MapAnimator;
import com.oumardiallo636.gtuc.troskymate.Entities.CloseBusStop.BusStop;
import com.oumardiallo636.gtuc.troskymate.Entities.CloseBusStop.CloseStops;
import com.oumardiallo636.gtuc.troskymate.Entities.Direction.WalkingPoints;
import com.oumardiallo636.gtuc.troskymate.R;
import com.oumardiallo636.gtuc.troskymate.Utility.MyFragmentList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
        MapActivityMVP.View {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static MapActivityMVP.View mView;

    private GoogleMap mMap;
    private Marker mMaker;
    private boolean mCameraOnLocation = true;


    private List<List<String>> mPolylineList;
    private List<Polyline> mStraightPolyline = new ArrayList<>();
    private List<Polyline> mDotedPolylines = new ArrayList<>();
    private Queue<Integer> color = new LinkedList<>();

    private ArrayList<String> closeStopNameList;
    private ArrayList<String> closeStoplocationList;


    private String destinationName;
    private Fragment mCurrentBottomFragment;
    private Fragment mCurrentHeaderFragment;

    private int mMapPadding;

    private GeofencingClient mGeofencingClient;
    private ArrayList<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;

    ProgressDialog mDialog;

    /**
     * Get reference to the presenter which contain the business logic of the app
     **/
    Presenter presenter;

    @BindView(R.id.stop_one)
    RadioButton stop1;

    @BindView(R.id.stop_two)
    RadioButton stop2;

    @BindView(R.id.stop_three)
    RadioButton stop3;

    @BindView(R.id.stop_four)
    RadioButton stop4;

    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;

    @BindView(R.id.tv_cancel)
    TextView cancel;

    @BindView(R.id.tv_select_origin)
    TextView select_origin;

    @BindView(R.id.btn_show_direction)
    Button btnshowDirection;

    @BindView(R.id.tv_search)
    TextView search;

    @OnClick(R.id.tv_cancel)
    public void cancelClick() {
        clearMap();
    }

    @OnClick(R.id.btn_show_direction)
    public void submit(View view) {
        // TODO submit data to server...

        int radioCheckedId = radioGroup.getCheckedRadioButtonId();

        String origin = "";

        switch (radioCheckedId) {
            case R.id.stop_one:
                origin = closeStoplocationList.get(0);
                //Toast.makeText(getContext(),"location 1 "+stop_locations.get(0),Toast.LENGTH_SHORT).show();
                break;
            case R.id.stop_two:
                origin = closeStoplocationList.get(1);
                // Toast.makeText(getContext(),"location 2 "+stop_locations.get(1),Toast.LENGTH_SHORT).show();
                break;
            case R.id.stop_three:
                origin = closeStoplocationList.get(2);
                // Toast.makeText(getContext(),"location 3 "+stop_locations.get(2),Toast.LENGTH_SHORT).show();
                break;
            case R.id.stop_four:
                origin = closeStoplocationList.get(3);
                //Toast.makeText(getContext(),"location 4 "+stop_locations.get(3),Toast.LENGTH_SHORT).show();
                break;
        }

        searchDirection(origin);
//        startProgressBar("Loading direction");
        switchBottomFragment(MyFragmentList.START_DIRECTION, null);
    }

    public static MapActivityMVP.View getInstance() {
        return mView;
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        color.add(Color.BLUE);
        color.add(Color.GREEN);
        color.add(Color.RED);
        color.add(Color.BLACK);

//        ButterKnife.bind(this);
        mView = this;

//        activateToolbar(false);

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


        CustomToolBar customToolBar = new CustomToolBar();
        mCurrentHeaderFragment = customToolBar;

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container_header_layout, customToolBar)
                .commitAllowingStateLoss();

        mDialog = new ProgressDialog(this);
        mGeofencingClient = LocationServices.getGeofencingClient(this);
        mGeofenceList = new ArrayList<>();

        mGeofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId("geofence1")
                .setCircularRegion(
                        5.603153,
                        -0.235881,
                        50
                )
                .setExpirationDuration(60000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());


    }

    /**
     * The following Method uses the GeofencingRequest class and its nested
     * GeofencingRequestBuilder class to specify the geofences to monitor and to
     * set how related geofence events are triggered:
     *
     * @return
     */

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private void startGeofences() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Geofences added
                        // ...

                        Toast.makeText(MainActivity.this, "geo added correctly", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to add geofences
                        // ...

                        Toast.makeText(MainActivity.this, "geo added correctly", Toast.LENGTH_LONG);
                    }
                });
    }


    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        mGeofencePendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
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

        mMap.setPadding(0, 108, 0, 0);

    }


    public void searchDirection(String origin) {

        String destination = presenter.getStringDestination();

        presenter.getDirection(origin, destination.toString());
    }

    /**
     * This method is called when user clicks on toolbar's search field .
     */
    @Override
    public void searchPlace(View view) {
        Log.d(TAG, "searchPlace: starts");

        clearMap();
        presenter.findDestination();
    }

    public void clearMap() {
        mMap.clear();
        hideBottomFragment();

        if (!(mCurrentHeaderFragment instanceof CustomToolBar)) {
            switchHeaderFragment(MyFragmentList.CUSTOM_TOOLBAR, null);
            search.setText(null);
            search.setHint("Where to ?");
        }

        if (search != null) {
            search.setText(null);
        }

        changeMapBottomPadding(0);
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
    public void displayWakingPath(List<WalkingPoints> walkingPoints) {
        Log.d(TAG, "displayWakingPath: starts");

        List<PatternItem> pattern = Arrays.<PatternItem>asList(
                new Dot(), new Gap(10));

        if (mDotedPolylines.size() != 0) {
            for (Polyline polyline : mDotedPolylines) {
                polyline.remove(); // Remove existing polyline from the map
            }
        }

        for (WalkingPoints path : walkingPoints) {

            String origin = path.getOrigin();
            double originLat = Double.parseDouble(origin.split(",")[0]);
            double originLng = Double.parseDouble(origin.split(",")[1]);
            LatLng originCoordinates = new LatLng(originLat, originLng);

            String destination = path.getDestination();
            double destiLat = Double.parseDouble(destination.split(",")[0]);
            double destiLng = Double.parseDouble(destination.split(",")[1]);
            LatLng destinationCoordinates = new LatLng(destiLat, destiLng);

            PolylineOptions polylineOptions = new PolylineOptions()
                    .add(originCoordinates)
                    .add(destinationCoordinates)
                    .color(Color.BLUE)
                    .pattern(pattern);
            mDotedPolylines.add(mMap.addPolyline(polylineOptions));

        }

        Log.d(TAG, "displayWakingPath: ends");
    }

    @Override
    public void drawPolyline(List<String> route) {

        PolylineOptions options = new PolylineOptions();

        for (String p : route) {
            options.addAll(PolyUtil.decode(p));
        }
//            route.addAll(PolyUtil.decode(p));
        Polyline polyline = mMap.addPolyline(options);
        polyline.setColor(Color.BLUE);
        polyline.setWidth(10);
        mStraightPolyline.add(polyline);
    }

    @Override
    public void drawMarkers(List<MarkerOptions> markerOptions) {

        //remove destination marker
        mMaker.remove();

        Log.d(TAG, "drawMarkers: starts");
        for (MarkerOptions m : markerOptions) {
            Log.d(TAG, "drawMarkers: " + m);
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

    public void hideBottomFragment() {

        if (mCurrentBottomFragment instanceof StartFragment) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.start_slide_in, R.anim.start_slide_out)
                    .hide(mCurrentBottomFragment)
                    .remove(mCurrentBottomFragment)
                    .commitAllowingStateLoss();
        } else if (mCurrentBottomFragment instanceof ShowDirection) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.show_direction_slide_in, R.anim.show_direction_slide_out)
                    .hide(mCurrentBottomFragment)
                    .remove(mCurrentBottomFragment)
                    .commitAllowingStateLoss();
        } else if (mCurrentBottomFragment instanceof BottomDistanceTimeFragment) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.show_direction_slide_in, R.anim.show_direction_slide_out)
                    .hide(mCurrentBottomFragment)
                    .remove(mCurrentBottomFragment)
                    .commitAllowingStateLoss();
        }
    }

    public void hideHeaderFragment() {

        if (mCurrentHeaderFragment instanceof CustomToolBar) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.next_stop_info_slide_in, R.anim.next_stop_info_slide_out)
                    .hide(mCurrentHeaderFragment)
                    .remove(mCurrentHeaderFragment)
                    .commitAllowingStateLoss();
        } else if (mCurrentHeaderFragment instanceof NextStopInfo) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.show_direction_slide_in, R.anim.show_direction_slide_out)
                    .hide(mCurrentHeaderFragment)
                    .remove(mCurrentHeaderFragment)
                    .commitAllowingStateLoss();
        }

    }

    @Override
    public void switchBottomFragment(String toFragment, Bundle bundle) {

        Log.d(TAG, "switchBottomFragment: starts");
        //animate hiding of the old fragment

        if (mCurrentBottomFragment != null) {
            hideBottomFragment();
        }
        switch (toFragment) {
            case MyFragmentList.START_DIRECTION:
                Log.d(TAG, "switchBottomFragment: called");

                StartFragment startFragment = new StartFragment();
                mCurrentBottomFragment = startFragment;

                startFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.show_direction_slide_in, R.anim.show_direction_slide_out)
                        .add(R.id.container_layout, startFragment)
                        .commitAllowingStateLoss();
                break;

            case MyFragmentList.SHOW_DIRECTION:

                ShowDirection showDirection = new ShowDirection();
                mCurrentBottomFragment = showDirection;
                showDirection.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.show_direction_slide_in, R.anim.show_direction_slide_out)
                        .replace(R.id.container_layout, showDirection)
                        .commitAllowingStateLoss();
                break;

            case MyFragmentList.BOTTOM_DISTANCE_TIME_FRAG:
                BottomDistanceTimeFragment fragment = new BottomDistanceTimeFragment();
                mCurrentBottomFragment = fragment;

                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.start_slide_in, R.anim.start_slide_out)
                        .replace(R.id.container_layout, fragment)
                        .commitAllowingStateLoss();

                break;
            default:
                break;
        }
        Log.d(TAG, "switchBottomFragment: ends");
    }


    @Override
    public void switchHeaderFragment(String toFragment, Bundle bundle) {

        Log.d(TAG, "switchBottomFragment: starts");
        //animate hiding of the old fragment

        if (mCurrentHeaderFragment != null) {
            hideHeaderFragment();
        }
        switch (toFragment) {
            case MyFragmentList.CUSTOM_TOOLBAR:
                Log.d(TAG, "switchBottomFragment: called");

                CustomToolBar customToolBar = new CustomToolBar();
                mCurrentHeaderFragment = customToolBar;

                customToolBar.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container_header_layout, customToolBar)
                        .commitAllowingStateLoss();
                break;

            case MyFragmentList.NEXT_STOP_INFO:

                NextStopInfo nextStopInfo = new NextStopInfo();
                mCurrentHeaderFragment = nextStopInfo;
                nextStopInfo.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container_header_layout, nextStopInfo)
                        .commitAllowingStateLoss();
                break;
            default:
                break;
        }
        Log.d(TAG, "switchHeaderFragment: ends");
    }

    /**
     * Callback received when a list of close bus stops have been returned.
     */
    @Override
    public void saveCloseStops(CloseStops stops) {

        closeStopNameList = new ArrayList<>();
        closeStoplocationList = new ArrayList<>();

        List<BusStop> busStops = stops.getBusStop();
        if (busStops.size() > 0) {

            for (BusStop stop : busStops) {
                closeStopNameList.add(stop.getStopName());
                closeStoplocationList.add(stop.getStopLocation());
            }

        }

        if (mCurrentBottomFragment instanceof ShowDirection) {

            ButterKnife.bind(this);

            ViewSwitcher switcher = (ViewSwitcher) findViewById(R.id.viewSwitcher);
            RelativeLayout progressbarContainer = (RelativeLayout) findViewById(R.id.pb_container);

            if (busStops.size() > 0) {
                stop1.setText(closeStopNameList.get(0));
                stop2.setText(closeStopNameList.get(1));
                stop3.setText(closeStopNameList.get(2));
                stop4.setText(closeStopNameList.get(3));
            }

            if (switcher.getCurrentView() == progressbarContainer) {
                switcher.showNext();
                cancel.setVisibility(View.VISIBLE);
                btnshowDirection.setVisibility(View.VISIBLE);
                select_origin.setVisibility(View.VISIBLE);
            }

//            search.setText(destinationName);
        }

    }

    @Override
    public void showNoRouteDialogue(String message) {

        stopProgressBar();
        Snackbar.make(
                findViewById(android.R.id.content),
                message,
                Snackbar.LENGTH_LONG)
                .show();

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
                    double destinationLat = place.getLatLng().latitude;
                    double destinationLng = place.getLatLng().longitude;

                    presenter.saveDestination(destinationLat, destinationLng);
                    requestClosestStops();
                    showDestination();

                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    Status status = PlaceAutocomplete.getStatus(this, data);
                    // TODO: Handle the error.
                    Log.i(TAG, status.getStatusMessage());

                } else if (resultCode == RESULT_CANCELED) {
                    // The user canceled the operation.
                }
        }
    }


    private void requestClosestStops() {

        Bundle bundle = new Bundle();
        presenter.getClosestStops();

        bundle.putStringArrayList("stop_names", closeStopNameList);
        bundle.putStringArrayList("stop_location", closeStoplocationList);
        switchBottomFragment(MyFragmentList.SHOW_DIRECTION, bundle);
    }

    @Override
    public void changeMapBottomPadding(int padding) {
        mMapPadding = padding;
        ValueAnimator animator = ValueAnimator.ofInt(mMapPadding, padding);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mMap.setPadding(0, 116, 0, (Integer) valueAnimator.getAnimatedValue());
            }
        });

        animator.setDuration(5000);
        animator.start();
    }


    public String getDestinationName() {
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

    public void startProgressBar(String message) {

        mDialog.setMessage(message);
        mDialog.setCancelable(false);
        mDialog.setInverseBackgroundForced(false);

        mDialog.show();
    }

    /**
     * This function stops the progress bar then move the map camera to the destination of the user
     ***/
    public void stopProgressBar() {
        Log.d(TAG, "stopProgressBar: ");
        mDialog.hide();

        Log.d(TAG, "stopProgressBar: ends");
    }

    public void showDestination() {

        if (mCurrentHeaderFragment instanceof CustomToolBar) {

            TextView tv_search = (TextView) findViewById(R.id.tv_search);

            tv_search.setText(destinationName);

            LatLng destination = presenter.getDestination();

            if (mMaker != null)
                mMaker.remove(); // remove any existing destination marker from the map
            mMaker = mMap.addMarker(new MarkerOptions() // add a new destination marker to the map
                    .title(destinationName)
                    .position(destination));

            moveCameraToLocation(presenter.getDestination());
        }
    }

    @Override
    public void loadStartFragment(double distance, int seconds) {

        double valueInkillo = distance / 1000;

        int day = (int) TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds) - (day * 24);
        long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds) * 60);
        long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) * 60);

        StringBuilder remainingTime = new StringBuilder();

        if (day != 0){
            remainingTime.append(day)
                   .append("day ");
        }

        remainingTime.append(hours)
                .append("h ")
                .append(minute)
                .append("min ")
                .append(second)
                .append("s");

        if (mCurrentBottomFragment instanceof StartFragment) {

            ViewSwitcher viewSwitcher = (ViewSwitcher) findViewById(R.id.vs_start);
            TextView distanceTextView = (TextView) findViewById(R.id.tv_distance);
            TextView arrivalTime = (TextView) findViewById(R.id.tv_arrival_time);
            Button start = (Button) findViewById(R.id.btn_start);

            ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.ct_layout_progressBar);

            if (viewSwitcher.getCurrentView() == constraintLayout) {
                viewSwitcher.showNext();
                start.setVisibility(View.VISIBLE);
                distanceTextView.setText(valueInkillo+"km");
                arrivalTime.setText(remainingTime.toString());
            }
        }
    }
}