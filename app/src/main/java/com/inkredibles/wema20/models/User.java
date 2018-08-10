package com.inkredibles.wema20.models;

import com.parse.ParseClassName;
import com.parse.ParseUser;

@ParseClassName("_User")
public class User extends ParseUser {
    private static final String KEY_RAK = "current_rak";
    private static final String KEY_NUM_CLAPS = "num_claps";
    private static final String KEY_NUM_GROUPS = "num_groups";

    public User() {
        super();
    }

    public void setRak(Rak rak) {
        put(KEY_RAK, rak);
    }

    public Rak getRak() {
        return (Rak) getParseObject("current_rak");
    }

    public User getUser() {
        return (User) ParseUser.getCurrentUser();
    }
    public void  addClap(){
        int numClaps = getInt(KEY_NUM_CLAPS);
        put(KEY_NUM_CLAPS, numClaps+1);
    }
    public int getNumClaps(){
        return getInt(KEY_NUM_CLAPS);
    }
    public void  addGroup(){
        int numGroups = getInt(KEY_NUM_GROUPS);
        put(KEY_NUM_CLAPS, numGroups+1);
    }
    public int getNumGroups(){
        return getInt(KEY_NUM_GROUPS);
    }
}
