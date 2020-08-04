package com.caspar.xl.utils.caton

import android.annotation.SuppressLint
import android.content.Context
import android.os.Debug
import android.os.Environment
import android.os.Handler
import android.os.Looper
import com.caspar.base.helper.LogUtil
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.PrintStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * CongestionCheck
 * 使用线程轮询的方式监听App是否产生卡顿
 * 代码来自：https://github.com/D-clock/AndroidPerformanceTools
 *
 * 使用方法：
 * 在application中使用：
 * CongestionCheck.initialize(CongestionCheck.Builder(this).setFrequency(1000) //超过1s为卡顿
 *   .setIgnoreDebugger(true) //是否忽略调试引起的卡顿
 *   .setReportAllThreadInfo(true) //是否打印出卡顿发生时所有线程的堆栈信息
 *   .setSaveLog(true) //是否保存到log信息到文件
 *   .setOnBlockListener(object : CongestionCheck.OnBlockListener {
 *   //当卡顿发生时的回调接口，回调过程发生在异步线程中
 *   override fun onBlock(congestionError: CongestionErrors) {
 *   LogUtil.e(congestionError)
 *   }
 *   }).build())
 *
 *   在activity中使用：
 *    oncreate中使用启动监听
 *    CongestionCheck.congestionCheck?.start()
 *    在你认为卡顿完之后调用
 *    CongestionCheck.congestionCheck?.stop()
 *
 *    之后如果真的有卡顿，在控制台会有打印，打印日志的tag取决于你打印日志的工具类tag
 *
 */
class CongestionCheck : Runnable {
    private var appContext: Context? = null
    private val uiHandler = Handler(Looper.getMainLooper())

    @Volatile
    private var tickCounter = 0
    private val ticker = Runnable { tickCounter = (tickCounter + 1) % Int.MAX_VALUE }
    private var frequency: Long = 0
    private var ignoreDebugger = false
    private var reportAllThreadInfo = false
    private var saveLog = false
    private var onBlockListener: OnBlockListener? = null
    private var isStop = true

    private fun init(configuration: Configuration) {
        appContext = configuration.appContext
        frequency = if (configuration.frequency < MIN_FREQUENCY) MIN_FREQUENCY else configuration.frequency
        ignoreDebugger = configuration.ignoreDebugger
        reportAllThreadInfo = configuration.reportAllThreadInfo
        onBlockListener = configuration.onBlockListener
        saveLog = configuration.saveLog
    }

