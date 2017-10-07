package com.group3.cop4331.glimpsetest;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        loadDatabase();
    }

    private void loadDatabase()
    {
        String[] quoteText = {"Frankly, my dear, I don't give a damn.", "Here's looking at you, kid.",
                "E.T. phone home.", "You can't handle the truth!"};

        TextView loadingText = (TextView) findViewById(R.id.loadText);
        ProgressBar loading = (ProgressBar) findViewById(R.id.progressBar);

        loading.setMax(100);
        loading.setProgress(0);

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {

                runOnUiThread(() -> {

                    int r = 0;

                    if (loading.getProgress() < 100)
                    {
                        loading.setProgress(loading.getProgress() + 25);
                     //   r = (int) (Math.random() * quoteText.length);
                        loadingText.setText(quoteText[r]);
                        r++;
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
