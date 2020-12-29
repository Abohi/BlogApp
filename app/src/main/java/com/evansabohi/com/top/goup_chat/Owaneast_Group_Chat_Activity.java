package com.evansabohi.com.top.goup_chat;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.evansabohi.com.top.GetTimeAgo;
import com.evansabohi.com.top.R;
import com.evansabohi.com.top.user_profile.ProfileModelNames;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

public class Owaneast_Group_Chat_Activity extends AppCompatActivity {
  DatabaseReference mGroupChat,mDatabseUsers;
  FirebaseAuth mAuth;
  String user_id;
  ValueEventListener userValues;
 private RecyclerView mGroupList;
  private EditText mEdit;
  private Button mButton;
 private String name;
 private FirebaseRecyclerAdapter<ChatMessage, GroupChatViewHolder> mChatAdapter;
  private  LinearLayoutManager linearLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group__chat_);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Owan East");

        // Initializing Firebase Dependency
        String owan_east = getIntent().getStringExtra("owaneast");

        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        mGroupChat = FirebaseDatabase.getInstance().getReference().child("Group_Chat").child(owan_east);
        Query groupQuery = mGroupChat.orderByKey();
        mGroupChat.keepSynced(true);
        groupQuery.keepSynced(true);
        mDatabseUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        //Initializing views
        mButton = findViewById(R.id.groupchat_send_btn);
        mEdit = findViewById(R.id.group_edit_box);
        mGroupList = findViewById(R.id.group_chat_list);
        mGroupList.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mGroupList.setLayoutManager(linearLayoutManager);
        userValues = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ProfileModelNames profileModelNames = dataSnapshot.getValue(ProfileModelNames.class);
                name = profileModelNames.getName();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabseUsers.addValueEventListener(userValues);
        onButtonClick(mButton);

        //Setting Firebase Recycler view Ui and getting the datas via the node
        FirebaseRecyclerOptions chatOptions = new FirebaseRecyclerOptions.Builder<ChatMessage>().setQuery(groupQuery, ChatMessage.class).build();
        mChatAdapter = new FirebaseRecyclerAdapter<ChatMessage, GroupChatViewHolder>(chatOptions) {


            @NonNull
            @Override
            public GroupChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_chat_adapter, parent, false);
                return new GroupChatViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final GroupChatViewHolder holder, int position, @NonNull ChatMessage model) {
                holder.setMessage(model.getMessage());
                holder.setNameChat(model.getName());
                holder.setTimeChat(GetTimeAgo.getTimeAgo(model.getTime(),holder.context));
                final String userChatedid = model.getCurrent_uid();
                final String chatId = getRef(position).getKey();
                holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (user_id.equals(userChatedid)){
                            holder.deleteBnt.setVisibility(View.VISIBLE);
                            holder.deleteBnt.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mGroupChat.child(chatId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            holder.deleteBnt.setVisibility(View.GONE);
                                            Toast.makeText(getApplicationContext(),"Message deleted successfully",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }
                        return false;
                    }
                });
            }
        };

        mGroupList.setAdapter(mChatAdapter);
        mChatAdapter.notifyDataSetChanged();
    }

    private void onButtonClick(Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupMessage = mEdit.getText().toString();
                Map<String,Object> groupChatMap = new HashMap<>();
                groupChatMap.put("name",name);
                groupChatMap.put("message",groupMessage);
                groupChatMap.put("time", ServerValue.TIMESTAMP);
                groupChatMap.put("current_uid",user_id);
                mGroupChat.push().setValue(groupChatMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(Owaneast_Group_Chat_Activity.this,"message sent",Toast.LENGTH_SHORT).show();
                    }
                });
                mEdit.setText("");

            }
        });
    }
    public static class GroupChatViewHolder extends RecyclerView.ViewHolder{
        View mView;
        Context context;
        ImageButton deleteBnt;
        public GroupChatViewHolder(View itemView){
            super(itemView);
            mView = itemView;
            context = mView.getContext();
            deleteBnt = mView.findViewById(R.id.delete);

        }
        public void setNameChat(String name){
            TextView profile_name = mView.findViewById(R.id.comment_username);
            profile_name.setText(name);
        }
        public void setMessage(String message){
            TextView messageChat = mView.findViewById(R.id.comment_message);
            messageChat.setText(message);
        }

        public void setTimeChat(String time){
            TextView timeComment = mView.findViewById(R.id.timeComment);
            timeComment.setText(time);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        mChatAdapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        mChatAdapter.stopListening();
        if (userValues!=null){
            mDatabseUsers.removeEventListener(userValues);
        }
    }


}
