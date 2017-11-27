package com.group3.glimpse;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class LoadMedia extends AppCompatActivity {

    TextView title, description, actors;
    ImageView mediaImage, closeButton;
    Button trackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_media);

        title = (TextView) findViewById(R.id.titleOfMedia);
        description = (TextView) findViewById(R.id.descriptionOfMedia);
        actors = (TextView) findViewById(R.id.actorsInMedia);
        mediaImage = (ImageView) findViewById(R.id.imageOfMedia);
        closeButton = (ImageView) findViewById(R.id.closeSpecificMedia);
        trackButton = (Button) findViewById(R.id.trackBtn);

        Bundle b = getIntent().getExtras();

        String mediaTitle = b.getString("title");
        title.setText(mediaTitle);

        String desc = b.getString("description");
        description.setText(desc);

        String cast = b.getString("cast");
        actors.setText(cast);

        int mediaID = b.getInt("mediaID");

        byte[] imgBytes = getIntent().getByteArrayExtra("imgBytes");
        Bitmap bitM = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
        mediaImage.setImageBitmap(bitM);

        if (MainActivity.user.getTrackedIDs().contains(mediaID))
            trackButton.setText("Un-Track");
        else
            trackButton.setText("Track");

        closeButton.setOnClickListener(v -> reloadSource());
        trackButton.setOnClickListener(v -> {

            if (trackButton.getText().equals("Track")) {
                MainActivity.user.addTrackedMedia(mediaID);
                trackButton.setText("Un-Track");
            }

            else {
                MainActivity.user.removeTrackedMedia(mediaID);
                trackButton.setText("Track");
            }

        });

        System.out.println("Title: " + title + "\nDescription: " + desc + "\nCast: " + cast);
    }

    private void reloadSource()
    {
        finish();
    }

}
