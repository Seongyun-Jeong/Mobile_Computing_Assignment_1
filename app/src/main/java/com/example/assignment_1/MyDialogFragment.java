package com.example.assignment_1;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class MyDialogFragment extends DialogFragment {

    public interface OnDialogButtonClickedListener {
        void onYesButtonClicked();
        void onCancelButtonClicked(int dotIndex);
    }

    private OnDialogButtonClickedListener listener;
    private int dotIndex;

    public static MyDialogFragment newInstance(int dotIndex) {
        MyDialogFragment fragment = new MyDialogFragment();
        Bundle args = new Bundle();
        args.putInt("dotIndex", dotIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            dotIndex = getArguments().getInt("dotIndex");
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (OnDialogButtonClickedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnDialogButtonClickedListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_signin, null);
        Button yesButton = view.findViewById(R.id.Yes_button);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onYesButtonClicked();
                dismiss();
            }
        });

        Button cancelButton = view.findViewById(R.id.Cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCancelButtonClicked(dotIndex);
                dismiss();
            }
        });

        builder.setView(view);
        return builder.create();
    }
}

