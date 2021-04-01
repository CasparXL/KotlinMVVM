package com.caspar.xl.ui.fragment

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.caspar.base.base.BaseFragment
import com.caspar.base.ext.*
import com.caspar.base.helper.LogUtil
import com.caspar.base.helper.Permission
import com.caspar.xl.config.Constant
import com.caspar.xl.databinding.FragmentHomeBinding
import com.caspar.xl.network.util.GsonUtils
import com.caspar.xl.ui.activity.*
import com.caspar.xl.ui.adapter.HomeMenuAdapter
import com.caspar.xl.utils.decoration.Decoration
import com.caspar.xl.viewmodel.HomeViewModel
import com.hjq.toast.ToastUtils
import java.io.File

/**
 *  @Create 2020/6/13.
 *  @Use
 */
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    //首页列表适配器
    private val mAdapter: HomeMenuAdapter by lazy { HomeMenuAdapter() }
    //首页ViewModel
    private val mViewModel: HomeViewModel by viewModels()
    //请求拍照所需的权限
    private val permission = requestMultiplePermissions(allGranted = {
        acStart<CameraActivity>()
    }, denied = {
        toast("你拒绝了以下权限->${GsonUtils.toJson(it)}")
    }, explained = {
        toast("你拒绝了以下权限，并点击了不再询问->${GsonUtils.toJson(it)}")
    })

    //选择文件的跳转回调
    private val selectFile = acStartForResult {
        if (it.resultCode == Constant.SELECT_FILE_PATH) {
            val path = it.data?.getStringExtra("path") ?: ""
            if (path.isNotEmpty()) {
                toast("你选择的文件路径为：$path")
            }
            LogUtil.e("接收到的数据->$path")
        }
    }
    //选择文件需要储存权限，申请完成以后会在allGranted中回调跳转下一个界面
    private val permissionRequest = requestMultiplePermissions(
        allGranted = {
            selectFile.launch(createIntent<SelectFileActivity>())
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
                    3 -> {
                        permissionRequest.launch(Permission.Group.STORAGE)
                    }
                    4 -> {
                        acStart<CoroutinesAboutActivity>()
                    }
                }
            }
        }
    }

}