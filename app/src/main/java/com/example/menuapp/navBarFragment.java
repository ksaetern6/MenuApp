package com.example.menuapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class navBarFragment extends Fragment {

    // method called in getItem()
    public static navBarFragment newInstance() {
        navBarFragment fragment = new navBarFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main , container, false);
        return view;
    }
}
