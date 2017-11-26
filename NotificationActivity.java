package com.group3.glimpse;

        import android.content.Context;
        import android.content.Intent;
        import android.media.Image;
        import android.net.ConnectivityManager;
        import android.net.NetworkInfo;
        import android.net.Uri;
        import android.provider.MediaStore;
        import android.support.annotation.NonNull;
        import android.support.design.widget.NavigationView;
        import android.support.v4.view.GravityCompat;
        import android.support.v4.widget.DrawerLayout;
        import android.support.v7.app.ActionBarDrawerToggle;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.Gravity;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.EditText;
        import android.widget.HorizontalScrollView;
        import android.widget.ImageButton;
        import android.widget.ImageView;
        import android.support.v7.widget.Toolbar;
        import android.widget.LinearLayout;
        import android.widget.Spinner;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.bumptech.glide.Glide;
        import com.firebase.ui.storage.images.FirebaseImageLoader;
        import com.google.android.gms.tasks.OnSuccessListener;
        import com.google.firebase.FirebaseApp;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;
        import com.google.firebase.storage.FirebaseStorage;
        import com.google.firebase.storage.StorageException;
        import com.google.firebase.storage.StorageReference;

        import org.w3c.dom.Text;

        import java.util.ArrayList;
        import java.util.concurrent.Executors;
        import java.util.concurrent.ScheduledExecutorService;
        import java.util.concurrent.TimeUnit;


public class NotificationActivity extends AppCompatActivity {


    EditText days;
    TextView title, description;
    ImageView mediaImage, closeButton;
    ImageButton notificationButton;
    Spinner am_or_pm, num_time;
    private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        title = (TextView) findViewById(R.id.notificationTitle);
        description = (TextView) findViewById(R.id.notificationDescription);
        mediaImage = (ImageView) findViewById(R.id.notificationImage);
        closeButton = (ImageView) findViewById(R.id.closeNotification);
        notificationButton = (ImageButton) findViewById(R.id.notificationButton);
        days = (EditText) findViewById(R.id.inputDays);
        num_time = (Spinner) findViewById(R.id.notification_num_time);
        am_or_pm = (Spinner) findViewById(R.id.notification_am_or_pm);




        //TO go back to the homepage
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadHomePage();
            }
        });

    }



    // Activity switcher
    private void loadHomePage()
    {
        Intent intent = new Intent(this, HomePage.class);
        startActivity(intent);
    }

}
