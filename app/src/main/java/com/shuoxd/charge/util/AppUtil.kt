package com.shuoxd.charge.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.annotation.WorkerThread
import androidx.core.content.FileProvider
import com.shuoxd.charge.BuildConfig
import com.shuoxd.charge.application.MainApplication
import com.shuoxd.lib.LibApplication
import com.shuoxd.lib.util.DateUtils
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.math.abs

object AppUtil {
    private const val JPEG_FILE_PREFIX = "IMG_"
    private const val JPG_FILE_SUFFIX = "jpg"

/*    @WorkerThread
    fun saveBitmapToDisk(context: Context?, bitmap: Bitmap?): String? {
        return try {
            val destFile: File = createImageFile()!!
            BitmapUtils.writeBitmapToUri(
                context,
                bitmap,
                Uri.fromFile(destFile),
                Bitmap.CompressFormat.JPEG,
                90
            )
            destFile.absolutePath
        } catch (e: Exception) {
            null
        }
    }*/

    /**
     * 在/storage/emulated/0/Android/data/包名/files/Pictures/目录下面生成一个图片文件
     */
    fun createImageFile(): File? {
        val imageFolder =
            MainApplication.instance().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                ?: return null
        return createFile(
            imageFolder,
            JPEG_FILE_PREFIX,
            JPG_FILE_SUFFIX
        )
    }

    fun createFile(folder: File?, prefix: String?, suffix: String?): File? {
        if (folder == null) {
            return null
        }
        if (!folder.exists() || !folder.isDirectory) {
            folder.mkdirs()
        }
        val fileName = String.format(
            "%s_%s.%s",
            prefix,
            genereteImageRandomFileName(),
            suffix
        )
        val file = File(folder, fileName)
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {

            }
        }
        return file
    }

    fun genereteImageRandomFileName(): String? {
        return java.lang.String.format(
            Locale.US,
            "%s_%d",
            DateUtils.yyyy_MM_dd_hh_mm_ss_format(Date()),
            abs(generateUUID().hashCode())
        )
    }

    fun generateUUID(): String {
        return UUID.randomUUID().toString()
    }

    fun fileToUri(context: Context, file: File): Uri {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(context, context.packageName + ".fileProvider", file)
        }
        return Uri.fromFile(file)
    }


    fun getUserAgreement():String{
        val appLang = LibApplication.instance().deviceService().getAppLang()
        val agreementUrl: String;
        when{
            appLang==="zh_CN"->agreementUrl= BuildConfig.userAgreementUrl+"userterms_cn.html"
            else->agreementUrl= BuildConfig.userAgreementUrl+"userterms_en.html"
        }
        return agreementUrl;
    }


    fun getPrivacy():String{
        val appLang = LibApplication.instance().deviceService().getAppLang()
        val agreementUrl: String;
        when{
            appLang==="zh_CN"->agreementUrl= BuildConfig.userAgreementUrl+"userterms_cn.html"
            else->agreementUrl= BuildConfig.userAgreementUrl+"userterms_en.html"
        }
        return agreementUrl;
    }


}