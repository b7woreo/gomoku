package com.chrnie.gomoku.app.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import kotlin.math.max
import kotlin.math.min


abstract class PixelView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0)
    : View(context, attrs, defStyleAttr, defStyleRes) {

    protected open fun beforeDrawPixel(canvas: Canvas, rect: Rect) {

    }

    protected abstract fun onDrawPixel(canvas: Canvas, x: Int, y: Int)

    protected open fun afterDrawPixel(canvas: Canvas, rect: Rect) {

    }

    private val rect = Rect()
    private var pixelWidth = 0
    private var pixelHeight = 0

    var offsetX: Int = 0
        set(offsetX) {
            if (this.offsetX == offsetX) {
                return
            }

            field = offsetX
            invalidate()
        }
    var offsetY: Int = 0
        set(offsetY) {
            if (this.offsetY == offsetY) {
                return
            }

            field = offsetY
            invalidate()
        }

    var zoom: Int = 0
        set(zoom) {
            if (this.zoom == zoom) {
                return
            }

            field = zoom
            invalidate()
        }

    fun zoomAdjustViewSize() {
        if (pixelWidth == 0 || pixelHeight == 0) {
            return
        }

        val zoomWidth = width / pixelWidth
        val zoomHeight = height / pixelHeight
        zoom = min(zoomWidth, zoomHeight)
    }

    fun centerContent() {
        offsetX = (width - (zoom * pixelWidth)) / 2
        offsetY = (height - (zoom * pixelHeight)) / 2
    }

    fun transformationViewCoordinate(x: Int, y: Int, out: Point? = null): Point? {
        val result = out ?: Point()

        val pixelX = (x - offsetX) / zoom
        val pixelY = (y - offsetY) / zoom

        if (pixelX !in 0 until pixelWidth || pixelY !in 0 until pixelHeight) {
            return null
        }

        result.set(pixelX, pixelY)
        return result
    }

    fun getPixelRect(pixelX: Int, pixelY: Int, out: Rect? = null): Rect {
        if (pixelX !in 0 until pixelWidth || pivotY !in 0 until pixelHeight) {
            throw RuntimeException("($pixelX,$pixelY) not in bound ($pixelWidth,$pixelHeight)")
        }

        val result = out ?: Rect()

        val left = pixelX * zoom + offsetX
        val top = pixelY * zoom + offsetY

        result.set(left, top, left + zoom, right + zoom)
        return result
    }

    protected fun setPixelSize(pixelWidth: Int, pixelHeight: Int) {
        this.pixelWidth = pixelWidth
        this.pixelHeight = pixelHeight
        invalidate()
    }

    override fun getSuggestedMinimumWidth(): Int {
        val contentWidth = zoom * pixelWidth
        return max(minimumWidth, contentWidth)
    }

    override fun getSuggestedMinimumHeight(): Int {
        val contentHeight = zoom * pixelHeight
        return max(minimumWidth, contentHeight)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (pixelHeight == 0 || pixelWidth == 0 || this.zoom == 0) {
            return
        }

        val leftPos = max(0, -offsetX / zoom)
        val topPos = max(0, -offsetY / zoom)
        val rightPos = min(pixelWidth, (width - offsetX) / zoom)
        val bottomPos = min(pixelHeight, (height - offsetY) / zoom)

        rect.set(
                max(0, offsetX),
                max(0, offsetY),
                min(width, offsetX + pixelWidth * zoom),
                min(height, offsetY + pixelHeight * zoom)
        )
        beforeDrawPixel(canvas, rect)
        for (x in leftPos until rightPos) {
            for (y in topPos until bottomPos) {
                val left = x * zoom + offsetX
                val top = y * zoom + offsetY

                val saveCount = canvas.save()

                canvas.translate(left.toFloat(), top.toFloat())
                canvas.clipRect(0, 0, zoom, zoom)
                onDrawPixel(canvas, x, y)

                canvas.restoreToCount(saveCount)
            }
        }
        afterDrawPixel(canvas, rect)
    }
}
