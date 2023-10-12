package com.caspar.base.utils.log

import android.content.Context
import android.util.Log
import com.caspar.base.ext.timeFormatDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException


/**
 * 写入本地文件的日志工具类
 * @param maxLogFileSize 一个文件最大写入多少日志 默认单个日志文件为1MB
 * @param maxLogFiles 最多写入多少个文件
 */
class FileLoggingTree(
    private val context: Context,
    private val maxLogFileSize: Long = 1 * 1024 * 1024,
    private val maxLogFiles: Int = 10
) : AppTree() {
    private var job = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val logBuffer = StringBuilder()
    private var lastFlushTime: Long = 0
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