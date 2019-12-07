package com.example.menuapp.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.gesture.Gesture;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;

import com.example.menuapp.R;
import com.example.menuapp.imageDisplay;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;

import java.util.List;

public class cameraFragment extends Fragment {

    private int totalImages = 0;
    private int REQUEST_CODE_PERMISSIONS = 101;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    TextureView textureView;

    FirebaseVisionBarcodeDetectorOptions options = new FirebaseVisionBarcodeDetectorOptions.Builder()
            .setBarcodeFormats(
                    FirebaseVisionBarcode.FORMAT_QR_CODE)
            .build();
    FirebaseVisionBarcodeDetector detector;

    // method called in getItem()
    public static cameraFragment newInstance() {
        cameraFragment fragment = new cameraFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        View cameraView = inflater.inflate(R.layout.activity_main , container, false);

        cameraView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int event = motionEvent.getActionMasked();
                switch (event) {
                    case 0:
                        totalImages = 0;
                }
                return false;
            }
        });
        textureView = cameraView.findViewById(R.id.view_finder);
        //Toolbar mainToolBar = cameraView.findViewById(R.id.my_toolbar);
        //setSupportActionBar(mainToolBar);

        if(allPermissionsGranted()){
            startCamera(); //start camera if permission has been granted by user
        } else{
            ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
            //startCamera();
        }

        return cameraView;
    }

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
                .setImageQueueDepth(1)
                .setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
                .build();
        ImageAnalysis imageAnalysis = new ImageAnalysis(iConfig);

        imageAnalysis.setAnalyzer(
                new ImageAnalysis.Analyzer() {
                    @Override
                    public void analyze(final ImageProxy imageProxy, int degrees) {
                        if (imageProxy == null || imageProxy.getImage() == null) {
                            return;
                        }
                        Image mediaImage = imageProxy.getImage();
                        int rotation = degreesToFirebaseRotation(degrees);
                        FirebaseVisionImage image = FirebaseVisionImage.fromMediaImage(mediaImage,rotation);
                        detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options);

                        detector.detectInImage(image)
                                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                                    @Override
                                    public void onSuccess(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {
//                                        if (firebaseVisionBarcodes.size() > 0) {
//
//                                        }
                                        processResult(firebaseVisionBarcodes);
                                        //Log.d("MainActivity", String.valueOf(totalImages));

                                    }
                                });
                        //imageProxy.close();

                    }
                }

        );
        Log.d("MainActivity", String.valueOf(totalImages));


        CameraX.bindToLifecycle(this, preview, imageAnalysis); //imgCap

    }

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

    private void processResult(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {
        int totalBarcodes = firebaseVisionBarcodes.size();
        totalImages += totalBarcodes;
        if (totalBarcodes == 1 && totalImages == 1) {
            for (FirebaseVisionBarcode barcode : firebaseVisionBarcodes) {
                int valueType = barcode.getValueType();

                switch (valueType) {
                    case FirebaseVisionBarcode.TYPE_TEXT: {
                        String msg = barcode.getRawValue();
                        Intent intent = new Intent(getContext(), imageDisplay.class);
                        intent.putExtra("FBRef", msg);
                        startActivity(intent);
                        break;
                    }
                }

            }
        }
    }

    private boolean allPermissionsGranted(){

        for(String permission : REQUIRED_PERMISSIONS) {
            if(ContextCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }


}
