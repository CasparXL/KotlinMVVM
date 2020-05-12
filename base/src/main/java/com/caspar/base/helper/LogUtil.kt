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
 * User: Tenz Liu
 * Date: 2017-08-21
 * Time: 12-10
 * Description: Log日志打印工具类
 */
object LogUtil {

    var debug = false
    var Tag = "\u21E2"
    var LINE_SEPARATOR: String? = null


    /**
     * 初始化
     *
     * @param debug
     */
    fun init(debug: Boolean, Tag: String) {
        LogUtil.debug = debug
        LogUtil.Tag = Tag
        if (null == LINE_SEPARATOR) {
            LINE_SEPARATOR =
                System.getProperty("line.separator")
        }
    }
    //打印异常信息的
    fun e(throwable:Throwable){
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
                0,
                MAX_STACK_TRACE_SIZE - disclaimer.length
            ) + disclaimer
        }
        e(stackTraceString)
    }
    /**
     * 打印日志(Verbose)
     *
     * @param msg 内容
     */
    fun v(msg: String?) {
        if (debug) {
            Log.v(Tag, "\u21E2" + msg)
        }
    }

    /**
     * 打印日志(Debug)
     *
     * @param msg 内容
     */
    fun d(msg: String?) {
        if (debug) {
            Log.d(Tag, "\u21E2" + msg)
        }
    }

    /**
     * 打印日志(Info)
     *
     * @param msg 内容
     */
    fun i(msg: String?) {
        if (debug) {
            Log.i(Tag, "\u21E2" + msg)
        }
    }

    /**
     * 打印日志(Warm)
     *
     * @param msg 内容
     */
    fun w(msg: String?) {
        if (debug) {
            Log.w(Tag, "\u21E2" + msg)
        }
    }

    /**
     * 打印日志(wtf)
     *
     * @param msg 内容
     */
    fun wtf(msg: String?) {
        if (debug) {
            Log.wtf(Tag, "\u21E2" + msg)
        }
    }

    /**
     * 打印日志(Error)
     *
     * @param msg 内容
     */
    fun e(msg: String?) {
        if (debug) {
            Log.e(Tag, "\u21E2" + msg)
        }
    }

    /**
     * 打印Json的辅助线
     *
     * @param isTop 是否顶部
     */
    private fun printLine(isTop: Boolean) {
        if (isTop) {
            e("╔═══════════════════════════════════════════════════════════════════════════════════════")
        } else {
            e("╚═══════════════════════════════════════════════════════════════════════════════════════")
        }
    }

    /**
     * 打印json内容
     *
     * @param msg 内容
     */
    fun json(msg: String?) {
        if (msg.isNullOrEmpty()){
            e("Json is Empty")
            return
        }
        var message: String
        message = try {
            if (msg.startsWith("{")) {
                val jsonObject = JSONObject(msg)
                jsonObject.toString(4) //最重要的方法，就一行，返回格式化的json字符串，其中的数字4是缩进字符数
            } else if (msg.startsWith("[")) {
                val jsonArray = JSONArray(msg)
                jsonArray.toString(4)
            } else {
                msg
            }
        } catch (e: JSONException) {
            msg
        }
        printLine(true)
        message = "Json:$LINE_SEPARATOR$message"
        val lines =
            message.split(LINE_SEPARATOR!!).toTypedArray()
        for (line in lines) {
            e("║ $line")
        }
        printLine(false)
    }

    /**
     * 打印json内容
     *
     * @param msg 内容
     */
    fun jsonI(msg: String?) {
        if (msg.isNullOrEmpty()){
            e("Json is Empty")
            return
        }
        var message: String
        message = try {
            if (msg.startsWith("{")) {
                val jsonObject = JSONObject(msg)
                jsonObject.toString(4) //最重要的方法，就一行，返回格式化的json字符串，其中的数字4是缩进字符数
            } else if (msg.startsWith("[")) {
                val jsonArray = JSONArray(msg)
                jsonArray.toString(4)
            } else {
                msg
            }
        } catch (e: JSONException) {
            msg
        }
        printLine(true)
        message = "Json:$LINE_SEPARATOR$message"
        val lines =
            message.split(LINE_SEPARATOR!!).toTypedArray()
        for (line in lines) {
            i("║ $line")
        }
        printLine(false)
    }

    /**
     * 打印json内容
     *
     * @param msg 内容
     */
    fun jsonD(msg: String?) {
        if (msg.isNullOrEmpty()){
            e("Json is Empty")
            return
        }
        var message: String
        message = try {
            if (msg.startsWith("{")) {
                val jsonObject = JSONObject(msg)
                jsonObject.toString(4) //最重要的方法，就一行，返回格式化的json字符串，其中的数字4是缩进字符数
            } else if (msg.startsWith("[")) {
                val jsonArray = JSONArray(msg)
                jsonArray.toString(4)
            } else {
                msg
            }
        } catch (e: JSONException) {
            msg
        }
        printLine(true)
        message = "Json:$LINE_SEPARATOR$message"
        val lines =
            message.split(LINE_SEPARATOR!!).toTypedArray()
        for (line in lines) {
            d("║ $line")
        }
        printLine(false)
    }

    /**
     * 打印xml
     *
     * @param xml 内容
     */
    fun xml(xml: String?) {
        var xml = xml
        if (xml != null) {
            xml = formatXML(xml)
            xml = "Xml:\n$xml"
        } else {
            xml = "Xml:End"
        }
        printLine(true)
        val lines =
            xml.split(LINE_SEPARATOR!!).toTypedArray()
        for (line in lines) {
            if (!TextUtils.isEmpty(line)) {
                e("║ $line")
            }
        }
        printLine(false)
    }
    private fun formatXML(inputXML: String): String {
        return try {
            val xmlInput: Source =
                StreamSource(StringReader(inputXML))
            val xmlOutput =
                StreamResult(StringWriter())
            val transformer =
                TransformerFactory.newInstance().newTransformer()
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