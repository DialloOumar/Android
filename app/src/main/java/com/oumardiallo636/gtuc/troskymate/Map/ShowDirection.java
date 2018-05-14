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
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowDirection extends Fragment {

//    @BindView(R.id.btn_show_direction)
//    Button btnShowDirection;
//    @BindView(R.id.tv_distance)
//    TextView tvDistance;
//    @BindView(R.id.tv_buses)
//    TextView tvBuses;
//    @BindView(R.id.tv_arrival_time)
//    TextView tvArrivalTime;
//    @BindView(R.id.tv_street_view)

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

    @BindView(R.id.tv_street_view)
    TextView tvStreetView;

    public ShowDirection() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View view = inflater.inflate(R.layout.direction_layout, container, false);

        ButterKnife.bind(this, view);

        stop_names.addAll(getArguments().getStringArrayList("stop_names"));
        stop_locations.addAll(getArguments().getStringArrayList("stop_location"));

        stop1.setText(stop_names.get(0));
        stop2.setText(stop_names.get(1));
        stop3.setText(stop_names.get(2));
        stop4.setText(stop_names.get(3));


        view.post(new Runnable() {
            @Override
            public void run() {
                int height = view.getMeasuredHeight(); // for instance
                MainActivity.getInstance().changeMapPadding(height);
            }
        });

        MainActivity.getInstance().stopProgressBar();
        return view;
    }


    @OnClick(R.id.btn_show_direction)
    public void submit(View view) {
        // TODO submit data to server...

        int radioCheckedId = radioGroup.getCheckedRadioButtonId();

        String origin = "";

        switch (radioCheckedId) {
            case R.id.stop_one:
                origin = stop_locations.get(0);
                //Toast.makeText(getContext(),"location 1 "+stop_locations.get(0),Toast.LENGTH_SHORT).show();
                break;
            case R.id.stop_two:
                origin = stop_locations.get(1);
               // Toast.makeText(getContext(),"location 2 "+stop_locations.get(1),Toast.LENGTH_SHORT).show();
                break;
            case R.id.stop_three:
                origin = stop_locations.get(2);
               // Toast.makeText(getContext(),"location 3 "+stop_locations.get(2),Toast.LENGTH_SHORT).show();
                break;
            case R.id.stop_four:
                origin = stop_locations.get(3);
                //Toast.makeText(getContext(),"location 4 "+stop_locations.get(3),Toast.LENGTH_SHORT).show();
                break;
        }

        MainActivity.getInstance().searchDirection(origin);
        MainActivity.getInstance().startProgressBar("Loading direction");
    }

    @OnClick(R.id.tv_cancel_route)
    public void cancelClick() {
        MainActivity.getInstance().clearMap();
    }

}
