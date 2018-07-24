package com.inkredibles.wema20;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.inkredibles.wema20.models.Post;
import com.inkredibles.wema20.models.Rak;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.parse.ParseImageView;

public class MainActivity extends AppCompatActivity implements onItemSelectedListener {

    private Toolbar toolbar;
    final Fragment createPostFragment = new CreatePostFragment();
    final Fragment archiveFragment = new ArchiveFragment();
    final Fragment feedFragment = new FeedFragment();
    final Fragment rakFragment = new RakFragment();
    final Fragment createGroupFragment = new CreateGroupFragment();
    private Drawer result;
    private SecondaryDrawerItem feed;



    public static boolean archiveBool = false;

   // private onItemSelectedListener itemSelectedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        new DrawerBuilder().withActivity(this).build();

        //if you want to update the items at a later time it is recommended to keep it in a variable
        final PrimaryDrawerItem home = new PrimaryDrawerItem().withIdentifier(1).withName("Home");
        final SecondaryDrawerItem reflection = new SecondaryDrawerItem().withIdentifier(2).withName("Reflection");
        final SecondaryDrawerItem archive = new SecondaryDrawerItem().withIdentifier(3).withName("Archive");
        feed = new SecondaryDrawerItem().withIdentifier(4).withName("Feed");
        final SecondaryDrawerItem rak = new SecondaryDrawerItem().withIdentifier(5).withName("RAK");
        final SecondaryDrawerItem group = new SecondaryDrawerItem().withIdentifier(6).withName("Groups");


    // create the drawer and remember the `Drawer` result object
    result =
        new DrawerBuilder()
            .withActivity(this)
            .withToolbar(toolbar)
            .addDrawerItems(
                home,
                new DividerDrawerItem(),
                feed,
                rak,
                reflection,
                archive,
                group,
                new SecondaryDrawerItem().withName("othername"))
            .withOnDrawerItemClickListener(
                new Drawer.OnDrawerItemClickListener() {
                  @Override
                  public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                    if (drawerItem == reflection) {
                      nextFragment(createPostFragment);
                    }else if (drawerItem == archive){
                        archiveBool = true;
                        nextFragment(archiveFragment);

                    }else if (drawerItem == rak){
                        nextFragment(rakFragment);
                    }else if (drawerItem == feed){

                        archiveBool = false;
                        nextFragment(feedFragment);
                    }else if (drawerItem == group){
                        nextFragment(createGroupFragment);

                    }

                    return true;
                  }
                })
            .build();
    nextFragment(feedFragment);
    }

    private void nextFragment(Fragment fragment){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.placeholder, fragment);
        ft.addToBackStack("added to stack");
        ft.commit();

    }


    public boolean getArchiveBool(){return archiveBool;}

    @Override
    public void fromFeedtoDetail(Post post, ParseImageView parseImageView) {
        Fragment detailFragment = new DetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("post", post);
        detailFragment.setArguments(bundle);
        Log.d("Main Activity", "feed");
        ViewCompat.setTransitionName(parseImageView, "postPicTransition");

        getSupportFragmentManager()
                .beginTransaction()
                .addSharedElement(parseImageView, "postPicTransition")
                .replace(R.id.placeholder, detailFragment)
                .addToBackStack("Added to stack")
                .commit();

        nextFragment(detailFragment);
    }


    @Override
    //after new post created go back to feed fragment
    public void toFeed() {
        //Need to begin a new fragment transaction for any fragment operation
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.placeholder, feedFragment).commit();
        result.setSelection(feed);

    }

    @Override
    //after new post created go back to feed fragment
    public void fromRAKtoCreatePost(Rak rak) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("RAK", rak );
        //bundle.putString("rak_title", rak.getTitle());
        createPostFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.placeholder, createPostFragment).commit();

    }

  @Override
  public void toCreatePost() {

      FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
      fragmentTransaction.replace(R.id.placeholder, createPostFragment).commit();
  }

}
