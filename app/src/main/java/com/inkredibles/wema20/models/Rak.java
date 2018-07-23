package com.inkredibles.wema20.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

/*
    Data schema for the RAK (random act of kindness of the day). The title of the RAK is the suggested daily random act of kindess
    for the user for the day. In terms of UI, the image we hope to underlay the RAK post.
 */

@ParseClassName("Rak")
public class Rak extends ParseObject{

    public Rak() {}

    private static final String KEY_TITLE = "title";
    private static final String KEY_IMAGE = "image";

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



}
