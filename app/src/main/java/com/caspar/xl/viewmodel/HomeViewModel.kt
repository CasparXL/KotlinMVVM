package com.caspar.xl.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.launcher.ARouter
import com.caspar.base.base.BaseViewModel
import com.caspar.base.helper.ActivityStackManager
import com.caspar.base.utils.permissions.OnPermission
import com.caspar.base.utils.permissions.Permission
import com.caspar.base.utils.permissions.XXPermissions
import com.caspar.xl.config.ARouterApi

/**
 *  "CasparXL" 创建 2020/5/12.
 *   界面名称以及功能: 首页功能菜单
 */
class HomeViewModel(application: Application) : BaseViewModel(application) {
    //功能列表
    val mData: List<String> = arrayListOf("翻译", "CameraX", "Room")

    fun permission() {
        XXPermissions.with(ActivityStackManager.topActivity).permission(Permission.Group.CAMERA).request(object : OnPermission {
            override fun hasPermission(granted: List<String?>?, isAll: Boolean) {
                if (isAll) {
                    ARouter.getInstance().build(ARouterApi.CAMERA).navigation()
                } else {
                    toast("请先同意权限")
                }
            }

            override fun noPermission(denied: List<String?>?, quick: Boolean) {
                toast("请先开启权限")
            }
        })
    }
}
