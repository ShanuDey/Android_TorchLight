package com.example.torchlight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class MainActivity extends AppCompatActivity {
    private SwitchMaterial switchMaterial,switchMaterial_front,switchMaterial_theme;
    private TextView tv_info,tv_info_front;
    private boolean hasFlash;
    private String backCameraId,frontCameraId="";

    public static final String SHRD_PRF_FLASH_STATE = "ShrdFlashState";
    public static final String KEY_REAR_FLASH_MODE = "RearFlashMode";
    public static final String KEY_FRONT_FLASH_MODE = "FrontFlashMode";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor shrdPrefEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dark);

        //cast
        switchMaterial = findViewById(R.id.id_switch);
        switchMaterial_front = findViewById(R.id.id_switch_front);
        switchMaterial_theme = findViewById(R.id.id_switch_theme);
        tv_info = findViewById(R.id.tv_info);
        tv_info_front = findViewById(R.id.tv_info_front);

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
            switchMaterial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            cameraManager.setTorchMode(backCameraId,isChecked);

                            shrdPrefEditor.putBoolean(KEY_REAR_FLASH_MODE, isChecked);
                            shrdPrefEditor.apply();
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

                            shrdPrefEditor.putBoolean(KEY_FRONT_FLASH_MODE, isChecked);
                            shrdPrefEditor.apply();
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

    @Override
    protected void onResume() {
        super.onResume();

        switchMaterial.setChecked(sharedPreferences.getBoolean(KEY_REAR_FLASH_MODE, false));
        switchMaterial_front.setChecked(sharedPreferences.getBoolean(KEY_FRONT_FLASH_MODE, false));
    }
}
