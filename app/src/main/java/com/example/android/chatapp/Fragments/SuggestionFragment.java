package com.example.android.chatapp.Fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.android.chatapp.Adapter.SuggestionTitleAdapter;
import com.example.android.chatapp.Main2Activity;
import com.example.android.chatapp.Model.Title;
import com.example.android.chatapp.R;
import com.example.android.chatapp.SuggestionActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SuggestionFragment extends Fragment implements SuggestionTitleAdapter.SuggestionTitleListRecyclerClickListener,
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private RatingBar ratingBar;
    MaterialEditText titleinf, description;
    private List<Title> mTitle;
    private RecyclerView recyclerView;
    Context context;
    private SuggestionTitleAdapter suggestionTitleAdapter;
    private int clicks;
    private List<String> idList;
    TextView date, time, location;
    Geocoder geocoder;
    List<Address> addresses;


    FirebaseUser fuser;
    Button editSuggestionSave;
    String coord1, coord2, skill;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_suggestion, container, false);

        if ((getArguments() != null))
        {
            coord1 = getArguments().getString("coord1");
            coord2 = getArguments().getString("coord2");}
        clicks = 0;
        editSuggestionSave = view.findViewById(R.id.edit_suggestion_save);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        titleinf = view.findViewById(R.id.title_info);
        description = view.findViewById(R.id.description_info);
        time = view.findViewById(R.id.time);
        date = view.findViewById(R.id.date);
        location = view.findViewById(R.id.location_inf);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        context = getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mTitle = new ArrayList<>();
        idList = new ArrayList<>();
        geocoder = new Geocoder(getContext(), Locale.getDefault());
        if ((getArguments() != null))
        {
            coord1 = getArguments().getString("coord1");
            coord2 = getArguments().getString("coord2");
            Double lat = Double.parseDouble(coord1);
            Double longi = Double.parseDouble(coord2);
            try {
                addresses = geocoder.getFromLocation(lat, longi, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            } catch (IOException e) {
                e.printStackTrace();
            }

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            location.setText(address);
        }

        readTitles();

        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat dd = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dd.format(c);

        SimpleDateFormat dh = new SimpleDateFormat("HH:mm");
        String formattedTime = dh.format(c);

        time.setText(formattedTime);
        date.setText(formattedDate);

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
        /*calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                calendarView.setVisibility(View.GONE);
                String yearInString = String.valueOf(year);
                String monthInString = String.valueOf(month+1);
                String dayInString = String.valueOf(dayOfMonth);
                date.setText(yearInString+"-"+monthInString+"-"+dayInString);
            }
        }); */

        ratingBar = view.findViewById(R.id.rating_bar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                skill = (String.valueOf(rating));
            }
        });


        editSuggestionSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    updateSuggestion(titleinf.getText().toString(), description.getText().toString(), coord1, coord2, skill,
                            date.getText().toString(), time.getText().toString());
                    Intent intent2 = new Intent(context, Main2Activity.class);
                    intent2.putExtra("fr", "1");
                    intent2.putExtra("cord1", coord1);
                    intent2.putExtra("cord2", coord2);
                    startActivity(intent2);
            }
        });

        getFriends();

        titleinf.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                searchTitle(charSequence.toString().toLowerCase());
                recyclerView.bringToFront();
                clicks = 0;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return view;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        //do some stuff for example write on log and update TextField on activity
        String yearInString = String.valueOf(year);
        String monthInString = String.valueOf(month+1);
        String dayInString = String.valueOf(day);
        date.setText(yearInString+"-"+monthInString+"-"+dayInString);

    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        String hourInString = String.valueOf(hourOfDay);
        String minuteInString = String.valueOf(minute);
        time.setText(hourInString+":"+minuteInString);
    }

    private void updateSuggestion(String title, String description, String coord1, String coord2, String skill, String date, String time){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Suggestion");

        String sid = reference.push().getKey();

        HashMap<String, Object> map = new HashMap<>();
        map.put("sid", sid);
        map.put("spublisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
        map.put("title", title);
        map.put("date", date);
        map.put("time", time);
        map.put("search", title.toLowerCase());
        map.put("description", description);
        map.put("coord1", coord1);
        map.put("coord2", coord2);
        map.put("skill", skill);

        reference.child(sid).setValue(map);

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

    public void onSuggestionTitleClicked(int position) {
        final String selectedTitle = mTitle.get(position).getTitle();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Title");

        clicks = 1;

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Title title = snapshot.getValue(Title.class);

                    assert title != null;

                    if (title.getTitle().equals(selectedTitle)) {
                        titleinf.setText(selectedTitle);
                        mTitle.clear();

                    }
                }
                initAdapter(context, mTitle);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void searchTitle(String s) {

        if (clicks==0){
            Query query = FirebaseDatabase.getInstance().getReference("Title").orderByChild("title")
                    .startAt(s)
                    .endAt(s+"\uf8ff");

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mTitle.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Title title = snapshot.getValue(Title.class);

                        mTitle.add(title);

                        assert title != null;
                        if (titleinf.getText().toString().equals("")){
                            mTitle.clear();
                        }
                    }

                    initAdapter(context, mTitle);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }}

    private void initAdapter(Context context, List<Title> mTitle){
        suggestionTitleAdapter = new SuggestionTitleAdapter(context, mTitle, this);
        recyclerView.setAdapter(suggestionTitleAdapter);
    }

    private void readTitles() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Title");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (titleinf.getText().toString().equals("")) {
                    mTitle.clear();

                }
                initAdapter(context, mTitle);
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
}
