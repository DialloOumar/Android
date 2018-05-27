package com.oumardiallo636.gtuc.troskymate.Map;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oumardiallo636.gtuc.troskymate.R;

import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */



public class CustomToolBar extends Fragment
        implements ViewHelper.CustomToolBarCallback{

//    @BindView(R.id.tv_search)
//    TextView tvSearch;

    public CustomToolBar() {
        // Required empty public constructor
    }
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_custom_tool_bar, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

}
