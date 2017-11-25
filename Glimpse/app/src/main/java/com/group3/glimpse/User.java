package com.group3.glimpse;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

/**
 * Created by Coleman on 11/19/2017.
 */

public class User {

    private FirebaseAuth userAuth;
    private ArrayList <Integer> trackedIDs, notificationIDs;
    private ArrayList <Long> notificationSettings;

    public User(FirebaseAuth token, ArrayList<Integer> tIDs, ArrayList<Long> notifcations, ArrayList<Integer> nIDs)
    {
        this.userAuth = token;
        this.trackedIDs = tIDs;
        this.notificationSettings = notifcations;
        this.notificationIDs = nIDs;
    }

    public FirebaseAuth getUserAuth() {
        return userAuth;
    }

    public ArrayList<Integer> getTrackedIDs() {
        return trackedIDs;
    }

    public ArrayList<Long> getNotificationSettings() {
        return notificationSettings;
    }

    public ArrayList<Integer> getNotificationIDs() { return notificationIDs; }

    public void addTrackedMedia(int mediaID)
    {
        if (trackedIDs.contains(mediaID))
            System.out.println("Already added, won't add duplicates!");
        else {
            trackedIDs.add(mediaID);
            System.out.println("[INFO] Added " + mediaID + " to tracked content");
        }

        System.out.println("Current tracked content: ");

        for (int i : trackedIDs)
            System.out.println(i);
    }

    public void removeTrackedMedia(int mediaID)
    {
        for (int i = 0; i < trackedIDs.size(); i++)
        {
            if (trackedIDs.get(i).equals(mediaID)) {
                trackedIDs.remove(i);
                System.out.println("[INFO] Removed media: " + mediaID);
                break;
            }
        }

        System.out.println("Current tracked content: ");

        for (int i : trackedIDs)
            System.out.println(i);
    }

    public void logUserOut()
    {
        userAuth.signOut();
        System.out.println("User logged out successfully!");
    }

}
