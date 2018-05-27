package com.oumardiallo636.gtuc.troskymate.Map;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oumardiallo636.gtuc.troskymate.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class NextStopInfo extends Fragment
        implements ViewHelper.NextStopInfoCallback{

    public NextStopInfo() {
        // Required empty public constructor
    }

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_next_stop_info, container, false);

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
