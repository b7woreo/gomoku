package com.chrnie.gomoku.app.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import com.chrnie.gomoku.Chessman
import com.chrnie.gomoku.GomokuGame
import com.chrnie.gomoku.app.R
import com.chrnie.gomoku.app.util.DimensionUtils

class GomokuView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0) : PixelView(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
) {

    companion object {
        const val TAG = "GomokuView"
        const val ANCHOR_OFFSET = 3
        const val COLOR_CHESS_BOARD = 0xff_00_00_00.toInt()
        const val COLOR_HIGHLIGHT = 0xff_ff_00_00.toInt()
    }

    init {
        setPixelSize(GomokuGame.CHESSBOARD_WIDTH, GomokuGame.CHESSBOARD_HEIGHT)
    }

    private val _gestureListener = GestureListener()
    private val _gestureDetector = GestureDetector(context, _gestureListener)
    private val _gesturePoint = Point()

    private val _linePaint by lazy {
        Paint().apply {
            style = Paint.Style.STROKE
            color = COLOR_CHESS_BOARD
            strokeWidth = DimensionUtils.dipToPix(context, 1f).toFloat()
        }
    }
    private val _anchorPaint by lazy {
        Paint().apply {
            style = Paint.Style.FILL
            color = COLOR_CHESS_BOARD
        }
    }
    private val _highlightPaint by lazy {
        Paint().apply {
            style = Paint.Style.STROKE
            color = COLOR_HIGHLIGHT
            strokeWidth = DimensionUtils.dipToPix(context, 1f).toFloat()
        }
    }
    private val _path = Path()
    private val _rect = Rect()

    private val _blackChessman by lazy { BitmapFactory.decodeResource(resources, R.drawable.ic_chessmain_black) }
    private val _whiteChessman by lazy { BitmapFactory.decodeResource(resources, R.drawable.ic_chessman_white) }

    var game: GomokuGame? = null
        set(value) {
            field = value
            invalidate()
        }

    var onPutChessmanListener: OnPutChessmanListener? = null
    var highlightPoint: Point? = null

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            zoomAdjustViewSize()
            centerContent()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        _gestureDetector.onTouchEvent(event)
        return true
    }

    override fun onDrawPixel(canvas: Canvas, x: Int, y: Int) {
        drawChessboard(canvas, x, y)
        drawAnchor(canvas, x, y)
        drawChessman(canvas, x, y)
        drawHighlightPoint(canvas, x, y)
    }

    private fun drawChessboard(canvas: Canvas, x: Int, y: Int) {
        val halfZoom = zoom / 2

        var clipCount: Int? = null
        if (x == 0) {
            clipCount = canvas.save()
            canvas.clipRect(halfZoom - 1, 0, zoom, zoom)
        }

        if (x == GomokuGame.CHESSBOARD_WIDTH - 1) {
            clipCount = canvas.save()
            canvas.clipRect(0, 0, halfZoom + 2, zoom)
        }

        if (y == 0) {
            clipCount = canvas.save()
            canvas.clipRect(0, halfZoom - 1, zoom, zoom)
        }

        if (y == GomokuGame.CHESSBOARD_HEIGHT - 1) {
            clipCount = canvas.save()
            canvas.clipRect(0, 0, zoom, halfZoom + 2)
        }

        val halfWidth = zoom / 2f
        val halfHeight = zoom / 2f

        _path.reset()
        _path.moveTo(0f, halfHeight)
        _path.lineTo(zoom.toFloat(), halfHeight)
        canvas.drawPath(_path, _linePaint)

        _path.reset()
        _path.moveTo(halfWidth, 0f)
        _path.lineTo(halfWidth, zoom.toFloat())
        canvas.drawPath(_path, _linePaint)

        clipCount?.let {
            canvas.restoreToCount(it)
        }
    }

    private fun drawAnchor(canvas: Canvas, x: Int, y: Int) {
        val center = zoom / 2f
        val radius = zoom / 5f

        if ((x == ANCHOR_OFFSET || GomokuGame.CHESSBOARD_WIDTH - x - 1 == ANCHOR_OFFSET)
                && (y == ANCHOR_OFFSET || GomokuGame.CHESSBOARD_HEIGHT - y - 1 == ANCHOR_OFFSET)
                || (x == GomokuGame.CHESSBOARD_WIDTH / 2 && y == GomokuGame.CHESSBOARD_HEIGHT / 2)) {
            canvas.drawCircle(center, center, radius, _anchorPaint)
        }
    }

    private fun drawChessman(canvas: Canvas, x: Int, y: Int) {
        _rect.set(0, 0, zoom, zoom)
        game?.let {
            when (it.chessmanAt(x, y)) {
                Chessman.BLACK -> canvas.drawBitmap(_blackChessman, null, _rect, null)
                Chessman.WHITE -> canvas.drawBitmap(_whiteChessman, null, _rect, null)
            }
        }
    }

    private fun drawHighlightPoint(canvas: Canvas, x: Int, y: Int) {
        highlightPoint?.also {
            if (it.x != x || it.y != y) return
            val halfWidth = zoom / 2f
            val halfHeight = zoom / 2f
            val offset = (zoom - zoom / 5f) / 2f

            _path.reset()

            _path.reset()
            _path.moveTo(offset, halfHeight)
            _path.lineTo(zoom - offset, halfHeight)
            canvas.drawPath(_path, _highlightPaint)

            _path.reset()
            _path.moveTo(halfWidth, offset)
            _path.lineTo(halfWidth, zoom - offset)
            canvas.drawPath(_path, _highlightPaint)
        }
    }

    inner class GestureListener : GestureDetector.OnGestureListener {
        override fun onShowPress(e: MotionEvent) {
        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            if (!isEnabled || onPutChessmanListener == null) {
                return true
            }

            val point = transformationViewCoordinate(e.x.toInt(), e.y.toInt(), _gesturePoint)
            point?.apply {
                onPutChessmanListener?.let { it(x, y) }
            }

            return true
        }

        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            return false
        }

        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            return false
        }

        override fun onLongPress(e: MotionEvent) {
        }
    }
}

typealias OnPutChessmanListener = (Int, Int) -> Unit