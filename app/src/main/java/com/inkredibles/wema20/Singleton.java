package com.inkredibles.wema20;

import com.parse.ParseRole;

class Singleton {
    private static final Singleton ourInstance = new Singleton();
    private ParseRole role;
    private String mode;


    static Singleton getInstance() {
        return ourInstance;
    }

    private Singleton() {
    }

    //returns the current role
    public ParseRole getRole() {
        return role;
    }

    //sets the current role
    public void setRole(ParseRole role) {
        this.role = role;
    }

    //sets the adapter mode
    public void setAdapterMode(String mode){
        this.mode = mode;
    }
    //returns the adapter mode
    public String getAdapterMode(){
        return  mode;
    }
}
