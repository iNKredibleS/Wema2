package com.inkredibles.wema20;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class PrivacyDialog extends DialogFragment {

    private String mPrivacy;

    public PrivacyDialog(){}

    public static PrivacyDialog newInstance(String privacy) {
        PrivacyDialog frag = new PrivacyDialog();
        Bundle args = new Bundle();
        args.putString("privacy", privacy);
        frag.setArguments(args);
        return frag;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        //set up the radio buttons

        // Get the layout inflater
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //LayoutInflater inflater = getActivity().getLayoutInflater();


        final CharSequence[] items = {"Public", "Private"};


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        //builder.setView(inflater.inflate(R.layout.dialog_tags, null))
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int item) {
                mPrivacy = items[item].toString();
            }
        })
                .setTitle(R.string.dialog_privacy_text)
                // Add action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        sendBackResult();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                })

        ;


        return builder.create();


    }

    public void sendBackResult() {
        // Notice the use of `getTargetFragment` which will be set when the dialog is displayed
        DialogueListener listener = (DialogueListener) getTargetFragment();
        assert listener != null;
        listener.onFinishPrivacyDialog(mPrivacy);
        dismiss();
    }

}
