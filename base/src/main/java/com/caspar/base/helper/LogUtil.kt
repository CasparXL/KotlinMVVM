package com.caspar.base.helper

import android.text.TextUtils
import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.PrintWriter
import java.io.StringReader
import java.io.StringWriter
import javax.xml.transform.OutputKeys
import javax.xml.transform.Source
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

/**
 * User: CasparXL
 * Description: logUtil
 */
object LogUtil {
    /**
     *  是否打印日志的标识位
     *  Whether to print the identity bit of the log
     */
    private var debug = false

    /**
     * 文本tag，默认为 `⇢`
     *  The text in the tag, the default for ` ⇢ `
     */
    private var mTag = "\u21E2"

    /**
     * 获取文件行数的堆栈信息下标
     * Gets the index of stack information for the number of file lines
     */
    private var mIndex = 3

    /**
     * 打印json，用于做节点分割
     * Print JSON for node segmentation
     */
    private val LINE_SEPARATOR = System.getProperty("line.separator").toString()

    /**
     * 初始化日志基本参数
     * Initialize log base parameters
     *
     * @param debug 是否打印       Whether or not to print
     * @param Tag 日志Tag         Log Tag
     */
    fun init(isPrint: Boolean, Tag: String) {
        debug = isPrint
        mTag = Tag
    }

    /**
     * 打印文本格式:[线程:Thread.currentThread()] -> [方法名称:@param method] -> [所处文件行数:@param clazz:@param line] -> 正文
     *
     *  Print text format :[Thread: thread.currentThread ()] -> [method name :@param method] -> [line number :@param clazz:@param line] -> body
     *  示例(sample) ：
     *  class MainActivity{
     *   fun test(){
     *       LogUtil.e("test")
     *   }
     *  }
     * Logcat -----> [thread:main]⇢[test()]⇢(MainActivity.kt:124) -> test
     */
    fun content(index: Int, optionalTag: String? = null, msg: String?): String {
        val stackTraceElement = Throwable().stackTrace[index] //调用代码的堆栈
        val clazz = stackTraceElement.fileName //类名称
        val line = stackTraceElement.lineNumber //代码行数
        val secondTag = if (optionalTag == mTag) "" else "[$optionalTag]\u21E2"
        return "$secondTag[thread:${Thread.currentThread().name}]\u21E2($clazz:$line)\u21E2 $msg"
    }

    /**
     * 打印Throwable的详情堆栈信息
     * Print Throwable details stack information
     */
    fun e(throwable: Throwable, index: Int = mIndex) {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        throwable.printStackTrace(pw)
        var stackTraceString = sw.toString()
        val MAX_STACK_TRACE_SIZE = 131071 //128 KB - 1
        //Reduce data to 128KB so we don't get a TransactionTooLargeException when sending the intent.
        //The limit is 1MB on Android but some devices seem to have it lower.
        //See: http://developer.android.com/reference/android/os/TransactionTooLargeException.html
        //And: http://stackoverflow.com/questions/11451393/what-to-do-on-transactiontoolargeexception#comment46697371_12809171
        if (stackTraceString.length > MAX_STACK_TRACE_SIZE) {
            val disclaimer = "[stack trace too large]"
            stackTraceString = stackTraceString.substring(0, MAX_STACK_TRACE_SIZE - disclaimer.length) + disclaimer
        }
        Log.e(mTag, content(index = index, optionalTag = mTag, msg = stackTraceString))
    }

    fun v(msg: String?, optionalTag: String? = mTag, index: Int = mIndex) {
        if (debug) {
            printLog(content(index = index, optionalTag = optionalTag, msg = msg)) {
                Log.v(mTag, it)
            }
        }
    }

    fun d(msg: String?, optionalTag: String? = mTag, index: Int = mIndex) {
        if (debug) {
            printLog(content(index = index, optionalTag = optionalTag, msg = msg)) {
                Log.d(mTag, it)
            }
        }
    }

    fun i(msg: String?, optionalTag: String? = mTag, index: Int = mIndex) {
        if (debug) {
            printLog(content(index = index, optionalTag = optionalTag, msg = msg)) {
                Log.i(mTag, it)
            }
        }
    }

