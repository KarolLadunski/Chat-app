package com.example.android.chatapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.android.chatapp.Fragments.EditSuggestionFragment;

public class EditSuggestionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_suggestion);

        Bundle intent = getIntent().getExtras();
        if (intent != null){
            if (getIntent().getStringExtra("sid")!=null){

                String sid = getIntent().getStringExtra("sid");
                String type = getIntent().getStringExtra("type");


                EditSuggestionFragment fragment = new EditSuggestionFragment();
                Bundle args2 = new Bundle();
                args2.putString("sid", sid);
                args2.putString("type", type);

                fragment.setArguments(args2);

                //fr = "0";
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit();
            }}
    }
}
