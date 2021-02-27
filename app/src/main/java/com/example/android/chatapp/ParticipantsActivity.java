package com.example.android.chatapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.android.chatapp.Adapter.UserAdapter;
import com.example.android.chatapp.Model.Interested;
import com.example.android.chatapp.Model.Suggestion;
import com.example.android.chatapp.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ParticipantsActivity extends AppCompatActivity {

    String sid = null;
    private UserAdapter userAdapter;
    private List<String> mUsers;
    private List<User> ListOfUsers;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participants);

        Bundle intent = getIntent().getExtras();
        if (intent != null){
            sid = getIntent().getStringExtra("sid");}

        mUsers = new ArrayList<>();
        ListOfUsers = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        userAdapter = new UserAdapter(getApplicationContext(), ListOfUsers, true);
        recyclerView.setAdapter(userAdapter);
        getAccepted(sid);

    }


    private void getAccepted(final String sid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Interested");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final Interested interested = snapshot.getValue(Interested.class);
                    assert interested != null;
                    if (interested.getType().equals("accepted")){
                        if (interested.getSid().equals(sid)) {
                            mUsers.add(interested.getU1());
                        }
                    }}
                getUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUsers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ListOfUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    for (String id : mUsers){
                        assert user != null;
                        if (user.getId().equals(id)){
                            ListOfUsers.add(user);
                        }
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
