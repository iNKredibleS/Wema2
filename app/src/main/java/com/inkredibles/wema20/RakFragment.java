package com.inkredibles.wema20;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
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

import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.inkredibles.wema20.models.Rak;
import com.inkredibles.wema20.models.User;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRole;
import com.parse.ParseUser;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class RakFragment extends Fragment {
    @BindView(R.id.rakTxt)
    TextView rakTxt;
    @BindView(R.id.refreshBtn)
    ImageButton refreshBtn;
    @BindView(R.id.feedBtn)
    ImageButton feedBtn;
    @BindView(R.id.doLaterBtn)
    ImageButton doLaterBtn;
    @BindView(R.id.doneBtn)
    ImageButton doneBtn;
    @BindView(R.id.rakLayout)
    RelativeLayout rlayout;
    @BindView(R.id.rak_bck)
    ImageView rackBck;

    private static final String CURRENT_RAK = "current_rak";
    private static final String TITLE = "title";
    public static final String ROLE_IDENTIFIER = "WtDPPCDLba";
    private static final int CHANNEL_ID = 100;
    private static BitmapDrawable background;
    private static int currentBck;
    private Random rand;
    private Button newRakBtn;
    private ArrayList<Rak> rakList;
    private ArrayList<User> userList;
    private static Rak rak;
    private String text;
    boolean created = false;
    private String reg_url;
    private Date date;
    private File filesDir;
    private Bundle bundle;
    private ParseQuery<Rak> query;
    private onItemSelectedListener listener;
    private Bitmap myBitmap;
    private ParseFile rakFile;
    private User user;
    private int completed;
    private PlaceAutocompleteFragment autocompleteFragment;
    private NotificationCompat.Builder notification;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rak, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        filesDir = getContext().getFilesDir();
        query = ParseQuery.getQuery(Rak.class);
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

        //if you create a new RAK with the create button
        bundle = this.getArguments();
        if (bundle != null && bundle.size() != 0) {
            String title = bundle.getString("new_rak_title");
            String dateString = bundle.getString("date");
            String location = bundle.getString("location");
            User user = (User) ParseUser.getCurrentUser();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            try {
                date = dateFormat.parse(dateString);
                if (date != null) {
                    Log.i("createrak", "date");
                }

            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }

            //create new Rak
            Rak newRak = new Rak();
            newRak.put(TITLE, title);
            newRak.setUser(user);
            newRak.setScheduleDate(date);
            newRak.setBackground(R.drawable.wemabck6);
            newRak.saveInBackground();

            //save new rak to current user
            user.setRak(newRak);
            user.saveInBackground();

            rakTxt.setText(title);
            currentBck = R.drawable.wemabck6;
            rackBck.setImageBitmap(
                    decodeSampledBitmapFromResource(getResources(), newRak.getBackground(), 500, 600));

        } else {

            query = ParseQuery.getQuery(Rak.class);
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


            User user = (User) ParseUser.getCurrentUser();

            Rak rak = null;
            try {
                rak = (Rak) user.fetchIfNeeded().get("current_rak");
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String title = null;
            try {
                title = rak.fetchIfNeeded().getString("title");
                rakTxt.setText(title);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Integer background = null;
            try {
                background = rak.fetchIfNeeded().getInt("current_background");
                rackBck.setImageBitmap(
                        decodeSampledBitmapFromResource(getResources(), background
                                , 500, 600));

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        if (this.getArguments() != null) {
            this.getArguments().clear();
        }


    }


    @Override
    public void onResume() {
        super.onResume();
        query = ParseQuery.getQuery(Rak.class);
        query.findInBackground(new FindCallback<Rak>() {
            @Override
            public void done(List<Rak> objects, ParseException e) {
                if (e == null) {
                    Log.d("FindSuccessful", "Finding RAK Successful");
                    rakList.addAll(objects);

                } else {
                    Log.d("FindFailed", "Retrieving RAK unsuccessful");
                }
            }
        });

    }

    //function to load a properly sized background image:
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
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


    //onclick handler for the refresh button
    @OnClick(R.id.refreshBtn)
    protected void createButtonClick() {
        User user = (User) ParseUser.getCurrentUser();
        Rak rak = RAKGenerator(rakList, rakList.size(), true);
        user.setRak(rak);
        user.saveInBackground();

        rakTxt.setText(rak.getTitle());
        rak.setBackground(R.drawable.wemabck2);
        rak.saveInBackground();
        rackBck.setImageBitmap(
                decodeSampledBitmapFromResource(getResources(), R.drawable.wemabck2, 500, 600));
    }

    //onclick handler for the feed button
    @OnClick(R.id.feedBtn)
    protected void goToFeed() {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getContext().getApplicationContext(), "notify_001");
        Intent ii = new Intent(getActivity(), MainActivity.class);
        ii.putExtra("groupFragment", ROLE_IDENTIFIER);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getContext());
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(ii);


        //PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, ii, 0);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(100, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText("Click here to see the new Rak");
        bigText.setBigContentTitle("A new Rak has been added to Power Rangers group");
        bigText.setSummaryText("RAK");

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        mBuilder.setContentTitle("Reminder to complete Rak of the Day");
        mBuilder.setContentText("Swipe here to complete your Rak and write your reflection! ");
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setAutoCancel(false);
        mBuilder.setOngoing(true);
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

    //on click handler for the later button
    @OnClick(R.id.doLaterBtn)
    protected void goToFeedDoLater() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getContext().getApplicationContext(), "notify_001");
        Intent ii = new Intent(getActivity(), MainActivity.class);
        ii.putExtra("rakFragment", "rakFragment");

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getContext());
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(ii);


        //PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, ii, 0);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(100, PendingIntent.FLAG_IMMUTABLE);

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        mBuilder.setContentTitle("Reminder to complete Rak of the day");
        mBuilder.setContentText("Click here to complete your Rak and write your reflection!");
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setAutoCancel(false);
        mBuilder.setOngoing(true);
//        mBuilder.setStyle(bigText);
        mBuilder.setTicker("Reminder to complete Rak of the day");

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

    //onclick handler for the done button
    @OnClick(R.id.doneBtn)
    protected void goToPost() {
        User user = (User) ParseUser.getCurrentUser();

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getContext().getApplicationContext(), "notify_001");
        Intent ii = new Intent(getActivity(), MainActivity.class);
        ii.putExtra("groupFragment", ROLE_IDENTIFIER);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getContext());
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(ii);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(100, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText("Click here to see their current Raks and posts");
        bigText.setBigContentTitle("You've been added to the Power Rangers group");
        bigText.setSummaryText("RAK");

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        mBuilder.setContentTitle("Reminder to complete Rak of the Day");
        mBuilder.setContentText("Swipe here to complete your Rak and write your reflection! ");
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);
        mBuilder.setAutoCancel(false);
        mBuilder.setOngoing(true);

        NotificationManager mNotificationManager =
                (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("notify_001",
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(channel);
        }

        mNotificationManager.notify(0, mBuilder.build());

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
    private Rak RAKGenerator(ArrayList<Rak> list, int size, boolean refresh) {
        String title = "";
        int randomNum = rand.nextInt(list.size()) + 1; //get random rak from list
        int current = randomNum;

        //change random number so RAK won't refresh to the same/current RAK
        if (refresh) {
            while (randomNum == current) {
                randomNum = rand.nextInt(size) + 1;
            }
        }
        //get title
        Rak rak = rakList.get(randomNum - 1);
        return rak;
    }


    //This method initializes the onItemSelectedListener
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
    public void onDestroy() {
        super.onDestroy();
        if (this.getArguments() != null) {
            this.getArguments().clear();
        }
    }
}
