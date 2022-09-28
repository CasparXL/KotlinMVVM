package com.caspar.xl.ui.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.FileProvider
import com.caspar.base.base.BaseActivity
import com.caspar.commom.ext.acStart
import com.caspar.commom.ext.dp
import com.caspar.commom.ext.setDrawableSize
import com.caspar.commom.ext.toDateString
import com.caspar.commom.helper.LogUtil
import com.caspar.xl.BuildConfig
import com.caspar.xl.R
import com.caspar.xl.databinding.ActivityCrashLogBinding
import com.caspar.xl.ui.adapter.ItemCityAdapter
import com.caspar.xl.utils.decoration.Decoration
import xcrash.TombstoneManager
import xcrash.XCrash
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.random.Random


/**
 * 崩溃日志列表
 */
class CrashLogActivity : BaseActivity<ActivityCrashLogBinding>() {
    private val adapter by lazy { ItemCityAdapter() }
    override fun initView(savedInstanceState: Bundle?) {
        val logList = File(XCrash.getLogDir())
        mBindingView.title.tvLeft.setOnClickListener { finish() }
        mBindingView.title.tvRight.setOnClickListener {
            if (adapter.itemCount > 0) {
                val parent = logList.parentFile
                parent?.list()?.forEach {
                    LogUtil.d(it)
                    if (it.contains("tombstones") && it.contains("zip")){
                        val del = File(parent,it)
                        LogUtil.d("删除${del.path}")
                        del.delete()
                    }
                }
                val path = ZipFolder(zipFileString = logList.path, srcFileString = logList.path)
                shareFile(this, path)
            } else {
                toast("没有文件可以分享")
            }
        }
        mBindingView.title.tvRight.setDrawableSize(1, R.drawable.ic_share, 24.dp, 24.dp)
        mBindingView.rvList.addItemDecoration(Decoration.decoration(10.dp, 10.dp, 0, 0))
        mBindingView.rvList.adapter = adapter
        adapter.setList(logList.list()?.toList() ?: listOf())
        mBindingView.btnCrash.setOnClickListener {
            val number = (Math.random()*10).toInt()
            LogUtil.d("随机数:${number}")
            when (number) {
                in 0..4 -> {
                    XCrash.testJavaCrash(true)
                }
                in 5..8 -> {
                    XCrash.testNativeCrash(true)
                }
                else -> {
                    throw IndexOutOfBoundsException("假装数组越界")
                }
            }
        }
        mBindingView.btnClear.setOnClickListener {
            TombstoneManager.clearAllTombstones()
            adapter.setList(logList.list()?.toList() ?: listOf())
        }
        adapter.setOnItemClickListener { a, v, p ->
            acStart<CrashLogDetailActivity> {
                putExtra(CrashLogDetailActivity.FILE_PATH, adapter.data[p])
            }
        }
    }

    fun ZipFolder(srcFileString: String, zipFileString: String) : String{
        val path = zipFileString + "" + System.currentTimeMillis().toDateString("yyyyMMdd") + ".zip"
        //创建ZIP
        try {
            val outZip = ZipOutputStream(FileOutputStream(File(path)))
            //创建文件
            val file = File(srcFileString)
            //压缩
            ZipFiles(file.parent?.plus(File.separator) ?: "", file.name, outZip)
            //完成和关闭
            outZip.finish()
            outZip.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return path
    }

    private fun ZipFiles(
        folderString: String,
        fileString: String,
        zipOutputSteam: ZipOutputStream?,
    ) {
        try {
            if (zipOutputSteam == null) return
            val file = File(folderString + fileString)
            if (file.isFile) {
                val zipEntry = ZipEntry(fileString)
                val inputStream = FileInputStream(file)
                zipOutputSteam.putNextEntry(zipEntry)
                var len: Int
                val buffer = ByteArray(4096)
                while (inputStream.read(buffer).also { len = it } != -1) {
                    zipOutputSteam.write(buffer, 0, len)
                }
                zipOutputSteam.closeEntry()
            } else {
                //文件夹
                val fileList = file.list()
                //没有子文件和压缩
                if (fileList.isNullOrEmpty()) {
                    val zipEntry = ZipEntry(fileString + File.separator)
                    zipOutputSteam.putNextEntry(zipEntry)
                    zipOutputSteam.closeEntry()
                } else {
                    //子文件和递归
                    for (i in fileList.indices) {
                        ZipFiles("$folderString$fileString/", fileList[i], zipOutputSteam)
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
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