package com.evansabohi.com.top.goup_chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.evansabohi.com.top.GetTimeAgo;
import com.evansabohi.com.top.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ABOHI EVANS on 7/1/2016.
 */
public class Group_Chat_Fragment extends Fragment  {
CardView owanBnt,eastBnt;
Context mContext;
private FloatingActionButton floatingActionButton;
private EditText mCreateEdit;
private DatabaseReference mGroupList;
private RecyclerView mRecyclerview;
LinearLayoutManager linearLayoutManager;
private String current_uid;
private FirebaseAuth mAuth;
    private FirebaseRecyclerAdapter<GroupListModel, GroupList_ViewHolder> mGroupListAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
       View rootView= inflater.inflate(R.layout.chat_room_layout, container, false);
       mContext = rootView.getContext();
       mAuth = FirebaseAuth.getInstance();
       mGroupList = FirebaseDatabase.getInstance().getReference().child("Group_List");
        Query groupQuery = mGroupList.orderByKey();
        groupQuery.keepSynced(true);
        mGroupList.keepSynced(true);
       owanBnt = rootView.findViewById(R.id.owanWest);
       eastBnt = rootView.findViewById(R.id.owanEast);
       floatingActionButton = rootView.findViewById(R.id.fab);
       mCreateEdit = rootView.findViewById(R.id.groupCreate);

       mRecyclerview = rootView.findViewById(R.id.group_chat_list);
       current_uid = mAuth.getCurrentUser().getUid();
        linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerview.setLayoutManager(linearLayoutManager);

       owanBnt.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(mContext,Owan_Group_Chat_Activity.class);
               intent.putExtra("owanwest","Owan_west");
               startActivity(intent);
           }
       });
        eastBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,Owaneast_Group_Chat_Activity.class);
                intent.putExtra("owaneast","Owan_east");
                startActivity(intent);
            }
        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String groupList = mCreateEdit.getText().toString();
                if (groupList.contains(" ")){
                    Toast.makeText(getContext(),"Empty String not allowed",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (groupList.length()<3){
                    Toast.makeText(getContext(),"Group name must be at least 4 characters long",Toast.LENGTH_LONG).show();
                    return;
                }
                if (!TextUtils.isEmpty(groupList)){
                    Map<String,Object> groupMap = new HashMap<>();
                    groupMap.put("name_of_group",groupList);
                    groupMap.put("time", ServerValue.TIMESTAMP);
                    groupMap.put("current_uid",current_uid);
                    mGroupList.push().setValue(groupMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(mContext,"Group: "+groupList+" Created Successfully",Toast.LENGTH_LONG).show();
                            }
                            mCreateEdit.setText("");
                        }
                    });
                }else {
                    Toast.makeText(getContext(),"Empty String not allowed",Toast.LENGTH_LONG).show();
                }


            }
        });
        //Setting Firebase Recycler view Ui and getting the datas via the node
        FirebaseRecyclerOptions groupOptions = new FirebaseRecyclerOptions.Builder<GroupListModel>().setQuery(groupQuery, GroupListModel.class).build();
        mGroupListAdapter = new FirebaseRecyclerAdapter<GroupListModel, GroupList_ViewHolder>(groupOptions) {


            @NonNull
            @Override
            public GroupList_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.goup_list_card, parent, false);
                return new GroupList_ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull GroupList_ViewHolder holder, int position, @NonNull final GroupListModel model) {
                  holder.setName(""+model.getName_of_group().charAt(0)+model.getName_of_group().charAt(1));
                  holder.setGroupName(model.getName_of_group());
                  String creatorId = model.getCurrent_uid();
                 final String groudNameId = getRef(position).getKey();
                  holder.mView.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          Intent intent = new Intent(mContext,Group_Chat_Activity.class);
                          intent.putExtra("nameofgroup",model.getName_of_group());
                          startActivity(intent);
                      }
                  });
                  if (creatorId.equals(current_uid)){
                      holder.deleteBnt.setVisibility(View.VISIBLE);
                      holder.deleteBnt.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {

                              mGroupList.child(groudNameId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                  @Override
                                  public void onComplete(@NonNull Task<Void> task) {
                                      DatabaseReference mGroup =  FirebaseDatabase.getInstance().getReference().child("Group_Chat").child(model.getName_of_group());
                                      mGroup.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                          @Override
                                          public void onComplete(@NonNull Task<Void> task) {
                                              Toast.makeText(mContext,"Chat room deleted successfully",Toast.LENGTH_SHORT).show();
                                          }
                                      });
                                  }
                              });
                          }
                      });
                  }
                  holder.timeCreated.setText("Group created: "+ GetTimeAgo.getTimeAgo(model.getTime(),mContext));
            }
        };
        mRecyclerview.setAdapter(mGroupListAdapter);
        mGroupListAdapter.notifyDataSetChanged();
        return rootView;
    }


    public static class GroupList_ViewHolder extends RecyclerView.ViewHolder{
        View mView;
        Context context;
        ImageButton deleteBnt;
        TextView timeCreated;
        public GroupList_ViewHolder(View itemView){
            super(itemView);
            mView = itemView;
            context = mView.getContext();
            deleteBnt = mView.findViewById(R.id.deletBnt);
            timeCreated = mView.findViewById(R.id.timecreated);

        }
        public void setName(String name){
            TextView group_name_icon = mView.findViewById(R.id.icon_East);
            group_name_icon.setText(name);
        }
        public void setGroupName(String name){
            TextView group_name = mView.findViewById(R.id.name_East);
           group_name.setText(name);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        mGroupListAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGroupListAdapter.stopListening();
    }





}
