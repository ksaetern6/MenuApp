package com.example.menuapp.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.menuapp.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class navBarFragment extends Fragment {

    private static final int RC_SIGN_IN = 123;
    // init firebase auth
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = null;
    Button signOutBtn;

    // method called in getItem()
    public static navBarFragment newInstance() {
        navBarFragment fragment = new navBarFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_navigation_view , container, false);

//        createSignInIntent();
        if (!isLogged()) {
            createSignInIntent();
        }

        signOutBtn = view.findViewById(R.id.sign_out_button);
        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthUI.getInstance()
                        .signOut(getContext())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //Toast.makeText(getActivity(), "Sign Out Successful", Toast.LENGTH_LONG);
                            }
                        });
            }
        });

        return view;
    }
    private boolean isLogged() {
        user = mAuth.getCurrentUser();
        if (user != null) {
            // User is signed
            return true;
        }
        return false;
    }

    private void getProfileInfo() {
        if (user == null) {
            return;
        }
        String name = user.getDisplayName();
        String email = user.getEmail();
        Uri photoUrl = user.getPhotoUrl();

        String uid = user.getUid();
    }

    /*
@name:
@desc: Sign in intent with sign in methods
*/
    public void createSignInIntent(){
        //Choose auth providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        //Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN
        );
    } //createSignInIntent

    /*
    @name:
    @desc: Sign in intent with sign in methods
    */
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == RC_SIGN_IN) {
//            IdpResponse response = IdpResponse.fromResultIntent(data);
//
//            if (resultCode == RESULT_OK) {
//                //Successfully signed in
//                user = mAuth.getCurrentUser();
//                Toast.makeText(getActivity(), "Login Successful", Toast.LENGTH_SHORT);
//            }
//            else {
//                //Sign in failed
//                Toast.makeText(getActivity(), "Unsuccessful", Toast.LENGTH_SHORT);
//            }
//        }
//    }
}
