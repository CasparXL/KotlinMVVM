package com.caspar.xl.widget.captcha

import android.animation.Animator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.annotation.AttrRes
import androidx.annotation.DrawableRes
import androidx.annotation.IntDef
import com.caspar.base.ext.dp
import com.caspar.xl.R
import com.caspar.xl.databinding.ContainerBinding

/**
 * Created by luozhanming on 2018/1/17.
 */
class Captcha : LinearLayout {

    private lateinit var mBindingView: ContainerBinding

    //控件属性
    private var drawableId = -1 //验证图片资源id
    private var progressDrawableId = 0 //滑动条背景id
    private var thumbDrawableId = 0//滑动条滑块id
    private var mMode = 0 //控件验证模式(有滑动条/无滑动条)
    var maxFailedCount = 0 //最大失败次数
    private var failCount = 0 //已失败次数
    private var blockSize = 0 //拼图缺块大小

    //处理滑动条逻辑
    private var isResponse = false
    private var isDown = false
    private var mListener: CaptchaListener? = null
    private var mTask: BitmapLoaderTask? = null

    @IntDef(value = [MODE_BAR, MODE_NONBAR])
    annotation class Mode
    interface CaptchaListener {
        /**
         * Called when captcha access.
         *
         * @param time cost of access time
         * @return text to show,show default when return null
         */
        fun onAccess(time: Long): String?

        /**
         * Called when captcha failed.
         *
         * @param failCount fail count
         * @return text to show,show default when return null
         */
        fun onFailed(failCount: Int): String?

        /**
         * Called when captcha failed
         *
         * @return text to show,show default when return null
         */
        fun onMaxFailed(): String?
    }

