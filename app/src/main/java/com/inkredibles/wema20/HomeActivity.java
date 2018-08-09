package com.inkredibles.wema20;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.inkredibles.wema20.models.Rak;
import com.inkredibles.wema20.models.User;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends AppCompatActivity {


    @BindView(R.id.continueBtn) Button continueBtn;
    ArrayList<Rak> rakList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        ParseQuery<Rak> query = ParseQuery.getQuery(Rak.class);


        query.getInBackground("53mFlzVx3G", new GetCallback<Rak>() {
            public void done(Rak object, ParseException e) {
                if (e == null) {
                    User user = (User) ParseUser.getCurrentUser();

                    object.setBackground(R.drawable.wemabck6);
                    object.setUser(user);

                    user.setRak(object);
                    user.saveInBackground();

                    object.saveInBackground();
                } else {
                    // something went wrong
                }
            }
        });

    }



    @OnClick(R.id.continueBtn)
    protected void launchWema() {
        final Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }








}
