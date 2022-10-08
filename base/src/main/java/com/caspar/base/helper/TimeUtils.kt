package com.caspar.base.helper

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object TimeUtils {
    /**
     * 通过毫秒转换时间格式
     *
     * @param ms 时间(字符串) 支持时分秒和毫秒的单位级别,其他的暂未做适配
     * @param time 传入的时间单位
     */
    fun formatTime(timeString: String?, timeUnit: TimeUnit = TimeUnit.MILLISECONDS): String {
        val timeStringValue: String = timeString ?: "0"
        val ss = 1000 //毫秒代表的毫秒级别数值
        val mi = ss * 60 //秒代表的毫秒级别数值
        val hh = mi * 60 //分钟代表的毫秒级别数值
        val dd = hh * 24 //小时代表的毫秒级别数值
        val ms: Long = when (timeUnit) { //传入的毫秒数据
            TimeUnit.DAYS -> (timeStringValue.toDouble() * dd).toLong() //天
            TimeUnit.HOURS -> (timeStringValue.toDouble() * hh).toLong() //小时
            TimeUnit.MINUTES -> (timeStringValue.toDouble() * mi).toLong() //分钟
            TimeUnit.SECONDS -> (timeStringValue.toDouble() * ss).toLong() //秒
            else -> {timeStringValue.toLong() } //其余格式显示 0
        }
        val day = ms / dd //天代表的毫秒级别数值
        val allHour = ms / hh //超过24小时的时间
        val hour = (ms - day * dd) / hh //小时
        val minute = (ms - day * dd - hour * hh) / mi //分钟
        val second = (ms - day * dd - hour * hh - minute * mi) / ss //秒
        val strHour = if (allHour < 10) "0${allHour}" else allHour
        val strMinute = if (minute < 10) "0${minute}" else minute
        val strSecond = if (second < 10) "0${second}" else second
        return if (allHour > 0) {
            "$strHour:$strMinute:$strSecond"
        } else {
            "$strMinute:$strSecond"
        }
    }
    /**
     * 通过毫秒转换时间格式
     *
     * @param ms 时间(字符串) 支持时分秒和毫秒的单位级别,其他的暂未做适配
     * @param time 传入的时间单位
     */
    fun formatTimeForMin(timeString: String?, timeUnit: TimeUnit = TimeUnit.MILLISECONDS): String {
        val timeStringValue :String = timeString?:"0"
        val ss = 1000 //毫秒代表的毫秒级别数值
        val mi = ss * 60 //秒代表的毫秒级别数值
        val hh = mi * 60 //分钟代表的毫秒级别数值
        val dd = hh * 24 //小时代表的毫秒级别数值
        val ms : Long = when(timeUnit){ //传入的毫秒数据
            TimeUnit.DAYS -> (timeStringValue.toDouble() * dd).toLong() //天
            TimeUnit.HOURS -> (timeStringValue.toDouble() * hh).toLong() //小时
            TimeUnit.MINUTES -> (timeStringValue.toDouble() * mi).toLong() //分钟
            TimeUnit.SECONDS -> (timeStringValue.toDouble() * ss).toLong() //秒
            else -> { timeStringValue.toLong() } //其余格式显示 0
        }
        /*val day = ms / dd //天代表的毫秒级别数值
         val hour = (ms - day * dd) / hh //小时
         val minute = (ms - day * dd - hour * hh) / mi //分钟
         val second = (ms - day * dd - hour * hh - minute * mi) / ss //秒
         val milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss //毫秒
         val strDay = if (day < 10) "0$day" else "" + day //天
         val strHour = if (hour < 10) "0$hour" else "" + hour //小时
         val strMinute = if (minute < 10) "0$minute" else "" + minute //分钟
         val strSecond = if (second < 10) "0$second" else "" + second //秒
         var strMilliSecond = if (milliSecond < 10) "0$milliSecond" else "" + milliSecond //毫秒
         strMilliSecond = if (milliSecond < 100) "0$strMilliSecond" else "" + strMilliSecond
        return when {
            day > 0 -> {
                "$strDay:$strHour(d./h.)"
            }
            hour > 0 -> {
                "$strHour:$strMinute(h./m.)"
            }
            else -> {
                "$strMinute:$strSecond(m./s.)"
            }
        }*/
        /*val minute = ms / 60000
        val seconds = ms % 60000
        val second = (seconds.toFloat() / 1000).roundToLong()
        val strMinute = if (minute<10) "0${minute}" else minute
        val strSecond = if (second<10) "0${second}" else second
        return  "$strMinute:$strSecond min"*/
        val minute = ms / 60000
        val seconds = ms % 60000
        return if (seconds >= 30 * 1000){
            "${minute + 1} min"
        } else {
            "$minute min"
        }
    }

    /**
     * 校验日期格式是否正确
     */
    fun isValidDate(str: String,pattern:String): Boolean {
        var convertSuccess = true
        // 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
        val format = SimpleDateFormat(pattern, Locale.CHINA)
        try {
            // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
            format.isLenient = false
            format.parse(str)
        } catch (e: ParseException) {
            // e.printStackTrace();
            // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            convertSuccess = false
        }
        return convertSuccess
    }

    fun formatDuration(duration: String): String {
        val durationLong = duration.toLong()
        val unitHour = (durationLong / 60 * 60).toInt()
        val unitMinute = (durationLong / 60).toInt()
        val unitSecond = (durationLong % 60).toInt()
        var time = "00:00:00"
        var hour = ""
        var minute = ""
        var second = ""
        hour = if (unitHour < 10) {
            "0$unitHour"
        } else {
            "$unitHour"
        }
        minute = if (unitMinute < 10) {
            "0$unitMinute"
        } else {
            "$unitMinute"
        }
        second = if (unitSecond < 10) {
            "0$unitSecond"
        } else {
            "$unitSecond"
        }
        time = "$hour:$minute:$second"
        return time
    }

    fun getDays(startTime: String, endTime: String): List<String> {
        // 返回的日期集合
        val days: MutableList<String> = ArrayList()
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        try {
            val start: Date = dateFormat.parse(startTime)
            val end: Date = dateFormat.parse(endTime)
            val tempStart = Calendar.getInstance()
            tempStart.time = start
            val tempEnd = Calendar.getInstance()
            tempEnd.time = end
            tempEnd.add(Calendar.DATE, +1) // 日期加1(包含结束)
            while (tempStart.before(tempEnd)) {
                days.add(dateFormat.format(tempStart.time))
                tempStart.add(Calendar.DAY_OF_YEAR, 1)
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return days
    }
}