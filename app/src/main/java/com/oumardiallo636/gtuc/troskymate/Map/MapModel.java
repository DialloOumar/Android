package com.oumardiallo636.gtuc.troskymate.Map;

import android.util.Log;

import com.oumardiallo636.gtuc.troskymate.Entities.CloseBusStop.CloseStops;
import com.oumardiallo636.gtuc.troskymate.Entities.Direction.TroskyDirection;
import com.oumardiallo636.gtuc.troskymate.Entities.Matrix.Element;
import com.oumardiallo636.gtuc.troskymate.Entities.Matrix.MatrixInfo;
import com.oumardiallo636.gtuc.troskymate.Entities.Matrix.Row;
import com.oumardiallo636.gtuc.troskymate.Entities.Matrix.TimeDistanceMatrix;
import com.oumardiallo636.gtuc.troskymate.Utility.HttpCaller;
import com.oumardiallo636.gtuc.troskymate.Utility.MyStatus;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by oumar on 1/11/18.
 */

public class MapModel implements MapActivityMVP.Model {

    private static final String TAG = "MapModel";

    /**
     *Injecting the presenter to the model for making callbacks
     */
    private MapActivityMVP.Presenter presenter;

    /**
     * The number of buses that the user will take for the entire journey
     * */
    private int numberOfBuses;

    public MapModel(MapActivityMVP.Presenter presenter) {
        this.presenter = presenter;
    }


    /**
     * Method interact with the TroskyApi. This api provide a
     * list of the 4 closest bus stops
     * the method passes the returned data to the presenter
     *
     * @param origin The current position of the user.
     */
    @Override
    public void requestClosestBuses(String origin) {
        Log.d(TAG, "requestClosestBuses: starts");
        String baseUrl =ApplicationRepo.TroskyRepo.baseUrl;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(HttpCaller.getInstance())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApplicationRepo.TroskyRepo troskyRepo = retrofit.create(ApplicationRepo.TroskyRepo.class);

        Call<CloseStops> call = troskyRepo.getCloseStops(origin);

        call.enqueue(new Callback<CloseStops>() {
            @Override
            public void onResponse(Call<CloseStops> call, Response<CloseStops> response) {
                Log.d(TAG, "onResponse: starts");
                CloseStops closeStops = response.body();


                if (closeStops != null){

                    if (closeStops.getStatus() == 202){
                        presenter.provideClosestStops(closeStops);
                    }else if (closeStops.getStatus() == MyStatus.NO_ROUTE_FOUND){
                        Log.d(TAG, "onResponse: in"+closeStops.getMessage());

                    }
                }else {
                    Log.d(TAG, "onResponse: closeStops == null");

                    presenter.notifyNoRouteFound(405);
                }

            }

            @Override
            public void onFailure(Call<CloseStops> call, Throwable t) {
                Log.d(TAG, "onResponse: unavailable");
                presenter.notifyNoRouteFound(404);
            }
        });

        Log.d(TAG, "requestClosestBuses: ends");
    }

    /**
     * Method interact with the Google Direction  api. This api provide a
     * step by step direction to a particular destination
     * the method returns a callback to the presenter with the routes gotten
     * from google api and also the differents busstop returned by the troskyApi
     *
     * @param origin The current position of the user.
     * @param destination where to user is heading
     */
    @Override
    public void requestDirectionApi(String origin, String destination) {

        Log.d(TAG, "requestDirectionApi: starts");
        String baseUrl = ApplicationRepo.TroskyRepo.baseUrl;
        String apiKey = ApplicationRepo.TroskyRepo.apiKey;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(HttpCaller.getInstance())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApplicationRepo.TroskyRepo troskyRepo = retrofit.create(ApplicationRepo.TroskyRepo.class);

        Call<TroskyDirection> call = troskyRepo.getStops(origin,destination,apiKey);

        call.enqueue(new Callback<TroskyDirection>() {
            @Override
            public void onResponse(Call<TroskyDirection> call, Response<TroskyDirection> response) {

                Log.d(TAG, "onResponse: url == "+ call.request().url());
                Log.d(TAG, "onResponse: "+response.message());

                if (response.isSuccessful()){
                    TroskyDirection troskyDirection = response.body();
                    Log.d(TAG, "onResponse: message "+ troskyDirection.getMessage());

                    if (troskyDirection.getStatus() == MyStatus.OK){
                        presenter.provideRoutes(troskyDirection.getRoutes(),
                                troskyDirection.getStops());
                    }else if (troskyDirection.getStatus() == MyStatus.NO_ROUTE_FOUND){
                        presenter.notifyNoRouteFound(404);
                    }
                } else {
                    Log.d(TAG, "onResponse: "+ response.message());
                    presenter.notifyNoRouteFound(505);
                    Log.d(TAG, "onResponse: unavailable");
                }

            }

            @Override
            public void onFailure(Call<TroskyDirection> call, Throwable t) {
                Log.d(TAG, "onFailure: "+call.request());
                Log.d(TAG, "onFailure: "+t.getMessage());

                presenter.notifyNoRouteFound(405);
            }
        });

    }

    @Override
    public void requestDistanceAndTime(String origin, String destination, String mode) {

        Log.d(TAG, "requestDistanceAndTime: starts");

        String baseUrl = ApplicationRepo.GoogleRepo.baseUrl;
        String apiKey = ApplicationRepo.GoogleRepo.apiKey;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(HttpCaller.getInstance())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApplicationRepo.GoogleRepo googleRepo = retrofit.create(ApplicationRepo.GoogleRepo.class);

        Call<TimeDistanceMatrix> call = googleRepo.getDistanceAndTime(origin,destination,mode,apiKey);

        call.enqueue(new Callback<TimeDistanceMatrix>() {
            @Override
            public void onResponse(Call<TimeDistanceMatrix> call, Response<TimeDistanceMatrix> response) {

                Log.d(TAG, "onResponse: starts");

                TimeDistanceMatrix timeDistanceMatrix = response.body();

                Log.d(TAG, "onResponsetime: "+timeDistanceMatrix.getStatus());
                Log.d(TAG, "onResponsetime"+call.request().url());

                if (timeDistanceMatrix.getStatus().equalsIgnoreCase("ok")){

                    Log.d(TAG, "onResponsetime: "+timeDistanceMatrix.getRows().size());
                    Row row = timeDistanceMatrix.getRows().get(0);

                    List<Element> elements = row.getElements();

                    Integer nextStopDuration = elements.get(0).getDuration().getValue();
                    Integer nextStopDistance = elements.get(0).getDistance().getValue();

                    Integer lastStopDuration = elements.get(1).getDuration().getValue();
                    Integer lastStopDistance = elements.get(1).getDistance().getValue();

                    MatrixInfo nextStop = new MatrixInfo(nextStopDuration, nextStopDistance);
                    MatrixInfo lastStop = new MatrixInfo(lastStopDuration, lastStopDistance);

                    presenter.provideDistanceAndTime(nextStop, lastStop);

                    Log.d(TAG, "onResponse: ends");
                }
            }

            @Override
            public void onFailure(Call<TimeDistanceMatrix> call, Throwable t) {

            }
        });

        Log.d(TAG, "requestDistanceAndTime: ends");
    }
}
