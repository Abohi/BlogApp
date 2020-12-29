package com.evansabohi.com.top.status;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.amulyakhare.textdrawable.TextDrawable;
import com.evansabohi.com.top.GetTimeAgo;
import com.evansabohi.com.top.R;
import com.evansabohi.com.top.mainpage_fragments.ProgressActivity;
import com.evansabohi.com.top.status.StatusListModel;
import com.evansabohi.com.top.user_profile.ProfileModelNames;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by ABOHI EVANS on 7/1/2016.
 */
public class View_Status extends Fragment  {
    private RecyclerView mStatusRecyclerView;
    DatabaseReference user_status,mUserDb;
    FirebaseAuth mAuth;
    LinearLayoutManager mStatusLayout;
    private Context mcontext;
    private FirebaseRecyclerAdapter<StatusListModel, StatusViewHolder> mStatusAdapter;
    private ValueEventListener userValues;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView= inflater.inflate(R.layout.view_status, container, false);
        mcontext = getContext();
        //initializing firebase database
        mAuth = FirebaseAuth.getInstance();
       user_status = FirebaseDatabase.getInstance().getReference().child("user_status");
        mUserDb = FirebaseDatabase.getInstance().getReference().child("Users");
        Query user_statusQuery = user_status.orderByKey();
        user_status.keepSynced(true);

        //Recycler View
       mStatusRecyclerView =rootView.findViewById(R.id.status_list);
        mStatusLayout = new LinearLayoutManager(mcontext);
        mStatusRecyclerView.setLayoutManager(mStatusLayout);

//Setting Firebase Recycler view Ui and getting the datas via the node
        FirebaseRecyclerOptions userOptions = new FirebaseRecyclerOptions.Builder<StatusListModel>().setQuery(user_statusQuery, StatusListModel.class).build();
        mStatusAdapter = new FirebaseRecyclerAdapter<StatusListModel, StatusViewHolder>(userOptions) {


            @NonNull
            @Override
            public StatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.status_row , parent, false);
                return new StatusViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final StatusViewHolder holder, int position, @NonNull final StatusListModel model) {
                String  uid= model.getUid();
                 userValues = new ValueEventListener() {
                     @Override
                     public void onDataChange(DataSnapshot dataSnapshot) {
                         if (dataSnapshot.exists()){
                             ProfileModelNames modelNames = dataSnapshot.getValue(ProfileModelNames.class);
                             final String  name = modelNames.getName();
                             final String  native_name = modelNames.getNative_name();
                             final String profImage = modelNames.getImage();
                             holder.setName(name+" "+native_name);
                             holder.setImage(model.getStatusmessages().get(0));
                             holder.setTimePosted("Status posted "+GetTimeAgo.getTimeAgo(model.getTimestamp(),mcontext));
                             final ArrayList<String> imageCollection = model.getStatusmessages();
                             holder.mView.setOnClickListener(new View.OnClickListener() {
                                 @Override
                                 public void onClick(View v) {
                                     String username = name+" "+native_name;
                                     Intent intent = new Intent(mcontext,ProgressActivity.class);
                                     intent.putStringArrayListExtra("imagecollection",imageCollection);
                                     intent.putExtra("username",username);
                                     intent.putExtra("image",profImage);
                                     startActivity(intent);
                                 }
                             });
                         }
                         
                     }

                     @Override
                     public void onCancelled(DatabaseError databaseError) {

                     }
                 };
                 mUserDb.child(uid).addValueEventListener(userValues);

            }
        };
        mStatusRecyclerView.setAdapter(mStatusAdapter);
        mStatusAdapter.notifyDataSetChanged();


        return rootView;
    }
    public static class StatusViewHolder extends RecyclerView.ViewHolder{
        View mView;
        Context context;
        TextDrawable mTextDrawable;
        public StatusViewHolder(View itemView){
            super(itemView);
            mView = itemView;
            context = mView.getContext();

        }
        public void setImage(String image){

            ImageView profile_image =  mView.findViewById(R.id.statusimage);
                mTextDrawable= TextDrawable.builder().beginConfig()
                        .textColor(Color.WHITE)
                        .fontSize(20)
                        .bold()
                        .toUpperCase()
                        .withBorder(4)
                        .endConfig().buildRoundRect(image, Color.DKGRAY,30);
                profile_image.setImageDrawable(mTextDrawable);


        }
        public void setName(String name){
            TextView mTextview = mView.findViewById(R.id.statusname);
            mTextview.setText(name);
        }
        public void setTimePosted(String timePosted){
            TextView mTextview = mView.findViewById(R.id.timeposted);
            mTextview.setText(timePosted);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        mStatusAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mStatusAdapter.stopListening();
        if (userValues!=null){
            mUserDb.removeEventListener(userValues);
        }


    }

}
