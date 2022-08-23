package com.shuoxd.lib.service.device

import android.content.Context
import android.os.Build
import android.text.TextUtils
import com.shuoxd.lib.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.RandomAccessFile
import java.util.*

/**
 * 获取设备的唯一标识
 */
internal class DeviceId private constructor() {

    //内部类，在装载该内部类时才会去创建单例对象
    private object Holder {
        val instance = DeviceId()
    }

    companion object {

        fun instance() = Holder.instance

        fun init(context: Context) {
            if (!TextUtils.isEmpty(instance().id)) {
                return
            }
            val deviceSerial = Build.SERIAL
            if (!isSerialIllegal(context, deviceSerial)) {
                instance().id = deviceSerial
            } else {
                instance().id = InstallationID.id(context)
            }
        }

        private fun isSerialIllegal(context: Context, deviceSerial: String): Boolean {
            if (TextUtils.isEmpty(deviceSerial)) {
                return true
            }
            val res = context.resources
            val illegalIds = res.getStringArray(R.array.illegal_device_ids)
            for (illegalId in illegalIds) {
                if (deviceSerial.equals(illegalId, ignoreCase = true)) {
                    return true
                }
            }
            return false
        }
    }

    private var id: String = ""

    fun id(): String {
        return id
    }

    /**
     * 应用生命周期内唯一，卸载后重新生成
     */
    private object InstallationID {
        private var sID: String = ""
        private const val INSTALLATION = "Growatt-InstallationID"

        @Synchronized
        fun id(context: Context): String {
            if (TextUtils.isEmpty(sID)) {
                val installation = File(context.filesDir, INSTALLATION)
                try {
                    if (!installation.exists()) {
                        writeInstallationFile(installation)
                    }
                    sID = readInstallationFile(installation)
                } catch (e: Exception) {
                    throw RuntimeException(e)
                }
            }
            return sID
        }

        @Throws(IOException::class)
        private fun readInstallationFile(installation: File): String {
            val f = RandomAccessFile(installation, "r")
            val bytes = ByteArray(f.length().toInt())
            f.readFully(bytes)
            f.close()
            return String(bytes)
        }

        @Throws(IOException::class)
        private fun writeInstallationFile(installation: File) {
            val out = FileOutputStream(installation)
            val id = UUID.randomUUID().toString()
            out.write(id.toByteArray())
            out.close()
        }
    }

}