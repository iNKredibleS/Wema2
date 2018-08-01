package com.inkredibles.wema20;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseRole;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
/*
Create Group Fragment allows users to create a new group and then save that group to parse. The user
can input a group name and add parse users by searching through a list of current Wema users.

 */


public class CreateGroupFragment extends Fragment {

    private onItemSelectedListener listener;
    ArrayList<ParseUser> addedUsers;


    @BindView(R.id.etGroupName) EditText etGroupName;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_create_group, parent, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
    }

    @Override
    public void onResume() {
        super.onResume();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            addedUsers = bundle.getParcelableArrayList("added_users");
        }
    }


    @OnClick(R.id.createGroupBtn)
    protected void createGroup(){

        String roleName = etGroupName.getText().toString();
        //the line below is not necessary also figure out what the ACL does
        ParseACL roleAcl = new ParseACL();
        roleAcl.setPublicWriteAccess(false);
        roleAcl.setPublicReadAccess(true);

        //currentRole maybe should be called new Role... maybe it shouldn't be currentRole or newRole
        //this could all be resolved if it were just called roll
        final ParseRole currentRole = new ParseRole(roleName, roleAcl);
        for(int i = 0; i < addedUsers.size(); i++){
            currentRole.getUsers().add(addedUsers.get(i));
        }

        currentRole.saveInBackground(
        new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    listener.fromCreateGrouptoCurrentGroup(currentRole);
                    Log.d("CreateGroup", "create group success");

                } else {
                    e.printStackTrace();
                }
            }
        });


    }

    @OnClick(R.id.addUsersBtn)
    protected void addUsersLaunch(){
        listener.toAddUsers();
    }





    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof onItemSelectedListener) {
            listener = (onItemSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement OnItemSelectedListener");
        }
    }

}
