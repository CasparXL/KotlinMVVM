package com.caspar.base.ext

import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

/**
 *  @Create 2020/6/25.
 *  @Use CasparXL
 *  用于对数据转换时间的处理
 */

/**
 *  根据字符串的时间转换毫秒时间戳,格式不正确时返回-1
 *  @param format 时间的格式，默认是按照yyyy-MM-dd HH:mm:ss来转换，如果您的格式不一样，则需要传入对应的格式
 */
fun String?.timeFormatMillis(format: String = "yyyy-MM-dd HH:mm:ss"): Long {
    return try {
        val dtfInput = DateTimeFormatter.ofPattern(format, Locale.getDefault())
        LocalDateTime.parse(this ?: "", dtfInput)
            .toInstant(ZoneOffset.systemDefault().rules.getOffset(Instant.now())).toEpochMilli()
    } catch (e: java.lang.Exception) {
        this.timeFormatMillisForData(format)
    }
}

/**
 *  根据字符串的时间转换毫秒时间戳,格式不正确时返回-1
 *  @param format 时间的格式，默认是按照yyyy-MM-dd HH:mm:ss来转换，如果您的格式不一样，则需要传入对应的格式
 */
private fun String?.timeFormatMillisForData(format: String = "yyyy-MM-dd HH:mm:ss"): Long {
    return try {
        val dtfInput = DateTimeFormatter.ofPattern(format, Locale.getDefault())
        LocalDate.parse(this ?: "", dtfInput).atStartOfDay()
            .toInstant(ZoneOffset.systemDefault().rules.getOffset(Instant.now())).toEpochMilli()
    } catch (e: java.lang.Exception) {
        -1
    }
}

/**
 * 根据时间戳转换字符串时间
 */
fun Long?.timeFormatDate(format: String = "yyyy-MM-dd HH:mm:ss"): String {
    val time: String = try {
        val dtfInput = DateTimeFormatter.ofPattern(format, Locale.getDefault())
        LocalDateTime.ofInstant(Instant.ofEpochMilli(this ?: -1), ZoneId.systemDefault())
            .format(dtfInput)
    } catch (e: java.lang.Exception) {
        ""
    }
    return time
}

/**
 * 根据时间戳获取事件对象，从而拿到当天的对象
 */
fun Long?.getLocalDataTime(): LocalDateTime {
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(this ?: -1L), ZoneId.systemDefault())
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

/**
 * 根据Duration转换时间,如 60*1000.milliseconds(毫秒).formatTime -->
 */
fun kotlin.time.Duration.formatTime(): String {
    val day = this.inWholeDays.days
    val hour = this.inWholeHours.hours - day
    val minute = (this.inWholeMinutes.minutes - hour - day).inWholeMinutes.minutes
    val second =
        (this.inWholeSeconds.seconds - minute - hour - day).inWholeSeconds.seconds

    val dayString = day.toInt(DurationUnit.DAYS)
    val hourString = hour.toInt(DurationUnit.HOURS)
    val minuteString = minute.toInt(DurationUnit.MINUTES)
    val secondString = second.toInt(DurationUnit.SECONDS)

    return when {
        dayString > 0 -> {
            "${dayString}d:".plus("${hourString}h:")
                .plus("${minuteString}m:").plus("${secondString}s")
        }
        hourString > 0 -> {
            "${hourString}h:".plus("${minuteString}m:").plus("${secondString}s")
        }
        minuteString > 0 -> {
            "${minuteString}m:".plus("${secondString}s")
        }
        else -> {
            "${secondString}s"
        }
    }
}