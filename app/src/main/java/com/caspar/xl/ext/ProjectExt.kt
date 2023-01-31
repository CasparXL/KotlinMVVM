package com.caspar.xl.ext

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ComponentActivity
import androidx.viewbinding.ViewBinding


inline fun <reified VB : ViewBinding> inflateBinding(layoutInflater: LayoutInflater) =
    VB::class.java.getMethod("inflate", LayoutInflater::class.java).invoke(null, layoutInflater) as VB

inline fun <reified VB : ViewBinding> inflateBinding(parent: ViewGroup) =
    inflateBinding<VB>(LayoutInflater.from(parent.context), parent, false)

inline fun <reified VB : ViewBinding> inflateBinding(
    layoutInflater: LayoutInflater, parent: ViewGroup?, attachToParent: Boolean
) =
    VB::class.java.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
        .invoke(null, layoutInflater, parent, attachToParent) as VB

/**
 * 初始化binding
 * @param inflate ViewBinding的初始化 指定类
 * @param setContentView 是否直接赋值
 */
fun <VB : ViewBinding> ComponentActivity.binding(inflate: (LayoutInflater) -> VB, setContentView: Boolean = true) = lazy(LazyThreadSafetyMode.NONE) {
    inflate(layoutInflater).also { binding ->
        if (setContentView) setContentView(binding.root)
    }
}

/**
 * 初始化binding
 * @param setContentView 是否直接赋值
 */
inline fun <reified VB : ViewBinding> ComponentActivity.binding(setContentView: Boolean = true) = lazy(LazyThreadSafetyMode.NONE) {
    inflateBinding<VB>(layoutInflater).also { binding ->
        if (setContentView) setContentView(binding.root)
    }
}