package com.group3.glimpse;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class LoadMedia extends AppCompatActivity {

    TextView title, description, actors;
    ImageView mediaImage, closeButton;
    Button trackButton, notifButtn;

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
        notifButtn = (Button) findViewById(R.id.notifBtn);

        Bundle b = getIntent().getExtras();

        String mediaTitle = b.getString("title");
        title.setText(mediaTitle);

        String desc = b.getString("description");
        description.setText(desc);

        String cast = b.getString("cast");
        actors.setText(cast);

        int mediaID = b.getInt("mediaID");

        // Only show notification settings if user is tracking media
        if (MainActivity.user.getTrackedIDs().contains(mediaID))
            notifButtn.setVisibility(View.VISIBLE);
        else
            notifButtn.setVisibility(View.INVISIBLE);

        notifButtn.setOnClickListener(v -> loadNotificationSettings(mediaTitle, mediaID));

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
                notifButtn.setVisibility(View.VISIBLE);
            } else {
                MainActivity.user.removeTrackedMedia(mediaID);
                trackButton.setText("Track");
                notifButtn.setVisibility(View.INVISIBLE);
                MainActivity.user.removeNotificationSettings(mediaID);
                MainActivity.user.removeNotificationID(mediaID);
            }

        });

        System.out.println("Title: " + title + "\nDescription: " + desc + "\nCast: " + cast);
    }

    private void loadNotificationSettings(String mediaTitle, int mediaID) {

        Intent i = new Intent(this, NotificationActivity.class);
        i.putExtra("title", mediaTitle);
        i.putExtra("mediaID", mediaID);
        startActivity(i);

    }

    private void reloadSource() {
        finish();
    }

}