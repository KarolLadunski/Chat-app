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
import com.example.android.chatapp.EventsActivity;
import com.example.android.chatapp.Model.Interested;
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

public class EventsITakePartInFragment extends Fragment implements SuggestionHomeAdapter.SuggestionListRecyclerClickListener{

    private RecyclerView recyclerView;
    List<String> interestedList;

    private SuggestionHomeAdapter suggestionHomeAdapter;
    private List<Suggestion> suggestionList;
    private FirebaseUser fuser;
    int a;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_events_itake_part_in, container, false);
        recyclerView = view.findViewById(R.id.home_recycler);
        recyclerView.setHasFixedSize(true);
        a=0;
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        suggestionList = new ArrayList<>();
        interestedList = new ArrayList<>();
        suggestionHomeAdapter = new SuggestionHomeAdapter(getContext(), suggestionList, this);
        recyclerView.setAdapter(suggestionHomeAdapter);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        checkInterestedAndAccepted();


        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    private void checkInterestedAndAccepted() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Interested");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                interestedList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Interested interested = snapshot.getValue(Interested.class);
                    assert interested != null;
                    if (!interested.getType().equals("invitationToFriends")){
                    if (interested.getU1().equals(fuser.getUid()))
                    interestedList.add(interested.getSid());}
                }
                getSuggestions();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getSuggestions() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Suggestion");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                suggestionList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Suggestion suggestion = snapshot.getValue(Suggestion.class);
                    for (String id : interestedList){
                        assert suggestion != null;
                        if (suggestion.getSid().equals(id)){
                            suggestionList.add(suggestion);
                        }
                    }
                }
                suggestionHomeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onSuggestionClicked(int position){
        a=1;

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
                        Double cord1 = Double.parseDouble(cordinate1);
                        Double cord2 = Double.parseDouble(cordinate2);
                        ifLocIconPressed(a, cord1, cord2);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void ifLocIconPressed(int s, Double c1, Double c2){
        if ((EventsActivity) getActivity() != null){
        EventsActivity m1 = (EventsActivity) getActivity();
        { m1.checkLocPressed(s, c1, c2); }}
    }
}