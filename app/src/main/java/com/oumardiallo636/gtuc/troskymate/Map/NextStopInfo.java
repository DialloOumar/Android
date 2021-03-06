package com.oumardiallo636.gtuc.troskymate.Map;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oumardiallo636.gtuc.troskymate.R;

import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class NextStopInfo extends Fragment
        implements ViewHelper.NextStopInfoCallback{

    public NextStopInfo() {
        // Required empty public constructor
    }
//
//    @BindView(R.id.tv_then)
//    TextView then;
//
//    @OnClick(R.id.tv_then)
//    public void then() {
//        Toast.makeText(getContext(),"tab", Toast.LENGTH_LONG).show();
//        MainActivity.getInstance().changeNextStopInfo();
//        then.setVisibility(View.INVISIBLE);
//    }


    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_next_stop_info, container, false);

        ButterKnife.bind(this,view);

        String name = getArguments().getString("next_stop_name");

        TextView nextStopName = view.findViewById(R.id.tv_next_stop_name);
        TextView instruction = view.findViewById(R.id.tv_info);

        instruction.setText(getString(R.string.walk_to));
        nextStopName.setText(name);

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
