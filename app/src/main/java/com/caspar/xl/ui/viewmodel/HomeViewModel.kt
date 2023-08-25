package com.caspar.xl.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.caspar.xl.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 *  "CasparXL" 创建 2020/5/12.
 *   界面名称以及功能: 首页功能菜单
 */
@HiltViewModel
class HomeViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {
    //功能列表
    val mData: List<String> = application.resources.getStringArray(R.array.home_menu).toList()
}
