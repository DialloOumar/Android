package com.oumardiallo636.gtuc.troskymate.search;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.oumardiallo636.gtuc.troskymate.Map.BaseActivity;

/**
 * Created by oumar on 2/8/18.
 */

public class Presenter extends BaseActivity implements SearchActivityMVP.Presenter{
    private static final String TAG = "Presenter";

    SearchActivityMVP.View mView;
    AppCompatActivity mActivity;
    public Presenter(SearchActivityMVP.View view){
        this.mView = view;
        mActivity = (AppCompatActivity) view;
    }

    @Override
    public void findPlace() {

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

//    @Override
//    public void displayRecentQueries() {
//
//    }
}
