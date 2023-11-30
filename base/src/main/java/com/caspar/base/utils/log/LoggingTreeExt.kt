package com.caspar.base.utils.log


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.core.content.FileProvider
import com.caspar.base.ext.timeFormatDate
import com.caspar.base.utils.log.AppTree.Companion.createStackElementTag
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * 日志文件夹名称
 */
const val LOG_DIR_NAME = "Logs"

/**
 * 当前写入日志名称
 */
const val LOG_FILE_NAME = "app_logs.txt"

/**
 * 下一次日志距离上一次时间间隔，超出则将当前缓存区日志存入本地沙盒
 */
const val FLUSH_INTERVAL = 1000

/**
 * 过滤堆栈信息,将日志定位到调用层面上
 */
private val fqcnIgnore = listOf(
    Timber::class.java.name,
    Timber.Forest::class.java.name,
    Timber.Tree::class.java.name,
    AppTree::class.java.name,
    FileLoggingTree::class.java.name,
    Thread.currentThread().stackTrace.getOrNull(2)?.className ?: "NULL"
)

/**
 * 安装日志到Timber日志管理
 * @param maxLogFileSize 一个文件最大写入多少日志
 * @param maxLogFiles 最多写入多少个文件
 */
fun Context.createFileLoggingTree(
    maxLogFileSize: Long = 1 * 1024 * 1024,
    maxLogFiles: Int = 10
){
    Timber.plant(FileLoggingTree(
        context = this,
        maxLogFileSize = maxLogFileSize,
        maxLogFiles = maxLogFiles
    ))
}

/**
 * Verbose 日志
 */
fun String.vLog(
    tag: String = Throwable().stackTrace
        .first { it.className !in fqcnIgnore }
        .let(::createStackElementTag), throwable: Throwable? = null
) {
    Timber.tag(tag).v(throwable, this)
}

/**
 * Debug 日志
 */
fun String.dLog(
    tag: String = Throwable().stackTrace
        .first { it.className !in fqcnIgnore }
        .let(::createStackElementTag), throwable: Throwable? = null
) {
    Timber.tag(tag).d(throwable, this)
}

/**
 * 信息日志
 */
fun String.iLog(
    tag: String = Throwable().stackTrace
        .first { it.className !in fqcnIgnore }
        .let(::createStackElementTag), throwable: Throwable? = null
) {
    Timber.tag(tag).i(throwable, this)
}

/**
 * 警告日志
 */
fun String.wLog(
    tag: String = Throwable().stackTrace
        .first { it.className !in fqcnIgnore }
        .let(::createStackElementTag), throwable: Throwable? = null
) {
    Timber.tag(tag).w(throwable, this)
}

/**
 * 错误日志
 */
fun String.eLog(
    tag: String = Throwable().stackTrace
        .first { it.className !in fqcnIgnore }
        .let(::createStackElementTag), throwable: Throwable? = null
) {
    Timber.tag(tag).e(throwable, this)
}

/**
 * 异常Log
 */
fun Throwable?.eLog() {
    Timber.e(this)
}

/**
 * Json格式打印日志
 */
fun String.jsonLog() {
    if (this.isEmpty()) {
        "Empty/Null json content".dLog()
        return
    }
    try {
        if (this.trim().startsWith("{")) {
            val jsonObject = JSONObject(this)
            val message = jsonObject.toString(2)
            message.dLog()
            return
        }
        if (this.trim().startsWith("[")) {
            val jsonArray = JSONArray(this)
            val message = jsonArray.toString(2)
            message.dLog()
            return
        }
        "Invalid Json".eLog()
    } catch (e: JSONException) {
        e.eLog()
    }
}

/**
 * 获取日志目录
 */
fun Context.getLogFile(): File {
    return File(cacheDir, LOG_DIR_NAME)
}

/**
 * 压缩文件
 */
fun zipFolder(srcFileString: String, zipFileString: String): String {
    val path = zipFileString + "" + System.currentTimeMillis().timeFormatDate("yyyyMMdd") + ".zip"
    //创建ZIP
    try {
        val outZip = ZipOutputStream(FileOutputStream(File(path)))
        //创建文件
        val file = File(srcFileString)
        //压缩
        zipFiles(file.parent?.plus(File.separator) ?: "", file.name, outZip)
        //完成和关闭
        outZip.finish()
        outZip.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return path
}

private fun zipFiles(
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
                    zipFiles("$folderString$fileString/", fileList[i], zipOutputSteam)
                }
            }
        }
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
}

/**
 * 分享文件
 */
fun shareFile(context: Context, fileName: String) {
    val file = File(fileName)
    if (file.exists()) {
        val share = Intent(Intent.ACTION_SEND)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val contentUri: Uri =
                FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
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

