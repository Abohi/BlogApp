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

public class ChangedPassword extends AppCompatActivity {
 private ProgressDialog mProgress;
    private EditText mChangpass;
    private Button mBack,resetButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changed_password);

        mProgress = new ProgressDialog(this);
        resetButton =findViewById(R.id.btn_reset_password);
        mChangpass =findViewById(R.id.changePas);
        mBack=findViewById(R.id.btn_back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChangedPassword.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
     resetButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             String forgotPass = mChangpass.getText().toString().trim();
             if (TextUtils.isEmpty(forgotPass)) {
                 Toast.makeText(getApplication(), "Enter your new Password", Toast.LENGTH_LONG).show();
                 mChangpass.requestFocus();
                 return;
             }
             mProgress.setMessage("Updating your password");
             mProgress.show();
             FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
             user.updatePassword(forgotPass)
                     .addOnCompleteListener(new OnCompleteListener<Void>() {
                         @Override
                         public void onComplete(@NonNull Task<Void> task) {
                             if (task.isSuccessful()) {
                                 mProgress.dismiss();
                                 Toast.makeText(ChangedPassword.this, "Your Password is updated successfully!", Toast.LENGTH_LONG).show();
                             } else {
                                 mProgress.dismiss();
                                 Toast.makeText(ChangedPassword.this, "Failed to update password!", Toast.LENGTH_LONG).show();

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
                    Intent intent = new Intent(ChangedPassword.this,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    return  true;
            }
        }
        return super.onKeyDown(keyCode,event);
    }
}
