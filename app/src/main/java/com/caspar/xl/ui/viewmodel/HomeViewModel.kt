package com.caspar.xl.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.caspar.xl.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 *  "CasparXL" 创建 2020/5/12.
 *   界面名称以及功能: 首页功能菜单
 */
@HiltViewModel
class HomeViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {
    val translate = "翻译"
    val camera = "相机"
    val room = "数据库"
    val selectFile = "文件选择器"
    val coroutines = "协程库"
    val imageLoad = "图片加载库"
    val colorSelect = "颜色选择器"
    val imageSelect = "图片选择器"
    val verifyCaptcha = "滑块验证"
    val local = "获取定位信息"
    val selectCity = "选择城市"
    val crashLog = "崩溃日志"
    val refreshList = "刷新部分布局"

    //功能列表
    val mData: List<String> = arrayListOf(translate,
        camera,
        room,
        selectFile,
        coroutines,
        imageLoad,
        colorSelect,
        imageSelect,
        verifyCaptcha,
        local,
        selectCity,
        crashLog,
        refreshList,
    )
}