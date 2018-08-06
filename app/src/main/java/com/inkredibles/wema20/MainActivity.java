package com.inkredibles.wema20;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.inkredibles.wema20.models.Post;
import com.inkredibles.wema20.models.Rak;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.parse.ParseImageView;
import com.parse.ParseRole;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/*The mainactivity handles navigation between fragments. It also here that the navigation drawer is instantiated and its options set.*/
public class MainActivity extends AppCompatActivity implements onItemSelectedListener {

    private Toolbar toolbar;
    final Fragment createPostFragment = new CreatePostFragment();
    final Fragment archiveFragment = new ArchiveFragment();
    final Fragment feedFragment = new FeedFragment();
    final Fragment rakFragment = new RakFragment();
    final Fragment createRakFragment = new CreateRakFragment();
    final Fragment createGroupFragment = new CreateGroupFragment();
    final Fragment placesFragment = new PlacesFragment();
    final Fragment addUsersFragment = new AddUsersFragment();
    final Fragment currentGroupFragment = new CurrentGroupFragment();
    final Fragment groupsFragment = new GroupsFragment();
    private Drawer result;
    private SecondaryDrawerItem feed;
    private SecondaryDrawerItem rak;
    private SecondaryDrawerItem group;

    FirebaseAuth mAuth;


    public static boolean archiveBool = false;
    public boolean isGroup = false;
    public boolean isReflection = false;
    public boolean isRak = false;

    @Override
    protected void onStart() {
        super.onStart();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        new DrawerBuilder().withActivity(this).build();

        mAuth = FirebaseAuth.getInstance();

        //if you want to update the items at a later time it is recommended to keep it in a variable
        final PrimaryDrawerItem home = new PrimaryDrawerItem().withIdentifier(1).withName("Home");
        final SecondaryDrawerItem reflection = new SecondaryDrawerItem().withIdentifier(2).withName("Reflection");
        final SecondaryDrawerItem archive = new SecondaryDrawerItem().withIdentifier(3).withName("Archive");
        feed = new SecondaryDrawerItem().withIdentifier(4).withName("Feed");
        rak = new SecondaryDrawerItem().withIdentifier(5).withName("RAK");
        group = new SecondaryDrawerItem().withIdentifier(6).withName("Groups");
        final SecondaryDrawerItem places = new SecondaryDrawerItem().withIdentifier(7).withName("Places");
        final SecondaryDrawerItem logout = new SecondaryDrawerItem().withIdentifier(7).withName("Log Out");


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
                places,
                logout)
            .withOnDrawerItemClickListener(
                new Drawer.OnDrawerItemClickListener() {
                  @Override
                  public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                    if (drawerItem == reflection) {
                        isReflection = true;
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("isReflection", isReflection);
                        createPostFragment.setArguments(bundle);
                        nextFragment(createPostFragment);
                        isReflection = false;

                    }else if (drawerItem == archive){
                        archiveBool = true;
                        nextFragment(archiveFragment);

                    }else if (drawerItem == rak){
                        nextFragment(rakFragment);
                    }else if (drawerItem == feed){

                        archiveBool = false;
                        nextFragment(feedFragment);
                    }else if (drawerItem == group){
                        //launch groups Fragment to see user's groups
                        nextFragment(groupsFragment);
                    }
                    else if (drawerItem == places){
                        //nextFragment();
                        nextFragment(placesFragment);
                    }else if (drawerItem == logout){
                        ParseUser.logOut();
                        mAuth.signOut();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
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
        ft.addToBackStack(fragment.getClass().toString());
        ft.commit();
        closeDrawer();
    }
    //This method closes the drawer
    private void closeDrawer(){
        if (result.isDrawerOpen()) result.closeDrawer();
    }


    public boolean getArchiveBool(){return archiveBool;}





    //TODO is there a way to make this code more concise?
    @Override
    public void fromFeedtoDetail(Post post, ParseImageView parseImageView, String sharedTransitionName, int position, ArrayList<Post>posts, TextView title, String titleTransition) {
        Context context = feedFragment.getContext();
        Fragment detailFragment = new DetailFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        ViewCompat.setTransitionName(parseImageView, sharedTransitionName);
        ViewCompat.setTransitionName(title, titleTransition);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            feedFragment.setSharedElementReturnTransition(TransitionInflater.from( context).inflateTransition(R.transition.default_transition));
            feedFragment.setExitTransition(TransitionInflater.from( context).inflateTransition(android.R.transition.no_transition));

            detailFragment.setSharedElementEnterTransition(TransitionInflater.from( context).inflateTransition(R.transition.default_transition));
            detailFragment.setEnterTransition(TransitionInflater.from( context).inflateTransition(android.R.transition.no_transition));

            fragmentTransaction.addSharedElement(parseImageView,sharedTransitionName);
            fragmentTransaction.addSharedElement(title,titleTransition);
        }
        //fragmentTransaction.replace(R.id.fragment_pla, newFragment, tag);
        //fragmentTransaction.addToBackStack(tag);
        Bundle bundle = new Bundle();
        bundle.putParcelable("post", post);
        bundle.putParcelableArrayList("all_posts", posts);
        bundle.putInt("position", position);
        bundle.putString("transitionName", sharedTransitionName);
        bundle.putString("titleTransition", titleTransition);
        detailFragment.setArguments(bundle);
//        Log.d("Main Activity", "feed");
        //ViewCompat.setTransitionName(parseImageView, "postPicTransition");
        fragmentTransaction.replace(R.id.placeholder, detailFragment)
                .addToBackStack(detailFragment.getClass().toString())
                .commit();
        //nextFragment(detailFragment);
    }


    @Override
    //after new post created go back to feed fragment
    public void toFeed() {
        //Need to begin a new fragment transaction for any fragment operation
        archiveBool = false;
        nextFragment(feedFragment);

    }

    @Override
    //after new post created go back to feed fragment
    public void fromRAKtoCreatePost(Rak rak) {

        Bundle bundle = new Bundle();
        isRak = true;
        bundle.putBoolean("isRak", isRak);
        bundle.putParcelable("RAK", rak );
        createPostFragment.setArguments(bundle);
        nextFragment(createPostFragment);
        isRak = false;
        //result.setSelection(rak);

    }

    @Override
    public void toAddUsers(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.placeholder, addUsersFragment).commit();

    }

