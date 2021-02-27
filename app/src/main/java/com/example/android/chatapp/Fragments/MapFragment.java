package com.example.android.chatapp.Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.chatapp.Adapter.SuggestionHomeAdapter;
import com.example.android.chatapp.Adapter.SuggestionMapAdapter;
import com.example.android.chatapp.ChatsActivity;
import com.example.android.chatapp.EventsActivity;
import com.example.android.chatapp.Main2Activity;
import com.example.android.chatapp.Model.MarkerClusterRenderer;
import com.example.android.chatapp.Model.MyItem;
import com.example.android.chatapp.Model.Suggestion;
import com.example.android.chatapp.Model.Title;
import com.example.android.chatapp.Model.User;
import com.example.android.chatapp.R;
import com.example.android.chatapp.SuggestionActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.clustering.ClusterManager;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import android.support.v7.widget.SearchView;
import android.app.SearchManager;
import android.widget.SearchView.OnQueryTextListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.android.volley.VolleyLog.TAG;


public class MapFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, OnMapReadyCallback, OnBackPressed, SuggestionMapAdapter.SuggestionListRecyclerClickListener {

    private GoogleMap mMap;
    private ClusterManager<MyItem> mClusterManager;
    private int clicks;

    private List<String> idList;

    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private RecyclerView recyclerView;
    private SuggestionMapAdapter suggestionAdapter;
    private List<Suggestion> mSuggestion;
    private FusedLocationProviderClient mFusedLocationClient;

    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;
    private FloatingActionButton floatingActionButton;

    private Location locationData;
    FirebaseUser fuser;
    String description, coordi1, coordi2, spublisher;
    Double l1, l2;
    private BottomSheetBehavior mBottomSheetBehavior;
    int a =0;
    int b = 0;
    String status = "null";
    int distanceFilter;
    String when, showMyMarkers;
    String CameraCordinate1 = null;
    String CameraCordinate2 = null;
    Double cc1 = null;
    Double cc2 = null;


