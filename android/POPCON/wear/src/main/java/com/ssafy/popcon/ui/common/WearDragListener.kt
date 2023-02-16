package com.ssafy.popcon.ui.common

import android.content.ClipData
import android.content.ClipDescription
import android.location.LocationManager
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.view.View.OnDragListener
import com.ssafy.popcon.dto.DonateRequest
import com.ssafy.popcon.dto.User
import com.ssafy.popcon.ui.map.DonateLocation
import com.ssafy.popcon.viewmodel.WearViewModel

open class WearDragListener(
    private val targetView: View,
    private val barNum: String,
    private val viewModel: WearViewModel, private val user: User
) : OnDragListener {
    override fun onDrag(v: View, e: DragEvent): Boolean {
        when (e.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                e.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
                targetView.visibility = View.VISIBLE
                v.invalidate()
            }
            DragEvent.ACTION_DRAG_ENTERED -> {

                true
            }

            DragEvent.ACTION_DRAG_LOCATION ->
                // Ignore the event.
                true
            DragEvent.ACTION_DRAG_EXITED -> {

                // Returns true; the value is ignored.
                true
            }
            DragEvent.ACTION_DROP -> {
                targetView.visibility = View.INVISIBLE
                if (v == targetView) {
                    Log.d("TAG", "onDrag: ${DonateLocation.x} ${DonateLocation.y}")
                    if (!DonateLocation.x.isNullOrBlank() && !DonateLocation.x.isNullOrBlank()) {
                        viewModel.donate(
                            DonateRequest(
                                barNum, DonateLocation.x,
                                DonateLocation.y
                            ),
                            user
                        )
                    }
                }
                // Invalidates the view to force a redraw.
                v.invalidate()

                // Returns true. DragEvent.getResult() will return true.
                true
            }

            DragEvent.ACTION_DRAG_ENDED -> {
                true
            }
            else -> {
                false
            }
        }

        return true
    }
}