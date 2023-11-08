package com.liquidskr.btclient

import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionManager(private val activity: Activity) {

    // 권한 요청 코드
    private val REQUEST_CODE = 123

    // 필요한 권한을 확인하고 요청하는 메소드
    fun asdf(permission: String, onPermissionGranted: () -> Unit, onPermissionDenied: () -> Unit) {
        if (ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED) {
            // 이미 권한이 부여된 경우
            onPermissionGranted()
        } else {
            // 권한이 부여되지 않은 경우, 사용자에게 권한 요청
            onPermissionDenied()
            Log.d("asdf", "Permission Fail")
            ActivityCompat.requestPermissions(activity, arrayOf(permission), REQUEST_CODE)
            Log.d("asdf", "Try to get Permission ")
        }
    }

    fun checkAndRequestPermission() {
        if (ContextCompat.checkSelfPermission(activity, "Manifest.permission.BLUETOOTH") != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(activity, "Manifest.permission.BLUETOOTH_ADMIN") != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf("Manifest.permission.BLUETOOTH", "Manifest.permission.BLUETOOTH_ADMIN"), REQUEST_CODE)
        }
    }
}