package com.chrnie.gomoku.app.util

import android.content.Context
import android.util.DisplayMetrics


object DimensionUtils {

    fun dipToPix(context: Context, value: Float): Int {
        val metrics = getDisplayMetrics(context)
        val f = value * metrics.density
        return ceil(value, f)
    }

    fun spToPix(context: Context, value: Float): Int {
        val metrics = getDisplayMetrics(context)
        val f = value * metrics.scaledDensity
        return ceil(value, f)
    }

    private fun ceil(value: Float, f: Float): Int {
        val res = (if (f >= 0) f + 0.5f else f - 0.5f).toInt()
        if (res != 0) {
            return res
        }
        if (value == 0f) {
            return 0
        }
        return if (value > 0) {
            1
        } else -1
    }

    fun pixToDip(context: Context, value: Int): Float {
        val metrics = getDisplayMetrics(context)
        return value / metrics.density
    }

    fun pixToSp(context: Context, value: Int): Float {
        val metrics = getDisplayMetrics(context)
        return value / metrics.scaledDensity
    }

    private fun getDisplayMetrics(context: Context): DisplayMetrics {
        val resources = context.resources
        return resources.displayMetrics
    }

}