package com.jcp.herehear.Class;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class Permission extends Activity {
    static int requestCodeActivity = 1;

    /* Permission Allow or Deny */
    public static void CheckAllPermission(Activity context){
        int record_permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO);
        int write_permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int vibrate_permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.VIBRATE);

        ArrayList<String> permissions = new ArrayList<String>();
        if (write_permissionCheck == PackageManager.PERMISSION_DENIED)
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (record_permissionCheck== PackageManager.PERMISSION_DENIED)
            permissions.add(Manifest.permission.RECORD_AUDIO);
        if (vibrate_permissionCheck== PackageManager.PERMISSION_DENIED)
            permissions.add(Manifest.permission.VIBRATE);

        if(permissions.size()>0){
            String[] reqPermissionArray = new String[permissions.size()];
            reqPermissionArray = permissions.toArray(reqPermissionArray);
            ActivityCompat.requestPermissions(context, reqPermissionArray, requestCodeActivity);
        }
    }

    /* Permission 허용되지 않은 경우 기능 동작 제한 */
    public static boolean CheckPermissionProblem(Activity context){
        int record_permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO);
        int write_permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int vibrate_permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.VIBRATE);

        ArrayList<String> permissions = new ArrayList<String>();
        if (write_permissionCheck == PackageManager.PERMISSION_DENIED)
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (record_permissionCheck== PackageManager.PERMISSION_DENIED)
            permissions.add(Manifest.permission.RECORD_AUDIO);
        if (vibrate_permissionCheck== PackageManager.PERMISSION_DENIED)
            permissions.add(Manifest.permission.VIBRATE);

        if(permissions.size()>0)
            return false;
        else
            return true;
    }

    /*
    // 단일 항목 퍼미션 구현
        if (write_permissionCheck == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        if (record_permissionCheck== PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 2);
    */

    /* Permission 관련 문구 실제 사용하진 않음 */
    public void onRequestPermissionResult(int requestCode,
                                          String permissions[], int[] grantResults){
        switch(requestCode){
            case 1:{
                if(grantResults.length==3){
                    Toast.makeText(this, "필요한 승인이 모두 허가되었습니다.", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(this, "설정에서 필요한 승인을 허가할 수 있습니다.", Toast.LENGTH_LONG).show();
                    /*
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED
                            && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                        Toast.makeText(this, "두 가지 승인이 모두 허가되지 않았습니다.", Toast.LENGTH_LONG).show();
                    else if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
                        Toast.makeText(this, "Record Audio 승인이 허가 되었습니다. 저장 승인을 필요로합니다.", Toast.LENGTH_LONG).show();
                    else if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                        Toast.makeText(this, "저장 승인이 허가 되었습니다. Record Audio 승인을 필요로합니다.", Toast.LENGTH_LONG).show();
                     */
                }
                return;
            }
        }
    }
}
