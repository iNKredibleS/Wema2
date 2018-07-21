package com.inkredibles.wema20;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.inkredibles.wema20.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

public class ArchiveFragment extends Fragment{

    private ArrayList<Post> archivedPosts;
    private RecyclerView rvArchivePosts;
    private PostsAdapter archiveAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_archive, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        rvArchivePosts = (RecyclerView) view.findViewById(R.id.ArchiveRecyclerView);
        //initialize posts
        archivedPosts = new ArrayList<>();
        loadArchives();

    }

    private void loadArchives(){
        //get the top posts
        final Post.Query postsQuery = new Post.Query();
        postsQuery.getPrivate().withUser();

        postsQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null){
                    for (int i = 0; i < objects.size(); i++){
                        archivedPosts.add(objects.get(i));
                        Log.d("Post", archivedPosts.get(i).getMessage());
                    }
                    //create a posts adapter
                    archiveAdapter = new PostsAdapter(archivedPosts);
                    //attach the adapter to the recyclerview to populate items
                    rvArchivePosts.setAdapter(archiveAdapter);

                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());

                    rvArchivePosts.setLayoutManager(mLayoutManager);
                }else{
                    e.printStackTrace();
                }
            }
        });
    }
}
