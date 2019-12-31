package com.example.torchlight;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id){
            case R.id.action_qstile:
                AlertDialog ad_qstile = new MaterialAlertDialogBuilder(this)
                        .setTitle("QS tile")
                        .setMessage("Quick Settings tile is available for front and rear flashlight. Add them from notification bar settings of this device")
                        .setCancelable(true)
                        .create();
                ad_qstile.show();
                return true;
            case R.id.action_about:

                AlertDialog ad_About = new MaterialAlertDialogBuilder(this)
                        .setTitle("About")
                        .setView(R.layout.view_about)
                        .setCancelable(true)
                        .create();
                ad_About.show();

                // Make the textview clickable. Must be called after show()
                TextView tv_source = ad_About.findViewById(R.id.tv_source);
                tv_source.setMovementMethod(LinkMovementMethod.getInstance());

                ((TextView) ad_About.findViewById(R.id.tv_author)).setMovementMethod(LinkMovementMethod.getInstance());
                ((TextView) ad_About.findViewById(R.id.tv_donation)).setMovementMethod(LinkMovementMethod.getInstance());

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
