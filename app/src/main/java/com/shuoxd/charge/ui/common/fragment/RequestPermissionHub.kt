package com.shuoxd.charge.ui.common.fragment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.shuoxd.charge.R
import com.shuoxd.charge.application.MainApplication

/**
 * 权限请求工具
 * 1.shouldShowRequestPermissionRationale方法返回值（1.用户禁止权限，并勾选不再提醒，返回false；2.用户禁止权限，不勾选不再提醒，返回true；3.用户同意权限，返回false）
 */
class RequestPermissionHub : Fragment() {

    companion object {

        const val PERMISSION_REQUEST_CODE = 4896
        const val APP_SETTING_REQUEST_CODE = 4851

        /**
         * 是有拥有对应的权限
         */
        fun hashPermission(context: Context, permissions: Array<String>): Boolean {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        permission
                    ) == PackageManager.PERMISSION_DENIED
                ) {
                    return false
                }
            }
            return true

        }

        fun requestPermission(
            fragmentManager: FragmentManager,
            permissions: Array<String>,
            callback: ((isGranted: Boolean) -> Unit)? = null
        ) {
            val requestPermissionHub = RequestPermissionHub()
            requestPermissionHub.permissions = permissions
            requestPermissionHub.callback = callback
            fragmentManager.beginTransaction()
                .add(requestPermissionHub, RequestPermissionHub::class.java.name)
                .commitAllowingStateLoss()
        }

    }

    private lateinit var permissions: Array<String>

    /**
     * @param permission 权限名
     * @param isGranted true已授权，false未授权
     */
    private var callback: ((isGranted: Boolean) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (hashPermission(requireContext(), permissions)) {
            callback?.invoke(true)
            detach()
            return
        }

        requestPermissions(permissions, PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        //解决授权弹框导致通过Application获取string的多语言适配失效
        MainApplication.instance().initLanguage(requireActivity().applicationContext)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (index in grantResults.indices) {
                if (grantResults[index] == PackageManager.PERMISSION_DENIED) {
                    callback?.invoke(false)
                    showOpenAppSettingDialog(permissions[index])
                    return
                }
            }
            callback?.invoke(true)
            detach()
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun showOpenAppSettingDialog(permission: String) {
        var title = ""
        var messagge = ""
        val appName = getString(R.string.app_name)
        when (permission) {
            Manifest.permission.CAMERA -> {
                title = getString(
                    R.string.m47_app_want_to_visit_format,
                    appName,
                    getString(R.string.m48_camera)
                )
                messagge = getString(
                    R.string.m51_to_app_set_permission_format,
                    appName,
                    getString(R.string.m48_camera)
                )
            }
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE -> {
                title = getString(
                    R.string.m47_app_want_to_visit_format,
                    appName,
                    getString(R.string.m49_photo_album)
                )
                messagge = getString(
                    R.string.m51_to_app_set_permission_format,
                    appName,
                    getString(R.string.m54_storage_photo_album)
                )
            }
            Manifest.permission.ACCESS_FINE_LOCATION -> {
                title = getString(
                    R.string.m47_app_want_to_visit_format,
                    appName,
                    getString(R.string.m50_location)
                )
                messagge = getString(
                    R.string.m51_to_app_set_permission_format,
                    appName,
                    getString(R.string.m50_location)
                )
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(messagge)
            .setNegativeButton(R.string.m16_cancel, null)
            .setPositiveButton(
                R.string.m55_to_set
            ) { _, _ -> openAppSetting() }
            .show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        detach()
    }

    private fun openAppSetting() {
        startActivityForResult(Intent().also {
            it.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
            it.data = Uri.fromParts("package", requireContext().packageName, null)
        }, APP_SETTING_REQUEST_CODE)
    }

    private fun detach() {
        parentFragmentManager.beginTransaction().remove(this).commitAllowingStateLoss()
    }
}