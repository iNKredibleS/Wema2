package com.inkredibles.wema20;


import com.inkredibles.wema20.models.Post;
import com.parse.ParseImageView;
import com.inkredibles.wema20.models.Rak;

public interface onItemSelectedListener {

    public void fromFeedtoDetail(Post post, ParseImageView parseImageView);

    //Transitions from any fragment to the feed fragment
    void toFeed();

    //Transitions from any fragment to the CreatePost Fragment
    void fromRAKtoCreatePost(Rak rak);

    void toCreateRak();

    void toAddUsers();

    void addRakToServer(String rakTitle);



}
