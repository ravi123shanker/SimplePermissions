package com.luseen.simplepermissions;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.luseen.simplepermission.permissions.MultiplePermissionCallback;
import com.luseen.simplepermission.permissions.PermissionBaseFragment;
import com.luseen.simplepermission.permissions.Permissions;

import java.util.List;

public class BlankFragment extends PermissionBaseFragment {

    public static final String TAG = BlankFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_blank, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Permissions[] permissions = new Permissions[]{
                Permissions.CALL_PHONE,
                Permissions.CAMERA,
                Permissions.GET_ACCOUNTS,
                Permissions.FINE_LOCATION};

        requestPermissions(permissions, new MultiplePermissionCallback() {
            @Override
            public void onPermissionGranted(boolean allPermissionsGranted, List<Permissions> grantedPermissions) {
                Log.d(TAG, "All permissions is granted  = " + allPermissionsGranted);
                for (Permissions grantedPermission : grantedPermissions) {
                    Log.d(TAG, "Granted permissions " + grantedPermission);
                }
            }

            @Override
            public void onPermissionDenied(List<Permissions> deniedPermissions, List<Permissions> foreverDeniedPermissions) {
                for (Permissions deniedPermission : deniedPermissions) {
                    Log.d(TAG, "Denied permissions " + deniedPermission);
                }

                for (Permissions deniedPermission : foreverDeniedPermissions) {
                    Log.d(TAG, "Forever denied permissions" + deniedPermission);
                }
            }
        });
    }
}
