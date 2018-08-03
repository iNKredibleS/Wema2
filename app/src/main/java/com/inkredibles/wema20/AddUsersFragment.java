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

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
/*
Add users fragment allows the user to choose which users he or she would like to be in their group.
The user can choose from a list of all users and when a user is chosen their view in the recyclerview
is highlighted in background.
 */

public class AddUsersFragment extends Fragment {

    @BindView(R.id.rvUsers) RecyclerView rvUsers;
    @BindView(R.id.etSearch) EditText etSearch;



    //private RecyclerView rvUsers;
    private ArrayList<ParseUser> allUsers;
    private List<ParseUser> addedUsers;
    private UsersAdapter adapter;
    private onItemSelectedListener listener;
    private ParseUser currentUser;
    private String currentUsername;

    private LinearLayoutManager linearLayoutManager;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_add_users, parent, false);


    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        //View mainView = getView();
        //get the recyclerview
        //Button usersAddedBtn = (Button) mainView.findViewById(R.id.doneAddedUsersBtn);
        // /rvUsers = (RecyclerView) mainView.findViewById(R.id.rvUsers);
        ButterKnife.bind(this, view);

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

    @OnClick(R.id.doneAddedUsersBtn)
    protected void doneAddingUsers(){
        addedUsers = adapter.getAddedUsers();
        listener.fromAddUserstoCreateGroup(addedUsers);
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
