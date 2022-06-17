package appentwicklung.android.myrunapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

/** Class for the ShowDetailsDialog
 *
 * @author Daniel Johansson
 */
public class ShowDetailsDialog extends DialogFragment {
    SaveDialogListener mListener;

    public interface SaveDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(getContext());

        try {
            mListener = (SaveDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement SaveDialogListener");
        }
    }

    /** Creates the ShowDetailsDialog */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Show session details?")
                .setPositiveButton("Show session", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogPositiveClick(ShowDetailsDialog.this);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogNegativeClick(ShowDetailsDialog.this);
                    }

                });

        return builder.create();
    }
}

