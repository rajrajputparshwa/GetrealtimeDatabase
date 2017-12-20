package com.getrealtimedata;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapDemoActivity extends AppCompatActivity {

    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private LocationRequest mLocationRequest;
    Location mCurrentLocation;
    private long UPDATE_INTERVAL = 60000;  /* 60 secs */
    private long FASTEST_INTERVAL = 1000; /* 5 secs */
    private int markerCount;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    public String userId;
    Context context = this;
    String token;
    Marker mk = null;
    LatLng latlngOne;
    String value;
    Button recenter, speed;
    Location location;
    int center = 0;
    Handler handler;
    GoogleMap getMap;

    @Override
    public void onBackPressed() {

        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
    }

    private final static String KEY_LOCATION = "location";

    /*
     * Define a request code to send to Google Play services This code is
     * returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_demo_activity);
        markerCount = 0;
        recenter = findViewById(R.id.recenter);
        speed = findViewById(R.id.speed);

        recenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latlngOne, 17);
                getMap.animateCamera(cameraUpdate);

                center = 1;
            }
        });


        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            // and get whatever type user account id is

            value = extras.getString("gps");

        }


        mFirebaseInstance = FirebaseDatabase.getInstance();




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

                    getMap = map;


                    mFirebaseInstance = FirebaseDatabase.getInstance();


                    mFirebaseDatabase = mFirebaseInstance.getReference("cars");

                    mFirebaseDatabase.child(value).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            final Customer customer = dataSnapshot.getValue(Customer.class);



                                    location = new Location("");
                                    location.setLatitude(customer.lat);
                                    location.setLongitude(customer.log);
                                    final float bearing = customer.bearing;
                                    latlngOne = new LatLng(customer.lat, customer.log);


                                    if (markerCount == 1) {

                           /*     String msg = "Updated Locations : " +
                                        Double.toString(customer.lat) + "," +
                                        Double.toString(customer.log);

                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();*/

                             /*   CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latlngOne, 18);
                                map.animateCamera(cameraUpdate);
*/

                                        Log.e("ZoomControl", "" + map.getCameraPosition().zoom);

                                        if (center == 1 || map.getCameraPosition().zoom == 17) {


                                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latlngOne, 17);
                                            map.animateCamera(cameraUpdate);
                                            map.setMapStyle(
                                                    MapStyleOptions.loadRawResourceStyle(
                                                            context, R.raw.map_style));



                                            animateMarker(location, mk, bearing);
                                            speed.setText("" + customer.speed);
                                            Log.e("Speed", " " + customer.speed);

                                            center = 0;


                                            Log.e("Handler", "Zoom");


                                        } else if (map.getCameraPosition().zoom < 17 || map.getCameraPosition().zoom > 17) {


                                            animateMarker(location, mk, bearing);
                                            speed.setText("" + customer.speed);
                                            Log.e("Speed", " " + customer.speed);

                                            Log.e("UnZoom", "UNZoom");
                                        }


                                    } else if (markerCount == 0) {


                                        mk = map.addMarker(new MarkerOptions()
                                                .position(new LatLng(customer.lat, customer.log))
                                                .title("office").icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));


                                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latlngOne, 17);
                                        map.animateCamera(cameraUpdate);
                                        map.setMapStyle(
                                                MapStyleOptions.loadRawResourceStyle(
                                                        context, R.raw.map_style));



                                /*map.setMapType(GoogleMap.MAP_TYPE_HYBRID);*/

                                        markerCount = 1;


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


    public static void animateMarker(final Location destination, final Marker marker, final Float Bearing) {
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

}
