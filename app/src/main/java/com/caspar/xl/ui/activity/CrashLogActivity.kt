package com.caspar.xl.ui.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.caspar.base.base.BaseActivity
import com.caspar.commom.ext.acStart
import com.caspar.commom.ext.dp
import com.caspar.commom.ext.setDrawableSize
import com.caspar.commom.helper.LogFileManager
import com.caspar.commom.helper.LogUtil
import com.caspar.xl.R
import com.caspar.xl.databinding.ActivityCrashLogBinding
import com.caspar.xl.ui.adapter.ItemCityAdapter
import com.caspar.xl.utils.decoration.Decoration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


/**
 * 崩溃日志列表
 */
class CrashLogActivity : BaseActivity<ActivityCrashLogBinding>() {
    private val adapter by lazy { ItemCityAdapter() }
    override fun initView(savedInstanceState: Bundle?) {
        val logList = LogFileManager.allLogFile()
        mBindingView.title.tvLeft.setOnClickListener { finish() }
        mBindingView.title.tvRight.setOnClickListener {
            if (adapter.itemCount > 0) {
                logList?.apply {
                    list()?.forEach {
                        if (it.contains("zip")){
                            File(it).delete()
                        }
                    }
                }
                val path = LogFileManager.ZipFolder(zipFileString = logList?.path?:"", srcFileString = logList?.path?:"")
                LogFileManager.shareFile(this, path)
            } else {
                toast("没有文件可以分享")
            }
        }
        mBindingView.title.tvRight.setDrawableSize(1, R.drawable.ic_share, 24.dp, 24.dp)
        mBindingView.rvList.addItemDecoration(Decoration.decoration(10.dp, 10.dp, 0, 0))
        mBindingView.rvList.adapter = adapter
        adapter.setList(LogFileManager.allLogFile()?.list()?.toList())
        mBindingView.btnCrash.setOnClickListener {
            val number = (Math.random()*10).toInt()
            LogUtil.d("随机数:${number}")
            when (number) {
                in 0..4 -> {
                    throw SecurityException("假装出现加密异常")
                }
                in 5..8 -> {
                    throw IllegalAccessException("假装出现了特殊问题")
                }
                else -> {
                    throw IndexOutOfBoundsException("假装数组越界")
                }
            }
        }
        mBindingView.btnClear.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                LogFileManager.clearCrashLog(0)
                withContext(Dispatchers.Main){
                    adapter.setList(LogFileManager.allLogFile()?.list()?.toList())
                }
            }
        }
        adapter.setOnItemClickListener { a, v, p ->
            acStart<CrashLogDetailActivity> {
                putExtra(CrashLogDetailActivity.FILE_PATH, adapter.data[p])
            }
        }
    }
}