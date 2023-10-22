package com.liquidskr.btclient

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionManager(private val activity: Activity) {

    // 권한 요청 코드
    private val REQUEST_CODE = 123

    // 필요한 권한을 확인하고 요청하는 메소드
    fun checkAndRequestPermission(permission: String, onPermissionGranted: () -> Unit, onPermissionDenied: () -> Unit) {
        if (ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED) {
            // 이미 권한이 부여된 경우
            onPermissionGranted()
        } else {
            // 권한이 부여되지 않은 경우, 사용자에게 권한 요청
            ActivityCompat.requestPermissions(activity, arrayOf(permission), REQUEST_CODE)
        }
    }
}