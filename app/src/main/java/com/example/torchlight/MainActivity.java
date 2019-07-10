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
    private SwitchMaterial switchMaterial;
    private TextView tv_info;
    private boolean isOn,hasFlash;
    private String cameraId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //cast
        switchMaterial = findViewById(R.id.id_switch);
        tv_info = findViewById(R.id.tv_info);

        //set deafult value
        isOn = false;
        hasFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        //logic
        if(hasFlash){
            final CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            try {
                cameraId = cameraManager.getCameraIdList()[0];
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
                            cameraManager.setTorchMode(cameraId,isChecked);
                        }
                    } catch (CameraAccessException e) {
                        //e.printStackTrace();
                        Log.v("shanu",e.getMessage());
                        Toast.makeText(MainActivity.this, "No Camera Available", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
        else{
            Toast.makeText(this, "Flash Light is not present", Toast.LENGTH_SHORT).show();
        }
    }
}
