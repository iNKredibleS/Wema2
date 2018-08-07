package com.inkredibles.wema20;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.inkredibles.wema20.models.Rak;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseRole;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
This fragment allows the user to create their own random act of kindness either for themselves
as an alternative to the Rak of the day or as a rak for a group to do together.

TODO implement a time and place aspect
 */

public class CreateRakFragment extends Fragment implements TimePickerDialog.OnTimeSetListener{
    @BindView(R.id.createRakTxt) EditText createRakTxt;
    @BindView(R.id.timeTxt) EditText timeTxt;
    @BindView(R.id.createBtn) Button createBtn;

    private ParseGeoPoint geoPoint;
    private onItemSelectedListener listener;

    private PlaceAutocompleteFragment autocompleteFragment;
    private TimePickerFragment timePickerFragment;
    private String placeName;
    private static View view;

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
        setupAutoComplete();

    }

    @Override
    public void onResume() {
        super.onResume();
        createRakTxt.setText("");
    }

    @OnClick(R.id.createBtn)
    protected void createRak() {
        Bundle bundle = this.getArguments();
        if(bundle != null && bundle.getBoolean("isGroup")){
            createGroupRak(bundle);

        }else{
            //complete normal flow of creating a rak
            listener.addRakToServer(createRakTxt.getText().toString(), timeTxt.getText().toString(), placeName);
        }

    }

    //create a new Group Rak and save to Parse
    private void createGroupRak(Bundle bundle){
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

    /*Sets up the location autocomplete*/
    private void setupAutoComplete(){
        autocompleteFragment = (PlaceAutocompleteFragment) getActivity().getFragmentManager().findFragmentById(R.id.place_fragment);
        if (autocompleteFragment != null) {
            autocompleteFragment.onResume();
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {
                    LatLng latLong = place.getLatLng(); //get a lat long
                    geoPoint = new ParseGeoPoint(latLong.latitude, latLong.longitude); //use a latlong to get a parsegeopoint
                    placeName = place.getName().toString();
                }
                @Override
                public void onError(Status status) {
                    Log.i("CreatePost: ", "An error occurred: " + status);
                }

            });
        }
    }

    public void showTimeFragment(View v) {
        TimePickerFragment newFragment = new TimePickerFragment();


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


    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {

    }
}


