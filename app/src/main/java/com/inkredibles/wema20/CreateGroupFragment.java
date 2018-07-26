package com.inkredibles.wema20;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.parse.ParseACL;
import com.parse.ParseRole;
import com.parse.ParseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateGroupFragment extends Fragment {


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

    @OnClick(R.id.createGroupBtn)
    protected void createGroup(){
        String roleName = etGroupName.getText().toString();
        ParseACL roleAcl = new ParseACL();
        roleAcl.setPublicWriteAccess(false);
        roleAcl.setPublicReadAccess(false);

        ParseRole newRole = new ParseRole(roleName, roleAcl);
        newRole.getUsers().add(ParseUser.getCurrentUser());
        newRole.saveInBackground();
    }


}
