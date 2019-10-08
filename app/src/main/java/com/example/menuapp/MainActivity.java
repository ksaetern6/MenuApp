package com.example.menuapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;

public class MainActivity extends AppCompatActivity {

    // https://i.imgur.com/JhYpzdR.jpg
    private int REQUEST_CODE_PERMISSIONS = 101;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    TextureView textureView;

    FirebaseVisionBarcodeDetectorOptions options = new FirebaseVisionBarcodeDetectorOptions.Builder()
            .setBarcodeFormats(
                    FirebaseVisionBarcode.FORMAT_QR_CODE)
            .build();
    FirebaseVisionBarcodeDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textureView = findViewById(R.id.view_finder);

        if(allPermissionsGranted()){
            startCamera(); //start camera if permission has been granted by user
        } else{
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    /*
    @name: startCamera

    @desc:
     */
    private void startCamera() {
        CameraX.unbindAll();
        PreviewConfig pConfig = new PreviewConfig.Builder().build();
        Preview preview = new Preview(pConfig);

        preview.setOnPreviewOutputUpdateListener(
                new Preview.OnPreviewOutputUpdateListener() {
                    //to update the surface texture we  have to destroy it first then re-add it
                    @Override
                    public void onUpdated(Preview.PreviewOutput output) {
                        ViewGroup parent = (ViewGroup) textureView.getParent();
                        parent.removeView(textureView);
                        parent.addView(textureView, 0);

                        textureView.setSurfaceTexture(output.getSurfaceTexture());
                    }
                });

        findViewById(R.id.imgCapture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, imageDisplay.class);
                startActivity(intent);
            }
        });

        CameraX.bindToLifecycle(this, preview); //imgCap

    }
    /*
    @name: allPermissionsGranted()
    @desc: true or false to check if AndroidManifest gave camera permission.

    @doc: https://developer.android.com/training/permissions/requesting
     */
    private boolean allPermissionsGranted(){

        for(String permission : REQUIRED_PERMISSIONS) {
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }
}
