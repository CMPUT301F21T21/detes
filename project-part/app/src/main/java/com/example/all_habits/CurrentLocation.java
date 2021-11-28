package com.example.all_habits;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Get the optional location for where the habit was completed
 */
public class CurrentLocation extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener {

    private GoogleMap mMap;

    FusedLocationProviderClient client;
    SupportMapFragment MapFragment;
    private LatLng latLng;
    private MarkerOptions markerOptions;

    Button uploadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_location);

        client = LocationServices.getFusedLocationProviderClient(this);
        MapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);

        assert MapFragment != null;
        MapFragment.getMapAsync(this);

        // check that we have permission to access location
        if (ActivityCompat.checkSelfPermission(CurrentLocation.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // if we do, get the current location
            onMapReady(mMap);
            mMap.setOnMarkerDragListener(this);
        } else {
            ActivityCompat.requestPermissions(CurrentLocation.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        @SuppressLint("MissingPermission")
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    markerOptions = new MarkerOptions().position(latLng).title("Current location");
                    markerOptions = new MarkerOptions().draggable(true);
                    mMap.addMarker(markerOptions);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                }
            }
        });
    }


    @Override
    public void onMarkerDragEnd(@NonNull Marker marker) {
        latLng = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);

        // upload the current location to firebase
        Intent intent = getIntent();
        String habitId = intent.getStringExtra("habitId");
        uploadButton = findViewById(R.id.uploadLocationButton);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentFireBaseUser = FirebaseAuth.getInstance().getCurrentUser();

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //locationCoordinate coordinate = new locationCoordinate(location.getLatitude(), location.getLongitude());
                db.collection(currentFireBaseUser.getUid()).document(habitId).update("optionalLocation", latLng);
            }
        });
    }

    @Override
    public void onMarkerDrag (@NonNull Marker marker){
    }

    @Override
    public void onMarkerDragStart (@NonNull Marker marker){
    }
}