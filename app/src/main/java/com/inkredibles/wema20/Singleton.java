package com.inkredibles.wema20;

import com.parse.ParseRole;

class Singleton {
    private static final Singleton ourInstance = new Singleton();
    private ParseRole role;


    static Singleton getInstance() {
        return ourInstance;
    }

    private Singleton() {
    }

    public ParseRole getRole() {
        return role;
    }

    public void setRole(ParseRole role) {
        this.role = role;
    }
}
