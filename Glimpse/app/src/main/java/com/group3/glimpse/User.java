package com.group3.glimpse;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

/**
 * Created by Coleman on 11/19/2017.
 */

public class User {

    private FirebaseAuth userAuth;
    private ArrayList < Integer > trackedIDs, notificationIDs;
    private ArrayList < NotificationSettings > nSettings;

    public User(FirebaseAuth token, ArrayList < Integer > tIDs, ArrayList < NotificationSettings > notifcations, ArrayList < Integer > nIDs) {
        this.userAuth = token;
        this.trackedIDs = tIDs;
        this.nSettings = notifcations;
        this.notificationIDs = nIDs;
    }

    public FirebaseAuth getUserAuth() {
        return userAuth;
    }

    public ArrayList < Integer > getTrackedIDs() {
        return trackedIDs;
    }

    public ArrayList < NotificationSettings > getnSettings() {
        return nSettings;
    }

    public void addNotifcationSettings(int mediaID, int hour, boolean isDay) {
        NotificationSettings notif;

        if (nSettings.size() == 0) {
            notif = new NotificationSettings(mediaID, hour, isDay);
            nSettings.add(notif);
        }

        for (NotificationSettings n: nSettings) {
            if (n.getNotifMediaID() == mediaID)
                break;
            else {
                notif = new NotificationSettings(mediaID, hour, isDay);
                nSettings.add(notif);
                break;
            }
        }
    }

    public void removeNotificationSettings(int mediaID) {
        for (int i = 0; i < nSettings.size(); i++) {
            if (nSettings.get(i).getNotifMediaID() == mediaID) {
                nSettings.remove(i);
                System.out.println("[INFO] Removed notification for: " + mediaID);
                break;
            }
        }
    }

    public ArrayList < Integer > getNotificationIDs() {
        return notificationIDs;
    }

    public void addNotifcationID(int mediaID) {
        if (notificationIDs.contains(mediaID))
            System.out.println("Already added, won't add duplicates!");
        else {
            notificationIDs.add(mediaID);
            System.out.println("[INFO] Added " + mediaID + " to notification settings");
        }
    }

    public void removeNotificationID(int mediaID) {
        for (int i = 0; i < notificationIDs.size(); i++) {
            if (notificationIDs.get(i).equals(mediaID)) {
                notificationIDs.remove(i);
                System.out.println("[INFO] Removed media: " + mediaID);
                break;
            }
        }
    }

    public void addTrackedMedia(int mediaID) {
        if (trackedIDs.contains(mediaID))
            System.out.println("Already added, won't add duplicates!");
        else {
            trackedIDs.add(mediaID);
            System.out.println("[INFO] Added " + mediaID + " to tracked content");
        }
    }

    public void removeTrackedMedia(int mediaID) {
        for (int i = 0; i < trackedIDs.size(); i++) {
            if (trackedIDs.get(i).equals(mediaID)) {
                trackedIDs.remove(i);
                System.out.println("[INFO] Removed media: " + mediaID);
                break;
            }
        }
    }

    public void logUserOut() {
        userAuth.signOut();
        System.out.println("User logged out successfully!");
    }

}