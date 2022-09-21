package com.caspar.xl.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.*
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.caspar.base.base.BaseActivity
import com.caspar.base.base.BaseDialog
import com.caspar.commom.ext.setOnClickListener
import com.caspar.commom.helper.LogUtil
import com.caspar.xl.R
import com.caspar.xl.databinding.ActivityCameraBinding
import com.caspar.xl.ui.dialog.WaitDialog
import java.io.File
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : BaseActivity<ActivityCameraBinding>(), View.OnClickListener {
    var cameraControl: CameraControl? = null
    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private lateinit var imageAnalyzer: ImageAnalysis
    private var preview: Preview? = null
    private var camera: Camera? = null
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    var create :BaseDialog? = null

    override fun initView(savedInstanceState: Bundle?) {
        mBindingView.title.tvCenter.text = "CameraX"
        setOnClickListener(this, R.id.tv_left, R.id.iv_take_photo)
        startCamera()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    //开始预览，CameraX绑定Activity，随生命周期销毁而自动销毁
    @SuppressLint("ClickableViewAccessibility")
    private fun startCamera() {
        scaleGestureDetector = ScaleGestureDetector(this, listener)
        //初始化双指缩放的控制器
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            imageCapture = ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build()

            imageAnalyzer = ImageAnalysis.Builder().build().also {
                it.setAnalyzer(cameraExecutor, LuminosityAnalyzer())
            }
            // Used to bind the lifecycle of cameras to the lifecycle owner
            //用于将相机的生命周期绑定到生命周期所有者
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            // Preview
            //预览
            preview = Preview.Builder().build()
            // Select back camera
            // 使用相机
            val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
            try {
                // Unbind use cases before rebinding
                //在重新绑定之前解除用例绑定
                cameraProvider.unbindAll()
                // Bind use cases to camera
                //将用例绑定到摄像机
                camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture, imageAnalyzer)
                cameraControl = camera?.cameraControl
                preview?.setSurfaceProvider(mBindingView.viewFinder.surfaceProvider)
            } catch (exc: Exception) {
                LogUtil.e(exc)
            }
        }, ContextCompat.getMainExecutor(this))
        initImageCapture()
        mBindingView.viewFinder.setOnTouchListener(View.OnTouchListener { v, event ->
            run {
                if (event.action == MotionEvent.ACTION_UP) { //加这个判断是为了缩放以后自动聚焦，单击也可以聚焦
                    onTouch(v.x, v.y)
                }
                scaleGestureDetector.onTouchEvent(event) //将event传给监听，进行自动缩放
                return@OnTouchListener true
            }
        })
    }

    // 创建一个名为 listener 的回调函数，当手势事件发生时会调用这个回调函数
    private val listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            // 获取当前的摄像头的缩放比例
            val currentZoomRatio: Float = camera?.cameraInfo?.zoomState?.value?.zoomRatio ?: 1F
            // 获取用户捏拉手势所更改的缩放比例
            val delta = detector.scaleFactor
            LogUtil.e("当前比例:$currentZoomRatio 手势更改比例 $delta")
            // 更新摄像头的缩放比例
            cameraControl?.setZoomRatio(currentZoomRatio * delta)
            return true
        }
    }

    // 点击聚焦的方法
    private fun onTouch(x: Float, y: Float) {
        // 创建 MeteringPoint，命名为 factory
        val factory = mBindingView.viewFinder.meteringPointFactory
        // 将 UI 界面的坐标转换为摄像头传感器的坐标
        val point = factory.createPoint(x, y)
        // 创建对焦需要用的 action
        val action = FocusMeteringAction.Builder(point).build()
        // 执行所创建的对焦 action
        cameraControl?.startFocusAndMetering(action)
    }

    //拍照
    private fun takePhoto() {
        if (create == null){
            create = WaitDialog.Builder(this).setMessage("稍等").create()
            create?.show()
        } else {
            create?.show()
        }
        // Get a stable reference of the modifiable image capture use case
        //获得可修改映像捕获用例的稳定引用
        val imageCapture = imageCapture ?: return
        // Create timestamped output file to hold the image
        // 创建带有时间戳的输出文件来保存图像
        val photoFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US).format(System.currentTimeMillis()) + ".jpg")
        // Create output options object which contains file + metadata
        // 创建包含文件+元数据的输出选项对象
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile.outputStream()).build()
        // Setup image capture listener which is triggered after photo has been taken
        // 设置图片捕捉监听器，在拍照后触发
        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageSavedCallback {
            override fun onError(exc: ImageCaptureException) {
                create?.hide()
                LogUtil.e(exc)
            }

            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                create?.hide()
                val savedUri = Uri.fromFile(photoFile)
                val msg = "拍照成功，图片被保存到app内部储存，路径为: $savedUri"
                toast(msg)
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
            val lightness = pixels.average()
            LogUtil.e("手机摄像头亮度[0-100，越高越亮]$lightness")
            image.close()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_left -> finish()
            R.id.iv_take_photo -> {
                takePhoto()
            }
        }
    }

    private fun initImageCapture() {
        // 旋转监听
        val orientationEventListener: OrientationEventListener = object : OrientationEventListener(this as Context) {
            override fun onOrientationChanged(orientation: Int) {
                // Monitors orientation values to determine the target rotation value
                val rotation: Int = when (orientation) {
                    in 45..134 -> {
                        Surface.ROTATION_270
                    }
                    in 135..224 -> {
                        Surface.ROTATION_180
                    }
                    in 225..314 -> {
                        Surface.ROTATION_90
                    }
                    else -> {
                        Surface.ROTATION_0
                    }
                }
                imageCapture?.targetRotation = rotation
            }
        }
        orientationEventListener.enable()
    }



}