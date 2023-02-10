package com.ssafy.popcon.gallery

import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class GalleryReceiver: BroadcastReceiver() {
    private lateinit var contentResolver: ContentResolver
    private lateinit var addGalleryGifticon: AddGalleryGifticon

    override fun onReceive(context: Context?, intent: Intent?) {
        contentResolver = context!!.contentResolver
        //addGalleryGifticon = AddGalleryGifticon(contentResolver)
        Toast.makeText(context, "onReceive: GalleryUpdate!!!", Toast.LENGTH_SHORT).show()
    }
}