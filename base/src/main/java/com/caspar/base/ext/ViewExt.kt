package com.caspar.base.ext

import android.view.View

/**
 *  @Create 2020/6/25.
 *  @Use
 */

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}


