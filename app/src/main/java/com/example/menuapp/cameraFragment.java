package com.example.menuapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class cameraFragment extends Fragment {

    // method called in getItem()
    public static cameraFragment newInstance() {
        cameraFragment fragment = new cameraFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_navigation_view , container, false);
        return view;
    }
}
