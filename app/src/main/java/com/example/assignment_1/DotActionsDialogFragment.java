package com.example.assignment_1;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class DotActionsDialogFragment extends DialogFragment {

    public interface OnDotActionsSelectedListener {
        void onDeleteAndAddNew(int dotIndex);
        void onDelete(int dotIndex);
        void onSeeResults(int dotIndex);
    }

    private OnDotActionsSelectedListener listener;
    private int dotIndex;

    public DotActionsDialogFragment(int dotIndex) {
        this.dotIndex = dotIndex;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (OnDotActionsSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnDotActionsSelectedListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Select an action")
                .setItems(new String[]{"Delete and Add New", "Delete", "See Results", "Cancel"}, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: // Delete and Add New
                                listener.onDeleteAndAddNew(dotIndex);
                                break;
                            case 1: // Delete
                                listener.onDelete(dotIndex);
                                break;
                            case 2: // See Results
                                listener.onSeeResults(dotIndex);
                                break;
                            case 3: // Cancel
                            default:
                                dialog.dismiss();
                                break;
                        }
                    }
                });
        return builder.create();
    }
}
