package com.example.android.chatapp.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.chatapp.Adapter.NotificationsAdapter;
import com.example.android.chatapp.Main2Activity;
import com.example.android.chatapp.Model.Interested;
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


public class NotificationsFragment extends Fragment implements OnBackPressed {

    private RecyclerView recyclerView;

    private NotificationsAdapter notificationsAdapter;
    private List<Interested> mInterested;
    private FirebaseUser fuser;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        mInterested = new ArrayList<>();
        mContext = getContext();
        getNid(fuser.getUid());
        sendBottomSheetState(1);


        return view;
    }

    private void getNid(final String fuser){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Interested");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mInterested.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Interested interested = snapshot.getValue(Interested.class);
                    assert interested != null;
                    if ((interested.getU1().equals(fuser) && ((!interested.getType().equals("join")) && (!interested.getType().equals("invitationToFriends"))))
                            || (interested.getU2().equals(fuser) && !interested.getType().equals("notification")))
                    {mInterested.add(interested);}
                }
                notificationsAdapter = new NotificationsAdapter(mContext, mInterested);
                recyclerView.setAdapter(notificationsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void onBackPressed() {
        ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new BlankFragment()).commit();

    }

    public void sendBottomSheetState(int s){
        Main2Activity m1 = (Main2Activity) getActivity();
        m1.bottomSheetState(s);
    }
}