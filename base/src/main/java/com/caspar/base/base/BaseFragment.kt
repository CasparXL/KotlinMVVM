package com.caspar.base.base

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.caspar.base.action.ToastAction
import com.caspar.base.utils.log.LogUtil
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType

/**
 * @author CasparXL
 * @time 2020/4/2
 */
abstract class BaseFragment : Fragment(), ToastAction {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return getViewBinding(inflater, container).root
    }

    //获取父Activity
    private fun getParentActivity(): Activity? {
        return activity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(savedInstanceState)
    }

    abstract fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): ViewBinding

    abstract fun initView(savedInstanceState: Bundle?)

}