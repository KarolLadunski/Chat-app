package com.example.android.chatapp.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.chatapp.Adapter.SuggestionHomeAdapter;
import com.example.android.chatapp.Model.Suggestion;
import com.example.android.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class MyEventsFragment extends Fragment implements SuggestionHomeAdapter.SuggestionListRecyclerClickListener{

    private RecyclerView recyclerView;

    private SuggestionHomeAdapter suggestionHomeAdapter;
    private List<Suggestion> suggestionList;
    private FirebaseUser fuser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_events, container, false);
        recyclerView = view.findViewById(R.id.home_recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        suggestionList = new ArrayList<>();
        suggestionHomeAdapter = new SuggestionHomeAdapter(getContext(), suggestionList, this);
        recyclerView.setAdapter(suggestionHomeAdapter);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        getMyEvents();


        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    private void getMyEvents(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Suggestion");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                suggestionList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Suggestion suggestion = snapshot.getValue(Suggestion.class);
                    if (suggestion.getSpublisher().equals(fuser.getUid())){
                        suggestionList.add(suggestion);

                    }
                }
                suggestionHomeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onSuggestionClicked(int position){
        /*
        final String selectedSuggestion = suggestionList.get(position).getSid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Suggestion");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Suggestion suggestion = snapshot.getValue(Suggestion.class);
                    assert suggestion != null;
                    if (suggestion.getSid().equals(selectedSuggestion)){
                        String cordinate1 = suggestion.getCoord1();
                        String cordinate2 = suggestion.getCoord2();

                        MapFragment fragment = new MapFragment();
                        Bundle args = new Bundle();
                        args.putString("CameraCord1", cordinate1);
                        args.putString("CameraCord2", cordinate2);
                        args.putString("when", "In a month");
                        args.putString("distanceFilter", "10");
                        fragment.setArguments(args);
                        assert getFragmentManager() != null;
                        getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }); */
    }
}