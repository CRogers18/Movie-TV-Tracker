package com.group3.glimpse;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    HorizontalScrollView movieView, tvView;
    LinearLayout movies, tv;
    ArrayList <MediaDoc> mediaList;

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

        makeMediaRefArray();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        movies = (LinearLayout) findViewById(R.id.movieList);
        tv = (LinearLayout) findViewById(R.id.tvList);
        movieView = (HorizontalScrollView) findViewById(R.id.movies);

        movies.setGravity(Gravity.CENTER);

        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open_drawer, R.string.close_drawer);

        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        // Scheduled task to check for interruptions to network connectivity every second
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            if (isNetworkActive() != true)
                Toast.makeText(getApplicationContext(), "[ERROR] Network connectivity interrupted!", Toast.LENGTH_LONG).show();
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void makeMediaRefArray()
    {
        // Database objects
        FirebaseStorage storage = FirebaseStorage.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // Database references
        DatabaseReference ref = database.getReference("/media");
        StorageReference storageRef = storage.getReference();

        mediaList = new ArrayList();

        ref.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {

                    // Returns true for "Movie" or false for "TV Show"
                    boolean isMovie = snap.child("format").getValue().toString().equals("Movie");

                    // Extract data from database and create and array of MediaDocs
                    String cast = snap.child("cast").getValue().toString();
                    String tags = snap.child("category").getValue().toString();
                    String desc = snap.child("description").getValue().toString();
                    int mediaID = Integer.parseInt(snap.child("id").getValue().toString());
                    String tStart = snap.child("releaseDate").getValue().toString();
                    String title = snap.child("title").getValue().toString();
                    String trailer = snap.child("trailerLink").getValue().toString();
                    String uploader = snap.child("uploader").getValue().toString();

                    // TODO: Add this to mediaDoc constructor
                    long tStartUnix = Long.parseLong(tStart);

                    // Grab media images from storage
                    ArrayList<ImageView> imageArray = getImagesForMedia(mediaID);

                    MediaDoc mediaDoc = new MediaDoc(cast, tags, desc, isMovie, mediaID, tStart, title,
                            trailer, uploader, imageArray);

                    // Add the mediaDoc to the reference array and increment mediaID
                    mediaList.add(mediaDoc);
                }

                for (MediaDoc m : mediaList)
                {
                    if (m.isMovie()) {
                        movies.addView(m.getMediaIcon());
                        System.out.println("added icon for movie, ID: "+ m.getId());
                    }
                    else {
                        tv.addView(m.getMediaIcon());
                        System.out.println("added icon for tv show, ID: "+ m.getId());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private ArrayList <ImageView> getImagesForMedia(int mediaID)
    {
        ArrayList <ImageView> mediaImages = new ArrayList<>();
        LinearLayout.LayoutParams layoutParm = new LinearLayout.LayoutParams(300, 500);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://poosd-test.appspot.com/");
        StorageReference pathRef;

        // Tries to load 1 images associated with that mediaID if they exist
        for (int i = 0; i < 1; i++)
        {
            ImageView mediaPic = new ImageView(this);
            try
            {
                String location = "movies/" + mediaID + "_" + i + ".jpg";
                System.out.println(location);
                pathRef = storageRef.child(location);

                Glide.with(this).using(new FirebaseImageLoader()).load(pathRef).into(mediaPic);
                mediaPic.setLayoutParams(layoutParm);

                // If its the displayIcon for the media, add an ID and a clickListener
                if (i == 0)
                {
                    // GetID on an imageView will now return which mediaID to get info for from the list
                    mediaPic.setId(mediaID);
                    mediaPic.setOnClickListener(v -> System.out.println("TODO: Load stuff for mediaID: " + mediaPic.getId()));
                }

                mediaImages.add(mediaPic);

            } catch (Exception err)
            {
                System.out.println("StorageRef child for " + mediaID + ":" + i + " was not found on the database");
            }
        }

        return mediaImages;
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
