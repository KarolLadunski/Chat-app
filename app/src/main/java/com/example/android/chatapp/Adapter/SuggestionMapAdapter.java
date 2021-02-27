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

public class SuggestionMapAdapter extends RecyclerView.Adapter<SuggestionMapAdapter.ViewHolder> {
    private Context mContext;
    private List<Suggestion> mSuggestion;
    private FirebaseUser fuser;
    private SuggestionMapAdapter.SuggestionListRecyclerClickListener mClickListener;


    public SuggestionMapAdapter(Context mContext, List<Suggestion> mSuggestion, SuggestionMapAdapter.SuggestionListRecyclerClickListener mClickListener)
    {
        this.mSuggestion = mSuggestion;
        this.mContext = mContext;
        this.mClickListener = mClickListener;
    }

    @NonNull
    @Override
    public SuggestionMapAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.suggestion_home_item, parent, false);
        return new SuggestionMapAdapter.ViewHolder(view, mClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final SuggestionMapAdapter.ViewHolder holder, final int position) {

        final Suggestion suggestion = mSuggestion.get(position);
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        holder.title.setText(suggestion.getTitle());
        holder.description.setText(suggestion.getDescription());
        holder.join.setBackgroundColor(suggestion.getButtonColor());
        getUserInfo(holder.username, holder.profile_image, suggestion.getSpublisher());

        checkIfInterested(suggestion, fuser.getUid(), position);
        checkIfAccepted(suggestion, fuser.getUid(), position);

        holder.join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(suggestion.getButtonColor() != 0xFFFF0000 && suggestion.getButtonColor() != 0xff00ff00){
                    joinEvent(fuser.getUid(), suggestion);
                    notifyItemChanged(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSuggestion.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        public TextView username;
        public ImageView profile_image;
        public TextView title;
        public TextView description;
        public Button join;
        public ImageView iLocation;
        SuggestionMapAdapter.SuggestionListRecyclerClickListener mClickListener;

        public ViewHolder(View itemView, SuggestionMapAdapter.SuggestionListRecyclerClickListener clickListener)
        { super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            join = itemView.findViewById(R.id.btn_join);
            iLocation = itemView.findViewById(R.id.slocation);
            mClickListener = clickListener;
            iLocation.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            mClickListener.onSuggestionClicked(getAdapterPosition());
        }
    }

    private void getUserInfo(final TextView username, final ImageView profile_image, final String sPublisher) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(sPublisher);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());

                if (user.getImageURL().equals("default")) {
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(mContext.getApplicationContext()).load(user.getImageURL()).into(profile_image);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void joinEvent(String fuser, Suggestion suggestion){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Interested");

        String iId = reference.push().getKey();

        HashMap<String, Object> map = new HashMap<>();
        map.put("u1", fuser);
        map.put("sid", suggestion.getSid());
        map.put("iId", iId);
        map.put("u2", suggestion.getSpublisher());
        map.put("type", "join");
        reference.child(iId).setValue(map);

        suggestion.setButtonColor(0xFFFF0000);

    }

    private void checkIfInterested(final Suggestion suggestion, final String fuser, final int position) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Interested");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final Interested interested = snapshot.getValue(Interested.class);
                    assert interested != null;
                    if (interested.getType().equals("join")) {
                        if (interested.getSid().equals(suggestion.getSid()) && interested.getU1().equals(fuser)) {

                            suggestion.setButtonColor(0xFFFF0000);
                            notifyItemChanged(position);

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void checkIfAccepted(final Suggestion suggestion, final String fuser, final int position) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Interested");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final Interested interested = snapshot.getValue(Interested.class);
                    assert interested != null;
                    if (interested.getType().equals("accepted")){
                    if (interested.getSid().equals(suggestion.getSid()) && interested.getU1().equals(fuser)) {

                        suggestion.setButtonColor(0xff00ff00);
                        notifyItemChanged(position);
                    }
                }}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public interface SuggestionListRecyclerClickListener{
        void onSuggestionClicked(int position);
    }
}