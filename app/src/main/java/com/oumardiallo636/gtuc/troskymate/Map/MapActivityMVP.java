package com.oumardiallo636.gtuc.troskymate.Map;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.oumardiallo636.gtuc.troskymate.Entities.CloseBusStop.CloseStops;
import com.oumardiallo636.gtuc.troskymate.Entities.Direction.Route;
import com.oumardiallo636.gtuc.troskymate.Entities.Direction.Stop;

import java.util.List;

/**
 * Created by oumar on 12/26/17.
 */

public interface MapActivityMVP {

    interface View {

        void updateLocationUI(Location location);
        void searchPlace(android.view.View v);
        void showSnackbar(int textId, int action, android.view.View.OnClickListener listener);
        void displayWakingPath(List<PolylineOptions> polylineOptions);
        void moveCameraToLocation(LatLng location);
        void drawMarkers(List<MarkerOptions> markerOptions);
        void switchFragment(Fragment fromFragment, String toFragment, Bundle bundle);
        void showCloseStops(CloseStops stops);
        void clearMap();
        void showErrorGettingCloseStops();
        void stopProgressBar();
        void startProgressBar(String message);
        void changeMapPadding(int padding);
        void searchDirection(String origin);
        String getDestinationName();
        void drawPolyline (List<String> route);


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
        void getClosestStops(Double destinationLat, Double destinationLng);
        void provideRoutes(List<List<Route>> routes, List<Stop> stops);
        void getDirection(String origin, String destination);
        void provideClosestStops(CloseStops stops);

    }

    interface Model {

        void requestClosestBuses(String origin, String destination);
        void requestDirectionApi(String origin, String destination);

    }
}
