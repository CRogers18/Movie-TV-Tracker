package com.group3.cop4331.glimpsetest;

import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Coleman on 10/6/2017.
 */

public class MediaDoc {

    private String mediaName, description, tags;

    // Media images stored in arraylist as # of images uploaded will be unknown beforehand
    private ArrayList <ImageView> mediaImages;

    // if isMovie is false than the media is a TV show
    private boolean isMovie;

    // start and endTime will be stored in UNIX time, web app should handle conversion to UNIX time
    // from producer input times and should store the result in the DB as a string or a long, whatever
    // you prefer
    private long startTime, endTime;

    // MediaDoc constructor
    public MediaDoc(String name, String desc, boolean isMovie, ArrayList <ImageView> images,
                         String genreTags, String tStart, String tEnd)
    {
        this.mediaName = name;
        this.description = desc;
        this.isMovie = isMovie;
        this.mediaImages = images;
        this.tags = genreTags;
        this.startTime = Long.parseLong(tStart);
        this.endTime = Long.parseLong(tEnd);
    }

    // Getters for each MediaDoc instance, no need for setters as the constructor will handle var init

    public String getMediaName() {
        return mediaName;
    }

    public String getDescription() {
        return description;
    }

    public String getTags() {
        return tags;
    }

    public ArrayList<ImageView> getMediaImages() {
        return mediaImages;
    }

    // NOTE: mediaImages[0] should ALWAYS hold the Media's preview icon
    public ImageView getMediaIcon() {
        return mediaImages.get(0);
    }

    public boolean isMovie() {
        return isMovie;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }
}