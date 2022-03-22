package com.caspar.commom.activityresult

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.CallSuper
import com.caspar.commom.helper.LogUtil

/**
 * 选择照片的协定
 * Input type  : Unit? 不需要传值
 * Output type : Uri?  选择完成后的 image uri
 * // 选择图片
 *          val selectPhoto = registerForActivityResult(SelectPhotoContract()) { uri: Uri? ->
 *                                  uri?.apply {
 *                                      mBindingView.ivBack.load(this)
 *                                  }?:run{
 *                                      toast("您没有选择任何图片")
 *                                  }
 *                            }
 *           selectPhoto.launch(null)
 *
 */
class SelectPhotoContract : ActivityResultContract<Unit?, Uri?>() {

    @CallSuper
    override fun createIntent(context: Context, input: Unit?): Intent {
        return Intent(Intent.ACTION_PICK).setType("image/*")
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        LogUtil.d("pick photo result: ${intent?.data}")
        return intent?.data
    }
}