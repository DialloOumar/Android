package com.oumardiallo636.gtuc.troskymate.Map;

import com.oumardiallo636.gtuc.troskymate.Entities.CloseBusStop.CloseStops;
import com.oumardiallo636.gtuc.troskymate.Entities.Direction.TroskyDirection;
import com.oumardiallo636.gtuc.troskymate.Entities.Matrix.TimeDistanceMatrix;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by oumar on 1/11/18.
 */

public interface ApplicationRepo {

    /**
     * This Repository connect to the google direction api
     */
    interface GoogleRepo{
        String baseUrl = "https://maps.googleapis.com/maps/api/";
        String apiKey ="AIzaSyAVDCXvs610OmEl1ximvN4lB3n-YKBdtmo";

        @GET("distancematrix/json")
        Call<TimeDistanceMatrix> getDistanceAndTime(@Query("origins") String origin,
                                                    @Query("destinations") String destination,
                                                    @Query("mode") String mode,
                                                    @Query("key") String key);
    }

    /**
     * This Repository connect our application to an API currently running on heruku
     * Given the start and destination addressed the API returns a list of trotro bus-stops that
     * are on the routes
     */
    interface TroskyRepo {

        String baseUrl = "https://trosky-api.herokuapp.com/";
        String apiKey ="AIzaSyAVDCXvs610OmEl1ximvN4lB3n-YKBdtmo";

        @GET("routes")
        Call<TroskyDirection> getStops(@Query("origin") String origin,
                                      @Query("destination") String destination,
                                       @Query("key") String apiKey);

        @GET("closeBusStops")
        Call<CloseStops> getCloseStops(@Query("origin") String origin);
    }

}
