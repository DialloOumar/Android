package com.oumardiallo636.gtuc.troskymate.Map;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oumardiallo636.gtuc.troskymate.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class BottomDistanceTimeFragment extends Fragment
        implements ViewHelper.BottomDistanceTimeCallback {

    public BottomDistanceTimeFragment() {
        // Required empty public constructor
    }

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_bottom_distance_time, container, false);

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

    @OnClick(R.id.tv_dis_time_cancel)
    public void cancle(){

        MainActivity.getInstance().clearMap();
    }

}
