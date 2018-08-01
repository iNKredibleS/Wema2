package com.inkredibles.wema20;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.inkredibles.wema20.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;

import java.util.ArrayList;
import java.util.List;

/*The feedfragment is responsible for the feed page which shows the posts.
* Setting the adapter occurs in the findInbackground method since by then, we have gotten a list of posts*/
public class FeedFragment extends Fragment {

    private static final  int SPAN_COUNT = 2;


    private ArrayList<Post> posts;
    private RecyclerView rvPosts;
    private PostsAdapter adapter;
    private SwipeRefreshLayout swipeContainer;
    private onItemSelectedListener itemSelectedListener;
    private EndlessRecyclerViewScrollListener scrollListener;// for infinite scrolling

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
        //create a posts adapter
        adapter = new PostsAdapter(posts);
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
        //this is called by the viewholder
        adapter.setViewHolderListener(new PostsAdapter.ViewHolderListener() {
            @Override
            public void onViewHolderClicked(Post post, ParseImageView parseImageView, String transitionName) {
               //now move this post from the feed fragment to the main activity
                if (itemSelectedListener != null) itemSelectedListener.fromFeedtoDetail(post, parseImageView, transitionName);
            }
        });
    }

    //This method loads posts from the server, attaches the adapter to the recyclerview and sets the layout manager.
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
                    }
                    //attach the adapter to the recyclerview to populate items
                    rvPosts.setAdapter(adapter);
                    //set the layout manager to position the items
                    StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL);
                    rvPosts.setLayoutManager(mLayoutManager);
                    scrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
                        @Override
                        public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {

                            loadNextDataFromApi(totalItemsCount);
                        }
                    };
                    // Adds the scroll listener to RecyclerView
                    rvPosts.addOnScrollListener(scrollListener);

                }else{
                    e.printStackTrace();
                }
            }
        });
    }

    //method that loads more data from the parse server and nofitfies the adapter.
    public void loadNextDataFromApi(int offset) {
        // Send an API request to retrieve appropriate paginated data
        final Post.Query postsQuery = new Post.Query();
        postsQuery.getMore(offset);
        postsQuery.withUser();
        postsQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null){
                    for (int i = 0; i < objects.size(); i++){
                        posts.add(objects.get(i));
                        adapter.notifyItemInserted(objects.size()-1);
                    }
                }
            }
        });
    }

    //initializes the onItemSelectedListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof onItemSelectedListener) {
            itemSelectedListener = (onItemSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement OnItemSelectedListener");
        }
    }

}