    fun w(msg: String?, optionalTag: String? = mTag, index: Int = mIndex) {
        if (debug) {
            printLog(content(index = index, optionalTag = optionalTag, msg = msg)) {
                Log.w(mTag, it)
            }
        }
    }

    fun wtf(msg: String?, optionalTag: String? = mTag, index: Int = mIndex) {
        if (debug) {
            printLog(content(index = index, optionalTag = optionalTag, msg = msg)) {
                Log.wtf(mTag, it)
            }
        }
    }

    fun e(msg: String?, optionalTag: String? = mTag, index: Int = mIndex) {
        if (debug) {
            printLog(content(index = index, optionalTag = optionalTag, msg = msg)) {
                Log.e(mTag, it)
            }
        }
    }

    /**
     * 用于打印长数据Log,如果Log内容超出Logcat一行4K的字符数时，跨行打印
     */
    private fun printLog(msg: String, level: (body: String) -> Unit) {
        if (msg.length > 4000) {
            var i = 0
            while (i < msg.length) {
                //当前截取的长度<总长度则继续截取最大的长度来打印
                if (i + 4000 < msg.length) {
                    level.invoke(msg.substring(i, i + 4000))
                } else {
                    //当前截取的长度已经超过了总长度，则打印出剩下的全部信息
                    level.invoke(msg.substring(i, msg.length))
                }
                i += 4000
            }
        } else {
            //直接打印
            level.invoke(msg)
        }
    }

    /**
     * 打印json格式文本
     * Print text in JSON format
     */
    fun json(msg: String?, tag: String? = mTag, index: Int = mIndex) {
        if (msg.isNullOrEmpty()) {
            e(msg = "Json is Empty", optionalTag = tag, index = index + 1)
            return
        }
        var message: String = try {
            when {
                msg.startsWith("{") -> {
                    val jsonObject = JSONObject(msg)
                    jsonObject.toString(4) //最重要的方法，就一行，返回格式化的json字符串，其中的数字4是缩进字符数
                }
                msg.startsWith("[") -> {
                    val jsonArray = JSONArray(msg)
                    jsonArray.toString(4)
                }
                else                -> {
                    msg
                }
            }
        } catch (e: JSONException) {
            msg
        }
        i("╔═══════════════════════════════════════════════════════════════════════════════════════", optionalTag = tag, index = index + 1)
        message = "Json:$LINE_SEPARATOR$message"
        val lines = message.split(LINE_SEPARATOR).toTypedArray()
        for (line in lines) {
            i(msg = "║ $line", optionalTag = tag, index = index + 1)
        }
        i("╚═══════════════════════════════════════════════════════════════════════════════════════", optionalTag = tag, index = index + 1)
    }

    /**
     * 打印xml格式文本
     * Print text in XML format
     */
    fun xml(xml: String?, index: Int = mIndex) {
        var mXml = xml
        if (mXml != null) {
            mXml = formatXML(mXml)
            mXml = "Xml:\n$mXml"
        } else {
            mXml = "Xml:End"
        }
        e("╔═══════════════════════════════════════════════════════════════════════════════════════", index = index + 1)
        val lines = mXml.split(LINE_SEPARATOR).toTypedArray()
        for (line in lines) {
            if (!TextUtils.isEmpty(line)) {
                e("║ $line")
            }
        }
        e("╚═══════════════════════════════════════════════════════════════════════════════════════", index = index + 1)
    }

    private fun formatXML(inputXML: String): String {
        return try {
            val xmlInput: Source = StreamSource(StringReader(inputXML))
            val xmlOutput = StreamResult(StringWriter())
            val transformer = TransformerFactory.newInstance().newTransformer()
            transformer.setOutputProperty(OutputKeys.INDENT, "yes")
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
            transformer.transform(xmlInput, xmlOutput)
            xmlOutput.writer.toString().replaceFirst(">".toRegex(), ">\n")
        } catch (e: Exception) {
            e.printStackTrace()
            inputXML
        }
    }

}
