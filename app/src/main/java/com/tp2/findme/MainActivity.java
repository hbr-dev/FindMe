package com.tp2.findme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tp2.findme.fragments.DashboardFragment;
import com.tp2.findme.fragments.HomeFragment;
import com.tp2.findme.fragments.NotificationFragment;

import java.util.List;
import java.util.Objects;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static final int RC_PERM = 125;
    private static final int RC_SETTINGS = 126;

    private int frg_id = R.id.home_item;

    private final String[] wantedPerm = {
                                            Manifest.permission.SEND_SMS,
                                            Manifest.permission.RECEIVE_SMS,
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.POST_NOTIFICATIONS,
                                            Manifest.permission.INTERNET,
                                        };


    private HomeFragment home_fragment;
    private DashboardFragment dashboard_fragment;
    private NotificationFragment notification_fragment;

    private BottomNavigationView btm_menu;

    private GPSTracker gps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gps = new GPSTracker(MainActivity.this);

        home_fragment = new HomeFragment(this);
        dashboard_fragment = new DashboardFragment(this);
        notification_fragment = new NotificationFragment();

        btm_menu = findViewById(R.id.bottomNavigationView);

        btm_menu.setOnItemSelectedListener(this::onNavigationItemSelected);

        btm_menu.setSelectedItemId(R.id.home_item);

        // Check if the activity was launched from a notification click
        if (getIntent().hasExtra("action")) {
            String action = getIntent().getStringExtra("action");
            if (action.equals("share_location"))
                manageIntentActions(0, btm_menu);
            else
                manageIntentActions(1, btm_menu);
            Toast.makeText(this, "onCreate(): " + action, Toast.LENGTH_SHORT).show();
        }
    }



    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if ( item.getItemId()==R.id.home_item && isPermissionsGranted()) {
            frg_id = R.id.home_item;
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, home_fragment)
                    .commit();
            return true;
        }
        else if ( item.getItemId()==R.id.dashboard_item ) {
            frg_id = R.id.dashboard_item;
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, dashboard_fragment)
                    .commit();
            return true;
        }
        else if ( item.getItemId()==R.id.notification_item ) {
            frg_id = R.id.notification_item;
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, notification_fragment)
                    .commit();
            return true;
        }
        else {
            return false;
        }
    }



    private void manageIntentActions(int action_code, BottomNavigationView btm_menu) {
        if (action_code == 0) {
            if (gps.canGetLocation()) {
                double longitude = gps.getLongitude();
                double latitude = gps.getLatitude();

                Toast.makeText(getApplicationContext(), "Longitude:" + longitude + "\nLatitude:" + latitude, Toast.LENGTH_SHORT).show();
                SMS.sendSMS(MainActivity.this, SMS.sender_nbr, longitude + "-" + latitude);
            } else {
                gps.showSettingsAlert();
            }
        }
        if (action_code == 1) {
            Toast.makeText(MainActivity.this, "Find the location", Toast.LENGTH_SHORT).show();
            btm_menu.setSelectedItemId(R.id.dashboard_item);
            SMS.received_coords = SMS.received_sms;
            // -122.08400000000002-37.421998333333335
        }
    }



    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // Check if the activity was launched from a notification click
        if (intent.hasExtra("action")) {
            String action = intent.getStringExtra("action");
            Toast.makeText(this, "onNewIntent(): " + action, Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, MainActivity.this);
        System.out.println("onRequestPermissionsResult(): triggered");
    }



    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        System.out.println("onPermissionsGranted(): " + requestCode + ": " + perms.size());
    }



    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        System.out.println("onPermissionsDenied(): " + requestCode + ": " + perms.size());
        if (EasyPermissions.somePermissionPermanentlyDenied(MainActivity.this, perms)) {
            new AppSettingsDialog.Builder(MainActivity.this)
                    .setTitle("Permissions Required")
                    .setPositiveButton("Settings")
                    .setNegativeButton("Cancel")
                    .setRequestCode(RC_SETTINGS)
                    .build()
                    .show();
        }
    }




    private boolean isPermissionsGranted() {
        if (EasyPermissions.hasPermissions(this, wantedPerm)) {
            System.out.println("isPermissionsGranted(): Already have permission, do the thing");
            return true;
        } else {
            System.out.println("isPermissionsGranted(): Do not have permission, request them now");
            EasyPermissions.requestPermissions(MainActivity.this, "This app needs access to send SMS.", RC_PERM, wantedPerm);
            return false;
        }
    }



    @Override
    public void onResume(){
        super.onResume();
        btm_menu.setSelectedItemId(frg_id);
    }


}