package com.caspar.base.ext

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment

/**
 *  @Create 2020/6/25.
 *  @Use CasparXL
 */
/**
 * Fragment扩展函数，用于给视图添加点击事件
 */
fun Fragment.setOnClickListener(listener: View.OnClickListener, vararg view: View) {
    for (id in view) {
        id.setOnClickListener(listener)
    }
}

/**
 * 扩展函数，用于startActivity(intent)
 * 使用方法如下:
 *    acStart(XxxACTIVITY::class.java)
 * 或者如果需要传输数据进其他界面
 *    acStart(XxxACTIVITY::class.java){
 *         putString("key","value")
 *         putInt("key",123)
 *    }
 */
@JvmOverloads
inline fun <reified T:Activity> Fragment.acStart(block: intentVoid = {}) = run {
    val intent = Intent(this.context, T::class.java)
    block(intent)
    startActivity(intent)
}
/**
 * 在Fragment中先初始化出这个注册，代码如下
 * val toHomePage = acStartForResult{ //it是ActivityResult，包含了所有界面返回的数据
 *     LogUtil.e("我收到数据了 ${it.resultCode} ${it.data?.getStringExtra("name")}")
 * }
 * 如果要跳转界面
 * Ⅰ toHomePage.launch(createIntent<XXActivity>())  不传值出去  等同于用法Ⅲ
 * Ⅱ toHomePage.launch(createIntent<XXActivity>{ putExtra("key","value") }) 传值到下一个activity
 * Ⅲ toHomePage.launch(Intent(context,XXActivity::class.java)) 等同于用法Ⅰ
 * @param block 与原来onActivityResult的回调一样，只是返回值做了封装 不包含 requestCode
 *
 */
inline fun Fragment.acStartForResult(
    crossinline block: (ac: ActivityResult) -> Unit
): ActivityResultLauncher<Intent> =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        block.invoke(it)
    }

/**
 * 搭配[acStartForResult]方法使用，主要作用为跳转到 [I] activity
 * @param I 要跳转的Activity ,具体用法，参照[acStartForResult]方法注释
 * @param block 使用了JvmOverloads注解，主要作用为intent附带传值，即putExtra等功能,可不写
 */
@JvmOverloads
inline fun <reified I : ComponentActivity> Fragment.createIntent(block: intentVoid = {}): Intent {
    val intent = Intent(context, I::class.java)
    block.invoke(intent)
    return intent
}
