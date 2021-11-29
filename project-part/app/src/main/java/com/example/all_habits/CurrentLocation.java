package com.example.all_habits;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

/**
 * Get the optional location for where the habit was completed
 */
public class CurrentLocation extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;

    FusedLocationProviderClient client;
    SupportMapFragment MapFragment;
    private LatLng latLng;
    private MarkerOptions markerOptions;
    private LocationRequest locationRequest;
    private Button uploadButton;
    private int habitNum;
    private  FirebaseFirestore db;
    private FirebaseUser currentFireBaseUser;
    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_location);

        client = LocationServices.getFusedLocationProviderClient(CurrentLocation.this);

        MapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);

        assert MapFragment != null;
        MapFragment.getMapAsync(this);

        // check that we have permission to access location
        if (ActivityCompat.checkSelfPermission(CurrentLocation.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // if we do, get the current location
            if (mMap != null) {
                onMapReady(mMap);

            }
        } else {
            ActivityCompat.requestPermissions(CurrentLocation.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

        // upload the current location to firebase
        Intent intent = getIntent();
        String habitId = intent.getStringExtra("habitId");
        habitNum = intent.getIntExtra("habitNum", 1);
        uploadButton = findViewById(R.id.uploadLocationButton);
        db = FirebaseFirestore.getInstance();
        currentFireBaseUser = FirebaseAuth.getInstance().getCurrentUser();

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //locationCoordinate coordinate = new locationCoordinate(location.getLatitude(), location.getLongitude());
                db.collection(currentFireBaseUser.getUid()).document(habitId).update("optionalLocation", latLng);
                Toast.makeText(getApplicationContext(), "Your location has been saved", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerDragListener(this);

        Query findHabit = db.collection(currentFireBaseUser.getUid()).whereEqualTo("habitNum", habitNum).limit(1);
        findHabit.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String habitId = document.getId();

                                Map<String, Double> locationData = (Map<String, Double>) document.get("optionalLocation");
                                if (locationData != null){
                                    latLng = new LatLng(locationData.get("latitude"), locationData.get("longitude"));
                                    markerOptions = new MarkerOptions().position(latLng).title("Current location");
                                    markerOptions.draggable(true);
                                    mMap.addMarker(markerOptions);
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                                }
                                else{
                                    mMap.setMyLocationEnabled(true);
                                    mMap.getUiSettings().setMyLocationButtonEnabled(true);
                                }

                            }

                        }
                    }
                });
    }


    @Override
    public void onMarkerDragEnd(@NonNull Marker marker) {
        latLng = marker.getPosition();
        marker.setTitle(String.valueOf(latLng.latitude) + "," +String.valueOf(latLng.longitude));
    }

    @Override
    public void onMarkerDrag (@NonNull Marker marker){
        latLng = marker.getPosition();
        marker.setTitle(String.valueOf(latLng.latitude) + "," +String.valueOf(latLng.longitude));
    }

    @Override
    public void onMarkerDragStart (@NonNull Marker marker){
        latLng = marker.getPosition();

    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        mMap.addMarker(new MarkerOptions().position(latLng).draggable(true).title(String.valueOf(latLng.latitude) + "," +String.valueOf(latLng.longitude)));

    }

}