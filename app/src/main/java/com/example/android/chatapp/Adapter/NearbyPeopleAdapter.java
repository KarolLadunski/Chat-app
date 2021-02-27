package com.example.android.chatapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.chatapp.Fragments.ProfileFragment;
import com.example.android.chatapp.InviteForActivity;
import com.example.android.chatapp.Model.User;
import com.example.android.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class NearbyPeopleAdapter extends RecyclerView.Adapter<NearbyPeopleAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private FirebaseUser fuser;



    public NearbyPeopleAdapter(Context mContext, List<User> mUsers){
        this.mUsers = mUsers;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public NearbyPeopleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.nearby_people_item, parent, false);
        return new NearbyPeopleAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NearbyPeopleAdapter.ViewHolder holder, int position) {

        final User user = mUsers.get(position);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        holder.username.setText(user.getUsername());
        if (user.getImageURL().equals("default")){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(mContext).load(user.getImageURL()).into(holder.profile_image);
        }

        checkDistance(user, holder.distance);

        holder.invite_person_to_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, InviteForActivity.class);
                intent.putExtra("userid", user.getId());
                mContext.startActivity(intent);

            }
        });


        checkWhenSeen(user, holder.timeSeen);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                    editor.putString("profileid", user.getId());
                    editor.apply();

                    ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new ProfileFragment()).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public ImageView profile_image;
        public Button invite_person_to_join;
        public TextView timeSeen;
        public TextView distance;

        public ViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            invite_person_to_join = itemView.findViewById(R.id.invite_person);
            timeSeen = itemView.findViewById(R.id.time_seen);
            distance = itemView.findViewById(R.id.distance);
        }
    }

    private void checkWhenSeen(User user, TextView whenSeen){
        String timeSeen = user.getTimeSeen();
        long time = Long.valueOf(timeSeen);

        Date c = Calendar.getInstance().getTime();
        long timeInMilliseconds = c.getTime();

        long timeInMinutes = (timeInMilliseconds-time)/(1000*60);

        int minutes = (int) timeInMinutes;

        if (minutes<1){
            whenSeen.setText("just now");
        }
        else {
            whenSeen.setText(minutes + "minutes ago");
        }
    }

    private void checkDistance(User user, final TextView distance){
        final Double Lat = user.getLat();
        final Double Lng = user.getLng();

        final Location userLocation = new Location("point A");
        final Location myLocation = new Location("point B");

        userLocation.setLatitude(Lat);
        userLocation.setLongitude(Lng);

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
                    Float distanceToUser = myLocation.distanceTo(userLocation)/1000;

                    //String distanceInString = Float.toString(distanceToUser);
                    String s = String.format("%.1f", distanceToUser);
                    distance.setText(s + " km away");
                }}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}