package com.inkredibles.wema20;


import com.inkredibles.wema20.models.Post;
import com.inkredibles.wema20.models.Rak;
import com.parse.ParseImageView;
import com.parse.ParseRole;
import com.parse.ParseUser;

import java.util.List;

public interface onItemSelectedListener {

    public void fromFeedtoDetail(Post post, ParseImageView parseImageView);

    //Transitions from any fragment to the feed fragment
    void toFeed();

    //Transitions from any fragment to the CreatePost Fragment
    void fromRAKtoCreatePost(Rak rak);

    void toAddUsers();

    void fromAddUserstoCreateGroup (List<ParseUser> addedUsers);

    void fromCreateGrouptoCurrentGroup (ParseRole newRole);


}
