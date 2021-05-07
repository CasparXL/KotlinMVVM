package com.caspar.xl.ui.activity

import android.annotation.SuppressLint
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.caspar.base.base.BaseActivity
import com.caspar.base.ext.dp
import com.caspar.base.ext.setOnClickListener
import com.caspar.xl.R
import com.caspar.xl.databinding.ActivitySelectImageBinding
import com.caspar.xl.ui.adapter.SelectImageAdapter
import com.caspar.xl.utils.decoration.Decoration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

/**
 * 选择图片文件
 * 界面存在一个问题，当快速滑动时，会显得卡顿(不确定是否是部分机型，还是所有机型)，目前确定，问题是由Coil对Adapter复用机制的支持不是很友好，当缓慢滑动时则不会有这个问题
 * 如果用Glide，在滑动时则不会存在卡顿问题，因此适配器中图片加载的方案请谨慎选择
 */
class SelectImageActivity : BaseActivity<ActivitySelectImageBinding>(), View.OnClickListener {
    private val mAdapter: SelectImageAdapter by lazy { SelectImageAdapter() }

    /** 图片专辑  */
    private val mAllAlbum = HashMap<String, MutableList<String>>()
    override fun initIntent() {

    }

    override fun initView(savedInstanceState: Bundle?) {
        setOnClickListener(this, R.id.tv_left)
        mBindingView.title.tvCenter.text = "相册选择器"

        with(mBindingView) {
            rvList.layoutManager = GridLayoutManager(this@SelectImageActivity, 3)
            rvList.itemAnimator = null
            rvList.addItemDecoration(Decoration.GridDecoration(3, 3.dp, true))
            rvList.adapter = mAdapter
        }
        mAdapter.setOnItemClickListener { _, _, position ->
            val select: String = mAdapter.getItem(position)
            mAdapter.selectItem(select)
        }
        loadImages()
    }

    @SuppressLint("SetTextI18n")
    private fun loadImages() {
        lifecycleScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                mAdapter.data.clear()
                mAdapter.notifyDataSetChanged()
            }
            val contentUri = MediaStore.Files.getContentUri("external")
            val sortOrder = MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC"
            val selection =
                "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?)" + " AND " + MediaStore.MediaColumns.SIZE + ">0"

            val contentResolver = contentResolver
            val projections = arrayOf(
                MediaStore.Files.FileColumns._ID, MediaStore.MediaColumns.DATA,
                MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns.DATE_MODIFIED,
                MediaStore.MediaColumns.MIME_TYPE, MediaStore.MediaColumns.WIDTH,
                MediaStore.MediaColumns.HEIGHT, MediaStore.MediaColumns.SIZE
            )
            val cursor: Cursor? = contentResolver.query(
                contentUri,
                projections,
                selection,
                arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString()),
                sortOrder
            )
            cursor?.apply {
                if (moveToFirst()) {
                    val pathIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DATA)
                    val mimeTypeIndex = cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE)
                    val sizeIndex = cursor.getColumnIndex(MediaStore.MediaColumns.SIZE)
                    do {
                        val size = cursor.getLong(sizeIndex)
                        // 图片大小不得小于 1 KB
                        if (size < 1024) {
                            continue
                        }
                        val type = cursor.getString(mimeTypeIndex)
                        val path = cursor.getString(pathIndex)
                        if (TextUtils.isEmpty(path) || TextUtils.isEmpty(type)) {
                            continue
                        }
                        val file = File(path)
                        if (!file.exists() || !file.isFile) {
                            continue
                        }
                        val parentFile = file.parentFile
                        if (parentFile != null) {
                            // 获取目录名作为专辑名称
                            val albumName = parentFile.name
                            var data: MutableList<String>? = mAllAlbum[albumName]
                            if (data == null) {
                                data = ArrayList()
                                mAllAlbum[albumName] = data
                            }
                            data.add(path)
                            mAdapter.data.add(path)
                        }
                    } while (cursor.moveToNext())
                    cursor.close()
                }
                withContext(Dispatchers.Main) {
                    // 滚动回第一个位置
                    mBindingView.rvList.scrollToPosition(0)
                    mAdapter.notifyDataSetChanged()
                    mBindingView.title.tvRight.text = "All Image"
                }
            }
        }

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_left -> {
                finish()
            }
        }
    }
}