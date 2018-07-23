package com.inkredibles.wema20;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

public class MainActivity extends AppCompatActivity implements  onItemSelectedListener {

    private Toolbar toolbar;
    final Fragment createPostFragment = new CreatePostFragment();
    final Fragment archiveFragment = new ArchiveFragment();
    final Fragment feedFragment = new FeedFragment();
    final Fragment rakFragment = new RakFragment();


    public static boolean archiveBool = false;


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
        final SecondaryDrawerItem feed = new SecondaryDrawerItem().withIdentifier(4).withName("Feed");
        final SecondaryDrawerItem rak = new SecondaryDrawerItem().withIdentifier(5).withName("RAK");


    // create the drawer and remember the `Drawer` result object
    Drawer result =
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
                    }
                    return true;
                  }
                })
            .build();
    }

    private void nextFragment(Fragment fragment){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.placeholder, fragment);
        ft.commit();

    }

    public boolean getArchiveBool(){return archiveBool;}
}
