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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.menuapp.firebase.FBAuth;
import com.example.menuapp.models.commentModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.text.Line;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.grpc.Server;

public class imageDisplay extends AppCompatActivity {

    // allow read, write: if request.auth != null;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String FBDocRef = "";
    FirebaseUser user;

    //
    ImageView itemImage;

    // Scrolling
    RecyclerView recyclerView;
    List<commentModel> commentList;
    commentMain commentMain;

    // Add Review/Comment
    EditText commentField;
    ImageButton addCommentBtn;

    //Add Comment
    String userComment;
    String userDocId;
    long rating;
    boolean isThereAComment;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);

        recyclerView = findViewById(R.id.comment_section);
        commentField = findViewById(R.id.comment_add_comment_field);
        addCommentBtn = findViewById(R.id.comment_add_button);
        itemImage = findViewById(R.id.fb_image);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            commentField.setEnabled(false);
            addCommentBtn.setEnabled(false);
        }
        else {
            // init user's doc id
            userDocId = user.getUid();

        }
        // [START Listeners]

        // Adding Comment Button
        addCommentBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                userComment = commentField.getText().toString();

                if (commentField.isEnabled()) {

                    if (userComment.isEmpty()) {
                        Toast.makeText(imageDisplay.this, "Comment cannot be empty", Toast.LENGTH_SHORT).show();
                    } // if

                    else {


                        LayoutInflater inflater = (LayoutInflater) view.getContext()
                                .getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);

                        final View popupView = inflater.inflate(R.layout.activity_pop_up_view,null); // bad practice

                        // Height x Width Contraints
                        int width = LinearLayout.LayoutParams.MATCH_PARENT;
                        int height = LinearLayout.LayoutParams.MATCH_PARENT;

                        // make items outside of window inactive
                        boolean focusable = true;

                        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                        //set location of window
                        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

                        // Init elements of window
                        final RatingBar popUpRatingBar = popupView.findViewById(R.id.pop_up_rating_bar);

                        Button submitBtn = popupView.findViewById(R.id.pop_up_submit_btn);

                        submitBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                rating = (long) popUpRatingBar.getRating();
                                addComment();
                                finish();
                                startActivity(getIntent());
                            }
                        });

                        popupView.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                // Close when when clicked
                                popupWindow.dismiss();
                                return true;
                            }
                        });

                       } // else
                }

                else {
                    Toast.makeText(imageDisplay.this, "Log In to Comment", Toast.LENGTH_SHORT).show();
                }
            }
        }); //addComment listener

        itemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.document(FBDocRef).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Intent intent = new Intent(getApplicationContext(), RestaurantMap.class);

                        String itemTitle = documentSnapshot.get("name").toString();
                        String itemDesc = documentSnapshot.get("description").toString();
                        String resRef= documentSnapshot.get("res_ref").toString();

                        intent.putExtra("TITLE", itemTitle);
                        intent.putExtra("DESC", itemDesc);
                        intent.putExtra("RES_REF", resRef);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(imageDisplay.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });



        // [END Listeners]
        Intent intent = getIntent();
        FBDocRef = intent.getStringExtra("FBRef");
        printImage();
    }

    private void loadComments(boolean isThereAComment, commentModel comment) {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

        // set layout to recycleview
        recyclerView.setLayoutManager(layoutManager);

        // init comment list
        commentList = new ArrayList<>();

        // load comments from fb and display to view

        // ** if user has already commented, add to front of list
        if (isThereAComment) {
            // prepend to commentList
            commentList.add(comment);
        }


        String FBColRef = FBDocRef + "/comments";
        CollectionReference commentsRef = db.collection(FBColRef);
        commentsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        String comment_field = document.getString("comment");
                        String email = document.getString("email");

//                        Timestamp timestamp = (Timestamp) document.get("timestamp");
//                        if (timestamp == null) {
//                            Log.d("imageDisplayDebug", "timestamp is null");
//                            Log.d("imageDisplayDebug", document.getId());
//                        }
                        //String date = formatTimestamp(timestamp);

                        String uid = document.getString("uid");
                        String username = document.getString("username");

                        long rating = (long) document.get("rating");
                        commentModel comment = new commentModel(comment_field, uid, email, username, rating);
                        commentList.add(comment);

                        // setup commentMain
                        commentMain = new commentMain(getApplicationContext(), commentList);

                        // set Adapter to commentMain
                        recyclerView.setAdapter(commentMain);

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
                        loadImageFromBucket(document.getString("locationRef"));

                        // load comments
                        if (user != null){ checkIfComment(); }
                    }

                }
            });
        }
        catch (Exception e) {
            Toast.makeText(imageDisplay.this, "Invalid QR Code", Toast.LENGTH_LONG).show();
            finish();
        }

    }

    private void loadImageFromBucket(String gsURL) {
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

    private void addComment() {

        // Add comment to Restaurant item
        Map<String, Object> fbComment = new HashMap<>();
        fbComment.put("comment", userComment);
        fbComment.put("email", user.getEmail());
        fbComment.put("rating", rating);
        fbComment.put("uid", user.getUid());
        fbComment.put("username", user.getDisplayName());

        db.collection(FBDocRef + "/comments")
                .add(fbComment)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("imageDisplayDebug", "Comment Saved to " + FBDocRef);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("imageDisplayDebug", "Comment NOT Saved to Restaurant item!");
                    }
                });

        // Add comment to user profile
        fbComment.put("locationRef", FBDocRef);

        db.collection("Users/" + userDocId + "/total_comments")
                .add(fbComment)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("imageDisplayDebug", "Comment Saved to " + userDocId);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("imageDisplayDebug", "Comment NOT Saved to user profile");
                    }
                });

    }

    private void checkIfComment() {

        // Collection of all of user's comments
        CollectionReference totalCom = db.collection("Users/" + userDocId + "/total_comments");

        // query to heck if any of the comments have a reference to the current image
        Query commentQuery = totalCom.whereEqualTo("locationRef", FBDocRef);

        // checking for reference to image
        commentQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    commentModel comment = new commentModel();
                    // if there are no comments
                    if (task.getResult().size() == 0) {

                        isThereAComment = false;
                        //Log.d("imageDisplayDebug", "empty: " + task.getResult().toString());
                    }

                    // else there are comments
                    else {
                        isThereAComment = true;
                        for (QueryDocumentSnapshot doc : task.getResult()) {

                            // The user's comment
                            long usrRating = (long) doc.get("rating");

                            comment = new commentModel(
                                            doc.getString("comment"),
                                            doc.getString("uid"),
                                            doc.getString("email"),
                                            doc.getString("username"),
                                            usrRating);

                        }
                    }
                    // load all comments
                    if (isThereAComment) {
                        loadComments(isThereAComment, comment);
                    }
                }
            }
        });

    } // checkIfComment()

    private void updateComment() {

        //update Res.
        // update user
    }

}
