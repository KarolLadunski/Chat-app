package com.example.android.chatapp.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.chatapp.Fragments.APIService;
import com.example.android.chatapp.MessageActivity;
import com.example.android.chatapp.Model.User;
import com.example.android.chatapp.Notifications.Client;
import com.example.android.chatapp.Notifications.Data;
import com.example.android.chatapp.Notifications.MyResponse;
import com.example.android.chatapp.Notifications.Sender;
import com.example.android.chatapp.Notifications.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.android.chatapp.Model.Chat;
import com.example.android.chatapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static  final int MSG_TYPE_LEFT = 0;
    public static  final int MSG_TYPE_RIGHT = 1;
    public static  final int MSG_TYPE_LEFT_INVITATION = 2;
    public static  final int MSG_TYPE_RIGHT_INVITATION = 3;

    private Context mContext;
    private List<Chat> mChat;
    private String imageurl, username;
    private int c = 0;
    private boolean notify = false;

    private FirebaseUser fuser;
    private APIService apiService;

    public MessageAdapter(Context mContext, List<Chat> mChat, String imageurl, String username){
        this.mChat = mChat;
        this.mContext = mContext;
        this.imageurl = imageurl;
        this.username = username;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        } if (viewType == MSG_TYPE_LEFT){
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
        if (viewType == MSG_TYPE_RIGHT_INVITATION){
            View view = LayoutInflater.from(mContext).inflate(R.layout.invitation_just_text_item, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.invited_for_item, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.ViewHolder holder, final int position) {

        final Chat chat = mChat.get(position);

        holder.show_message.setText(chat.getMessage());
        holder.date.setText(chat.getDate());
        holder.show_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayHideDate(holder.date, chat);
            }
        });


       holder.justInfo.setText("you sent invitation for "+chat.getInvitationTitle());
       String icon = chat.getInvitationTitle();
       int resID = mContext.getResources().getIdentifier(icon , "drawable", mContext.getPackageName());
       holder.icon.setImageResource(resID);
       holder.info.setText(username+" invited you for "+chat.getInvitationTitle());
       holder.accept.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
               notify = true;
               acceptInvitationInChat(chat);
               notifyItemChanged(position);
           }
       });

       if (chat.getType().equals("invitation") && chat.getSender().equals(fuser.getUid()))
       {holder.justInfo.setText("you sent invitation for "+chat.getInvitationTitle());}

       if (chat.getType().equals("accepted") && chat.getSender().equals(fuser.getUid()))
       {holder.justInfo.setText("Your invitation has been accepted");}

        if (chat.getType().equals("accepted") && chat.getReceiver().equals(fuser.getUid()))
        {holder.justInfo.setText("You accepted invitation for " + chat.getInvitationTitle());}


       String previousTs = "";
        if(position>0){
            Chat pm = mChat.get(position-1);
            previousTs = pm.getDate();
        }
        try {
            setTimeTextVisibility(chat.getDate(), previousTs, holder.date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (imageurl.equals("default")){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(mContext).load(imageurl).into(holder.profile_image);
        }

        if (imageurl.equals("default")){
            holder.profile_image2.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(mContext).load(imageurl).into(holder.profile_image2);
        }

        if (position == mChat.size()-1){
            if (chat.isIsseen()){
                holder.txt_seen.setText("Seen");
            } else {
                holder.txt_seen.setText("Delivered");
            }
        } else {
            holder.txt_seen.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{

        public TextView show_message;
        public ImageView profile_image;
        public TextView txt_seen;
        public TextView date;
        public TextView justInfo;
        public ImageView icon;
        public TextView info;
        public ImageView profile_image2;
        public Button accept;
        public Button reject;


        public ViewHolder(View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);
            txt_seen = itemView.findViewById(R.id.txt_seen);
            date = itemView.findViewById(R.id.msg_date);
            justInfo = itemView.findViewById(R.id.just_info);
            icon = itemView.findViewById(R.id.invited_for_icon);
            info = itemView.findViewById(R.id.info);
            profile_image2 = itemView.findViewById(R.id.profile_image_2);
            accept = itemView.findViewById(R.id.accept);
            reject = itemView.findViewById(R.id.reject);

        }
    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(fuser.getUid()) && mChat.get(position).getType().equals("message")){
            return MSG_TYPE_RIGHT; }
        if(!mChat.get(position).getSender().equals(fuser.getUid()) && mChat.get(position).getType().equals("message")){
            return MSG_TYPE_LEFT; }

        if (mChat.get(position).getReceiver().equals(fuser.getUid()) && mChat.get(position).getType().equals("invitation")){
            return MSG_TYPE_LEFT_INVITATION; }

        else {return MSG_TYPE_RIGHT_INVITATION; }

    }

    private void setTimeTextVisibility(String ts1, String ts2, TextView timeText) throws ParseException {

        if(ts2.equals("")){
            timeText.setVisibility(View.VISIBLE);
            timeText.setText(ts1);
        }else {


            SimpleDateFormat format1=new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date dt1=format1.parse(ts1);
            long timeInMiliseconds1 = dt1.getTime();


            Date dt2=format1.parse(ts2);
            long timeInMiliseconds2 = dt2.getTime();


            Date c = Calendar.getInstance().getTime();
            String todayDate = format1.format(c);
            Date dt = format1.parse(todayDate);

            String year1 = (String) DateFormat.format("yyyy", dt1);
            
            String year2 = (String) DateFormat.format("yyyy", dt);

            boolean sameYear = year1.equals(year2);

            if (sameYear){
                format1 = new SimpleDateFormat("MM-dd HH:mm");
                ts1 = format1.format(dt1);
            }

            boolean lessThanTimeLimit = timeInMiliseconds1 - timeInMiliseconds2 < 600000;

            if(lessThanTimeLimit){
                timeText.setVisibility(View.GONE);
                timeText.setText("");
            }else {
                timeText.setVisibility(View.VISIBLE);
                timeText.setText(ts1);
            }

        }
    }

    private void displayHideDate(TextView date, Chat m){
        switch (c){
            case 0:
                date.setText(m.getDate());
            {date.setVisibility(View.VISIBLE);
                c=1;
            break;}
            case 1:
            {date.setVisibility(View.GONE);
                c=0;
            break;}
        }

    }

    private void acceptInvitationInChat(final Chat chat){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats").child(chat.getMid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("type", "accepted");


        reference.updateChildren(hashMap);

        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (notify) {
                    sendNotification(chat.getSender(), user.getUsername(), "accepted your invitation");
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendNotification(final String receiver, final String username, final String message){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(fuser.getUid(), R.mipmap.ic_launcher, username+": "+message, "New Message",
                            receiver);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200){
                                        if (response.body().success != 1){
                                            Toast.makeText(mContext, "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}