package com.evansabohi.com.top.mainpage_fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.evansabohi.com.top.GetTimeAgo;
import com.evansabohi.com.top.R;
import com.evansabohi.com.top.status.UserStatus;
import com.evansabohi.com.top.storiesprogressview.StoriesProgressView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProgressActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    ArrayList<String> imagecollection;
    ArrayList<UserStatus> txtDrawable;
    TextDrawable drawableText;
    private StoriesProgressView storiesProgressView;
    private ImageView image;
    CircleImageView mProfImage;
    private Toolbar screenToolbar;

    private int counter = 0;


    private final long[] durations = new long[]{
            500L, 1000L, 1500L, 4000L, 5000L, 1000,
    };

    long pressTime = 0L;
    long limit = 500L;

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    pressTime = System.currentTimeMillis();
                    storiesProgressView.pause();
                    return false;
                case MotionEvent.ACTION_UP:
                    long now = System.currentTimeMillis();
                    storiesProgressView.resume();
                    return limit < now - pressTime;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.progress_screen);
        final Context  mContext = getBaseContext();
        //Setting ActionBar
        screenToolbar = findViewById(R.id.screenprogress);
        setSupportActionBar(screenToolbar);
        String nameUser = getIntent().getStringExtra("username");
        getSupportActionBar().setTitle(nameUser);

         mProfImage = findViewById(R.id.profImage);
         final String profImage = getIntent().getStringExtra("image");
        Picasso.with(mContext).load(profImage).networkPolicy(NetworkPolicy.OFFLINE).into(mProfImage, new Callback() {
            @Override
            public void onSuccess() {


            }

            @Override
            public void onError() {
                Picasso.with(mContext).load(profImage).into(mProfImage);
            }
        });
        imagecollection  = getIntent().getStringArrayListExtra("imagecollection");
        txtDrawable = new ArrayList<UserStatus>();

        for(int i = 0;i<imagecollection.size();i++){

            drawableText= TextDrawable.builder().beginConfig()
                    .textColor(Color.WHITE)
                    .fontSize(20)
                    .bold()
                    .toUpperCase()
                    .withBorder(4)
                    .endConfig().buildRoundRect(imagecollection.get(i), Color.DKGRAY,5);
            txtDrawable.add(new UserStatus(drawableText));
        }
        storiesProgressView = (StoriesProgressView) findViewById(R.id.stories);
        storiesProgressView.setStoriesCount(txtDrawable.size());
        storiesProgressView.setStoryDuration(3000L);
        // or
        // storiesProgressView.setStoriesCountWithDurations(durations);
        storiesProgressView.setStoriesListener(this);
        storiesProgressView.startStories();

        image = (ImageView) findViewById(R.id.image);
       image.setImageDrawable(txtDrawable.get(counter).getTxtDrawable());


        // bind reverse view
        View reverse = findViewById(R.id.reverse);
        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.reverse();
            }
        });
        reverse.setOnTouchListener(onTouchListener);

        // bind skip view
        View skip = findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.skip();
            }
        });
        skip.setOnTouchListener(onTouchListener);
    }

    @Override
    public void onNext() {

        image.setImageDrawable(txtDrawable.get(++counter).getTxtDrawable());


    }

    @Override
    public void onPrev() {
        if ((counter - 1) < 0) return;

        image.setImageDrawable(txtDrawable.get(--counter).getTxtDrawable());

    }

    @Override
    public void onComplete() {
       finish();
    }

    @Override
    protected void onDestroy() {
        // Very important !
        storiesProgressView.destroy();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();


    }
}

