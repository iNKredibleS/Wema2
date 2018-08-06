package com.inkredibles.wema20;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.inkredibles.wema20.models.Rak;
import com.inkredibles.wema20.models.User;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class RakFragment extends Fragment {


    @BindView(R.id.rakTxt) TextView rakTxt;
    @BindView(R.id.refreshBtn) ImageButton refreshBtn;
    @BindView(R.id.feedBtn) ImageButton feedBtn;
    @BindView(R.id.doLaterBtn) ImageButton doLaterBtn;
    @BindView(R.id.doneBtn) ImageButton doneBtn;
    @BindView(R.id.rakLayout) RelativeLayout rlayout;
    //@BindView(R.id.notifyBtn) Button notifyBtn;
    @BindView(R.id.rak_bck) ImageView rackBck;
    @BindView(R.id.dateTxt) TextView dateTxt;
    @BindView(R.id.locationTxt) TextView locationTxt;

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

    ParseFile rakFile;

    static int currentBck;

    User user;

    private PlaceAutocompleteFragment autocompleteFragment;

    NotificationCompat.Builder notification;
    private static final int CHANNEL_ID = 100;

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

    private void createNotification(int nId, int iconRes, String title, String body) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                getContext()).setSmallIcon(iconRes)
                .setContentTitle(title)
                .setContentText(body);
        NotificationManager mNotificationManager =
                (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(nId, mBuilder.build());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        query = ParseQuery.getQuery(Rak.class);
        rakList = new ArrayList();
        userList = new ArrayList();
        rand = new Random();

        notification = new NotificationCompat.Builder(getContext());
        notification.setAutoCancel(true);


//        // Create an explicit intent for an Activity in your app
//        Intent intent = new Intent(getActivity(), RakFragment.class);
//        intent.putExtra("rakFragment", "launchRak");
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, 0);

//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getContext())
//                .setSmallIcon(R.drawable.ic_launcher_background)
//                .setContentTitle("My notification")
//                .setContentText("Hello World!")
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                // Set the intent that will fire when the user taps the notification
//                //.setContentIntent(pendingIntent)
//                .setAutoCancel(true);



        //if you create a new RAK with the create button
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String title = bundle.getString("new_rak_title");
            String date = bundle.getString("date");
            String location = bundle.getString("location");
            User user = (User) ParseUser.getCurrentUser();

            rakTxt.setText(title);
            if(date != null) {
                dateTxt.setText(date);
            }
            if(location != null) {
                locationTxt.setText(location);
            }

            //create new Rak
            Rak newRak = new Rak();
            newRak.setTitle(title);
            newRak.setUser(user);
            newRak.setBackground(R.drawable.wemabck5);
            newRak.saveInBackground();

            //save new rak to current user
           user.setRak(newRak);

           rakTxt.setText(title);

           currentBck = newRak.getBackground();
           rackBck.setImageBitmap(
                    decodeSampledBitmapFromResource(getResources(), newRak.getBackground(),
                            500, 600));
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
                            rak.setBackground(R.drawable.wemabck0);
                            rak.saveInBackground();
                            Log.d("RakGenerator", "Generating random RAK successful");

                        }
                        //add RAK field to current user
                        User user = (User) ParseUser.getCurrentUser();


                        if (user.get("current_rak") == null) {
                            user.put("current_rak", rak);
                            user.saveInBackground();
                            Log.d("RakToUser", "Adding rak to user successful");
                        }

                    } else {
                        Log.d("FindFailed", "Retrieving RAK unsuccessful");
                    }
                }
            });

            User user = (User) ParseUser.getCurrentUser();

            Rak rak = null;
            try {
                rak = (Rak) user.fetchIfNeeded().get("current_rak");
            } catch (ParseException e) {
                e.printStackTrace();
            }

            ParseQuery<Rak> query = ParseQuery.getQuery(Rak.class);
            query.getInBackground(rak.getObjectId(), new GetCallback<Rak>() {
                public void done(Rak object, ParseException e) {
                    if (e == null) {
                        Log.d("Success", "Finding user to set title and background Success");
                        rakTxt.setText(object.getTitle());

                        rackBck.setImageBitmap(
                        decodeSampledBitmapFromResource(getResources(), object.getBackground(),
                                500, 600));

                        currentBck = object.getBackground();
                    } else {
                        Log.d("Fail", "Finding RAK failed");
                    }
                }
            });

        }

        query.findInBackground(new FindCallback<Rak>() {
            @Override
            public void done(List<Rak> objects, ParseException e) {
                if (e == null) {
                    Log.d("FindSuccessful", "Finding RAK Successful");
                    //initialize array list
                    rakList.addAll(objects);
                } else {
                    Log.d("FindFailed", "Retrieving RAK unsuccessful");
                }
            }
        });

    }


    @Override
    public void onResume() {
            super.onResume();

            User user = (User) ParseUser.getCurrentUser();

            Rak rak = null;
            try {
             rak = (Rak) user.fetchIfNeeded().get("current_rak");
            } catch (ParseException e) {
            e.printStackTrace();
            }

        try {
            rakTxt.setText(rak.fetchIfNeeded().getString("title"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        rackBck.setImageBitmap(
                decodeSampledBitmapFromResource(getResources(),rak.getBackground(),
                        500, 600));


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
                 R.drawable.wemabck6};

        int randomNum = rand.nextInt(5) + 1;

        while(randomNum == currentBck ) {
            randomNum = rand.nextInt(5) + 1;
        }

        int newBckg = randomBckg[randomNum];

        Rak r = null;

        try {
            r = (Rak) user.fetchIfNeeded().get("current_rak");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        r.setBackground(newBckg);
        r.saveInBackground();

        rackBck.setImageBitmap(
                decodeSampledBitmapFromResource(getResources(), r.getBackground(), 500, 600));

        createNotification(CHANNEL_ID, R.drawable.ic_launcher_background, "Rak", "Complete your RAK before the day is over!");

    }

    @OnClick(R.id.feedBtn)
    protected void goToFeed() {
        listener.toFeed();
    }

    @OnClick(R.id.doLaterBtn)
    protected void goToFeedDoLater() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getContext().getApplicationContext(), "notify_001");
        Intent ii = new Intent(getContext().getApplicationContext(), RakFragment.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, ii, 0);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText("Click here to complete your Rak and write your reflection! ");
        bigText.setBigContentTitle("Reminder to complete Rak of the Day");
        bigText.setSummaryText("RAK");

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        mBuilder.setContentTitle("Your Title");
        mBuilder.setContentText("Your text");
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);

        NotificationManager mNotificationManager =
                (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("notify_001",
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(channel);
        }

        mNotificationManager.notify(0, mBuilder.build());

        listener.toFeed();
    }

    @OnClick(R.id.doneBtn)
    protected void goToPost()  {
        User user = (User) ParseUser.getCurrentUser();

        listener.fromRAKtoCreatePost(user.getRak());

        try {
            listener.fromRAKtoCreatePost((Rak) user.fetchIfNeeded().get("current_rak"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
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

}
