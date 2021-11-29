package com.example.all_habits;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.util.UUID;

/**
 * Access to camera to allow a photo to be taken and saved
 */
public class cameraActivity extends AppCompatActivity {


    //storage of the picture
    private StorageReference storage;
    private StorageReference imageRef;
    ActivityResultLauncher<String> getContent;
    ActivityResultLauncher<Intent> activityResultLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        //initializing
        Intent intent = getIntent();

        Button takePhoto = findViewById(R.id.takePhotoButton);
        Button gallery = findViewById(R.id.galleryButton);
        Button uploadButton = findViewById(R.id.uploadImageButton);
        ImageView habitPicture = findViewById(R.id.habitPicture);
        String habitId = intent.getStringExtra("habitId");
        String photoName = intent.getStringExtra("photoName");
        storage = FirebaseStorage.getInstance().getReference();

        //Allows the habit picture to be downloaded to the firestore storage.
        habitPicture.setDrawingCacheEnabled(true);
        habitPicture.buildDrawingCache();
        FirebaseFirestore db;
        FirebaseUser currentFireBaseUser;
        StorageReference storageRefUrl = FirebaseStorage.getInstance().getReferenceFromUrl("gs://projecthabits.appspot.com");

        // create an instance of the firestore
        db = FirebaseFirestore.getInstance();
        currentFireBaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final CollectionReference collectionReference = db.collection(currentFireBaseUser.getUid().toString());

        //know location of where to access picture

        if(photoName != null){
            imageRef = storageRefUrl.child(photoName);
            final long ONE_MEGABYTE = 1024 * 1024;
            imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    habitPicture.setImageBitmap(bmp);

                }
            });
        }
        //Sets the imageView to the gallery chosen picture.
        getContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                habitPicture.setImageURI(result);

            }
        });


        //Sets the imageView to the camera picture
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == RESULT_OK && result.getData() != null){
                    Bundle bundle = result.getData().getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    habitPicture.setImageBitmap(bitmap);
                }
            }
        });

        //Opens the camera application.
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(intent.resolveActivity(getPackageManager())!= null){
                    activityResultLauncher.launch(intent);
                }else{
                    Toast.makeText(getApplicationContext(),"There are no applications that support this action.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //opens the gallery application.
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getContent.launch("image/*");
            }
        });

        //Uploads the imageView picture to the firestore db.
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //If the imageView is empty, don't upload any image.
                if(habitPicture.getDrawable() == null){
                    Toast.makeText(getApplicationContext(), "There is no image to upload", Toast.LENGTH_SHORT).show();
                }else {

                    if(photoName != null){
                        imageRef.delete();
                    }
                    String randomName = UUID.randomUUID().toString();
                    StorageReference uploadRef = storage.child(randomName);
                    // Create a reference to 'images/mountains.jpg'
                    StorageReference storageImageRef = storage.child("images/" + randomName);

                    // While the file names are the same, the references point to different files
                    uploadRef.getName().equals(storageImageRef.getName());    // true
                    uploadRef.getPath().equals(storageImageRef.getPath());

                    //Converts imageView into bytes to send into firestore storage.
                    Bitmap habitPictureBitMap = ((BitmapDrawable) habitPicture.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    habitPictureBitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();
                    db.collection(currentFireBaseUser.getUid()).document(habitId).update("optionalPhoto",randomName);
                    UploadTask uploadTask = uploadRef.putBytes(data);
                    Toast.makeText(getApplicationContext(), "Uploading...", Toast.LENGTH_SHORT).show();

                    //Closes activity after file is finished uploading.
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getApplicationContext(), "Done~", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });

                    //Sends a message if file fails to upload.
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Failed to upload", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

}