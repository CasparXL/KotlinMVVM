package com.caspar.xl.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.caspar.base.base.BaseActivity
import com.caspar.xl.R
import com.caspar.xl.databinding.ActivityPdfPreviewBinding
import com.caspar.xl.databinding.ActivitySelectCityBinding
import com.caspar.xl.ext.binding
import com.rajat.pdfviewer.PdfRendererView
import com.rajat.pdfviewer.PdfViewerActivity
import com.rajat.pdfviewer.util.saveTo

/**
 * 查看Pdf文件
 */
class PdfPreviewActivity : BaseActivity() {
    private val mBindingView: ActivityPdfPreviewBinding by binding()
    //pdf地址
    private val pdfUrl = "https://xxxxx"
    override fun initView(savedInstanceState: Bundle?) {
        mBindingView.title.tvLeft.setOnClickListener { finish() }
        mBindingView.title.tvCenter.text = "查看pdf功能"
        mBindingView.pdfView.statusListener = object : PdfRendererView.StatusCallBack {
            override fun onError(error: Throwable) {
                super.onError(error)
                toast("加载失败${error.localizedMessage}")
            }
        }
        mBindingView.btnQuery.setOnClickListener {
            mBindingView.pdfView.initWithUrl(
                url = pdfUrl,
                lifecycleCoroutineScope = lifecycleScope,
                lifecycle = lifecycle
            )
        }
        mBindingView.btnQuery2.setOnClickListener {
            startActivity(
                PdfViewerActivity.launchPdfFromUrl(
                    context = this,
                    pdfUrl = pdfUrl,
                    pdfTitle = null,
                    saveTo = saveTo.DOWNLOADS,
                    enableDownload = true
                )
            )
        }
    }
}