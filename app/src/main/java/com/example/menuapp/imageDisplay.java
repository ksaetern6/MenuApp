package com.example.menuapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.menuapp.models.commentModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.text.Line;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class imageDisplay extends AppCompatActivity {
    // allow read, write: if request.auth != null;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String FBDocRef = "";

    // Scrolling
    RecyclerView recyclerView;
    List<commentModel> commentList;
    commentMain commentMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);

        recyclerView = findViewById(R.id.comment_section);

        Intent intent = getIntent();
        FBDocRef = intent.getStringExtra("FBRef");
        printImages();
        loadComments();
    }

    private void loadComments() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

        // set layout to recycleview
        recyclerView.setLayoutManager(layoutManager);

        // init comment list
        commentList = new ArrayList<>();

        // load comments from fb and display to view
        for(int x=0; x < 10; x++) {
            String commentid = String.format("c0%s", String.valueOf(x));
            String comment_field = String.format("test%s", String.valueOf(x));
            String timestamp = String.valueOf(System.currentTimeMillis());
            String uid = String.format("u0%s", String.valueOf(x));
            String email = String.format("0%s@example.com", String.valueOf(x));
            String username = String.format("01%suser", String.valueOf(x));

            Log.d("imageDisplayDebug", commentid);

            commentModel comment = new commentModel(commentid,comment_field,timestamp,uid,email,username);
            commentList.add(comment);

            // setup commentMain
            commentMain = new commentMain(getApplicationContext(), commentList);

            //set commentMain
            recyclerView.setAdapter(commentMain);
        }
    }

    private void printImages() {

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