    private static final long TIME_INTERVAL_GET_LOCATION = 1000 * 1000; // 1 Minute
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 5000 * 1000;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, null);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        // needs to be edited
        clicks = 0;
        when = "In a month";
        distanceFilter = 10;
        showMyMarkers = "do not show";

        //
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        View bottomSheet = rootView.findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        recyclerView = rootView.findViewById(R.id.bottom_sheet_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        floatingActionButton = rootView.findViewById(R.id.fab);

        mSuggestion = new ArrayList<>();
        idList = new ArrayList<>();

        if ((getArguments() != null))
        {
            String distanceFilterString = getArguments().getString("distanceFilter");
            assert distanceFilterString != null;
            distanceFilter = Integer.parseInt(distanceFilterString);
            when = getArguments().getString("when");
            showMyMarkers = getArguments().getString("showMyMarkers");
            CameraCordinate1 = getArguments().getString("CameraCord1");
            CameraCordinate2 = getArguments().getString("CameraCord2");
            if (CameraCordinate1!=null && CameraCordinate2!=null){
            cc1 = Double.parseDouble(CameraCordinate1);
            cc2 = Double.parseDouble(CameraCordinate2);}

        }

        sendBottomSheetState(b);


        mapFragment.getMapAsync(this);

        TedPermission.with(getActivity())
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mContext = getContext();

        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        getStatus();
        super.onResume();
    }

    @Override
    public void onPause() {
        getStatus();
        super.onPause(); }


    private void getStatus(){
        { DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    assert user != null;
                    status = user.getStatus();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }}

    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {

            // Create the LocationRequest object
            mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(TIME_INTERVAL_GET_LOCATION)    // 3 seconds, in milliseconds
                    .setFastestInterval(TIME_INTERVAL_GET_LOCATION); // 1 second, in milliseconds


            if (!mGoogleApiClient.isConnected()) {
                mGoogleApiClient.connect();
                locationChecker(mGoogleApiClient, getActivity());
            }
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
        }
    };

    @Override
    public void onConnected(@Nullable final Bundle bundle) {


        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                locationData = locationResult.getLastLocation();
                getStatus();

                if (locationData != null){ //&& status.equals("online")) {

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("Lat", locationData.getLatitude());
                    map.put("Lng", locationData.getLongitude());

                    reference.updateChildren(map);



                    if (a<1){

                        readSuggestion();

                    LatLng point = new LatLng(locationData.getLatitude(), locationData.getLongitude());


                    if (ActivityCompat.checkSelfPermission((Activity)mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED && ActivityCompat.
                            checkSelfPermission((Activity)mContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    mMap.setMyLocationEnabled(true);

                    if (cc1 == null && cc2 == null){
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(point).zoom(16).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    a++;}
                    else {
                        LatLng point2 = new LatLng(cc1, cc2);
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(point2).zoom(16).build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        a++;
                        cc1 = null;
                        cc2 = null;
                    }}

                }

                else {
                mFusedLocationClient.removeLocationUpdates(this);}

            }
        }, null);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        if (connectionResult.hasResolution() && getActivity() instanceof Activity) {
            try {
                Activity activity = (Activity) getActivity();
                connectionResult.startResolutionForResult(activity, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.i("", "Location services connection failed with code " + connectionResult.getErrorCode());
        }

    }

    public void sendBottomSheetState(int s){
        Main2Activity m1 = (Main2Activity) getActivity();
        m1.bottomSheetState(s);
    }

    public void locationChecker(GoogleApiClient mGoogleApiClient, final Activity activity) {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1800 * 1000);
        locationRequest.setFastestInterval(1200 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(activity, 1000);

                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }


        });
    }

    @Override
    public void onLocationChanged(Location location) {
        //Log.w("==>UpdateLocation<==", "" + String.format("%.6f", location.getLatitude()) + "," + String.format("%.6f", location.getLongitude()));
        //locationData = location;
        //Toast.makeText(getActivity(), "Latitude: " + locationData.getLatitude() + ", Longitude: " + locationData.getLongitude(), Toast.LENGTH_SHORT).show();
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.setBuildingsEnabled(false);
        mMap.getUiSettings().setCompassEnabled(true);
        //mMap.getUiSettings().setAllGesturesEnabled(false);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setIndoorEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mClusterManager = new ClusterManager<MyItem>(mContext, mMap);
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

        String apiKey = getString(R.string.google_maps_key);


        // Initialize Places. For simplicity, the API key is hard-coded. In a production
        // environment we recommend using a secure mechanism to manage API keys.

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Double l1 = mMap.getCameraPosition().target.latitude;
                Double l2 = mMap.getCameraPosition().target.longitude;
                String coordl12 = l1.toString();
                String coordl22 = l2.toString();
                Intent intent = new Intent(getActivity(), SuggestionActivity.class);
                intent.putExtra("type", "New");
                intent.putExtra("CameraCord1", coordl12);
                intent.putExtra("CameraCord2", coordl22);
                Log.i(TAG, "Coords 1:" + coordl12 + "   2:" + coordl22);

                startActivity(intent);

            }
        });

       mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                b=0;
                sendBottomSheetState(b);
                hideKeyboard(getActivity());
            }
        });

       mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
           @Override
           public boolean onMarkerClick(Marker marker) {
               mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
               b=1;
               sendBottomSheetState(b);
               LatLng latlng = marker.getPosition();
               CameraPosition cameraPosition = new CameraPosition.Builder().target(latlng).zoom(16).build();
               mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
               readCertainSuggestion(marker.getTitle());
               hideKeyboard(getActivity());
               return(true);
           }
       });

    }

    public MarkerOptions getMarkerOptions (Double l1, Double l2, String title,  int iconRes) {
        return new MarkerOptions()
                .title(title)
                .position(new LatLng(l1, l2))
                .icon(BitmapDescriptorFactory.fromResource(iconRes));
    }


    private void readMarkers(){
        mClusterManager.clearItems();
        mMap.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Suggestion");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Suggestion suggestion = snapshot.getValue(Suggestion.class);
                        if (getContext() == null){
                            return;
                        }

                    for (String id : idList){
                        if (suggestion.getSid().equals(id)){

                        description = suggestion.getDescription();
                        spublisher = suggestion.getSpublisher();
                        coordi1 = suggestion.getCoord1();
                        coordi2 = suggestion.getCoord2();
                        l1 = Double.parseDouble(coordi1);
                        l2 = Double.parseDouble(coordi2);
                        String sid = suggestion.getSid();

                        String sportPic = suggestion.getTitle();
                        int resID = getResources().getIdentifier(sportPic , "drawable", getActivity().getPackageName());



                        if (locationData != null){
                        Location markerLocation = new Location("point A");
                        markerLocation.setLatitude(l1);
                        markerLocation.setLongitude(l2);



                        Location userLocation = new Location("point B");
                        userLocation.setLatitude(locationData.getLatitude());
                        userLocation.setLongitude(locationData.getLongitude());

                        Float distanceToUser = userLocation.distanceTo(markerLocation)/1000;

                        if (distanceToUser <= 10)
                         { MyItem offsetItem = new MyItem(getMarkerOptions(l1, l2, sid, resID));
                         mClusterManager.addItem(offsetItem);}}}}}

                setRenderer();
                mClusterManager.cluster();
                    }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void searchSuggestion(final String s) {
        final Location sLocation = new Location("point A");
        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("Suggestion").orderByChild("search")
                .startAt(s)
                .endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Suggestion suggestion = snapshot.getValue(Suggestion.class);

                    assert suggestion != null;
                    assert fuser != null;


                    if (!suggestion.getSpublisher().equals(fuser.getUid())) {
                        String Lat2 = suggestion.getCoord1();
                        String Lng2 = suggestion.getCoord2();
                        Double Lat1 = Double.parseDouble(Lat2);
                        Double Lng1 = Double.parseDouble(Lng2);
                        sLocation.setLatitude(Lat1);
                        sLocation.setLongitude(Lng1);

                        SimpleDateFormat format1=new SimpleDateFormat("yyyy-MM-dd");
                        Date dt1= null;
                        try {
                            dt1 = format1.parse(suggestion.getDate());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        Date c = Calendar.getInstance().getTime();
                        String todayDate = format1.format(c);
                        Date dt = null;
                        try {
                            dt = format1.parse(todayDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        String dayOfSuggestion = (String) DateFormat.format("dd", dt1);
                        String dayToday = (String) DateFormat.format("dd", dt);

                        int dayOfSuggestionInt = Integer.parseInt(dayOfSuggestion);
                        int dayTodayInt = Integer.parseInt(dayToday);

                        int days = 31;

                        if (when.equals("Today")){
                            days = 0;
                        }

                        if (when.equals("In a week")){
                            days = 7;
                        }

                        if (locationData != null){

                        float distanceToSuggestion = locationData.distanceTo(sLocation)/1000;
                        if (distanceToSuggestion <= distanceFilter){
                            if (dayOfSuggestionInt - dayTodayInt < days)
                            { idList.add(suggestion.getSid());}
                            }}
                        }
                    //////here
                    }

                    readMarkers();

                //setRenderer();
                //mClusterManager.cluster();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }


    private void readSuggestion() {

        final Location sLocation = new Location("point A");
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Suggestion");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (searchView.getQuery().toString().equals("")) {
                    idList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Suggestion suggestion = snapshot.getValue(Suggestion.class);

                        assert suggestion != null;
                        if (showMyMarkers == null){
                            showMyMarkers.equals("do not show");
                        }
                        if (showMyMarkers.equals("do not show")){
                        if (!suggestion.getSpublisher().equals(firebaseUser.getUid())) {
                            String Lat2 = suggestion.getCoord1();
                            String Lng2 = suggestion.getCoord2();
                            Double Lat1 = Double.parseDouble(Lat2);
                            Double Lng1 = Double.parseDouble(Lng2);
                            sLocation.setLatitude(Lat1);
                            sLocation.setLongitude(Lng1);

                            SimpleDateFormat format1=new SimpleDateFormat("yyyy-MM-dd");
                            Date dt1= null;
                            try {
                                dt1 = format1.parse(suggestion.getDate());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            Date c = Calendar.getInstance().getTime();
                            String todayDate = format1.format(c);
                            Date dt = null;
                            try {
                                dt = format1.parse(todayDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if (dt != null && dt1 != null){
                            String dayOfSuggestion = (String) DateFormat.format("dd", dt1);
                            String dayToday = (String) DateFormat.format("dd", dt);

                            int dayOfSuggestionInt = Integer.parseInt(dayOfSuggestion);
                            int dayTodayInt = Integer.parseInt(dayToday);

                            int days = 31;

                            if (when.equals("Today")){
                                days = 0;
                            }

                            if (when.equals("In a week")){
                                days = 7;
                            }

                            if (locationData != null){

                                float distanceToSuggestion = locationData.distanceTo(sLocation)/1000;
                                if (distanceToSuggestion <= distanceFilter){
                                    if (dayOfSuggestionInt - dayTodayInt < days)
                                    { idList.add(suggestion.getSid());}
                                }} }}}
                        else{

                            String Lat2 = suggestion.getCoord1();
                            String Lng2 = suggestion.getCoord2();
                            Double Lat1 = Double.parseDouble(Lat2);
                            Double Lng1 = Double.parseDouble(Lng2);
                            sLocation.setLatitude(Lat1);
                            sLocation.setLongitude(Lng1);

                            SimpleDateFormat format1=new SimpleDateFormat("yyyy-MM-dd");
                            Date dt1= null;
                            try {
                                dt1 = format1.parse(suggestion.getDate());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            Date c = Calendar.getInstance().getTime();
                            String todayDate = format1.format(c);
                            Date dt = null;
                            try {
                                dt = format1.parse(todayDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if (dt != null && dt1 != null){
                            String dayOfSuggestion = (String) DateFormat.format("dd", dt1);
                            String dayToday = (String) DateFormat.format("dd", dt);

                            int dayOfSuggestionInt = Integer.parseInt(dayOfSuggestion);
                            int dayTodayInt = Integer.parseInt(dayToday);

                            int days = 31;

                            if (when.equals("Today")){
                                days = 0;
                            }

                            if (when.equals("In a week")){
                                days = 7;
                            }

                            if (locationData != null){

                                float distanceToSuggestion = locationData.distanceTo(sLocation)/1000;
                                if (distanceToSuggestion <= distanceFilter){
                                    if (dayOfSuggestionInt - dayTodayInt < days)
                                    { idList.add(suggestion.getSid());}
                                }}}
                        }
                    }

                    readMarkers();
                    //setRenderer();
                    //mClusterManager.cluster();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readCertainSuggestion(final String sid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Suggestion");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    mSuggestion.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Suggestion suggestion = snapshot.getValue(Suggestion.class);

                        if (suggestion.getSid().equals(sid)) {
                            mSuggestion.add(suggestion);

                        }

                    }

                    initAdapter(getContext(), mSuggestion);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void onBackPressed() {
        if (mBottomSheetBehavior.getState() == (BottomSheetBehavior.STATE_EXPANDED)){
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        b=0;
            sendBottomSheetState(b);}

    }

    private void initAdapter(Context context, List<Suggestion> mSuggestion){
        suggestionAdapter = new SuggestionMapAdapter(context, mSuggestion, this);
        recyclerView.setAdapter(suggestionAdapter);
    }

    private void setRenderer() {
        MarkerClusterRenderer clusterRenderer = new MarkerClusterRenderer(mContext, mMap, mClusterManager);
        clusterRenderer.setMinClusterSize(3);
        mClusterManager.setRenderer(clusterRenderer);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        menu.findItem(R.id.logout).setVisible(false);
        menu.findItem(R.id.chats_icon).setVisible(false);
        menu.findItem(R.id.event_icon).setVisible(false);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();//(SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    Log.i("onQueryTextChange", newText);
                    searchSuggestion(newText.toLowerCase());
                    clicks = 0;
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    b=0;
                    sendBottomSheetState(b);

                    return true;
                }
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Log.i("onQueryTextSubmit", query);

                    return true;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_search)
        {
            return false;
        }

        if(id == R.id.filter_icon)
        {
            ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new FilterMapFragment()).commit();
        }

        searchView.setOnQueryTextListener(queryTextListener);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSuggestionClicked(int position){
        final String selectedSuggestion = mSuggestion.get(position).getSid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Suggestion");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Suggestion suggestion = snapshot.getValue(Suggestion.class);
                    assert suggestion != null;
                    if (suggestion.getSid().equals(selectedSuggestion)){
                        String cordinate1 = suggestion.getCoord1();
                        String cordinate2 = suggestion.getCoord2();
                        Double l1 = Double.parseDouble(cordinate1);
                        Double l2 = Double.parseDouble(cordinate2);

                        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(l1, l2)).zoom(16).build();

                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
