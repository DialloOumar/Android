package com.oumardiallo636.gtuc.troskymate.Map;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.oumardiallo636.gtuc.troskymate.BuildConfig;
import com.oumardiallo636.gtuc.troskymate.Entities.CloseBusStop.CloseStops;
import com.oumardiallo636.gtuc.troskymate.Entities.Direction.Leg;
import com.oumardiallo636.gtuc.troskymate.Entities.Direction.Route;
import com.oumardiallo636.gtuc.troskymate.Entities.Direction.Step;
import com.oumardiallo636.gtuc.troskymate.Entities.Direction.Stop;
import com.oumardiallo636.gtuc.troskymate.R;
import com.oumardiallo636.gtuc.troskymate.Utility.MyStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oumar on 12/27/17.
 */

public class Presenter extends BaseActivity implements
        MapActivityMVP.Presenter {

    private static final String TAG = "Presenter";

    private AppCompatActivity mActivity;
    private MapActivityMVP.View mView;
    private MapModel mModel;

    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Provides access to the Location Settings API.
     */
    private SettingsClient mSettingsClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    private LocationRequest mLocationRequest;

    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    private LocationSettingsRequest mLocationSettingsRequest;

    /**
     * Callback for Location events.
     */
    private LocationCallback mLocationCallback;
    /**
     * Represents a geographical location.
     */
    private Location mCurrentLocation;
    private LatLng mDestination;

    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    private Boolean mRequestingLocationUpdates;

    /**
     * Time when the location was updated represented as a String.
     */
    private String mLastUpdateTime;


    public Presenter() {
        mView = MainActivity.getInstance();
        mActivity = (AppCompatActivity) mView;
        mRequestingLocationUpdates = false;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mActivity);
        mSettingsClient = LocationServices.getSettingsClient(mActivity);
        mModel = new MapModel(this);

    }

    public Location getCurrentLocation() {
        return mCurrentLocation;
    }

    public String getStringDestination(){
        StringBuilder destination = new StringBuilder();

        destination.append(mDestination.latitude)
                .append(",")
                .append(mDestination.longitude);

        return  destination.toString();
    }

    public LatLng getDestination(){
        return mDestination;
    }

    public Boolean getRequestingLocationUpdates() {
        return mRequestingLocationUpdates;
    }

    public String getLastUpdateTime() {
        return mLastUpdateTime;
    }

    @Override
    public void saveDestination(double lat, double lng) {
        mDestination = new LatLng(lat,lng);
    }

    /**
     * From the user's current location get 4 nearby bus stop that can serve as a starting point
     */
    @Override
    public void getClosestStops() {

        String origin = new StringBuilder().append(mCurrentLocation.getLatitude())
                .append(',')
                .append(mCurrentLocation.getLongitude())
                .toString();

            mModel.requestClosestBuses(origin);
    }

    @Override
    public void getDirection(String origin, String destination){
        mModel.requestDirectionApi(origin, destination);
    }


    @Override
    public void provideRoutes(List<List<Route>> routes, List<Stop> stops) {
        Log.d(TAG, "provideRoutes: starts");

//        List<List<String>> polylineList = new ArrayList<>();
        List<MarkerOptions> markers = new ArrayList<>();
        List<PolylineOptions> dotedPolylines = new ArrayList<>();

        for(List<Route> route : routes){
            List<String> routePolyline = new ArrayList<>();
            for (Route r : route ){
                for (Leg leg : r.getLegs()){
                    for(Step step: leg.getSteps()){
                        
                        routePolyline.add(step.getPolyline().getPoints());
                    }
                }
            }

            mView.drawPolyline(routePolyline);
        }

        Stop destinationStop = new Stop();

        destinationStop.setBusName("End");
        destinationStop.setStopLocation(getStringDestination());
        destinationStop.setStopName(mView.getDestinationName());
        stops.add(destinationStop);

        for (int i = 0; i < stops.size(); i++){

            double latitude = Double.parseDouble(stops.get(i).getStopLocation().split(",")[0]);
            double logitude = Double.parseDouble(stops.get(i).getStopLocation().split(",")[1]);

            String busName = stops.get(i).getBusName();
            String stopName = stops.get(i).getStopName();
            Log.d(TAG, "provideRoutes: "+busName);

            if (busName.equals("WALKING")){
                Log.d(TAG, "provideRoutes: walking called");
                double nextLatitude = Double.parseDouble(stops.get(i+1).getStopLocation().split(",")[0]);
                double nextLogitude = Double.parseDouble(stops.get(i+1).getStopLocation().split(",")[1]);

                PolylineOptions polylineOptions = new PolylineOptions()
                        .add(new LatLng(latitude,logitude), new LatLng(nextLatitude, nextLogitude));

                dotedPolylines.add(polylineOptions);
            }

            MarkerOptions options = new MarkerOptions();
            options.title(stopName)
                    .position(new LatLng(latitude,logitude))
                    .snippet("Transport: "+stops.get(i).getBusName());
            markers.add(options);
        }

        mView.stopProgressBar();
        mView.drawMarkers(markers);
        mView.displayWakingPath(dotedPolylines);
//        mView.drawMarkers(markers);

        Log.d(TAG, "provideRoutes: ends");
    }


    /**
     * Callback received when a list of close bus stops have been returned.
     */
    @Override
    public void provideClosestStops(CloseStops stops) {
        Log.d(TAG, "provideClosestStops: " +"starts");
        mView.saveCloseStops(stops);
    }

    @Override
    public void notifyNoRouteFound(int status) {

        switch (status){
            case MyStatus.NO_ROUTE_FOUND:
                mView.showNoRouteDialogue("Sorry, the destination you entered is not yet supported");
                mView.clearMap();
                break;
            case MyStatus.SERVER_ERROR:
                mView.showNoRouteDialogue("Sorry, the destination you entered is not yet supported");
                mView.clearMap();
                break;
            case MyStatus.TIME_OUT:
                mView.showNoRouteDialogue("Internet connection time out try again!");
        }

    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for this mapping application that shows real-time location
     * updates.
     */
    @Override
    public void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Creates a callback for receiving location events.
     */
    @Override
    public void createLocationCallback() {
        Log.d(TAG, "createLocationCallback: starts");

        //Log.d(TAG, "createLocationCallback: mActivity "+mActivity.getApplication() );
        if (ActivityCompat.checkSelfPermission((MainActivity) mView, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            requestPermissions();
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener( mActivity, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            mCurrentLocation = location;
                        }
                    }
                });

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.d(TAG, "onLocationResult: starts");
                super.onLocationResult(locationResult);

                if (locationResult.getLastLocation() != null) {
                    mCurrentLocation = locationResult.getLastLocation();
                }
                mView.updateLocationUI(mCurrentLocation);
                Log.d(TAG, "onLocationResult: "+mCurrentLocation);

                Log.d(TAG, "onLocationResult: ends");
            }
        };

        Log.d(TAG, "createLocationCallback: ends");
    }

    /**
     * Uses a {@link com.google.android.gms.location.LocationSettingsRequest.Builder} to build
     * a {@link com.google.android.gms.location.LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */
    @Override
    public void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    /**
     * creating an intent to receive places autocompletion from google places api
     * This function also call the getClosestStops for getting the nearby stops as soon as the user start looking
     * for a destination, and save those stop before the destination result arrives
     */
    @Override
    public void findDestination() {

        Log.d(TAG, "findDestination: starts");


        try {
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setCountry("GH")
                    .build();
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .setBoundsBias(new LatLngBounds(
                                    new LatLng(5.532431, -0.293029),
                                    new LatLng(5.638014, -0.104202)))
                            .setFilter(typeFilter)
                            .build(mActivity);
            mActivity.startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }

        Log.d(TAG, "findDestination: ends");
    }

    @Override
    public void startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        //noinspection MissingPermission
                        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        mView.updateLocationUI(mCurrentLocation);

                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(mActivity, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                                Toast.makeText(mActivity, errorMessage, Toast.LENGTH_LONG).show();
                                mRequestingLocationUpdates = false;
                        }
                        mView.updateLocationUI(mCurrentLocation);
                    }
                });
    }
    /**
     * Removes location updates from the FusedLocationApi.
     */
    @Override
    public void stopLocationUpdates() {
        Log.d(TAG, "stopLocationUpdates: starts");
        if (!mRequestingLocationUpdates) {
            Log.d(TAG, "stopLocationUpdates: updates never requested, no-op.");
            return;
        }
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "onComplete: stoping");
                        mRequestingLocationUpdates = false;
                    }
                });
        Log.d(TAG, "stopLocationUpdates: ends");
    }

    @Override
    public boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(mActivity,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            mView.showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(mActivity,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(mActivity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mRequestingLocationUpdates) {
                    Log.i(TAG, "Permission granted, updates requested, starting location updates");
                    startLocationUpdates();
                }
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                mView.showSnackbar(R.string.permission_denied_explanation,
                        R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }

    /**
     * Updates fields based on data stored in the bundle.
     *
     * @param savedInstanceState The activity state saved in the Bundle.
     */
    @Override
    public void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(KEY_REQUESTING_LOCATION_UPDATES)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        KEY_REQUESTING_LOCATION_UPDATES);
            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(KEY_LOCATION)) {
                // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(KEY_LAST_UPDATED_TIME_STRING)) {
                mLastUpdateTime = savedInstanceState.getString(KEY_LAST_UPDATED_TIME_STRING);
            }
//            updateUI();
            startLocationUpdates();
        }
    }

}
