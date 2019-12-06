package com.example.menuapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Map;

public class imageDisplay extends AppCompatActivity {
    // allow read, write: if request.auth != null;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String FBDocRef = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);

        Intent intent = getIntent();
        FBDocRef = intent.getStringExtra("FBRef");
        printImages();
    }

    private void printImages() {
        Log.d("imageDisplay","PRINTIMAGES RUNNING");

        // Restaurants => Res.Name => menu/items => "documents"
        // OF4aus6Oz8q5VwzPkWdg
//        db.collection("Restaurants")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
        db.collection("Restaurants")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("imageDisplayDebug", document.getId() + " => " + document.getData());
                                Map<String,Object> dataMap = document.getData();
                                if (dataMap.containsKey("test")) {
                                    Object gsURLObject = dataMap.get("test");
                                    loadImageFromBucket(gsURLObject.toString());
                                }
                            }

                        }
                        else {
                            Log.d("imageDisplayDebug", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void loadImageFromBucket(String gsURL) {
        Log.d("imageDisplayDebug",gsURL);
        // init reference
        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference imageRef = storage.getReferenceFromUrl(gsURL);

        // Load bucket image
        final long ONE_MB = 1024 * 1024;
        imageRef.getBytes(ONE_MB).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                ImageView image = findViewById(R.id.fb_image);

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
