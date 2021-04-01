package com.caspar.xl.ui.fragment

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.caspar.base.base.BaseFragment
import com.caspar.base.ext.acStart
import com.caspar.base.ext.dp
import com.caspar.base.ext.hide
import com.caspar.base.ext.requestMultiplePermissions
import com.caspar.base.helper.Permission
import com.caspar.xl.databinding.FragmentHomeBinding
import com.caspar.xl.network.util.GsonUtils
import com.caspar.xl.ui.activity.CameraActivity
import com.caspar.xl.ui.activity.RoomActivity
import com.caspar.xl.ui.activity.TranslateActivity
import com.caspar.xl.ui.adapter.HomeMenuAdapter
import com.caspar.xl.utils.decoration.Decoration
import com.caspar.xl.viewmodel.HomeViewModel

/**
 *  @Create 2020/6/13.
 *  @Use
 */
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    //首页列表适配器
    private val mAdapter: HomeMenuAdapter by lazy { HomeMenuAdapter() }
    //首页ViewModel
    private val mViewModel: HomeViewModel by viewModels()

    private val permission = requestMultiplePermissions(allGranted = {
            acStart<CameraActivity>()
        }, denied = {
            toast("你拒绝了以下权限->${GsonUtils.toJson(it)}")
        }, explained = {
            toast("你拒绝了以下权限，并点击了不再询问->${GsonUtils.toJson(it)}")
        })

    override fun initView(savedInstanceState: Bundle?) {
        mBindingView.title.tvLeft.hide()
        initAdapter()
    }

    private fun initAdapter() {
        mBindingView.rvList.layoutManager = GridLayoutManager(context, 2)
        mBindingView.rvList.addItemDecoration(Decoration.GridDecoration(2, 10.dp, true))
        mBindingView.rvList.adapter = mAdapter
        mAdapter.setList(mViewModel.mData)
        mAdapter.setOnItemClickListener { _, _, position ->
            run {
                when (position) {
                    0 -> {
                        acStart<TranslateActivity>()
                    }
                    1 -> {
                        permission.launch(Permission.Group.CAMERA)
                    }
                    2 -> {
                        acStart<RoomActivity>()
                    }
                }
            }
        }
    }

}