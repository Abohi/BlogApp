package com.evansabohi.com.top.account;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.evansabohi.com.top.MainActivity;
import com.evansabohi.com.top.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.basgeekball.awesomevalidation.ValidationStyle.TEXT_INPUT_LAYOUT;

public class Register extends AppCompatActivity {
    private TextInputLayout confirmPassword,passWord,emailfield;
    private AppCompatButton registerBnt;
    private ProgressDialog mProgress;
    FirebaseAuth mAuth;
    private AppCompatTextView login;
    AwesomeValidation mAwesomeValidation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Initializing views
        mAwesomeValidation = new AwesomeValidation(TEXT_INPUT_LAYOUT);
        confirmPassword = findViewById(R.id.textInputLayoutConfirmPassword);
        passWord = findViewById(R.id.textInputLayoutPassword);
        emailfield = findViewById(R.id.textInputLayoutEmail);
        login = findViewById(R.id.loginUser);
        registerBnt = findViewById(R.id.appCompatButtonRegister);
        mProgress = new ProgressDialog(this);

        // Initializing Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        //Register and login method
        loginEvent(login);
        addValidationForTextInputLayout(Register.this);


    }
    private void addValidationForTextInputLayout(Activity activity) {
        mAwesomeValidation.addValidation(activity, R.id.textInputLayoutEmail, Patterns.EMAIL_ADDRESS, R.string.err_email);
        mAwesomeValidation.addValidation(activity, R.id.textInputLayoutConfirmPassword, R.id.textInputLayoutPassword, R.string.err_password_confirmation);
        registerEvent(registerBnt);

    }
    private void registerEvent(AppCompatButton registerBnt) {
         registerBnt.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
            if (mAwesomeValidation.validate()){
                String loginEmail =emailfield.getEditText().getText().toString();
                String loginPass = passWord.getEditText().getText().toString();
                String confirm = confirmPassword.getEditText().getText().toString();
                if (loginPass.length()<6 && confirm.length()<6 ){
                    confirmPassword.setError("Charaters must be at least 6");
                    passWord.setError("Charaters must be at least 6");
                    return;
                }
                if ((!TextUtils.isEmpty(loginPass)&& !loginPass.contains(" "))&& (!TextUtils.isEmpty(confirm)|| !confirm.contains(" "))){
                    mProgress.setMessage("Registering User..");
                    mProgress.show();

                    mAuth.createUserWithEmailAndPassword(loginEmail, loginPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){

                                Intent setupIntent = new Intent(Register.this, SetUpActivity.class);
                                startActivity(setupIntent);
                                finish();

                            } else {

                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(Register.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();

                            }

                            mProgress.dismiss();

                        }
                    });
                }else {
                    confirmPassword.setError("Empty string not allowed");
                    passWord.setError("Empty string not allowed");
                }
            }

             }
         });
    }

    //Register and also login Event
    private void loginEvent(AppCompatTextView loginButton) {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){

            sendToMain();

        }

    }

    private void sendToMain() {

        Intent mainIntent = new Intent(Register.this, MainActivity.class);
        startActivity(mainIntent);
        finish();

    }


}
