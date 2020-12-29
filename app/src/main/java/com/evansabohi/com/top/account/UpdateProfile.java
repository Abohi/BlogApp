package com.evansabohi.com.top.account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.evansabohi.com.top.MainActivity;
import com.evansabohi.com.top.R;
import com.evansabohi.com.top.RequestPermissions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class UpdateProfile extends AppCompatActivity {

    private CircleImageView setupImage;
    private Uri mainImageURI = null;
    private DatabaseReference mDatabseUsers;
    private String user_id;

    private boolean isChanged = false;

    private EditText setupName, nativeNameHolder, ageHolder, infoHolder;
    private Button setupBtn;
    private ProgressDialog setupProgress;

    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private Bitmap compressedImageFile;
    private RequestPermissions permissions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_profile);
        //Toolbar Setup
        Toolbar setupToolbar = findViewById(R.id.setupToolbar);
        setSupportActionBar(setupToolbar);
        getSupportActionBar().setTitle("Update Profile");
        // Initializing the views
       setupImage = findViewById(R.id.setup_image);
       setupBtn = findViewById(R.id.submit);
       setupName = findViewById(R.id.name);
       nativeNameHolder = findViewById(R.id.nativename);
       ageHolder = findViewById(R.id.age);
       infoHolder = findViewById(R.id.briefDesc);
       setupProgress = new ProgressDialog(this);
       permissions = new RequestPermissions(UpdateProfile.this);
       // firebase setup
        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        mDatabseUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        //Events Method

        storeUser(setupBtn);
        onImageClick(setupImage);
    }

    private void onImageClick(CircleImageView imgView) {

        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                  if (permissions.checkPermissions()){
                      BringImagePicker();
                  }
                } else {
                    BringImagePicker();
                }
            }
        });
    }

    private void BringImagePicker() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(UpdateProfile.this);
    }

    private void storeUser(Button setupBtn) {

        setupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String user_name = setupName.getText().toString();
                final String native_name = nativeNameHolder.getText().toString();
                final String age = ageHolder.getText().toString();
                final String urInf = infoHolder.getText().toString();


                    setupProgress.setMessage("Updating your details");
                    setupProgress.setCanceledOnTouchOutside(false);
                    setupProgress.setCancelable(false);
                    setupProgress.show();

                    if (isChanged) {

                        //writing file to a stream of byte array and getting the byte array from the written stream

                        File newImageFile = new File(mainImageURI.getPath());
                        try {

                            compressedImageFile = new Compressor(UpdateProfile.this)
                                    .setMaxHeight(520)
                                    .setMaxWidth(520)
                                    .setQuality(50)
                                    .compressToBitmap(newImageFile);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] thumbData = baos.toByteArray();

                        UploadTask image_path = storageReference.child("profile_images").child(user_id + ".jpg").putBytes(thumbData);

                        image_path.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                if (task.isSuccessful()) {
                                    storeFirebase(task, user_name,urInf,age,native_name);

                                } else {

                                    String error = task.getException().getMessage();
                                    Toast.makeText(UpdateProfile.this, "(IMAGE Error) : " + error, Toast.LENGTH_LONG).show();

                                    setupProgress.dismiss();

                                }
                            }
                        });

                    } else {

                        storeFirebase(null, user_name,urInf,age,native_name);

                    }


            }
        });
    }

    private void storeFirebase(Task<UploadTask.TaskSnapshot> task, final String user_name, String urInfo, String age, String nativeName) {
        Uri download_uri;

        if(task != null) {

            download_uri = task.getResult().getDownloadUrl();

        } else {

            download_uri = mainImageURI;
        }
        Map<String, Object> userMap = new HashMap<>();
        if (download_uri!=null){
            userMap.put("image", download_uri.toString());
        }else if(!TextUtils.isEmpty(user_name)){
            userMap.put("name", user_name);
        }
        else if(!TextUtils.isEmpty(nativeName)){
            userMap.put("native_name", nativeName);
        }
        else if(!TextUtils.isEmpty(age)){
            userMap.put("user_age", age);
        }
        else if (!TextUtils.isEmpty(urInfo)){
            userMap.put("user_info", urInfo);
        }
        else if(!TextUtils.isEmpty(user_name) && !TextUtils.isEmpty(urInfo) && !TextUtils.isEmpty(age) && !TextUtils.isEmpty(nativeName) && download_uri!=null){
            userMap.put("name", user_name);
            userMap.put("image", download_uri.toString());
            userMap.put("native_name", nativeName);
            userMap.put("user_age", age);
            userMap.put("user_info", urInfo);
        }

        mDatabseUsers.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    Toast.makeText(UpdateProfile.this, user_name.toUpperCase()+" your details are updated successfully", Toast.LENGTH_SHORT).show();
                }
                else{
                    String error = task.getException().getMessage();
                    Toast.makeText(UpdateProfile.this, user_name.toUpperCase()+" There was an error submitting your details, please try again ", Toast.LENGTH_LONG).show();
                }
                setupProgress.dismiss();
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageURI = result.getUri();
                setupImage.setImageURI(mainImageURI);

                isChanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }

    }

}
