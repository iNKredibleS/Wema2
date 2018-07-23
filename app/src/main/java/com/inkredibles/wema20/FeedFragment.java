package com.inkredibles.wema20;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.inkredibles.wema20.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

public class FeedFragment extends Fragment {

    private static final  int SPAN_COUNT = 3;


    private ArrayList<Post> posts;
    private RecyclerView rvPosts;
    private PostsAdapter adapter;
    private SwipeRefreshLayout swipeContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_feed, parent, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        View mainView = getView();
        //get the recyclerview
        rvPosts = (RecyclerView) mainView.findViewById(R.id.rvPosts);
        //initialize posts
        posts = new ArrayList<>();
        //get the swipe container
        swipeContainer = (SwipeRefreshLayout) mainView.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (adapter != null){ //just in case the adapter has not yet been initialized.
                    adapter.clear();
                    final Post.Query postsQuery = new Post.Query();
                    postsQuery.getTop().withUser();

                    postsQuery.findInBackground(new FindCallback<Post>() {
                        @Override
                        public void done(List<Post> objects, ParseException e) {
                            if (e == null){
                                adapter.addAll(objects);
                                swipeContainer.setRefreshing(false);
                            }else{
                                e.printStackTrace();
                            }
                        }
                    });


                }
            }
        });
        loadPosts();
    }

    private void loadPosts(){
        //get the top posts
        final Post.Query postsQuery = new Post.Query();
        postsQuery.getTop().withUser();

        postsQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null){
                    for (int i = 0; i < objects.size(); i++){
                        posts.add(objects.get(i));
                        Log.d("Post", posts.get(i).getMessage());
                    }
                    //create a posts adapter
                    adapter = new PostsAdapter(posts);
                    //attach the adapter to the recyclerview to populate items
                    rvPosts.setAdapter(adapter);
                    //set the layout manager to position the items
                    StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL);
                    rvPosts.setLayoutManager(mLayoutManager);
                }else{
                    e.printStackTrace();
                }
            }
        });
    }
}
