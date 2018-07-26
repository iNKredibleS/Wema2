package com.inkredibles.wema20.models;


import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("User")
public class User extends ParseObject{

    private static final String KEY_RAK = "current_rak";

    public User() {}

    public void setRak(Rak rak) {
        put(KEY_RAK, rak);
    }

    public Rak getRak() {
        return (Rak) getParseObject("current_rak");
    }


}