  @Override
    public void toCreateRak() {
     nextFragment(createRakFragment);

  }

  @Override
    public void addRakToServer(String rakTitle, String date, String location) {
      Bundle bundle = new Bundle();
      bundle.putString("new_rak_title", rakTitle);
      bundle.putString("date", date);
      bundle.putString("location", location);
      rakFragment.setArguments(bundle);
      nextFragment(rakFragment);
  }

  @Override
  public void fromAddUserstoCreateGroup(List<ParseUser> addedUsers){
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("added_users", new ArrayList<ParseUser>(addedUsers));
        createGroupFragment.setArguments(bundle);
        nextFragment(createGroupFragment);
  }


  @Override
  public void fromCurrentGrouptoCreatePost(ParseRole currentRole){

        isGroup = true;
        Bundle bundle = new Bundle();
        bundle.putBoolean("isGroup", isGroup);
        bundle.putParcelable("currentRole", currentRole);
        createPostFragment.setArguments(bundle);
        nextFragment(createPostFragment);
        isGroup = false;
  }

  @Override
  public void fromCurrentGrouptoCreateRak(ParseRole currentRole){

        isGroup = true;
        Bundle bundle = new Bundle();
        bundle.putBoolean("isGroup", isGroup);
        bundle.putParcelable("currentRole", currentRole);
        createRakFragment.setArguments(bundle);
        nextFragment(createRakFragment);
        isGroup = false;
        //this code can be optimized
  }

  @Override
  public void fromCreateGrouptoCurrentGroup(ParseRole currentRole){
        Bundle bundle = new Bundle();
        bundle.putParcelable("currentRole", currentRole);
        currentGroupFragment.setArguments(bundle);
        nextFragment(currentGroupFragment);
    }

  @Override
    public void toCurrentGroup(ParseRole currentRole) {
//     Bundle bundle = new Bundle();
//     bundle.putParcelable("currentRole", currentRole);
//     currentGroupFragment.setArguments(bundle);
      Singleton.getInstance().setRole(currentRole);
      nextFragment(currentGroupFragment);


  }


  @Override
  public void fromGroupstoCreateGroup(){
        nextFragment(createGroupFragment);
  }






}
