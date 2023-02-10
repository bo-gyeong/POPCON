package com.ssafy.popcon.ui.add

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.transition.Transition
import com.ssafy.popcon.R
import com.ssafy.popcon.databinding.DialogProgressBinding

/*** gif 안떠서 남겨놓음, 해결되면 삭제 ***/
class ProgressDialog:DialogFragment() {
    private lateinit var binding:DialogProgressBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogProgressBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(context)
        builder.setView(binding.root)

        val bounds = binding.pbLoading.indeterminateDrawable.bounds
        binding.pbLoading.indeterminateDrawable.bounds = bounds

        // imageView로 변경 후 glide 적용
//        Glide.with(requireContext()).load(R.raw.pop).into(object : DrawableImageViewTarget(binding.pbLoading){
//            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
//                if (resource is GifDrawable) {
//                    (resource as GifDrawable).setLoopCount(1)
//                }
//                super.onResourceReady(resource, transition)
//            }
//        })

        return builder.create()
    }
}