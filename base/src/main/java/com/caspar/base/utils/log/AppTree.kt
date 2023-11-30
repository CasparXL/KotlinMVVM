package com.caspar.base.utils.log

import android.os.Build
import android.util.Log
import timber.log.Timber
import java.util.regex.Pattern

open class AppTree : Timber.Tree() {

    /**
     * Break up `message` into maximum-length chunks (if needed) and send to either
     * [Log.println()][Log.println] or
     * [Log.wtf()][Log.wtf] for logging.
     *
     * {@inheritDoc}
    */
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
      if (message.length < MAX_LOG_LENGTH) {
        if (priority == Log.ASSERT) {
          Log.wtf(tag, message)
        } else {
          Log.println(priority, tag, message)
        }
        return
      }

      // Split by line, then ensure each line can fit into Log's maximum length.
      var i = 0
      val length = message.length
      while (i < length) {
        var newline = message.indexOf('\n', i)
        newline = if (newline != -1) newline else length
        do {
          val end = Math.min(newline, i + MAX_LOG_LENGTH)
          val part = message.substring(i, end)
          if (priority == Log.ASSERT) {
            Log.wtf(tag, part)
          } else {
            Log.println(priority, tag, part)
          }
          i = end
        } while (i < newline)
        i++
      }
    }


    companion object {
        private const val MAX_LOG_LENGTH = 4000
        private const val MAX_TAG_LENGTH = 23

        /**
         * 创建独特化tag
         * @param element 堆栈信息
         */
        fun createStackElementTag(element: StackTraceElement): String {
            var tag = element.className.substringAfterLast('.')
            /*val m = Pattern.compile("(\\$\\d+)+$").matcher(tag)
            if (m.find()) {
                //比如[类名$方法名$xxx$xxx]，会变成[类名$方法名]
                tag = m.replaceAll("")
            }*/
            //比如[类名$方法名$xxx$xxx]，会变成[类名]
            tag = tag.substringBefore("$")
            // Tag length limit was removed in API 26.
            return if (tag.length <= MAX_TAG_LENGTH || Build.VERSION.SDK_INT >= 26) {
                tag
            } else {
                tag.substring(0, MAX_TAG_LENGTH)
            }.plus(":${element.lineNumber}")
        }
    }
  }