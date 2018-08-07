package com.inkredibles.wema20.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRole;
import com.parse.ParseUser;

/*
    Data schema for the RAK (random act of kindness of the day). The title of the RAK is the suggested daily random act of kindess
    for the user for the day. In terms of UI, the image we hope to underlay the RAK post.
 */


@ParseClassName("Rak")

public class Rak extends ParseObject {

    public Rak() {}

    private static final String KEY_TITLE = "title";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_CREATOR = "current_user";
    private static final String KEY_ROLE = "role";
    private static final String KEY_CURRENT_BACKGROUND = "current_background";
    private static  final String KEY_CREATED_AT = "createdAt";



    public String getTitle() {
        return getString(KEY_TITLE);
    }

    public void setTitle(String title) {
        put(KEY_TITLE, title);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public ParseUser getUser(){
        return getParseUser(KEY_CREATOR);
    }

    public void setUser(ParseUser parseUser){
        put(KEY_CREATOR, parseUser);

    }

    public void setImage(ParseFile file) {
        put(KEY_IMAGE, file);
    }


    public void setRole (ParseRole parseRole) {
        put(KEY_ROLE, parseRole);
    }

    public int getBackground() {
        return (int) getNumber(KEY_CURRENT_BACKGROUND);
    }

    public void setBackground(Number num) {
        put(KEY_CURRENT_BACKGROUND, num);
    }

    public static class Query extends ParseQuery<Rak> {
        public Query() {
            super(Rak.class);
        }

        public Rak.Query getTop() {

            orderByDescending(KEY_CREATED_AT);
            setLimit(20);
            return this;
        }

        public Rak.Query withUser() {
            include(KEY_CREATOR);
            return this;
        }
    }


}
