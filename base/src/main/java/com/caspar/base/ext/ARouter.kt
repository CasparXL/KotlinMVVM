package com.caspar.base.ext

import android.app.Activity
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.launcher.ARouter


//ARouter传值跳转
inline fun Activity.ARouterStart(url: String, block: Postcard.() -> Unit) = ARouter.getInstance().build(url).apply { block() }.navigation()
//ARouter传值跳转
inline fun Fragment.ARouterStart(url: String, block: Postcard.() -> Unit) = ARouter.getInstance().build(url).apply { block() }.navigation()
//ARouter传值跳转
inline fun Activity.ARouterStartResult(url: String,code:Int, block: Postcard.() -> Unit) = ARouter.getInstance().build(url).apply { block() }.navigation(this,code)
//ARouter传值跳转
inline fun Fragment.ARouterStartResult(url: String,code:Int, block: Postcard.() -> Unit) = ARouter.getInstance().build(url).apply { block() }.navigation(activity,code)
