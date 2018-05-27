package com.oumardiallo636.gtuc.troskymate.Map;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.oumardiallo636.gtuc.troskymate.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowDirection extends Fragment implements ViewHelper.ShowDirectionCallback{


    private List<String> stop_names = new ArrayList<>();
    private List<String> stop_locations = new ArrayList<>();

    @BindView(R.id.stop_one)
    TextView stop1;

    @BindView(R.id.stop_two)
    TextView stop2;

    @BindView(R.id.stop_three)
    TextView stop3;

    @BindView(R.id.stop_four)
    TextView stop4;

    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;
//
//    @BindView(R.id.tv_street_view)
//    TextView tvStreetView;

    public ShowDirection() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View view = inflater.inflate(R.layout.direction_layout, container, false);

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

}
