package com.example.android.chatapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.support.v4.app.Fragment;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.android.chatapp.Fragments.BlankFragment;
import com.example.android.chatapp.Fragments.MapFragment;
import com.example.android.chatapp.Fragments.NearbyPeopleFragment;
import com.example.android.chatapp.Fragments.NotificationsFragment;
import com.example.android.chatapp.Fragments.PeopleFragment;
import com.example.android.chatapp.Fragments.ProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;

public class Main2Activity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {


    private static final String TAG = "MyActivity";
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    int f = 1;
    int s = 0;
    Context mContext;


    public void bottomSheetState(int b){

        s=b;
    }

    @Override
    public void onBackPressed() {

        switch (s){
            case 1:
            tellFragments();
            break;

            case 0:
            super.onBackPressed();
            break;}
    }

    private void tellFragments(){
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for(Fragment f : fragments){
            if(f != null && f instanceof MapFragment){
                ((MapFragment)f).onBackPressed();
            s=0;}
            if(f != null && f instanceof NotificationsFragment)
            {   s=0;
                ((NotificationsFragment)f).onBackPressed();}
            if(f != null && f instanceof PeopleFragment)
            {   s=0;
                ((PeopleFragment)f).onBackPressed();}
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        loadFragment(new BlankFragment());
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Chatapp");
        navView.getMenu().findItem(R.id.navigation_explore).setChecked(true);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
       // Bundle intent = getIntent().getExtras();

///////////
        mContext = getApplicationContext();

        Bundle intent = getIntent().getExtras();
        if (intent != null){
            if (getIntent().getStringExtra("fr")!=null){

                String c1 = getIntent().getStringExtra("cord1");
                String c2 = getIntent().getStringExtra("cord2");
                MapFragment fragment = new MapFragment();
                Bundle args2 = new Bundle();
                args2.putString("CameraCord1", c1);
                args2.putString("CameraCord2", c2);
                args2.putString("when", "In a month");
                args2.putString("distanceFilter", "10");
                Log.i(TAG, "MyClass.getView() — my cords " + c1 + "   " + c2);
                fragment.setArguments(args2);
                getIntent().removeExtra("fr");
                getIntent().removeExtra("cord1");
                getIntent().removeExtra("cord2");
                getIntent().setData(null);
                //fr = "0";
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit();
            }}

        else {
            Fragment fragment = new MapFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    fragment).commit();
        }

       // intent = null;
        mContext = getApplicationContext();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String c1 = data.getStringExtra("cord1");
                String c2 = data.getStringExtra("cord2");
                MapFragment fragment = new MapFragment();
                Bundle args2 = new Bundle();
                args2.putString("CameraCord1", c1);
                args2.putString("CameraCord2", c2);
                args2.putString("when", "In a month");
                args2.putString("distanceFilter", "10");
                Log.i(TAG, "MyClass.getView() — my cords " + c1 + "   " + c2);
                fragment.setArguments(args2);
                data.removeExtra("cord1");
                data.removeExtra("cord2");
                getIntent().setData(null);
                //fr = "0";
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
                Fragment fragment = new MapFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        fragment).commit();
            }
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        switch (item.getItemId()) {

            case R.id.navigation_events:
                fragment = new BlankFragment();
                break;

            case R.id.navigation_explore:
                f=0;
                fragment = new MapFragment();
                break;

            case R.id.notifications:
                f=0;
                fragment = new NotificationsFragment();
                break;

            case R.id.navigation_people:
                fragment = new NearbyPeopleFragment();
                break;

            case R.id.navigation_profile:
                SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                editor.apply();
                fragment = new ProfileFragment();
                break;

        }

        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }

        return false;
    }

    private void status(String status){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
        getIntent().setData(null);
    }
}
