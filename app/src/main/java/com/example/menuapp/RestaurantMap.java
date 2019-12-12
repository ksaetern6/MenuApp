package com.example.menuapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class RestaurantMap extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker myMarker;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    private String itemTitle, itemDesc, resRef;

    TextView titleText, descText;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_map);

        titleText = findViewById(R.id.restaurant_map_title);
        descText = findViewById(R.id.restaurant_map_description);

        itemTitle = getIntent().getStringExtra("TITLE");
        itemDesc = getIntent().getStringExtra("DESC");
        resRef = getIntent().getStringExtra("RES_REF");

        titleText.setText(itemTitle);
        descText.setText(itemDesc);

        SupportMapFragment resMap = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.restaurant_map_map);

        String api_key = getString(R.string.places_api_key);
        Places.initialize(getApplicationContext(), api_key);

        resMap.getMapAsync(this);
     }
     @Override
     public void onMapReady(GoogleMap googleMap) {
         mMap = googleMap;

         db.document(resRef).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
             @Override
             public void onSuccess(DocumentSnapshot documentSnapshot) {
                 String placeID = documentSnapshot.get("place_id").toString();
                 PlacesClient placesClient = Places.createClient(getApplicationContext());

                 List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
                 FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeID,placeFields);

                 placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                     @Override
                     public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                         Place place = fetchPlaceResponse.getPlace();
                         Log.d("aMapActDebug", "Place found " + place.getLatLng());

                         LatLng lat_long = place.getLatLng();

                         // Add a and move camera
                         LatLng restaurant = lat_long;
                         myMarker = mMap.addMarker(new MarkerOptions()
                                 .position(restaurant)
                                 .title(place.getName()));
                         moveCamera(restaurant);
                         myMarker.showInfoWindow();

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
         });


     }

    private void moveCamera(LatLng location) {

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,15));
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
    }
}

