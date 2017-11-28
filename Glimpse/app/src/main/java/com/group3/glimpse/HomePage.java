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
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Bitmap;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
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
    static ArrayList <MediaDoc> mediaList;

    TextView title, description, actors;
    ImageView mediaImage, closeButton;
    ImageButton trackButton, searchButton;

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
        // Search button
        searchButton = (ImageButton) findViewById(R.id.searchButton);

        movies.setGravity(Gravity.CENTER);

        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open_drawer, R.string.close_drawer);

        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        // Switch to search if search button pressed
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchSearch();
            }
        });

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
                    ArrayList<ImageView> imageArray = getImagesForMedia(mediaID, isMovie);

                    MediaDoc mediaDoc = new MediaDoc(cast, tags, desc, isMovie, mediaID, tStart, title,
                            trailer, uploader, imageArray);

                    // Add the mediaDoc to the reference array and increment mediaID
                    mediaList.add(mediaDoc);
                }

                movies.removeAllViews();
                tv.removeAllViews();

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

    private ArrayList <ImageView> getImagesForMedia(int mediaID, boolean isMovie)
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
                String location = "";

                if (isMovie)
                    location = "movies/" + mediaID + "_" + i + ".jpg";
                else
                    location = "tv/" + mediaID + "_" + i + ".jpg";

                System.out.println(location);
                pathRef = storageRef.child(location);

                Glide.with(this).using(new FirebaseImageLoader()).load(pathRef).into(mediaPic);
                mediaPic.setLayoutParams(layoutParm);

                // If its the displayIcon for the media, add an ID and a clickListener
                if (i == 0)
                {
                    // GetID on an imageView will now return which mediaID to get info for from the list
                    mediaPic.setId(mediaID);
                    mediaPic.setOnClickListener(v -> loadMediaInfo(mediaID));
                }

                mediaImages.add(mediaPic);

            } catch (Exception err)
            {
                System.out.println("StorageRef child for " + mediaID + ":" + i + " was not found on the database");
            }
        }

        return mediaImages;
    }

    private void loadMediaInfo(int mediaID)
    {
        System.out.println("Load stuff for mediaID: " + mediaID);

        for (MediaDoc m : mediaList)

            if (m.getId() == mediaID)
            {
                // Gets a byte array for the image to send to the loadMedia activity
                ImageView icon = m.getMediaIcon();
                icon.buildDrawingCache();
                Bitmap img = icon.getDrawingCache();

                ByteArrayOutputStream imgStream = new ByteArrayOutputStream();
                img.compress(Bitmap.CompressFormat.JPEG, 100, imgStream);
                byte[] imgBytes = imgStream.toByteArray();

                String title = m.getTitle(), desc = m.getDescription(), cast = m.getCast();

                /*
                for (ImageView v : m.getImages())
                {
                    // get each image ready to package
                }
                */

                Intent intent = new Intent(this, LoadMedia.class);

                intent.putExtra("title", title);
                intent.putExtra("mediaID", mediaID);
                intent.putExtra("imgBytes", imgBytes);
                intent.putExtra("description", desc);
                intent.putExtra("cast", cast);

                startActivity(intent);
                break;
            }
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

            case R.id.home_id:

                movies.removeAllViews();
                tv.removeAllViews();

                for (MediaDoc m : mediaList)
                {
                    if (m.isMovie()) {
                        movies.addView(m.getMediaIcon());
                    //    System.out.println("added icon for movie, ID: "+ m.getId());
                    }
                    else {
                        tv.addView(m.getMediaIcon());
                    //    System.out.println("added icon for tv show, ID: "+ m.getId());
                    }
                }

                break;

            case R.id.tracked_id:

                Toast.makeText(getApplicationContext(), "tracked", Toast.LENGTH_LONG).show();

                movies.removeAllViews();
                tv.removeAllViews();

                for (int i : MainActivity.user.getTrackedIDs())
                {
                    for (MediaDoc m : mediaList) {
                        if (m.getId() == i) {
                            if (m.isMovie())
                                movies.addView(m.getMediaIcon());
                            else
                                tv.addView(m.getMediaIcon());
                        }
                    }
                }

                break;

            case R.id.notifications_id:

                launchNotifications();

                // Same code as tracked, just a little extra for things you want to be notified about
                movies.removeAllViews();
                tv.removeAllViews();

                for (int i : MainActivity.user.getNotificationIDs())
                {
                    for (MediaDoc m : mediaList) {
                        if (m.getId() == i) {
                            if (m.isMovie())
                                movies.addView(m.getMediaIcon());
                            else
                                tv.addView(m.getMediaIcon());
                        }
                    }
                }

                Toast.makeText(getApplicationContext(), "notifications", Toast.LENGTH_LONG).show();

                break;

            case R.id.suggestions_id:

                movies.removeAllViews();
                tv.removeAllViews();

                if (MainActivity.user.getTrackedIDs().size() > 0)
                {
                    int rId = MainActivity.user.getTrackedIDs().get(0);
                    String cat = "meow";    // bad joke, I know

                    // Find the media tracked by the user and get its category
                    for (MediaDoc m : mediaList) {
                        if (m.getId() == rId) {
                            cat = m.getCategory();
                            break;
                        }
                    }

                    for (MediaDoc m : mediaList) {

                        // If the categories match and the media isn't already tracked by the user, add it to the view
                        if (m.getCategory().contains(cat) && !MainActivity.user.getTrackedIDs().contains(m.getId())) {
                            if (m.isMovie())
                                movies.addView(m.getMediaIcon());
                            else
                                tv.addView(m.getMediaIcon());
                        }
                    }

                    System.out.println("Displaying suggestions based on users tracked content");
                }

                else
                {
                    System.out.println("YO ADD SOME STUFF TO MAKE THIS SHIT EASIER MAN");
                }

                break;

            case R.id.frequentlyAskedQuestions_id:

                // TODO: Swap to new activity for basic instructions for using app
                Toast.makeText(getApplicationContext(), "frequently asked questions", Toast.LENGTH_LONG).show();

                break;

        }

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    // Activity switcher
    private void launchSearch()
    {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }


    // Activity switcher
    private void launchNotifications()
    {
        Intent intent = new Intent(this, NotificationActivity.class);
        startActivity(intent);
    }


}
