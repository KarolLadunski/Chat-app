package com.example.android.chatapp.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.chatapp.Model.Interested;
import com.example.android.chatapp.Model.Suggestion;
import com.example.android.chatapp.Model.User;
import com.example.android.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    private Context mContext;
    private List<Interested> mInterested;
    private FirebaseUser fuser;


    public NotificationsAdapter(Context mContext, List<Interested> mInterested){
        this.mInterested = mInterested;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.notification__item, parent, false);
        return new NotificationsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Interested interested = mInterested.get(position);
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        if (interested.getType().equals("join") && !interested.getU1().equals(fuser.getUid())){
            getSuggestion(holder.notification_info, interested.getSid());
            getInterestedInfo(interested.getU1(), holder.profile_image, holder.username);
            holder.reject.setVisibility(View.VISIBLE);
            holder.accept.setVisibility(View.VISIBLE);
            holder.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    acceptInterested(interested.getiId(), interested.getU1());

                }
            });

            holder.reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rejectInterested(interested.getiId());
                }
            });
        }

        if (interested.getType().equals("accepted")){
            if (!interested.getU1().equals(fuser.getUid())){
                getSuggestion(holder.notification_info, interested.getSid());
            getAcceptedInfo(interested.getU1(), holder.profile_image, holder.username);
            holder.reject.setVisibility(View.GONE);
            holder.accept.setVisibility(View.GONE);}
            else {
                getSuggestion(holder.notification_info, interested.getSid());
                getPublisherInfo(interested.getU2(), holder.profile_image, holder.username);
                holder.reject.setVisibility(View.GONE);
                holder.accept.setVisibility(View.GONE);
            }
        }

        if (interested.getType().equals("notification")){
            if (interested.getU1().equals(fuser.getUid())){
                getNotifiedInfo(interested.getU2(), holder.profile_image, holder.username);
                getSuggestion(holder.notification_info, interested.getSid());
                holder.reject.setVisibility(View.GONE);
                holder.accept.setVisibility(View.GONE);
            }
        }

        if (interested.getType().equals("invitationToFriends")){
            if (interested.getU2().equals(fuser.getUid())){
            getInvitorInfo(interested.getU1(), holder.profile_image, holder.username);
            holder.notification_info.setVisibility(View.GONE);
            holder.reject.setVisibility(View.VISIBLE);
            holder.accept.setVisibility(View.VISIBLE);
                holder.accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        acceptInvitation(interested.getU1(), interested.getU2());

                    }
                });
            }
        }

        if (interested.getType().equals("friend")){
            if (interested.getU1().equals(fuser.getUid())){
                getFriendInfo(interested.getU2(), holder.profile_image, holder.username);
                holder.reject.setVisibility(View.GONE);
                holder.accept.setVisibility(View.GONE);
                holder.notification_info.setVisibility(View.GONE);
            }
            if (interested.getU2().equals(fuser.getUid())){
                getFriendInfo(interested.getU1(), holder.profile_image, holder.username);
                holder.reject.setVisibility(View.GONE);
                holder.accept.setVisibility(View.GONE);
                holder.notification_info.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mInterested.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{

        public TextView notification_info;
        public TextView username;
        public Button accept;
        public ImageView profile_image;
        public Button reject;

        public ViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            accept = itemView.findViewById(R.id.accept);
            notification_info = itemView.findViewById(R.id.notification_info);
            profile_image = itemView.findViewById(R.id.profile_image);
            reject = itemView.findViewById(R.id.reject);

        }
    }



    private void getSuggestion(final TextView title, String sid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Suggestion").child(sid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final Suggestion suggestion = dataSnapshot.getValue(Suggestion.class);
                    assert suggestion != null;

                    title.setText(suggestion.getTitle());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getInterestedInfo(String user, final ImageView profilePic, final TextView username) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(user);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final User user = dataSnapshot.getValue(User.class);
                    assert user != null;
                    if (user.getImageURL().equals("default")) {
                        profilePic.setImageResource(R.mipmap.ic_launcher);
                    } else {
                        Glide.with(mContext.getApplicationContext()).load(user.getImageURL()).into(profilePic);
                    }
                    username.setText(user.getUsername());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getInvitorInfo(String user, final ImageView profilePic, final TextView username) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(user);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final User user = dataSnapshot.getValue(User.class);
                assert user != null;
                if (user.getImageURL().equals("default")) {
                    profilePic.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(mContext.getApplicationContext()).load(user.getImageURL()).into(profilePic);
                }
                username.setText(user.getUsername() + "wants to be your friend");


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getFriendInfo(String user, final ImageView profilePic, final TextView username) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(user);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final User user = dataSnapshot.getValue(User.class);
                assert user != null;
                if (user.getImageURL().equals("default")) {
                    profilePic.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(mContext.getApplicationContext()).load(user.getImageURL()).into(profilePic);
                }
                username.setText("You are now friends with " + user.getUsername());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getNotifiedInfo(String user, final ImageView profilePic, final TextView username) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(user);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final User user = dataSnapshot.getValue(User.class);
                assert user != null;
                if (user.getImageURL().equals("default")) {
                    profilePic.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(mContext.getApplicationContext()).load(user.getImageURL()).into(profilePic);
                }
                username.setText(user.getUsername() +" posted activity: ");


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getAcceptedInfo(String user, final ImageView profilePic, final TextView username) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(user);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final User user = dataSnapshot.getValue(User.class);
                assert user != null;
                if (user.getImageURL().equals("default")) {
                    profilePic.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(mContext.getApplicationContext()).load(user.getImageURL()).into(profilePic);
                }
                username.setText("You accepted " + user.getUsername() + " for ");


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void acceptInterested(String iId, String user){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Interested").child(iId);

        HashMap<String, Object> map = new HashMap<>();
        map.put("type", "accepted");

        reference.updateChildren(map);
    }

    private void getPublisherInfo(String user, final ImageView profilePic, final TextView username) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(user);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final User user = dataSnapshot.getValue(User.class);
                assert user != null;
                if (user.getImageURL().equals("default")) {
                    profilePic.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(mContext.getApplicationContext()).load(user.getImageURL()).into(profilePic);
                }
                String accepted = " accepted you for ";
                username.setText(user.getUsername()+ accepted);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void rejectInterested(final String iId){
        FirebaseDatabase.getInstance().getReference()
                .child("Interested").child(iId).removeValue();
    }

    private void acceptInvitation(String invitor, String futureFriend){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Friends");

        String FId = reference.push().getKey();
        HashMap<String, Object> map = new HashMap<>();
        map.put("friend1", invitor);
        map.put("friend2", futureFriend);
        map.put("FId", FId);
        reference.child(FId).setValue(map);


        deleteInvitationFromFirebase(invitor, futureFriend);

    }

    private void deleteInvitationFromFirebase(final String invitor, final String futureFriend){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Interested");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    final Interested interested = snapshot.getValue(Interested.class);
                    assert interested != null;
                    if (interested.getType().equals("invitationToFriends")){
                    if (interested.getU1().equals(invitor) && interested.getU2().equals(futureFriend)) {

                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Interested")
                                .child(interested.getiId());

                        HashMap<String, Object> map = new HashMap<>();
                        map.put("type", "friend");

                        reference1.updateChildren(map);
                    }
                }}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}