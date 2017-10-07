package com.group3.cop4331.glimpsetest;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

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

        /*
        // Super long and drawn out, less sexy way of adding a listener </3
        image.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                System.out.println("Nice click m8");
            }

        });
        */

    }

}
