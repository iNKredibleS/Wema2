package com.inkredibles.wema20;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.RadioGroup;

public class TagsDialog extends DialogFragment {

    private String mType;
    private DialogueListener listener;
    private RadioGroup rgPrivacy;
    private RadioGroup rgType;

    public TagsDialog() {
    }

    public static TagsDialog newInstance(String type) {
        TagsDialog frag = new TagsDialog();
        Bundle args = new Bundle();
        args.putString("type", type);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get the layout inflater
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final CharSequence[] items = {"Give", "Receive"};
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int item) {
                mType = items[item].toString();
            }
        })
                .setTitle(R.string.dialog_type_question)
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
                });

        return builder.create();
    }

    //sends the give/receive type back to the createpost
    public void sendBackResult() {
        // Notice the use of `getTargetFragment` which will be set when the dialog is displayed
        DialogueListener listener = (DialogueListener) getTargetFragment();
        assert listener != null;
        listener.onFinishTagDialog(mType);
        dismiss();
    }
}


