package com.caspar.base.helper

import androidx.lifecycle.AndroidViewModel
import com.caspar.base.base.BaseViewModel
import java.lang.reflect.ParameterizedType

/**
 * 获取另外两层的基类
 */
object ClassUtil {
    /**
     * 获取泛型ViewModel的class对象
     */
    fun <T> getViewModel(obj: Any): Class<T>? {
        val currentClass: Class<*> = obj.javaClass
        val tClass: Class<T>? = getGenericClass(currentClass, AndroidViewModel::class.java)
        return if (tClass == null || tClass == AndroidViewModel::class.java || tClass == BaseViewModel::class.java) {
            null
        } else tClass
    }

    private fun <T> getGenericClass(klass: Class<*>?, filterClass: Class<*>): Class<T>? {
        val type = klass?.genericSuperclass
        if (type == null || type !is ParameterizedType) return null
        val types = type.actualTypeArguments
        for (t in types) {
            val tClass = t as Class<T>
            if (filterClass.isAssignableFrom(tClass)) {
                return tClass
            }
        }
        return null
    }
}