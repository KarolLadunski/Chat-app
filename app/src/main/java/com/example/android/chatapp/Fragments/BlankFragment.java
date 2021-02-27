package com.example.android.chatapp.Fragments;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.example.android.chatapp.Adapter.SuggestionHomeAdapter;
import com.example.android.chatapp.ChatsActivity;
import com.example.android.chatapp.EventsActivity;
import com.example.android.chatapp.Model.Suggestion;
import com.example.android.chatapp.Model.User;
import com.example.android.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class BlankFragment extends Fragment implements SuggestionHomeAdapter.SuggestionListRecyclerClickListener{

    private RecyclerView recyclerView;

    private SuggestionHomeAdapter suggestionHomeAdapter;
    private List<Suggestion> suggestionList;
    private int clicks;

    private Context mContext;
    private FirebaseUser fuser;
    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;
    int distanceFilter;
    String when;
    int a = 0;
    private int fo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blank, container, false);

        when = "In a month";
        clicks = 0;
        fo = 1;
        distanceFilter = 10;
        recyclerView = view.findViewById(R.id.home_recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        suggestionList = new ArrayList<>();
        recyclerView.setAdapter(suggestionHomeAdapter);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        mContext = getContext();
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        if ((getArguments() != null))
        { String distanceFilterString = getArguments().getString("distanceFilter");
            assert distanceFilterString != null;
            distanceFilter = Integer.parseInt(distanceFilterString);
            when = getArguments().getString("when");}

        Date c = Calendar.getInstance().getTime();

        long timeInMilliseconds = c.getTime();
        String formattedDate = Long.toString(timeInMilliseconds);


        HashMap<String, Object> map = new HashMap<>();
        map.put("timeSeen", formattedDate);

        reference.updateChildren(map);

        if (a==0){
            getMyLocation();
            a++;
        }




        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

   private void getMyLocation(){
        final Location myLocation = new Location("point B");


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user1 = snapshot.getValue(User.class);
                    assert user1 != null;

                    if (user1.getId().equals(fuser.getUid())){
                        Double Lat1 = user1.getLat();
                        Double Lng1 = user1.getLng();
                        myLocation.setLatitude(Lat1);
                        myLocation.setLongitude(Lng1);
                        if (myLocation!=null){
                        readSuggestions(myLocation);}
                    return;}
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    private void readSuggestions(final Location myLocation){

        final Location sLocation = new Location("point A");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Suggestion");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                suggestionList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Suggestion suggestion = snapshot.getValue(Suggestion.class);

                    assert suggestion != null;
                    String Lat2 = suggestion.getCoord1();
                    String Lng2 = suggestion.getCoord2();
                    Double Lat1 = Double.parseDouble(Lat2);
                    Double Lng1 = Double.parseDouble(Lng2);
                    sLocation.setLatitude(Lat1);
                    sLocation.setLongitude(Lng1);

                    SimpleDateFormat format1=new SimpleDateFormat("yyyy-MM-dd");
                    Date dt1= null;
                    try {
                        dt1 = format1.parse(suggestion.getDate());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Date c = Calendar.getInstance().getTime();
                    String todayDate = format1.format(c);
                    Date dt = null;
                    try {
                        dt = format1.parse(todayDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (dt != null && dt1 != null){
                    String dayOfSuggestion = (String) DateFormat.format("dd", dt1);
                    String dayToday = (String) DateFormat.format("dd", dt);

                    int dayOfSuggestionInt = Integer.parseInt(dayOfSuggestion);
                    int dayTodayInt = Integer.parseInt(dayToday);

                    int days = 31;

                    if (when.equals("Today")){
                        days = 0;
                    }

                    if (when.equals("In a week")){
                        days = 7;
                    }


                    float distanceToSuggestion = myLocation.distanceTo(sLocation)/1000;
                        if (distanceToSuggestion <= distanceFilter){
                            if (dayOfSuggestionInt - dayTodayInt < days)
                            { suggestionList.add(suggestion);}
                        }

                }}
                initAdapter(getContext(), suggestionList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        menu.findItem(R.id.logout).setVisible(false);
        menu.findItem(R.id.event_icon).setVisible(false);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();//(SearchView) searchItem.getActionView();

        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    Log.i("onQueryTextChange", newText);
                    searchSuggestion(newText.toLowerCase());
                    clicks = 0;

                    return true;
                }
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Log.i("onQueryTextSubmit", query);

                    return true;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.chats_icon)
        {

            Intent intent = new Intent(getActivity(), ChatsActivity.class);
            startActivity(intent);
            return true;
        }

        if(id == R.id.action_search)
        {
            return false;
        }

        if(id == R.id.filter_icon)
        {
            ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new FilterFragment()).commit();
        }

        searchView.setOnQueryTextListener(queryTextListener);
        return super.onOptionsItemSelected(item);
    }

    private void initAdapter(Context context, List<Suggestion> mSuggestion){
        suggestionHomeAdapter = new SuggestionHomeAdapter(context, mSuggestion, this);
        recyclerView.setAdapter(suggestionHomeAdapter);
    }

    private void searchSuggestion(String s) {

        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("Suggestion").orderByChild("search")
                .startAt(s)
                .endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                suggestionList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Suggestion suggestion = snapshot.getValue(Suggestion.class);

                    assert suggestion != null;
                    assert fuser != null;

                    if (!suggestion.getSpublisher().equals(fuser.getUid())) {
                        suggestionList.add(suggestion);}
                }
                initAdapter(getContext(), suggestionList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onSuggestionClicked(int position){
        final String selectedSuggestion = suggestionList.get(position).getSid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Suggestion");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Suggestion suggestion = snapshot.getValue(Suggestion.class);
                    MapFragment fragment = null;
                    assert suggestion != null;
                    if (suggestion.getSid().equals(selectedSuggestion) && fo == 1){
                        String cordinate1 = suggestion.getCoord1();
                        String cordinate2 = suggestion.getCoord2();
                        String distance = Integer.toString(distanceFilter);
                        fragment = new MapFragment();
                        Bundle args = new Bundle();
                        args.putString("CameraCord1", cordinate1);
                        args.putString("CameraCord2", cordinate2);
                        args.putString("when", when);
                        args.putString("distanceFilter", distance);
                        fragment.setArguments(args);
                        assert getFragmentManager() != null;
                        getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                        fo++;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
