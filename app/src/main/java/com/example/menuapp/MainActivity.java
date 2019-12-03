package com.example.menuapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;


import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private int RC_SIGN_IN = 123;
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
        Toolbar mainToolBar = findViewById(R.id.my_toolbar);
        setSupportActionBar(mainToolBar);

        if(allPermissionsGranted()){
            startCamera(); //start camera if permission has been granted by user
        } else{
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
            //startCamera
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

        ImageAnalysisConfig iConfig = new ImageAnalysisConfig.Builder()
                .setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
                .build();
        ImageAnalysis imageAnalysis = new ImageAnalysis(iConfig);

        imageAnalysis.setAnalyzer(
                new ImageAnalysis.Analyzer() {
                    @Override
                    public void analyze(ImageProxy imageProxy, int degrees) {
//                        if (imageProxy == null || imageProxy.getImage() == null) {
//                            return;
//                        }
                        Image mediaImage = imageProxy.getImage();
                        int rotation = degreesToFirebaseRotation(degrees);
                        FirebaseVisionImage image = FirebaseVisionImage.fromMediaImage(mediaImage,rotation);
                        detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options);

                        Task<List<FirebaseVisionBarcode>> result = detector.detectInImage(image)
                                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                                    @Override
                                    public void onSuccess(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {
                                        processResult(firebaseVisionBarcodes);
                                    }
                                });
                    }
                }
        );

        CameraX.bindToLifecycle(this, preview, imageAnalysis); //imgCap

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
    /*
    @name: degreesToFirebaseRotation
    @desc: Deals with CameraX camera rotation.

    @doc: https://developer.android.com/training/permissions/requesting
    */
    private int degreesToFirebaseRotation(int degrees) {
        switch (degrees) {
            case 0:
                return FirebaseVisionImageMetadata.ROTATION_0;
            case 90:
                return FirebaseVisionImageMetadata.ROTATION_90;
            case 180:
                return FirebaseVisionImageMetadata.ROTATION_180;
            case 270:
                return FirebaseVisionImageMetadata.ROTATION_270;
            default:
                throw new IllegalArgumentException(
                        "Rotation must be 0, 90, 180, or 270.");
        }
    }
    /*
    @name: processResult
    @desc: Load ImageDisplay activity with passed value of barcode to do a firebase bucket lookup.

    @doc: https://developer.android.com/training/permissions/requesting
    */
    private void processResult(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {
//        int debugLen = firebaseVisionBarcodes.size();
//        Log.d("MainActivity", String.valueOf(debugLen));
        for(FirebaseVisionBarcode barcode: firebaseVisionBarcodes) {
            int valueType = barcode.getValueType();

            switch (valueType) {
                case FirebaseVisionBarcode.TYPE_TEXT:
                {
                    String msg = barcode.getRawValue();
//                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
//                    Log.d("mainActivity",msg);
                    Intent intent = new Intent(MainActivity.this, imageDisplay.class);
                    intent.putExtra("FBRef", msg);
                    startActivity(intent);
                    break;
                }
            }

        }
    }


    /*
    @name:
    @desc:

    *Only called once
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()) {
            case R.id.action_favorite:
                Intent intent = new Intent(MainActivity.this, loginMain.class);
                startActivity(intent);
                break;
            case R.id.action_settings:
                Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT).show();
                break;
            default:
                // user's action was not recognized, invoke superclass to handle it.
                return super.onOptionsItemSelected(item);
        } //switch

        return true;
    }
}



