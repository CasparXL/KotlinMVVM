package com.caspar.xl.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.caspar.base.base.BaseActivity
import com.caspar.base.ext.setOnClickListener
import com.caspar.base.helper.LogUtil
import com.caspar.xl.config.Constant
import com.caspar.xl.databinding.ActivitySelectFileBinding
import com.caspar.xl.ui.adapter.FileBean
import com.caspar.xl.ui.adapter.SelectFileAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

class SelectFileActivity : BaseActivity<ActivitySelectFileBinding>() {
    private val mAdapter = SelectFileAdapter()
    private var currentFile: File? = null


    override fun initIntent() {

    }

    override fun initView(savedInstanceState: Bundle?) {
        with(mBindingView) {
            title.tvCenter.text = "SelectFile"
            rvList.layoutManager = LinearLayoutManager(this@SelectFileActivity)
            rvList.adapter = mAdapter
            title.tvLeft.setOnClickListener {
                finish()
            }
        }
        mAdapter.setOnItemClickListener { adapter, view, position ->
            val file = mAdapter.data[position].file
            if (position == 0) {
                backFiles()
            } else {
                if (file.isDirectory) {
                    currentFile = file
                    updateFiles()
                } else {
                    LogUtil.e(file.path)
                    val intent = Intent()
                    intent.putExtra("path",file.path)
                    setResult(Constant.SELECT_FILE_PATH,intent)
                    finish()
                }
            }
        }
        currentFile = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        updateFiles()
    }

    private fun backFiles() {
        currentFile = currentFile?.parentFile
        if (currentFile != null) {
            updateFiles()
        } else {
            currentFile = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        }
    }

    private fun updateFiles() {
        lifecycleScope.launch(Dispatchers.IO) {
            currentFile?.apply {
                LogUtil.e("当前目录:${this.name}")
                val filesList: MutableList<FileBean> = ArrayList()
                filesList.add(FileBean("..", this.parentFile ?: this))
                val childFiles:Array<File>? = listFiles()
                childFiles?.apply {
                    val list = this.map {
                        FileBean(it.name, it)
                    }
                    filesList.addAll(list.sortedBy { it.name.toLowerCase(Locale.ROOT) })
                    withContext(Dispatchers.Main) {
                        mAdapter.setList(filesList)
                    }
                } ?: run {
                    LogUtil.e("返回到了原目录")
                    currentFile = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                    withContext(Dispatchers.Main) {
                        mAdapter.setList(filesList)
                    }
                }
            }
        }
    }
}