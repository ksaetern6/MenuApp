package com.example.menuapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.menuapp.R;

public class profileInfo extends AppCompatActivity {

    CardView savedItems, savedRatings, savedComments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_info);


    }

}
