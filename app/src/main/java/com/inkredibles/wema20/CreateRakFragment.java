package com.inkredibles.wema20;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.inkredibles.wema20.models.Rak;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseRole;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
This fragment allows the user to create their own random act of kindness either for themselves
as an alternative to the Rak of the day or as a rak for a group to do together.

TODO implement a time and place aspect
 */

public class CreateRakFragment extends Fragment implements DateTimeListener{
    @BindView(R.id.createRakTxt) EditText createRakTxt;
    @BindView(R.id.createBtn) Button createBtn;
    @BindView(R.id.createLayout) FrameLayout frameLayout;

    private ParseGeoPoint geoPoint;
    private onItemSelectedListener listener;

    private PlaceAutocompleteFragment autocompleteFragment;
    private TimePickerFragment timePickerFragment;
    private String placeName;
    private static View view;
    private static Calendar cal;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (autocompleteFragment != null)autocompleteFragment.setText("");
        // Defines the xml file for the fragment
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_create_rak, container, false);
        } catch (InflateException e) {
            /* map is already there, just return view as it is */
        }
        return view;

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        cal = Calendar.getInstance();



    }

    //function to load a properly sized background image:
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 3;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    @Override
    public void onResume() {
        super.onResume();
        createRakTxt.setText("");
    }

    @OnClick(R.id.createBtn)
    protected void createRak() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        dateFormat.setTimeZone(cal.getTimeZone());
        String dateString = dateFormat.format(cal.getTime());





        Bundle bundle = this.getArguments();
        if(bundle != null && bundle.getBoolean("isGroup")){
            createGroupRak(bundle);

        }else{
            //complete normal flow of creating a rak
            listener.addRakToServer(createRakTxt.getText().toString(), dateString);
        }





    }

    @OnClick(R.id.btnDate)
    public void showDatePickerDialog() {
        FragmentManager fm = getFragmentManager();
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.setTargetFragment(CreateRakFragment.this, 300);
        newFragment.show(fm, "datePicker");



    }

    @OnClick(R.id.btnTime)
    public void showTimePickerDialog() {
        FragmentManager fm = getFragmentManager();
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.setTargetFragment(CreateRakFragment.this, 300);
        newFragment.show(fm, "timePicker");
    }

    @Override
    public void dateSet(Calendar c){
        cal.set(Calendar.YEAR, c.get(Calendar.YEAR));
        cal.set(Calendar.MONTH, c.get(Calendar.MONTH));
        cal.set(Calendar.DATE, c.get(Calendar.DATE));
    }

    @Override
    public void timeSet(Calendar c){
        cal.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, c.get(Calendar.MINUTE));
    }



    //create a new Group Rak and save to Parse
    private void createGroupRak(Bundle bundle) {
        final ParseRole currentRole = bundle.getParcelable("currentRole");
        Rak groupRak = new Rak();
        groupRak.setTitle(createRakTxt.getText().toString());
        groupRak.setUser(ParseUser.getCurrentUser());
        groupRak.setRole(currentRole);

        groupRak.saveInBackground(
                new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.d("CreateRakFragment", "create grouprak success");
                            Toast.makeText(getActivity(), "Group Rak Created", Toast.LENGTH_SHORT).show();
                            createRakTxt.setText("");
                            listener.toCurrentGroup(currentRole);

                        } else {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(this.getArguments() != null){this.getArguments().clear(); }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof onItemSelectedListener) {
            listener = (onItemSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement OnItemSelectedListener");
        }
    }



}


