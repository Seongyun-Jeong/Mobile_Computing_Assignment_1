package com.example.assignment_1;
import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.IOException;
import android.app.AlertDialog;


public class MainActivity extends AppCompatActivity implements MyDialogFragment.OnYesButtonClickedListener, DotActionsDialogFragment.OnDotActionsSelectedListener {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSION_REQUEST_CODE = 2;
    private ImageView imageView;
    private Button btnUpload;
    private Button btnScan;
    private boolean isImageSelected = false;
    private DotsOverlayView dotsOverlayView;


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
                        MyDialogFragment dialog = new MyDialogFragment();
                        dialog.show(getSupportFragmentManager(), "MyDialogFragment");
                    }
                }
                return true;
            }
        });





        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MyDialogFragment().show(getSupportFragmentManager(), "MyDialogFragment");
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
//        MyDialogFragment dialog = new MyDialogFragment();
//        dialog.show(getSupportFragmentManager(), "MyDialogFragment");
//        Intent intent = new Intent(this, AP_Scanned.class);
//        startActivity(intent);
        AP_Scanned AP = new AP_Scanned();
        AP.show(getSupportFragmentManager(), "AP_Scanned");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putBoolean("IsImageSelected", isImageSelected);
        // etc.
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        isImageSelected = savedInstanceState.getBoolean("IsImageSelected");
        // etc.
    }
        // ...

        @Override
        public void onDeleteAndAddNew(int dotIndex) {
            // Implement your deletion and addition logic here
            // For example, you might remove the dot at the given index and then show a dialog to add a new one
            dotsOverlayView.removeDot(dotIndex);
            MyDialogFragment dialog = new MyDialogFragment();
            dialog.show(getSupportFragmentManager(), "MyDialogFragment");
        }

        @Override
        public void onDelete(int dotIndex) {
            // Implement your deletion logic here
            // For example, you might simply remove the dot at the given index
            dotsOverlayView.removeDot(dotIndex);
        }

        @Override
        public void onSeeResults(int dotIndex) {
            // Implement your logic for viewing results here
            // For example, you might show a dialog with the results associated with the given dot
        }

        // ...







}
