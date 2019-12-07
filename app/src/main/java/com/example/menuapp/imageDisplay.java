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
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.menuapp.models.commentModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.text.Line;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Document;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class imageDisplay extends AppCompatActivity {
    // allow read, write: if request.auth != null;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String FBDocRef = "";

    // Scrolling
    RecyclerView recyclerView;
    List<commentModel> commentList;
    commentMain commentMain;

    // Add Review/Comment
    EditText addComment;
    ImageButton addCommentBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("imageDisplayDebug", "Is this running?");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);

        recyclerView = findViewById(R.id.comment_section);
        addComment = findViewById(R.id.comment_add);
        addCommentBtn = findViewById(R.id.comment_add_button);

        // [START Listeners]

//        addCommentBtn.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

        // [END Listeners]
        Intent intent = getIntent();
        FBDocRef = intent.getStringExtra("FBRef");
        printImage();
    }

    private void loadComments() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

        // set layout to recycleview
        recyclerView.setLayoutManager(layoutManager);

        // init comment list
        commentList = new ArrayList<>();

        // load comments from fb and display to view

        String FBColRef = FBDocRef + "/comments";
        CollectionReference commentsRef = db.collection(FBColRef);
        commentsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        String comment_field = document.getString("comment");
                        String email = document.getString("email");

                        Timestamp timestamp = (Timestamp) document.get("timestamp");
                        String date = formatTimestamp(timestamp);

                        String uid = document.getString("uid");
                        String username = document.getString("username");

                        long rating = (long) document.get("rating");
                        commentModel comment = new commentModel(comment_field,date,uid,email,username,rating);
                        commentList.add(comment);

                        // setup commentMain
                        commentMain = new commentMain(getApplicationContext(), commentList);

                        // set Adapter to commentMain
                        recyclerView.setAdapter(commentMain);

                        Log.d("imageDisplayDebug", document.getData().toString());
                    }
                }
            }
        });

    }

    private void printImage() {

        // if this fails finish activity
        try {
            DocumentReference imageRef = db.document(FBDocRef);
            Task<DocumentSnapshot> doc = imageRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        Log.d("imageDisplayDebug", document.getString("name"));
                        loadImageFromBucket(document.getString("locationRef"));

                        loadComments();
                    }

                }
            });
        }
        catch (Exception e) {
            Toast.makeText(imageDisplay.this, "Invalid QR Code", Toast.LENGTH_LONG).show();
            finish();
        }
//        DocumentReference imageRef = db.document(FBDocRef);
//        Task<DocumentSnapshot> doc = imageRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if(task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    Log.d("imageDisplayDebug", document.getString("name"));
//                    loadImageFromBucket(document.getString("locationRef"));
//
//                    loadComments();
//                }
//                else {
//                    Toast.makeText(imageDisplay.this, "Invalid QR Code", Toast.LENGTH_LONG).show();
//                    finish();
//                }
//
//            }
//        });
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

    private String formatTimestamp(Timestamp timestamp) {
        long epoch = timestamp.getSeconds();
        LocalDate date = Instant.ofEpochSecond(epoch).atZone(ZoneId.systemDefault()).toLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = date.format(formatter);

        return formattedDate;
    }

}
