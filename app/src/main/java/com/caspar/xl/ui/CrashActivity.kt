package com.caspar.xl.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import cat.ereza.customactivityoncrash.CustomActivityOnCrash
import cat.ereza.customactivityoncrash.config.CaocConfig
import com.caspar.base.base.BaseActivity
import com.caspar.base.databinding.ActivityCrashBinding
import com.caspar.commom.helper.DoubleClickHelper
import com.caspar.commom.helper.LogUtil
import com.caspar.xl.R

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2019/06/27
 * desc   : 崩溃捕捉界面
 */
class CrashActivity : BaseActivity<ActivityCrashBinding>(), View.OnClickListener {
    private var mConfig: CaocConfig? = null
    private var mDialog: AlertDialog? = null

    override fun onClick(v: View) {
        if (DoubleClickHelper.isOnDoubleClick) {
            LogUtil.e("我被拦截了")
            return
        }
        when (v.id) {
            R.id.btn_crash_restart -> CustomActivityOnCrash.restartApplication(this@CrashActivity, mConfig!!)
            R.id.btn_crash_log -> {
                if (mDialog == null) {
                    mDialog = AlertDialog.Builder(this@CrashActivity).setTitle(R.string.crash_error_details).setMessage(CustomActivityOnCrash.getAllErrorDetailsFromIntent(this@CrashActivity, intent)).setPositiveButton(R.string.crash_close, null).setNeutralButton(R.string.crash_copy_log) { _: DialogInterface?, _: Int -> copyErrorToClipboard() }.create()
                }
                mDialog!!.show()
                val textView = mDialog!!.findViewById<TextView>(android.R.id.message)
                textView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            }
            else                   -> {
            }
        }
    }

    /**
     * 复制报错信息到剪贴板
     */
    private fun copyErrorToClipboard() {
        val errorInformation = CustomActivityOnCrash.getAllErrorDetailsFromIntent(this@CrashActivity, intent)
        (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(ClipData.newPlainText(getString(R.string.crash_error_info), errorInformation))
    }

    override fun initView(savedInstanceState: Bundle?) {
        mConfig = CustomActivityOnCrash.getConfigFromIntent(intent)
        if (mConfig == null) {
            // 这种情况永远不会发生，只要完成该活动就可以避免递归崩溃
            finish()
        }
        mBindingView.btnCrashLog.setOnClickListener(this)
        mBindingView.btnCrashRestart.setOnClickListener(this)
    }
}