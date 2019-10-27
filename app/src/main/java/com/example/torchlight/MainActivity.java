package com.example.torchlight;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private LinearLayout front_flash_layout, rear_flash_layout;
    private ImageView rear_circle, front_circle;
    private boolean hasFlash;
    private String backCameraId,frontCameraId="";

    public static final String SHRD_PRF_FLASH_STATE = "ShrdFlashState";
    public static final String KEY_REAR_FLASH_MODE = "RearFlashMode";
    public static final String KEY_FRONT_FLASH_MODE = "FrontFlashMode";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor shrdPrefEditor;

    private Boolean rear_flash_state=false, front_flash_state=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //cast
        front_flash_layout = findViewById(R.id.ll_front_flash);
        rear_flash_layout = findViewById(R.id.ll_rear_flash);
        rear_circle = findViewById(R.id.rear_circle);
        front_circle = findViewById(R.id.front_circle);

        //get Flash
        hasFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        //Shared Preferences
        sharedPreferences = getSharedPreferences(SHRD_PRF_FLASH_STATE, MODE_PRIVATE);
        shrdPrefEditor = sharedPreferences.edit();

        if(hasFlash){
            final CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            try {
                String[] cameraIDs = cameraManager.getCameraIdList();
                backCameraId = cameraIDs[0];
                if(cameraIDs.length>=2){
                    frontCameraId = cameraIDs[1];
                }

            } catch (CameraAccessException e) {
                //e.printStackTrace();
                Log.v("shanu",e.getMessage());
                Toast.makeText(this, "No Camera available", Toast.LENGTH_SHORT).show();
            }

            //set listner
            rear_flash_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            rear_flash_state = !rear_flash_state;
                            cameraManager.setTorchMode(backCameraId,rear_flash_state);

                            shrdPrefEditor.putBoolean(KEY_REAR_FLASH_MODE, rear_flash_state);
                            shrdPrefEditor.apply();
                        }
                    } catch (CameraAccessException e) {
                        //e.printStackTrace();
                        Log.v("shanu",e.getMessage());
                        Toast.makeText(MainActivity.this, "No Camera Available", Toast.LENGTH_SHORT).show();
                    }
                    rear_circle.setImageResource(rear_flash_state? R.drawable.circle_image_on: R.drawable.circle_image_off);

                }
            });

            front_flash_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            front_flash_state = !front_flash_state;
                            cameraManager.setTorchMode(frontCameraId,front_flash_state);

                            shrdPrefEditor.putBoolean(KEY_FRONT_FLASH_MODE, front_flash_state);
                            shrdPrefEditor.apply();
                        }
                    } catch (CameraAccessException e) {
                        //e.printStackTrace();
                        Log.v("shanu",e.getMessage());
                        Toast.makeText(MainActivity.this, "No Camera Available", Toast.LENGTH_SHORT).show();
                    }

                    front_circle.setImageResource(front_flash_state? R.drawable.circle_image_on: R.drawable.circle_image_off);

                }
            });


        }
        else{
            Toast.makeText(this, "Flash Light is not present", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        rear_flash_state = sharedPreferences.getBoolean(KEY_REAR_FLASH_MODE,false);
        front_flash_state = sharedPreferences.getBoolean(KEY_FRONT_FLASH_MODE, false);

        rear_circle.setImageResource(rear_flash_state? R.drawable.circle_image_on: R.drawable.circle_image_off);
        front_circle.setImageResource(front_flash_state? R.drawable.circle_image_on: R.drawable.circle_image_off);
    }
}
