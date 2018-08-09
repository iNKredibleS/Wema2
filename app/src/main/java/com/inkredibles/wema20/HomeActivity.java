package com.inkredibles.wema20;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

import com.inkredibles.wema20.models.Rak;
import com.inkredibles.wema20.models.User;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*This is the onboarding page. The user gets a welcome message to Wema */
public class HomeActivity extends AppCompatActivity {
    @BindView(R.id.continueBtn)
    Button continueBtn;
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
                    Log.d("Error", e.toString());
                }
            }
        });

    }

    //continue button handler
    @OnClick(R.id.continueBtn)
    protected void launchWema() {
        final Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
