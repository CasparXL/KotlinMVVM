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
     * 默认是3，这是工具类配套使用好的，如果需要单独设置，请做测试到你需要的下标以达到你需要的文字
     * Gets the index of stack information for the number of file lines
     * The default is 3, which is used with the tool class. If it needs to be set separately, please test to the subscript you need to achieve the text you need
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
    fun init(debug: Boolean, Tag: String) {
        LogUtil.debug = debug
        LogUtil.mTag = Tag
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
    fun content(index: Int, msg: String?): String {
        val stackTraceElement = Throwable().stackTrace[index]
        val clazz = stackTraceElement.fileName
        val method = stackTraceElement.methodName
        val line = stackTraceElement.lineNumber
        return "[thread:${Thread.currentThread().name}]\u21E2[$method()]\u21E2($clazz:$line)\u21E2 $msg"
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
            val disclaimer = " [stack trace too large]"
            stackTraceString = stackTraceString.substring(
                0, MAX_STACK_TRACE_SIZE - disclaimer.length
            ) + disclaimer
        }
        Log.e(mTag, content(index = index, msg = stackTraceString))
    }

    fun v(msg: String?, index: Int = mIndex) {
        if (debug) {
            Log.v(mTag, content(index = index, msg = msg))
        }
    }

    fun d(msg: String?, index: Int = mIndex) {
        if (debug) {
            Log.d(mTag, content(index = index, msg = msg))
        }
    }

    fun i(msg: String?, index: Int = mIndex) {
        if (debug) {
            Log.i(mTag, content(index = index, msg = msg))
        }
    }

    fun w(msg: String?, index: Int = mIndex) {
        if (debug) {
            Log.w(mTag, content(index = index, msg = msg))
        }
    }

    fun wtf(msg: String?, index: Int = mIndex) {
        if (debug) {
            Log.wtf(mTag, content(index = index, msg = msg))
        }
    }

    fun e(msg: String?, index: Int = mIndex) {
        if (debug) {
            Log.e(mTag, content(index = index, msg = msg))
        }
    }

    /**
     * 打印json格式文本
     * Print text in JSON format
     */
    fun json(msg: String?, index: Int = mIndex) {
        if (msg.isNullOrEmpty()) {
            e("Json is Empty", index + 1)
            return
        }
        var message: String
        message = try {
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
        i("╔═══════════════════════════════════════════════════════════════════════════════════════", index = index + 1)
        message = "Json:$LINE_SEPARATOR$message"
        val lines = message.split(LINE_SEPARATOR).toTypedArray()
        for (line in lines) {
            i(msg = "║ $line", index = index + 1)
        }
        i("╚═══════════════════════════════════════════════════════════════════════════════════════", index = index + 1)
    }

    /**
     * 打印xml格式文本
     * Print text in XML format
     */
    fun xml(xml: String?, index: Int = mIndex) {
        var xml = xml
        if (xml != null) {
            xml = formatXML(xml)
            xml = "Xml:\n$xml"
        } else {
            xml = "Xml:End"
        }
        e("╔═══════════════════════════════════════════════════════════════════════════════════════", index = index + 1)
        val lines = xml.split(LINE_SEPARATOR).toTypedArray()
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
