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

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    final Fragment createPost = new CreatePostFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        new DrawerBuilder().withActivity(this).build();

        //if you want to update the items at a later time it is recommended to keep it in a variable
        final PrimaryDrawerItem home = new PrimaryDrawerItem().withIdentifier(1).withName("home");
        final SecondaryDrawerItem reflection = new SecondaryDrawerItem().withIdentifier(2).withName("Reflection");

    // create the drawer and remember the `Drawer` result object
    Drawer result =
        new DrawerBuilder()
            .withActivity(this)
            .withToolbar(toolbar)
            .addDrawerItems(
                home,
                new DividerDrawerItem(),
                reflection,
                new SecondaryDrawerItem().withName("othername"))
            .withOnDrawerItemClickListener(
                new Drawer.OnDrawerItemClickListener() {
                  @Override
                  public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                    if (drawerItem == reflection) {
                      FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                      ft.replace(R.id.placeholder, createPost);
                      ft.commit();
                    }
                    return true;
                  }
                })
            .build();
    }
}
