package com.example.assignment_1;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.net.wifi.ScanResult;
import java.util.List;
import android.Manifest;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import androidx.annotation.Nullable;



import android.app.Dialog;
import android.view.LayoutInflater;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import android.util.Pair;

public class AP_Scanned extends DialogFragment {

    WifiManager wifiManager;
    TextView textView;
    Button yesButton;
    Button noButton;
    List<ScanResult> results;

    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION = 1001;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.ap_scanned, null);

        textView = view.findViewById(R.id.ap_textview);
        textView.setMovementMethod(new ScrollingMovementMethod());

        yesButton = view.findViewById(R.id.ap_Yes_Button);
        noButton = view.findViewById(R.id.ap_No_Button);

        wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION);
        } else {
            scanWifiNetworks();
        }

        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    scanWifiNetworks();
                } else {
                    Log.d("DD","Something wrong in permission");
                }
                return;
            }
        }
    }

    private void scanWifiNetworks() {
        try {
            results = wifiManager.getScanResults();
            StringBuilder stringBuilder = new StringBuilder();
            for (ScanResult scanResult : results) {
                String ssid = scanResult.SSID;
                String bssid = scanResult.BSSID;
                int level = scanResult.level;

                String details = ssid + ", " + bssid + ", " + level + "dBm";
                stringBuilder.append(details).append("\n");
            }

            textView.setText(stringBuilder.toString());

            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    float dotX = getArguments().getFloat("dotX");
                    float dotY = getArguments().getFloat("dotY");
                    saveResults(dotX, dotY, stringBuilder.toString());
                    dismiss();
                }
            });

            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        } catch (SecurityException e) {
        }
    }


    private void saveResults(float x, float y, String results) {
        Log.d("TT","Reach SaveResults");
        TouchPointAndResults data = new TouchPointAndResults(x, y, results);
        ((MainActivity) getActivity()).addScanResults(new Pair<>(x, y), results);

    }


}
