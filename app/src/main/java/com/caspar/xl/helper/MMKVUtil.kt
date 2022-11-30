package com.caspar.xl.helper

import android.os.Parcelable
import com.tencent.mmkv.MMKV
import java.util.*

/**
 * 腾讯mmkv跨进程数据共享
 */
object MMKVUtil {
    private var mmkv: MMKV? = null

    init {
        mmkv = MMKV.defaultMMKV()
    }

    /**
     * 存基础数据
     */
    fun encode(key: String, value: Any?) {
        when (value) {
            is String -> mmkv?.encode(key, value)
            is Float -> mmkv?.encode(key, value)
            is Boolean -> mmkv?.encode(key, value)
            is Int -> mmkv?.encode(key, value)
            is Long -> mmkv?.encode(key, value)
            is Double -> mmkv?.encode(key, value)
            is ByteArray -> mmkv?.encode(key, value)
            else -> return
        }
    }

    /**
     * 存序列化对象
     */
    fun <T : Parcelable> encode(key: String, t: T?) {
        t?.apply {
            mmkv?.encode(key, t)
        }
    }

    /**
     * 存Set类对象
     */
    fun encode(key: String, sets: Set<String>?) {
        if (sets == null) {
            return
        }
        mmkv?.encode(key, sets)
    }

    /**
     * 获取Int
     */
    fun decodeInt(key: String): Int {
        return mmkv?.decodeInt(key, 0)?:0
    }

    /**
     * 获取Double
     */
    fun decodeDouble(key: String): Double {
        return mmkv?.decodeDouble(key, 0.00)?:0.00
    }

    /**
     * 获取Long
     */
    fun decodeLong(key: String): Long {
        return mmkv?.decodeLong(key, 0L)?:0L
    }

    /**
     * 获取boolean
     */
    fun decodeBoolean(key: String): Boolean {
        return mmkv?.decodeBool(key, false)?:false
    }

    /**
     * 获取Float
     */
    fun decodeFloat(key: String): Float {
        return mmkv?.decodeFloat(key, 0F)?:0f
    }

    /**
     * 获取byteArray
     */
    fun decodeByteArray(key: String): ByteArray {
        return mmkv?.decodeBytes(key)?: byteArrayOf()
    }

    /**
     * 获取String
     */
    fun decodeString(key: String): String {
        return mmkv?.decodeString(key, "")?:""
    }

    /**
     * 获取序列化对象
     */
    fun <T : Parcelable> decodeParcelable(key: String, tClass: Class<T>): T? {
        return mmkv?.decodeParcelable(key, tClass)
    }

    /**
     * 获取Set
     */
    fun decodeStringSet(key: String): Set<String> {
        return mmkv?.decodeStringSet(key, Collections.emptySet())?: setOf()
    }

    /**
     * 删除某个key的数据
     */
    fun removeKey(key: String) {
        mmkv?.removeValueForKey(key)
    }

    /**
     * 清空所有数据
     */
    fun clearAll() {
        mmkv?.clearAll()
    }
}
