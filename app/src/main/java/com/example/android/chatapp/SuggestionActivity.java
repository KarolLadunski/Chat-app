package com.example.android.chatapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.android.chatapp.Fragments.ChangeLocFragment;

import static com.android.volley.VolleyLog.TAG;

public class SuggestionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion);
        Bundle intent = getIntent().getExtras();
        if (intent != null){

                String sid = getIntent().getStringExtra("sid");
                String type = getIntent().getStringExtra("type");
            String cord1 = getIntent().getStringExtra("CameraCord1");
            String cord2 = getIntent().getStringExtra("CameraCord2");

                ChangeLocFragment fragment = new ChangeLocFragment();
                Bundle args2 = new Bundle();
                args2.putString("sid", sid);
                args2.putString("type", type);
            args2.putString("CameraCord1", cord1);
            args2.putString("CameraCord2", cord2);
            Log.i(TAG, "Coords 1:" + cord1 + "   2:" + cord2);


            fragment.setArguments(args2);

                //fr = "0";
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit();
            }
    }
}