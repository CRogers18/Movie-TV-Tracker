package com.group3.cop4331.glimpsetest;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Click listener example using an ImageView
        ImageView image = (ImageView) findViewById(R.id.main_logo);

        // Super-sexy Java 8 lambda expressions <3
        image.setOnClickListener(v -> {
            // This swaps activities from the main menu to loading
            Intent i = new Intent(MainActivity.this, LoadingActivity.class);
            MainActivity.this.startActivity(i);
        });

        // Scheduled task to check for interruptions to network connectivity every second
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {

            if (isNetworkActive() != true)
                System.out.println("[ERROR] Network connectivity interrupted or not found!");

        }, 0, 1, TimeUnit.SECONDS);

    }

    // Verifies that a network connection is established
    private boolean isNetworkActive()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnected();
    }

}
