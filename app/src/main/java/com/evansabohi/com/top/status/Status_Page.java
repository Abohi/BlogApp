package com.evansabohi.com.top.status;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.evansabohi.com.top.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ABOHI EVANS on 7/1/2016.
 */
public class Status_Page extends Fragment  {
    private Button newPostBtn,addStatus;

    private FirebaseAuth firebaseAuth;
    private EditText statusTxt;
    private ArrayList<String> statusCollection;
    private StatusRecyclerAdapter mStatusRecyclerviewAdapter;
    private RecyclerView statusList;
    private String current_user_id;
    private DatabaseReference mUserStatus;
    private Context mcontext;
    private boolean isReached = false;
    private ProgressDialog mProgressDialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView= inflater.inflate(R.layout.status_page, container, false);
        mcontext = rootView.getContext();

        //initializing firebase dependency
        firebaseAuth = FirebaseAuth.getInstance();
        current_user_id = firebaseAuth.getCurrentUser().getUid();
        mUserStatus = FirebaseDatabase.getInstance().getReference().child("user_status").child(current_user_id);

        statusList = rootView.findViewById(R.id.addstatuslist);
        newPostBtn = rootView.findViewById(R.id.post_status);
        addStatus= rootView.findViewById(R.id.addstatus);
        statusTxt = rootView.findViewById(R.id.statusText);


        mProgressDialog = new ProgressDialog(rootView.getContext());
        statusCollection = new ArrayList<>();

        mStatusRecyclerviewAdapter = new StatusRecyclerAdapter(statusCollection);
        LinearLayoutManager horizontalLinear = new LinearLayoutManager(mcontext,LinearLayoutManager.HORIZONTAL,false);
        statusList.setLayoutManager(horizontalLinear);
        statusList.setAdapter(mStatusRecyclerviewAdapter);
        statusList.setHasFixedSize(true);

        postStatus(newPostBtn);
        addAStatus(addStatus);
        return rootView;
    }


    private void addAStatus(Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String statusText = statusTxt.getText().toString();
                if(!TextUtils.isEmpty(statusText)){
                    statusCollection.add(statusText);
                    mStatusRecyclerviewAdapter.notifyDataSetChanged();
                    Toast.makeText(mcontext,"Status Added: "+statusText,Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(mcontext,"Please kindly provide at text for status via the input field",Toast.LENGTH_SHORT).show();
                }
                statusTxt.setText("");

            }
        });
    }

    private void postStatus(Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!statusCollection.isEmpty()){
                    mProgressDialog.setMessage("Posting your status");
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.show();
                    HashMap<String,Object> statusMap = new HashMap<>();
                    statusMap.put("statusmessages",statusCollection);
                    statusMap.put("uid",current_user_id);
                    statusMap.put("timestamp", ServerValue.TIMESTAMP);
                    mUserStatus.setValue(statusMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            statusCollection.clear();
                            mStatusRecyclerviewAdapter.notifyDataSetChanged();
                            mProgressDialog.dismiss();
                        }
                    });

                }

            }
        });
    }
    private void filterEdit() {
        InputFilter filter = new InputFilter() {
            boolean canEnterSpace = false;
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (statusTxt.getText().toString().equals("")){
                    canEnterSpace = false;
                }
                StringBuilder builder = new StringBuilder();
                for (int i = start;i<end;i++){
                    char currentChar = source.charAt(i);
                    if(Character.isLetterOrDigit(currentChar)){
                        builder.append(currentChar);
                        canEnterSpace = true;
                    }
                    if (Character.isWhitespace(currentChar)&& canEnterSpace){
                        builder.append(currentChar);
                    }
                }
                return  builder.toString();
            }
        };
        statusTxt.setFilters(new InputFilter[]{filter});
    }



}
