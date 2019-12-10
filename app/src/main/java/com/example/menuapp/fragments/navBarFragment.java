package com.example.menuapp.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.menuapp.R;
import com.example.menuapp.aMapAct;
import com.example.menuapp.profileInfo;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class navBarFragment extends Fragment {

    private static final int RC_SIGN_IN = 123;
    // init firebase auth

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = null;
    View navBarView;
    ImageView profilePic;
    ImageButton profileBtn, mapsBtn, favBtn, logoutBtn;
    TextView username;

    // method called in getItem()
    public static navBarFragment newInstance() {
        navBarFragment fragment = new navBarFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        navBarView = inflater.inflate(R.layout.activity_navigation_view , container, false);

        //final TextView hello = navBarView.findViewById(R.id.hello);
        profilePic = navBarView.findViewById(R.id.profile_image);
        profileBtn = navBarView.findViewById(R.id.nav_profile_tile);
        mapsBtn = navBarView.findViewById(R.id.nav_maps_tile);
        favBtn = navBarView.findViewById(R.id.nav_fav_tile);
        logoutBtn = navBarView.findViewById(R.id.nav_log_out_tile);
        username = navBarView.findViewById(R.id.profile_username);

        if (!isLogged()) {
            // Start new activity?
            //createSignInIntent();
        }
        else {
            username.setText(user.getDisplayName());
        }


        /*
        OnClickListeners
         */
        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (user != null) {
                    // Go to Profile Page
                    Intent intent = new Intent(getContext(), profileInfo.class);
                    startActivity(intent);
                }
                else {
                    // Go to sign up page
                    createSignInIntent();
                    // Reload Activity
                }
            }
        });
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("MainActivity", "Log out button clicked");
                if (user != null) {
                    AuthUI.getInstance()
                        .signOut(getContext())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                user = null;
                                username.setText("Please Sign In");
                            }
                        });
                }
            }

        });

        mapsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), aMapAct.class);
                startActivity(intent);
            }
        });


        return navBarView;
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
    public void createSignInIntent() {
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("requestCode", String.valueOf(resultCode));

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (response.isNewUser()) {

                //get uid & add to fb
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                Map<String, Object> fbUser = new HashMap<>();
                fbUser.put("display name", user.getDisplayName());
                fbUser.put("uid", user.getUid());
                fbUser.put("email", user.getEmail());
                fbUser.put("photoUrl", "");

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("Users")
                        .add(fbUser)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("navBarFragmentDebug", documentReference.getId());
                                updateUI();
                            }
                        });
            }

            if (resultCode == RESULT_OK) {
                // Successfully signed

            }
            else {
                //Sign in failed
                Toast.makeText(getActivity(), "Unsuccessful", Toast.LENGTH_SHORT);
            }
        }
    }

    private void updateUI() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        username.setText(user.getDisplayName());
    }
}
