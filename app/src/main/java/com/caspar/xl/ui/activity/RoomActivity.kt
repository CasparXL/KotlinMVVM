package com.caspar.xl.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.caspar.base.base.BaseActivity
import com.caspar.base.ext.setOnClickListener
import com.caspar.base.utils.log.dLog
import com.caspar.base.utils.log.eLog
import com.caspar.xl.R
import com.caspar.xl.bean.db.TeacherBean
import com.caspar.xl.bean.db.UserBean
import com.caspar.xl.databinding.ActivityRoomBinding
import com.caspar.xl.eventandstate.RoomViewState
import com.caspar.xl.ext.binding
import com.caspar.xl.ext.fromJson
import com.caspar.xl.ext.observeState
import com.caspar.xl.ext.toJson
import com.caspar.xl.ui.viewmodel.RoomViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class RoomActivity : BaseActivity(), View.OnClickListener {
    private val mBindingView: ActivityRoomBinding by binding()
    private val mViewModel: RoomViewModel by viewModels()

    override fun initView(savedInstanceState: Bundle?) {
        mBindingView.tvLogcat.movementMethod = ScrollingMovementMethod.getInstance()
        searchAll()
        setOnClickListener(
            this,
            R.id.btnSearch,
            R.id.tv_left,
            R.id.btnInsert,
            R.id.btnClear,
            R.id.btnSearchAll
        )
        mViewModel.viewStates.let { state ->
            state.observeState(this, RoomViewState::str) {
                "str文字变化->${it}".dLog()
            }
            state.observeState(this, RoomViewState::teacherId) {
                "teacherId文字变化->${it}".dLog()
            }
            state.observeState(this, RoomViewState::userId) {
                "userId文字变化->${it}".dLog()
            }
        }
    }

    private fun searchAll() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                mViewModel.viewStates.value.str =
                    "学生数据\n" + mViewModel.getAllUser().toJson() + "\n\n老师数据:\n\n" +mViewModel.getAllTeacher().toJson()
            }
            mBindingView.tvLogcat.text = mViewModel.viewStates.value.str
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
                    "随机查询某老师对应的学生：老师数量$size".eLog()
                    if (size != 0) {
                        val tId = (0 until size).random()
                        val id = mViewModel.getTeacherForId(tId)
                        mViewModel.viewStates.value.str = "查找到老师，老师对应的id为${id}\n"
                        val user = mViewModel.getUserByTid(id)
                        if (user.isNotEmpty()) {
                            mViewModel.viewStates.value.str += user.toJson()
                            mBindingView.tvLogcat.text = mViewModel.viewStates.value.str
                        } else {
                            mViewModel.viewStates.value.str += "该老师没有对应的学生"
                            mBindingView.tvLogcat.text = mViewModel.viewStates.value.str
                        }
                    } else {
                        mViewModel.updateStr("没有查找到老师信息，请先进行添加数据再进行查询")
                        mBindingView.tvLogcat.text = mViewModel.viewStates.value.str
                    }
                }
            }
            R.id.btnInsert -> {
                val error = CoroutineExceptionHandler { _, throwable ->
                    run {
                        //数据库添加失败,可能因为其他原因导致的异常[比如学生表插入的teacherId在老师表中实际上目前不存在该数据，那会进入当前界面]
                        throwable.eLog()
                        lifecycleScope.launch {
                            val teacher =
                                mViewModel.getTeacherById(mViewModel.viewStates.value.teacherId)
                            val user = mViewModel.getUserById(mViewModel.viewStates.value.userId)
                            when {
                                mViewModel.viewStates.value.teacherId == -1L -> {
                                    mBindingView.tvLogcat.text =
                                        "由于本次生成的老师信息在数据库中已存在，所以添加用户信息时事务冲突了，因此老师和学生的信息都添加失败，请重新添加数据到数据库"
                                }
                                mViewModel.viewStates.value.userId == -1L -> {
                                    mBindingView.tvLogcat.text = "添加学生时的老师信息是错误的，因此老师添加成功了，但学生添加失败"
                                }
                                else -> {
                                    mBindingView.tvLogcat.text =
                                        "准备加入到用户id里" + mViewModel.viewStates.value.userId + "对应的老师id" + mViewModel.viewStates.value.teacherId + "但是失败了,以下是这两组数据的信息:\n 老师:\n $teacher \n学生:\n $user"
                                }
                            }
                        }
                    }
                }
                lifecycleScope.launch(error) {
                    //模拟随机添加学生和老师数据
                    val teacherBean = TeacherBean(
                        id = (0..100).random().toLong(),
                        name = "老师" + (0..100).random(),
                        age = (0..100).random()
                    )
                    val teacherId = mViewModel.insertTeacher(teacherBean)
                    mViewModel.updateTeacherId(teacherId)
                    mViewModel.updateStr(
                        if (teacherId != -1L) {
                            "添加老师数据成功[id:${teacherBean.id},name=${teacherBean.name}]\n"
                        } else {
                            "添加老师数据失败[id:${teacherBean.id},name=${teacherBean.name}]\n请进行重新添加老师信息[错误原因可能是因为随机数产生时，当前id在表中已存在]\n"
                        }
                    )
                    val user = UserBean(
                        id = (0..100).random().toLong(),
                        name = "学生" + (0..100).random(),
                        age = (0..100).random(),
                        tId = teacherBean.id
                    )
                    mViewModel.updateUserId(user.id)
                    "老师id $teacherId,学生id $mViewModel.viewStates.value.userId".eLog()
                    val userId = mViewModel.insertUser(user)
                    mViewModel.viewStates.value.str += if (userId != -1L) {
                        "添加学生数据成功[id:${user.id},name=${user.name}]\n"
                    } else {
                        "添加学生数据失败[id:${user.id},name=${user.name}]\n请进行重新添加学生信息[错误原因可能是因为随机数产生时，当前id在表中已存在]\n"
                    }
                    mViewModel.viewStates.value.str.eLog()
                    mBindingView.tvLogcat.text = mViewModel.viewStates.value.str
                }
            }
            R.id.btnClear -> {
                val error = CoroutineExceptionHandler { _, throwable ->
                    run {
                        //数据库删除失败
                        throwable.eLog()
                    }
                }
                lifecycleScope.launch(error) {
                    withContext(Dispatchers.IO) {
                        mViewModel.clear()
                    }
                    //若要删除失败，下面两个方法互相调换一下顺序
                    mViewModel.updateStr("")
                    mBindingView.tvLogcat.text = "数据清空成功"
                }
            }
        }
    }
}