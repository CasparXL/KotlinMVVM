package com.caspar.base.ext

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor
import java.util.*

/**
 *  @Create 2020/6/25.
 *  @Use CasparXL
 *  用于对数据转换时间的处理
 */

/**
 *  根据字符串的时间转换毫秒时间戳,格式不正确时返回-1
 *  @param format 时间的格式，默认是按照yyyy-MM-dd HH:mm:ss来转换，如果您的格式不一样，则需要传入对应的格式
 */
fun String.timeFormatMillis(format: String = "yyyy-MM-dd HH:mm:ss"): Long {
    return try {
        val dtfInput = DateTimeFormatter.ofPattern(format, Locale.getDefault())
        LocalDateTime.parse(this, dtfInput)
            .toInstant(ZoneOffset.of(ZoneOffset.systemDefault().normalized().id)).toEpochMilli()
    } catch (e: java.lang.Exception) {
        -1
    }
}

/**
 * 根据时间戳转换字符串时间
 */
fun Long.timeFormatDate(format: String = "yyyy-MM-dd HH:mm:ss"): String {
    val time: String = try {
        val dtfInput = DateTimeFormatter.ofPattern(format, Locale.getDefault())
        LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())
            .format(dtfInput)
    } catch (e: java.lang.Exception) {
        ""
    }
    return time
}

/**
 * 根据时间戳获取事件对象，从而拿到当天的对象
 */
fun Long.getLocalDataTime(): LocalDateTime {
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())
}

/**
 * 根据时间对象转换时间字符串
 */
fun LocalDateTime.toTimeString(format: String = "yyyy-MM-dd HH:mm:ss"): String {
    val time: String = try {
        val dtfInput = DateTimeFormatter.ofPattern(format, Locale.getDefault())
        this.format(dtfInput)
    } catch (e: java.lang.Exception) {
        ""
    }
    return time
}