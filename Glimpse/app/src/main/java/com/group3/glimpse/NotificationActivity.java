package com.group3.glimpse;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;


public class NotificationActivity extends AppCompatActivity {

    EditText days;
    TextView title, description, notifInfo;
    ImageView closeButton;
    Button notificationButton, removeNotificationButton;
    Spinner am_or_pm, num_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        title = (TextView) findViewById(R.id.notificationTitle);
        description = (TextView) findViewById(R.id.notificationDescription);
        closeButton = (ImageView) findViewById(R.id.closeNotification);
        notificationButton = (Button) findViewById(R.id.addNotificationBtn);
        removeNotificationButton = (Button) findViewById(R.id.removeNotificationBtn);
        num_time = (Spinner) findViewById(R.id.notification_num_time);
        am_or_pm = (Spinner) findViewById(R.id.notification_am_or_pm);
        notifInfo = (TextView) findViewById(R.id.notifInfo);

        Bundle b = getIntent().getExtras();
        String mediaTitle = b.getString("title");
        int mediaID = b.getInt("mediaID");

        title.setText(mediaTitle + " Notification Settings");

        System.out.println("ID is: " + mediaID);

        if (MainActivity.user.getNotificationIDs().contains(mediaID)) {

            System.out.println("ID found in notificationIDs for user");

            notificationButton.setVisibility(View.INVISIBLE);
            removeNotificationButton.setVisibility(View.VISIBLE);
            num_time.setVisibility(View.INVISIBLE);
            am_or_pm.setVisibility(View.INVISIBLE);

            for (NotificationSettings n: MainActivity.user.getnSettings()) {

                System.out.println("Found media " + n.getNotifMediaID());

                if (n.getNotifMediaID() == mediaID) {

                    String dayNight = "PM";

                    if (n.isDay())
                        dayNight = "AM";

                    notifInfo.setText("Notification set for: " + n.getHour() + " " + dayNight);
                    break;
                }
            }

            notifInfo.setVisibility(View.VISIBLE);
            description.setVisibility(View.INVISIBLE);
        } else {

            System.out.println("ID NOT found in notificationIDs for user");

            notificationButton.setVisibility(View.VISIBLE);
            removeNotificationButton.setVisibility(View.INVISIBLE);
            num_time.setVisibility(View.VISIBLE);
            am_or_pm.setVisibility(View.VISIBLE);
            notifInfo.setVisibility(View.INVISIBLE);
            description.setVisibility(View.VISIBLE);
        }

        notificationButton.setOnClickListener(v -> {

            MainActivity.user.addNotifcationID(mediaID);

            boolean isDay = false;

            if (am_or_pm.getSelectedItem().toString().equalsIgnoreCase("AM"))
                isDay = true;

            Toast.makeText(getApplicationContext(), "Notification for " + mediaTitle + " has been added", Toast.LENGTH_LONG).show();
            notificationButton.setVisibility(View.INVISIBLE);
            removeNotificationButton.setVisibility(View.VISIBLE);

            description.setVisibility(View.INVISIBLE);

            notifInfo.setText("Notification set for: " + num_time.getSelectedItem().toString() + " " + am_or_pm.getSelectedItem().toString());
            int selectHour = Integer.parseInt(num_time.getSelectedItem().toString());

            // Schedule notification here
            Intent i = new Intent(this, Notify.class);
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            PendingIntent p = PendingIntent.getActivity(this, 0, i, 0);
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, selectHour);
            cal.set(Calendar.MINUTE, 00);
            cal.set(Calendar.SECOND, 00);
            am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, p);

            MainActivity.user.addNotifcationSettings(mediaID, selectHour, isDay);
            notifInfo.setVisibility(View.VISIBLE);

            num_time.setVisibility(View.INVISIBLE);
            am_or_pm.setVisibility(View.INVISIBLE);

        });

        removeNotificationButton.setOnClickListener(v -> {

            MainActivity.user.removeNotificationID(mediaID);
            MainActivity.user.removeNotificationSettings(mediaID);

            Toast.makeText(getApplicationContext(), "Notification for " + mediaTitle + " has been removed", Toast.LENGTH_LONG).show();
            removeNotificationButton.setVisibility(View.INVISIBLE);
            notificationButton.setVisibility(View.VISIBLE);

            description.setVisibility(View.VISIBLE);

            notifInfo.setVisibility(View.INVISIBLE);

            num_time.setVisibility(View.VISIBLE);
            am_or_pm.setVisibility(View.VISIBLE);

        });

        //TO go back to the homepage
        closeButton.setOnClickListener(v -> finish());
    }

}