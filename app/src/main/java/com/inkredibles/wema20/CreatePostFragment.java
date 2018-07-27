package com.inkredibles.wema20;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Selection;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.inkredibles.wema20.models.Post;
import com.inkredibles.wema20.models.Rak;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseRole;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;
import static android.support.v4.content.ContextCompat.checkSelfPermission;

/*This Fragment handles the functionality to create posts. A reflection is composed of the title, body, location and an image. A user can
 *  also set if the reflection is for an act of kindness given or received. In addition, they can set it to be private or public. If it
  *  is private, only they can see it in their archive.*/
public class CreatePostFragment extends Fragment {

    @BindView(R.id.Title) EditText et_title;
    @BindView(R.id.et_message) EditText et_message;
    @BindView(R.id.switch_give_rec) Switch switch_give_rec;
    @BindView(R.id.switch_pub_pri) Switch switch_pub_pri;
    @BindView(R.id.pictureHolder) ImageView pictureHolder;
    @BindView(R.id.tv_give_rec) TextView tvGiveRec;
    @BindView(R.id.tv_pub_pri) TextView tvPubPri;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_GALLERY_IMAGE = 2;
    private static File filesDir;
    private String type;
    private String privacy;
    private onItemSelectedListener listener;
    private ParseGeoPoint geoPoint;
    private String placeName;
    Rak rak;
    private ParseRole currentRole;
    private Bundle bundle;


    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private ParseFile parseFile;
    private File file;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_create_post, parent, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        //butterknife bind
        ButterKnife.bind(this, view);

        setUpView();

    }

    @Override
    public void onResume() {
        super.onResume();

        setUpView();

    }


    public void setUpView() {

        filesDir = getContext().getFilesDir();

        bundle = this.getArguments();
        Boolean isGroup = bundle.getBoolean("isGroup");
        Boolean isReflection = bundle.getBoolean("isReflection");
        Boolean isRak = bundle.getBoolean("isRak");
        if (isGroup) {
            currentRole = bundle.getParcelable("currentRole");

        } else if (isRak) { //comes from rak
            rak = bundle.getParcelable("RAK");
            et_title.setText(rak.getTitle());
            //set the cursor position to end of input title
            int position = et_title.length();
            Editable etext = et_title.getText();
            Selection.setSelection(etext, position);
        } else if (isReflection) {
            //any Reflection specific posts
        } else {
            System.out.println("-------------");
        }

        //TODO change this to a radio button or spinner
        //setting up switches
        switch_pub_pri.setChecked(true);
        switch_give_rec.setChecked(true);
        tvGiveRec.setText("Given");
        tvPubPri.setText("Public");

        //default type and privacy values
        type = "give";
        privacy = "public";

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        if (autocompleteFragment != null) {
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {
                    // TODO: Get info about the selected place.
                    LatLng latLong = place.getLatLng(); //get a lat long
                    geoPoint = new ParseGeoPoint(latLong.latitude, latLong.longitude); //use a latlong to get a parsegeopoint
                    placeName = place.getName().toString();
                }

                @Override
                public void onError(Status status) {
                    // TODO: Handle the error.
                    Log.i("CreatePost: ", "An error occurred: " + status);
                }
            });


        }
    }


    //when button changes the type, change the textview that displays type and the type field in
    //posts model
    @OnCheckedChanged(R.id.switch_give_rec)
    void giveRec(CompoundButton compoundButton, boolean checked){
        if(checked) {
            tvGiveRec.setText("Given");
            type = "give";
        } else {
            tvGiveRec.setText("Received");
            type = "receive";
        }

    }

    @OnCheckedChanged(R.id.switch_pub_pri)
    void pubPri(CompoundButton compoundButton, boolean checked){
        if(checked) {
            tvPubPri.setText("Public");
            privacy = "public";
        } else {
            tvPubPri.setText("Just for me");
            privacy = "private";
        }
    }




    //on post button clicked
    @OnClick(R.id.btn_post)
    protected void postButtonClicked(){
        final String title = et_title.getText().toString();
        final String message = et_message.getText().toString();
        final ParseUser user = ParseUser.getCurrentUser();
        final String finalPrivacy = privacy;
        final String finalType = type;
        if(file != null) parseFile = new ParseFile(file);
        final ParseRole role = currentRole;

        createPost(title, message, user, parseFile, finalPrivacy, finalType, role);


    }

    //launch activity to choose a photo from gallery
    @OnClick(R.id.btn_gallery)
    protected void gallery(){
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent , REQUEST_GALLERY_IMAGE);
    }

    //launch activity to take a picture for the post
    @OnClick(R.id.btn_camera)
    protected void camera() {
        if(isStoragePermissionGranted()) {
            dispatchTakePictureIntent();
        }
    }


    //create post and store to parse server
    //set the title, message, user, image, privacy, give, receive
    private void createPost(String title, String message, ParseUser user, ParseFile parseFile, String privacy, String type, ParseRole role) {
        final Post newPost = new Post();
        newPost.setTitle(title);
        newPost.setMessage(message);
        newPost.setUser(user);
        if(parseFile != null) newPost.setImage(parseFile);
        newPost.setPrivacy(privacy);
        newPost.setType(type);
        //newPost.setLocation(geoPoint);
 //       newPost.setPlaceName(placeName);
        if(role != null) newPost.setRole(role);

        newPost.saveInBackground(
                new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.d("CreatePostActivity", "create post success");
                            Toast.makeText(getActivity(), "Post Created", Toast.LENGTH_SHORT).show();
                            resetCreatePost();
                            listener.toFeed();

                        } else {
                            e.printStackTrace();
                        }
                    }
                });
    }

    protected void resetCreatePost() {
        et_message.setText("");
        et_title.setText("");
//        listener.setIsGroup(false);
//        listener.setIsRak(false);
//        listener.setIsReflection(false);
        bundle = null;
        currentRole = null;
        file = null;
        parseFile = null;

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            pictureHolder.setImageBitmap(imageBitmap);
            file = persistImage(imageBitmap, "pic1");
        }else if(requestCode == REQUEST_GALLERY_IMAGE && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            pictureHolder.setImageURI(imageUri);
            try {
                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                file = persistImage(imageBitmap, "pic2");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static File  persistImage(Bitmap bitmap, String name) {
        File imageFile = new File(filesDir, name + ".jpg");
        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.d("Create Post Fragment", "Error writing bitmap", e);
        }
        return imageFile;

    }





    /*Requests permission to read external storage*/
    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v("Create Post Fragment","Permission is granted");
                return true;
            } else {

                Log.v("Create Post Fragment","Permission is revoked");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else {
            Log.v("Create Post Fragment","Permission is granted");
            return true;
        }
    }

    /*dispatches an intent to take a picture*/
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    // Initializes the listener
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
    public void onDestroyView() {
        super.onDestroyView();
        this.getArguments().clear();
    }

}

