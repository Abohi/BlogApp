package com.evansabohi.com.top.user_profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.evansabohi.com.top.R;
import com.evansabohi.com.top.RequestPermissions;
import com.evansabohi.com.top.new_post.BlogPostScreen;
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

public class ProfileSingleActivity extends AppCompatActivity {
  TextView userName,userNative,userAge,userDesc;
  ImageView coverPhoto;
  CircleImageView profileImage;
  Button coverPhotBnt;
    private Uri imageUri=null;
    private Bitmap compressedImageFile;
    StorageReference storageReference;
    DatabaseReference mProfileDb;
    FirebaseAuth mAuth;
    String current_uid;
    ValueEventListener coverValue;
private ProgressDialog mProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_profile_single);

        // Initializing views
        userName = findViewById(R.id.userName);
        userNative = findViewById(R.id.userNatvie);
        userAge = findViewById(R.id.urAge);
        userDesc = findViewById(R.id.userDesc);
        profileImage = findViewById(R.id.profPhoto);
        coverPhotBnt = findViewById(R.id.selectcover);
        coverPhoto = findViewById(R.id.profCover);
        mProgress = new ProgressDialog(this);

        storageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        current_uid = mAuth.getCurrentUser().getUid();
        mProfileDb = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
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
                    Glide.with(getApplicationContext()).load(proImage).into(profileImage);
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


        coverPhotBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestPermissions permissions = new RequestPermissions(ProfileSingleActivity.this);
                if (permissions.checkPermissions()){
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setMinCropResultSize(512, 512)
                            .setAspectRatio(1, 1)
                            .start(ProfileSingleActivity.this);
                }

            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                imageUri = result.getUri();
                coverPhoto.setImageURI(imageUri);
                uploadPhoto(imageUri);
                mProgress.setMessage("Uploading your photo");
                mProgress.setCancelable(false);
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.show();


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }

    }
    private void uploadPhoto(Uri imageUri) {
        // PHOTO UPLOAD
        File newImageFile = new File(imageUri.getPath());
        try {

            compressedImageFile = new Compressor(ProfileSingleActivity.this)
                    .setMaxHeight(720)
                    .setMaxWidth(720)
                    .setQuality(50)
                    .compressToBitmap(newImageFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
        //writting the file to an outputstream and also reading an array of byte from the outputstream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageData = baos.toByteArray();
        // PHOTO UPLOAD

        UploadTask filePath = storageReference.child("profile_images").child("cover_photo").child(current_uid + ".jpg").putBytes(imageData);
        filePath.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                     String downloadUri = task.getResult().getDownloadUrl().toString();
                     mProfileDb.child("coverphoto").setValue(downloadUri).addOnCompleteListener(new OnCompleteListener<Void>() {
                         @Override
                         public void onComplete(@NonNull Task<Void> task) {
                             if (task.isSuccessful()){

                             }
                             else{
                                 Toast.makeText(ProfileSingleActivity.this,"There was an error uploading your phot, please try again",Toast.LENGTH_SHORT).show();
                             }
                         }
                     });
                }
               mProgress.dismiss();
            }
        });
    }


}
