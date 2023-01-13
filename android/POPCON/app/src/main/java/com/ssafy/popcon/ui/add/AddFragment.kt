package com.ssafy.popcon.ui.add

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore.Images
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ssafy.popcon.R
import com.ssafy.popcon.databinding.FragmentAddBinding
import com.ssafy.popcon.ui.common.MainActivity

class AddFragment : Fragment() {
    private lateinit var binding: FragmentAddBinding

    lateinit var mainActivity: MainActivity
    val REQ_CODE_SELECT_IMAGE = 1000

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onStart() {
        super.onStart()
        mainActivity.hideBottomNav(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        openGalleryFirst()

        binding.btnRegi.setOnClickListener {
            //유효성 검사
            findNavController().navigate(R.id.action_addFragment_to_homeFragment)
        }
    }

    private fun openGalleryFirst(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.setDataAndType(Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        startActivityForResult(intent, REQ_CODE_SELECT_IMAGE)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainActivity.hideBottomNav(false)
    }
}