package com.caspar.xl.ui.activity

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.alibaba.android.arouter.facade.annotation.Route
import com.caspar.base.base.BaseActivity
import com.caspar.base.ext.setOnClickListener
import com.caspar.base.helper.LogUtil
import com.caspar.xl.R
import com.caspar.xl.config.ARouterApi
import com.caspar.xl.databinding.ActivityCameraBinding
import com.hjq.toast.ToastUtils
import java.io.File
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Route(path = ARouterApi.CAMERA)
class CameraActivity : BaseActivity<ActivityCameraBinding>(R.layout.activity_camera), View.OnClickListener {

    private lateinit var imageAnalyzer: ImageAnalysis
    private var preview: Preview? = null
    private var camera: Camera? = null
    private var imageCapture: ImageCapture? = null

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    override fun initIntent() {
        mBindingView.title.tvCenter.text = "CameraX"
        setOnClickListener(this, R.id.tv_left, R.id.iv_take_photo)
    }

    override fun initView(savedInstanceState: Bundle?) {
        startCamera()
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    //图片将要保存的路径
    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() } }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }
    //开始预览，CameraX绑定Activity，随生命周期销毁而自动销毁
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, LuminosityAnalyzer())
                }
            // Used to bind the lifecycle of cameras to the lifecycle owner
            //用于将相机的生命周期绑定到生命周期所有者
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            // Preview
            //预览
            preview = Preview.Builder()
                .build()
            // Select back camera
            // 使用相机
            val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
            try {
                // Unbind use cases before rebinding
                //在重新绑定之前解除用例绑定
                cameraProvider.unbindAll()
                // Bind use cases to camera
                //将用例绑定到摄像机
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview,imageCapture,imageAnalyzer)
                preview?.setSurfaceProvider(mBindingView.viewFinder.createSurfaceProvider(camera?.cameraInfo))
            } catch(exc: Exception) {
                LogUtil.e(exc)
            }

        }, ContextCompat.getMainExecutor(this))

    }
    //拍照
    private fun takePhoto() {
        showLoadingDialog("稍等")
        // Get a stable reference of the modifiable image capture use case
        //获得可修改映像捕获用例的稳定引用
        val imageCapture = imageCapture ?: return
        // Create timestamped output file to hold the image
        // 创建带有时间戳的输出文件来保存图像
        val photoFile = File(outputDirectory, SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US).format(System.currentTimeMillis()) + ".jpg")
        // Create output options object which contains file + metadata
        // 创建包含文件+元数据的输出选项对象
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        // Setup image capture listener which is triggered after photo has been taken
        // 设置图片捕捉监听器，在拍照后触发
        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    hideDialog()
                    LogUtil.e(exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    hideDialog()
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "拍照成功，图片被保存到app内部储存，路径为: $savedUri"
                    ToastUtils.show(msg)
                    LogUtil.d(msg)
                }
            })
    }
    //图片像素解析器
    private class LuminosityAnalyzer : ImageAnalysis.Analyzer {
        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind()    // Rewind the buffer to zero
            val data = ByteArray(remaining())
            get(data)   // Copy the buffer into a byte array
            return data // Return the byte array
        }

        override fun analyze(image: ImageProxy) {
            val buffer = image.planes[0].buffer
            val data = buffer.toByteArray()
            val pixels = data.map { it.toInt() and 0xFF }
            val luma = pixels.average()
            LogUtil.e("手机摄像头亮度[0-100，越高越亮]$luma")
            image.close()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_left -> finish()
            R.id.iv_take_photo -> {takePhoto()}
        }
    }

}