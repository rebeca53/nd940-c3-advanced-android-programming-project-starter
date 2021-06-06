package com.udacity

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates
import kotlin.math.min

/**
 * width of the button gets animated from left to right - drawRect - background green, loading is blue
 * text gets changed based on different states of the button - paint white, type face, drawText, text changes Download to We are Loading
 * circle gets be animated from 0 to 360 degrees - drawOval, drawArc, fill with setStyle
 */
class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var radius: Float = 0F
    private var text = ""

    private val valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Loading) { p, old, new ->
    }

    //styles attributes
    private var loadingColor = 0
    private var defaultColor = 0
    private var textButtonDefault = ""
    private var textButtonLoading = ""

    companion object {
        private const val TAG = "LoadingButton"
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isAntiAlias = true
        isDither = true
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create( "", Typeface.NORMAL)
    }

    init {
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            loadingColor = getColor(R.styleable.LoadingButton_boxLoadingColor, 0)
            textButtonDefault = getString(R.styleable.LoadingButton_textDefault).toString()
            textButtonLoading = getString(R.styleable.LoadingButton_textLoading).toString()
        }
        text = textButtonDefault
        isClickable = true
    }

    //todo function that will update widthsize of a clipping region of rectangle then call invalidate

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        radius = (min(width, height) / 2.0 * 0.8).toFloat()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        //set background color
        canvas?.drawColor(defaultColor)
        //draw background rectangle
        drawLoadingRectangle(canvas)

        //draw loading circle
        drawLoadingCircle(canvas)

        // draw text
        drawText(canvas)
    }

    private fun drawLoadingRectangle(canvas: Canvas?) {
        canvas?.save() //-> do it to each draw
        paint.color = loadingColor
        //todo how to animate rect width and loading circle sweepAngle -> seekable animation
        canvas?.drawRect(0F, 0F, (widthSize.toFloat() / 2), (height.toFloat()), paint)
        canvas?.restore() //to each draw
    }

    //todo set position and animate
    private fun drawLoadingCircle(canvas: Canvas?) {
        canvas?.save() //-> do it to each draw

        paint.color = context.getColor(R.color.colorAccent)
        canvas?.drawArc(
            (width / 2).toFloat() - radius,
            (height / 2).toFloat() - radius,
            (width / 2).toFloat() + radius,
            (height / 2).toFloat() + radius,
            0F,
            90F,
            true,
            paint)
        canvas?.restore() //to each draw
    }

    //todo set position
    private fun drawText(canvas: Canvas?) {
        canvas?.save() //-> do it to each draw

        //canvas.translate() // -> set position to each draw
        paint.color = Color.WHITE
        canvas?.drawText(text, (width / 2).toFloat(), (height / 2).toFloat(), paint)
        canvas?.restore() //to each draw
    }

    override fun performClick(): Boolean {
        if (super.performClick()) return true
        Log.d("rebeca", "perfomr click")
        updateButtonState()
        return true
    }

    private fun updateButtonState() {
        Log.d("rebeca", "Action up")
        buttonState = buttonState.next()
        text = when (buttonState) {
            ButtonState.Loading -> {
//                isClickable = false
                textButtonLoading
            }
            else -> {
//                isClickable = true
                textButtonDefault
            }
        }
        invalidate() // will call onDraw
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}