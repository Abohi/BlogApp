package com.evansabohi.com.top.user_profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.evansabohi.com.top.GetTimeAgo;
import com.evansabohi.com.top.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


public class ProfileActivity extends AppCompatActivity {

    private RecyclerView mProfileRecyclerList;
    DatabaseReference mdatabase;
    FirebaseAuth mAuth;
    LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter<ProfileModelNames, UserViewHolder> mUserAdapter;
    private Toolbar mProfileToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mProfileToolbar = findViewById(R.id.profileactivitytoolbar);
        setSupportActionBar(mProfileToolbar);
        getSupportActionBar().setTitle("List Of Top Users");
        //initializing firebase database
        mAuth = FirebaseAuth.getInstance();
        DatabaseReference user_profiles = FirebaseDatabase.getInstance().getReference().child("Users");
        Query user_profilesQuery = user_profiles.orderByKey();
        user_profiles.keepSynced(true);

        //Recycler View
        mProfileRecyclerList =findViewById(R.id.profile_list);
        mProfileRecyclerList.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mProfileRecyclerList.setLayoutManager(linearLayoutManager);

//Setting Firebase Recycler view Ui and getting the datas via the node
        FirebaseRecyclerOptions userOptions = new FirebaseRecyclerOptions.Builder<ProfileModelNames>().setQuery(user_profilesQuery, ProfileModelNames.class).build();
        mUserAdapter = new FirebaseRecyclerAdapter<ProfileModelNames, UserViewHolder>(userOptions) {
            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.profilerow, parent, false);
                return new UserViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull final ProfileModelNames model) {
                holder.setName(model.getName());
                holder.setNativeName(model.getNative_name());
                holder.setImage(model.getImage());
                holder.setTimeJoined("Became a memeber "+GetTimeAgo.getTimeAgo(model.getTime(),ProfileActivity.this));

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String userId = model.getUid();
                        Intent intent = new Intent(getApplicationContext(),UserProfile_View.class);
                        intent.putExtra("id", userId);
                        startActivity(intent);
                    }
                });
            }


        };

        mProfileRecyclerList.setAdapter(mUserAdapter);
        mUserAdapter.notifyDataSetChanged();




    }
    public static class UserViewHolder extends RecyclerView.ViewHolder{
        View mView;
        Context context;
        public UserViewHolder(View itemView){
            super(itemView);
            mView = itemView;
            context = mView.getContext();

        }
        public void setName(String name){
            TextView profile_name = mView.findViewById(R.id.profileName);
            profile_name.setText(name);
        }
        public void setNativeName(String nativeName){
            TextView profile_native = mView.findViewById(R.id.profileNative);
            profile_native.setText(nativeName);
        }
        public void setTimeJoined(String nativeName){
            TextView profile_time = mView.findViewById(R.id.timejoined);
            profile_time.setText(nativeName);
        }
        public void setImage(final String image){
            final ImageView profile_image =  mView.findViewById(R.id.profileImage);
            Glide.with(context).load(image).into(profile_image);
            Picasso.with(context).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(profile_image, new Callback() {
                @Override
                public void onSuccess() {


                }

                @Override
                public void onError() {
                    Picasso.with(context).load(image).into(profile_image);
                }
            });
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        mUserAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mUserAdapter.stopListening();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        return super.onKeyDown(keyCode,event);
    }

}
