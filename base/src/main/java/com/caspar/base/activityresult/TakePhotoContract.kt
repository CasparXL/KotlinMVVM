package com.caspar.base.activityresult

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.content.FileProvider
import androidx.core.content.contentValuesOf
import com.caspar.base.utils.log.dLog
import java.io.File

/**
 * 拍照协定
 * Input type  : Unit? 不需要传值
 * Output type : Uri?  拍照完成后的uri
 *
 *  val takePhoto = registerForActivityResult(TakePhotoContract()) { uri: Uri? ->
 *                  if (uri != null) {
 *                          if (needCrop) {
 *                               cropPhoto.launch(CropParams(uri))
 *                              } else {
 *                               ivImage.setImageURI(uri)
 *                          }
 *                      }
 *                  }
 */
class TakePhotoContract : ActivityResultContract<Unit?, Pair<Int,Uri?>>() {

    private var uri: Uri? = null

    override fun createIntent(context: Context, input: Unit?): Intent {
        val mimeType = "image/jpeg"
        val fileName = "IMG_${System.currentTimeMillis()}.jpg"
        uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10 及以上获取图片uri
            val values = contentValuesOf(
                Pair(MediaStore.MediaColumns.DISPLAY_NAME, fileName),
                Pair(MediaStore.MediaColumns.MIME_TYPE, mimeType),
                Pair(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
            )
            context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        } else {
            // Android 9 及以下获取图片uri
            FileProvider.getUriForFile(
                context, "${context.packageName}.provider",
                File(context.externalCacheDir, "/$fileName")
            )
        }
        return Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, uri)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Pair<Int,Uri?> {
        "Take photo, resultCode: $resultCode, uri: $uri".dLog()
        return resultCode to (if (resultCode == Activity.RESULT_OK) uri else null)
    }
}
