package com.caspar.xl.viewmodel

import android.app.Application
import com.caspar.base.base.BaseViewModel

/**
 *  "CasparXL" 创建 2020/5/12.
 *   界面名称以及功能: 首页功能菜单
 */
class HomeViewModel(application: Application) : BaseViewModel(application) {
    //功能列表
    val mData: List<String> = arrayListOf("翻译", "CameraX", "Room")
}
