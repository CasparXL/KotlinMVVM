package com.caspar.base.base

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
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
    private var _binding: ViewBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = getViewBinding(inflater, container)
        return _binding!!.root
    }

    //获取父Activity
    private fun getParentActivity(): Activity? {
        return activity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData(savedInstanceState)
        lifecycleScope.launchWhenResumed {
            initView(savedInstanceState)
        }
    }

    abstract fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): ViewBinding

    /**
     * 初始化数据状态，处于onCreate周期
     */
    open fun initData(savedInstanceState: Bundle?){}

    /**
     * 初始化View状态（懒加载，比onResume执行慢几毫秒）
     */
    abstract fun initView(savedInstanceState: Bundle?)

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}