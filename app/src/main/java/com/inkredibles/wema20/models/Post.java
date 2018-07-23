package com.inkredibles.wema20.models;


import android.text.format.DateUtils;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Date;

/*
    The data schema for posts created by users. Each post has a main text of the message, a privacy field, a type field
    (whether the kindness was given or received), a parse user that created the post, the time it was created, a RAK of the day pointer,
    and possibly an image if the user chooses to add one.
 */


@ParseClassName("Post")
public class Post extends ParseObject {


    //just a comment to test
    private static  final String KEY_NAME = "name";
    private static  final String KEY_MESSAGE = "message";
    private static  final String KEY_IMAGE = "image";
    private static  final String KEY_PRIVACY = "privacy";
    private static  final String KEY_TYPE = "type";
    private static  final String KEY_RAK = "rak";
    private static  final String KEY_CREATOR = "creator_user";
    private static  final String KEY_CREATED_AT = "createdAt";
    private static  final String KEY_TITLE = "title";

    public String getName() {return getString(KEY_NAME);}
    public void setName(String name) {
        put(KEY_NAME, name);
    }


    public  String getMessage(){
        return getString(KEY_MESSAGE);
    }

    public void setMessage(String message){
        put(KEY_MESSAGE, message);
    }

    public ParseFile getImage(){
        return getParseFile(KEY_IMAGE);
    }


    public void setImage(ParseFile image){
        put(KEY_IMAGE, image);
    }

    public String getPrivacy() {return getString(KEY_PRIVACY); }
    public void setPrivacy(String privacy){
        put(KEY_PRIVACY, privacy);
    }

    public String getType() {return getString(KEY_TYPE); }
    public void setType(String type) {put(KEY_TYPE, type);}

    public String getTitle() {return getString(KEY_TITLE); }
    public void setTitle(String title) {put(KEY_TITLE, title);}


    public Date getCreatedAt() {
        return super.getCreatedAt();
    }

    public String getRelativeTimeAgo() {
        long dateMillis = getCreatedAt().getTime();
        return DateUtils.getRelativeTimeSpanString(dateMillis, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
    }





    public ParseUser getUser(){
        return getParseUser(KEY_CREATOR);
    }

    public void setUser(ParseUser parseUser){
        put(KEY_CREATOR, parseUser);

    }



    public static class Query extends ParseQuery<Post> {
        public Query() {
            super(Post.class);
        }

        public Query getTop(){
            orderByDescending("createdAt");
            setLimit(50);
            return this;
        }

        public Query withUser (){
            include("creator_user");
            return this;
        }
        public Query getPrivate(){
            orderByDescending("createdAt");
            setLimit(50);
            whereContains("creator_user", ParseUser.getCurrentUser().getObjectId());
            return this;
        }
    }

}
