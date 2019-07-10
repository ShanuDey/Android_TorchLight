package com.example.torchlight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class MainActivity extends AppCompatActivity {
    private SwitchMaterial switchMaterial,switchMaterial_front;
    private TextView tv_info,tv_info_front;
    private boolean hasFlash;
    private String backCameraId,frontCameraId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //cast
        switchMaterial = findViewById(R.id.id_switch);
        switchMaterial_front = findViewById(R.id.id_switch_front);
        tv_info = findViewById(R.id.tv_info);
        tv_info_front = findViewById(R.id.tv_info_front);

        //get Flash
        hasFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

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
            switchMaterial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            cameraManager.setTorchMode(backCameraId,isChecked);
                        }
                    } catch (CameraAccessException e) {
                        //e.printStackTrace();
                        Log.v("shanu",e.getMessage());
                        Toast.makeText(MainActivity.this, "No Camera Available", Toast.LENGTH_SHORT).show();
                    }
                    if(isChecked)
                        tv_info.setText("ON");
                    else
                        tv_info.setText("OFF");
                }
            });

            switchMaterial_front.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            cameraManager.setTorchMode(frontCameraId,isChecked);
                        }
                    } catch (CameraAccessException e) {
                        //e.printStackTrace();
                        Log.v("shanu",e.getMessage());
                        Toast.makeText(MainActivity.this, "No Camera Available", Toast.LENGTH_SHORT).show();
                    }
                    if(isChecked)
                        tv_info_front.setText("ON");
                    else
                        tv_info_front.setText("OFF");
                }
            });

        }
        else{
            Toast.makeText(this, "Flash Light is not present", Toast.LENGTH_SHORT).show();
        }
    }
}
