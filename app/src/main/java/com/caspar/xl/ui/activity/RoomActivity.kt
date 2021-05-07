package com.caspar.xl.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.caspar.base.base.BaseActivity
import com.caspar.base.ext.setOnClickListener
import com.caspar.base.helper.LogUtil
import com.caspar.xl.R
import com.caspar.xl.bean.db.TeacherBean
import com.caspar.xl.bean.db.UserBean
import com.caspar.xl.databinding.ActivityRoomBinding
import com.caspar.xl.db.RoomManager
import com.caspar.xl.network.util.GsonUtils
import com.caspar.xl.viewmodel.RoomViewModel
import kotlinx.coroutines.*

class RoomActivity : BaseActivity<ActivityRoomBinding>(), View.OnClickListener {
    private val mViewModel: RoomViewModel by viewModels()
    private var str = ""
    private var teacherId = -1L
    private var userId = -1L
    override fun initIntent() {

    }

    override fun initView(savedInstanceState: Bundle?) {
        mBindingView.tvLogcat.movementMethod = ScrollingMovementMethod.getInstance()
        searchAll()
        setOnClickListener(this, R.id.btnSearch, R.id.tv_left, R.id.btnInsert, R.id.btnClear, R.id.btnSearchAll)
    }

    private fun searchAll() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                str = "学生数据\n" + GsonUtils.toJson(mViewModel.getAllUser()) + "\n\n老师数据:\n\n" + GsonUtils.toJson(mViewModel.getAllTeacher())
            }
            mBindingView.tvLogcat.text = str
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_left -> finish()
            R.id.btnSearchAll -> searchAll()
            R.id.btnSearch -> {
                lifecycleScope.launch {
                    val size = mViewModel.getTeacherSize()
                    LogUtil.e("随机查询某老师对应的学生：老师数量$size")
                    if (size != 0) {
                        val tId = (0 until size).random()
                        val id = mViewModel.getTeacherForId(tId)
                        str = "查找到老师，老师对应的id为${id}\n"
                        val user = mViewModel.getUserByTid(id)
                        if (!user.isNullOrEmpty()) {
                            str += GsonUtils.toJson(user)
                            mBindingView.tvLogcat.text = str
                        } else {
                            str += "该老师没有对应的学生"
                            mBindingView.tvLogcat.text = str
                        }
                    } else {
                        str = "没有查找到老师信息，请先进行添加数据再进行查询"
                        mBindingView.tvLogcat.text = str
                    }
                }
            }
            R.id.btnInsert -> {
                val error = CoroutineExceptionHandler { _, throwable ->
                    run {
                        //数据库添加失败,可能因为其他原因导致的异常[比如学生表插入的teacherId在老师表中实际上目前不存在该数据，那会进入当前界面]
                        LogUtil.e(throwable)
                        lifecycleScope.launch {
                            val teacher = mViewModel.getTeacherById(teacherId)
                            val user = mViewModel.getUserById(userId)
                            when {
                                teacherId == -1L -> {
                                    mBindingView.tvLogcat.text = "由于本次生成的老师信息在数据库中已存在，所以添加用户信息时事务冲突了，因此老师和学生的信息都添加失败，请重新添加数据到数据库"
                                }
                                userId == -1L -> {
                                    mBindingView.tvLogcat.text = "添加学生时的老师信息是错误的，因此老师添加成功了，但学生添加失败"
                                }
                                else -> {
                                    mBindingView.tvLogcat.text = "准备加入到用户id里" + userId + "对应的老师id" + teacherId + "但是失败了,以下是这两组数据的信息:\n 老师:\n $teacher \n学生:\n $user"
                                }
                            }
                        }
                    }
                }
                lifecycleScope.launch(error) {
                    //模拟随机添加学生和老师数据
                    val teacherBean = TeacherBean(id = (0..100).random().toLong(), name = "老师" + (0..100).random(), age = (0..100).random())
                    val teacherId = mViewModel.insertTeacher(teacherBean)
                    this@RoomActivity.teacherId = teacherId
                    str = if (teacherId != -1L) {
                        "添加老师数据成功[id:${teacherBean.id},name=${teacherBean.name}]\n"
                    } else {
                        "添加老师数据失败[id:${teacherBean.id},name=${teacherBean.name}]\n请进行重新添加老师信息[错误原因可能是因为随机数产生时，当前id在表中已存在]\n"
                    }
                    val user = UserBean(id = (0..100).random().toLong(), name = "学生" + (0..100).random(), age = (0..100).random(), tId = teacherBean.id)
                    this@RoomActivity.userId = user.id
                    LogUtil.e("老师id $teacherId,学生id $userId")
                    val userId = mViewModel.insertUser(user)
                    str += if (userId != -1L) {
                        "添加学生数据成功[id:${user.id},name=${user.name}]\n"
                    } else {
                        "添加学生数据失败[id:${user.id},name=${user.name}]\n请进行重新添加学生信息[错误原因可能是因为随机数产生时，当前id在表中已存在]\n"
                    }
                    LogUtil.e(str)
                    mBindingView.tvLogcat.text = str
                }
            }
            R.id.btnClear -> {
                val error = CoroutineExceptionHandler { _, throwable ->
                    run {
                        //数据库删除失败
                        LogUtil.e(throwable)
                    }
                }
                lifecycleScope.launch(error) {
                    withContext(Dispatchers.IO) {
                        RoomManager.getInstance().getUserDao().deleteAll()
                        RoomManager.getInstance().getTeacherDao().deleteAll()
                    }
                    //若要删除失败，下面两个方法互相调换一下顺序
                    str = ""
                    mBindingView.tvLogcat.text = "数据清空成功"
                }
            }
        }
    }
}