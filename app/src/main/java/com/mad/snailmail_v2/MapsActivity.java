package com.mad.snailmail_v2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

// Passive view: https://martinfowler.com/eaaDev/PassiveScreen.html

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

// Google console: https://console.cloud.google.com/home/dashboard?project=location-practic-1527168180684

// http://blog.teamtreehouse.com/beginners-guide-location-android

// https://google-developer-training.gitbooks.io/android-developer-advanced-course-practicals/unit-4-add-geo-features-to-your-apps/lesson-7-location/7-1-p-use-the-device-location/7-1-p-use-the-device-location.html?q=#chapterstart

// https://code.tutsplus.com/tutorials/how-to-work-with-geofences-on-android--cms-26639

// https://developers.google.com/maps/documentation/android-sdk/map

// add functionality to add a geofence at a location
// if user clicks within that geofence, it is triggered (attempting to mimic someone being in it)

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private FusedLocationProviderClient mFusedLocationClient;
    private GeofencingClient mGeofencingClient;
    private ArrayList<Geofence> mGeofenceList;
    private GoogleMap mMap;
    private Marker mMarker;

    private static final double SYDNEY_LATITUDE = -33.852;
    private static final double SYDNEY_LONGITUDE = 151.211;
    // private static final LatLng LAT_LNG_SYDNEY = new LatLng(SYDNEY_LATITUDE, SYDNEY_LONGITUDE);
    private static final String TAG = "MainActivity";
    private static final int REQUEST_LOCATION_PERMISSIONS = 2001;

    private TextView mLatitudeTV;
    private TextView mLongitudeTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mGeofencingClient = LocationServices.getGeofencingClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsActivity.this);

        mLatitudeTV = (TextView) findViewById(R.id.latitude);
        mLongitudeTV = (TextView) findViewById(R.id.longitude);

        Button currentLocationButton = (Button) findViewById(R.id.current_location);
        currentLocationButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MapsActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) { /* request permissions */
                    Log.i(TAG, "onCreate: permission was not granted");
                    ActivityCompat.requestPermissions(MapsActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_LOCATION_PERMISSIONS);

                }

                mFusedLocationClient.getLastLocation().addOnSuccessListener(
                        new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {

                                if (location != null) {
                                    Log.i(TAG, "onSuccess: location is not null");
                                    mLatitudeTV.setText(location.getLatitude() + "");
                                    mLongitudeTV.setText(location.getLongitude() + "");
                                } else {
                                    Log.i(TAG, "onSuccess: location is null");
                                    mLatitudeTV.setText("null");
                                }
                            }
                        });

            }
        });
        /*
        mGeofenceList.add(new Geofence.Builder().setRequestId("first")
                .setCircularRegion()
                .build());
        */
    }

    private GoogleMap.OnMapLongClickListener getLongClickListener() {
        return new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                // remove any existing marker first
                // maybe add favourite places
                mMarker = mMap.addMarker(new MarkerOptions().position(latLng)
                        .title("User marker"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                Log.i(TAG, "onMapLongClick: mMarker latitude :" + mMarker.getPosition().latitude + ", mMarker longitude: " + mMarker.getPosition().longitude);

            }
        };
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSIONS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "onRequestPermissionsResult: permission granted");
                } else {
                    Log.i(TAG, "onRequestPermissionsResult: permission still not granted");
                }
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setOnMapLongClickListener(getLongClickListener());
        // may need to add marker click listener
        mMap.setOnMarkerClickListener(getMarkerClickListener());
        // marker initially on sydney
        LatLng sydney = new LatLng(SYDNEY_LATITUDE, SYDNEY_LONGITUDE);
        MarkerOptions options = new MarkerOptions().position(sydney)
                .title("Marker in Sydney");

        Log.i(TAG, "onMapReady: sydney latitude :" + sydney.latitude + ", sydney.longitude: " + sydney.longitude);
        mMarker = mMap.addMarker(options);
        Log.i(TAG, "onMapReady: mMarker latitude :" + mMarker.getPosition().latitude + ", mMarker longitude: " + mMarker.getPosition().longitude);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }

    private GoogleMap.OnMarkerClickListener getMarkerClickListener() {
        return new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.i(TAG, "onMarkerClick: " + marker.getPosition().latitude);
                mLatitudeTV.setText(marker.getPosition().latitude + "");
                mLongitudeTV.setText(marker.getPosition().longitude + "");
                // check marker and add either address or coordinates to geofence

                return false;
            }
        };
    }
}

