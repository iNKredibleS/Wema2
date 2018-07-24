package com.inkredibles.wema20;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class AddUsersFragment extends Fragment {

    private RecyclerView rvUsers;
    private ArrayList<ParseUser> allUsers;
    private UsersAdapter adapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_add_users, parent, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        View mainView = getView();
        //get the recyclerview
        rvUsers = (RecyclerView) mainView.findViewById(R.id.rvUsers);
        allUsers = new ArrayList<>();
        adapter = new UsersAdapter(allUsers);
        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        rvUsers.setAdapter(adapter);

        loadUsers();


    }


    private void loadUsers(){
        //get all users and load them into arrayList allUsers
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        Log.d(
                                "HomeFragment",
                                "Post["
                                        + i
                                        + "] = "
                                        + objects.get(i).getUsername());

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


}
