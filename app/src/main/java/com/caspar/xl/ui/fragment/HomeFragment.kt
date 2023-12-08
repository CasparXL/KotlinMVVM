package com.caspar.xl.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.view.isInvisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import com.caspar.base.base.BaseFragment
import com.caspar.base.ext.*
import com.caspar.base.helper.Permission
import com.caspar.base.utils.log.dLog
import com.caspar.base.utils.log.eLog
import com.caspar.xl.app.BaseApplication
import com.caspar.xl.config.Constant
import com.caspar.xl.databinding.FragmentHomeBinding
import com.caspar.xl.di.WaitDialogInject
import com.caspar.xl.ext.binding
import com.caspar.xl.ext.toJson
import com.caspar.xl.helper.createNetty
import com.caspar.xl.helper.isInit
import com.caspar.xl.network.util.getIPAddress
import com.caspar.xl.network.util.isPortAvailable
import com.caspar.xl.ui.activity.*
import com.caspar.xl.ui.adapter.HomeMenuAdapter
import com.caspar.xl.ui.dialog.VerifyDialog
import com.caspar.xl.utils.decoration.Decoration
import com.caspar.xl.ui.viewmodel.HomeViewModel
import com.caspar.xl.widget.captcha.Captcha
import com.caspar.xl.ui.dialog.CarNumDialog
import com.caspar.xl.ui.dialog.WaitDialog
import com.chad.library.adapter4.dragswipe.QuickDragAndSwipe
import com.rajat.pdfviewer.PdfViewerActivity
import com.rajat.pdfviewer.util.saveTo
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import dagger.hilt.android.AndroidEntryPoint
import io.ktor.server.netty.NettyApplicationEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*
import javax.inject.Inject

/**
 *  @Create 2020/6/13.
 *  @Use
 */
