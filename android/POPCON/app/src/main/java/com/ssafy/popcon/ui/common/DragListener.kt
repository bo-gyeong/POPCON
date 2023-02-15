package com.ssafy.popcon.ui.common

import android.content.ClipData
import android.content.ClipDescription
import android.location.LocationManager
import android.view.DragEvent
import android.view.View
import android.view.View.OnDragListener
import com.ssafy.popcon.dto.DonateRequest
import com.ssafy.popcon.dto.User
import com.ssafy.popcon.ui.map.DonateLocation
import com.ssafy.popcon.util.MyLocationManager
import com.ssafy.popcon.viewmodel.MapViewModel

open class DragListener(
    private val target: View,
    private val barNum: String,
    private val viewModel: MapViewModel, private val user: User, private val lm: LocationManager
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
                // Gets the item containing the dragged data.
                val item: ClipData.Item = e.clipData.getItemAt(0)

                // Gets the text data from the item.
                val dragData = item.text

                // 이미지 제거
                if (v == target) {
                    viewModel.donate(
                        DonateRequest(barNum, DonateLocation.x, DonateLocation.y),
                        user,
                        MyLocationManager.getLocation(lm)!!.longitude.toString(),
                        MyLocationManager.getLocation(lm)!!.latitude.toString()
                    )
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