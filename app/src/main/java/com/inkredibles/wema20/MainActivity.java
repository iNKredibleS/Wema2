package com.inkredibles.wema20;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.inkredibles.wema20.models.Post;
import com.inkredibles.wema20.models.Rak;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
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
        ParseUser user = ParseUser.getCurrentUser();
        // Create the AccountHeader for the nav drawer
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header_bk)
                .addProfiles(
                        new ProfileDrawerItem().withName(user.getUsername()).withEmail(user.getEmail()).withIcon(getResources().getDrawable(R.drawable.profile_pic))
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();
        //sets up the navigation drawer
        setupNavDrawer(headerResult);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        //set the adapter mode to feed
        Singleton.getInstance().setAdapterMode(getResources().getString(R.string.feed_mode));

        String rakNotification = getIntent().getStringExtra("rakFragment");
        String groupNotification = getIntent().getStringExtra("groupFragment");

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (rakNotification != null) {
            if (rakNotification.equals("rakFragment")) nextFragment(rakFragment);
            return;
        } else if (groupNotification != null) {
            if (groupNotification.equals(RakFragment.ROLE_IDENTIFIER)) {
                ParseQuery<ParseRole> query = ParseQuery.getQuery(ParseRole.class);
                query.getInBackground(RakFragment.ROLE_IDENTIFIER, new GetCallback<ParseRole>() {
                    public void done(ParseRole object, ParseException e) {
                        if (e == null) {
                            toCurrentGroup(object);

                        } else {
                            Log.d("Error", e.toString());
                        }
                    }
                });

            }
        }
        nextFragment(rakFragment);
    }

    //sets up the navigation drawer
    private void setupNavDrawer(AccountHeader headerResult) {
        //if you want to update the items at a later time it is recommended to keep it in a variable
        final SecondaryDrawerItem reflection = new SecondaryDrawerItem().withIdentifier(2).withName("Reflection");
        final SecondaryDrawerItem places = new SecondaryDrawerItem().withIdentifier(3).withName("Places");
        feed = new SecondaryDrawerItem().withIdentifier(4).withName("Feed");
        rak = new SecondaryDrawerItem().withIdentifier(5).withName("RAK");
        group = new SecondaryDrawerItem().withIdentifier(6).withName("Groups");
        final SecondaryDrawerItem archive = new SecondaryDrawerItem().withIdentifier(7).withName("Profile");
        final SecondaryDrawerItem logout = new SecondaryDrawerItem().withIdentifier(8).withName("Log Out");
        result =
                new DrawerBuilder()
                        .withActivity(this)
                        .withToolbar(toolbar)
                        .withAccountHeader(headerResult)
                        .withTranslucentStatusBar(false)
                        .addDrawerItems(
                                feed,
                                new DividerDrawerItem(),
                                rak,
                                new DividerDrawerItem(),
                                reflection,
                                new DividerDrawerItem(),
                                places,
                                new DividerDrawerItem(),
                                group,
                                new DividerDrawerItem(),
                                archive,
                                new DividerDrawerItem(),
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
                                        } else if (drawerItem == archive) {
                                            Singleton.getInstance().setAdapterMode(getResources().getString(R.string.rak_tab)); //set raks to be the default
                                            nextFragment(archiveFragment);

                                        } else if (drawerItem == rak) {
                                            nextFragment(rakFragment);
                                        } else if (drawerItem == feed) {
                                            Singleton.getInstance().setAdapterMode(getResources().getString(R.string.feed_mode));
                                            nextFragment(feedFragment);
                                        } else if (drawerItem == group) {
                                            //launch groups Fragment to see user's groups
                                            nextFragment(groupsFragment);
                                        } else if (drawerItem == places) {
                                            //nextFragment();
                                            nextFragment(placesFragment);
                                        } else if (drawerItem == logout) {
                                            ParseUser.logOut();
                                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                            startActivity(intent);
                                        }

                                        return true;
                                    }
                                })
                        .build();
        result.setSelection(rak); //sets rak to be the selected element
    }

    //This method launches the fragment that is passed to it.
    private void nextFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.placeholder, fragment);
        ft.addToBackStack(fragment.getClass().toString());
        ft.commit();
        closeDrawer();
    }

    //This method closes the drawer
    private void closeDrawer() {
        if (result.isDrawerOpen()) result.closeDrawer();
    }

    //This method transitions from the feed page to the detail page
    @Override
    public void fromFeedtoDetail(Post post, ParseImageView parseImageView, String sharedTransitionName, int position, ArrayList<Post> posts, TextView title, String titleTransition, CardView cardView, String cardTransition) {
        Context context = feedFragment.getContext();
        Fragment detailFragment = new DetailFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        ViewCompat.setTransitionName(parseImageView, sharedTransitionName);
        ViewCompat.setTransitionName(title, titleTransition);
        ViewCompat.setTransitionName(cardView, cardTransition);

        //adds the shared transitions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            feedFragment.setSharedElementReturnTransition(TransitionInflater.from(context).inflateTransition(R.transition.default_transition));
            feedFragment.setExitTransition(TransitionInflater.from(context).inflateTransition(android.R.transition.no_transition));

            detailFragment.setSharedElementEnterTransition(TransitionInflater.from(context).inflateTransition(R.transition.default_transition));
            detailFragment.setEnterTransition(TransitionInflater.from(context).inflateTransition(android.R.transition.no_transition));

            fragmentTransaction.addSharedElement(parseImageView, sharedTransitionName);
            fragmentTransaction.addSharedElement(title, titleTransition);
            fragmentTransaction.addSharedElement(cardView, cardTransition);
        }

        //bundle up the data to be sent to the details fragment
        Bundle bundle = new Bundle();
        bundle.putParcelable("post", post);
        bundle.putParcelableArrayList("all_posts", posts);
        bundle.putInt("position", position);
        bundle.putString("transitionName", sharedTransitionName);
        bundle.putString("titleTransition", titleTransition);
        bundle.putString("cardTransition", cardTransition);

        detailFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.placeholder, detailFragment)
                .addToBackStack(detailFragment.getClass().toString())
                .commit();
    }


    @Override
    //after new post created go back to feed fragment
    public void toFeed() {
        //Need to begin a new fragment transaction for any fragment operation
        Singleton.getInstance().setAdapterMode(getResources().getString(R.string.feed_mode));
        nextFragment(feedFragment);
    }

    @Override
    //after new post created go back to feed fragment
    public void fromRAKtoCreatePost(Rak rak) {

        Bundle bundle = new Bundle();
        isRak = true;
        bundle.putBoolean("isRak", isRak);
        bundle.putParcelable("RAK", rak);
        createPostFragment.setArguments(bundle);
        nextFragment(createPostFragment);
        isRak = false;
    }

    @Override
    public void toAddUsers() {
        nextFragment(addUsersFragment);
    }

    @Override
    public void toCreateRak() {
        nextFragment(createRakFragment);
    }

    @Override
    public void addRakToServer(String rakTitle, String dateString) {
        Bundle bundle = new Bundle();
        bundle.putString("new_rak_title", rakTitle);
        bundle.putString("date", dateString);

        rakFragment.setArguments(bundle);
        nextFragment(rakFragment);
    }

    @Override
    public void fromAddUserstoCreateGroup(List<ParseUser> addedUsers) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("added_users", new ArrayList<ParseUser>(addedUsers));
        createGroupFragment.setArguments(bundle);
        nextFragment(createGroupFragment);
    }


    @Override
    public void fromCurrentGrouptoCreatePost(ParseRole currentRole) {

        isGroup = true;
        Bundle bundle = new Bundle();
        bundle.putBoolean("isGroup", isGroup);
        bundle.putParcelable("currentRole", currentRole);
        createPostFragment.setArguments(bundle);
        nextFragment(createPostFragment);
        isGroup = false;
    }

    @Override
    public void fromCurrentGrouptoCreateRak(ParseRole currentRole) {

        isGroup = true;
        Bundle bundle = new Bundle();
        bundle.putBoolean("isGroup", isGroup);
        bundle.putParcelable("currentRole", currentRole);
        createRakFragment.setArguments(bundle);
        nextFragment(createRakFragment);
        isGroup = false;
    }

    @Override
    public void fromCreateGrouptoCurrentGroup(ParseRole currentRole) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("currentRole", currentRole);
        currentGroupFragment.setArguments(bundle);
        nextFragment(currentGroupFragment);
    }

    @Override
    public void toCurrentGroup(ParseRole currentRole) {
        Singleton.getInstance().setRole(currentRole);
        nextFragment(currentGroupFragment);
    }

    @Override
    public void fromGroupstoCreateGroup() {
        nextFragment(createGroupFragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //Handling Action Bar button click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //Back button
            case android.R.id.home:
                //If this activity started from other activity
                finish();
                return true;
            case R.id.composeRak:
                //compose RAK clicked
                nextFragment(createPostFragment);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
