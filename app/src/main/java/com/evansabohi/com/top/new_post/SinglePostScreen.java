package com.evansabohi.com.top.new_post;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.evansabohi.com.top.GetTimeAgo;
import com.evansabohi.com.top.R;
import com.evansabohi.com.top.user_profile.ProfileModelNames;
import com.evansabohi.com.top.user_profile.UserProfile_View;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SinglePostScreen extends AppCompatActivity {

String post_key=null;
String user_id=null;
DatabaseReference mUserDb,mPostDatabase,mCommentDb,  mDatabaseLike;
private Query mQueryCurrentComment;
private FirebaseAuth mAuth;
private RecyclerView commentList;
private Button sendBnt;
private EditText commentEdit;
private ProgressDialog mProgressDialog;
private ValueEventListener commentValuelistener,postValues,posterUserValue;
private FirebaseRecyclerAdapter<CommentModel, CommentViewHolder> mCommentAdapter;
private Context mContext;

    TextView postertime,singlePostDesc,singlePostName;
    CircleImageView singlePostProfileImage;
    PhotoView singlePostImage;
    ImageButton singleRemoveBnt;
    private String current_user;
    ValueEventListener userPostValues,userValues,changeLikeBnt,getFavouriteCount,getCommentCount,setLike;
    String user_idOfPost;
    private ImageView singleFavBnt,singleCommentBnt;
    private TextView singleFavCount,singeCommentCount;
    private boolean mProcessLike = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post);

        // Getting the extras
        post_key = getIntent().getStringExtra("post_id");
        user_id = getIntent().getStringExtra("user_id");

        //Getting firebase Dependency
        mAuth = FirebaseAuth.getInstance();
        current_user = mAuth.getCurrentUser().getUid();
        mPostDatabase = FirebaseDatabase.getInstance().getReference().child("Posts");
        mUserDb = FirebaseDatabase.getInstance().getReference().child("Users");
        mCommentDb = FirebaseDatabase.getInstance().getReference().child("Comments");
        mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
        mQueryCurrentComment = mCommentDb.orderByChild("post_id").equalTo(post_key);
        mContext = getBaseContext();

        //Initializing Views
        singleCommentBnt = findViewById(R.id.single_comment_btn);
        singleFavBnt = findViewById(R.id.single_fav_btn);
        singeCommentCount = findViewById(R.id.single_comment_count);
        commentList = findViewById(R.id.cmItems);
        sendBnt = findViewById(R.id.comment_send_btn);
        commentEdit = findViewById(R.id.comment_edit_box);
        mProgressDialog = new ProgressDialog(this);
        singleRemoveBnt = findViewById(R.id.singleRemoveBtn);
        singleFavCount = findViewById(R.id.single_fav_count);
        postertime = findViewById(R.id.singlePostTime);
        singlePostDesc = findViewById(R.id.singlePostDescp);
        singlePostName = findViewById(R.id.singlePostUsername);
        singlePostProfileImage = findViewById(R.id.singleImage);
        singlePostImage = findViewById(R.id.singlePostImage);
         singlePostProfileImage.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent singlePostIntent = new Intent(mContext, UserProfile_View.class);
                 singlePostIntent.putExtra("id", user_id);
                 singlePostIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                 startActivity(singlePostIntent);
             }
         });
         singlePostName.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent singlePostIntent = new Intent(mContext, UserProfile_View.class);
                 singlePostIntent.putExtra("id", user_id);
                 singlePostIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                 startActivity(singlePostIntent);
             }
         });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        commentList.setHasFixedSize(true);
        commentList.setLayoutManager(layoutManager);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        userPostValues = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String posterPostDesc="";
                String posterPostImage="";
                String posterPostThumb="";
                long posterPostTime = 0;
                if(dataSnapshot.exists()){
                    BlogPost blogPost = dataSnapshot.getValue(BlogPost.class);
                    posterPostDesc = blogPost.getDesc();
                    posterPostImage = blogPost.getImage_url();
                    posterPostThumb = blogPost.getImage_thumb();
                     posterPostTime = blogPost.getTimestamp();
                    user_idOfPost = blogPost.getCurrent_uid();
                }

                userValues = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        ProfileModelNames profileModelNames = dataSnapshot.getValue(ProfileModelNames.class);
                        String name = profileModelNames.getName();
                        String image = profileModelNames.getImage();
                        Glide.with(mContext).load(image).into(singlePostProfileImage);
                        singlePostName.setText(name);
                    }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
                mUserDb.child(user_idOfPost).addValueEventListener(userValues);
                postertime.setText(GetTimeAgo.getTimeAgo(posterPostTime,mContext));
                Glide.with(mContext).load(posterPostImage).thumbnail(
                        Glide.with(mContext).load(posterPostThumb)
                ).into(singlePostImage);
                singlePostDesc.setText(posterPostDesc);
                if (current_user.equals(user_idOfPost)){
                    singleRemoveBnt.setVisibility(View.VISIBLE);
                }
                mProgressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mPostDatabase.child(post_key).addValueEventListener(userPostValues);
        changeLikeBnt = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(post_key).hasChild(current_user)){
                    //If Liked, Change the mLike button blue

                    singleFavBnt.setImageDrawable(ContextCompat.getDrawable(SinglePostScreen.this,R.mipmap.action_like_accent));

                }else {

                    //If Unlike, Change the mLike button blue
                    singleFavBnt.setImageDrawable(ContextCompat.getDrawable(SinglePostScreen.this,R.mipmap.action_like_gray));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabaseLike.addValueEventListener(changeLikeBnt);
        //Get Favourites
        getFavouriteCount = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    int favCount = (int)dataSnapshot.getChildrenCount();
                    singleFavCount.setText(""+favCount);
                }else{
                    singleFavCount.setText(""+0);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabaseLike.child(post_key).addValueEventListener(getFavouriteCount);
        //Get comment Count
        getCommentCount = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    int commentCount = (int) dataSnapshot.getChildrenCount();
                    singeCommentCount.setText("" + commentCount);
                }
                else{
                    singeCommentCount.setText("" + 0);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mQueryCurrentComment.addValueEventListener(getCommentCount);
        //Favorite Feature
        singleFavBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 mProcessLike = true;
                 setLike = new ValueEventListener() {
                     @Override
                     public void onDataChange(DataSnapshot dataSnapshot) {
                         if (mProcessLike) {
                             if (dataSnapshot.child(post_key).hasChild(current_user)) {
                                 //unliking the post
                                 mDatabaseLike.child(post_key).child(current_user).removeValue();
                                 mProcessLike = false;
                             } else {
                                 //adding like to the post
                                 mDatabaseLike.child(post_key).child(current_user).setValue("1");
                                 mProcessLike = false;

                             }
                         }
                     }

                     @Override
                     public void onCancelled(DatabaseError databaseError) {

                     }
                 };
                 mDatabaseLike.addValueEventListener(setLike);
            }
        });
        // commenting focus
        singleCommentBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentEdit.requestFocus();
            }
        });
        //remove post
        singleRemoveBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPostDatabase.child(post_key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            finish();
                            Toast.makeText(SinglePostScreen.this, "Post deleted successfully!", Toast.LENGTH_LONG).show();

                        }
                    }
                });
            }
        });
        //commenting on post
        sendBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Comment code starts from here
                 String strComment = commentEdit.getText().toString();
                 if (!TextUtils.isEmpty(strComment)){
                     mProgressDialog.setMessage("Commenting on post...");
                     mProgressDialog.setCanceledOnTouchOutside(false);
                     mProgressDialog.show();

                      DatabaseReference newComment = mCommentDb.push();

                      String current_uid = mAuth.getCurrentUser().getUid();
                      Map<String,Object> commentMap = new HashMap<>();
                      commentMap.put("comment",strComment);
                      commentMap.put("datetime",ServerValue.TIMESTAMP);
                      commentMap.put("uid",current_uid);
                      commentMap.put("post_id",post_key);
                      newComment.setValue(commentMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                          @Override
                          public void onComplete(@NonNull Task<Void> task) {
                              if (task.isSuccessful()){
                                 commentEdit.setText("");

                              }else {
                                  Toast.makeText(SinglePostScreen.this, "Unable to post Comment", Toast.LENGTH_LONG).show();

                              }
                              mProgressDialog.dismiss();
                          }
                      });

                 }else {
                     mProgressDialog.dismiss();
                     Toast.makeText(getApplicationContext(), "Please write short comment", Toast.LENGTH_LONG).show();
                 }
            }
        });
        //Setting Firebase Recycler view Ui and getting the datas via the node
        FirebaseRecyclerOptions commentOptions = new FirebaseRecyclerOptions.Builder<CommentModel>().setQuery(mQueryCurrentComment, CommentModel.class).build();
        mCommentAdapter = new FirebaseRecyclerAdapter<CommentModel, CommentViewHolder>(commentOptions) {


            @NonNull
            @Override
            public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.comment_list_item, parent, false);
                return new CommentViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final CommentViewHolder holder, int position, @NonNull final CommentModel model) {
                String uid = model.getUid();
                final String comment_key = getRef(position).getKey();
                final String userID = model.getUid();
                if (current_user.equals(userID)){

                    holder.popUpmenu.setVisibility(View.VISIBLE);

                }else {

                    holder.popUpmenu.setVisibility(View.INVISIBLE);

                }
                commentValuelistener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ProfileModelNames profileModelNames = dataSnapshot.getValue(ProfileModelNames.class);
                        String name = profileModelNames.getName();
                        String image = profileModelNames.getImage();
                        holder.setName(name);
                        holder.setCommentProfileImage(image);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
                mUserDb.child(uid).addValueEventListener(commentValuelistener);
                holder.setTime(GetTimeAgo.getTimeAgo(model.getDatetime(),mContext));
                holder.setComment(model.getComment());

                holder.popUpmenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popupMenu = new PopupMenu(mContext,holder.popUpmenu);
                        popupMenu.inflate(R.menu.commentmenu);
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {

                                switch (item.getItemId()){

                                    case R.id.deletePost:
                                        //Removing Comment
                                        mCommentDb.child(comment_key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(SinglePostScreen.this,"Comment deleted successfully",Toast.LENGTH_SHORT).show();
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
        commentList.setAdapter(mCommentAdapter);
        mCommentAdapter.notifyDataSetChanged();
    }
    public static class CommentViewHolder extends RecyclerView.ViewHolder{
        View mView;
        Context context;
        ImageView popUpmenu;
        public CommentViewHolder(View itemView){
            super(itemView);
            mView = itemView;
            context = mView.getContext();
            popUpmenu = mView.findViewById(R.id.menuPopup);

        }
        public void setName(String name){
            TextView profile_name = mView.findViewById(R.id.comment_username);
            profile_name.setText(name);
        }
        public void setTime(String time){
            TextView timeComment = mView.findViewById(R.id.timeComment);
            timeComment.setText(time);
        }
        public void setComment(String comment){
            TextView mComment = mView.findViewById(R.id.comment_message);
            mComment.setText(comment);
        }
        public void setCommentProfileImage(String image){
            ImageView commentProfile_image =  mView.findViewById(R.id.comment_image);
            Glide.with(context).load(image).into(commentProfile_image);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        mCommentAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mCommentAdapter.stopListening();
    }



}