@AndroidEntryPoint
class HomeFragment : BaseFragment() {
    private val mBindingView: FragmentHomeBinding by binding()
    private val quickDragAndSwipe =
        QuickDragAndSwipe().setDragMoveFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)

    //首页列表适配器
    private val mAdapter: HomeMenuAdapter = HomeMenuAdapter()

    //首页ViewModel
    private val mViewModel: HomeViewModel by viewModels()

    //跳转到某个界面，这里是用来标识需要储存权限的几个界面
    private var toOtherPage: String = ""

    @WaitDialogInject
    @Inject
    lateinit var waitDialog: WaitDialog.Builder
    //跳转到某个界面，这里是用来标识需要储存权限的几个界面
    private val carDialog: CarNumDialog.Builder by lazy {
        CarNumDialog.Builder(requireContext())
    }

    private var service: NettyApplicationEngine? = null

    //请求拍照所需的权限
    private val permission = requestMultiplePermissions(allGranted = {
        acStart<CameraActivity>()
    }, denied = {
        toast("你拒绝了以下权限->${it.toJson()}")
    }, explained = {
        toast("你拒绝了以下权限，并点击了不再询问->${it.toJson()}")
    })

    //选择文件的跳转回调
    private val selectFile = acStartForResult {
        if (it.resultCode == Constant.SELECT_FILE_PATH) {
            val path = it.data?.getStringExtra("path") ?: ""
            if (path.isNotEmpty()) {
                toast("你选择的文件路径为：$path")
            }
            "接收到的数据->$path".dLog()
        }
    }

    //选择文件需要储存权限，申请完成以后会在allGranted中回调跳转下一个界面
    private val permissionRequest = requestMultiplePermissions(
        allGranted = {
            toPermissionActivity()
        }, denied = {
            toast("你拒绝了以下权限->${it.toJson()}")
        }, explained = {
            toast("你拒绝了以下权限，并点击了不再询问->${it.toJson()}")
        })

    /**
     * 根据情况跳转到需要存储权限的界面
     */
    private fun toPermissionActivity() {
        when (toOtherPage) {
            mViewModel.mData[3] -> {
                selectFile.launch(createIntent<SelectFileActivity>())
            }
            mViewModel.mData[7] -> {
                acStart<SelectImageActivity>()
            }
            else -> {
                selectFile.launch(createIntent<SelectFileActivity>())
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return mBindingView.root
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
        mBindingView.rvList.addItemDecoration(Decoration.gridDecoration(2, 10.dp, true))
        mBindingView.rvList.adapter = mAdapter
        mAdapter.submitList(mViewModel.mData)
        mAdapter.setOnItemClickListener { _, _, position ->
            run {
                val menu = mAdapter.items[position]
                toOtherPage = menu
                when (menu) {
                    mViewModel.mData[0] -> {
                        acStart<TranslateActivity>()
                    }
                    mViewModel.mData[1]-> {
                        permission.launch(Permission.Group.CAMERA)
                    }
                    mViewModel.mData[2] -> {
                        acStart<RoomActivity>()
                    }
                    mViewModel.mData[3] -> {
                        startSelectFile2AllStorage()
                    }
                    mViewModel.mData[4] -> {
                        acStart<CoroutinesAboutActivity>()
                    }
                    mViewModel.mData[5] -> {
                        acStart<ImageLoadActivity>()
                    }
                    mViewModel.mData[6] -> {
                        acStart<PaletteActivity>()
                    }
                    mViewModel.mData[7] -> {
                        startSelectFile2AllStorage()
                    }
                    mViewModel.mData[8] -> {
                        acStart<SelectCityActivity>()
                    }
                    mViewModel.mData[9] -> {
                        VerifyDialog.Builder(requireContext())
                            .setListener(object : Captcha.CaptchaListener {
                                override fun onAccess(time: Long): String {
                                    time.toString().dLog()
                                    return time.toString()
                                }

                                override fun onFailed(failCount: Int): String {
                                    failCount.toString().dLog()
                                    return failCount.toString()
                                }

                                override fun onMaxFailed(): String {
                                    "超出了失败次数".eLog()
                                    return "超出了失败次数"
                                }
                            }).create().show()
                    }
                    mViewModel.mData[10] -> {
                        acStart<CrashLogActivity>()
                    }
                    mViewModel.mData[11] -> {
                        acStart<RefreshListActivity>()
                    }
                    mViewModel.mData[12] -> {
                        val current = if (AppCompatDelegate.getApplicationLocales().isEmpty) {
                            Locale.getDefault().toLanguageTag()
                        } else {
                            AppCompatDelegate.getApplicationLocales()[0]?.toLanguageTag() ?: ""
                        }
                        val appLocale: LocaleListCompat = if (current == "en") LocaleListCompat.forLanguageTags("zh") else LocaleListCompat.forLanguageTags("en")
                        // 注意：需要在主线程上调用它，因为它可能需要Activity.restart()
                        AppCompatDelegate.setApplicationLocales(appLocale)
                    }
                    mViewModel.mData[13] -> {
                        carDialog.clearText("陕A").setCarNumListener {
                            toast("您输入了车牌->${it}")
                        }.show()
                    }
                    mViewModel.mData[14] -> {
                        lifecycleScope.launch(Dispatchers.IO) {
                            runCatching {
                                if (8080.isPortAvailable()) {
                                    withContext(Dispatchers.Main){
                                        waitDialog.show()
                                    }
                                    service = (service ?: context?.createNetty(8080))
                                    if (service?.isInit() == false){
                                        "服务开启,访问地址:${getIPAddress()}:8080/".dLog()
                                        withContext(Dispatchers.Main) {
                                            toast("服务开启,访问地址:${getIPAddress()}:8080/")
                                            waitDialog.dismiss()
                                        }
                                        service?.start(wait = true)
                                    } else {
                                        service?.stop()
                                        service = null
                                        "服务关闭".dLog()
                                        withContext(Dispatchers.Main) {
                                            toast("服务关闭")
                                        }
                                    }

                                } else {
                                    withContext(Dispatchers.Main){
                                        waitDialog.show()
                                    }
                                    service?.stop()
                                    service = null
                                    "服务关闭".dLog()
                                    withContext(Dispatchers.Main){
                                        toast("服务关闭")
                                        waitDialog.dismiss()
                                    }
                                }
                            }
                        }
                    }
                    mViewModel.mData[15] -> {
                        acStart<PdfPreviewActivity>()
                    }
                }
            }
        }
        // 滑动事件
        /*quickDragAndSwipe.attachToRecyclerView(mBindingView.rvList)
            .setDataCallback(mAdapter)*/
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

    override fun onDestroy() {
        super.onDestroy()
        if (service?.isInit() == true){
            service?.stop()
            service = null
            "关闭Netty服务".dLog()
        }
    }
}