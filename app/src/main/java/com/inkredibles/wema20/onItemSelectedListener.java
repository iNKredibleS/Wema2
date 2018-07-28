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

    void toCreateRak();

    void toAddUsers();


    void addRakToServer(String rakTitle);

    void fromAddUserstoCreateGroup (List<ParseUser> addedUsers);

    void fromCreateGrouptoCurrentGroup (ParseRole newRole);

    void fromCurrentGrouptoCreatePost (ParseRole currentRole);

    void fromCurrentGrouptoCreateRak(ParseRole currentRole);

    void toCurrentGroup(ParseRole newRole);

    void fromGroupAdaptertoCurrentGroup(ParseRole newRole);

    void fromGroupstoCreateGroup();



//    void setIsGroup (Boolean bool);
//
//    void setIsReflection (Boolean bool);
//
//    void setIsRak(Boolean bool);



}
