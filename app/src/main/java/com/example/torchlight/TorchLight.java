package com.example.torchlight;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Icon;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;
import android.widget.Toast;

import java.util.Locale;

@TargetApi(Build.VERSION_CODES.N)
public class TorchLight extends TileService {

    public static final String SERVICE_STATUS_FLAG = "servicestatus";
    public static final String PREFERENCES_KEY = "com.example.torchlight";
    private boolean hasCamera;
    private String cameraId;
    private CameraManager cameraManager;
    @Override
    public void onClick() {
        super.onClick();


        hasCamera =  getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if(hasCamera)
            updateTile();
        else
            Toast.makeText(this, "No Flash Available", Toast.LENGTH_SHORT).show();
    }

    private void updateTile() {
        Tile tile = this.getQsTile();
        boolean isActive = getServiceStatus();

        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            //e.printStackTrace();
            Log.v("shanu",e.getMessage());
        }


        Icon newIcon;
        String newLabel;
        int newState;


        // Change the tile to match the service status.
        if (isActive) {

            try {
                cameraManager.setTorchMode(cameraId,true);
            } catch (CameraAccessException e) {
                //e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
           // Toast.makeText(this, "off", Toast.LENGTH_SHORT).show();

            newLabel = String.format(Locale.US,
                    "%s %s",
                    getString(R.string.tile_label),
                    "ON");

            newIcon = Icon.createWithResource(getApplicationContext(),
                    R.drawable.ic_torchlight);

            newState = Tile.STATE_ACTIVE;


        } else {

            //Toast.makeText(this, "on", Toast.LENGTH_SHORT).show();

            try {
                cameraManager.setTorchMode(cameraId,false);
            } catch (CameraAccessException e) {
                //e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            newLabel = String.format(Locale.US,
                    "%s %s",
                    getString(R.string.tile_label),
                    "OFF");

            newIcon =
                    Icon.createWithResource(getApplicationContext(),
                            R.drawable.ic_torchlight);

            newState = Tile.STATE_INACTIVE;
        }

        // Change the UI of the tile.
        tile.setLabel(newLabel);
        tile.setIcon(newIcon);
        tile.setState(newState);

        // Need to call updateTile for the tile to pick up changes.
        tile.updateTile();

    }






    // Access storage to see how many times the tile
    // has been tapped.
    private boolean getServiceStatus() {

        SharedPreferences prefs =
                getApplicationContext()
                        .getSharedPreferences(PREFERENCES_KEY, MODE_PRIVATE);

        boolean isActive = prefs.getBoolean(SERVICE_STATUS_FLAG, false);
        isActive = !isActive;

        prefs.edit().putBoolean(SERVICE_STATUS_FLAG, isActive).apply();

        return isActive;
    }

    @Override
    public void onTileAdded() {
        super.onTileAdded();

        Tile tile = this.getQsTile();
        String newLabel = String.format(Locale.US,
                "%s %s",
                getString(R.string.tile_label),
                "OFF");

        Icon newIcon =
                Icon.createWithResource(getApplicationContext(),
                        R.drawable.ic_torchlight);

        int newState = Tile.STATE_INACTIVE;

        tile.setLabel(newLabel);
        tile.setIcon(newIcon);
        tile.setState(newState);

        // Need to call updateTile for the tile to pick up changes.
        tile.updateTile();

    }
}
