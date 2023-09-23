package com.caspar.base.utils.log


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import com.caspar.base.ext.timeFormatDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

//日志文件夹名称
const val LOG_DIR_NAME = "Logs"

//当前写入日志名称
const val LOG_FILE_NAME = "app_logs.txt"

//下一次日志距离上一次时间间隔，超出则将当前缓存区日志存入本地沙盒
const val FLUSH_INTERVAL = 1000

/**
 * 写入本地文件的日志工具类
 * @param maxLogFileSize 一个文件最大写入多少日志
 * @param maxLogFiles 最多写入多少个文件
 */
fun Context.createFileLoggingTree(
    maxLogFileSize: Long = 1 * 1024 * 1024,
    maxLogFiles: Int = 10
): Timber.Tree {
    return FileLoggingTree(
        context = this,
        maxLogFileSize = maxLogFileSize,
        maxLogFiles = maxLogFiles
    )
}

// Verbose 日志
fun String.vLog(throwable: Throwable? = null) {
    Timber.v(throwable, this)
}

// Debug 日志
fun String.dLog(throwable: Throwable? = null) {
    Timber.d(throwable, this)
}

// 信息日志
fun String.iLog(throwable: Throwable? = null) {
    Timber.i(throwable, this)
}

// 警告日志
fun String.wLog(throwable: Throwable? = null) {
    Timber.w(throwable, this)
}

// 错误日志
fun String.eLog(throwable: Throwable? = null) {
    Timber.e(throwable, this)
}

// 异常Log
fun Throwable?.eLog() {
    Timber.e(this)
}

fun Context.getLogFile(): File {
    return File(cacheDir, LOG_DIR_NAME)
}

fun ZipFolder(srcFileString: String, zipFileString: String): String {
    val path = zipFileString + "" + System.currentTimeMillis().timeFormatDate("yyyyMMdd") + ".zip"
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

/**
 * 写入本地文件的日志工具类
 * @param maxLogFileSize 一个文件最大写入多少日志 默认单个日志文件为1MB
 * @param maxLogFiles 最多写入多少个文件
 */
private class FileLoggingTree(
    private val context: Context,
    private val maxLogFileSize: Long = 1 * 1024 * 1024,
    private val maxLogFiles: Int = 10
) : Timber.DebugTree() {
    private var job = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val logBuffer = StringBuilder()
    private var lastFlushTime = System.currentTimeMillis()
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        super.log(priority, tag, message, t)
        job.launch(Dispatchers.IO) {
            appendLogLine(priority, tag, message, t)
        }
    }

    private fun appendLogLine(priority: Int, tag: String?, message: String, t: Throwable?) {
        synchronized(logBuffer) {
            val logLine = formatLogLine(priority, tag, message, t)
            logBuffer.append(logLine)
            logBuffer.append('\n')

            val currentTime = System.currentTimeMillis()
            if ((currentTime - lastFlushTime) >= FLUSH_INTERVAL) {
                flushLogBuffer()
                lastFlushTime = currentTime
            }
        }
    }

    private fun flushLogBuffer() {
        val logFile = context.createLogFile()
        if (logFile != null) {
            try {
                BufferedWriter(FileWriter(logFile, true)).use { writer ->
                    writer.write(logBuffer.toString())
                    writer.flush()
                    logBuffer.setLength(0)
                }
            } catch (e: IOException) {
                Timber.e(e, "Failed to write log to file")
            }
        }
    }

    private fun Context.createLogFile(): File? {
        val logDir = File(cacheDir, LOG_DIR_NAME)
        if (!logDir.exists() && !logDir.mkdirs()) {
            Timber.e("Failed to create log directory: ${logDir.absolutePath}")
            return null
        }
        cleanUpOldLogFiles(logDir)
        val logFileName = "log_${System.currentTimeMillis().timeFormatDate("yyyyMMddHHmmss")}.txt"
        val file = File(logDir, LOG_FILE_NAME)
        if (file.exists() && file.length() >= maxLogFileSize) {
            file.renameTo(File(logDir, logFileName))
        }
        return File(logDir, LOG_FILE_NAME)
    }

    /**
     * 倒叙排列，将旧数据移除，仅保留最新十个日志文件
     */
    private fun cleanUpOldLogFiles(logDir: File) {
        val logFiles = logDir.listFiles { _, name ->
            name.startsWith("log_") && name.endsWith(".txt")
        }
        logFiles?.apply {
            if (size > maxLogFiles) {
                sortedByDescending { it.lastModified() }.subList(maxLogFiles, size)
                    .forEach { file ->
                        if (!file.delete()) {
                            Timber.e("Failed to delete log file: ${file.absolutePath}")
                        }
                    }
            }
        }
    }

    private fun formatLogLine(priority: Int, tag: String?, message: String, t: Throwable?): String {
        val priorityStr = LogPriority.fromPriority(priority).name
        val tagStr = tag ?: "NULL"
        val logLine = StringBuilder()
            .append("[")
            .append(System.currentTimeMillis().timeFormatDate())
            .append("] [")
            .append(priorityStr)
            .append("] [")
            .append(tagStr)
            .append("] ")
            .append(message)
        t?.let {
            logLine.append("\n").append(Log.getStackTraceString(t))
        }
        return logLine.toString()
    }

    private enum class LogPriority(val priority: Int) {
        VERBOSE(Log.VERBOSE),
        DEBUG(Log.DEBUG),
        INFO(Log.INFO),
        WARN(Log.WARN),
        ERROR(Log.ERROR),
        ASSERT(Log.ASSERT);

        companion object {
            fun fromPriority(priority: Int): LogPriority {
                return values().find { it.priority == priority } ?: VERBOSE
            }
        }
    }
}