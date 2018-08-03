package com.inkredibles.wema20;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.inkredibles.wema20.models.Rak;
import com.inkredibles.wema20.models.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

import static com.inkredibles.wema20.R.drawable.wemabck0;


public class RakFragment extends Fragment {


    @BindView(R.id.rakTxt) TextView rakTxt;
    @BindView(R.id.refreshBtn) ImageButton refreshBtn;
    @BindView(R.id.feedBtn) ImageButton feedBtn;
    @BindView(R.id.doLaterBtn) ImageButton doLaterBtn;
    @BindView(R.id.doneBtn) ImageButton doneBtn;
    @BindView(R.id.rakLayout) RelativeLayout rlayout;
    //@BindView(R.id.notifyBtn) Button notifyBtn;
    @BindView(R.id.rak_bck) ImageView rackBck;

    private  Random rand;
    Button newRakBtn;
    ArrayList<Rak> rakList;
    ArrayList<User> userList;
    private static Rak rak;
    String text;
    boolean created = false;
    String reg_url;

    File filesDir;

    ParseQuery<Rak> query;

    static BitmapDrawable background;

    private onItemSelectedListener listener;
    Bitmap myBitmap;
    ParseFile backgroundImage = null;

    ParseFile rakFile;

    static int currentBck;

    User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rak, container, false);

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        filesDir = getContext().getFilesDir();

        query = ParseQuery.getQuery(Rak.class);
        //query.setLimit(10);

        rakList = new ArrayList<Rak>();


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        query = ParseQuery.getQuery(Rak.class);
        rakList = new ArrayList();
        userList = new ArrayList();
        rand = new Random();

        //if you create a new RAK with the create button
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String title = bundle.getString("new_rak_title");
            User user = (User) ParseUser.getCurrentUser();

            rakTxt.setText(title);

            //create new Rak
            Rak newRak = new Rak();
            newRak.setTitle(title);
            newRak.setUser(user);
            newRak.saveInBackground();

            //save new rak to current user
            //user.setRak(newRak);
            user.put("current_rak", newRak);
        //loads the normal rak
        } else {
            query.findInBackground(new FindCallback<Rak>() {
                @Override
                public void done(List<Rak> objects, ParseException e) {
                    if (e == null) {
                        Log.d("FindSuccessful", "Finding RAK Successful");
                        //initialize array list
                        rakList.addAll(objects);

                        if (rak == null) {
                            rak = RAKGenerator(rakList, rakList.size(), false);

                        }
                        //add RAK field to current user
                        //User user = (User) ParseUser.getCurrentUser();

                        user.getUsername();

                        if (user.get("current_rak") == null) {
                            user.put("current_rak", rak);
                            user.saveInBackground();
                        }

                        getPopularPhoto();
                        Rak r = null;
                        rakTxt.setText(rak.getTitle());

                        try {
                            r = (Rak) user.fetchIfNeeded().get("current_rak");
                        } catch (ParseException e1) {
                            e.printStackTrace();
                        }

                        try {
                            if (r.fetchIfNeeded().get("current_background") == null) {
                                r.put("current_background", R.drawable.wemabck5);
                                r.saveInBackground();
                            }
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }


                        try {
                            rackBck.setImageBitmap(
                                    decodeSampledBitmapFromResource(getResources(), r.fetchIfNeeded().getInt("current_background"), 500, 600));
                        } catch (ParseException e2) {
                            e.printStackTrace();
                        }


                    } else {
                        Log.d("FindFailed", "Retrieving RAK unsuccessful");
                    }
                }
            });


        }

    }


    @Override
    public void onResume() {
            super.onResume();


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


    private void getPopularPhoto() {
        String url ="https://api.unsplash.com/photos/random/?client_id=" + getString(R.string.api_key);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("collections", "1922729" );
        params.put("w", 320);
        params.put("h", 420);
        // 981639"

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("RequestSuccess", "Unsplash request successful");

                try {
                   JSONObject urls =  response.getJSONObject("urls");
                   reg_url = urls.getString("small");

                   new BitmapOperation().execute(reg_url);
                   // rlayout.setBackground(background);

                } catch (JSONException e) {
                    Log.d("FetchFailed", "Fetching urls failed");
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.d("RequestFailure", "Unsplash request failed");
            }


        });
    }


    @OnClick(R.id.refreshBtn)
    protected void createButtonClick(){
        Rak rak = RAKGenerator(rakList, rakList.size(), true);
        User user = (User) ParseUser.getCurrentUser();
        user.setRak(rak);
        user.saveInBackground();

        try {
            rakTxt.setText( user.getRak().fetchIfNeeded().getString("title"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int[] randomBckg = {R.drawable.wemabck0, R.drawable.wemabck1, R.drawable.wemabck2, R.drawable.wemabck3, R.drawable.wemabck4,
                 R.drawable.wemabck5, R.drawable.wemabck6};

        int randomNum = rand.nextInt(6) + 1;

        while(randomNum == currentBck ) {
            randomNum = rand.nextInt(6) + 1;
        }

        int newBckg = randomBckg[randomNum];

        rackBck.setImageBitmap(
                decodeSampledBitmapFromResource(getResources(), newBckg, 500, 600));

        Rak r = null;

        try {
            r = (Rak) user.fetchIfNeeded().get("current_rak");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        r.put("current_background", newBckg);
        r.saveInBackground();


    }

    @OnClick(R.id.feedBtn)
    protected void goToFeed() {
        listener.toFeed();
    }


    @OnClick(R.id.doneBtn)
    protected void goToPost()  {
        User user = (User) ParseUser.getCurrentUser();

        listener.fromRAKtoCreatePost(user.getRak());

    }

    @OnClick(R.id.newRakBtn)
    protected void createRak() {
        listener.toCreateRak();
    }


    //This method returns a random RAK
    private  Rak RAKGenerator(ArrayList<Rak> list,  int size, boolean refresh) {
        String title = "";

        //get random rak from list
        int randomNum = rand.nextInt(rakList.size()) + 1;
        int current = randomNum;

        //change random number so RAK won't refresh to the same/current RAK
        if(refresh) {
            while (randomNum == current) {
                randomNum = rand.nextInt(size) + 1;
            }
        }

        //get title
        Rak rak = rakList.get(randomNum - 1);


        return rak;
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


    private class BitmapOperation extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                myBitmap = BitmapFactory.decodeStream(input);

                return myBitmap;

            } catch (IOException e) {
                // Log e
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap result) {

            File file = persistImage(result, "rakPic");
            rakFile = new ParseFile(file);

            User user = (User) ParseUser.getCurrentUser();

            ParseQuery<Rak> query = ParseQuery.getQuery(Rak.class);
            query.getInBackground(user.getRak().getObjectId(), new GetCallback<Rak>() {
                public void done(Rak object, ParseException e) {
                    if (e == null) {
                        Log.d("RakSuccess", "Finding current rak succesful");
                        object.setImage(rakFile);
                        object.saveInBackground();



                    } else {
                        Log.d("RakFailure", "Finding current rak failed");
                    }
                }
            });





        }

//        public static Bitmap scaleBitmap(Bitmap bitmap, int newWidth, int newHeight) {
//            Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Config.ARGB_8888);
//
//            float scaleX = newWidth / (float) bitmap.getWidth();
//            float scaleY = newHeight / (float) bitmap.getHeight();
//            float pivotX = 0;
//            float pivotY = 0;
//
//            Matrix scaleMatrix = new Matrix();
//            scaleMatrix.setScale(scaleX, scaleY, pivotX, pivotY);
//
//            Canvas canvas = new Canvas(scaledBitmap);
//            canvas.setMatrix(scaleMatrix);
//            canvas.drawBitmap(bitmap, 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG));
//
//            return scaledBitmap;
//        }


    }
    private File  persistImage(Bitmap bitmap, String name) {
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

}
