package com.ssafy.popcon.ui.common

import android.content.ClipDescription
import android.location.LocationManager
import android.view.DragEvent
import android.view.View
import android.view.View.OnDragListener
import com.ssafy.popcon.dto.DonateRequest
import com.ssafy.popcon.dto.User
import com.ssafy.popcon.util.MyLocationManager
import com.ssafy.popcon.viewmodel.WearViewModel

open class DragListener(
    private val barNum: String,
    private val viewModel: WearViewModel, private val user: User, private val lm: LocationManager
) : OnDragListener {
    override fun onDrag(v: View, e: DragEvent): Boolean {
        when (e.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                e.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
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
                viewModel.donate(
                    DonateRequest(barNum, MyLocationManager.getLocation(lm)!!.longitude.toString(),MyLocationManager.getLocation(lm)!!.latitude.toString()),
                    user
                )

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