package com.caspar.commom.ext


private fun Int.int2ByteArray2(): ByteArray {
    val byteArray = ByteArray(2)
    val LowH = ((this shr 8) and 0xff).toByte()
    val LowL = (this and 0xff).toByte()
    byteArray[0] = LowH
    byteArray[1] = LowL
    return byteArray
}

private fun Int.int2ByteArray4(): ByteArray {
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

fun ByteArray.convertStringFilter(): String {
    val sb = StringBuilder()
    val filterZero = this.filterNot { it < 0 }
    for (i in filterZero.indices) {
        if (i != filterZero.size - 1) {
            sb.append(filterZero[i].toInt().toChar().toString())
        }
    }
    return sb.toString()
}