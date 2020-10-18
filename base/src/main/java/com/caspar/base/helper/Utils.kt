package com.caspar.base.helper

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.*
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import org.json.JSONArray
import org.json.JSONTokener
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.math.BigInteger
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.RSAPrivateKeySpec
import java.security.spec.RSAPublicKeySpec
import java.text.SimpleDateFormat
import java.util.*

object Utils {
    /**
     * 读取assets本地json
     * @param fileName
     * @param context
     * @return
     */
    fun getJson(fileName: String?, context: Context): String {
        //将json数据变成字符串
        val stringBuilder = StringBuilder()
        try {
            //获取assets资源管理器
            val assetManager = context.assets
            //通过管理器打开文件并读取
            val bf = BufferedReader(InputStreamReader(assetManager.open(fileName!!)))
            var line: String?
            while (bf.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }
            bf.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return stringBuilder.toString()
    }

    /***生成随机32位数，用于辨别命令 */
    val uUID: String
        get() {
            val uuid = UUID.randomUUID()
            val str = uuid.toString()
            return str.toUpperCase(Locale.ROOT)
        }

    /**
     * 设置文本文字和大小以及颜色，分为两段，开始部分和结束部分
     */
    fun textAfter(textView: TextView, start: String, content: String, startSize: Int, endSize: Int, startColor: Int, endColor: Int) {
        val spannableString: SpannableString = SpannableString(start + content)
        spannableString.setSpan(AbsoluteSizeSpan(startSize), 0, start.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(ForegroundColorSpan(startColor), 0, start.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(AbsoluteSizeSpan(endSize), start.length, start.length + content.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(ForegroundColorSpan(endColor), start.length, start.length + content.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        textView.text = spannableString
    }

    //可根据需要自行截取数据显示
    fun getTimeHour(date: Date): String {
        val format = SimpleDateFormat("HH:mm")
        return format.format(date)
    }

    /**
     * 获取时间，AM以及PM
     */
    fun getTimeAP(date: Date): String {
        val format = SimpleDateFormat("KK:mm aa", Locale.ENGLISH)
        return format.format(date)
    }

    /***获取时区 */
    val timeZone: String
        get() {
            val now = Calendar.getInstance()
            return now.timeZone.id //原本
            /* val mDummyDate: Calendar
             mDummyDate = Calendar.getInstance()
             val now = Calendar.getInstance()
             mDummyDate.timeZone = now.timeZone
             mDummyDate[now[Calendar.YEAR], 11, 31, 13, 0] = 0
             return getTimeZoneText(now.timeZone, true)*/
        }

    private fun getTimeZoneText(tz: TimeZone, includeName: Boolean): String {
        val now = Date()
        val gmtFormatter = SimpleDateFormat("ZZZZ")
        gmtFormatter.timeZone = tz
        var gmtString = gmtFormatter.format(now)
        val bidiFormatter = BidiFormatter.getInstance()
        val l = Locale.getDefault()
        val isRtl = TextUtils.getLayoutDirectionFromLocale(l) == View.LAYOUT_DIRECTION_RTL
        gmtString = bidiFormatter.unicodeWrap(gmtString, if (isRtl) TextDirectionHeuristics.RTL else TextDirectionHeuristics.LTR)
        return if (!includeName) {
            gmtString
        } else gmtString
    }

    /***将时间戳转换为时间 */
    fun timeToString(timeMillis: Long): String {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date = Date(timeMillis)
        return simpleDateFormat.format(date)
    }

    /**
     * 左侧补0，转换为二进制字符串     一周数据从右往左 比如 00000111则是周天周一周二
     *
     * @param i      数据
     * @param bitNum 多少位
     */
    fun intToBinary32(i: Int, bitNum: Int): String {
        val binaryStr = StringBuilder(Integer.toBinaryString(i))
        while (binaryStr.length < bitNum) {
            binaryStr.insert(0, "0")
        }
        return binaryStr.toString()
    }

    /**
     * 文字添加icon，换行时不会出现剧中的问题
     */
    fun getTextIcon(drawable: Drawable, text: String): SpannableString? {
        val spannableString = SpannableString("  $text")
        drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        spannableString.setSpan(VerticalImageSpan(drawable), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannableString
    }

    /**
     * @date  2017/8/24
     * @description 将字符串格式化成JSON的格式
     */
    fun stringToJSON(strJson: String): String? {
        // 计数tab的个数
        var tabNum = 0
        val jsonFormat = StringBuffer()
        val length = strJson.length
        var last = 0.toChar()
        for (i in 0 until length) {
            val c = strJson[i]
            if (c == '{') {
                tabNum++
                jsonFormat.append("""$c""".trimIndent())
                jsonFormat.append(getSpaceOrTab(tabNum))
            } else if (c == '}') {
                tabNum--
                jsonFormat.append("\n")
                jsonFormat.append(getSpaceOrTab(tabNum))
                jsonFormat.append(c)
            } else if (c == ',') {
                jsonFormat.append("""$c""".trimIndent())
                jsonFormat.append(getSpaceOrTab(tabNum))
            } else if (c == ':') {
                jsonFormat.append("$c ")
            } else if (c == '[') {
                tabNum++
                val next = strJson[i + 1]
                if (next == ']') {
                    jsonFormat.append(c)
                } else {
                    jsonFormat.append("""$c""".trimIndent())
                    jsonFormat.append(getSpaceOrTab(tabNum))
                }
            } else if (c == ']') {
                tabNum--
                if (last == '[') {
                    jsonFormat.append(c)
                } else {
                    jsonFormat.append("""${getSpaceOrTab(tabNum)}$c""".trimIndent())
                }
            } else {
                jsonFormat.append(c)
            }
            last = c
        }
        return jsonFormat.toString()
    }

    private fun getSpaceOrTab(tabNum: Int): String {
        val sbTab = StringBuffer()
        for (i in 0 until tabNum) {
            sbTab.append('\t')
        }
        return sbTab.toString()
    }

    /**
     * 判断json字符串是否是集合
     */
    fun isArrayJson(jsonString: String?): Boolean {
        return try { //判断被拦截参数是否为JSONObject或者JSONArray格式
            var flag = false
            val json = JSONTokener(jsonString).nextValue()
            if (json is JSONArray) {
                flag = true
            }
            flag
        } catch (e: Exception) {
            false
        }
    }

    /* 这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
       * @param src byte[] data
       * @return hex string
       */
    fun bytesToHexString(src: ByteArray?): String? {
        val stringBuilder = StringBuilder("")
        if (src == null || src.isEmpty()) {
            return null
        }
        for (element in src) {
            val v = element.toInt() and 0xFF
            val hv = Integer.toHexString(v)
            if (hv.length < 2) {
                stringBuilder.append(0)
            }
            stringBuilder.append(hv)
        }
        return stringBuilder.toString()
    }

    /**
     * 十六进制String转换成Byte[]
     * @param hexString the hex string
     * *
     * @return byte[]
     */
    fun hexStringToBytes(hexString: String?): ByteArray? {
        var mHexString = hexString
        if (mHexString?.apply {
                mHexString = this.toUpperCase()
                val length = this.length / 2
                val hexChars = this.toCharArray()
                val d = ByteArray(length)
                for (i in 0 until length) {
                    val pos = i * 2
                    d[i] = (charToByte(hexChars[pos]).toInt() shl 4 or charToByte(hexChars[pos + 1]).toInt()).toByte()
                }
            }.isNullOrEmpty()) {
            return null
        }
        return null
    }

    /**
     * Convert char to byte
     * @param c char
     * *
     * @return byte
     */
    private fun charToByte(c: Char): Byte {
        return "0123456789ABCDEF".indexOf(c).toByte()
    }

    fun getPublicKey(modulus: String, publicExponent: String): PublicKey? {
        val biglntModulus: BigInteger = BigInteger(modulus, 16)
        val biglntPrivateExponent: BigInteger = BigInteger(publicExponent, 16)
        val keySpec = RSAPublicKeySpec(biglntModulus, biglntPrivateExponent)
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePublic(keySpec)
    }

    fun getPrivateKey(modulus: String, privateExponent: String): PrivateKey? {
        val biglntModulus = BigInteger(modulus, 16)
        val biglntPrivateExponent: BigInteger = BigInteger(privateExponent, 16)
        val keySpec = RSAPrivateKeySpec(biglntModulus, biglntPrivateExponent)
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePrivate(keySpec)
    }

    //图片将要保存的路径文件夹
    private fun getOutputDirectory(context: Context, appName: String): File {
        val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
            File(it, appName).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else context.filesDir
    }
}
