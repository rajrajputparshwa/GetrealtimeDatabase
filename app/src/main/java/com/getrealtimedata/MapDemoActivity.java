package com.getrealtimedata;

import android.*;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.firebase.geofire.GeoQueryEventListener;
import com.getrealtimedata.Adapter.DriverAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MapDemoActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private SupportMapFragment mapFragment;
    private GoogleMap map;
    Location mCurrentLocation;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    Location locationss;
    Context context = this;
    int driverliststart = 0;
    int radius = 20;
    Marker mk = null;
    Location location1 ;
    GoogleApiClient mGoogleApiClient;
    ArrayList<Customer> arrayList = new ArrayList<>();
    LatLng latlngOne;
    private static final int STORAGE_PERMISSION_CODE_LOCATION = 22;
    LocationManager locationManager;
    ArrayList<Message> array_list = new ArrayList<>();
    RecyclerView driverList;
    float bearing;
    DriverAdapter driverAdapter;

    private final static String KEY_LOCATION = "location";

    /*
     * Define a request code to send to Google Play services This code is
       returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_demo_activity);
        requestLOCAtion();

        driverList = findViewById(R.id.driverList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        driverList.setLayoutManager(layoutManager);


        driverAdapter = new DriverAdapter(context, array_list);
        driverList.setAdapter(driverAdapter);
        mFirebaseInstance = FirebaseDatabase.getInstance();

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }




       /* token = FirebaseInstanceId.getInstance().getToken();*/

        if (TextUtils.isEmpty(getResources().getString(R.string.google_maps_api_key))) {
            throw new IllegalStateException("You forgot to supply a Google Maps API key");
        }

        if (savedInstanceState != null && savedInstanceState.keySet().contains(KEY_LOCATION)) {
            // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
            // is not null.
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);


        }


        mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(final GoogleMap map) {


                    mFirebaseInstance = FirebaseDatabase.getInstance();

                    mFirebaseDatabase = mFirebaseInstance.getReference("cars");

                    mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
                        @SuppressLint("MissingPermission")
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {


                            //Get key cars


                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
/*

                                Double lat = Double.valueOf(childSnapshot.child("lat").getValue(Double.class));
                                Double log = Double.valueOf(childSnapshot.child("log").getValue(Double.class));
                                float bearing = Float.valueOf(childSnapshot.child("bearing").getValue(Float.class));
                                Log.e("Mainssssssssssssss", lat + " / " + log);

                                String myParentNode = childSnapshot.getKey();
                                Log.e("Key", "" + myParentNode);

                                Log.e("Counts", "" + childSnapshot.getChildrenCount());
                                markerCount++;
                                Log.e("MarkerCount", "" + markerCount);

                                location = new Location("");
                                location.setLatitude(lat);
                                location.setLongitude(log);
                                latlngOne = new LatLng(lat, log);

                                if (markerCount <= childSnapshot.getChildrenCount() + 1) {


                                    mk = map.addMarker(new MarkerOptions()
                                            .position(new LatLng(lat, log))
                                            .title("office").icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_car)));


                                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latlngOne, 18);
                                    map.animateCamera(cameraUpdate);
                                    map.setMyLocationEnabled(true);


                                    Customer customer = new Customer();
                                    customer.setLat(lat);
                                    customer.setLog(log);
                                    customer.setValue(myParentNode);
                                    customer.setMarker(mk);
                                    arrayList.add(customer);


                                    Log.e("First", "First");

                                } else {

                                    markerCount--;

                                    for (int i = 0; i <= childSnapshot.getChildrenCount(); i++) {


                                        if (myParentNode.equals(arrayList.get(i).getValue())) {

                                          String msg = "Updated Locations : " +
                                                    Double.toString(lat) + "," +
                                                    Double.toString(log);

                                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();


                                            animateMarker(location, arrayList.get(i).getMarker(), bearing);
                                            Log.e("Second", "Second");
                                        }
                                    }
                                }*/
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            });
        } else {
            Toast.makeText(this, "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
        }


    }

    public static void animateMarker(final Location destination, final Marker marker, final float Bearing) {
        if (marker != null) {
            final LatLng startPosition = marker.getPosition();
            final LatLng endPosition = new LatLng(destination.getLatitude(), destination.getLongitude());

            final float startRotation = marker.getRotation();

            final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.LinearFixed();
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(1000); // duration 1 second
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        float v = animation.getAnimatedFraction();
                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
                        marker.setPosition(newPosition);
                        marker.setRotation(computeRotation(v, startRotation, Bearing));
                        marker.setFlat(true);
                    } catch (Exception ex) {
                        // I don't care atm..
                    }
                }
            });

            valueAnimator.start();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
       /* LocationListener locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {

                getDriver(location, location.getBearing());
                Log.e("Laaaat", "" + location.getLongitude());

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };


        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);


*/

        final Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        location1 = location;
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(location.getLatitude(), location.getLongitude()))
                        .title("own").icon(BitmapDescriptorFactory.defaultMarker()));
            }
        });

        getDriver(location);


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    private interface LatLngInterpolator {
        LatLng interpolate(float fraction, LatLng a, LatLng b);

        class LinearFixed implements LatLngInterpolator {
            @Override
            public LatLng interpolate(float fraction, LatLng a, LatLng b) {
                double lat = (b.latitude - a.latitude) * fraction + a.latitude;
                double lngDelta = b.longitude - a.longitude;
                // Take the shortest path across the 180th meridian.
                if (Math.abs(lngDelta) > 180) {
                    lngDelta -= Math.signum(lngDelta) * 360;
                }
                double lng = lngDelta * fraction + a.longitude;
                return new LatLng(lat, lng);
            }
        }
    }


    private static float computeRotation(float fraction, float start, float end) {
        float normalizeEnd = end - start; // rotate start to 0
        float normalizedEndAbs = (normalizeEnd + 360) % 360;

        float direction = (normalizedEndAbs > 180) ? -1 : 1; // -1 = anticlockwise, 1 = clockwise
        float rotation;
        if (direction > 0) {
            rotation = normalizedEndAbs;
        } else {
            rotation = normalizedEndAbs - 360;
        }

        float result = fraction * rotation + start;
        return (result + 360) % 360;
    }


    private void getDriver(final Location location) {


        mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {


                    map = googleMap;


                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("cars").child("driverAvailable");

                    GeoFire geoFire = new GeoFire(reference);
                    GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(location.getLatitude(), location.getLongitude()), radius);
                    geoQuery.removeAllListeners();
                    geoQuery.addGeoQueryDataEventListener(new GeoQueryDataEventListener() {
                        @SuppressLint("MissingPermission")
                        @Override
                        public void onDataEntered(DataSnapshot childSnapshot, final GeoLocation location) {




                            Double lat = Double.valueOf(childSnapshot.child("l").child("0").getValue(Double.class));
                            Double log = Double.valueOf(childSnapshot.child("l").child("1").getValue(Double.class));
             /*   float bearing = Float.valueOf(childSnapshot.child("bearing").getValue(Float.class));*/


                            String myParentNode = childSnapshot.getKey();


                            locationss = new Location("");
                            locationss.setLatitude(lat);
                            locationss.setLongitude(log);
                            latlngOne = new LatLng(lat, log);



                            if (childSnapshot.getKey().equals("15")){

                                mk = map.addMarker(new MarkerOptions()
                                        .position(new LatLng(lat, log))
                                        .title("office").icon(BitmapDescriptorFactory.fromResource(R.mipmap.fourseater)));

                            } else if ((childSnapshot.getKey().equals("10"))){
                                mk = map.addMarker(new MarkerOptions()
                                        .position(new LatLng(lat, log))
                                        .title("office").icon(BitmapDescriptorFactory.fromResource(R.mipmap.sizseater)));

                            }

                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latlngOne, 18);
                            map.animateCamera(cameraUpdate);


                            Customer customer = new Customer();
                            customer.setLat(lat);
                            customer.setLog(log);
                            customer.setValue(myParentNode);
                            customer.setMarker(mk);
                            arrayList.add(customer);


                            driverAdapter = new DriverAdapter(context, array_list);
                            driverList.setAdapter(driverAdapter);
                            driverAdapter.notifyDataSetChanged();


                            FirebaseDatabase.getInstance().getReference("cars").child("trip").child("tripID1").child("msg").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    array_list.clear();
                                    for (DataSnapshot chiSnapshot : dataSnapshot.getChildren()) {


                                        Message message = new Message();
                                        message.setMsg("" + chiSnapshot.child("message").getValue());
                                        array_list.add(message);
                                        driverAdapter.notifyDataSetChanged();

                                    }
/*

                        Log.e("car", "" + dataSnapshot.child("car").getValue());

                        Log.e("carmodel", "" + dataSnapshot.child("carModel").getValue());

                        Log.e("detail", "" + dataSnapshot.child("car").getValue());
*/




/*
                        DriverList driverList = new DriverList();
                        driverList.setCar("" + dataSnapshot.child("car").getValue());
                        driverList.setCarModel("" + (dataSnapshot.child("carModel").getValue()));
                        driverList.setDetail("" + (dataSnapshot.child("detail").getValue()));*/



                        /*

                        Log.e("Value", dataSnapshot.getKey());


                        Log.e("ChildValue", " " + dataSnapshot.child("detail").getValue());


                        DriverList driverList = new DriverList();
                        driverList.setCar("" + dataSnapshot.child("car").getValue());
                        driverList.setCarmodel("" + (dataSnapshot.child("carModel").getValue()));
                        driverList.setDetail("" + (dataSnapshot.child("detail").getValue()));
                        array_list.add(driverList);

                        Log.e("car", "" + dataSnapshot.child("car").getValue());

                        Log.e("carmodel", "" + dataSnapshot.child("carModel").getValue());

                        Log.e("detail", "" + dataSnapshot.child("car").getValue());
*/
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });





/*

                Double lat = Double.valueOf(childSnapshot.child("lat").getValue(Double.class));
                Double log = Double.valueOf(childSnapshot.child("log").getValue(Double.class));
                float bearing = Float.valueOf(childSnapshot.child("bearing").getValue(Float.class));
                Log.e("Mainssssssssssssss", lat + " / " + log);

                String myParentNode = childSnapshot.getKey();
                Log.e("Key", "" + myParentNode);

                Log.e("Counts", "" + childSnapshot.getChildrenCount());
                markerCount++;
                Log.e("MarkerCount", "" + markerCount);

                locationss = new Location("");
                locationss.setLatitude(lat);
                locationss.setLongitude(log);
                latlngOne = new LatLng(lat, log);

                if (markerCount <= childSnapshot.getChildrenCount() + 1) {


                    mk = map.addMarker(new MarkerOptions()
                            .position(new LatLng(lat, log))
                            .title("office").icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_car)));


                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latlngOne, 18);
                    map.animateCamera(cameraUpdate);
                    map.setMyLocationEnabled(true);


                    Customer customer = new Customer();
                    customer.setLat(lat);
                    customer.setLog(log);
                    customer.setValue(myParentNode);
                    customer.setMarker(mk);
                    arrayList.add(customer);


                    Log.e("First", "First");

                } else {

                    markerCount--;

                    for (int i = 0; i <= childSnapshot.getChildrenCount(); i++) {


                        if (myParentNode.equals(arrayList.get(i).getValue())) {

                            String msg = "Updated Locations : " +
                                    Double.toString(lat) + "," +
                                    Double.toString(log);

                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();


                            animateMarker(locationss, arrayList.get(i).getMarker(), bearing);
                            Log.e("Second", "Second");
                        }
                    }
                }
*/
                        }

                        @Override
                        public void onDataExited(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onDataMoved(DataSnapshot dataSnapshot, GeoLocation location) {

                        }

                        @Override
                        public void onDataChanged(DataSnapshot dataSnapshot, GeoLocation location) {


                        }

                        @Override
                        public void onGeoQueryReady() {


                            live();


                        }

                        @Override
                        public void onGeoQueryError(DatabaseError error) {

                        }
                    });


                }
            });


        }
    }

    private void requestLOCAtion() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, STORAGE_PERMISSION_CODE_LOCATION);
    }


    @SuppressLint("MissingPermission")
    private void live() {


                mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
                if (mapFragment != null) {
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(GoogleMap googleMap) {


                            map = googleMap;


                            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("cars").child("driverAvailable");

                            GeoFire geoFire = new GeoFire(reference);
                            GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(location1.getLatitude(), location1.getLongitude()), radius);
                            geoQuery.removeAllListeners();
                            geoQuery.addGeoQueryDataEventListener(new GeoQueryDataEventListener() {
                                @SuppressLint("MissingPermission")
                                @Override
                                public void onDataEntered(final DataSnapshot childSnapshot, GeoLocation location) {



                                    FirebaseDatabase.getInstance().getReference("cars").child("driverDetail").child(childSnapshot.getKey()).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Double lat = Double.parseDouble("" + dataSnapshot.child("lat").getValue());
                                            Double log = Double.parseDouble("" + dataSnapshot.child("lng").getValue());
                                            bearing = Float.parseFloat(" " + dataSnapshot.child("bearing").getValue());
             /*   float bearing = Float.valueOf(childSnapshot.child("bearing").getValue(Float.class));*/

                                            String myParentNode = childSnapshot.getKey();

                                            locationss = new Location("");
                                            locationss.setLatitude(lat);
                                            locationss.setLongitude(log);
                                            latlngOne = new LatLng(lat, log);
                                            for (int i = 0; i < arrayList.size(); i++) {
                                                if (myParentNode.equals(arrayList.get(i).getValue())) {
                                                    animateMarker(locationss, arrayList.get(i).getMarker(), bearing);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });


                                    FirebaseDatabase.getInstance().getReference("cars").child("trip").child("tripID1").child("msg").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            array_list.clear();
                                            for (DataSnapshot chiSnapshot : dataSnapshot.getChildren()) {


                                                Message message = new Message();
                                                message.setMsg("" + chiSnapshot.child("message").getValue());
                                                array_list.add(message);
                                                driverAdapter.notifyDataSetChanged();

                                            }
/*

                        Log.e("car", "" + dataSnapshot.child("car").getValue());

                        Log.e("carmodel", "" + dataSnapshot.child("carModel").getValue());

                        Log.e("detail", "" + dataSnapshot.child("car").getValue());
*/




/*
                        DriverList driverList = new DriverList();
                        driverList.setCar("" + dataSnapshot.child("car").getValue());
                        driverList.setCarModel("" + (dataSnapshot.child("carModel").getValue()));
                        driverList.setDetail("" + (dataSnapshot.child("detail").getValue()));*/



                        /*

                        Log.e("Value", dataSnapshot.getKey());


                        Log.e("ChildValue", " " + dataSnapshot.child("detail").getValue());


                        DriverList driverList = new DriverList();
                        driverList.setCar("" + dataSnapshot.child("car").getValue());
                        driverList.setCarmodel("" + (dataSnapshot.child("carModel").getValue()));
                        driverList.setDetail("" + (dataSnapshot.child("detail").getValue()));
                        array_list.add(driverList);

                        Log.e("car", "" + dataSnapshot.child("car").getValue());

                        Log.e("carmodel", "" + dataSnapshot.child("carModel").getValue());

                        Log.e("detail", "" + dataSnapshot.child("car").getValue());
*/
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });





/*

                Double lat = Double.valueOf(childSnapshot.child("lat").getValue(Double.class));
                Double log = Double.valueOf(childSnapshot.child("log").getValue(Double.class));
                float bearing = Float.valueOf(childSnapshot.child("bearing").getValue(Float.class));
                Log.e("Mainssssssssssssss", lat + " / " + log);

                String myParentNode = childSnapshot.getKey();
                Log.e("Key", "" + myParentNode);

                Log.e("Counts", "" + childSnapshot.getChildrenCount());
                markerCount++;
                Log.e("MarkerCount", "" + markerCount);

                locationss = new Location("");
                locationss.setLatitude(lat);
                locationss.setLongitude(log);
                latlngOne = new LatLng(lat, log);

                if (markerCount <= childSnapshot.getChildrenCount() + 1) {


                    mk = map.addMarker(new MarkerOptions()
                            .position(new LatLng(lat, log))
                            .title("office").icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_car)));


                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latlngOne, 18);
                    map.animateCamera(cameraUpdate);
                    map.setMyLocationEnabled(true);


                    Customer customer = new Customer();
                    customer.setLat(lat);
                    customer.setLog(log);
                    customer.setValue(myParentNode);
                    customer.setMarker(mk);
                    arrayList.add(customer);


                    Log.e("First", "First");

                } else {

                    markerCount--;

                    for (int i = 0; i <= childSnapshot.getChildrenCount(); i++) {


                        if (myParentNode.equals(arrayList.get(i).getValue())) {

                            String msg = "Updated Locations : " +
                                    Double.toString(lat) + "," +
                                    Double.toString(log);

                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();


                            animateMarker(locationss, arrayList.get(i).getMarker(), bearing);
                            Log.e("Second", "Second");
                        }
                    }
                }
*/
                                }

                                @Override
                                public void onDataExited(DataSnapshot dataSnapshot) {

                                }

                                @Override
                                public void onDataMoved(DataSnapshot dataSnapshot, GeoLocation location) {

                                }

                                @Override
                                public void onDataChanged(DataSnapshot dataSnapshot, GeoLocation location) {


                                }

                                @Override
                                public void onGeoQueryReady() {

                                }

                                @Override
                                public void onGeoQueryError(DatabaseError error) {

                                }
                            });


                        }
                    });


                }

    }


}
