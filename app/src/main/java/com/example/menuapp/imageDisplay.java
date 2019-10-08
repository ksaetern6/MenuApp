package com.example.menuapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class imageDisplay extends AppCompatActivity {
    // allow read, write: if request.auth != null;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);

        //Intent intent = getIntent();
        printImages();
    }

    private void printImages() {
        Log.d("imageDisplay","PRINTIMAGES RUNNING");

        StorageReference sandwichRef = storage.getReferenceFromUrl(
                "gs://menuapp-f3764.appspot.com/sandwich.jpg");
        final long ONE_MB = 1024 * 1024;
        sandwichRef.getBytes(ONE_MB).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.d("imageDisplay","This is printing!");
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                ImageView image = findViewById(R.id.imageView1);

                image.setImageBitmap(bmp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast toast = Toast.makeText(imageDisplay.this, e.getMessage(), Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

}
