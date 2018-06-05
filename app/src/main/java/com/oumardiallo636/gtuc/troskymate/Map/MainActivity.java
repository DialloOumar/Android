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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
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

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
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
import com.google.android.gms.maps.model.LatLngBounds;
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
import com.oumardiallo636.gtuc.troskymate.Entities.Direction.Stop;
import com.oumardiallo636.gtuc.troskymate.Entities.Direction.WalkingPoints;
import com.oumardiallo636.gtuc.troskymate.R;
import com.oumardiallo636.gtuc.troskymate.Utility.DayTimeSecondConverter;
import com.oumardiallo636.gtuc.troskymate.Utility.MyFragmentList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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
    private ArrayList<Stop> mListBusStops;

    private Stop mNextStop;
    private Stop mCurrentStop;

    private Stop mDestinationStop;


    Vibrator vibrator;
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

        switchBottomFragment(MyFragmentList.START_DIRECTION, null);
    }

    public static MapActivityMVP.View getInstance() {
        return mView;
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

//        color.add(Color.BLUE);
//        color.add(Color.GREEN);
//        color.add(Color.RED);
//        color.add(Color.BLACK);

       vibrator = (Vibrator) this.getSystemService(this.VIBRATOR_SERVICE);

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


    }

    /**
     * The following Method uses the GeofencingRequest class and its nested
     * GeofencingRequestBuilder class to specify the geofences to monitor and to
     * set how related geofence events are triggered:
     *
     * @return
     */

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
                    Double destinationLat = place.getLatLng().latitude;
                    Double destinationLng = place.getLatLng().longitude;

                    // keeping a reference of the final destination

                    StringBuilder destinationBuilder = new StringBuilder();
                    destinationBuilder.append(destinationLat.toString())
                            .append(",")
                            .append(destinationLng.toString());

                    mDestinationStop = new Stop();
                    mDestinationStop.setStopName(destinationName);
                    mDestinationStop.setBusName("END");
                    mDestinationStop.setStopLocation(destinationBuilder.toString());


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
                moveCameraToLocation(currentPosition, 0);
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



    /**
     * ********************************** GEOFENCE MANAGEMENT  ***********************************
     **/

    @Override
    public void createGeofences(List<Stop> stops) {
        Log.d(TAG, "createGeofences: starts");

        mGeofenceList = new ArrayList<>();
        mListBusStops = new ArrayList<>();

        mNextStop = stops.get(0);
        mListBusStops.addAll(stops);

        mGeofenceList.add(new Geofence.Builder()
                .setRequestId("1")
                .setCircularRegion(
                        5.602911,
                        -0.236360,
                        5000
                )
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());

        for (int i = 0; i < stops.size(); i++) {

            double lat = Double.parseDouble(stops.get(i).getStopLocation().split(",")[0]);
            double lng = Double.parseDouble(stops.get(i).getStopLocation().split(",")[1]);

            mGeofenceList.add(new Geofence.Builder()
                    .setRequestId(i+"")
                    .setCircularRegion(lat, lng, 40)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .build());

        }
//
        Log.d(TAG, "createGeofences: ends");
    }

    @Override
    public void geofenceResponse(final String message) {

        Log.d(TAG, "geofenceResponse: starts");

        int currentStopId = Integer.parseInt(message);

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(2000, VibrationEffect.DEFAULT_AMPLITUDE));

                }else{
                    //deprecated in API 26
                    vibrator.vibrate(2000);
                }
            }
        });

        if ( (mCurrentHeaderFragment instanceof NextStopInfo)){

            final TextView directionInfoTV =  findViewById(R.id.tv_info);
            final TextView stopNameInfoTV = (TextView) findViewById(R.id.tv_next_stop_name);
            final Stop nextBusStop = mListBusStops.get(currentStopId + 1);

            mCurrentStop = mListBusStops.get(currentStopId);
            final Stop tempStop = mCurrentStop;

            if ( (currentStopId +1) <= mListBusStops.size() ){
                mNextStop = mListBusStops.get(currentStopId + 1);
                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        Log.d(TAG, "geofenceResponse: got run");
                        directionInfoTV.setText("YOU HAVE ARRIVED AT ");
                        stopNameInfoTV.setText(tempStop.getStopName());

                    }
                });

            }else {
                mNextStop = null;
            }

        }

        Log.d(TAG, "geofenceResponse: ends");
    }

    private void changeDirectionIcon(Stop previousStop){

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

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    @Override
    public void startGeofences() {

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

                        Toast.makeText(MainActivity.this, "Failure to add geofence", Toast.LENGTH_LONG);
                    }
                });
    }

    @Override
    public void removeAGeofence(final String message) {

        List<String> toRemove = new ArrayList<String>();

        toRemove.add(message);

        mGeofencingClient.removeGeofences(toRemove)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Geofences removed
                        // ...
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Exit", Toast.LENGTH_LONG).show();
                                Toast.makeText(MainActivity.this, message + " removed", Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to remove geofences
                        // ...
                    }
                });
    }

    /**
     * ****************************** FRAGMENT MANAGEMENT *************************************
     **/
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

                Bundle bundle1 = new Bundle();
                bundle1.putString("next_stop_name", mNextStop.getStopName());

                nextStopInfo.setArguments(bundle1);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container_header_layout, nextStopInfo)
                        .commitAllowingStateLoss();

                LatLng target = new LatLng(presenter.getCurrentLocation().getLatitude(),
                        presenter.getCurrentLocation().getLongitude());

                moveCameraToLocation(target,2);
                break;
            default:
                break;
        }
        Log.d(TAG, "switchHeaderFragment: ends");
    }

    /**
     * ********************************** Map Management ************************************
     */

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
    public void moveCameraToLocation(LatLng location, int type) {


        if (type == 0){

            CameraPosition cameraPosition = CameraPosition.builder()
                    .target(location)
                    .zoom(13)
                    .bearing(90)
                    .build();
            // Animate the change in camera view over 2 seconds
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                    2000, null);
        } else if (type == 1){

            Log.d(TAG, "moveCameraToLocation: called");

            LatLng origin = new LatLng(presenter.getCurrentLocation().getLatitude(),
                    presenter.getCurrentLocation().getLongitude());

            LatLng destination = new LatLng(presenter.getCurrentLocation().getLatitude(),
                    presenter.getCurrentLocation().getLongitude());

            LatLngBounds target = new LatLngBounds(origin, destination);

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(target.getCenter(),12),2000,null);

        } else if (type == 2){

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(presenter.getCurrentLocation().getLatitude(),
                            presenter.getCurrentLocation().getLongitude()))      // Sets the center of the map to Mountain View
                    .zoom(18)                   // Sets the zoom
                    .bearing(180)                // Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                    2000, null);
        }


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

            moveCameraToLocation(presenter.getDestination(),0);
        }
    }


    private void startAnim(List<LatLng> route) {
        if (mMap != null) {
            MapAnimator.getInstance().animateRoute(mMap, route);
        } else {
            Toast.makeText(getApplicationContext(), "Map not ready", Toast.LENGTH_LONG).show();
        }
    }

    private void showCurrentLocation() {
        Log.d(TAG, "showCurrentLocation: starts");
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

        if (mNextStop != null && mNextStop.getBusName().equalsIgnoreCase("walking")) {
            updateNavigationLayout("driving");
        }else {
            updateNavigationLayout("driving");
        }

        mMap.setMyLocationEnabled(true);
    }

    private void updateNavigationLayout(String mode){

        Log.d(TAG, "updateNavigationLayout: starts");

        if ( (mCurrentHeaderFragment instanceof NextStopInfo)
                && (mCurrentBottomFragment instanceof BottomDistanceTimeFragment) ){

            List<String> origin = new ArrayList<>();

            Location location = presenter.getCurrentLocation();
            Double lat = location.getLatitude();
            Double lng = location.getLongitude();

            StringBuilder originBuilder = new StringBuilder();
            originBuilder.append(lat.toString())
                    .append(",")
                    .append(lng.toString());

            origin.add(originBuilder.toString());

            List<String> destination = new ArrayList<>();

            if (mNextStop != null){
                destination.add(mNextStop.getStopLocation());
                destination.add(mDestinationStop.getStopLocation());
                presenter.getDistanceAndTime(origin,destination, mode);
            }


        }

    }

    /**
     * ****************************** REQUEST MANAGEMENT **********************************
     * */
    private void requestClosestStops() {

        Bundle bundle = new Bundle();
        presenter.getClosestStops();

//        bundle.putStringArrayList("stop_names", closeStopNameList);
//        bundle.putStringArrayList("stop_location", closeStoplocationList);
        switchBottomFragment(MyFragmentList.SHOW_DIRECTION, bundle);
    }

    /**
     ********************************* RESPONSE MANAGEMENT **************************************
     * */

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
    public void displayWakingPath(List<WalkingPoints> walkingPoints) {
        Log.d(TAG, "displayWakingPath: starts");

        final List<PatternItem> pattern = Arrays.<PatternItem>asList(
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
            final LatLng originCoordinates = new LatLng(originLat, originLng);

            String destination = path.getDestination();
            double destiLat = Double.parseDouble(destination.split(",")[0]);
            double destiLng = Double.parseDouble(destination.split(",")[1]);
            final LatLng destinationCoordinates = new LatLng(destiLat, destiLng);

            GoogleDirection.withServerKey(ApplicationRepo.GoogleRepo.apiKey)
                    .from(originCoordinates)
                    .to(destinationCoordinates)
                    .transportMode(TransportMode.WALKING)
                    .execute(new DirectionCallback() {
                        @Override
                        public void onDirectionSuccess(Direction direction, String rawBody) {
                            // Do something here
                            if (direction.isOK()){

                                Log.d(TAG, "onDirectionSuccess: okk");
                                Route route = direction.getRouteList().get(0);
                                Leg leg = route.getLegList().get(0);
                                ArrayList<LatLng> pointList = leg.getDirectionPoint();

                                PolylineOptions polylineOptions = DirectionConverter.createPolyline(MainActivity.this, pointList, 5, Color.BLUE);
                                polylineOptions.pattern(pattern);
                                mMap.addPolyline(polylineOptions);

                            } else {

                                PolylineOptions polylineOptions = new PolylineOptions()
                                    .add(originCoordinates)
                                    .add(destinationCoordinates)
                                    .color(Color.BLUE)
                                    .pattern(pattern);
                                mDotedPolylines.add(mMap.addPolyline(polylineOptions));
                            }
                        }

                        @Override
                        public void onDirectionFailure(Throwable t) {
                            // Do something here
                        }
                    });

//            PolylineOptions polylineOptions = new PolylineOptions()
//                    .add(originCoordinates)
//                    .add(destinationCoordinates)
//                    .color(Color.BLUE)
//                    .pattern(pattern);
//            mDotedPolylines.add(mMap.addPolyline(polylineOptions));

        }

        moveCameraToLocation(null, 1);
        Log.d(TAG, "displayWakingPath: ends");
    }

    @Override
    public void updateNextStopTimeAndDate(long seconds, int distanceMeters) {

        if ( mCurrentHeaderFragment instanceof NextStopInfo){

            String totalDistance;

            if (distanceMeters > 1000){
                Double distance = distanceMeters / 1000.0;
                totalDistance = distance.toString()+" km";
            }else {
                Integer distance = distanceMeters;
                totalDistance = distance.toString()+ " m";
            }
             //convert distance from meters to kilometers



            DayTimeSecondConverter converter  = new DayTimeSecondConverter(seconds);
            long second = converter.getSeconds();
            long minute = converter.getMin();
            long hours = converter.getHour();
            int day = converter.getDay();

            TextView time = (TextView) findViewById(R.id.tv_info_time);
            TextView remainingDistance = (TextView) findViewById(R.id.tv_info_distance);

            StringBuilder remainingTime = new StringBuilder();

            if (day != 0) remainingTime.append(day).append("day ");

            if (hours != 0) remainingTime.append(hours).append("h ");

            if(minute != 0) remainingTime.append(minute).append("min ");

            remainingTime.append(second).append(" s");


            if (time != null && remainingDistance !=null){
                time.setText(remainingTime.toString());
                remainingDistance.setText(totalDistance);
            }

        }
    }

    @Override
    public void updateFinalStopTimeAndDate(long seconds, int distanceMeters) {

        Log.d(TAG, "updateFinalStopTimeAndDate: called");

        if ( mCurrentBottomFragment instanceof BottomDistanceTimeFragment){

            String totalDistance;

            if (distanceMeters > 1000){
                Double distance = distanceMeters / 1000.0;
                totalDistance = distance.toString()+" km";
            }else {
                Integer distance = distanceMeters;
                totalDistance = distance.toString()+ " m";
            }

            DayTimeSecondConverter converter  = new DayTimeSecondConverter(seconds);
            long second = converter.getSeconds();
            long minute = converter.getMin();
            long hours = converter.getHour();
            int day = converter.getDay();

            TextView time = (TextView) findViewById(R.id.tv_final_time);
            TextView remainingDistance = (TextView) findViewById(R.id.tv_final_distance);

            StringBuilder remainingTime = new StringBuilder();

            if (day != 0) remainingTime.append(day).append("day ");

            if (hours != 0) remainingTime.append(hours).append("h ");

            if(minute != 0) remainingTime.append(minute).append("min ");

            remainingTime.append(second).append("s");


            if (time != null && remainingDistance !=null){
                time.setText(remainingTime.toString());
                remainingDistance.setText(totalDistance);
            }
        }
    }



    @Override
    public void loadStartFragment(Integer distance, int seconds) {

        String totalDistance;

        if (distance >= 1000){

            Double valueInkillo = distance / 1000.0;

            totalDistance = valueInkillo.toString()+ " km";
        }else {

            Integer valueInMeters = distance;
            totalDistance = valueInMeters.toString()+ " m";
        }

        DayTimeSecondConverter converter = new DayTimeSecondConverter(seconds);

        int day = converter.getDay();
        long hours = converter.getHour();
        long minute = converter.getMin();
        long second = converter.getSeconds();

        StringBuilder remainingTime = new StringBuilder();

        if (day != 0) remainingTime.append(day).append("day ");

        if (hours != 0) remainingTime.append(hours).append("h");

        if(minute != 0) remainingTime.append(minute).append("min");

        remainingTime.append(second).append("s");


        if (mCurrentBottomFragment instanceof StartFragment) {

            ViewSwitcher viewSwitcher = (ViewSwitcher) findViewById(R.id.vs_start);
            TextView distanceTextView = (TextView) findViewById(R.id.tv_distance);
            TextView arrivalTime = (TextView) findViewById(R.id.tv_arrival_time);
            Button start = (Button) findViewById(R.id.btn_start);

            ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.ct_layout_progressBar);

            if (viewSwitcher.getCurrentView() == constraintLayout) {
                viewSwitcher.showNext();
                start.setVisibility(View.VISIBLE);
                distanceTextView.setText(totalDistance);
                arrivalTime.setText(remainingTime.toString());
            }
        }
    }

    @Override
    public void showNoRouteDialogue(String message) {
        Snackbar.make(
                findViewById(android.R.id.content),
                message,
                Snackbar.LENGTH_LONG)
                .show();
    }


    public String getDestinationName() {
        return destinationName;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mGeofencingClient.removeGeofences(getGeofencePendingIntent())
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Geofences removed
                        // ...
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to remove geofences
                        // ...
                    }
                });
    }
}