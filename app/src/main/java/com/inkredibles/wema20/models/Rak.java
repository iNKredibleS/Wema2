package com.inkredibles.wema20.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
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


    public String getTitle() {
        return getString(KEY_TITLE);
    }

    public void setTitle(String title) {
        put(KEY_TITLE, title);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile image) {
        put(KEY_IMAGE, image);
    }

    public ParseUser getUser(){
        return getParseUser(KEY_CREATOR);
    }

    public void setUser(ParseUser parseUser){
        put(KEY_CREATOR, parseUser);

    }

    public void setRole (ParseRole parseRole) {
        put(KEY_ROLE, parseRole);
    }



}
