package com.group3.glimpse;

import android.media.Image;
import android.widget.ImageView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by simon on 11/12/2017.
 */

public class MediaDoc {
    // The getters
    public String getCast() {
        return cast;
    }
    public String getCategory() {
        return category;
    }
    public String getDescription() {
        return description;
    }
    public String getFormat() {
        return format;
    }
    public int getId() {
        return id;
    }
    public String getReleaseDate() {
        return releaseDate;
    }
    public String getTitle() {
        return title;
    }
    public String getTrailerLink() {
        return trailerLink;
    }
    public String getUploader() {
        return uploader;
    }
    // Make MediaDoc object immutable
    private final String category;
    private final String cast;
    private final String description;
    private final String format;
    private final int id;
    private final String releaseDate;
    private final String title;
    private final String trailerLink;
    private final String uploader;
    private final ArrayList<ImageView> images = new ArrayList<>();

    // Constructor for MediaDoc. Takes categories from database and converts them to easy to use
    // variables for the app.
    public MediaDoc (String cast, String category, String description, String format, String id,
                     String releaseDate, String title, String trailerLink, String uploader,
                     ArrayList<ImageView> images) {
        this.cast = cast;
        this.category = category;
        this.description = description;
        this.format = format;
        this.id = Integer.parseInt(id);
        this.releaseDate = convertToDate(releaseDate, format);
        this.title = title;
        this.trailerLink = trailerLink;
        this.uploader = uploader;
        this.images.addAll(images);
    }
    // Convert the UST to a string date only for movies or time and date for TV shows
    private String convertToDate(String releaseDate, String format) {
        long tStartConverted = Long.parseLong(releaseDate) * 1000;
        Date date = new Date(tStartConverted);
        String release = date.toString();
        try {
            date = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy",
                    Locale.US).parse(release);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(format.compareTo("Movie") == 0) {
            return new SimpleDateFormat("MM/dd/yyyy").format(date);
        }
        else {
            return new SimpleDateFormat("MM/dd/yyyy hh:mm aa").format(date);
        }
    }
}
