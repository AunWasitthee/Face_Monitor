package com.fg.mdp.facemonitor.utils

import android.content.Context
import android.util.TypedValue

object PixelHelper {
    fun pixelsToDp(px: Int, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, px.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }
}