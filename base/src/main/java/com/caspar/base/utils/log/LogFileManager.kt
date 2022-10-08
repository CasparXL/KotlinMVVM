package com.caspar.base.utils.log

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.core.content.FileProvider
import com.caspar.base.ext.toDateString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object LogFileManager {
    private var logPath: File? = null
    //崩溃日志前缀
    private var crashName = "Crash"
    //包名
    private var packageName = "packageName"
    //普通日志前缀
    private var eventName = "Event"
    /**
     * @param parentPath 父级文件夹路径 例如 context.filesDir.path
     * @param name 日志文件夹名称 例如 CustomLog
     */
    fun initPath(packageName: String, parentPath: String, name: String) {
        LogFileManager.packageName = packageName
        logPath = File(parentPath, name)
    }

    /**
     * 返回所有日志路径
     */
    fun allLogFile(): File? {
        return logPath
    }

    /**
     * 清除日志
     * @param isAll 0,全部删除 1,删除全部的crash日志 2,删除自定义日志
     */
    suspend fun clearCrashLog(isAll:Int){
        withContext(Dispatchers.IO){
            logPath?.apply {
                when(isAll){
                    0 -> this.apply {
                        this.list()?.forEach {
                            File(this,it).delete()
                        }
                    }
                    1 -> {
                        list()?.forEach {
                            if (it.contains(crashName)){
                                File(this,it).delete()
                            }
                        }
                    }
                    2 -> getEventLog()?.delete()
                    else->{}
                }
            }
        }
    }
    /**
     * 写入日志的文件
     */
    fun getEventLog(): File? {
        return logPath?.let {
            if (!it.exists()) {
                it.mkdirs()
            }
            File(it, "${eventName}Log.txt")
        } ?: run {
            null
        }
    }

    /**
     * 崩溃日志的文件列表
     */
    fun getCrashLog(): File? {
        return logPath?.let {
            if (!it.exists()) {
                it.mkdirs()
            }
            File(it, "${crashName}_${System.currentTimeMillis().toDateString("yyyyMMdd")}_Log.txt")
        } ?: run {
            null
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
                val contentUri: Uri = FileProvider.getUriForFile(context, "$packageName.provider", file)
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