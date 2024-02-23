import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.liquidskr.btclient.Constants.REQUEST_CODE

@SuppressLint("StaticFieldLeak") // 단일 activity로 이루어진 앱이므로 싱글톤 객체에서 액티비티 참조가 메모리 누수에 영향을 미치지 않습니다.
object PermissionManager { // 싱글톤 해버렸습니다

    class PermissionManagerInitializationException(message: String) : Exception(message)

    private lateinit var activity: Activity

    fun initialize(activity: Activity) {
        this.activity = activity
    }

    fun checkAndRequestBluetoothPermissions() {
        val requiredPermissions = mutableListOf(
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH
        )

        // Android 12(API 레벨 31) 이상 대응
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requiredPermissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        }

        // 외부 저장소 권한 대응 (Android 11 이상에서는 스코프드 스토리지 사용 권장)
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            requiredPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        val missingPermissions = requiredPermissions.filter {
            ActivityCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(activity, missingPermissions.toTypedArray(), REQUEST_CODE)
        }
    }

}