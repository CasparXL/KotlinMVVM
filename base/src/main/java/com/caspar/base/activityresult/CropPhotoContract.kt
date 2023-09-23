package com.caspar.base.activityresult

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.content.contentValuesOf
import com.caspar.base.utils.log.dLog
import java.io.File

/**
 * 剪裁照片的协定
 * Input type  : CropParams 剪裁照片的相关参数
 * Output type : Uri?       照片剪裁完成后的uri
 *  cropPhoto.launch(CropParams(uri))
 */
class CropPhotoContract : ActivityResultContract<Uri, CropPhotoContract.CropOutput?>() {
    companion object {
        private const val TAG = "CropPhotoContract"
    }

    private var output: CropOutput? = null

    override fun createIntent(context: Context, input: Uri): Intent {
        // 获取输入图片uri的媒体类型
        val mimeType = context.contentResolver.getType(input)
        // 创建新的图片名称
        val fileName = "IMG_${System.currentTimeMillis()}.${
            MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
        }"
        val outputUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10 及以上获取图片uri
            val values = contentValuesOf(
                Pair(MediaStore.MediaColumns.DISPLAY_NAME, fileName),
                Pair(MediaStore.MediaColumns.MIME_TYPE, mimeType),
                Pair(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
            )
            context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        } else {
            Uri.fromFile(File(context.externalCacheDir!!.absolutePath, fileName))
        }
        output = CropOutput(outputUri!!, fileName)
        return Intent("com.android.camera.action.CROP")
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            .setDataAndType(input, "image/*")
            .putExtra("outputX", 300)
            .putExtra("outputY", 300)
            .putExtra("aspectX", 1)
            .putExtra("aspectY", 1)
            .putExtra("scale", true)
            .putExtra("crop", true)
            .putExtra("return-data", false) // 在小米手机部分机型中 如果直接返回Data给Intent，图片过大的时候会有问题
            .putExtra("noFaceDetection", true)
            .putExtra(MediaStore.EXTRA_OUTPUT, outputUri)
            .putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
    }

    override fun parseResult(resultCode: Int, intent: Intent?): CropOutput? {
        "Crop photo, resultCode: $resultCode output: $output".dLog()
        if (resultCode == Activity.RESULT_OK) return output
        return null
    }

    data class CropOutput(val uri: Uri, val fileName: String) {
        override fun toString(): String {
            return "{ uri: $uri, fileName: $fileName }"
        }
    }
}
