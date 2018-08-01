package com.inkredibles.wema20;


import android.app.Application;

import com.inkredibles.wema20.models.NewFile;
import com.inkredibles.wema20.models.Post;
import com.inkredibles.wema20.models.Rak;
import com.inkredibles.wema20.models.User;
import com.parse.Parse;
import com.parse.ParseObject;
/*
    This class intitializes the parse SDK. We use Heruko as our back end server to push our Parse data to.
    According to the code path guide "building data driven apps with parse" the possible built in functions
    avialable to us using parse are "User registration and authentication, Connecting user with Facebook to create a user account,
    Creating, querying, modifying and deleting arbitrary data models, Makes sending push notifications easier,
    Uploading files to a server for access across clients"
 */


public class ParseApp extends Application {

    //first entry point of our application
    @Override
    public void onCreate() {
        super.onCreate();

        //custom parse model that we need to implement
        ParseObject.registerSubclass(Rak.class);
        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(User.class);
//        ParseObject.registerSubclass(Group.class);

        //set up parse...clientKey is masterkey
        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("wema")
                .clientKey("RAKAttackSNK")
                .server("http://wema-rak.herokuapp.com/parse")
                .build();


        //intialize the parse
        Parse.initialize(configuration);

        //PushService.setDefaultPushCallback(this, MainActivity.class);

    }
}

