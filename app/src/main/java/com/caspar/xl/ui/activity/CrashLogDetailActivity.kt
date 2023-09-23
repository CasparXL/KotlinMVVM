package com.caspar.xl.ui.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.caspar.base.base.BaseActivity
import com.caspar.base.ext.dp
import com.caspar.base.ext.setDrawableSize
import com.caspar.base.utils.log.getLogFile
import com.caspar.base.utils.log.shareFile

import com.caspar.xl.R
import com.caspar.xl.app.BaseApplication
import com.caspar.xl.databinding.ActivityCrashLogDetailBinding
import com.caspar.xl.ext.binding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@AndroidEntryPoint
class CrashLogDetailActivity : BaseActivity() {
    private val mBindingView:ActivityCrashLogDetailBinding by binding()

    companion object {
        const val FILE_PATH = "FILE_PATH"
    }

    override fun initView(savedInstanceState: Bundle?) {
        val logFiles = getLogFile()
        val fileName = intent.getStringExtra(FILE_PATH)?:""
        mBindingView.title.tvRight.setOnClickListener {
            shareFile(this, File(logFiles, fileName).absolutePath)
        }
        mBindingView.title.tvRight.setDrawableSize(1, R.drawable.ic_share, 24.dp, 24.dp)
        mBindingView.title.tvCenter.text = fileName
        mBindingView.title.tvLeft.setOnClickListener { finish() }
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                //读取大文件专用方法，该写法可以流畅读取大文件内容
                File(logFiles, fileName).useLines {
                    it.iterator().apply {
                        while (this.hasNext()) {
                            withContext(Dispatchers.Main) {
                                mBindingView.tvText.text =
                                    mBindingView.tvText.text.toString().plus("\n")
                                        .plus(this@apply.next())
                            }
                        }
                    }
                }
            }
        }
    }
}