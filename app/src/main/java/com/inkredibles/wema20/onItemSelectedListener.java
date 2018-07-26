package com.inkredibles.wema20;

import com.inkredibles.wema20.models.Rak;

public interface onItemSelectedListener {

    //Transitions from any fragment to the feed fragment
    void toFeed();

    //Transitions from any fragment to the CreatePost Fragment
    void fromRAKtoCreatePost(Rak rak);

    void toCreatePost();

    void toCreateRak();

    void addRakToServer(String rakTitle);


}
