package com.example.menuapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import java.util.Arrays;
import java.util.List;

public class loginMain extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    private Button btnLogin, btnSignUp, btnForgotPass;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main);

//        btnLogin = (Button) findViewById(R.id.login_button);
//        btnSignUp = (Button) findViewById(R.id.sign_up_button);
//        btnForgotPass = (Button) findViewById(R.id.forgot_pass_button);

        createSignInIntent();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                //Successfully signed in
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT);
            }
            else {
                //Sign in failed
                Toast.makeText(this, "Unsuccessful", Toast.LENGTH_SHORT);
            }
        }
    }
}
