package com.caspar.xl.widget.index.ext

import android.graphics.Rect
import android.text.TextPaint
import com.caspar.xl.widget.index.bean.ItemData

fun TextPaint.getTextHeight(text: String): Int {
    val bounds = Rect()
    this.getTextBounds(text, 0, text.length, bounds)
    return bounds.height()
}

fun TextPaint.getTextWidth(text: String): Int {
    return this.measureText(text).toInt()
}

/**
 * 对数据源进行拼音解析
 */
fun MutableList<ItemData>.sortByLetter(){
    this.forEach { item ->
        val firstChar = item.tag.first()
        when{
            firstChar.toString().matches("[A-Za-z]".toRegex()) -> {
                item.tag = firstChar.uppercase()
            }
            //缺少一个拼音转换的方法,原来的pinyin库是jcenter的，已弃用
            /*Pinyin.isChinese(firstChar) -> {
                item.tag = Pinyin.toPinyin(firstChar).first().uppercase()
            }*/
            else -> {
                item.tag = "#"
            }
        }
    }

    this.sortWith { o1, o2 ->
        if (o1.tag == "#") {
            1
        } else if (o2.tag == "#") {
            -1
        } else {
            o1.tag.compareTo(o2.tag)
        }
    }
}
