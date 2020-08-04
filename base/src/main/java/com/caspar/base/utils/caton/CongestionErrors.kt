package com.caspar.xl.utils.caton

import android.os.Looper
import java.util.*
import kotlin.Comparator

class CongestionErrors private constructor(threadStackInfo: ThreadStackInfoWrapper.ThreadStackInfo) : Error(threadStackInfo.message,threadStackInfo.fillInStackTrace()) {
    private class ThreadStackInfoWrapper(private val nameAndState: String, private val stackTraceElements: Array<StackTraceElement>) {
        inner class ThreadStackInfo(throwable: Throwable?) : Throwable(nameAndState, throwable) {
            @Synchronized
            override fun fillInStackTrace(): Throwable {
                stackTrace = stackTraceElements
                return this
            }
        }
    }

    companion object {
        fun getAllThread(): CongestionErrors {
            val uiThread = Looper.getMainLooper().thread
            val stackTraceElementMap: MutableMap<Thread, Array<StackTraceElement>?> = TreeMap(Comparator { lhs: Thread, rhs: Thread ->
                when {
                    lhs === rhs      -> {
                        return@Comparator 0
                    }
                    lhs === uiThread -> {
                        return@Comparator 1
                    }
                    rhs === uiThread -> {
                        return@Comparator -1
                    }
                    else             -> rhs.name.compareTo(lhs.name)
                }
            })
            for ((key, value) in Thread.getAllStackTraces()) {
                if (value.isNotEmpty()) {
                    stackTraceElementMap[key] = value
                }
            }
            //Fix有时候Thread.getAllStackTraces()不包含UI线程的问题
            if (!stackTraceElementMap.containsKey(uiThread)) {
                stackTraceElementMap[uiThread] = uiThread.stackTrace
            }
            var threadStackInfo: ThreadStackInfoWrapper.ThreadStackInfo? = null
            for ((key, value) in stackTraceElementMap) {
                threadStackInfo = value?.let { ThreadStackInfoWrapper(getThreadNameAndState(key), it).ThreadStackInfo(threadStackInfo) }
            }
            if (threadStackInfo == null) {
                threadStackInfo = ThreadStackInfoWrapper("unKnownThread-state-unKnownState", arrayOf()).ThreadStackInfo(threadStackInfo)
            }
            return CongestionErrors(threadStackInfo)
        }

        fun getUiThread(): CongestionErrors {
            val uiThread = Looper.getMainLooper().thread
            val stackTraceElements = uiThread.stackTrace
            val threadStackInfo = ThreadStackInfoWrapper(getThreadNameAndState(uiThread), stackTraceElements).ThreadStackInfo(null)
            return CongestionErrors(threadStackInfo)
        }

        private fun getThreadNameAndState(thread: Thread): String {
            return thread.name + "-state-" + thread.state
        }
    }

}