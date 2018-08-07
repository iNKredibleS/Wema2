package com.inkredibles.wema20;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.firestore.FirebaseFirestore;
import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRole;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
/*
Create Group Fragment allows users to create a new group and then save that group to parse. The user
can input a group name and add parse users by searching through a list of current Wema users.

 */


public class CreateGroupFragment extends Fragment {



    //private RecyclerView rvUsers;
    private ArrayList<ParseUser> allUsers;
    private List<ParseUser> addedUsers;
    private UsersAdapter adapter;
    private onItemSelectedListener listener;
    private ParseUser currentUser;
    private String currentUsername;

    private LinearLayoutManager linearLayoutManager;

    @BindView(R.id.etGroupName) EditText etGroupName;
    @BindView(R.id.rvUsers)
    RecyclerView rvUsers;
    @BindView(R.id.etSearch) EditText etSearch;

    private FirebaseFirestore mFirestore;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_create_group, parent, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        //set up recycler view of users
        currentUser = ParseUser.getCurrentUser();
        currentUsername = currentUser.getUsername();
        allUsers = new ArrayList<>();
        addedUsers = new ArrayList<>();
        addedUsers.add(currentUser);
        adapter = new UsersAdapter(allUsers, addedUsers);
        linearLayoutManager = new LinearLayoutManager(getContext());
        rvUsers.setLayoutManager(linearLayoutManager);
        rvUsers.setAdapter(adapter);

        loadUsers();
    }

    @Override
    public void onResume() {
        super.onResume();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            addedUsers = bundle.getParcelableArrayList("added_users");
        }
    }



    @OnTextChanged(value = R.id.etSearch, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void textChanged(Editable editable) {
        String userQuery = editable.toString();

        //adapterUpdate();
        adapter.clear();
        allUsers.clear();

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereContains("username", userQuery);
        query.whereNotEqualTo("username", currentUsername);
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        Log.i("add users fragmetn", "users =" + objects.get(i).getUsername());
                        allUsers.add(objects.get(i));
                        adapter.notifyItemInserted(allUsers.size() - 1);

                    }
                    adapter.notifyDataSetChanged();


                } else {
                    e.printStackTrace();

                }

            }
        });
    }



    private void loadUsers(){
        //get all users and load them into arrayList allUsers
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("username", currentUsername);
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        allUsers.add(objects.get(i));
                        adapter.notifyItemInserted(allUsers.size() - 1);

                    }
                    //adapter.notifyDataSetChanged();


                } else {
                    e.printStackTrace();

                }

            }
        });
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
                    Singleton.getInstance().setRole(currentRole);
                    etGroupName.setText("");

                } else {
                    e.printStackTrace();
                }
            }
        });


    }

//    @OnClick(R.id.addUsersBtn)
//    protected void addUsersLaunch(){
//        listener.toAddUsers();
//    }





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
