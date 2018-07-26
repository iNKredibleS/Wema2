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

public class CreateGroupFragment extends Fragment {

    private onItemSelectedListener listener;
    ArrayList<ParseUser> addedUsers;


    @BindView(R.id.etGroupName) EditText etGroupName;
    @BindView(R.id.etTestText) EditText etTestText;

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
            for(int i = 0; i < addedUsers.size(); i++){
                Log.i("create group fragment", "users[" + i + "]" + "=" + addedUsers.get(i).getUsername());
            }
        }
    }


    @OnClick(R.id.createGroupBtn)
    protected void createGroup(){

        String roleName = etGroupName.getText().toString();
        String roleTestText = etTestText.getText().toString();
        ParseACL roleAcl = new ParseACL();
        roleAcl.setPublicWriteAccess(false);
        roleAcl.setPublicReadAccess(false);

        final ParseRole newRole = new ParseRole(roleName, roleAcl);
        for(int i = 0; i < addedUsers.size(); i++){
            newRole.getUsers().add(addedUsers.get(i));
        }
        newRole.put("testString", roleTestText);

        newRole.saveInBackground(
        new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    listener.fromCreateGrouptoCurrentGroup(newRole);
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
