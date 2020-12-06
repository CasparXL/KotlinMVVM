package com.caspar.base.base

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.caspar.base.R
import com.caspar.base.action.ToastAction
import com.caspar.base.dialog.WaitDialog
import com.caspar.base.ext.immersionBar
import com.caspar.base.helper.KeyBoardUtils
import com.caspar.base.helper.LogUtil
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType

/**
 * @author CasparXL
 * @description 如果使用了ARouter,Activity顶部需要加上@Router注解，参数path为标注路径，示例:@Route(path = ARouterApi.MAIN)
 * @time 2020/4/2
 */
abstract class BaseActivity<SV : ViewBinding> : AppCompatActivity(), ToastAction {
    /***************************************初始化视图以及变量,相关生命周期**********************************************/
    /**
     * ViewBinding
     */
    protected lateinit var mBindingView: SV

    private var mBaseDialog: BaseDialog? = null

    //判断是否显示
    open fun isShowDialog(): Boolean {
        return mBaseDialog?.isShowing ?: false
    }

    /**
     * 显示加载对话框
     */
    open fun showLoadingDialog(tips: String? = null) {
        if (mBaseDialog == null) {
            mBaseDialog = if (tips.isNullOrEmpty()) {
                WaitDialog.Builder(this@BaseActivity).create()
            } else {
                WaitDialog.Builder(this@BaseActivity).setMessage(tips).create()
            }
        } else {
            if (!tips.isNullOrEmpty()) {
                if ((mBaseDialog?.findViewById<TextView>(R.id.tv_wait_message))?.text != tips) {
                    (mBaseDialog?.findViewById<TextView>(R.id.tv_wait_message))?.text = tips
                }
            }
        }
        mBaseDialog?.apply {
            if (!this.isShowing) {
                this.show()
            }
        }
    }

    /**
     * 隐藏加载对话框
     */
    open fun hideDialog() {
        mBaseDialog?.dismiss()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(initViewBinding())
        //沉浸式的拓展方法
        immersionBar {
            /**
             * 状态栏字体深色或亮色 true是深色
             */
            statusBarDarkFont(true)
            /**
             * 解决软键盘与底部输入框冲突问题 ，默认是false
             * Keyboard enable immersion bar.
             *
             */
            keyboardEnable(true)
        }
        initIntent()
        initView(savedInstanceState)
    }

    private fun initViewBinding() :View{
        val superclass = javaClass.genericSuperclass
        val aClass = (superclass as ParameterizedType).actualTypeArguments[0] as Class<*>
        try {
            val method: Method = aClass.getDeclaredMethod("inflate", LayoutInflater::class.java)
            mBindingView = method.invoke(null, layoutInflater) as SV
        } catch (e: NoSuchMethodException) {
            LogUtil.e(e)
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            LogUtil.e(e)
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            LogUtil.e(e)
            e.printStackTrace()
        }
        return mBindingView.root
    }

    override fun onNewIntent(intent: Intent?) { //页面特殊销毁的话通过该方法重新赋值
        super.onNewIntent(intent)
        setIntent(intent)
    }

    /***初始化获取Intent数据***/
    protected abstract fun initIntent()

    /***初始化视图数据***/
    protected abstract fun initView(savedInstanceState: Bundle?)

    //销毁ViewBinding
    override fun onDestroy() {
        super.onDestroy()
    }

    /**
     * 重置App界面的字体大小，fontScale 值为 1 代表默认字体大小
     *
     * @return 重置继承该activity子类的文字大小，使它不受系统字体大小限制
     */
    override fun getResources(): Resources {
        val res = super.getResources()
        val config = res.configuration
        config.fontScale = 1f
        res.updateConfiguration(config, res.displayMetrics)
        return res
    }

    /**
     * 重写关闭界面方法
     */
    override fun finish() {
        hideSoftKeyboard()
        super.finish()
    }

    /**
     * 跳转界面时隐藏软键盘
     */
    override fun startActivityForResult(intent: Intent, requestCode: Int, options: Bundle?) {
        hideSoftKeyboard()
        // 查看源码得知 startActivity 最终也会调用 startActivityForResult
        super.startActivityForResult(intent, requestCode, options)
    }
    /***************************************初始化视图以及变量,相关生命周期**********************************************/
    /*******************************************拓展方法以及函数**************************************************/
    /***************************************隐藏软键盘相关方法**********************************************/
    /**
     * 隐藏软键盘
     */
    private fun hideSoftKeyboard() {
        // 隐藏软键盘，避免软键盘引发的内存泄露
        val view = currentFocus
        view?.run {
            val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            manager?.hideSoftInputFromWindow(this.applicationWindowToken, 0) //applicationWindowToken可以隐藏整个app的键盘,而使用windowToken的时候隐藏不了Fragment的键盘
            //manager?.hideSoftInputFromWindow(this.windowToken, 0)
        }
    }

    /**
     * 清除editText的焦点
     *
     * @param v   焦点所在View
     * @param ids 输入框
     */
    private fun clearViewFocus(v: View?, vararg ids: Int) {
        ids.let {
            //不为空则继续
            for (id in ids) {
                if (v?.id == id) {
                    v.clearFocus()
                    break
                }
            }
        }
    }

    /**
     * 隐藏键盘
     *
     * @param v   焦点所在View
     * @param ids 输入框
     * @return true代表焦点在edit上
     */
    private fun isFocusEditText(v: View?, vararg ids: Int): Boolean {
        if (v is EditText) {
            for (id in ids) {
                if (v.id == id) {
                    return true
                }
            }
        }
        return false
    }

    //是否触摸在指定view上面,对某个控件过滤
    private fun isTouchView(views: Array<View>?, ev: MotionEvent): Boolean {
        if (views == null || views.isEmpty()) return false
        val location = IntArray(2)
        for (view in views) {
            view.getLocationOnScreen(location)
            val x = location[0]
            val y = location[1]
            if (ev.x > x && ev.x < x + view.width && ev.y > y && ev.y < y + view.height) {
                return true
            }
        }
        return false
    }

    //是否触摸在指定view上面,对某个控件过滤
    private fun isTouchView(ids: IntArray?, ev: MotionEvent): Boolean {
        val location = IntArray(2)
        for (id in ids!!) {
            val view = findViewById<View>(id) ?: continue
            view.getLocationOnScreen(location)
            val x = location[0]
            val y = location[1]
            if (ev.x > x && ev.x < x + view.width && ev.y > y && ev.y < y + view.height) {
                return true
            }
        }
        return false
    }

    //endregion
    //region 右滑返回上级
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            if (isTouchView(filterViewByIds(), ev)) return super.dispatchTouchEvent(ev)
            if (hideSoftByEditViewIds().isEmpty()) return super.dispatchTouchEvent(ev)
            val v = currentFocus
            if (isFocusEditText(v, *hideSoftByEditViewIds())) {
                if (isTouchView(hideSoftByEditViewIds(), ev)) return super.dispatchTouchEvent(ev)
                //隐藏键盘
                KeyBoardUtils.hideInputForce(this)
                clearViewFocus(v, *hideSoftByEditViewIds())
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    /**
     * 传入EditText的Id
     * 没有传入的EditText不做处理
     *
     * @return id 数组
     */
    open fun hideSoftByEditViewIds(): IntArray {
        return IntArray(0)
    }

    /**
     * 传入要过滤的View
     * 过滤之后点击将不会有隐藏软键盘的操作
     *
     * @return id 数组
     */
    open fun filterViewByIds(): Array<View>? {
        return null
    }
    /***************************************隐藏软键盘相关方法**********************************************/
}
