package com.evansabohi.com.top.account_settings;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.evansabohi.com.top.MainActivity;
import com.evansabohi.com.top.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangeEmail extends AppCompatActivity {
  private Button changeEmail,mBack;
    private EditText mChangeEmail;
    private ProgressDialog mProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);

        changeEmail = findViewById(R.id.btn_reset_email);
        mBack = findViewById(R.id.btn_back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChangeEmail.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        mProgress = new ProgressDialog(this);
        mChangeEmail = (EditText)findViewById(R.id.emailChange);
        changeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = mChangeEmail.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplication(), "Enter your new email address", Toast.LENGTH_LONG).show();
                    return;
                }
                mProgress.setMessage("Updating your email...");
                mProgress.show();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                user.updateEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    mProgress.dismiss();
                                    Toast.makeText(ChangeEmail.this, "Email address is updated.", Toast.LENGTH_LONG).show();
                                } else {
                                    mProgress.dismiss();
                                    Toast.makeText(ChangeEmail.this, "Failed to update email!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });


    }




    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getAction()==KeyEvent.ACTION_DOWN){
            switch(keyCode){
                case KeyEvent.KEYCODE_BACK:
                    Intent intent = new Intent(ChangeEmail.this,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    return  true;
            }
        }
        return super.onKeyDown(keyCode,event);
    }

}
