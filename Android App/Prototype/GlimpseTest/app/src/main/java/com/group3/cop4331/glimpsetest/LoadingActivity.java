package com.group3.cop4331.glimpsetest;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LoadingActivity extends AppCompatActivity {

    ArrayList <MediaDoc> media = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference mediaRef = db.getReference("media");

        loadDatabase(db, mediaRef);
    }

    private void loadDatabase(FirebaseDatabase db, DatabaseReference mRef)
    {
        String[] quoteText = {"Frankly, my dear, I don't give a damn.", "Here's looking at you, kid.",
                "E.T. phone home.", "You can't handle the truth!"};

        TextView loadingText = (TextView) findViewById(R.id.loadText);
        ProgressBar loading = (ProgressBar) findViewById(R.id.progressBar);

        loading.setMax(100);
        loading.setProgress(0);

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren())
                {
                    boolean isMovie = false;

                    // Extract data from database
                    String cast = snap.child("cast").getValue().toString();
                    String tags = snap.child("category").getValue().toString();
                    String title = snap.child("title").getValue().toString();
                    String desc = snap.child("description").getValue().toString();
                    String format = snap.child("format").getValue().toString();
                    String tStart = snap.child("releaseDate").getValue().toString();

                    // Default value for tEnd not found
                    String tEnd = "1/1/3000";

                    ArrayList <ImageView> images = new ArrayList<ImageView>();

                    if (format.equals("Movie"))
                        isMovie = true;

                    MediaDoc mediaContent = new MediaDoc(title, desc, isMovie, images, tags, tStart, tEnd);
                    media.add(mediaContent);

                    System.out.println("[MEDIA INFO] " + title + "\n" + desc + "\ntags: " + tags + "\ncast: " + cast + "\n");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {



                runOnUiThread(() -> {

                    int r;

                    if (loading.getProgress() < 100)
                    {
                        loading.setProgress(loading.getProgress() + 25);
                        r = (int) (Math.random() * quoteText.length);
                        loadingText.setText(quoteText[r]);
                    }
                    else
                    {
                        Intent i = new Intent(LoadingActivity.this, MainActivity.class);
                        scheduler.shutdownNow();
                        LoadingActivity.this.startActivity(i);
                    }

                });

            } catch (Exception err) {
                err.printStackTrace();
            }

        }, 0, 3, TimeUnit.SECONDS);



    }

}
