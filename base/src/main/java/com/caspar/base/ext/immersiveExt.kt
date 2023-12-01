package com.caspar.base.ext

import android.app.Activity
import android.view.View
import android.view.WindowManager
import com.caspar.base.base.BaseDialog
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ktx.immersionBar

/**
 * 由于弹框使用Window管理器，导致状态栏和导航栏重新触发展示，因此在dialog显示前先使用此段代码隐隐藏.
 * 然后dismiss()之后调用[Activity.immersive]重新再次以Activity为上下文隐藏状态栏和导航栏
 */
fun BaseDialog.immersive() {
    /*getActivity()?.immersionBar(this){
        hideBar(BarHide.FLAG_HIDE_BAR)
    }*/
    with(window?.decorView) {
        this?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }
    this.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
}

/**
 * Activity隐藏状态栏和导航栏
 */
fun Activity.immersive() {
    immersionBar {
        hideBar(BarHide.FLAG_HIDE_BAR)
    }
    /*with(window) {
        val params = this.attributes
        params.systemUiVisibility =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE
        this.attributes = params
    }*/
}