    constructor(context: Context) : super(context) {}

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int = 0) : super(
        context,
        attrs,
        defStyleAttr) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.Captcha)
        drawableId = typedArray.getResourceId(R.styleable.Captcha_src, R.drawable.cat)
        progressDrawableId = typedArray.getResourceId(R.styleable.Captcha_progressDrawable,
            R.drawable.progressbar_upload_value_horizontal)
        thumbDrawableId =
            typedArray.getResourceId(R.styleable.Captcha_thumbDrawable, R.drawable.ic_seekbar_icon)
        mMode = typedArray.getInteger(R.styleable.Captcha_mode, MODE_BAR)
        maxFailedCount = typedArray.getInteger(R.styleable.Captcha_max_fail_count, 3)
        blockSize = typedArray.getDimensionPixelSize(R.styleable.Captcha_blockSize, 50.dp)
        typedArray.recycle()
        init()
    }

    private fun init() {
        val parentView = LayoutInflater.from(context).inflate(R.layout.container, this, true)
        mBindingView = ContainerBinding.bind(parentView)
        mode = mMode
        if (drawableId != -1) {
            mBindingView.vertifyView.setImageResource(drawableId)
        }
        setBlockSize(blockSize)
        mBindingView.vertifyView.callback(object : PictureVerifyView.Callback {
            override fun onSuccess(time: Long) {
                if (mListener != null) {
                    val s = mListener?.onAccess(time)
                    if (s != null) {
                        mBindingView.accessText.text = s
                    } else { //默认文案
                        mBindingView.accessText.text =
                            String.format(resources.getString(R.string.vertify_access), time)
                    }
                }
                mBindingView.accessRight.visibility = VISIBLE
                mBindingView.accessFailed.visibility = GONE
            }

            override fun onFailed() {
                mBindingView.seekbar.isEnabled = false
                mBindingView.vertifyView.setTouchEnable(false)
                failCount = if (failCount > maxFailedCount) maxFailedCount else failCount + 1
                mBindingView.accessFailed.visibility = VISIBLE
                mBindingView.accessRight.visibility = GONE
                if (mListener != null) {
                    if (failCount == maxFailedCount) {
                        val s = mListener?.onMaxFailed()
                        if (s != null) {
                            mBindingView.accessFailedText.text = s
                        } else { //默认文案
                            mBindingView.accessFailedText.text =
                                String.format(resources.getString(R.string.vertify_failed),
                                    maxFailedCount - failCount)
                        }
                    } else {
                        val s = mListener?.onFailed(failCount)
                        if (s != null) {
                            mBindingView.accessFailedText.text = s
                        } else { //默认文案
                            mBindingView.accessFailedText.text =
                                String.format(resources.getString(R.string.vertify_failed),
                                    maxFailedCount - failCount)
                        }
                    }
                }
            }
        })
        setSeekBarStyle(progressDrawableId, thumbDrawableId)
        //用于处理滑动条渐滑逻辑
        mBindingView.seekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (isDown) {  //手指按下
                    isDown = false
                    if (progress > 10) { //按下位置不正确
                        isResponse = false
                    } else {
                        isResponse = true
                        mBindingView.accessFailed.visibility = GONE
                        mBindingView.vertifyView.down(0)
                    }
                }
                if (isResponse) {
                    mBindingView.vertifyView.move(progress)
                } else {
                    seekBar.progress = 0
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                isDown = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                if (isResponse) {
                    mBindingView.vertifyView.loose()
                }
            }
        })
        mBindingView.refresh.setOnClickListener { v -> startRefresh(v) }
    }

    private fun startRefresh(v: View) {
        //点击刷新按钮，启动动画
        v.animate().rotationBy(360f).setDuration(500)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    reset(false)
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
    }

    fun setCaptchaListener(listener: CaptchaListener?) {
        mListener = listener
    }

    fun setCaptchaStrategy(strategy: CaptchaStrategy?) {
        if (strategy != null) {
            mBindingView.vertifyView.setCaptchaStrategy(strategy)
        }
    }

    fun setSeekBarStyle(@DrawableRes progressDrawable: Int, @DrawableRes thumbDrawable: Int) {
        mBindingView.seekbar.progressDrawable = resources.getDrawable(progressDrawable)
        mBindingView.seekbar.thumb = resources.getDrawable(thumbDrawable)
        mBindingView.seekbar.thumbOffset = 0
    }

    /**
     * 设置滑块图片大小，单位px
     */
    fun setBlockSize(blockSize: Int) {
        mBindingView.vertifyView.setBlockSize(blockSize)
    }

    /**
     * 设置滑块验证模式
     */
    private var mode: Int
        get() = mMode
        set(mode) {
            mMode = mode
            mBindingView.vertifyView.setMode(mode)
            if (mMode == MODE_NONBAR) {
                mBindingView.seekbar.visibility = GONE
                mBindingView.vertifyView.setTouchEnable(true)
            } else {
                mBindingView.seekbar.visibility = VISIBLE
                mBindingView.seekbar.isEnabled = true
            }
            hideText()
        }

    fun setBitmap(drawableId: Int) {
        val bitmap = BitmapFactory.decodeResource(resources, drawableId)
        setBitmap(bitmap)
    }

    fun setBitmap(bitmap: Bitmap?) {
        mBindingView.vertifyView.setImageBitmap(bitmap)
        reset(false)
    }

    fun setBitmap(url: String?) {
        mTask = BitmapLoaderTask(object : BitmapLoaderTask.Callback {
            override fun result(bitmap: Bitmap?) {
                setBitmap(bitmap)
            }
        })
        mTask!!.execute(url)
    }

    override fun onDetachedFromWindow() {
        if (mTask != null && mTask!!.status == AsyncTask.Status.RUNNING) {
            mTask!!.cancel(true)
        }
        super.onDetachedFromWindow()
    }

    /**
     * 复位
     * @param clearFailed 是否清除失败次数
     */
    fun reset(clearFailed: Boolean) {
        hideText()
        mBindingView.vertifyView.reset()
        if (clearFailed) {
            failCount = 0
        }
        if (mMode == MODE_BAR) {
            mBindingView.seekbar.isEnabled = true
            mBindingView.seekbar.progress = 0
        } else {
            mBindingView.vertifyView.setTouchEnable(true)
        }
    }

    /**
     * 隐藏成功失败文字显示
     */
    fun hideText() {
        mBindingView.accessText.visibility = GONE
        mBindingView.accessRight.visibility = GONE
    }

    companion object {
        /**
         * 带滑动条验证模式
         */
        const val MODE_BAR = 1

        /**
         * 不带滑动条验证，手触模式
         */
        const val MODE_NONBAR = 2
    }
}