package com.tp2.findme.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tp2.findme.GPSTracker;
import com.tp2.findme.MainActivity;
import com.tp2.findme.R;
import com.tp2.findme.SMS;

import java.util.HashMap;
import java.util.Map;


public class HomeFragment extends Fragment {

    EditText ed_phone;
    Button btn_send;
    Context context;






    public HomeFragment(Context context) {
        this.context = context;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View frgt_view = inflater.inflate(R.layout.fragment_home, container, false);

        ed_phone = frgt_view.findViewById(R.id.ed_phone);
        btn_send = frgt_view.findViewById(R.id.btn_send);


        btn_send.setOnClickListener(view -> {
            String phone_nbr = ed_phone.getText().toString();
            if (phone_nbr.equals(""))
                Toast.makeText(context, "You must add a number!", Toast.LENGTH_SHORT).show();
            else
                SMS.sendSMS(context, phone_nbr, "I'm asking you for your location.");
        });

        return frgt_view;
    }

}