package com.jcp.herehear.Class;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class Permission extends Activity {

    public static void CheckAllPermission(Activity context){
        /* Permission Check */
        int record_permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO);
        int write_permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        ArrayList<String> permissions = new ArrayList<String>();
        if (write_permissionCheck == PackageManager.PERMISSION_DENIED)
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (record_permissionCheck== PackageManager.PERMISSION_DENIED)
            permissions.add(Manifest.permission.RECORD_AUDIO);

        if(permissions.size()>0){
            String[] reqPermissionArray = new String[permissions.size()];
            reqPermissionArray = permissions.toArray(reqPermissionArray);
            ActivityCompat.requestPermissions(context, reqPermissionArray, 1);
        }
    }
}
