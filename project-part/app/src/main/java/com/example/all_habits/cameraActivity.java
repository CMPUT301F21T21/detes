package com.example.all_habits;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.UUID;

public class cameraActivity extends AppCompatActivity {


    private StorageReference storage;
    ActivityResultLauncher<String> getContent;
    ActivityResultLauncher<Intent> activityResultLauncher;
    public Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        Button takePhoto = findViewById(R.id.takePhotoButton);
        Button gallery = findViewById(R.id.galleryButton);
        Button uploadButton = findViewById(R.id.uploadImageButton);
        ImageView habitPicture = findViewById(R.id.habitPicture);
        storage = FirebaseStorage.getInstance().getReference();


        habitPicture.setDrawingCacheEnabled(true);
        habitPicture.buildDrawingCache();

        getContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                habitPicture.setImageURI(result);

            }
        });

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

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getContent.launch("image/*");
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(habitPicture.getDrawable() == null){
                    Toast.makeText(getApplicationContext(), "There is no image to upload", Toast.LENGTH_SHORT).show();
                }else {
                    String randomName = UUID.randomUUID().toString();
                    StorageReference storageRef = storage.child(randomName);
                    // Create a reference to 'images/mountains.jpg'
                    StorageReference storageImageRef = storage.child("images/" + randomName);

                    // While the file names are the same, the references point to different files
                    storageRef.getName().equals(storageImageRef.getName());    // true
                    storageRef.getPath().equals(storageImageRef.getPath());

                    Bitmap habitPictureBitMap = ((BitmapDrawable) habitPicture.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    habitPictureBitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();

                    UploadTask uploadTask = storageRef.putBytes(data);
                    finish();
                }
            }
        });
    }

}