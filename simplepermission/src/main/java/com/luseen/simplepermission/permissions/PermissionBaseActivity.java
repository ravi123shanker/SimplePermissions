package com.luseen.simplepermission.permissions;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryadav3 on 1/4/2017.
 */

public class PermissionBaseActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 3110;

    private List<String> permissionsToRequest = new ArrayList<>();
    private List<Permissions> grantedPermissions = new ArrayList<>();
    private List<Permissions> deniedPermissions = new ArrayList<>();
    private List<Permissions> foreverDeniedPermissions = new ArrayList<>();
    private MultiplePermissionCallback multiplePermissionCallback;
    private SinglePermissionCallback singlePermissionCallback;
    private boolean isMultiplePermissionRequested = false;

    protected void requestPermissions(Permissions[] permissions, MultiplePermissionCallback multiplePermissionCallback) {
        if (PermissionUtils.isMarshmallowOrHigher()) {
            isMultiplePermissionRequested = true;
            this.multiplePermissionCallback = multiplePermissionCallback;

            for (Permissions permission : permissions) {
                if (!PermissionUtils.isGranted(this, permission)) {
                    permissionsToRequest.add(permission.toString());
                }
            }

            if (!permissionsToRequest.isEmpty()) {
                ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
                        PERMISSION_REQUEST_CODE);
            }else{
                alreadyGranted(multiplePermissionCallback);
            }
        }else{
            alreadyGranted(multiplePermissionCallback);
        }
    }

    protected void requestPermission(Permissions permission, SinglePermissionCallback singlePermissionCallback) {
        if (PermissionUtils.isMarshmallowOrHigher()) {
            isMultiplePermissionRequested = false;
            this.singlePermissionCallback = singlePermissionCallback;
            if(!PermissionUtils.isGranted(this, permission))
                permissionsToRequest.add(permission.toString());
            if (!permissionsToRequest.isEmpty()) {
                ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
                        PERMISSION_REQUEST_CODE);
            }else {
                alreadyGranted(singlePermissionCallback);
            }
        } else {
            alreadyGranted(singlePermissionCallback);
        }
    }

    private void alreadyGranted(SinglePermissionCallback singlePermissionCallback){
        grantedPermissions.clear();
        deniedPermissions.clear();
        foreverDeniedPermissions.clear();
        permissionsToRequest.clear();
        singlePermissionCallback.onPermissionResult(true, false);
    }
    private void alreadyGranted(MultiplePermissionCallback multiplePermissionCallback){
        grantedPermissions.clear();
        deniedPermissions.clear();
        foreverDeniedPermissions.clear();
        permissionsToRequest.clear();
        multiplePermissionCallback.onPermissionGranted(true, null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        onPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void onPermissionsResult(int requestCode, String[] permissions,
                                     int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            grantedPermissions.clear();
            deniedPermissions.clear();
            foreverDeniedPermissions.clear();

            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    if (permissionsToRequest.contains(permissions[i])) {
                        grantedPermissions.add(Permissions.stringToPermission(permissions[i]));
                    }
                } else {
                    boolean permissionsDeniedForever =
                            ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i]);
                    if (permissionsToRequest.contains(permissions[i])) {
                        if (!permissionsDeniedForever) {
                            foreverDeniedPermissions.add(Permissions.stringToPermission(permissions[i]));
                        }
                        deniedPermissions.add(Permissions.stringToPermission(permissions[i]));
                    }
                }
            }

            boolean allPermissionsGranted = deniedPermissions.isEmpty();
            if (isMultiplePermissionRequested) {
                multiplePermissionCallback.onPermissionGranted(allPermissionsGranted, grantedPermissions);
                multiplePermissionCallback.onPermissionDenied(deniedPermissions, foreverDeniedPermissions);
            } else {
                boolean permissionsDeniedForever = ActivityCompat.shouldShowRequestPermissionRationale(
                        this, permissionsToRequest.get(0));
                if (allPermissionsGranted)
                    permissionsDeniedForever = true;
                singlePermissionCallback.onPermissionResult(allPermissionsGranted, !permissionsDeniedForever);
            }
            permissionsToRequest.clear();
        }
    }

    protected void openSettings(){
        PermissionUtils.openApplicationSettings(this);
    }
}
