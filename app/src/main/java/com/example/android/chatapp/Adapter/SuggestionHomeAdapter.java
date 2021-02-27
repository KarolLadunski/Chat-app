package com.example.android.chatapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.chatapp.EditSuggestionActivity;
import com.example.android.chatapp.Model.Interested;
import com.example.android.chatapp.Model.Suggestion;
import com.example.android.chatapp.Model.User;
import com.example.android.chatapp.ParticipantsActivity;
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

public class SuggestionHomeAdapter extends RecyclerView.Adapter<SuggestionHomeAdapter.ViewHolder> {
    private Context mContext;
    private List<Suggestion> mSuggestion;
    private FirebaseUser fuser;
    private SuggestionHomeAdapter.SuggestionListRecyclerClickListener mClickListener;
    private int i = 0;


    public SuggestionHomeAdapter(Context mContext, List<Suggestion> mSuggestion, SuggestionHomeAdapter.SuggestionListRecyclerClickListener mClickListener) {
        this.mSuggestion = mSuggestion;
        this.mContext = mContext;
        this.mClickListener = mClickListener;
    }

    @NonNull
    @Override
    public SuggestionHomeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.suggestion_home_item, parent, false);
        return new SuggestionHomeAdapter.ViewHolder(view, mClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final SuggestionHomeAdapter.ViewHolder holder, final int position) {

        final Suggestion suggestion = mSuggestion.get(position);
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        holder.title.setText(suggestion.getTitle());
        holder.description.setText(suggestion.getDescription());
        holder.join.setBackgroundColor(suggestion.getButtonColor());
        holder.date.setText(suggestion.getDate());
        holder.time.setText(suggestion.getTime());
        getUserInfo(holder.username, holder.profile_image, suggestion.getSpublisher());

        checkIfInterested(suggestion, fuser.getUid(), position);
        checkIfAccepted(suggestion, fuser.getUid(), position);
        getNumberOfAccepted(suggestion, holder.participants);

        holder.join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(suggestion.getButtonColor() != 0xFFFF0000 && suggestion.getButtonColor() != 0xff00ff00){
                    joinEvent(fuser.getUid(), suggestion);
                    notifyItemChanged(position);
                }
            }
        });

        holder.participants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ParticipantsActivity.class);
                intent.putExtra("sid", suggestion.getSid());
                mContext.startActivity(intent);
            }
        });

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //creating a popup menu
                PopupMenu popup = new PopupMenu(mContext, holder.more);
                //inflating menu from xml resource
                popup.inflate(R.menu.suggestion_menu);

                if (suggestion.getSpublisher().equals(fuser.getUid())){
                    popup.getMenu().removeItem(R.id.report);
                }
                else {
                    popup.getMenu().removeItem(R.id.edit);
                    popup.getMenu().removeItem(R.id.delete_event);
                }
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.report:
                                Report();
                                break;
                            case R.id.edit:
                                editSuggestion(suggestion);
                                break;
                            case R.id.delete_event:
                                deleteSuggestion();
                                break;
                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return mSuggestion.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView username;
        public ImageView profile_image;
        public TextView title;
        public TextView description;
        public Button join;
        public TextView date;
        public TextView time;
        public ImageView sLocation;
        public TextView participants;
        ImageView more;

        SuggestionHomeAdapter.SuggestionListRecyclerClickListener mClickListener;

        public ViewHolder(View itemView, SuggestionHomeAdapter.SuggestionListRecyclerClickListener clickListener){
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            join = itemView.findViewById(R.id.btn_join);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            sLocation = itemView.findViewById(R.id.slocation);
            more = itemView.findViewById(R.id.more);
            participants = itemView.findViewById(R.id.participants);

            mClickListener = clickListener;
            sLocation.setOnClickListener(this);

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

    private void getNumberOfAccepted(final Suggestion suggestion, final TextView participants) {
        i = 0;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Interested");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final Interested interested = snapshot.getValue(Interested.class);
                    assert interested != null;
                    if (interested.getType().equals("accepted")){
                        if (interested.getSid().equals(suggestion.getSid())) {
                            i++; }
                    }}
                participants.setText(i + " participants");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public interface SuggestionListRecyclerClickListener{
        void onSuggestionClicked(int position);
    }
    private void Report(){
        ///
    }

    private void deleteSuggestion(){

    }

    private void editSuggestion(Suggestion suggestion){
        Intent intent = new Intent(mContext, EditSuggestionActivity.class);
        intent.putExtra("sid", suggestion.getSid());
        intent.putExtra("type", "Edit");
        mContext.startActivity(intent);

    }
}
