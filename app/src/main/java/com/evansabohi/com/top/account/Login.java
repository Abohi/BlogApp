package com.evansabohi.com.top.account;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.evansabohi.com.top.MainActivity;
import com.evansabohi.com.top.R;
import com.evansabohi.com.top.account_settings.ChangedPassword;
import com.evansabohi.com.top.account_settings.ForgotPassword;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.Range;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

import static com.basgeekball.awesomevalidation.ValidationStyle.TEXT_INPUT_LAYOUT;

public class Login extends AppCompatActivity {
  private TextInputLayout confirmPassword,passWord,emailfield;
  private AppCompatButton loginButton,gotoRegisterBnt;
    private ProgressDialog mProgress;
  FirebaseAuth mAuth;
  private AppCompatTextView mForgotPass;
  AwesomeValidation mAwesomeValidation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initializing views
        mAwesomeValidation = new AwesomeValidation(TEXT_INPUT_LAYOUT);
        confirmPassword = findViewById(R.id.textInputLayoutConfirmPassword);
        passWord = findViewById(R.id.textInputLayoutPassword);
        emailfield = findViewById(R.id.textInputLayoutEmail);
        loginButton = findViewById(R.id.appCompatButtonLogin);
        gotoRegisterBnt = findViewById(R.id.appCompatButtonRegister);
        mForgotPass = findViewById(R.id.forgotPas);
        passWord.setHint("Password");
        confirmPassword.setHint("Confirm Passsword");
        emailfield.setHint("Email");
        mProgress = new ProgressDialog(this);

        // Initializing Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        addValidationForTextInputLayout(Login.this);
        // register and login methods
        gotoRegister(gotoRegisterBnt);
        mForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, ForgotPassword.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

    }
    private void addValidationForTextInputLayout(Activity activity) {
        mAwesomeValidation.addValidation(activity, R.id.textInputLayoutEmail, Patterns.EMAIL_ADDRESS, R.string.err_email);
        mAwesomeValidation.addValidation(activity, R.id.textInputLayoutConfirmPassword, R.id.textInputLayoutPassword, R.string.err_passwor);

        loginUser(loginButton);
    }
//Register and also login Event
    private void gotoRegister(Button register) {
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this,Register.class);
                startActivity(intent);
            }
        });
    }
    private void loginUser(AppCompatButton loginButton) {
        loginButton.setOnClickListener(new View.OnClickListener() {
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
                    if ((!TextUtils.isEmpty(loginPass)&& !loginPass.contains(" ")) && (!TextUtils.isEmpty(confirm)&& !confirm.contains(" "))){
                        mProgress.setMessage("Login In...");
                        mProgress.show();

                        mAuth.signInWithEmailAndPassword(loginEmail, loginPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if(task.isSuccessful()){

                                    sendToMain();

                                } else {

                                    String errorMessage = task.getException().getMessage();
                                    Toast.makeText(Login.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();


                                }

                                mProgress.dismiss();
                            }
                        });
                    }else{
                        confirmPassword.setError("Empty string not allowed");
                        passWord.setError("Empty string not allowed");
                    }



                }

            }
        });
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(Login.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getAction()==KeyEvent.ACTION_DOWN){
            switch(keyCode){
                case KeyEvent.KEYCODE_BACK:
                    finish();
                    return  true;
            }
        }
        return super.onKeyDown(keyCode,event);
    }

}
