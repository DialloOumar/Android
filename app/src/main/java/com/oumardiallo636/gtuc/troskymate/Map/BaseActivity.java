package com.oumardiallo636.gtuc.troskymate.Map;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.oumardiallo636.gtuc.troskymate.R;

/**
 * Created by oumar on 1/1/18.
 */

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";

    /**
     * Code used in requesting runtime permissions.
     */
    protected static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    protected static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 12;
    /**
     * Constant used in the location settings dialog.
     */
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    protected static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    protected static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    // Keys for storing activity state in the Bundle.
    protected final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    protected final static String KEY_LOCATION = "location";
    protected final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";

    private Toolbar toolbar;

    public void activateToolbar(boolean enableHome){
        Log.d(TAG, "activateToolbar: starts");

        ActionBar actionBar = getSupportActionBar();
        if(actionBar == null){
             toolbar = (Toolbar) findViewById(R.id.toolbar);

            if (toolbar != null){
                setSupportActionBar(toolbar);;
                actionBar = getSupportActionBar();
            }
        }

        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(enableHome);
        }

        Log.d(TAG, "activateToolbar: ends");
    }

    public void showTitle(boolean value){

    }

    Toolbar getToolbar(){
        return toolbar;
    }
}
