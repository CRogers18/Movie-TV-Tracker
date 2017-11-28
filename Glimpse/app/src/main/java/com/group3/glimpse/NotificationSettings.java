package com.group3.glimpse;

/**
 * Created by Coleman on 11/27/2017.
 */

public class NotificationSettings {

    private long notifTime;
    private boolean isDay;
    private int notifMediaID, hour, notificationID;

    public NotificationSettings(int nID, int hour, boolean isDay)
    {
        this.notifMediaID = nID;
        this.hour = hour;
        this.isDay = isDay;
    }

    public long getNotifTime() {
        return notifTime;
    }

    public boolean isDay() {
        return isDay;
    }

    public int getNotifMediaID() {
        return notifMediaID;
    }

    public int getHour() {
        return hour;
    }

}