    override fun run() {
        var lastTickNumber: Int
        while (!isStop) {
            lastTickNumber = tickCounter
            uiHandler.post(ticker)
            try {
                Thread.sleep(frequency)
            } catch (e: InterruptedException) {
                e.printStackTrace()
                break
            }
            if (lastTickNumber == tickCounter) {
                if (!ignoreDebugger && Debug.isDebuggerConnected()) {
                    LogUtil.e("当前由调试模式引起消息阻塞引起ANR，可以通过setIgnoreDebugger(true)来忽略调试模式造成的ANR")
                    continue
                }
                val congestionError: CongestionErrors = if (!reportAllThreadInfo) {
                    CongestionErrors.getUiThread()
                } else {
                    CongestionErrors.getAllThread()
                }
                onBlockListener?.onBlock(congestionError)
                if (saveLog) {
                    if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
                        val logDir = logDirectory
                        saveLogToSdcard(congestionError, logDir)
                    } else {
                        LogUtil.e("sdcard is unmounted")
                    }
                }
            }
        }
    }

    private fun saveLogToSdcard(congestionError: CongestionErrors?, dir: File?) {
        if (congestionError == null) {
            return
        }
        if (dir != null && dir.exists() && dir.isDirectory) {
            val fileName = logFileName
            val logFile = File(dir, fileName)
            if (!logFile.exists()) {
                try {
                    logFile.createNewFile()
                    val printStream = PrintStream(FileOutputStream(logFile, false), true)
                    congestionError.printStackTrace(printStream)
                    printStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private val logDirectory: File?
        get() {
            val cacheDir = appContext?.externalCacheDir
            if (cacheDir != null) {
                val logDir = File(cacheDir, "block")
                LogUtil.e("写入文件位置$logDir")
                return if (!logDir.exists()) {
                    val successful = logDir.mkdirs()
                    if (successful) {
                        logDir
                    } else {
                        null
                    }
                } else {
                    logDir
                }
            }
            return null
        }
    private val logFileName: String
        get() {
            val timeStampString = DATE_FORMAT.format(Date())
            return "$timeStampString.trace"
        }

    @Synchronized
    fun start() {
        if (isStop) {
            isStop = false
            val blockThread = Thread(this)
            blockThread.name = LOOPER_NAME
            blockThread.start()
        }
    }

    @Synchronized
    fun stop() {
        if (!isStop) {
            isStop = true
        }
    }

    class Builder(private val appContext: Context) {
        private var frequency: Long = 0
        private var ignoreDebugger = false
        private var reportAllThreadInfo = false
        private var saveLog = false
        private var onBlockListener: OnBlockListener? = null
        fun setFrequency(frequency: Long): Builder {
            this.frequency = frequency
            return this
        }

        /**
         * 设置是否忽略debugger模式引起的卡顿
         *
         * @param ignoreDebugger
         * @return
         */
        fun setIgnoreDebugger(ignoreDebugger: Boolean): Builder {
            this.ignoreDebugger = ignoreDebugger
            return this
        }

        /**
         * 设置发生卡顿时，是否上报所有的线程信息，默认是false
         *
         * @param reportAllThreadInfo
         * @return
         */
        fun setReportAllThreadInfo(reportAllThreadInfo: Boolean): Builder {
            this.reportAllThreadInfo = reportAllThreadInfo
            return this
        }

        fun setSaveLog(saveLog: Boolean): Builder {
            this.saveLog = saveLog
            return this
        }

        /**
         * 设置发生卡顿时的回调
         *
         * @param onBlockListener
         * @return
         */
        fun setOnBlockListener(onBlockListener: OnBlockListener?): Builder {
            this.onBlockListener = onBlockListener
            return this
        }

        fun build(): Configuration {
            val configuration = Configuration()
            configuration.appContext = appContext
            configuration.frequency = frequency
            configuration.ignoreDebugger = ignoreDebugger
            configuration.reportAllThreadInfo = reportAllThreadInfo
            configuration.saveLog = saveLog
            configuration.onBlockListener = onBlockListener
            return configuration
        }

    }

    class Configuration(var appContext: Context? = null, var frequency: Long = 0, var ignoreDebugger: Boolean = false, var reportAllThreadInfo: Boolean = false, var saveLog: Boolean = false, var onBlockListener: OnBlockListener? = null)

    interface OnBlockListener {
        /**
         * 发生ANR时产生回调(在非UI线程中回调)
         *
         * @param congestionError
         */
        fun onBlock(congestionError: CongestionErrors)
    }

    companion object {
        private val TAG = CongestionCheck::class.java.simpleName
        private const val LOOPER_NAME = "block-looper-thread"

        @SuppressLint("SimpleDateFormat")
        private val DATE_FORMAT = SimpleDateFormat("yyyyMMdd-HH-mm-ss")

        /**
         * 最小的轮询频率（单位：ms）
         */
        private const val MIN_FREQUENCY: Long = 500
        private var sLooper: CongestionCheck? = null
        fun initialize(configuration: Configuration) {
            if (sLooper == null) {
                synchronized(CongestionCheck::class.java) {
                    if (sLooper == null) {
                        sLooper = CongestionCheck()
                    }
                }
                sLooper?.init(configuration)
            }
        }

        val congestionCheck: CongestionCheck?
            get() {
                checkNotNull(sLooper) { "未使用initialize方法初始化BlockLooper" }
                return sLooper
            }
    }
}