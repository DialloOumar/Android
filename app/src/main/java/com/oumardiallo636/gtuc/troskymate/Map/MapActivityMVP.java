package com.oumardiallo636.gtuc.troskymate.Map;

import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.oumardiallo636.gtuc.troskymate.Entities.CloseBusStop.CloseStops;
import com.oumardiallo636.gtuc.troskymate.Entities.Direction.Route;
import com.oumardiallo636.gtuc.troskymate.Entities.Direction.Stop;
import com.oumardiallo636.gtuc.troskymate.Entities.Direction.WalkingPoints;
import com.oumardiallo636.gtuc.troskymate.Entities.Matrix.MatrixInfo;

import java.util.List;

/**
 * Created by oumar on 12/26/17.
 */

public interface MapActivityMVP {

    interface View {

        void updateLocationUI(Location location);
        void searchPlace(android.view.View v);
        void showSnackbar(int textId, int action, android.view.View.OnClickListener listener);
        void moveCameraToLocation(LatLng location, int type);
        void drawMarkers(List<MarkerOptions> markerOptions);
        void switchBottomFragment(String toFragment, Bundle bundle);
        void switchHeaderFragment(String toFragment, Bundle bundle);
        void loadStartFragment(Integer distance, int time);
        void saveCloseStops(CloseStops stops);
        void clearMap();
        void showNoRouteDialogue(String message);
        void changeNextStopInfo(int type);

        //geofence callbacks
        void createGeofences(List<Stop> stops);
        void startGeofences();
        void removeAGeofence(String message);

        void changeMapBottomPadding(int padding);
        void searchDirection(String origin);
        String getDestinationName();
        void drawPolyline (List<String> route);

        void geofenceEnterResponse(String requestId);
//
//        void displayWakingPath(List<PolylineOptions> polylineOptions);
        void displayWakingPath(List<WalkingPoints> walkingPoints);

        void updateNextStopTimeAndDate(long second, int distance);
        void updateFinalStopTimeAndDate(long second, int distance);

    }


    interface Presenter{

        void createLocationRequest();
        void buildLocationSettingsRequest();
        void findDestination();
        void startLocationUpdates();
        void stopLocationUpdates();
        boolean checkPermissions();
        void requestPermissions();
        void createLocationCallback();
        void updateValuesFromBundle(Bundle savedInstanceState);
        Boolean getRequestingLocationUpdates();
        Location getCurrentLocation();
        String getLastUpdateTime();
        void saveDestination(double lat, double lng);
        void getClosestStops();
        void provideRoutes(List<List<Route>> routes, List<Stop> stops);
        void getDirection(String origin, String destination);
        void provideClosestStops(CloseStops stops);
        void notifyNoRouteFound(int status);
        void getDistanceAndTime(List<String> origins, List<String> destinations,String mode);
        void provideDistanceAndTime(MatrixInfo nextStop, MatrixInfo lastStop);

    }

    interface Model {

        void requestClosestBuses(String origin);
        void requestDirectionApi(String origin, String destination);
        void requestDistanceAndTime(String origin, String destination, String mode);

    }
}
