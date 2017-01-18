package com.luseen.simplepermission.permissions;

import java.util.List;

/**
 * Created by Chatikyan on 24.11.2016.
 */

public interface MultiplePermissionCallback {

    void onPermissionGranted(boolean allPermissionsGranted, List<Permissions> grantedPermissions);

    void onPermissionDenied(List<Permissions> deniedPermissions, List<Permissions> foreverDeniedPermissions);
}
