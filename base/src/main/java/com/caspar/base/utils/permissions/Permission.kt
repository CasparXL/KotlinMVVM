package com.caspar.base.utils.permissions

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2018/06/15
 * desc   : 权限请求实体类
 */
object Permission {
    const val REQUEST_INSTALL_PACKAGES = "android.permission.REQUEST_INSTALL_PACKAGES" // 8.0及以上应用安装权限
    const val SYSTEM_ALERT_WINDOW = "android.permission.SYSTEM_ALERT_WINDOW" // 6.0及以上悬浮窗权限
    const val READ_CALENDAR = "android.permission.READ_CALENDAR" // 读取日程提醒
    const val WRITE_CALENDAR = "android.permission.WRITE_CALENDAR" // 写入日程提醒
    const val WRITE_SETTINGS = "android.permission.WRITE_SETTINGS" // 系统修改权限
    const val CAMERA = "android.permission.CAMERA" // 拍照权限
    const val READ_CONTACTS = "android.permission.READ_CONTACTS" // 读取联系人
    const val WRITE_CONTACTS = "android.permission.WRITE_CONTACTS" // 写入联系人
    const val GET_ACCOUNTS = "android.permission.GET_ACCOUNTS" // 访问账户列表
    const val ACCESS_FINE_LOCATION = "android.permission.ACCESS_FINE_LOCATION" // 获取精确位置
    const val ACCESS_COARSE_LOCATION = "android.permission.ACCESS_COARSE_LOCATION" // 获取粗略位置
    const val USE_FINGERPRINT = "android.permission.USE_FINGERPRINT" // 指纹
    const val RECORD_AUDIO = "android.permission.RECORD_AUDIO" // 录音权限
    const val READ_PHONE_STATE = "android.permission.READ_PHONE_STATE" // 读取电话状态
    const val CALL_PHONE = "android.permission.CALL_PHONE" // 拨打电话
    const val READ_CALL_LOG = "android.permission.READ_CALL_LOG" // 读取通话记录
    const val WRITE_CALL_LOG = "android.permission.WRITE_CALL_LOG" // 写入通话记录
    const val ADD_VOICEMAIL = "com.android.voicemail.permission.ADD_VOICEMAIL" // 添加语音邮件
    const val USE_SIP = "android.permission.USE_SIP" // 使用SIP视频
    const val PROCESS_OUTGOING_CALLS = "android.permission.PROCESS_OUTGOING_CALLS" // 处理拨出电话
    const val ANSWER_PHONE_CALLS = "android.permission.ANSWER_PHONE_CALLS" // 8.0危险权限：允许您的应用通过编程方式接听呼入电话。要在您的应用中处理呼入电话，您可以使用 acceptRingingCall() 函数
    const val READ_PHONE_NUMBERS = "android.permission.READ_PHONE_NUMBERS" // 8.0危险权限：权限允许您的应用读取设备中存储的电话号码
    const val BODY_SENSORS = "android.permission.BODY_SENSORS" // 传感器
    const val SEND_SMS = "android.permission.SEND_SMS" // 发送短信
    const val RECEIVE_SMS = "android.permission.RECEIVE_SMS" // 接收短信
    const val READ_SMS = "android.permission.READ_SMS" // 读取短信
    const val RECEIVE_WAP_PUSH = "android.permission.RECEIVE_WAP_PUSH" // 接收WAP PUSH信息
    const val RECEIVE_MMS = "android.permission.RECEIVE_MMS" // 接收彩信
    const val READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE" // 读取外部存储
    const val WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE" // 写入外部存储

    object Group {
        // 日历
        val CALENDAR = arrayOf(
            READ_CALENDAR, WRITE_CALENDAR
        )

        // 联系人
        val CONTACTS = arrayOf(
            READ_CONTACTS, WRITE_CONTACTS, GET_ACCOUNTS
        )

        //电话
        val PHONE_GROUP = arrayOf(
            READ_PHONE_STATE, CALL_PHONE, READ_CALL_LOG, WRITE_CALL_LOG, ADD_VOICEMAIL, USE_SIP, PROCESS_OUTGOING_CALLS
        )

        // 位置
        val LOCATION = arrayOf(
            ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION
        )

        //短信
        val SMS_GROUP = arrayOf(
            SEND_SMS, RECEIVE_SMS, READ_SMS, RECEIVE_WAP_PUSH, RECEIVE_MMS
        )

        // 存储
        val STORAGE = arrayOf(
            READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE
        )

        // 相机
        val CAMERA = arrayOf(
            Permission.CAMERA, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE
        )
    }
}