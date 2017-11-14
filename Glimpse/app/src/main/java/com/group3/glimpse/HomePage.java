package com.group3.glimpse;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;


    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)){

            drawerLayout.closeDrawer(GravityCompat.START);

        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open_drawer, R.string.close_drawer);

        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();


        // Scheduled task to check for interruptions to network connectivity every second
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {

            if (isNetworkActive() != true)
                System.out.println("[ERROR] Network connectivity interrupted or not found!");

        }, 0, 1, TimeUnit.SECONDS);

    }

    private boolean isNetworkActive()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnected();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            case R.id.shows_id:

                Toast.makeText(getApplicationContext(), "shows", Toast.LENGTH_LONG).show();

                break;

            case R.id.movies_id:

                Toast.makeText(getApplicationContext(), "movies", Toast.LENGTH_LONG).show();

                break;

            case R.id.tracked_id:

                Toast.makeText(getApplicationContext(), "tracked", Toast.LENGTH_LONG).show();

                break;

            case R.id.notifications_id:

                Toast.makeText(getApplicationContext(), "notifications", Toast.LENGTH_LONG).show();

                break;

            case R.id.suggestions_id:

                Toast.makeText(getApplicationContext(), "suggestions", Toast.LENGTH_LONG).show();

                break;

            case R.id.frequentlyAskedQuestions_id:

                Toast.makeText(getApplicationContext(), "frequently asked questions", Toast.LENGTH_LONG).show();

                break;

        }

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;

    }
}
