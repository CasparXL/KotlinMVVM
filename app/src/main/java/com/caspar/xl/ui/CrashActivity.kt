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
import androidx.lifecycle.lifecycleScope
import cat.ereza.customactivityoncrash.CustomActivityOnCrash
import cat.ereza.customactivityoncrash.config.CaocConfig
import com.caspar.base.base.BaseActivity
import com.caspar.base.helper.DoubleClickHelper
import com.caspar.base.R
import com.caspar.base.utils.log.createFileLoggingTree
import com.caspar.base.utils.log.eLog
import com.caspar.xl.databinding.ActivityCrashBinding
import com.caspar.xl.ext.binding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2019/06/27
 * desc   : 崩溃捕捉界面
 */
class CrashActivity : BaseActivity(), View.OnClickListener {
    private val mBindingView: ActivityCrashBinding by binding()
    private var mConfig: CaocConfig? = null
    private var mDialog: AlertDialog? = null

    override fun onClick(v: View) {
        if (DoubleClickHelper.isOnDoubleClick) {
            //禁止多秒内重复点击
            return
        }
        when (v.id) {
            com.caspar.xl.R.id.btn_crash_restart -> CustomActivityOnCrash.restartApplication(this@CrashActivity, mConfig!!)
            com.caspar.xl.R.id.btn_crash_log -> {
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
        //ApplicationInitializer仅在主进程中有效，其他进程中需要单独拿出来初始化，否则写入文件失败
        mConfig = CustomActivityOnCrash.getConfigFromIntent(intent)
        if (mConfig == null) {
            // 这种情况永远不会发生，只要完成该活动就可以避免递归崩溃,但是为了保险起见,加入该方法避免出现其他异常
            finish()
        }
        val errorInformation = CustomActivityOnCrash.getAllErrorDetailsFromIntent(this@CrashActivity, intent)
        lifecycleScope.launch(Dispatchers.IO) {
            createFileLoggingTree(maxLogFileSize = 1 * 1024 * 1024)
            "Found Crash:${errorInformation}".eLog()
        }
        mBindingView.btnCrashLog.setOnClickListener(this)
        mBindingView.btnCrashRestart.setOnClickListener(this)
    }
}