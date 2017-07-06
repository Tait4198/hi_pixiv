package info.hzvtc.hipixiv.util

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import com.facebook.drawee.drawable.DrawableUtils

class LoadingDrawable : Drawable() {

    private lateinit var mRingBackgroundPaint: Paint
    private lateinit var mRingPaint: Paint
    private var mRingBackgroundColor: Int = 0
    private var mRingColor: Int = 0
    private var mRingRadius: Float = 0F
    private var mStrokeWidth: Float = 0F
    private val mTotalProgress = 10000
    private var mProgress: Int = 0

    init {
        val mRadius = 56f
        mStrokeWidth = 8f
        mRingBackgroundColor = 0x00000000
        mRingColor = 0xFF039BE5.toInt()
        mRingRadius = mRadius + mStrokeWidth / 2
        initVariable()
    }

    private fun initVariable() {
        mRingBackgroundPaint = Paint()
        mRingBackgroundPaint.isAntiAlias = true
        mRingBackgroundPaint.color = mRingBackgroundColor
        mRingBackgroundPaint.style = Paint.Style.STROKE
        mRingBackgroundPaint.strokeWidth = mStrokeWidth

        mRingPaint = Paint()
        mRingPaint.isAntiAlias = true
        mRingPaint.color = mRingColor
        mRingPaint.style = Paint.Style.STROKE
        mRingPaint.strokeWidth = mStrokeWidth
    }

    private fun drawBar(canvas: Canvas, level: Int, paint: Paint) {
        if (level > 0) {
            val bound = bounds
            val mXCenter = bound.centerX()
            val mYCenter = bound.centerY()
            val oval = RectF()
            oval.left = mXCenter - mRingRadius
            oval.top = mYCenter - mRingRadius
            oval.right = mRingRadius * 2 + (mXCenter - mRingRadius)
            oval.bottom = mRingRadius * 2 + (mYCenter - mRingRadius)
            canvas.drawArc(oval, -90f, level.toFloat() / mTotalProgress * 360, false, paint)
        }
    }

    override fun onLevelChange(level: Int): Boolean {
        mProgress = level
        if (level in 1..9999) {
            invalidateSelf()
            return true
        } else {
            return false
        }
    }

    override fun draw(canvas: Canvas) {
        drawBar(canvas, mTotalProgress, mRingBackgroundPaint)
        drawBar(canvas, mProgress, mRingPaint)
    }

    override fun setAlpha(alpha: Int) {
        mRingPaint.alpha = alpha
    }

    override fun getOpacity(): Int {
        return DrawableUtils.getOpacityFromColor(this.mRingPaint.color)
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        mRingPaint.colorFilter = colorFilter
    }
}