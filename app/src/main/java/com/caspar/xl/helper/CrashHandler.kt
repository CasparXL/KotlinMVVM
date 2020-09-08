package com.caspar.xl.helper

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.pm.PackageInfoCompat
import com.caspar.base.helper.LogUtil
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CrashHandler private constructor(context: Context) : Thread.UncaughtExceptionHandler {
    // 上下文对象
    private val mContext = context

    // 用于存放参数信息
    private val info = LinkedHashMap<String, String>()

    // 用于格式化日期
    private val mDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    // 单例模式
    companion object {
        private var instance: CrashHandler? = null
        fun getInstance(context: Context): CrashHandler? {
            if (instance == null) {
                synchronized(CrashHandler::class) {
                    instance = CrashHandler(context)
                }
            }
            return instance
        }
    }

    /**
     * 构造初始化
     */
    init {
        // 设置当前类为应用默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    /**
     * 当 UncaughtException 发生时转入该函数
     * @param t
     * @param e
     */
    override fun uncaughtException(t: Thread, e: Throwable) {
        try {
            handleException(e)
            Thread.sleep(2000)
        } catch (ex: Exception) {
        }
        /* val i: Intent? = mContext.packageManager.getLaunchIntentForPackage(mContext.packageName)
        i?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        mContext.startActivity(i)
        Process.killProcess(Process.myPid())
        exitProcess(0)*/
        killAppProcess()
    }
    
    private fun killAppProcess() {
        val appTaskList : ActivityManager = mContext.applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (appTask in appTaskList.appTasks) {
            appTask.finishAndRemoveTask()
        }
    }
    
    /**
     * 自定义错误处理
     * @param e
     */
    private fun handleException(e: Throwable) {
        val error = getLogSummary(e)
        LogUtil.e(error)
        val mydir = File(mContext.externalCacheDir, "/CrashLog")
        if (!mydir.exists()) {
            mydir.mkdirs()
        }
        saveSettingFile(mydir.absolutePath + "/" + "CrashLog.txt", error)
        /**
         * 这里可以执行一些业务操作
         * 比如保存崩溃日志到文件等等
         */
    }

    fun saveSettingFile(fileName: String, content: String) {
        var fos: FileOutputStream? = null
        val file = File(fileName)
        try {
            fos = FileOutputStream(file)
            val buffer = content.toByteArray()
            fos.write(buffer)
            fos.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * 收集参数信息
     * @param context
     */
    private fun putInfoToMap(context: Context) {
        info["设备型号"] = Build.MODEL
        info["设备品牌"] = Build.BOARD
        info["硬件名称"] = Build.HARDWARE
        info["硬件制造商"] = Build.MANUFACTURER
        info["系统版本"] = Build.VERSION.RELEASE
        info["系统版本号"] = "${Build.VERSION.SDK_INT}"
        val pm = context.packageManager
        val pi = pm.getPackageInfo(context.packageName, PackageManager.GET_ACTIVITIES)
        if (pi != null) {
            info["应用版本"] = pi.versionName
            info["应用版本号"] = "${PackageInfoCompat.getLongVersionCode(pi)}"
        }
    }

    /**
     * 获取日志头信息
     * @return StringBuffer
     */
    private fun getLogHeader(): StringBuffer {
        val sb = StringBuffer()
        sb.append(">>>>时间: ${mDateFormat.format(Date())}\n")
        putInfoToMap(mContext)
        info.entries.forEach {
            sb.append("${it.key}: ${it.value}\n")
        }
        return sb
    }

    /**
     * 获取日志概要
     * @param e
     * @return 日志概要
     */
    private fun getLogSummary(e: Throwable): String {
        val sb = getLogHeader().append("\n")
        sb.append("异常类: ${e.javaClass}\n")
        sb.append("异常信息: ${e.message}\n\n")
        for (i in e.stackTrace.indices) {
            sb.append("****堆栈追踪 ${i + 1}\n")
            sb.append("类名: ${e.stackTrace[i].className}\n")
            sb.append("方法: ${e.stackTrace[i].methodName}\n")
            sb.append("文件: ${e.stackTrace[i].fileName}\n")
            sb.append("行数: ${e.stackTrace[i].lineNumber}\n")
            sb.append("详情: (${e.stackTrace[i].fileName}:${e.stackTrace[i].lineNumber})\n\n")
        }
        return sb.toString().trim()
    }
}
