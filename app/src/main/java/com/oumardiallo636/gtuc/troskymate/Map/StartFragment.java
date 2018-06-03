package com.oumardiallo636.gtuc.troskymate.Map;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oumardiallo636.gtuc.troskymate.R;
import com.oumardiallo636.gtuc.troskymate.Utility.MyFragmentList;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class StartFragment extends Fragment
        implements ViewHelper.StartFragmentCallback{

//    @BindView(R.id.tv_arrival_time)
//    TextView arrivalTime;
//
//    @BindView(R.id.tv_distance)
//    TextView estimateDistance;



    public StartFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.start_direction_layout, container, false);
        ButterKnife.bind(this, view);

        view.post(new Runnable() {
            @Override
            public void run() {
                int height = view.getMeasuredHeight(); // for instance
                MainActivity.getInstance().changeMapBottomPadding(height);
            }
        });

        return view;

    }

    @OnClick(R.id.tv_start_cancel)
    public void cancelClick() {

        MainActivity.getInstance().clearMap();

    }

    @OnClick(R.id.btn_start)
    public void start(){
        MainActivity.getInstance().switchHeaderFragment(MyFragmentList.NEXT_STOP_INFO,null);
        MainActivity.getInstance().switchBottomFragment(MyFragmentList.BOTTOM_DISTANCE_TIME_FRAG,
                null);
        MainActivity.getInstance().startGeofences();
    }

}
