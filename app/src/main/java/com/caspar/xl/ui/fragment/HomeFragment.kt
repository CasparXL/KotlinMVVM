package com.caspar.xl.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.core.view.isInvisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.caspar.base.base.BaseFragment
import com.caspar.commom.ext.*
import com.caspar.commom.helper.LogUtil
import com.caspar.commom.helper.Permission
import com.caspar.commom.utils.local.getLocation
import com.caspar.xl.app.BaseApplication
import com.caspar.xl.config.Constant
import com.caspar.xl.databinding.FragmentHomeBinding
import com.caspar.xl.network.util.GsonUtils
import com.caspar.xl.ui.activity.*
import com.caspar.xl.ui.adapter.HomeMenuAdapter
import com.caspar.xl.ui.dialog.VerifyDialog
import com.caspar.xl.utils.decoration.Decoration
import com.caspar.xl.viewmodel.HomeViewModel
import com.caspar.xl.widget.captcha.Captcha
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

/**
 *  @Create 2020/6/13.
 *  @Use
 */
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    //首页列表适配器
    private val mAdapter: HomeMenuAdapter by lazy { HomeMenuAdapter() }

    //首页ViewModel
    private val mViewModel: HomeViewModel by viewModels()

    //跳转到某个界面，这里是用来标识需要储存权限的几个界面
    private var toOtherPage: String = ""
    //请求定位所需的权限
    private val localPermission = requestMultiplePermissions(allGranted = {
        lifecycleScope.launchWhenCreated {
            getLocal(0)
//            getLocal(0, Locale.ENGLISH)
        }
    }, denied = {
        toast("你拒绝了以下权限->${GsonUtils.toJson(it)}")
    }, explained = {
        toast("你拒绝了以下权限，并点击了不再询问->${GsonUtils.toJson(it)}")
    })
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
            toPermissionActivity()
        }, denied = {
            toast("你拒绝了以下权限->${GsonUtils.toJson(it)}")
        }, explained = {
            toast("你拒绝了以下权限，并点击了不再询问->${GsonUtils.toJson(it)}")
        })

    /**
     * 根据情况跳转到需要存储权限的界面
     */
    private fun toPermissionActivity() {
        when (toOtherPage) {
            mViewModel.selectFile -> {
                selectFile.launch(createIntent<SelectFileActivity>())
            }
            mViewModel.imageSelect -> {
                acStart<SelectImageActivity>()
            }
            else -> {
                selectFile.launch(createIntent<SelectFileActivity>())
            }
        }
    }


    override fun initView(savedInstanceState: Bundle?) {
        mBindingView.title.tvLeft.isInvisible = true
        mBindingView.srlRefresh.setEnableAutoLoadMore(false)
        mBindingView.srlRefresh.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
            override fun onRefresh(refreshLayout: RefreshLayout) {
                lifecycleScope.launch {
                    delay(2000)
                    refreshLayout.finishRefresh()
                }
            }

            override fun onLoadMore(refreshLayout: RefreshLayout) {
                lifecycleScope.launch {
                    delay(5000)
                    refreshLayout.finishLoadMore()
                }

            }

        })
        initAdapter()
    }

    private fun initAdapter() {
        mBindingView.rvList.layoutManager = GridLayoutManager(context, 2)
        mBindingView.rvList.addItemDecoration(Decoration.GridDecoration(2, 10.dp, true))
        mBindingView.rvList.adapter = mAdapter
        mAdapter.setList(mViewModel.mData)
        mAdapter.setOnItemClickListener { _, _, position ->
            run {
                val menu = mAdapter.data[position]
                toOtherPage = menu
                when (menu) {
                    mViewModel.translate -> {
                        acStart<TranslateActivity>()
                    }
                    mViewModel.camera -> {
                        permission.launch(Permission.Group.CAMERA)
                    }
                    mViewModel.room -> {
                        acStart<RoomActivity>()
                    }
                    mViewModel.selectFile -> {
                        startSelectFile2AllStorage()
                    }
                    mViewModel.coroutines -> {
                        acStart<CoroutinesAboutActivity>()
                    }
                    mViewModel.imageLoad -> {
                        acStart<ImageLoadActivity>()
                    }
                    mViewModel.colorSelect -> {
                        acStart<PaletteActivity>()
                    }
                    mViewModel.imageSelect -> {
                        startSelectFile2AllStorage()
                    }
                    mViewModel.selectCity -> {
                        acStart<SelectCityActivity>()
                    }
                    mViewModel.verifyCaptcha -> {
                        VerifyDialog.Builder(requireContext()).setListener(object : Captcha.CaptchaListener{
                            override fun onAccess(time: Long): String {
                                LogUtil.d(time.toString())
                                return time.toString()
                            }

                            override fun onFailed(failCount: Int): String {
                                LogUtil.d(failCount.toString())
                                return failCount.toString()
                            }

                            override fun onMaxFailed(): String {
                                LogUtil.d("超出了失败次数")
                                return "超出了失败次数"
                            }
                        }).create().show()
                    }
                    mViewModel.local -> {
                        localPermission.launch(Permission.Group.LOCATION)
                    }
                    mViewModel.crashLog -> {
                        acStart<CrashLogActivity>()
                    }
                }
            }
        }
        mAdapter.draggableModule.isDragEnabled = true //依赖库自带拖拽功能
        mAdapter.draggableModule.attachToRecyclerView(mBindingView.rvList) //绑定适配器才能拖拽
    }

    //兼容安卓10 ,11读写权限问题,11的手机暂时没有，所以需要有条件了再测，再次点击跳转按钮也可以
    private fun startSelectFile2AllStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                toPermissionActivity()
            } else {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:" + BaseApplication.context.packageName)
                startActivity(intent)
            }
        } else {
            permissionRequest.launch(Permission.Group.STORAGE)
        }
    }

    /**
     * 多次获取，因为首次获取到定位权限时不一定能获取到定位信息
     */
    private suspend fun getLocal(int: Int, locale: Locale? = null) {
        val location = requireContext().getLocation(locale)
        if (location.first) {
            val address = location.second
            val hashMapOf = hashMapOf<String, Any>()
            hashMapOf["国家"] = address.country
            hashMapOf["省"] = address.province
            hashMapOf["市"] = address.city
            hashMapOf["区"] = address.area
            hashMapOf["邮编"] = address.adCode
            hashMapOf["区号"] = address.cityCode
            hashMapOf["详细住址"] = address.addressDetail
            hashMapOf["经度"] = address.latitude
            hashMapOf["纬度"] = address.longitude
            toast("国家[${address.country}] 省份[${address.province}] 城市[${address.city}] 区[${address.area}] 详细住址[${address.addressDetail}] 经度[${address.latitude}] 纬度[${address.longitude}]")
            LogUtil.json(GsonUtils.toJson(hashMapOf))
            LogUtil.json(GsonUtils.toJson(address))
        } else {
            if (int < 3) {
                delay(100)
                getLocal(int+1, locale)
            }
            LogUtil.d("获取失败第${int+1}次")
        }
    }
}