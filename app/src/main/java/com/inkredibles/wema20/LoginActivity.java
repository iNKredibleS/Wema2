package com.inkredibles.wema20;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
/*
    Allows the user to login to their Wema account. After successful logging in the user will be directed to the main activity and the
    RAK of the day fragment.
 */

public class LoginActivity extends AppCompatActivity {

    //various fields for logging in

    @BindView(R.id.etUsername)
    EditText usernameInput;
    @BindView(R.id.etPassword) EditText passwordInput;
    @BindView(R.id.btnLogIn)
    Button btnLogIn;
    @BindView(R.id.btnSignUp) Button btnSignUp;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        //persisted user code so the user doesn't have to log in
        // each time they open the app
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null){
            final Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }


    }

    @OnClick(R.id.btnLogIn)
    public void login() {
        final String username = usernameInput.getText().toString();
        final String password = passwordInput.getText().toString();

        ParseUser.logInInBackground(
                username,
                password,
                new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        // so we know that the network request is completed
                        if (e == null) {
                            Log.d("LoginActivity", "login in successful");
                            final Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "User not recognized either signup or login again", Toast.LENGTH_SHORT).show();
                            Log.e("LoginActivity", "login failure");
                            e.printStackTrace();
                        }
                    }
                });
    }

    @OnClick(R.id.btnSignUp)
    public void launchSignUp() {
        startActivity(new Intent(LoginActivity.this, SignupActivity.class));
    }
}
