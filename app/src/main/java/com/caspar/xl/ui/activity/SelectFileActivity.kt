package com.caspar.xl.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.caspar.base.base.BaseActivity
import com.caspar.commom.helper.LogUtil
import com.caspar.xl.config.Constant
import com.caspar.xl.databinding.ActivitySelectFileBinding
import com.caspar.xl.helper.MMKVUtil
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

    @SuppressLint("SetTextI18n")
    override fun initView(savedInstanceState: Bundle?) {
        with(mBindingView) {
            title.tvCenter.text = "SelectFile"
            rvList.layoutManager = LinearLayoutManager(this@SelectFileActivity)
            rvList.adapter = mAdapter
            title.tvLeft.setOnClickListener {
                finish()
            }
        }
        mAdapter.setOnItemClickListener { _, _, position ->
            val file = mAdapter.data[position].file
            if (position == 0) {
                backFiles()
            } else {
                if (file.isDirectory) {
                    currentFile = file
                    updateFiles()
                } else {
                    MMKVUtil.encode(Constant.ADDRESS_HISTORICAL, file.parent)
                    LogUtil.e(file.path)
                    val intent = Intent()
                    intent.putExtra("path",file.path)
                    setResult(Constant.SELECT_FILE_PATH,intent)
                    finish()
                }
            }
        }
        initFilePath()
        updateFiles()
    }

    /**
     * 初始化目录
     */
    private fun initFilePath() {
        val oldPath = MMKVUtil.decodeString(Constant.ADDRESS_HISTORICAL)
        if (oldPath.isEmpty()){
            toRootDirectory()
        } else {
            currentFile = File(oldPath)
        }
    }

    /**
     * 用于定位sd卡根目录
     */
    private fun toRootDirectory() {
        var file: File? = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        var isNotNull = true
        do {
            val parentFile = file?.parentFile
            parentFile?.apply {
                file = this
                if (!parentFile.path.contains("/Android")) {
                    isNotNull = false
                }
            } ?: run {
                isNotNull = false
            }
        } while (isNotNull)
        currentFile = file ?: getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
    }

    /**
     * 用于点击[..]返回上一层目录
     */
    private fun backFiles() {
        currentFile = currentFile?.parentFile
        if (currentFile != null) {
            updateFiles()
        } else {
            toRootDirectory()
        }
    }

    private fun updateFiles() {
        lifecycleScope.launch(Dispatchers.IO) {
            currentFile?.apply {
                LogUtil.e("当前目录:${this.name}")
                val filesList: MutableList<FileBean> = ArrayList()
                filesList.add(FileBean("..", this.parentFile ?: this))
                val childFiles: Array<File>? = listFiles()
                childFiles?.apply {
                    val list = this.map {
                        FileBean(it.name, it)
                    }
                    filesList.addAll(list.sortedBy { it.name.lowercase(Locale.ROOT) })
                    withContext(Dispatchers.Main) {
                        mAdapter.setList(filesList)
                    }
                } ?: run {
                    LogUtil.e("返回到了原目录")
                    toRootDirectory()
                    updateFiles()
                }
            }
        }
    }
}