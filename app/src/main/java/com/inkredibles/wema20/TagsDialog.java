package com.inkredibles.wema20;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
// ...

public class TagsDialog extends DialogFragment {

    private String mType;
    private String mPrivacy;

    private RadioGroup rgPrivacy;
    private RadioGroup rgType;

    public TagsDialog() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static TagsDialog newInstance(String type, String privacy) {
        TagsDialog frag = new TagsDialog();
        Bundle args = new Bundle();
        args.putString("type", type);
        args.putString("privacy", privacy);
        frag.setArguments(args);
        return frag;
    }



    public interface TagDialogListener {
        void onFinishTagDialog(String mType, String mPrivacy);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // R.layout.my_layout - that's the layout where your textview is placed
        View view = inflater.inflate(R.layout.dialog_tags, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rgPrivacy = (RadioGroup) view.findViewById(R.id.radioGroupPrivacy);
        rgPrivacy.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                Log.d("Dialog", "changing priv");
                switch (i) {
                    case R.id.radio_public:
                        mPrivacy = "public"; //set string to public
                        break;
                    case R.id.radio_private:
                        mPrivacy = "private"; // set privacy string to private
                        break;
                }
            }
        });

        rgType = (RadioGroup) view.findViewById(R.id.radioGroupType);
        rgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                Log.d("Dialog", "canging type");
                switch (i) {
                    case R.id.radio_received:
                        mType = "received";
                        //set string to received
                        break;
                    case R.id.radio_given:
                        mType = "given";
                        // set string to given
                        break;

                }
            }

        });
        // you can use your textview.

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        //set up the radio buttons

        // Get the layout inflater
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();




        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_tags, null))
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



//    @Override
//    public void onCheckedChanged(RadioGroup radioGroup, int i) {
//        switch (i) {
//            case R.id.radio_public:
//                mPrivacy = "public"; //set string to public
//                break;
//            case R.id.radio_private:
//                mPrivacy = "private"; // set privacy string to private
//                break;
//            case R.id.radio_received:
//                mType = "received";
//                //set string to received
//                break;
//            case R.id.radio_given:
//                mType = "given";
//                // set string to given
//                break;
//        }




//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_edit_name, container);
//    }
//
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        // Get field from view
//        mEditText = (EditText) view.findViewById(R.id.txt_your_name);
//        // Fetch arguments from bundle and set title
//        String title = getArguments().getString("title", "Enter Name");
//        getDialog().setTitle(title);
//        // Show soft keyboard automatically and request focus to field
//        mEditText.requestFocus();
//        getDialog().getWindow().setSoftInputMode(
//                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
//    }

//    public void onRadioButtonClickedPrivacy(View view) {
//        // Is the button now checked?
//        boolean checked = ((RadioButton) view).isChecked();
//
//        // Check which radio button was clicked
//        switch(view.getId()) {
//            case R.id.radio_public:
//                if (checked)
//                    mPrivacy = "public"; //set string to public
//                break;
//            case R.id.radio_private:
//                if (checked)
//                    mPrivacy = "private"; // set privacy string to private
//                break;
//        }
//    }
//
//    public void onRadioButtonClickedKindType(View view) {
//        // Is the button now checked?
//        boolean checked = ((RadioButton) view).isChecked();
//
//        // Check which radio button was clicked
//        switch(view.getId()) {
//            case R.id.radio_received:
//                if (checked)
//                    mType = "received";
//                //set string to received
//                break;
//            case R.id.radio_given:
//                if (checked)
//                    mType = "given";
//                // set string to given
//                break;
//        }
//    }

    public void sendBackResult() {
        // Notice the use of `getTargetFragment` which will be set when the dialog is displayed
        TagDialogListener listener = (TagDialogListener) getTargetFragment();
        assert listener != null;
        listener.onFinishTagDialog(mType, mPrivacy);
        dismiss();
    }
}


