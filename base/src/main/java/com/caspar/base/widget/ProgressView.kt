package com.caspar.base.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.Parcel
import android.os.Parcelable
import android.os.SystemClock
import android.provider.Settings
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import com.caspar.base.R
import kotlin.math.min

class ProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    /** Sizes (with defaults in DP)  */
    private var mCircleRadius = 28
    private var mBarWidth = 4
    private var mRimWidth = 4
    private var mFillRadius: Boolean
    private var mTimeStartGrowing = 0.0
    private var mBarSpinCycleTime = 400.0
    private var mBarExtraLength = 0f
    private var mBarGrowingFromFront = true
    private var mPausedTimeWithoutGrowing: Long = 0

    /** Colors (with defaults)  */
    private var mBarColor = -0x56000000
    private var mRimColor = 0x00FFFFFF

    /** Paints  */
    private val mBarPaint = Paint()
    private val mRimPaint = Paint()

    /** Rectangles  */
    private var mCircleBounds = RectF()

    /** Animation The amount of degrees per second  */
    private var mSpinSpeed = 230.0f
    /** private float mSpinSpeed = 120.0f;  */
    /** The last time the spinner was animated  */
    private var mLastTimeAnimated: Long = 0
    private var mLinearProgress: Boolean
    private var mProgress = 0.0f
    private var mTargetProgress = 0.0f

    /**
     * Check if the wheel is currently spinning
     */
    var isSpinning = false
        private set
    private var mCallback: ProgressCallback? = null
    private val mShouldAnimate: Boolean
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width: Int
        val height: Int
        val viewWidth = mCircleRadius + this.paddingLeft + this.paddingRight
        val viewHeight = mCircleRadius + this.paddingTop + this.paddingBottom
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        width = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST, MeasureSpec.UNSPECIFIED -> Math.min(viewWidth, widthSize)
            else -> viewWidth
        }
        height = if (heightMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.EXACTLY) {
            heightSize
        } else if (heightMode == MeasureSpec.AT_MOST) {
            Math.min(viewHeight, heightSize)
        } else {
            viewHeight
        }
        setMeasuredDimension(width, height)
    }

    /**
     * Use onSizeChanged instead of onAttachedToWindow to get the dimensions of the view,
     * because this method is called after measuring the dimensions of MATCH_PARENT & WRAP_CONTENT.
     * Use this dimensions to setup the bounds and paints.
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setupBounds(w, h)
        setupPaints()
        invalidate()
    }

    /**
     * Set the properties of the paints we're using to
     * draw the progress wheel
     */
    private fun setupPaints() {
        mBarPaint.color = mBarColor
        mBarPaint.isAntiAlias = true
        mBarPaint.style = Paint.Style.STROKE
        mBarPaint.strokeWidth = mBarWidth.toFloat()
        mRimPaint.color = mRimColor
        mRimPaint.isAntiAlias = true
        mRimPaint.style = Paint.Style.STROKE
        mRimPaint.strokeWidth = mRimWidth.toFloat()
    }

    /**
     * Set the bounds of the component
     */
    private fun setupBounds(layoutWidth: Int, layoutHeight: Int) {
        val paddingTop = paddingTop
        val paddingBottom = paddingBottom
        val paddingLeft = paddingLeft
        val paddingRight = paddingRight
        mCircleBounds = if (!mFillRadius) {
            // Width should equal to Height, find the min value to setup the circle
            val minValue = Math.min(
                layoutWidth - paddingLeft - paddingRight,
                layoutHeight - paddingBottom - paddingTop
            )
            val circleDiameter =
                Math.min(minValue, mCircleRadius * 2 - mBarWidth * 2)

            // Calc the Offset if needed for centering the wheel in the available space
            val xOffset =
                (layoutWidth - paddingLeft - paddingRight - circleDiameter) / 2 + paddingLeft
            val yOffset =
                (layoutHeight - paddingTop - paddingBottom - circleDiameter) / 2 + paddingTop
            RectF(
                (xOffset + mBarWidth).toFloat(),
                (yOffset + mBarWidth).toFloat(),
                (xOffset + circleDiameter - mBarWidth).toFloat(),
                (yOffset + circleDiameter - mBarWidth).toFloat()
            )
        } else {
            RectF(
                (paddingLeft + mBarWidth).toFloat(),
                (paddingTop + mBarWidth).toFloat(),
                (layoutWidth - paddingRight - mBarWidth).toFloat(),
                (layoutHeight - paddingBottom - mBarWidth).toFloat()
            )
        }
    }

    fun setCallback(progressCallback: ProgressCallback?) {
        mCallback = progressCallback
        if (!isSpinning) {
            runCallback()
        }
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawArc(mCircleBounds, 360f, 360f, false, mRimPaint)
        var mustInvalidate = false
        if (!mShouldAnimate) {
            return
        }
        if (isSpinning) {
            //Draw the spinning bar
            mustInvalidate = true
            val deltaTime = SystemClock.uptimeMillis() - mLastTimeAnimated
            val deltaNormalized = deltaTime * mSpinSpeed / 1000.0f
            updateBarLength(deltaTime)
            mProgress += deltaNormalized
            if (mProgress > 360) {
                mProgress -= 360f

                // A full turn has been completed
                // we run the callback with -1 in case we want to
                // do something, like changing the color
                runCallback(-1.0f)
            }
            mLastTimeAnimated = SystemClock.uptimeMillis()
            var from = mProgress - 90
            var length = BAR_LENGTH + mBarExtraLength
            if (isInEditMode) {
                from = 0f
                length = 135f
            }
            canvas.drawArc(mCircleBounds, from, length, false, mBarPaint)
        } else {
            val oldProgress = mProgress
            if (mProgress != mTargetProgress) {
                //We smoothly increase the progress bar
                mustInvalidate = true
                val deltaTime =
                    (SystemClock.uptimeMillis() - mLastTimeAnimated).toFloat() / 1000
                val deltaNormalized = deltaTime * mSpinSpeed
                mProgress = Math.min(mProgress + deltaNormalized, mTargetProgress)
                mLastTimeAnimated = SystemClock.uptimeMillis()
            }
            if (oldProgress != mProgress) {
                runCallback()
            }
            var offset = 0.0f
            var progress = mProgress
            if (!mLinearProgress) {
                val factor = 2.0f
                offset = (1.0f - Math.pow(
                    1.0f - mProgress / 360.0f.toDouble(),
                    2.0f * factor.toDouble()
                )).toFloat() * 360.0f
                progress = (1.0f - Math.pow(
                    1.0f - mProgress / 360.0f.toDouble(),
                    factor.toDouble()
                )).toFloat() * 360.0f
            }
            if (isInEditMode) {
                progress = 360f
            }
            canvas.drawArc(mCircleBounds, offset - 90, progress, false, mBarPaint)
        }
        if (mustInvalidate) {
            invalidate()
        }
    }

    override fun onVisibilityChanged(
        changedView: View,
        visibility: Int
    ) {
        super.onVisibilityChanged(changedView, visibility)
        if (visibility == VISIBLE) {
            mLastTimeAnimated = SystemClock.uptimeMillis()
        }
    }

    private fun updateBarLength(deltaTimeInMilliSeconds: Long) {
        if (mPausedTimeWithoutGrowing >= PAUSE_GROWING_TIME) {
            mTimeStartGrowing += deltaTimeInMilliSeconds.toDouble()
            if (mTimeStartGrowing > mBarSpinCycleTime) {
                // We completed a size change cycle
                // (growing or shrinking)
                mTimeStartGrowing -= mBarSpinCycleTime
                //if(mBarGrowingFromFront) {
                mPausedTimeWithoutGrowing = 0
                //}
                mBarGrowingFromFront = !mBarGrowingFromFront
            }
            val distance =
                Math.cos((mTimeStartGrowing / mBarSpinCycleTime + 1) * Math.PI).toFloat() / 2 + 0.5f
            val destLength =
                (BAR_MAX_LENGTH - BAR_LENGTH).toFloat()
            if (mBarGrowingFromFront) {
                mBarExtraLength = distance * destLength
            } else {
                val newLength = destLength * (1 - distance)
                mProgress += mBarExtraLength - newLength
                mBarExtraLength = newLength
            }
        } else {
            mPausedTimeWithoutGrowing += deltaTimeInMilliSeconds
        }
    }

    /**
     * Reset the count (in increment mode)
     */
    fun resetCount() {
        mProgress = 0.0f
        mTargetProgress = 0.0f
        invalidate()
    }

    /**
     * Turn off spin mode
     */
    fun stopSpinning() {
        isSpinning = false
        mProgress = 0.0f
        mTargetProgress = 0.0f
        invalidate()
    }

    /**
     * Puts the view on spin mode
     */
    fun spin() {
        mLastTimeAnimated = SystemClock.uptimeMillis()
        isSpinning = true
        invalidate()
    }

    private fun runCallback(value: Float) {
        if (mCallback != null) {
            mCallback!!.onProgressUpdate(value)
        }
    }

    private fun runCallback() {
        if (mCallback != null) {
            val normalizedProgress =
                Math.round(mProgress * 100 / 360.0f).toFloat() / 100
            mCallback!!.onProgressUpdate(normalizedProgress)
        }
    }

    /**
     * Set the progress to a specific value,
     * the bar will be set instantly to that value
     *
     * @param progress the progress between 0 and 1
     */
    fun setInstantProgress(pgs: Float) {
        var progress = pgs
        if (isSpinning) {
            mProgress = 0.0f
            isSpinning = false
        }
        if (progress > 1.0f) {
            progress -= 1.0f
        } else if (progress < 0) {
            progress = 0f
        }
        if (progress == mTargetProgress) {
            return
        }
        mTargetProgress = min(progress * 360.0f, 360.0f)
        mProgress = mTargetProgress
        mLastTimeAnimated = SystemClock.uptimeMillis()
        invalidate()
    }

    // Great way to save a view's state http://stackoverflow.com/a/7089687/1991053
    public override fun onSaveInstanceState(): Parcelable? {
        val savedState = WheelSavedState(super.onSaveInstanceState())
        // We save everything that can be changed at runtime
        savedState.mProgress = mProgress
        savedState.mTargetProgress = mTargetProgress
        savedState.isSpinning = isSpinning
        savedState.spinSpeed = mSpinSpeed
        savedState.barWidth = mBarWidth
        savedState.barColor = mBarColor
        savedState.rimWidth = mRimWidth
        savedState.rimColor = mRimColor
        savedState.circleRadius = mCircleRadius
        savedState.linearProgress = mLinearProgress
        savedState.fillRadius = mFillRadius
        return savedState
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        if (state !is WheelSavedState) {
            super.onRestoreInstanceState(state)
            return
        }
        val savedState = state
        super.onRestoreInstanceState(savedState.superState)
        mProgress = savedState.mProgress
        mTargetProgress = savedState.mTargetProgress
        isSpinning = savedState.isSpinning
        mSpinSpeed = savedState.spinSpeed
        mBarWidth = savedState.barWidth
        mBarColor = savedState.barColor
        mRimWidth = savedState.rimWidth
        mRimColor = savedState.rimColor
        mCircleRadius = savedState.circleRadius
        mLinearProgress = savedState.linearProgress
        mFillRadius = savedState.fillRadius
        mLastTimeAnimated = SystemClock.uptimeMillis()
    }

    /**
     * @return the current progress between 0.0 and 1.0,
     * if the wheel is indeterminate, then the result is -1
     */// If we are currently in the right position
    // we set again the last time animated so the
    // animation starts smooth from here
    /**
     * Set the progress to a specific value,
     * the bar will smoothly animate until that value
     *
     * @param progress the progress between 0 and 1
     */
    var progress: Float
        get() = if (isSpinning) -1f else mProgress / 360.0f
        set(pgs) {
            var progress = pgs
            if (isSpinning) {
                mProgress = 0.0f
                isSpinning = false
                runCallback()
            }
            if (progress > 1.0f) {
                progress -= 1.0f
            } else if (progress < 0) {
                progress = 0f
            }
            if (progress == mTargetProgress) {
                return
            }

            // If we are currently in the right position
            // we set again the last time animated so the
            // animation starts smooth from here
            if (mProgress == mTargetProgress) {
                mLastTimeAnimated = SystemClock.uptimeMillis()
            }
            mTargetProgress = Math.min(progress * 360.0f, 360.0f)
            invalidate()
        }
    //----------------------------------
    //Getters + setters
    //----------------------------------

    /**
     * Sets the determinate progress mode
     *
     * @param isLinear if the progress should increase linearly
     */
    fun setLinearProgress(isLinear: Boolean) {
        mLinearProgress = isLinear
        if (!isSpinning) {
            invalidate()
        }
    }

    /**
     * @return the radius of the wheel in pixels
     */
    /**
     * Sets the radius of the wheel
     *
     * @param circleRadius the expected radius, in pixels
     */
    var circleRadius: Int
        get() = mCircleRadius
        set(circleRadius) {
            mCircleRadius = circleRadius
            if (!isSpinning) {
                invalidate()
            }
        }

    /**
     * @return the width of the spinning bar
     */
    /**
     * Sets the width of the spinning bar
     *
     * @param barWidth the spinning bar width in pixels
     */
    var barWidth: Int
        get() = mBarWidth
        set(barWidth) {
            mBarWidth = barWidth
            if (!isSpinning) {
                invalidate()
            }
        }

    /**
     * @return the color of the spinning bar
     */
    /**
     * Sets the color of the spinning bar
     *
     * @param barColor The spinning bar color
     */
    var barColor: Int
        get() = mBarColor
        set(barColor) {
            mBarColor = barColor
            setupPaints()
            if (!isSpinning) {
                invalidate()
            }
        }

    /**
     * @return the color of the wheel's contour
     */
    /**
     * Sets the color of the wheel's contour
     *
     * @param rimColor the color for the wheel
     */
    var rimColor: Int
        get() = mRimColor
        set(rimColor) {
            mRimColor = rimColor
            setupPaints()
            if (!isSpinning) {
                invalidate()
            }
        }

    /**
     * @return the base spinning speed, in full circle turns per second
     * (1.0 equals on full turn in one second), this value also is applied for
     * the smoothness when setting a progress
     */
    /**
     * Sets the base spinning speed, in full circle turns per second
     * (1.0 equals on full turn in one second), this value also is applied for
     * the smoothness when setting a progress
     *
     * @param spinSpeed the desired base speed in full turns per second
     */
    var spinSpeed: Float
        get() = mSpinSpeed / 360.0f
        set(spinSpeed) {
            mSpinSpeed = spinSpeed * 360.0f
        }

    /**
     * @return the width of the wheel's contour in pixels
     */
    /**
     * Sets the width of the wheel's contour
     *
     * @param rimWidth the width in pixels
     */
    var rimWidth: Int
        get() = mRimWidth
        set(rimWidth) {
            mRimWidth = rimWidth
            if (!isSpinning) {
                invalidate()
            }
        }

    interface ProgressCallback {
        /**
         * Method to call when the progress reaches a value
         * in order to avoid float precision issues, the progress
         * is rounded to a float with two decimals.
         *
         *
         * In indeterminate mode, the callback is called each time
         * the wheel completes an animation cycle, with, the progress value is -1.0f
         *
         * @param progress a double value between 0.00 and 1.00 both included
         */
        fun onProgressUpdate(progress: Float)
    }

    internal class WheelSavedState : BaseSavedState {
        var mProgress = 0f
        var mTargetProgress = 0f
        var isSpinning = false
        var spinSpeed = 0f
        var barWidth = 0
        var barColor = 0
        var rimWidth = 0
        var rimColor = 0
        var circleRadius = 0
        var linearProgress = false
        var fillRadius = false

        constructor(superState: Parcelable?) : super(superState) {}
        private constructor(`in`: Parcel) : super(`in`) {
            mProgress = `in`.readFloat()
            mTargetProgress = `in`.readFloat()
            isSpinning = `in`.readByte().toInt() != 0
            spinSpeed = `in`.readFloat()
            barWidth = `in`.readInt()
            barColor = `in`.readInt()
            rimWidth = `in`.readInt()
            rimColor = `in`.readInt()
            circleRadius = `in`.readInt()
            linearProgress = `in`.readByte().toInt() != 0
            fillRadius = `in`.readByte().toInt() != 0
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeFloat(mProgress)
            out.writeFloat(mTargetProgress)
            out.writeByte((if (isSpinning) 1 else 0).toByte())
            out.writeFloat(spinSpeed)
            out.writeInt(barWidth)
            out.writeInt(barColor)
            out.writeInt(rimWidth)
            out.writeInt(rimColor)
            out.writeInt(circleRadius)
            out.writeByte((if (linearProgress) 1 else 0).toByte())
            out.writeByte((if (fillRadius) 1 else 0).toByte())
        }

        companion object {
            // required field that makes Parcelables from a Parcel
            val CREATOR: Parcelable.Creator<WheelSavedState> =
                object : Parcelable.Creator<WheelSavedState> {
                    override fun createFromParcel(`in`: Parcel): WheelSavedState? {
                        return WheelSavedState(`in`)
                    }

                    override fun newArray(size: Int): Array<WheelSavedState?> {
                        return arrayOfNulls(size)
                    }
                }
        }
    }

    companion object {
        private const val BAR_LENGTH = 16
        private const val BAR_MAX_LENGTH = 270
        private const val PAUSE_GROWING_TIME: Long = 200
    }

    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.ProgressView)
        mBarWidth = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            mBarWidth.toFloat(),
            resources.displayMetrics
        ).toInt()
        mRimWidth = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            mRimWidth.toFloat(),
            resources.displayMetrics
        ).toInt()
        mCircleRadius = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            mCircleRadius.toFloat(),
            resources.displayMetrics
        ).toInt()
        mCircleRadius = array.getDimension(
            R.styleable.ProgressView_circleRadius,
            mCircleRadius.toFloat()
        ).toInt()
        mFillRadius = array.getBoolean(R.styleable.ProgressView_fillRadius, false)
        mBarWidth =
            array.getDimension(R.styleable.ProgressView_barWidth, mBarWidth.toFloat()).toInt()
        mRimWidth =
            array.getDimension(R.styleable.ProgressView_rimWidth, mRimWidth.toFloat()).toInt()
        val baseSpinSpeed =
            array.getFloat(R.styleable.ProgressView_spinSpeed, mSpinSpeed / 360.0f)
        mSpinSpeed = baseSpinSpeed * 360
        mBarSpinCycleTime =
            array.getInt(R.styleable.ProgressView_barSpinCycleTime, mBarSpinCycleTime.toInt())
                .toDouble()
        mBarColor = array.getColor(R.styleable.ProgressView_barColor, mBarColor)
        mRimColor = array.getColor(R.styleable.ProgressView_rimColor, mRimColor)
        mLinearProgress = array.getBoolean(R.styleable.ProgressView_linearProgress, false)
        if (array.getBoolean(R.styleable.ProgressView_progressIndeterminate, false)) {
            spin()
        }
        array.recycle()
        val animationValue = Settings.Global.getFloat(
            getContext().contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f
        )
        mShouldAnimate = animationValue != 0f
    }
}