package com.inkredibles.wema20;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.inkredibles.wema20.models.Rak;
import com.inkredibles.wema20.models.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

public class RakFragment extends Fragment {


    @BindView(R.id.rakTxt) TextView rakTxt;
    @BindView(R.id.refreshBtn) ImageButton refreshBtn;
    @BindView(R.id.feedBtn) ImageButton feedBtn;
    @BindView(R.id.doLaterBtn) ImageButton doLaterBtn;
    @BindView(R.id.doneBtn) ImageButton doneBtn;

    int total;
    private  Random rand;
    Button newRakBtn;
    ArrayList<Rak> rakList;
    ArrayList<User> userList;
    private static Rak rak;
    String text;
    boolean created = false;

    ParseQuery<Rak> query;

    private onItemSelectedListener listener;

    public final static String API_BASE_URL = "https://api.unsplash.com/";
    public final static String OATH_URL = "https://unsplash.com/oauth/authorize";
    //public final static String API_KEY_PARAM = "api_key";


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

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String title = bundle.getString("new_rak_title");
            rakTxt.setText(title);

            Rak newRak = new Rak();
            newRak.setTitle(title);
            newRak.setUser(ParseUser.getCurrentUser());
            newRak.saveInBackground();

        } else {
            query.findInBackground(new FindCallback<Rak>() {
                @Override
                public void done(List<Rak> objects, ParseException e) {
                    if (e == null) {
                        Log.d("FindSuccessful", "Finding RAK Successful");
                        //initialize array list
                        rakList.addAll(objects);

                        if(rak == null) {
                            rak = RAKGenerator(rakList, rakList.size(), false);
                        }
                        rakTxt.setText(rak.getTitle());
                        //add RAK field to current user
                        ParseUser user = ParseUser.getCurrentUser();

                        if (user.get("current_rak") == null) {
                            user.put("current_rak", rak);
                            user.saveInBackground();
                        }

                    } else {
                        Log.d("FindFailed", "Retrieving RAK unsuccessful");
                    }
                }
            });
            getPopularPhoto();
        }

    }

    @Override
    public void onResume() {
            super.onResume();

    }

    private void getAuthorization() {
        String url = OATH_URL;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("client_id", R.string.api_key);
        params.put("redirect_uri", R.string.redirect_uri);
        //params.put("response_type", )
    }
    private void getPopularPhoto() {
        String url = API_BASE_URL + "photos/random/?client_id=" + R.string.api_key;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        //params.put("client_id", R.string.api_key);
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("RequestSuccess", "Unsplash request successful");
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
        Rak rak = RAKGenerator(rakList, rakList.size(), false);
        System.out.println(rakList.size());

        rakTxt.setText(rak.getTitle());
    }

    @OnClick(R.id.feedBtn)
    protected void goToFeed() {
        listener.toFeed();
    }


    @OnClick(R.id.doneBtn)
    protected void goToPost() {
        listener.fromRAKtoCreatePost(rak);
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
