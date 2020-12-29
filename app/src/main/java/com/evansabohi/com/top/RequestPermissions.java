package com.evansabohi.com.top;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by www.AndroidSquad.Net on 12/14/2016.
 */

public class RequestPermissions implements ActivityCompat.OnRequestPermissionsResultCallback {
    static Context mContext;
    public RequestPermissions(Context mContext){
        this.mContext = mContext;
    }
    public static  final int MULTIPLE_PERMISSIONS = 10;

  public  static String [] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE
    };
    public  boolean checkPermissions(){
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p:permissions){
            result = ContextCompat.checkSelfPermission(mContext,p);
            if (result!= PackageManager.PERMISSION_GRANTED){
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()){
            ActivityCompat.requestPermissions((Activity) mContext,listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MULTIPLE_PERMISSIONS:{
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){

                }
                else{
                    ActivityCompat.requestPermissions((Activity) mContext,permissions,MULTIPLE_PERMISSIONS);
                }
                return;
            }
        }
    }
}

