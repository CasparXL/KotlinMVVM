package com.caspar.commom.ext

/**
 * int转换2个字节的ByteArray
 */
fun Int.int2ByteArray2(): ByteArray {
    val byteArray = ByteArray(2)
    val LowH = ((this shr 8) and 0xff).toByte()
    val LowL = (this and 0xff).toByte()
    byteArray[0] = LowH
    byteArray[1] = LowL
    return byteArray
}
/**
 * int转换4个字节的ByteArray
 */
fun Int.int2ByteArray4(): ByteArray {
    val byteArray = ByteArray(4)
    val highH = ((this shr 24) and 0xff).toByte()
    val highL = ((this shr 16) and 0xff).toByte()
    val LowH = ((this shr 8) and 0xff).toByte()
    val LowL = (this and 0xff).toByte()
    byteArray[0] = highH
    byteArray[1] = highL
    byteArray[2] = LowH
    byteArray[3] = LowL
    return byteArray
}
/**
 * byteArray转int方法
 * array.toHexStr.toInt(16) //最大转换 7f ff ff ff
 * 超出int最大值则
 * array.toHexStr.toLong(16)
 */
/**
 * ByteArray转换十六进制字符串
 */
fun ByteArray.toHexStr() =
    with(StringBuilder()) {
        this@toHexStr.forEach {
            val hex = it.toInt() and (0xFF)
            val hexStr = Integer.toHexString(hex)
            if (hexStr.length == 1) append("0").append(hexStr)
            else append(hexStr)
        }
        toString()
    }

/**
 * String转换byte数组
 */
fun String.decodeHex(): ByteArray {
    check(length % 2 == 0) { "Must have an even length" }
    return chunked(2)
        .map { it.toInt(16).toByte() }
        .toByteArray()
}
