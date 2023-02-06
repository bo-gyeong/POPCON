package com.ssafy.popcon.ui.common

import android.content.ClipData
import android.content.ClipDescription
import android.graphics.Color
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.view.View.OnDragListener
import android.widget.ImageView
import androidx.core.view.isVisible
import com.ssafy.popcon.dto.DonateRequest
import com.ssafy.popcon.dto.Gifticon
import com.ssafy.popcon.dto.User
import com.ssafy.popcon.util.SharedPreferencesUtil
import com.ssafy.popcon.viewmodel.MapViewModel

open class DragListener(
    private val target: ImageView,
    private val req: DonateRequest?,
    private val viewModel: MapViewModel, private val user: User
) : OnDragListener {
    override fun onDrag(v: View, e: DragEvent): Boolean {
        when (e.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                target.isVisible = true

                if (e.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    v.invalidate()
                    true
                } else {
                    false
                }
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                // Applies a green tint to the View.
                if (v == target) {
                    (v as? ImageView)?.setColorFilter(Color.LTGRAY)
                    v.invalidate()
                }
                true
            }

            DragEvent.ACTION_DRAG_LOCATION ->
                // Ignore the event.
                true
            DragEvent.ACTION_DRAG_EXITED -> {
                // Resets the color tint to blue.
                (v as? ImageView)?.clearColorFilter()

                // Invalidates the view to force a redraw in the new tint.
                v.invalidate()

                // Returns true; the value is ignored.
                true
            }
            DragEvent.ACTION_DROP -> {
                // Gets the item containing the dragged data.
                val item: ClipData.Item = e.clipData.getItemAt(0)

                // Gets the text data from the item.
                val dragData = item.text

                // 이미지 제거
                if (v == target) {
                    v.isVisible = false
                    v.invalidate()

                    Log.d("드랍", "onDrag: $req")
                    viewModel.donate(req!!, user)
                }
                // Invalidates the view to force a redraw.
                v.invalidate()

                // Returns true. DragEvent.getResult() will return true.
                true
            }

            DragEvent.ACTION_DRAG_ENDED -> {
                // Turns off any color tinting.
                (v as? ImageView)?.clearColorFilter()

                // Invalidates the view to force a redraw.
                v.invalidate()
                true
            }
            else -> {
                // An unknown action type was received.
                Log.e(
                    "DragDrop Example",
                    "Unknown action type received by View.OnDragListener."
                )
                false
            }

        }

        return true
    }
}