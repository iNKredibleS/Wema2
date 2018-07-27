package com.inkredibles.wema20.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("_User")
public class User extends ParseUser {
    private static final String KEY_RAK = "current_rak";
    private Rak rak;

    public User() {
        super();
    }


    public void setRak(Rak rak) {
        put(KEY_RAK, rak);
        this.rak = rak;
    }

    public Rak getRak() {
        return (Rak) getParseObject("current_rak");
    }

    public String getTitle() {
        return this.rak.getTitle();
    }
}
