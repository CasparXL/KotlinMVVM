package com.caspar.xl.helper


import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.caspar.base.ext.dp
import java.io.File


/**
 * 加载网络图片
 */
fun ImageView.loadUrl(url: String) {
    Glide.with(context).load(url).into(this)
}

/**
 * 加载网络图片
 */
fun ImageView.loadNet(url: String, @DrawableRes default: Int = -1, baseUrl: String = "") {
    Glide.with(context).load((baseUrl + url).replace("//","/")).apply {
        if (default != -1) {
            apply(placeholder(default))
        }
    }.into(this)
}

/**
 * 加载网络圆形图片
 */
fun ImageView.loadNetCircle(
    url: String,
    @DrawableRes default: Int,
    baseUrl: String = ""
) {
    val glide = Glide.with(context).load(default).circleCrop().placeholder(default)
    Glide.with(context).load(baseUrl + url).circleCrop().thumbnail(glide).into(this)
}
/**
 * 加载网络圆形图片
 */
fun ImageView.loadNetRounded(
    url: String,
    @DrawableRes default: Int,
    baseUrl: String = "",
    defaultRound: Int = 15
) {
    val glide = RequestOptions().placeholder(default).transform(RoundedCorners(defaultRound.dp))
    Glide.with(context).load(baseUrl + url).apply(glide).into(this)
}

/**
 * 加载Bitmap图片
 */
fun ImageView.loadBitmap(url: Bitmap) {
    Glide.with(context).load(url).into(this)
}

/**
 * 加载File图片
 */
fun ImageView.loadFile(url: File) {
    Glide.with(context).load(url).into(this)
}
/**
 * 加载File图片
 */

fun ImageView.loadFile(url: File, @DrawableRes default: Int,) {
    Glide.with(context).load(url).placeholder(default).into(this)
}

/**
 * 加载Uri图片
 */
fun ImageView.loadUri(url: Uri, @DrawableRes default: Int) {
    Glide.with(context).load(url).placeholder(default).into(this)
}

/**
 * 加载其他类型
 */
fun ImageView.loadAllType(url: Any) {
    Glide.with(context).load(url).into(this)
}

/**
 * 加载本地资源图片
 */
fun ImageView.load(@RawRes @DrawableRes url: Int) {
    Glide.with(context).load(url).into(this)
}