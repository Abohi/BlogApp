package com.evansabohi.com.top;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.evansabohi.com.top.about.EvansActivity;
import com.evansabohi.com.top.account.Login;
import com.evansabohi.com.top.account.SetUpActivity;
import com.evansabohi.com.top.account.UpdateProfile;
import com.evansabohi.com.top.account_settings.ChangeEmail;
import com.evansabohi.com.top.account_settings.ChangedPassword;
import com.evansabohi.com.top.goup_chat.Group_Chat_Fragment;
import com.evansabohi.com.top.new_post.BlogPostScreen;
import com.evansabohi.com.top.new_post.BlogPostSearch;
import com.evansabohi.com.top.new_post.Home_Page;
import com.evansabohi.com.top.status.View_Status;
import com.evansabohi.com.top.status.Status_Page;
import com.evansabohi.com.top.user_profile.ProfileActivity;
import com.evansabohi.com.top.user_profile.ProfileModelNames;
import com.evansabohi.com.top.user_profile.ProfileSingleActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
 DatabaseReference mUserref;
 FirebaseAuth mAuth;
 String current_userid;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private     FloatingActionButton fab;
    ValueEventListener mValue;
    NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Initializing Firebase Dependency
        mAuth = FirebaseAuth.getInstance();
        current_userid = mAuth.getCurrentUser().getUid();
        mUserref = FirebaseDatabase.getInstance().getReference().child("Users").child(current_userid);
        navigationView =  findViewById(R.id.nav_view);
        mValue = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    ProfileModelNames profileModelNames = dataSnapshot.getValue(ProfileModelNames.class);
                    final String profImage = profileModelNames.getImage();
                    if (profImage!=null){
                        View hView = navigationView.getHeaderView(0);
                        final ImageView navImage = hView.findViewById(R.id.navImage);

                        Picasso.with(getApplicationContext()).load(profImage).networkPolicy(NetworkPolicy.OFFLINE).into(navImage, new Callback() {
                            @Override
                            public void onSuccess() {


                            }

                            @Override
                            public void onError() {
                                Picasso.with(getApplicationContext()).load(profImage).into(navImage);
                            }
                        });
                    }
                }else{
                    Toast.makeText(MainActivity.this,"Please setup your profile",Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mUserref.addValueEventListener(mValue);
        //Initializing Views
        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabs);
        fab =  findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(MainActivity.this, BlogPostScreen.class);
               startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(this);

        // Populating tabs with fragment via viewpager
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

    }
    private void setupViewPager(ViewPager viewPager){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new Home_Page(),"Blog Post");
        adapter.addFrag(new Status_Page(),"Add Status");
        adapter.addFrag(new View_Status(),"View Status");
        adapter.addFrag(new Group_Chat_Fragment(),"Group Chat");
        viewPager.setAdapter(adapter);
    }
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String>mFragmentTitleList = new ArrayList<>();


        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }
        public void addFrag(Fragment fragment,String title){
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
             AlertDialog.Builder Confirm = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
            Confirm.setTitle("Top");
            Confirm.setMessage("Are you sure you want to exit Top? ");

            Confirm.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mAuth.signOut();
                    sendToLogin();

                }
            });
            Confirm.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            Confirm.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_setup) {
           Intent intent = new Intent(MainActivity.this, UpdateProfile.class);
           startActivity(intent);
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_changemail) {
            Intent intent = new Intent(MainActivity.this, ChangeEmail.class);
            startActivity(intent);
        } else if (id == R.id.nav_changepass) {
            Intent intent = new Intent(MainActivity.this, ChangedPassword.class);
            startActivity(intent);
        } else if (id == R.id.nav_myprofile) {
            Intent intent = new Intent(MainActivity.this, ProfileSingleActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_about) {
            Intent intent = new Intent(MainActivity.this, EvansActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_search) {
            Intent intent = new Intent(MainActivity.this, BlogPostSearch.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null){

            sendToLogin();

        } else {

          mUserref.addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(DataSnapshot dataSnapshot) {
                  if (!dataSnapshot.exists()){
                      Intent setupIntent = new Intent(MainActivity.this, SetUpActivity.class);
                      startActivity(setupIntent);
                      finish();
                  }

              }

              @Override
              public void onCancelled(DatabaseError databaseError) {

              }
          });



        }

    }

    private void sendToLogin() {
        Intent intent = new Intent(MainActivity.this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getAction()==KeyEvent.ACTION_DOWN){
            switch(keyCode){
                case KeyEvent.KEYCODE_BACK:

                     AlertDialog.Builder Confirm = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
                    Confirm.setTitle("Top");
                    Confirm.setMessage("Are you sure you want to exit Top? ");

                    Confirm.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mAuth.signOut();
                            sendToLogin();

                        }
                    });
                    Confirm.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    Confirm.show();
                    return  true;
            }
        }
        return super.onKeyDown(keyCode,event);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mValue!=null){
            mUserref.removeEventListener(mValue);
        }
    }
}
