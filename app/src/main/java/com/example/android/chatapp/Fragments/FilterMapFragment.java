package com.example.android.chatapp.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import com.example.android.chatapp.Manifest;
import com.example.android.chatapp.R;


public class FilterMapFragment extends Fragment {
    SeekBar distanceFilter;
    String distance;
    Button ok;
    Context mContext;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    String when = "In a month";
    CheckBox checkBox;
    String showMyMarkers;


    public FilterMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_filter_map, container, false);

        mContext = getContext();
        distance = "2";

        radioGroup = (RadioGroup) view.findViewById(R.id.radio_group);
        radioButton = (RadioButton) view.findViewById(0);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                radioButton = (RadioButton) view.findViewById(selectedId);
                when = radioButton.getText().toString();
            }
        });

        checkBox = view.findViewById(R.id.checkbox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (checkBox.isChecked()){
                    showMyMarkers = "show";
                }
                else {
                    showMyMarkers = "do not show";
                }
            }
        });
        ok = view.findViewById(R.id.button);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MapFragment fragment = new MapFragment();
                Bundle args = new Bundle();
                args.putString("distanceFilter", distance);
                args.putString("when", when);
                args.putString("showMyMarkers", showMyMarkers);
                fragment.setArguments(args);
                assert getFragmentManager() != null;
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            }
        });

        distanceFilter = view.findViewById(R.id.distance_filter);
        distanceFilter.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                distance = (String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return view;
    }
}