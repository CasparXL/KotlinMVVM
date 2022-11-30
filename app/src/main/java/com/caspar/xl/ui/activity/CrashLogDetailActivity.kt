package com.caspar.xl.ui.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.caspar.base.base.BaseActivity
import com.caspar.base.ext.dp
import com.caspar.base.ext.setDrawableSize
import com.caspar.base.utils.log.LogFileManager
import com.caspar.xl.BuildConfig
import com.caspar.xl.R
import com.caspar.xl.databinding.ActivityCrashLogDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@AndroidEntryPoint
class CrashLogDetailActivity : BaseActivity() {
    private lateinit var mBindingView:ActivityCrashLogDetailBinding

    companion object {
        const val FILE_PATH = "FILE_PATH"
    }

    override fun getViewBinding(): ViewBinding {
        return ActivityCrashLogDetailBinding.inflate(layoutInflater).apply {
            mBindingView = this
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        val fileName = intent.getStringExtra(FILE_PATH)?:""
        mBindingView.title.tvRight.setOnClickListener {
            shareFile(this, fileName)
        }
        mBindingView.title.tvRight.setDrawableSize(1, R.drawable.ic_share, 24.dp, 24.dp)
        mBindingView.title.tvCenter.text = fileName
        mBindingView.title.tvLeft.setOnClickListener { finish() }
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                //读取大文件专用方法，该写法可以流畅读取大文件内容
                File(LogFileManager.allLogFile(), fileName).useLines {
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

    fun shareFile(context: Context, fileName: String) {
        val file = File(fileName)
        if (file.exists()) {
            val share = Intent(Intent.ACTION_SEND)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val contentUri: Uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file)
                share.putExtra(Intent.EXTRA_STREAM, contentUri)
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } else {
                share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file))
            }
            share.type = "application/vnd.ms-excel" //此处可发送多种文件
            share.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(Intent.createChooser(share, "分享文件"))
        } else {
            Toast.makeText(context, "分享文件不存在", Toast.LENGTH_SHORT).show()
        }
    }
}