package com.inkredibles.wema20;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.inkredibles.wema20.models.Post;
import com.inkredibles.wema20.models.Rak;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
    The archive fragment allows viewers to look back on all of their posts about kindness. Using a recycler view
    and reusing the posts adapter, the posts query to populate the recycler view filters show's all of the posts
    the user has made including the one's they have marked as private that won't appear in their main feed.
 */

public class ArchiveFragment extends Fragment{
    private static final String  RAK_TAB = "rak";
    private static final String  REFLECTIONS_TAB = "reflections";
    private static final String  SCHEDULED_TAB = "scheduled";



    private ArrayList<Post> archivedPosts;
    private ArrayList<Rak> currentRaks;
    private RecyclerView rvArchivePosts;
    private PostsAdapter archiveAdapter;
    private RakAdapter raksAdapter;
    private TabLayout tabLayout;
    private Date currentDate;

    //MainActivity mainActivity = new MainActivity();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_archive, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        currentDate = new Date();
        archivedPosts = new ArrayList<>();
        currentRaks = new ArrayList<>();

        //TODO DECIDE WHICH TAB TO OPEN TO RIGHT NOW I THINK THE TAB THAT SHOULD BE SELECTED IS RAK
        Singleton.getInstance().setAdapterMode(RAK_TAB);

        rvArchivePosts = (RecyclerView) view.findViewById(R.id.ArchiveRecyclerView);
        tabLayout = view.findViewById(R.id.tbLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String nameOfSelectedTab = tab.getText().toString();
                switch (nameOfSelectedTab){
                    case "RAKs":
                        //change the adapter mode in the singleton
                        Singleton.getInstance().setAdapterMode(RAK_TAB);
                        loadRaks();
                    break;
                    case "Reflections":
                        Singleton.getInstance().setAdapterMode(REFLECTIONS_TAB);
                        loadArchives();
                    break;
                    case "Scheduled":
                        Singleton.getInstance().setAdapterMode(SCHEDULED_TAB);
                        loadRaks();
                        //loadScheduled();
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

        loadRaks(); //By default, we want to show the raks that the user has done
    }

    private  void loadRaks(){
        //get the user's private posts
        currentRaks.clear();
        raksAdapter = new RakAdapter(currentRaks);
        rvArchivePosts.setAdapter(raksAdapter);
        final Rak.Query raksQuery = new Rak.Query();
        raksQuery.getTop().withUser();

        raksQuery.findInBackground(new FindCallback<Rak>() {
            @Override
            public void done(List<Rak> objects, ParseException e) {
                if (e == null && Singleton.getInstance().getAdapterMode().equals(RAK_TAB)){
                    raksAdapter.addAll(objects);
                    //create a posts adapter
                //raksAdapter = new RakAdapter(currentRaks);
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                rvArchivePosts.setLayoutManager(mLayoutManager);
                    Log.i("current group fragment", "successful finding group raks");
                }else if (e == null && Singleton.getInstance().getAdapterMode().equals(SCHEDULED_TAB)){
                    for(int i = 0; i < objects.size(); i++){
                        if (objects.get(i).getScheduleDate() != null){
                            Date scheduleDate = objects.get(i).getScheduleDate();
                            if(scheduleDate.compareTo(currentDate) > 0){
                                currentRaks.add(objects.get(i));
                            }
                        }
                    }
                    raksAdapter.notifyDataSetChanged();
                } else{
                    e.printStackTrace();
                }
            }
        });
//        raksQuery.findInBackground(new FindCallback<Rak>() {
//            @Override
//            public void done(List<Rak> objects, ParseException e) {
//                if (e == null){
//                    for (Rak rak : objects){
//                        currentRaks.add(rak);
//                        raksAdapter.notifyItemInserted(currentRaks.size()-1);
//                    }
//
//                }
//                //create a posts adapter
//                //raksAdapter = new RakAdapter(currentRaks);
//
//                LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
//                rvArchivePosts.setLayoutManager(mLayoutManager);
//            }
//        });
    }

    private void loadArchives(){
        //get the user's private posts
        final Post.Query postsQuery = new Post.Query();
        postsQuery.getPrivate().withUser();
        //create a posts adapter
        archiveAdapter = new PostsAdapter(archivedPosts);
        //attach the adapter to the recyclerview to populate items
        rvArchivePosts.setAdapter(archiveAdapter);
        postsQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null){
                    for (Post post: objects){
                        archivedPosts.add(post);
                        archiveAdapter.notifyItemInserted(archivedPosts.size()-1);
                    }

                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                    rvArchivePosts.setLayoutManager(mLayoutManager);
                }else{
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //mainActivity.setArchiveBool(false);

    }
}
