package com.example.assignment_1;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class MyDialogFragment extends DialogFragment {

    public interface OnYesButtonClickedListener {
        void onYesButtonClicked();
    }

    private OnYesButtonClickedListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (OnYesButtonClickedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnYesButtonClickedListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        View view = inflater.inflate(R.layout.dialog_signin, null);

        // Find the buttons and set their onClickListeners
        Button yesButton = view.findViewById(R.id.Yes_button);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Notify the activity that the "Yes" button was clicked
                listener.onYesButtonClicked();
                dismiss();
            }
        });

        Button cancelButton = view.findViewById(R.id.Cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the dialog when the "Cancel" button is clicked
                dismiss();
            }
        });

        builder.setView(view);
        return builder.create();
    }




//    public void onYesButtonClicked() {
//        if (getActivity() != null) {
//            Intent intent = new Intent(getActivity(), AP_Scanned.class);
//            getActivity().startActivity(intent);
//        }
//    }



}


//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle("Custom Title")
//                .setMessage("What would you like to scan?")
//                .setPositiveButton("Option 1", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // Handle Option 1 click
//                    }
//                })
//                .setNegativeButton("Option 2", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // Handle Option 2 click
//                    }
//                });
//        return builder.create();
//    }