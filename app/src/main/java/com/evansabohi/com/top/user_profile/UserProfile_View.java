package com.evansabohi.com.top.user_profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.evansabohi.com.top.R;
import com.evansabohi.com.top.RequestPermissions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class UserProfile_View extends AppCompatActivity {
  TextView userName,userNative,userAge,userDesc;
  ImageView coverPhoto;
  CircleImageView profileImage;
    DatabaseReference mProfileDb;
    FirebaseAuth mAuth;
    ValueEventListener coverValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uses_profile_view);

        // Initializing views
        userName = findViewById(R.id.userName);
        userNative = findViewById(R.id.userNatvie);
        userAge = findViewById(R.id.urAge);
        userDesc = findViewById(R.id.userDesc);
        profileImage = findViewById(R.id.profPhoto);
        coverPhoto = findViewById(R.id.profCover);
        String uid = getIntent().getStringExtra("id");


        mAuth = FirebaseAuth.getInstance();
        mProfileDb = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        coverValue = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    ProfileModelNames profileModelNames = dataSnapshot.getValue(ProfileModelNames.class);
                    final String coverImage = profileModelNames.getCoverphoto();
                    String name = profileModelNames.getName();
                    String nativeName = profileModelNames.getNative_name();
                    String ageName = profileModelNames.getUser_age();
                    final String proImage = profileModelNames.getImage();
                    String profDesc = profileModelNames.getUser_info();
                    userName.setText("Name: "+name);
                    userNative.setText("Native Name: "+nativeName);
                    userAge.setText("Age: "+ageName);

                    Picasso.with(getApplicationContext()).load(proImage).networkPolicy(NetworkPolicy.OFFLINE).into(profileImage, new Callback() {
                        @Override
                        public void onSuccess() {


                        }

                        @Override
                        public void onError() {
                            Picasso.with(getApplicationContext()).load(proImage).into(profileImage);
                        }
                    });
                    userDesc.setText(profDesc);
                    if (coverImage!=null){

                        Picasso.with(getApplicationContext()).load(coverImage).networkPolicy(NetworkPolicy.OFFLINE).into(coverPhoto, new Callback() {
                            @Override
                            public void onSuccess() {


                            }

                            @Override
                            public void onError() {
                                Picasso.with(getApplicationContext()).load(coverImage).into(coverPhoto);
                            }
                        });
                    }
                }
                else {
                    coverPhoto.setImageResource(R.drawable.hompagescreen);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mProfileDb.addValueEventListener(coverValue);




    }

}
