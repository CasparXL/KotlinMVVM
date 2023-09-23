package com.caspar.xl.ui.activity

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.caspar.base.base.BaseActivity
import com.caspar.base.ext.acStart
import com.caspar.base.ext.dp
import com.caspar.base.ext.setDrawableSize
import com.caspar.base.utils.log.ZipFolder
import com.caspar.base.utils.log.dLog
import com.caspar.base.utils.log.getLogFile
import com.caspar.base.utils.log.shareFile
import com.caspar.xl.R
import com.caspar.xl.databinding.ActivityCrashLogBinding
import com.caspar.xl.ext.binding
import com.caspar.xl.ui.adapter.ItemCityAdapter
import com.caspar.xl.utils.decoration.Decoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File


/**
 * 崩溃日志列表
 */
@AndroidEntryPoint
class CrashLogActivity : BaseActivity() {
    private val mBindingView: ActivityCrashLogBinding by binding()
    private val adapter by lazy { ItemCityAdapter() }

    override fun initView(savedInstanceState: Bundle?) {
        val logPath = getLogFile()
        mBindingView.title.tvLeft.setOnClickListener { finish() }
        mBindingView.title.tvRight.setOnClickListener {
            if (adapter.itemCount > 0) {
                logPath.apply {
                    list()?.forEach {
                        if (it.contains("zip")){
                            File(it).delete()
                        }
                    }
                }
                val path = ZipFolder(zipFileString = logPath.path?:"", srcFileString = logPath.path?:"")
                shareFile(this, path)
            } else {
                toast("没有文件可以分享")
            }
        }
        mBindingView.title.tvRight.setDrawableSize(1, R.drawable.ic_share, 24.dp, 24.dp)
        mBindingView.rvList.addItemDecoration(Decoration.decoration(10.dp, 10.dp, 0, 0))
        mBindingView.rvList.adapter = adapter
        adapter.submitList(logPath.list()?.toList())
        mBindingView.btnCrash.setOnClickListener {
            val number = (Math.random()*10).toInt()
            "随机数:${number}".dLog()
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
            lifecycleScope.launch {
                logPath.deleteRecursively()
                adapter.submitList(logPath.list()?.toList())
            }
        }
        adapter.setOnItemClickListener { a, v, p ->
            acStart<CrashLogDetailActivity> {
                putExtra(CrashLogDetailActivity.FILE_PATH, adapter.items[p])
            }
        }
    }
}