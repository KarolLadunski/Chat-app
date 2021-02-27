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

        import java.text.ParseException;
        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Arrays;
        import java.util.Calendar;
        import java.util.Date;
        import java.util.HashMap;
        import java.util.List;

        import static com.android.volley.VolleyLog.TAG;


public class ChangeLocFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, OnMapReadyCallback {

    private GoogleMap mMap;
    private ClusterManager<MyItem> mClusterManager;
    private List<String> idList;

    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private SuggestionMapAdapter suggestionAdapter;
    private List<Suggestion> mSuggestion;
    private FusedLocationProviderClient mFusedLocationClient;

    private Location locationData;
    FirebaseUser fuser;
    int distanceFilter;
    String when;
    String CameraCordinate1 = null;
    String CameraCordinate2 = null;
    private Button ok, cancel;
    private ImageView addMarker;
    Double cc1 = null;
    Double cc2 = null;
    String sid, type;


    private static final long TIME_INTERVAL_GET_LOCATION = 1000 * 1000; // 1 Minute
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 5000 * 1000;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_change_loc, container, false);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        when = "In a month";
        distanceFilter = 10;

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);

        ok = rootView.findViewById(R.id.ok);
        cancel = rootView.findViewById(R.id.cancel_action);
        addMarker = rootView.findViewById(R.id.add_marker);

        mSuggestion = new ArrayList<>();
        idList = new ArrayList<>();

        if ((getArguments() != null))
        {
            CameraCordinate1 = getArguments().getString("CameraCord1");
            CameraCordinate2 = getArguments().getString("CameraCord2");
            sid = getArguments().getString("sid");
            type = getArguments().getString("type");
            cc1 = Double.parseDouble(CameraCordinate1);
            cc2 = Double.parseDouble(CameraCordinate2);

        }

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

                if (locationData != null){ //&& status.equals("online")) {

                        readSuggestion();


                        if (ActivityCompat.checkSelfPermission((Activity)mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED && ActivityCompat.
                                checkSelfPermission((Activity)mContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {

                            return;
                        }
                        mMap.setMyLocationEnabled(true);

                            LatLng point2 = new LatLng(cc1, cc2);
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(point2).zoom(16).build();
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    Log.i(TAG, "Coords 1:" + cc1 + "   2:" + cc2);


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

        if (!Places.isInitialized()) {
            Places.initialize(mContext, apiKey);
        }

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(mContext);

        // Initialize the AutocompleteSupportFragment.

        assert getFragmentManager() != null;
        final AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        assert autocompleteFragment != null;
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // TODO: Get info about the selected place.

                Log.i(TAG, "Clicked Place: " + place.getName() + "LatLng:" + place.getLatLng());
                CameraPosition cameraPosition1 = new CameraPosition.Builder().target(place.getLatLng()).zoom(16).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1));

            }

            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Double l1 = mMap.getCameraPosition().target.latitude;
                Double l2 = mMap.getCameraPosition().target.longitude;
                String coordl12 = l1.toString();
                String coordl22 = l2.toString();

                if (type.equals("Edit")){
                EditSuggestionFragment fragment = new EditSuggestionFragment();
                Bundle args = new Bundle();
                    args.putString("Cord1", coordl12);
                    args.putString("Cord2", coordl22);
                    args.putString("sid", sid);
                fragment.setArguments(args);
                assert getFragmentManager() != null;
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();}

                if (type.equals("New")){
                    Double l1S = mMap.getCameraPosition().target.latitude;
                    Double l2S = mMap.getCameraPosition().target.longitude;
                    String coordl12S = l1S.toString();
                    String coordl22S = l2S.toString();

                    SuggestionFragment fragment = new SuggestionFragment();
                    Bundle args = new Bundle();
                    args.putString("coord1", coordl12S);
                    args.putString("coord2", coordl22S);
                    fragment.setArguments(args);
                    assert getFragmentManager() != null;
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();

                }
            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //back to EditSuggestionFragment
            }
        });

    }

    public MarkerOptions getMarkerOptions (Double l1, Double l2, String title, int iconRes) {
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
                        assert suggestion != null;
                        if (suggestion.getSid().equals(id)){

                            String coordi1 = suggestion.getCoord1();
                            String coordi2 = suggestion.getCoord2();
                            Double l1 = Double.parseDouble(coordi1);
                            Double l2 = Double.parseDouble(coordi2);
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


    private void readSuggestion() {

        final Location sLocation = new Location("point A");
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Suggestion");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    idList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Suggestion suggestion = snapshot.getValue(Suggestion.class);

                        assert suggestion != null;
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
                                }} }
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

    private void setRenderer() {
        MarkerClusterRenderer clusterRenderer = new MarkerClusterRenderer(mContext, mMap, mClusterManager);
        clusterRenderer.setMinClusterSize(3);
        mClusterManager.setRenderer(clusterRenderer);

    }
}