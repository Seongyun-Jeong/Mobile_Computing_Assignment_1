package com.example.assignment_1;
import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.net.wifi.ScanResult;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.AlertDialog;
import android.util.Pair;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements MyDialogFragment.OnDialogButtonClickedListener, DotActionsDialogFragment.OnDotActionsSelectedListener {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSION_REQUEST_CODE = 2;
    private ImageView imageView;
    private Button btnUpload;
    private Button btnScan;
    private boolean isImageSelected = false;
    private DotsOverlayView dotsOverlayView;
    private HashMap<Pair<Float, Float>, String> scanResultsMap = new HashMap<>();
    private int lastDotIndex = -1;
    private Handler handler = new Handler();
    private Runnable updateRunnable;
    WifiManager wifiManager;
    private boolean isScanning = false;
    private Handler wifiScanHandler;
    private Runnable wifiScanRunnable;
    private List<ScanResult> previousScanResults;
    StringBuilder stringBuilder = new StringBuilder();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        btnUpload = findViewById(R.id.btnUpload);
        btnScan = findViewById(R.id.btnScan);
        btnScan.setVisibility(View.GONE); // Button B is initially invisible
        dotsOverlayView = findViewById(R.id.dotsOverlayView);

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isImageSelected) {
                    imageView.setImageDrawable(null);
                    btnUpload.setText("Upload");
                    btnScan.setVisibility(View.GONE); // Hide Button B when the image is deleted
                    isImageSelected = false;
                } else {
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                PERMISSION_REQUEST_CODE);
                    } else {
                        openFileChooser();
                    }
                }
            }
        });
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        previousScanResults = new ArrayList<>();

        wifiScanHandler = new Handler();
        wifiScanRunnable = new Runnable() {
            @Override
            public void run() {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    List<ScanResult> currentScanResults = wifiManager.getScanResults();
                    if (!currentScanResults.toString().equals(previousScanResults.toString())) {
                        Toast.makeText(MainActivity.this, "Wifi refreshed", Toast.LENGTH_SHORT).show();
                        previousScanResults = currentScanResults;
                        stringBuilder.setLength(0);
                        for (ScanResult scanResult : previousScanResults) {
                            String ssid = scanResult.SSID;
                            String bssid = scanResult.BSSID;
                            int level = scanResult.level;

                            String details = ssid + ", " + bssid + ", " + level + "dBm";
                            stringBuilder.append(details).append("\n");
                        }
                    }
                    wifiScanHandler.postDelayed(this, 1000);
                }else {
                    wifiScanHandler.removeCallbacks(this);
                }
            }
        };
        // Initialize the alert button
        Button alertButton = findViewById(R.id.btnAlert);
        alertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isScanning) {
                    // If currently scanning, stop the scanning task
                    wifiScanHandler.removeCallbacks(wifiScanRunnable);
                    isScanning = false;
                    alertButton.setText("Start Alert");
                } else {
                    // If not scanning, start the scanning task
                    wifiScanHandler.post(wifiScanRunnable);
                    isScanning = true;
                    alertButton.setText("Stop Alert");
                }
            }
        });

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    int dotIndex = dotsOverlayView.getDotAtPosition(event.getX(), event.getY());
                    if (dotIndex != -1) {
                        // An existing dot was touched. Show the DotActionsDialogFragment.
                        DotActionsDialogFragment dialog = new DotActionsDialogFragment(dotIndex);
                        dialog.show(getSupportFragmentManager(), "DotActionsDialogFragment");
                    } else {
                        // No existing dot was touched. Add a new dot and show the MyDialogFragment.
                        dotsOverlayView.addDot(event.getX(), event.getY());
                        int newDotIndex = dotsOverlayView.getDotCount() - 1;
                        MyDialogFragment dialog = MyDialogFragment.newInstance(newDotIndex);
                        dialog.show(getSupportFragmentManager(), "MyDialogFragment");

                    }
                }
                return true;
            }
        });


        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If the updateRunnable is not null, it means the button is currently in scanning mode.
                if (updateRunnable != null) {
                    handler.removeCallbacks(updateRunnable);
                    updateRunnable = null;
                    dotsOverlayView.removeBlueDot();
                    return;
                }

                updateRunnable = new Runnable() {
                    @Override
                    public void run() {
                        float totalX = 0;
                        float totalY = 0;
                        float totalWeight = 0;

                        Map<String, Integer> targetMap = parseDetails(stringBuilder.toString());

                        for (Map.Entry<Pair<Float, Float>, String> entry : scanResultsMap.entrySet()) {
                            Pair<Float, Float> coordinates = entry.getKey();
                            String details = entry.getValue();

                            Map<String, Integer> detailsMap = parseDetails(details);

                            float similarity = calculateSimilarity(targetMap, detailsMap);
                            totalX += (coordinates.first * similarity);
                            totalY += (coordinates.second * similarity);
                            totalWeight += similarity;
                        }

                        dotsOverlayView.addOrUpdateBlueDot(totalX / totalWeight, totalY / totalWeight);
                        handler.postDelayed(this, 1000);


                    }

                    private Map<String, Integer> parseDetails(String details) {
                        Map<String, Integer> map = new HashMap<>();
                        String[] splitDetails = details.split("\n");

                        for (String detail : splitDetails) {
                            String[] parts = detail.split(", ");
                            String bssid = parts[1];
                            int level = Integer.parseInt(parts[2].replace("dBm", ""));
                            map.put(bssid, level);
                        }

                        return map;
                    }

                    private float calculateSimilarity(Map<String, Integer> targetMap, Map<String, Integer> detailsMap) {
                        float similarity = 0;

                        for (Map.Entry<String, Integer> entry : targetMap.entrySet()) {
                            String bssid = entry.getKey();
                            int targetLevel = entry.getValue();

                            if (detailsMap.containsKey(bssid)) {
                                int detailsLevel = detailsMap.get(bssid);
                                similarity += 1 / (Math.abs(targetLevel - detailsLevel) + 1);
                            }
                        }

                        return similarity;
                    }
                };

                // Start updating
                handler.post(updateRunnable);
            }
        });


    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageView.setImageBitmap(bitmap);
                btnUpload.setText("Delete");
                btnScan.setVisibility(View.VISIBLE); // Show Button B when an image is uploaded
                isImageSelected = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void showDialog() {
        MyDialogFragment dialog = new MyDialogFragment();
        dialog.show(getSupportFragmentManager(), "MyDialogFragment");
    }

    @Override
    public void onYesButtonClicked() {
        AP_Scanned apScanned = new AP_Scanned();
        Bundle args = new Bundle();
        args.putFloat("dotX", dotsOverlayView.getDotX(lastDotIndex));
        args.putFloat("dotY", dotsOverlayView.getDotY(lastDotIndex));
        apScanned.setArguments(args);
        apScanned.show(getSupportFragmentManager(), "ap_scanned");

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("IsImageSelected", isImageSelected);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isImageSelected = savedInstanceState.getBoolean("IsImageSelected");
    }
        @Override
        public void onDeleteAndAddNew(int dotIndex) {
            dotsOverlayView.removeDot(dotIndex);
            MyDialogFragment dialog = new MyDialogFragment();
            dialog.show(getSupportFragmentManager(), "MyDialogFragment");
        }

        @Override
        public void onDelete(int dotIndex) {
            dotsOverlayView.removeDot(dotIndex);
        }

    @Override
    public void onSeeResults(int dotIndex) {
        float x = dotsOverlayView.getDotX(dotIndex);
        float y = dotsOverlayView.getDotY(dotIndex);

        Log.d("TT","Coordinate of onSeeResults - x : " + x + " y : " + y);

        String results = scanResultsMap.get(new Pair<>(x, y));

        if (results != null) {
            DialogFragment dialog = new ScanResultsDialogFragment(results);
            dialog.show(getSupportFragmentManager(), "ScanResultsDialogFragment");
        } else {
            Log.d("TT","something wrong");
        }
    }


    @Override
    public void onCancelButtonClicked(int dotIndex) {
        dotsOverlayView.removeDot(dotIndex);
    }
    public void addScanResults(Pair<Float, Float> coordinates, String results) {
        Log.d("TT", "Reach addScanResults");
        Log.d("TT", coordinates.toString());
        scanResultsMap.put(coordinates, results);
    }
    public void setLastDotIndex(int index) {
        lastDotIndex = index;
    }

    public int getLastDotIndex() {
        return lastDotIndex;
    }
    public static class ScanResultsDialogFragment extends DialogFragment {
        private final String results;

        ScanResultsDialogFragment(String results) {
            this.results = results;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(results)
                    .setPositiveButton("OK", null);
            return builder.create();
        }
    }
}
