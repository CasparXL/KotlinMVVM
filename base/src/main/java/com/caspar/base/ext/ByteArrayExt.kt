package com.caspar.base.ext

/**
 * int转换2个字节的ByteArray
 */
fun Int.toByteArray2(): ByteArray {
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
fun Int.toByteArray4(): ByteArray {
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
 * 字符串中添加空白
 * @param space 每隔几个添加空白
 */
fun String.toBlankSpace(space: Int = 2, char: String = " "): String {
    val spaceChar = StringBuilder().apply {
        repeat(space) {
            append('.')
        }
    }.toString()
    val allString = this.replace(spaceChar.toRegex(), "$0$char")
    return allString.substring(0, allString.lastIndex)
}

/**
 * String转换byte数组
 */
fun String.toDecodeHex(): ByteArray {
    check(length % 2 == 0) { "Must have an even length" }
    return chunked(2)
        .map { it.toInt(16).toByte() }
        .toByteArray()
}

