package com.inkredibles.wema20;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.inkredibles.wema20.models.User;
import com.parse.ParseException;
import com.parse.SignUpCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
    When the user clicks the signup button, this activity is launched to allow the user to create a new username, password, and
    attach their account to their email. Eventually we may to to be able to sync this with facebook and google sign in.
    If there are no issues, the user's information is saved in the parse user class on the parse server and the app opens to the RAK
    of the day.
 */

public class SignupActivity extends AppCompatActivity {

    @BindView(R.id.et_email) EditText et_email;
    @BindView(R.id.et_username) EditText et_username;
    @BindView (R.id.et_password) EditText et_password;
    @BindView(R.id.signup_background) ImageView signupBackground;

    private String email;
    private String username;
    private String password;
    //is this good practice?
    LoginActivity loginActivity = new LoginActivity();





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ButterKnife.bind(this);

       signupBackground.setImageBitmap(
                loginActivity.decodeSampledBitmapFromResource(getResources(), R.drawable.signup_new, 500, 600));
    }

    @OnClick(R.id.btn_signup)
    protected void signupbtn() {
         email = et_email.getText().toString();
         username = et_username.getText().toString();
         password = et_password.getText().toString();

        SignUp();



    }
    public void SignUp(){
        // Create the ParseUser
        User user = new User();
        // Set core properties
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(SignupActivity.this, "new user created", Toast.LENGTH_LONG).show();

                    Log.i("Signup activity", "user created");
                    startActivity(new Intent(SignupActivity.this, HomeActivity.class));
                    //TODO start the main activity
                    // Hooray! Let them use the app now.

                } else {
                    Toast.makeText(SignupActivity.this, "user not created-sign!", Toast.LENGTH_LONG).show();
                    et_email.setText("");
                    et_password.setText("");
                    et_username.setText("");
                    Log.i("signup activity", "user not created");
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    e.printStackTrace();
                }
            }
        });
    }

//    //function to load a properly sized background image:
//    public static int calculateInSampleSize(
//            BitmapFactory.Options options, int reqWidth, int reqHeight) {
//        // Raw height and width of image
//        final int height = options.outHeight;
//        final int width = options.outWidth;
//        int inSampleSize = 1;
//
//        if (height > reqHeight || width > reqWidth) {
//
//            final int halfHeight = height / 2;
//            final int halfWidth = width / 2;
//
//            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
//            // height and width larger than the requested height and width.
//            while ((halfHeight / inSampleSize) >= reqHeight
//                    && (halfWidth / inSampleSize) >= reqWidth) {
//                inSampleSize *= 2;
//            }
//        }
//
//        return inSampleSize;
//    }
//
//
//    //extract this out to its own class!- used in both login and signup. Should only be defined once
//    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
//                                                         int reqWidth, int reqHeight) {
//
//        // First decode with inJustDecodeBounds=true to check dimensions
//        final BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeResource(res, resId, options);
//
//        // Calculate inSampleSize
//        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
//
//        // Decode bitmap with inSampleSize set
//        options.inJustDecodeBounds = false;
//        return BitmapFactory.decodeResource(res, resId, options);
//    }


}
