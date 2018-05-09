package com.oumardiallo636.gtuc.troskymate.Map;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oumardiallo636.gtuc.troskymate.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class StartFragment extends Fragment {

    @BindView(R.id.tv_arrival_time)
    TextView arrivalTime;

    @BindView(R.id.tv_distance)
    TextView estimateDistance;

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
                MainActivity.getInstance().changeMapPadding(height);
            }
        });

        return view;

    }

    @OnClick(R.id.tv_start_cancel)
    public void cancelClick() {

        MainActivity.getInstance().clearMap();

    }

}
