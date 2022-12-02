package com.caspar.xl.ui.fragment

import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.caspar.base.base.BaseFragment
import com.caspar.base.ext.*
import com.caspar.base.utils.log.LogUtil
import com.caspar.base.helper.Permission
import com.caspar.base.utils.local.getLocal
import com.caspar.base.utils.local.getLocation
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
import com.chad.library.adapter.base.dragswipe.QuickDragAndSwipe
import com.chad.library.adapter.base.dragswipe.listener.DragAndSwipeDataCallback
import com.chad.library.adapter.base.dragswipe.listener.OnItemDragListener
import com.chad.library.adapter.base.viewholder.QuickViewHolder
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

/**
 *  @Create 2020/6/13.
 *  @Use
 */
@AndroidEntryPoint
class HomeFragment : BaseFragment() {
    private lateinit var mBindingView: FragmentHomeBinding
    private val quickDragAndSwipe = QuickDragAndSwipe().setDragMoveFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
    //首页列表适配器
    private val mAdapter: HomeMenuAdapter = HomeMenuAdapter()

    //首页ViewModel
    private val mViewModel: HomeViewModel by viewModels()

    //跳转到某个界面，这里是用来标识需要储存权限的几个界面
    private var toOtherPage: String = ""
    //请求定位所需的权限
    private val localPermission = requestMultiplePermissions(allGranted = {
        lifecycleScope.launchWhenCreated {
            getLocal()
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

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): ViewBinding {
        return FragmentHomeBinding.inflate(inflater, container,false).apply {
            mBindingView = this
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
        mAdapter.submitList(mViewModel.mData)
        mAdapter.setOnItemClickListener { _, _, position ->
            run {
                val menu = mAdapter.items[position]
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
                    mViewModel.refreshList -> {
                        acStart<RefreshListActivity>()
                    }
                }
            }
        }
        // 滑动事件
        quickDragAndSwipe.attachToRecyclerView(mBindingView.rvList)
            .setDataCallback(mAdapter)
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
    private suspend fun getLocal(locale: Locale? = null) {
        val location = requireContext().getLocal(locale = locale)
        location?.apply {
            val hashMapOf = hashMapOf<String, Any>()
            hashMapOf["国家"] = this.country
            hashMapOf["省"] = this.province
            hashMapOf["市"] = this.city
            hashMapOf["区"] = this.area
            hashMapOf["邮编"] = this.adCode
            hashMapOf["区号"] = this.cityCode
            hashMapOf["详细住址"] = this.addressDetail
            hashMapOf["经度"] = this.latitude
            hashMapOf["纬度"] = this.longitude
            toast("国家[${this.country}] 省份[${this.province}] 城市[${this.city}] 区[${this.area}] 详细住址[${this.addressDetail}] 经度[${this.latitude}] 纬度[${this.longitude}]")
            LogUtil.json(GsonUtils.toJson(hashMapOf))
            LogUtil.json(GsonUtils.toJson(this))
        }
    }
}