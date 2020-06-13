package com.caspar.base.base

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.launcher.ARouter
import com.caspar.base.ext.ARouterStart
import com.caspar.base.ext.ARouterStartResult

/**
 * @author CasparXL
 * @description 如果使用了ARouter,Activity顶部需要加上@Router注解，参数path为标注路径，示例:@Route(path = ARouterApi.MAIN)
 * @description2 如果使用了InjectManager.inject方式注入布局layout，同样，顶部使用注解@ContentView，参数value为布局xml，示例:@ContentView(R.layout.activity_main)
 * @time 2020/4/2
 */
abstract class BaseFragment< SV : ViewDataBinding>(@LayoutRes val contentLayoutId:Int) : Fragment() {


    /**
     * 绑定布局的ViewDatabinding,本项目中主要用于findViewById的作用，未来可用ViewBinding代替
     */
    protected lateinit var mBindingView: SV


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBindingView = DataBindingUtil.inflate(
            inflater,
            contentLayoutId,
            null,
            false
        )
        return mBindingView.root
    }

    //获取父Activity
    private fun getParentActivity(): Activity? {
        return activity
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView(savedInstanceState)
    }

    abstract fun initView(savedInstanceState: Bundle?)

    override fun onDestroy() {
        mBindingView.unbind()
        super.onDestroy()
    }

    /*******************************************拓展方法以及函数**************************************************/

    /***默认界面跳转,不带传值的***/
    fun arStart(url: String) {
        //ARouter自带API跳转界
        ARouter.getInstance().build(url).navigation()
    }

    /***默认界面跳转,带传值的***/
    fun arStart(url: String, block: Postcard.() -> Unit) {
        //使用扩展函数进行跳转界面，方法体包含传参的方法
        ARouterStart(url) {
            block()
        }
    }

    /***默认界面跳转,支持onActivityResult***/
    fun arStartResult(url: String, code: Int) {
        //ARouter自带API跳转界面，支持onActivityResult回调方法
        ARouter.getInstance().build(url).navigation(getParentActivity(), code)
    }

    /***默认界面跳转,带传值的支持onActivityResult***/
    fun arStartResult(url: String, code: Int, block: Postcard.() -> Unit) {
        //使用扩展函数进行跳转界面，方法体包含传参的方法，支持onActivityResult回调方法
        ARouterStartResult(url, code) {
            block()
        }
    }

}