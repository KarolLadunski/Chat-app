package com.example.android.chatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.chatapp.Model.Suggestion;
import com.example.android.chatapp.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class SuggestionDetailActivity extends AppCompatActivity {

    TextView username, title, description;
    CircleImageView publisher_profile_image;
    RatingBar ratingBar;
    String sPublisher, skill, sid;
    Float skill_float;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion_detail);

        username = findViewById(R.id.username);
        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        publisher_profile_image = findViewById(R.id.image_profile);
        ratingBar = findViewById(R.id.rating_bar);

        Intent intent = getIntent();
        sid = intent.getStringExtra("sid");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Suggestion");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final Suggestion suggestion = snapshot.getValue(Suggestion.class);

                    if (getApplicationContext() == null) {
                        return;
                    }

                    assert suggestion != null;

                    if (suggestion.getSid().equals(sid)){

                        title.setText(suggestion.getTitle());
                        description.setText(suggestion.getDescription());
                        skill = suggestion.getSkill();
                        ratingBar.setRating(Float.parseFloat(skill));
                        ratingBar.setEnabled(false);
                        sPublisher = suggestion.getSpublisher();
                        getUserInfo(username, publisher_profile_image);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void getUserInfo(final TextView username, final ImageView profile_image) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final User user = snapshot.getValue(User.class);

                    if (getApplicationContext() == null) {
                        return;
                    }

                    assert user != null;
                    String name = user.getUsername();
                    String spic = user.getImageURL();
                    if (user.getId().equals(sPublisher)) {
                        username.setText(name);

                        if (user.getImageURL().equals("default")) {
                            profile_image.setImageResource(R.mipmap.ic_launcher);
                        } else {
                            Glide.with(getApplicationContext()).load(spic).into(profile_image);
                        }
                    }

                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
