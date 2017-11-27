package com.group3.glimpse;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

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
        ArrayList<MediaDoc> mediaList = HomePage.mediaList;

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

        ArrayList<TextView> searchResults = new ArrayList<TextView>();
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

        // Code to control auto-complete search box
        searchBox.announceForAccessibility("Search by title or category.");

        // Functional code
        SearchBtn.setOnClickListener(v -> {

            ClearTextViews(searchResults);

            final String whatWasSearched = searchBox.getText().toString();

            ArrayList <Integer> results = new ArrayList<>();
            int n = 0;

            for (MediaDoc m : mediaList) {
                if(n >= 9) break;
                // Allow up to four missed/incorrect characters for search results
                if(whatWasSearched.length() == 0) {
                }

                else if (m.getTitle().substring(0, whatWasSearched.length()-1).equalsIgnoreCase(whatWasSearched)) {
                    searchResults.get(n).setText(m.getTitle());
                    n++;
                }
                // Search by category
                else if (m.getCategory().equalsIgnoreCase(whatWasSearched)) {
                    results.add(m.getId());
                    searchResults.get(n).setText(m.getTitle());
                    n++;

                }
            }

        });
        // Scheduled task to check for interruptions to network connectivity every second
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            if (isNetworkActive() != true)
                Toast.makeText(getApplicationContext(), "[ERROR] Network connectivity interrupted!", Toast.LENGTH_LONG).show();
        }, 0, 1, TimeUnit.SECONDS);

        // Go back to previous activity if "back" is pressed
        backBtn.setOnClickListener(v -> finish());
    }
    // Method to clear the textViews on the results page
    private void ClearTextViews(ArrayList<TextView> arrayList) {
        for (TextView t: arrayList) t.setText("");
    }
    private boolean isNetworkActive()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnected();
    }

}
