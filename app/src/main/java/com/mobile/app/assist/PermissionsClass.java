package com.mobile.app.assist;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by E841719 on 24.11.2017.
 */

public class PermissionsClass {
    Activity mActivity;
    int MY_PERMISSIONS_REQUEST=99;
    static String[] myPermissions=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH};

    public PermissionsClass(Activity c){
        mActivity =c;
    }
    void checkPermission(String permission){
        if (ContextCompat.checkSelfPermission(mActivity,permission)!= PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, permission)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(mActivity,new String[]{permission},MY_PERMISSIONS_REQUEST);
                // MY_PERMISSIONS_REQUEST is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }
    public void checkPermissions(){
        for(String p:myPermissions){
            checkPermission(p);
        }
    }
}
