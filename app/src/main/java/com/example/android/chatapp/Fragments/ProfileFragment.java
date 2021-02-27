package com.example.android.chatapp.Fragments;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.chatapp.Adapter.MyFotosAdapter;
import com.example.android.chatapp.EditDataActivity;
import com.example.android.chatapp.EventsActivity;
import com.example.android.chatapp.FriendsActivity;
import com.example.android.chatapp.Model.Interested;
import com.example.android.chatapp.Model.Post;
import com.example.android.chatapp.Model.User;
import com.example.android.chatapp.PostActivity;
import com.example.android.chatapp.R;
import com.example.android.chatapp.StartActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;


public class ProfileFragment extends Fragment {

    CircleImageView image_profile;
    TextView username, Work, School, WorkTitle, SchoolTitle, Friends, FriendsTitle, searchFriends;

    DatabaseReference reference;
    FirebaseUser fuser;

    StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;

    Button button, editData, addFriend, addFriendSent, deleteFriend, acceptInvitation;
    String profileid;

    private RecyclerView recyclerView;
    private MyFotosAdapter myFotosAdapter;
    private List<Post> postList;
    private List<Interested> mInviation;
    private Context mContext;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        image_profile = view.findViewById(R.id.profile_image);
        username = view.findViewById(R.id.username);

        School = view.findViewById(R.id.School);
        Work = view.findViewById(R.id.Work);
        searchFriends = view.findViewById(R.id.search_friends);
        SchoolTitle = view.findViewById(R.id.School_info_type);
        WorkTitle = view.findViewById(R.id.Work_info_type);
        addFriend = view.findViewById(R.id.add_friend);
        addFriendSent = view.findViewById(R.id.add_friend_sent);
        deleteFriend = view.findViewById(R.id.delete_friend);
        deleteFriend.setVisibility(View.GONE);
        Friends = view.findViewById(R.id.Friends);
        FriendsTitle = view.findViewById(R.id.Friends_info_type);
        acceptInvitation = view.findViewById(R.id.accept_invitationFromProfile);
        mInviation = new ArrayList<>();

        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", MODE_PRIVATE);
        profileid = prefs.getString("profileid", "none");



        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(mLayoutManager);
        postList = new ArrayList<>();
        myFotosAdapter = new MyFotosAdapter(getContext(), postList);
        recyclerView.setAdapter(myFotosAdapter);
        addFriend.setVisibility(View.VISIBLE);
        addFriendSent.setVisibility(View.GONE);
        deleteFriend.setVisibility(View.GONE);
        acceptInvitation.setVisibility(View.GONE);
        searchFriends.setVisibility(View.GONE);


        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        mContext = getContext();


        FriendsTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), FriendsActivity.class);
                intent.putExtra("id", profileid);
                startActivity(intent);
            }
        });



        button = view.findViewById(R.id.add_photo);
        editData = view.findViewById(R.id.edit_data);
        if (profileid.equals(fuser.getUid())){
            deleteFriend.setVisibility(View.GONE);
            addFriend.setVisibility(View.GONE);
            addFriendSent.setVisibility(View.GONE);
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), PostActivity.class);
                    startActivity(intent);
                }
            });
            editData.setVisibility(View.VISIBLE);
            editData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), EditDataActivity.class);
                    startActivity(intent);}
            });

            searchFriends.setVisibility(View.VISIBLE);
            searchFriends.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new PeopleFragment()).commit();
                }
            });

        } else {
            button.setVisibility(View.GONE);
            editData.setVisibility(View.GONE);
            searchFriends.setVisibility(View.GONE);
            addFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendInvitation(fuser.getUid(), profileid);
                    addFriend.setVisibility(View.GONE);
                    addFriendSent.setVisibility(View.VISIBLE);
                    deleteFriend.setVisibility(View.GONE);
                    acceptInvitation.setVisibility(View.GONE);
                }
            });
            deleteFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteFriend(profileid, fuser.getUid());
                    addFriend.setVisibility(View.VISIBLE);
                    addFriendSent.setVisibility(View.GONE);
                    deleteFriend.setVisibility(View.GONE);
                    acceptInvitation.setVisibility(View.GONE);
                }
            });
            acceptInvitation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    acceptInvitation(profileid, fuser.getUid());
                    addFriend.setVisibility(View.GONE);
                    addFriendSent.setVisibility(View.GONE);
                    deleteFriend.setVisibility(View.VISIBLE);
                    acceptInvitation.setVisibility(View.GONE);
                }
            });
        }


        myFotos();
        userInfo();
        checkInvitations();

        checkFriends();


        checkInvitationsToMe();


        image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        menu.findItem(R.id.chats_icon).setVisible(false);
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.filter_icon).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
        if (profileid.equals(fuser.getUid())){
            menu.findItem(R.id.logout).setVisible(true);}
        else{
            menu.findItem(R.id.logout).setVisible(false);}
        }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.event_icon)
        {
            Intent intent = new Intent(getActivity(), EventsActivity.class);
            startActivity(intent);
            return true;
        }

        if(id == R.id.logout)
        {
            Logout();
            return true;
        }

        return false;
    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(){
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Uploading");
        pd.show();

        if (imageUri != null){
            final  StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    +"."+getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw  task.getException();
                    }

                    return  fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageURL", ""+mUri);
                        reference.updateChildren(map);

                        pd.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        } else {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            imageUri = data.getData();

            if (uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(getContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }
        }
    }

    private void userInfo(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (getContext() == null){
                    return;
                }
                User user = dataSnapshot.getValue(User.class);

                assert user != null;
                username.setText(user.getUsername());
                School.setText(user.getSchool());
                Work.setText(user.getWork());

                String school_text = School.getText().toString();
                String work_text = Work.getText().toString();


                if (school_text.isEmpty()){
                    School.setVisibility(View.GONE);
                    SchoolTitle.setVisibility(View.GONE);
                }
                if (work_text.isEmpty()){
                    Work.setVisibility(View.GONE);
                    WorkTitle.setVisibility(View.GONE);
                }

                if (user.getImageURL().equals("default")){
                    image_profile.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(getContext()).load(user.getImageURL()).into(image_profile);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void myFotos(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileid)){
                        postList.add(post);
                    }
                }
                Collections.reverse(postList);
                myFotosAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

      private void sendInvitation(String userid, String futureFriend){

          DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                  .child("Interested");

          String iId = reference.push().getKey();
        HashMap<String, Object> map = new HashMap<>();
        map.put("u1", userid);
        map.put("u2", futureFriend);
        map.put("iId", iId);
        map.put("sid", "");
        map.put("type", "invitationToFriends");

          reference.child(iId).setValue(map);

    }

    private void checkInvitations(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Interested");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                final Interested interested = snapshot.getValue(Interested.class);
                assert interested != null;
                if (interested.getType().equals("invitationToFriends")){
                if (interested.getU1().equals(fuser.getUid()) && interested.getU2().equals(profileid)) {

                   // if (!profileid.equals(fuser.getUid())){
                addFriend.setVisibility(View.GONE);
                addFriendSent.setVisibility(View.VISIBLE);
                deleteFriend.setVisibility(View.GONE);}}}
               // }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkFriends(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Friends").child(fuser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String friend = snapshot.getKey();
                    if (friend.equals(profileid)){
                        if (!profileid.equals(fuser.getUid())){
                        addFriend.setVisibility(View.GONE);
                        addFriendSent.setVisibility(View.GONE);
                        deleteFriend.setVisibility(View.VISIBLE);
                    }}

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

   private void checkInvitationsToMe() {

       DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Interested");

       reference.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                   final Interested interested = snapshot.getValue(Interested.class);
                   assert interested != null;
                   if (interested.getType().equals("invitationToFriends")){
                   if (interested.getU1().equals(profileid) && interested.getU2().equals(fuser.getUid())) {
                       if (!profileid.equals(fuser.getUid())){
                       addFriend.setVisibility(View.GONE);
                       addFriendSent.setVisibility(View.GONE);
                       deleteFriend.setVisibility(View.GONE);
                       acceptInvitation.setVisibility(View.VISIBLE);}
                   }
               }}
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
   }

    private void acceptInvitation(String invitor, String futureFriend){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Friends").child(futureFriend);

        HashMap<String, Object> map = new HashMap<>();
        map.put(invitor, invitor);
        reference.updateChildren(map);


        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference()
                .child("Friends").child(invitor);
        HashMap<String, Object> map2 = new HashMap<>();
        map2.put(futureFriend, futureFriend);
        reference2.updateChildren(map2);


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

                        FirebaseDatabase.getInstance().getReference("Interested")
                                .child(interested.getiId()).removeValue();
                    }
                }}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void deleteFriend(final String profileid, final String fuser){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Friends").child(fuser);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    String friend = snapshot.getKey();
                    if (friend.equals(profileid)){
                        FirebaseDatabase.getInstance().getReference("Friends")
                                .child(fuser).child(profileid).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference()
                .child("Friends").child(profileid);
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    String friend = snapshot.getKey();
                    if (friend.equals(fuser)){
                        FirebaseDatabase.getInstance().getReference("Friends")
                                .child(profileid).child(fuser).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void Logout(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

        Date c = Calendar.getInstance().getTime();

        long timeInMilliseconds = c.getTime();
        String formattedDate = Long.toString(timeInMilliseconds);


        HashMap<String, Object> map = new HashMap<>();
        map.put("timeSeen", formattedDate);

        reference.updateChildren(map);
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), StartActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }

}
