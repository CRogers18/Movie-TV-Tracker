package com.group3.glimpse;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class LoadingActivity extends AppCompatActivity
{
    // Get a reference to Firebase storage
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference storageRef = storage.getReference();
    private ArrayList<MediaDoc> mediaList = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        // Get a reference to database media
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("/media");

        // Attach a listener to read changes
        ref.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {

                    // Returns true for "Movie" or false for "TV Show"
                    boolean isMovie = snap.child("format").equals("Movie");

                    // Extract data from database and create and array of MediaDocs
                    String cast = snap.child("cast").getValue().toString();
                    String tags = snap.child("category").getValue().toString();
                    String desc = snap.child("description").getValue().toString();
                    int mediaID = Integer.parseInt(snap.child("id").getValue().toString());
                    String tStart = snap.child("releaseDate").getValue().toString();
                    String title = snap.child("title").getValue().toString();
                    String trailer = snap.child("trailerLink").getValue().toString();
                    String uploader = snap.child("uploader").getValue().toString();

                    // TODO: Add this to mediaDoc constructor
                    long tStartUnix = Long.parseLong(tStart);

                    // Grab media images from storage
                    ArrayList<ImageView> imageArray = getImagesForMedia(mediaID);

                    MediaDoc mediaDoc = new MediaDoc(cast, tags, desc, isMovie, mediaID, tStart, title,
                                                     trailer, uploader, imageArray);

                    // Add the mediaDoc to the reference array and increment mediaID
                    mediaList.add(mediaDoc);
                    mediaID++;
                }

                loadHomePage();
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private ArrayList <ImageView> getImagesForMedia(int mediaID)
    {
        ArrayList <ImageView> mediaImages = new ArrayList<>();

        // Tries to load 3 images associated with that mediaID if they exist
        for (int i = 0; i < 3; i++)
        {
            ImageView mediaPic = new ImageView(this);
            try
            {
                storageRef.child(mediaID + "_" + i + ".jpg");
                Glide.with(this).using(new FirebaseImageLoader()).load(storageRef).into(mediaPic);
                mediaImages.add(mediaPic);
            } catch (Exception err)
            {
                System.out.println("StorageRef child for " + mediaID + ":" + i + " was not found on the database");
            }
        }

        return mediaImages;
    }

    private void loadHomePage()
    {
        Intent intent = new Intent(this, HomePage.class);
        Bundle b = new Bundle();
        b.putSerializable("mediaArray", mediaList);
        intent.putExtras(b);
        startActivity(intent);
    }
}
