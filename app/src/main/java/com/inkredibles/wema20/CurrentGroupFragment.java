package com.inkredibles.wema20;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.inkredibles.wema20.models.Post;
import com.inkredibles.wema20.models.Rak;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseRole;

import java.util.ArrayList;
import java.util.List;
/*
purpose of this fragment is to display the details of the group and allow for user interaction with the group. Still to do
is display the group's posts and random acts of kindness. The user can create a new post for the group or create
a new group random act of kindness challenge. Both functions are implemented from buttons on this page and
transition to their respective create fragments.
 */

public class CurrentGroupFragment extends Fragment{




    //AdapterView.OnItemSelectedListener spinnerListener;

    private onItemSelectedListener listener;
    private ParseRole currentRole;
    private ArrayList<Rak> groupRaks;
    private RakAdapter rakAdapter;

    private ArrayList<Post> groupPosts;
    private PostsAdapter postsAdapter;
    private TabLayout groupTabLayout;

    private TextView tvGroupName;
    private RecyclerView rvGroupItem;
    //@BindView(R.id.group_spinner) Spinner spinner;
    private TextView tvEmptyMessage;
    private Button createGroupPostBtn;
    private Button createGroupRakBtn;


    //tells the recyclerview in the fragment whether to upload the group raks or posts
    private static boolean isItemRak;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_current_group, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

//        bundleAndSetUp();


        rvGroupItem = view.findViewById(R.id.rvGroupItem);
        tvEmptyMessage = view.findViewById(R.id.tvEmptyMessage);

        tvGroupName = view.findViewById(R.id.tvGroupName);
        currentRole = Singleton.getInstance().getRole();
        tvGroupName.setText(currentRole.getName());
        loadRvGroupItem(true);

        groupTabLayout = view.findViewById(R.id.groupTabLayout);
        groupTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String nameOfSelectedTab = tab.getText().toString();
                switch (nameOfSelectedTab){
                    case "RAKs":
                        //change the adapter mode in the singleton
                        loadRvGroupItem(true);
                        break;
                    case "Posts":
                        loadRvGroupItem(false);
                        break;
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        createGroupPostBtn = view.findViewById(R.id.btnCreateGroupPost);
        createGroupPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.fromCurrentGrouptoCreatePost(currentRole);
            }
        });

        createGroupRakBtn = view.findViewById(R.id.btnCreateGroupRak);
        createGroupRakBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.fromCurrentGrouptoCreateRak(currentRole);
            }
        });




//        ButterKnife.bind(this, view);



        isItemRak = true;






    }

    @Override
    public void onResume() {
        super.onResume();

    }









    private void bundleAndSetUp(){



//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
//                R.array.groups_array, android.R.layout.simple_spinner_item);
//        // Specify the layout to use when the list of choices appears
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        // Apply the adapter to the spinner
//        spinner.setAdapter(adapter);
//
//        spinner.setOnItemSelectedListener(this);



    }


    private void loadRvGroupItem(Boolean isItemRak){
        //set the data, adapter, and recycler view with raks
        if(isItemRak){
            groupRaks = new ArrayList<>();
            //define adapter
            rakAdapter = new RakAdapter(groupRaks);
            //set layout manager
            rvGroupItem.setLayoutManager(new LinearLayoutManager(getContext()));
            //set adapter
            rvGroupItem.setAdapter(rakAdapter);

            //loadGroupRaks through a query
            loadGroupRaks();



        }
        //set the data, adapter, and recycler view with posts
        else{
            Singleton.getInstance().setAdapterMode("feed");
            groupPosts = new ArrayList<>();
            postsAdapter = new PostsAdapter(groupPosts);
            rvGroupItem.setLayoutManager(new LinearLayoutManager(getContext()));
            rvGroupItem.setAdapter(postsAdapter);
            loadGroupPosts();

            //implement on click listener to view details of group post
            postsAdapter.setViewHolderListener(new PostsAdapter.ViewHolderListener() {
                @Override
                public void onViewHolderClicked(Post post, ParseImageView parseImageView, String transitionName, int position, ArrayList<Post> posts, TextView title, String titleTransition) {
                    //now move this post from the feed fragment to the main activity
                    if (listener != null) {
                        listener.fromFeedtoDetail(post, parseImageView, transitionName,position, posts, title,  titleTransition);
                    }
                }
            });



        }
    }

    private void loadGroupRaks(){
        ParseQuery<Rak> query = ParseQuery.getQuery(Rak.class);
        query.whereEqualTo("role", currentRole);
        query.orderByDescending("createdAt");

        query.findInBackground(new FindCallback<Rak>() {
            @Override
            public void done(List<Rak> objects, ParseException e) {
                if (e == null){
                    if(objects.size() == 0) {
                        tvEmptyMessage.setText(R.string.empty_message_rak);
                    } else {
                        tvEmptyMessage.setText("");
                    }


                    rakAdapter.addAll(objects);
                    Log.i("current group fragment", "successful finding group raks");
                }else{
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadGroupPosts(){
        Post.Query postsQuery = new Post.Query();
        postsQuery.getGroupPosts(currentRole);

        postsQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null){
                    if(objects.size() == 0) {
                        tvEmptyMessage.setText(R.string.empty_message_posts);
                    } else {
                        tvEmptyMessage.setText("");
                    }

                    postsAdapter.addAll(objects);
                }else{
                    e.printStackTrace();
                }
            }
        });
    }



//    @OnClick(R.id.btnCreateGroupPost)
//    protected void createGroupPost() {
//        listener.fromCurrentGrouptoCreatePost(currentRole);
//
//
//    }
//
//    @OnClick(R.id.btnCreateGroupRak)
//    protected void createGroupRak(){
//        listener.fromCurrentGrouptoCreateRak(currentRole);
//
//    }

    //reset the fragment's arguments
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(this.getArguments() != null){this.getArguments().clear();
        }
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


//    @Override
//    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
//        if (view != null) {
//            if (parent.getItemAtPosition(pos).equals("Rak")) {
//                loadRvGroupItem(true);
//            } else if (parent.getItemAtPosition(pos).equals("Post")) {
//                loadRvGroupItem(false);
//
//            }
//        } else{
//            loadRvGroupItem(true);
//        }
//
//    }
//
//    @Override
//    public void onNothingSelected(AdapterView<?> adapterView) {
//
//    }
}
