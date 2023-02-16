package com.ssafy.popcon.ui.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View

class DragShadowBuilder() : View.DragShadowBuilder() {
    private var shadow: Drawable? = null

    companion object {
        fun fromResource(ctx: Context, resId: Int): View.DragShadowBuilder {
            val builder = DragShadowBuilder()
            val dr = ctx.resources.getDrawable(resId)
            val bitmap = dr as BitmapDrawable
            val resized = BitmapDrawable(
                ctx.resources,
                Bitmap.createScaledBitmap(bitmap.bitmap, 100, 100, true)
            )
            builder.shadow = resized

            builder.shadow?.let {
                it.setBounds(0, 0, it.minimumWidth, it.minimumWidth)
            }

            return builder
        }
    }

    override fun onProvideShadowMetrics(outShadowSize: Point, outShadowTouchPoint: Point) {
        outShadowSize?.x = shadow?.minimumWidth
        outShadowSize?.y = shadow?.minimumHeight

        outShadowTouchPoint?.x = (outShadowSize!!.x / 2)
        outShadowTouchPoint?.y = (outShadowSize!!.y / 2)
    }

    override fun onDrawShadow(canvas: Canvas) {

        shadow?.draw(canvas)
    }
}