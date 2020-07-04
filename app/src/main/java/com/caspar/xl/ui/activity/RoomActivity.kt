package com.caspar.xl.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.caspar.base.base.BaseActivity
import com.caspar.base.ext.setOnClickListener
import com.caspar.base.helper.LogUtil
import com.caspar.xl.R
import com.caspar.xl.bean.db.TeacherBean
import com.caspar.xl.bean.db.UserBean
import com.caspar.xl.config.ARouterApi
import com.caspar.xl.databinding.ActivityRoomBinding
import com.caspar.xl.db.RoomManager
import com.caspar.xl.network.util.GsonUtils
import com.caspar.xl.viewmodel.RoomViewModel
import kotlinx.coroutines.*
import kotlin.random.Random

@Route(path = ARouterApi.ROOM)
class RoomActivity : BaseActivity<ActivityRoomBinding>(R.layout.activity_room),
    View.OnClickListener {

    private val mViewModel: RoomViewModel by viewModels()

    override fun initIntent() {

    }

    override fun initView(savedInstanceState: Bundle?) {
        mViewModel.user.observe(this, Observer {
            LogUtil.e("学生数据")
            LogUtil.json(GsonUtils.toJson(it))
        })
        mViewModel.teacher.observe(this, Observer {
            LogUtil.e("老师数据")
            LogUtil.json(GsonUtils.toJson(it))
        })
        setOnClickListener(this, R.id.btnSearch, R.id.tv_left, R.id.btnInsert, R.id.btnClear)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_left -> finish()
            R.id.btnSearch -> {
                lifecycleScope.launch {
                    LogUtil.e("随机查询某老师对应的学生：")
                    val size = withContext(Dispatchers.IO) { mViewModel.teacher.value?.size ?: 0 }
                    if (size != 0) {
                        val tId = (0 until size).random()
                        val id = mViewModel.teacher.value?.get(tId)?.id ?: 0
                        val user = withContext(Dispatchers.IO) { RoomManager.instance.getUserDao().getUser(id) }
                        toast("查找到数据:" + GsonUtils.toJson(user))
                    } else {
                        toast("请先新增数据")
                    }
                }
            }
            R.id.btnInsert -> {
                val error = CoroutineExceptionHandler { coroutineContext, throwable ->
                    run {
                        //数据库添加失败
                        LogUtil.e(throwable)
                        toast("新增失败")
                    }
                }
                lifecycleScope.launch(error) {
                    val teacherId = RoomManager.instance.getTeacherDao().insert(
                        TeacherBean(
                            name = "老师" + (0..100).random(),
                            age = (0..100).random()
                        )
                    )
                    LogUtil.e("数据库返回数据teacherId$teacherId")
                    val user = UserBean(
                        name = "学生" + (0..100).random(),
                        age = (0..100).random(),
                        tId = teacherId
                    )
                    val userId = RoomManager.instance.getUserDao().insert(user)
                    LogUtil.e("数据库返回数据userId$userId")
                    toast("Success" + GsonUtils.toJson(user))
                }
            }
            R.id.btnClear -> {
                val error = CoroutineExceptionHandler { coroutineContext, throwable ->
                    run {
                        //数据库删除失败
                        LogUtil.e(throwable)
                    }
                }
                lifecycleScope.launch(error) {
                    //若要删除失败，下面两个方法互相调换一下顺序
                    RoomManager.instance.getUserDao().deleteAll()
                    RoomManager.instance.getTeacherDao().deleteAll()
                    toast("Delete Success")
                }
            }

        }
    }
}