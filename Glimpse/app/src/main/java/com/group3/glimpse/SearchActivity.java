package com.group3.glimpse;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.abs;

public class SearchActivity extends AppCompatActivity {

    // Return to previous activity if back is pressed
    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Create array containing all the media in the database
        ArrayList < MediaDoc > mediaList = HomePage.mediaList;

        // Create variables for the fields in the xml file
        Button SearchBtn = (Button) findViewById(R.id.searchBtn);
        Button backBtn = (Button) findViewById(R.id.back);
        MultiAutoCompleteTextView searchBox = (MultiAutoCompleteTextView) findViewById(R.id.searchBox);

        // A list of textviews to place search results in
        TextView textView_0 = (TextView) findViewById(R.id.textView_0);
        TextView textView_1 = (TextView) findViewById(R.id.textView_1);
        TextView textView_2 = (TextView) findViewById(R.id.textView_2);
        TextView textView_3 = (TextView) findViewById(R.id.textView_3);
        TextView textView_4 = (TextView) findViewById(R.id.textView_4);
        TextView textView_5 = (TextView) findViewById(R.id.textView_5);
        TextView textView_6 = (TextView) findViewById(R.id.textView_6);
        TextView textView_7 = (TextView) findViewById(R.id.textView_7);
        TextView textView_8 = (TextView) findViewById(R.id.textView_8);
        TextView textView_9 = (TextView) findViewById(R.id.textView_9);

        ArrayList < TextView > searchResults = new ArrayList < > ();
        searchResults.add(textView_0);
        searchResults.add(textView_1);
        searchResults.add(textView_2);
        searchResults.add(textView_3);
        searchResults.add(textView_4);
        searchResults.add(textView_5);
        searchResults.add(textView_6);
        searchResults.add(textView_7);
        searchResults.add(textView_8);
        searchResults.add(textView_9);

        for (TextView t: searchResults) {
            t.setOnClickListener(v -> {

                int id = t.getId();
                System.out.println("fetch mediaID: " + id);
                loadMediaInfo(id);

            });
        }

        // Code to control auto-complete search box
        searchBox.announceForAccessibility("Search by title or category.");

        searchBox.setOnClickListener(v -> searchBox.setText(""));

        // Functional code
        SearchBtn.setOnClickListener(v -> {

            ClearTextViews(searchResults);

            final String whatWasSearched = searchBox.getText().toString().toLowerCase();

            ArrayList < Integer > results = new ArrayList < > ();

            int n = 0;

            for (MediaDoc m: mediaList) {

                // Break loop for more than 9 entries displayed
                if (n >= 9) break;

                // Allow up to four missed/incorrect characters for search results
                if (whatWasSearched.length() == 0) {
                    searchResults.get(0).setText("Please enter a valid search term!");
                    break;
                }

                if (m.getTitle().toLowerCase().contains(whatWasSearched)) {
                    searchResults.get(n).setText(m.getTitle());
                    searchResults.get(n).setId(m.getId());
                    n++;
                }

                if (m.getCategory().toLowerCase().contains(whatWasSearched)) {
                    searchResults.get(n).setText(m.getTitle());
                    searchResults.get(n).setId(m.getId());
                    n++;
                }
            }
        });

        // Scheduled task to check for interruptions to network connectivity every second
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            if (!isNetworkActive())
                Toast.makeText(getApplicationContext(), "[ERROR] Network connectivity interrupted!", Toast.LENGTH_LONG).show();
        }, 0, 1, TimeUnit.SECONDS);

        // Go back to previous activity if "back" is pressed
        backBtn.setOnClickListener(v -> finish());
    }
    // Method to clear the textViews on the results page
    private void ClearTextViews(ArrayList < TextView > textList) {
        for (TextView t: textList) t.setText("");
    }

    private void loadMediaInfo(int mediaID) {
        for (MediaDoc m: HomePage.mediaList)

            if (m.getId() == mediaID) {
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

    private boolean isNetworkActive() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnected();
    }

}