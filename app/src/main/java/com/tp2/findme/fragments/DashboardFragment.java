package com.tp2.findme.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tp2.findme.GPSTracker;
import com.tp2.findme.MainActivity;
import com.tp2.findme.R;
import com.tp2.findme.SMS;

import java.util.ArrayList;
import java.util.Arrays;

public class DashboardFragment extends Fragment {

    private Context context;





    public DashboardFragment(Context context) {
        this.context = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Initialize map fragment
        SupportMapFragment supportMapFragment=(SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);

        // Async map
        supportMapFragment.getMapAsync(googleMap -> {
            // When map is loaded
            googleMap.setOnMapClickListener(latLng -> {
                // When clicked on map
                // Initialize marker options
                MarkerOptions markerOptions=new MarkerOptions();
                // Set position of marker
                markerOptions.position(latLng);
                // Set title of marker
                markerOptions.title(latLng.latitude+" : "+latLng.longitude);
                // Remove all marker
                googleMap.clear();
                // Animating to zoom the marker
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
                // Add marker on map
                googleMap.addMarker(markerOptions);
            });

            onLocationSharedBySMS(googleMap);
        });

        // Return view
        return view;
    }



    private void onLocationSharedBySMS(GoogleMap googleMap) {
        LatLng Location;
        if (SMS.received_coords.equals("")) {
            GPSTracker gpsTracker = new GPSTracker(this.context);
            Location = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
        }
        else {
            ArrayList<Double> rcv_coords = convertStringToArrayListOfDoubles();
            Location = new LatLng(rcv_coords.get(1), rcv_coords.get(0));
        }
        googleMap.addMarker(new MarkerOptions().position(Location).title("Location"));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Location,10));
    }



    private ArrayList<Double> convertStringToArrayListOfDoubles() {
        ArrayList<Double> coords = new ArrayList<>();
        String[] str_coords = SMS.received_coords.split("-");
        for (String str : str_coords) {
            try {
                double dbl = Double.parseDouble(str);
                coords.add(dbl);
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Error when getting data from the SMS", Toast.LENGTH_LONG).show();
            }

        }
        return coords;
    }


}
