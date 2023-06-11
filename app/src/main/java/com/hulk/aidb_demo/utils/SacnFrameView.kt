package com.hulk.aidb_demo.utils

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.Canvas.ALL_SAVE_FLAG
import android.util.AttributeSet
import android.view.View
import android.view.animation.AnticipateInterpolator
import android.view.animation.LinearInterpolator
import androidx.annotation.Nullable
import com.hulk.aidb_demo.R
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

@SuppressLint("CustomViewStyleable")
class ScanFrameView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private var mScanLinePaint: Paint

    //渐变色的起始值
    private var mStartColor: Int
    //渐变色的终止值
    private var mFinishColor: Int

    //圆形半径
    private var mRadius: Float
    //整个view的宽度
    private var mWidth: Float = 0.0f
    //整个view的高度度
    private var mHeight: Float = 0.0f
    //圆心的横坐标
    private var mCenterX: Float = 0.0f
    //圆心的纵坐标
    private var mCenterY: Float = 0.0f

    //扫描线（梯形）右上角的点对圆心的角度
    private var mDegree: Double = 0.0
    //扫描线（梯形）右下角的点对圆心的角度-右上角的点对圆心的角度的差值，体现为扫描线的线宽
    private var mDegreeAdd: Double = 1.0
    //遮罩的颜色
    private var mBackGroundColor: Int
    //扫描的动画时间
    private var mDuration: Int
    private var mAnimator: ValueAnimator? = null

    init {
        @SuppressLint("Recycle")
        val typedArray: TypedArray =
            context.obtainStyledAttributes(attrs, R.styleable.ScanFrameViewStyle)

        mBackGroundColor = typedArray.getColor(R.styleable.ScanFrameViewStyle_backGroundColor, Color.WHITE)

        mRadius = typedArray.getDimension(R.styleable.ScanFrameViewStyle_circleRadius, 0.0f)


        mScanLinePaint = Paint()
        mScanLinePaint.run {
            style = Paint.Style.FILL
            strokeCap = Paint.Cap.ROUND
            isAntiAlias = true
            isDither = true
            color = Color.GREEN
        }
        mDegreeAdd = typedArray.getFloat(R.styleable.ScanFrameViewStyle_scanLineWidth, 1.0f).toDouble()
        mDuration = typedArray.getInt(R.styleable.ScanFrameViewStyle_scanDuration, 10000)
        mStartColor = typedArray.getColor(R.styleable.ScanFrameViewStyle_scanLineStartColor, -1)
        mFinishColor = typedArray.getColor(R.styleable.ScanFrameViewStyle_scanLineFinishColor, -1)

        typedArray.recycle()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth = (measuredWidth - paddingLeft - paddingRight).toFloat()
        mHeight = (measuredHeight - paddingTop - paddingBottom).toFloat()
        mCenterX = mWidth / 2
        mCenterY = mHeight / 2

        val short = min(mWidth, mHeight)
        if (mRadius == 0.0f || mRadius * 2 > short) {
            mRadius = short / 2
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //角度制转弧度制
        val radians = Math.toRadians(mDegree).toFloat()
        val radiansAdd = Math.toRadians(mDegree + mDegreeAdd).toFloat()

        //着色器，实现渐变效果
        takeIf { mStartColor != -1 && mFinishColor != -1 }?.run {
            val y0 = mCenterY - mRadius * cos(radians)
            val y1 = mCenterY - mRadius * cos(radiansAdd)
            mScanLinePaint.shader = LinearGradient(0.0f, max(y0, y1), 0.0f, min(y0, y1), mStartColor, mFinishColor, Shader.TileMode.CLAMP)
        }

        //绘制扫描线，梯形
        val path = Path()
        path.moveTo(mCenterX + mRadius * sin(-radians), mCenterY - mRadius * cos(radians))
        path.lineTo(mCenterX + mRadius * sin(radians), mCenterY - mRadius * cos(radians))
        path.lineTo(mCenterX + mRadius * sin(radiansAdd), mCenterY - mRadius * cos(radiansAdd))
        path.lineTo(mCenterX + mRadius * sin(-radiansAdd), mCenterY - mRadius * cos(radiansAdd))
        path.close()
        canvas.drawPath(path, mScanLinePaint)
    }


    //扫描动画，通过改变角度，实现扫描线上下移动
    fun startScan() {
        mAnimator?.run { cancel() }
        mAnimator = ValueAnimator.ofFloat(0.0f, 360.0f)
        mAnimator?.let {
            it.addUpdateListener { animation ->
                this.mDegree = (animation.animatedValue as Float).toDouble()
                invalidate()
            }
            it.duration = mDuration.toLong()
            it.interpolator = LinearInterpolator()
            it.repeatCount = ValueAnimator.INFINITE
            it.repeatMode = ValueAnimator.RESTART
            it.start()
        }

    }

}
