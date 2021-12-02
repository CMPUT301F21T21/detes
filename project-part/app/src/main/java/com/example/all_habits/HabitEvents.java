package com.example.all_habits;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
/**
 * Habit Events: camera, comments, location that are accessed after the habit has been completed
 */
public class HabitEvents extends AppCompatActivity implements OnMapReadyCallback {

    final long ONE_MEGABYTE = 1024 * 1024;

    //initialize
    private TextView commentEditText;
    private ImageView habitEventImage;
    private Button saveCommentButton;
    private Button deletePhotoButton;
    private Button deleteCommentButton;
    private Button habitEventImageButton;
    private Button habitEventLocationButton;
    public String photoName;
    private String habitId;
    private String commentString;
    private MapView habitEventLocation;
    private GoogleMap gMap;

    ImageView backButton;

    private ArrayAdapter<Comment> commentAdapter;

    FirebaseFirestore db;
    private FirebaseUser currentFireBaseUser;
    private CollectionReference habitReference; // collection of selected habit
    private CollectionReference userCollectionReference; // collection of signed in user
    private DocumentReference documentRef;
    private StorageReference storageRef;
    private StorageReference imageRef;
    private StorageReference resetRef;

    private int habitNum;

    //When activity is returned to. checks if the habit picture has been changed.
    @Override
    protected void onRestart() {
        super.onRestart();
        db.collection(currentFireBaseUser.getUid()).document(habitId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    photoName = task.getResult().getString("optionalPhoto");
                    if(photoName != "") {
                        storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://projecthabits.appspot.com");
                        resetRef = storageRef.child(photoName);
                        resetRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                habitEventImage.setImageBitmap(bmp);
                            }
                        });
                    }
                }
            }
        });
        //habitEventLocation.postInvalidate();
        habitEventLocation.getMapAsync(this);


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.habit_events);

        //getting information of which habit has been ended
        Intent intent = getIntent();
        habitNum = intent.getIntExtra("habitNum", 1); // gets the habit that was clicked on

        //firestore access
        db = FirebaseFirestore.getInstance();
        currentFireBaseUser = FirebaseAuth.getInstance().getCurrentUser();
        CollectionReference collectionReference = db.collection(currentFireBaseUser.getUid().toString());
        storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://projecthabits.appspot.com");

        //userCollectionReference = db.collection(currentFireBaseUser.getUid());

        deleteCommentButton = findViewById( R.id.delete_comment );
        deletePhotoButton = findViewById( R.id.delete_photo );
        commentEditText = findViewById(R.id.commentEditText);
        saveCommentButton = findViewById(R.id.saveCommentButton);
        habitEventImageButton = findViewById(R.id.habitEventImageButton);
        habitEventImage = findViewById(R.id.habitEventImage);
        habitEventLocationButton = findViewById(R.id.habitEventLocationButton);

        habitEventLocation = findViewById(R.id.habitEventLocation);
        habitEventLocation.onCreate(savedInstanceState);
        habitEventLocation.getMapAsync(this);


        backButton = findViewById(R.id.displayBackButton);

        //adding a comment once the habit has been ended
        Query findHabit = db.collection(currentFireBaseUser.getUid()).whereEqualTo("habitNum", habitNum).limit(1);
        findHabit.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                habitId = document.getId();
                                documentRef = db.collection(currentFireBaseUser.getUid()).document(habitId);
                                commentString = document.getString("optionalComment");
                                photoName = document.getString("optionalPhoto");

                                // if the comment string is not empty
                                if (commentString != null) {
                                    if (!commentString.equals("")) {
                                        commentEditText.setText(document.getString("comment"));
                                    }
                                }
                            }
                        }

                        //returns to previous page
                        backButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                finish();
                            }
                        });

                        //saves comment
                        saveCommentButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (commentEditText.getText().toString().length() > 20) {
                                    Toast.makeText(getApplicationContext(), "The comment has to be under 20 characters long.", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                documentRef.update("optionalComment", commentEditText.getText().toString());
                                Toast.makeText(getApplicationContext(), "Your comment has been saved", Toast.LENGTH_SHORT).show();
                            }
                        });

                        if(commentString != null){
                            commentEditText.setText( commentString );
                        }

                        //deletes comment
                        deleteCommentButton.setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(commentString == null){
                                    Toast.makeText(getApplicationContext(), "There is no comment to Delete", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    documentRef.update("optionalComment", null);
                                    commentEditText.setText( "" );
                                    Toast.makeText(getApplicationContext(), "Your comment has been deleted", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } );

                        //Starts CameraActivity.
                        habitEventImageButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getApplicationContext(), cameraActivity.class);
                                intent.putExtra("habitId", habitId);
                                intent.putExtra("photoName", photoName);
                                startActivity(intent);
                            }
                        });

                        //Starts CurrentLocation.
                        habitEventLocationButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getApplicationContext(), CurrentLocation.class);
                                intent.putExtra("habitId", habitId);
                                intent.putExtra("habitNum", habitNum);
                                startActivity(intent);


                            }
                        });

                        //If this habit has a picture, display it in the habitEventImage.
                        if (photoName != "") {
                            imageRef = storageRef.child(photoName);
                            imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    habitEventImage.setImageBitmap(bmp);
                                }
                            });
                        }

                        deletePhotoButton.setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(photoName == ""){
                                    Toast.makeText(getApplicationContext(), "There is no photograph to Delete", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    documentRef.update("optionalPhoto", "");
                                    habitEventImage.setVisibility( View.INVISIBLE );
                                    Toast.makeText(getApplicationContext(), "Your photograph has been deleted", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } );
                    }
        });
    }

    //continues where you left off for map location
    @Override
    protected void onResume() {
        super.onResume();
       habitEventLocation.onResume();


    }

    //begins map location
    @Override
    protected void onStart() {
        super.onStart();
        habitEventLocation.onStart();
    }

    //stops map location
    @Override
    protected void onStop() {
        super.onStop();
        habitEventLocation.onStop();
    }

    //pauses map location
    @Override
    protected void onPause() {
        habitEventLocation.onPause();
        super.onPause();
    }

    //destroys map location previously saved
    @Override
    protected void onDestroy() {
        habitEventLocation.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        habitEventLocation.onLowMemory();
    }

    //saves map location of choice for the habit
    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.clear();
        Query findHabit = db.collection(currentFireBaseUser.getUid()).whereEqualTo("habitNum", habitNum).limit(1);
        findHabit.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String habitId = document.getId();
                                Map<String, Double> locationData = (Map<String, Double>) document.get("optionalLocation");

                                if (locationData != null){
                                    LatLng latLng = new LatLng(locationData.get("latitude"), locationData.get("longitude"));
                                    MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Habit completed here");
                                    //markerOptions.draggable(true);
                                    googleMap.addMarker(markerOptions);
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                                }

                            }

                        }
                    }
                });
    }

}
