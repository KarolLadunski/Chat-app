package com.example.android.chatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.android.chatapp.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class EditDataActivity extends AppCompatActivity {

    Button editDataSave;

    EditText school, work;

    FirebaseUser fuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_data);

        school = findViewById(R.id.school_info);
        work = findViewById(R.id.work_info);
        editDataSave = findViewById(R.id.edit_data_save);
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                school.setText(user.getSchool());
                work.setText(user.getWork());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        editDataSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile(school.getText().toString(), work.getText().toString());
                startActivity(new Intent(EditDataActivity.this, Main2Activity.class));

            }
        });

    }

    private void updateProfile(String school, String work){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(fuser.getUid());

        HashMap<String, Object> map = new HashMap<>();
        map.put("school", school);
        map.put("work", work);

        reference.updateChildren(map);

    }
}
