package com.example.menuapp.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FBAuth {

    FirebaseUser user;

    String displayName, email, photoUrl;

    public FBAuth() {

    }

    public FirebaseUser getCurrentuser() {
        user = FirebaseAuth.getInstance().getCurrentUser();

        return user;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }
}
