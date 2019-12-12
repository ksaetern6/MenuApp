package com.example.menuapp;

import android.os.Bundle;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Arrays;
import java.util.List;

public class aMapAct extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        String api_key = getString(R.string.places_api_key);
        Log.d("aMapActDebug", api_key);
        Places.initialize(getApplicationContext(), api_key);

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        String placeID = "ChIJpbu__Qong4ARmZLAvYTZ1h0";
        PlacesClient placesClient = Places.createClient(this);

        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
        FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeID,placeFields);

        placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
            @Override
            public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                Place place = fetchPlaceResponse.getPlace();
                Log.d("aMapActDebug", "Place found " + place.getLatLng());

                LatLng lat_long = place.getLatLng();

                // Add a marker in Sydney, Australia, and move the camera.
                LatLng restaurant = lat_long;
                Marker curMarker = mMap.addMarker(new MarkerOptions()
                        .position(restaurant)
                        .title(place.getName()));
                moveCamera(restaurant);
                curMarker.showInfoWindow();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ApiException) {
                    ApiException apiException = (ApiException) e;
                    int statusCode = apiException.getStatusCode();
                    // Handle error with given status code.
                    Log.e("aMapActDebug", "Place not found: " + e.getMessage());

                }
            }
        });

    }

    private void moveCamera(LatLng location) {

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,15));
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
    }

}
