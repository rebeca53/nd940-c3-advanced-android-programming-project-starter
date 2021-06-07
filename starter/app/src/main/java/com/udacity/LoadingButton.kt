package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
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
    private var circleCenterX: Float = 0F
    private var circleCenterY: Float = 0F
    private var text = ""
    private var textBounds = Rect()
    private var progress = 0F
    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
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

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        radius = (min(width, height) / 2.0 * 0.5).toFloat()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        //set background color
        canvas?.drawColor(defaultColor)
        //draw background rectangle
        drawLoadingRectangle(canvas)

        // draw text
        drawText(canvas)

        //draw loading circle
        drawLoadingCircle(canvas)
    }

    private fun drawLoadingRectangle(canvas: Canvas?) {
        canvas?.save()
        paint.color = loadingColor
        //todo seekable animation
        canvas?.drawRect(0F, 0F, progress * widthSize, heightSize.toFloat(), paint)
        canvas?.restore()
    }

    private fun drawLoadingCircle(canvas: Canvas?) {
        canvas?.save()
        paint.color = context.getColor(R.color.colorAccent)
        circleCenterX = (width / 2F) + (textBounds.width() / 2F) + radius
        circleCenterY = (height / 2F)
        canvas?.drawArc(
            circleCenterX - radius,
            circleCenterY - radius,
            circleCenterX + radius,
            circleCenterY + radius,
            0F,
            progress * 360F,
            true,
            paint)
        canvas?.restore()
    }

    private fun drawText(canvas: Canvas?) {
        canvas?.save()
        text = when (buttonState) {
            ButtonState.Loading -> textButtonLoading
            else -> textButtonDefault
        }
        paint.getTextBounds(text, 0, text.length, textBounds)
        paint.color = Color.WHITE
        canvas?.drawText(
            text,
            (width / 2).toFloat() ,
            (height / 2).toFloat() - textBounds.exactCenterY(),
            paint)
        canvas?.restore()
    }

    @SuppressLint("ObjectAnimatorBinding")
    fun loadAnimation() {
        buttonState = ButtonState.Loading
        val animator = ObjectAnimator.ofFloat(this, "progress", 0f, 100F)
        animator.duration = 3000
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                isEnabled = false
            }

            override fun onAnimationEnd(animation: Animator?) {
                isEnabled = true
                buttonState = ButtonState.Completed
                progress = 0F
                invalidate()
            }
        })

        animator.addUpdateListener {
            progress = (it.animatedValue as Float) / 100F
            invalidate()
        }
        animator.start()
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