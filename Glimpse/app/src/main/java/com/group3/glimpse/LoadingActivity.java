package com.group3.glimpse;

import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

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

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        // Start of my code
        ArrayList<MediaDoc> mediaList = new ArrayList();
        Toast.makeText(this, "You Logged in! Fancy that.", Toast.LENGTH_SHORT).show();

        // Get a reference to database media
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("/media");

        // Get a reference to Firebase storage
        final FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Attach a listener to read changes
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    boolean isMovie = false;
                    ArrayList<ImageView> imageArray = new ArrayList<ImageView>();

                    // Extract data from database and create and array of MediaDocs
                    String cast = snap.child("cast").getValue().toString();
                    String tags = snap.child("category").getValue().toString();
                    String desc = snap.child("description").getValue().toString();
                    String format = snap.child("format").getValue().toString();
                    String id = snap.child("id").getValue().toString();
                    String tStart = snap.child("releaseDate").getValue().toString();
                    String title = snap.child("title").getValue().toString();
                    String trailer = snap.child("trailerLink").getValue().toString();
                    String uploader = snap.child("uploader").getValue().toString();
                    Integer n = -1;
                    while(n != -1) {
                        storageRef.child(id + "_" + n.toString() + ".jpg").getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                            //    n = -1;
                            }
                        });
                    }
                    //StorageReference image = storageRef.child().(id.substring(0,id.indexOf('_')));
                    MediaDoc mediaDoc = new MediaDoc(cast, tags, desc, format, id, tStart, title,
                            trailer, uploader, imageArray);
                    mediaList.add(mediaDoc);
                }
                // Show me something
                for( MediaDoc m : mediaList)
                {
                    Toast.makeText(getApplicationContext(), "It will be released on " +
                            m.getReleaseDate(), Toast.LENGTH_SHORT).show();
                }
                long childs = dataSnapshot.getChildrenCount();
                Toast.makeText(getApplicationContext(), "This database holds a whopping "
                        + childs + " items.", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Toast.makeText(getApplicationContext(), "What's this? A show or movie has" +
                        " been changed.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Toast.makeText(getApplicationContext(), "Something is amiss-ing. It was" +
                        " probably pants anyway.", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Toast.makeText(getApplicationContext(), "Blast it all! Where did he go.",
                        Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Well that's the end of that.",
                        Toast.LENGTH_SHORT).show();

            }
        });

    }
}
