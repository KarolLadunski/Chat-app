package com.example.android.chatapp.Fragments;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.android.chatapp.Main2Activity;
import com.example.android.chatapp.Model.Suggestion;
import com.example.android.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class EditSuggestionFragment extends Fragment {

    MaterialEditText description;
    TextView date, time, title, location;
    Geocoder geocoder;
    List<Address> addresses;
    Button save;
    String sid;
    String skill = null;
    private List<String> idList;
    Button changeLoc;
    String cord1;
    String cord2;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_suggestion, container, false);

        title = view.findViewById(R.id.title);
        location = view.findViewById(R.id.location_inf);
        description = view.findViewById(R.id.description_info);
        time = view.findViewById(R.id.time);
        geocoder = new Geocoder(getContext(), Locale.getDefault());
        date = view.findViewById(R.id.date);
        save = view.findViewById(R.id.edit_suggestion_save);
        changeLoc = view.findViewById(R.id.change_loc_btn);
        idList = new ArrayList<>();
        if ((getArguments() != null))
        {
            sid = getArguments().getString("sid");

            if (getArguments().getString("Cord1")!=null && getArguments().getString("Cord2")!=null){
                cord1 = getArguments().getString("Cord1");
                cord2 = getArguments().getString("Cord2");
                Double lat = Double.parseDouble(cord1);
                Double longi = Double.parseDouble(cord2);
                try {
                    addresses = geocoder.getFromLocation(lat, longi, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                location.setText(address);
            }
        }
        //RatingBar ratingBar = view.findViewById(R.id.rating_bar);
        getFriends();

        if (sid!=null){
        getSuggestionInfo(title, description, time, date, sid, location);}

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(view);
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);

            }
        });

       /* ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                skill = (String.valueOf(rating));
            }
        });  */

       changeLoc.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               ChangeLocFragment fragment = new ChangeLocFragment();
               Bundle args = new Bundle();
               if (cord1!=null && cord2!=null && sid!=null){
               args.putString("CameraCord1", cord1);
               args.putString("CameraCord2", cord2);
               args.putString("sid", sid);
               args.putString("type", "Edit");}
               fragment.setArguments(args);
               assert getFragmentManager() != null;
               getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
           }
       });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateSuggestion(title.getText().toString(), description.getText().toString(),
                        date.getText().toString(), time.getText().toString(), sid);
                Intent intent = new Intent(getContext(), Main2Activity.class);
                startActivity(intent);
                assert getActivity() != null;
                getActivity().finish();

            }
        });


        return view;
    }

    private void getSuggestionInfo(final TextView title, final EditText description, final TextView time,
                                   final TextView date, String sid, final TextView location) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Suggestion").child(sid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final Suggestion suggestion = dataSnapshot.getValue(Suggestion.class);
                assert suggestion != null;
                title.setText(suggestion.getTitle());
                description.setText(suggestion.getDescription());
                time.setText(suggestion.getTime());
                date.setText(suggestion.getDate());
                if (cord1==null && cord2==null){
                cord1 = suggestion.getCoord1();
                cord2 = suggestion.getCoord2();
                if (location.getText()!=null){
                    Double lat = Double.parseDouble(cord1);
                    Double longi = Double.parseDouble(cord2);
                    try {
                        addresses = geocoder.getFromLocation(lat, longi, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    location.setText(address); }
                }
                //String numStr = suggestion.getSkill();
                //int ns = Integer.parseInt(numStr);
                //ratingBar.setNumStars(ns);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        assert getFragmentManager() != null;
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        assert getFragmentManager() != null;
        newFragment.show(getFragmentManager(), "datePicker");
    }

    private void updateSuggestion(String title, String description, String date, String time, String sid){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Suggestion").child(sid);

        HashMap<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("date", date);
        map.put("time", time);
        map.put("coord1", cord1);
        map.put("coord2", cord2);
        map.put("search", title.toLowerCase());
        map.put("description", description);
        //map.put("skill", skill);

        reference.updateChildren(map);

        sendToFriends(sid, FirebaseAuth.getInstance().getCurrentUser().getUid());

    }

    private void sendToFriends(String sid, String publisher){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Interested");

        for (String id : idList){

            String iId = reference.push().getKey();

            HashMap<String, Object> map = new HashMap<>();
            map.put("sid", sid);
            map.put("iId", iId);
            map.put("u2", publisher);
            map.put("type", "notification");
            map.put("u1", id);
            reference.child(iId).setValue(map);}
    }

    private void getFriends() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("friends");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                idList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    idList.add(snapshot.getKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
