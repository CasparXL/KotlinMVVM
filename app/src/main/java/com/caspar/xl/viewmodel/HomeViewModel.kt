package com.caspar.xl.viewmodel

import android.app.Application
import com.caspar.base.base.BaseViewModel

/**
 *  "CasparXL" 创建 2020/5/12.
 *   界面名称以及功能: 首页功能菜单
 */
class HomeViewModel(application: Application) : BaseViewModel(application) {
    val translate = "翻译"
    val camera = "相机"
    val room = "数据库"
    val selectFile = "文件选择器"
    val coroutines = "协程库"

    //功能列表
    val mData: List<String> = arrayListOf(translate, camera, room, selectFile, coroutines)
}
