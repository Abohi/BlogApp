package com.evansabohi.com.top.new_post;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.evansabohi.com.top.GetTimeAgo;
import com.evansabohi.com.top.MainActivity;
import com.evansabohi.com.top.R;
import com.evansabohi.com.top.RequestPermissions;
import com.evansabohi.com.top.user_profile.UserProfile_View;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class BlogPostSearch extends AppCompatActivity {

 private EditText mSearch;
 private RecyclerView mSearchResult;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mPostDatabase,mDatabaseComment;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter<BlogPost, BlogViewHolder> mBlogAdapter;
    private DatabaseReference mLikes,userDatabase;
    String current_uid;
    ValueEventListener getLikes,getCount,getUserValue,getCommentCount;
    private Query commentQuery;
    private boolean mProcessLike = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);

        //initializing firebase database
        firebaseAuth = FirebaseAuth.getInstance();
        current_uid = firebaseAuth.getCurrentUser().getUid();
        mPostDatabase = FirebaseDatabase.getInstance().getReference().child("Posts");
        mLikes = FirebaseDatabase.getInstance().getReference().child("Likes");
        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        userDatabase.keepSynced(true);
        mPostDatabase.keepSynced(true);



        //Recycler View and Views Initialization
        mSearch = findViewById(R.id.search_field);
        mSearchResult = findViewById(R.id.result_list);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mSearchResult.setLayoutManager(linearLayoutManager);

        mSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (!charSequence.toString().isEmpty()){
                    firebaseGlobalSearch(charSequence.toString());
                }

            }

            @Override
            public void afterTextChanged(Editable searchText) {

                if (!searchText.toString().isEmpty()){
                    firebaseGlobalSearch(searchText.toString());
                }
            }
        });



    }

    private void firebaseGlobalSearch(String searchText) {

        Query firebaseSearchQuery = mPostDatabase.orderByChild("desc").startAt(searchText).endAt(searchText + "\uf8ff");
        FirebaseRecyclerOptions blogOptions = new FirebaseRecyclerOptions.Builder<BlogPost>().setQuery(firebaseSearchQuery, BlogPost.class).build();
        mBlogAdapter = new FirebaseRecyclerAdapter<BlogPost, BlogViewHolder>(blogOptions) {
            @NonNull
            @Override
            public BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.blog_list_item, parent, false);
                return new BlogViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final BlogViewHolder holder, int position, @NonNull BlogPost model) {
                final String userID = model.getCurrent_uid();
                final String blogPostId = getRef(position).getKey();
                mDatabaseComment = FirebaseDatabase.getInstance().getReference().child("Comments");
                commentQuery = mDatabaseComment.orderByChild("post_id").equalTo(blogPostId);
                getUserValue = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            String   userName = dataSnapshot.child("name").getValue().toString();
                            String   userImage = dataSnapshot.child("image").getValue().toString();
                            holder.setProfileImage(userImage);
                            holder.setName(userName);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
                userDatabase.child(userID).addValueEventListener(getUserValue);
                if (model.getDesc()!=null && model.getImage_url()!=null){
                    holder.setDesc(model.getDesc());
                    holder.setTime(model.getTimestamp());
                    holder.setImage(model.getImage_url());
                }

                //Get Comment Count
                getCommentCount = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Count the number of like the post have
                        if (dataSnapshot.exists()) {

                            int commentCount = (int) dataSnapshot.getChildrenCount();
                            holder.commentCount.setText(commentCount+" Comments");

                        }
                        else{
                            holder.commentCount.setText("0 Comments");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
                commentQuery.addValueEventListener(getCommentCount);
                //Get Likes Count
                getCount = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            int count = (int)dataSnapshot.getChildrenCount();

                            holder.updateLikesCount(count);
                        }else {
                            holder.updateLikesCount(0);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
                mLikes.child(blogPostId).addValueEventListener(getCount);

                //Get Likes
                try{
                    getLikes = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                if (dataSnapshot.child(blogPostId).hasChild(firebaseAuth.getCurrentUser().getUid())){

                                    //If Liked, Change the mLike button dark red
                                    holder.blogLike.setImageDrawable(ContextCompat.getDrawable(BlogPostSearch.this,R.mipmap.action_like_accent));

                                }else {

                                    //If Not liked, Change the mLike button gray
                                    holder.blogLike.setImageDrawable(ContextCompat.getDrawable(BlogPostSearch.this,R.mipmap.action_like_gray));
                                }


                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };
                    mLikes.addValueEventListener(getLikes);
                }catch (NullPointerException e){
                    Toast.makeText(BlogPostSearch.this,"Logged out successfully",Toast.LENGTH_SHORT).show();
                }

                //Likes Feature
                holder.blogLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mProcessLike = true;
                        ValueEventListener setLikes = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    if (mProcessLike){
                                        if (dataSnapshot.child(blogPostId).hasChild(firebaseAuth.getCurrentUser().getUid())) {

                                            //unliking the post
                                            mLikes.child(blogPostId).child(firebaseAuth.getCurrentUser().getUid()).removeValue();
                                            mProcessLike = false;

                                        } else {

                                            //adding like to the post
                                            mLikes.child(blogPostId).child(firebaseAuth.getCurrentUser().getUid()).setValue("1");
                                            mProcessLike = false;

                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        };
                        mLikes.addValueEventListener(setLikes);
                    }
                });
                holder.profile_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent singlePostIntent = new Intent(BlogPostSearch.this, UserProfile_View.class);
                        singlePostIntent.putExtra("id", userID);
                        singlePostIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(singlePostIntent);

                    }
                });
                holder.profile_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent singlePostIntent = new Intent(BlogPostSearch.this, UserProfile_View.class);
                        singlePostIntent.putExtra("id", userID);
                        singlePostIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(singlePostIntent);
                    }
                });
                holder.blog_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String userid = userID;
                        //View the post in details
                        Intent singlePostIntent = new Intent(BlogPostSearch.this, SinglePostScreen.class);
                        singlePostIntent.putExtra("post_id", blogPostId);
                        singlePostIntent.putExtra("user_id", userID);
                        singlePostIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(singlePostIntent);
                    }
                });
                holder.optionMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popupMenu = new PopupMenu(BlogPostSearch.this,holder.optionMenu);
                        popupMenu.inflate(R.menu.recyceroption);
                        Menu popMenu = popupMenu.getMenu();
                        if (current_uid.equals(userID)){
                            popMenu.findItem(R.id.deletePost).setEnabled(true);
                        }else {
                            popMenu.findItem(R.id.deletePost).setEnabled(false);
                        }
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {

                                switch (item.getItemId()){
                                    case R.id.savetogallery:
                                        if (holder.blog_image!=null){
                                            RequestPermissions permissions = new RequestPermissions(BlogPostSearch.this);
                                            if (permissions.checkPermissions()){
                                                Bitmap imageMap= ((BitmapDrawable)holder.blog_image.getDrawable()).getBitmap();
                                                saveImageToExternalStorage(imageMap);
                                            }
                                        }
                                        break;
                                    case R.id.deletePost:
                                        mPostDatabase.child(blogPostId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Toast.makeText(BlogPostSearch.this, "Post deleted successfully!", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                        break;

                                }
                                return false;
                            }

                        });
                        popupMenu.show();
                    }
                });
            }




        };
        mSearchResult.setAdapter(mBlogAdapter);
        mBlogAdapter.notifyDataSetChanged();

        mBlogAdapter.startListening();
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder{
        View mView;
        Context context;
        ImageView blogLike,blog_image;
        ImageView optionMenu;
        TextView commentCount;
        CircleImageView profile_image;
        TextView profile_name;
        public BlogViewHolder(View itemView){
            super(itemView);
            mView = itemView;
            context = mView.getContext();
            blogLike = mView.findViewById(R.id.blog_like_btn);
            blog_image =  mView.findViewById(R.id.blog_image);
            optionMenu = mView.findViewById(R.id.option);
            commentCount = mView.findViewById(R.id.commentCount);
            profile_image =  mView.findViewById(R.id.blog_user_image);
            profile_name = mView.findViewById(R.id.blog_user_name);

        }
        public void setName(String name){
            profile_name.setText(name);
        }
        public void setDesc(String desc){
            TextView blog_desc = mView.findViewById(R.id.blog_desc);
            blog_desc.setText(desc);
        }
        public void setTime(long time){
            TextView blog_date = mView.findViewById(R.id.blog_date);
            blog_date.setText( GetTimeAgo.getTimeAgo(time,mView.getContext()));
        }
        public void setImage(final String image){


            Picasso.with(context).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(blog_image, new Callback() {
                @Override
                public void onSuccess() {


                }

                @Override
                public void onError() {
                    Picasso.with(context).load(image).into(blog_image);
                }
            });
        }
        public void setProfileImage(final String image){
            Picasso.with(context).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(profile_image, new Callback() {
                @Override
                public void onSuccess() {


                }

                @Override
                public void onError() {
                    Picasso.with(context).load(image).into(blog_image);
                }
            });
        }
        public void updateLikesCount(int count){

            TextView blogLikeCount = mView.findViewById(R.id.blog_like_count);
            blogLikeCount.setText(count + " Likes");

        }
    }
    private void saveImageToExternalStorage(Bitmap finalBitmap) {
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        File myDir = new File(root + "/toplite_images");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "TopApp-" + n + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }


        // Tell the media scanner about the new file so that it is
        // immediately available to the user.
        MediaScannerConnection.scanFile(BlogPostSearch.this, new String[]{file.toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Toast.makeText(BlogPostSearch.this,"Image saved to gallery",Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public void onStop() {
        super.onStop();
        if (getLikes!=null && getCount!=null ){
            mLikes.removeEventListener(getLikes);
            mLikes.removeEventListener(getCount);
        }
        if (getUserValue!=null){
            userDatabase.removeEventListener(getUserValue);
        }
        if (getCommentCount!=null){
            commentQuery.removeEventListener(getCommentCount);
        }

        mBlogAdapter.stopListening();
    }


}
