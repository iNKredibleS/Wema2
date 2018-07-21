package com.inkredibles.wema20;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.inkredibles.wema20.models.Rak;
import com.parse.FindCallback;
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

    int total, current;
    Random rand;
    Button newRakBtn;
    ArrayList<Rak> rakList;

    ParseQuery<Rak> query;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rak, container, false);

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //put basically all the code here

        ButterKnife.bind(this, view);



        query = ParseQuery.getQuery(Rak.class);
        //query.setLimit(10);

        rakList = new ArrayList<Rak>();

        //get total number of Rak objects from server
        try {
            total = query.count();

        } catch (ParseException e) {
            e.printStackTrace();
        }

        rand = new Random();


        //set text box initially
        query.findInBackground(new FindCallback<Rak>() {
            @Override
            public void done(List<Rak> objects, ParseException e) {
                if(e == null) {
                    Log.d("FindSuccessful", "Finding RAK Successful");
                    int randomNum = rand.nextInt(total) + 1;

                    Rak currentRak = objects.get(randomNum - 1);

                    //store current randomNum so dont refresh to the same thing
                    current = randomNum;

                    //set text box
                    String title = currentRak.getTitle();

                    //rakTxt = view.findViewById(rakTxt);
                    rakTxt.setText(title);

                } else {
                    Log.d("FindFailed", "Retrieving RAK unsuccessful");
                }


            }
        });
        
        //add ability to create new RAK and post to database\
        //newRakBtn = findViewById(R.id.newRakBtn);

        /*newRakBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Intent i = new Intent(RAKActivity.this, NewRakPost.class);
               // startActivityForResult(i, REQUEST_CODE);
            }
        });*/

    }

    @OnClick(R.id.refreshBtn)
    protected void createButtonClick(){

        query.findInBackground(new FindCallback<Rak>() {
            @Override
            public void done(List<Rak> objects, ParseException e) {
                if (e == null) {
                    Log.d("FindSuccessful", "Finding RAK Successful");

                    int randomNum = rand.nextInt(total) + 1;

                    while (randomNum == current) {
                        randomNum = rand.nextInt(total) + 1;
                    }


                    Rak currentRak = objects.get(randomNum - 1);
                    String title = currentRak.getTitle();

                    rakTxt.setText(title);

                } else {
                    Log.d("FindFailed", "Retrieving RAK unsuccessful");
                }


            }

        });

    }

    private String RAKGenerator(ParseQuery<Rak> query, final int total) {
        final String title = "";

        query.findInBackground(new FindCallback<Rak>() {
            @Override
            public void done(List<Rak> objects, ParseException e) {
                if(e == null) {
                    Log.d("FindSuccessful", "Finding RAK Successful");
                    int randomNum = rand.nextInt(total) + 1;

                    Rak currentRak = objects.get(randomNum - 1);
                    //ask angie
                    //title = currentRak.getTitle();


                } else {
                    Log.d("FindFailed", "Retrieving RAK unsuccessful");
                }


            }
        });

        return title;
    }





}
