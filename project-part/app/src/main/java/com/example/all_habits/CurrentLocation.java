package com.example.all_habits;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Get the optional location for where the habit was completed
 */
public class CurrentLocation extends AppCompatActivity {

    FusedLocationProviderClient client;
    SupportMapFragment MapFragment;
    private LatLng latLng;
    private MarkerOptions markerOptions;

    Button uploadButton;
    private StorageReference storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_location);

        client = LocationServices.getFusedLocationProviderClient(this);
        MapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);

        // check that we have permission to access location
        if (ActivityCompat.checkSelfPermission(CurrentLocation.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            // if we do, get the current location
            @SuppressLint("MissingPermission")
            Task<Location> task = client.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        MapFragment.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(GoogleMap googleMap) {
                                latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                markerOptions = new MarkerOptions().position(latLng).title("Current location");
                                googleMap.addMarker(markerOptions);
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                            }
                        });
                        // upload the current location to firebase
                        Intent intent = getIntent();
                        String habitId = intent.getStringExtra("habitId");
                        uploadButton = findViewById(R.id.uploadLocationButton);
                        storage = FirebaseStorage.getInstance().getReference();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        FirebaseUser currentFireBaseUser = FirebaseAuth.getInstance().getCurrentUser();

                        uploadButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                locationCoordinate coordinate = new locationCoordinate(location.getLatitude(),location.getLongitude());
                                db.collection(currentFireBaseUser.getUid()).document(habitId).update("optionalLocation",coordinate);
                            }
                        });
                    }
                }
            });
        }else{
            ActivityCompat.requestPermissions(CurrentLocation.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }
}


