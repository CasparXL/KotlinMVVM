package com.caspar.commom.ext

/**
 * 单精度匹配
 */
fun String.regexFloat():Boolean{
    val regex1 = "^(0|[1-9]+[0-9]*)(.[0-9])?\$"
    val regex2 = "^-(0|[1-9]+[0-9]*)(.[0-9])?\$"
    return Regex(regex1).matches(this) or Regex(regex2).matches(this)
}

/**
 * 双精度匹配
 */
fun String.regexFloat2():Boolean{
    val regex1 = "^(0|[1-9]+[0-9]*)(.[0-9]{1,2})?\$"
    val regex2 = "^-(0|[1-9]+[0-9]*)(.[0-9]{1,2})?\$"
    return Regex(regex1).matches(this) or Regex(regex2).matches(this)
}