package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
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
    private var buttonStateLoadingColor = 0
    private var buttonStateCompletedColor = 0
    private val valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Loading) { p, old, new ->
    }


    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create( "", Typeface.BOLD)
    }


    init {
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            buttonStateCompletedColor = getColor(R.styleable.LoadingButton_boxBackgroundColor1, 0)
            buttonStateLoadingColor = getColor(R.styleable.LoadingButton_boxBackgroundColor2, 0)
        }
        isClickable = true
    }

    override fun performClick(): Boolean {
        if (super.performClick()) return true

        Log.d("rebeca", "perfomr click")
        buttonState = buttonState.next()

        invalidate()
        return true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        radius = (min(width, height) / 2.0 * 0.8).toFloat()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        //draw background rectangle
        paint.color = when (buttonState) {
            ButtonState.Loading -> buttonStateLoadingColor
            else -> buttonStateCompletedColor
        }
        canvas?.drawRect(0F, 0F, (width.toFloat()), (height.toFloat()), paint)

        //draw loading circle
        //todo set background color using
        paint.color = Color.BLUE
        canvas?.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), radius, paint)        //canvas.drawCircle()

        // draw text
        paint.color = Color.WHITE
        val text = if (buttonState == ButtonState.Loading)
            context.getString(R.string.button_loading)
        else
            context.getString(R.string.button_name)
        canvas?.drawText(text, (width / 2).toFloat(), (height / 2).toFloat(), paint)
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