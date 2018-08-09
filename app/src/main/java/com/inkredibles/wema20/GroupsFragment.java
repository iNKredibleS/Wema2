package com.inkredibles.wema20;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRole;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
    The purpose of this fragment is to show all of the user's groups that they belong to. When
    a user clicks on one of the groups the user belongs to, this will direct them to the current group
    fragment. That fragment can be thought of as a groupDetails page (consider renaming). This fragment
    also acts as an entry point for the user to create a new group.
 */

public class GroupsFragment extends Fragment {
    @BindView(R.id.rvGroups)
    RecyclerView rvGroups;

    private ArrayList<ParseRole> usersGroups;
    private GroupAdapter groupAdapter;
    private onItemSelectedListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_groups, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        usersGroups = new ArrayList<>();
        groupAdapter = new GroupAdapter(usersGroups);
        rvGroups.setLayoutManager(new LinearLayoutManager(getContext()));
        rvGroups.setAdapter(groupAdapter);

        loadGroups();
    }

    //searches for groups the user belongs to and adds them to adapter
    protected void loadGroups() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseQuery<ParseRole> query = ParseRole.getQuery();
        query.whereEqualTo("users", currentUser);
        query.findInBackground(new FindCallback<ParseRole>() {
            public void done(List<ParseRole> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        usersGroups.add(objects.get(i));
                        groupAdapter.notifyItemInserted(usersGroups.size() - 1);
                    }
                    groupAdapter.notifyDataSetChanged();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }


    //Create a new group
    @OnClick(R.id.CreateNewGroupBtn)
    public void launchCreateGroupFrag() {
        listener.fromGroupstoCreateGroup();
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
