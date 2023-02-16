package com.ssafy.popcon.ui.login

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.wear.ambient.AmbientModeSupport
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.wearable.*
import com.ssafy.popcon.R
import com.ssafy.popcon.config.WearApplicationClass
import com.ssafy.popcon.databinding.ActivityLoginBinding
import com.ssafy.popcon.dto.User
import com.ssafy.popcon.ui.map.DonateActivity
import com.ssafy.popcon.util.SharedPreferencesUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.nio.charset.StandardCharsets

private const val TAG = "LoginFragment_싸피"

class LoginActivity : AppCompatActivity(), AmbientModeSupport.AmbientCallbackProvider,
    DataClient.OnDataChangedListener,
    MessageClient.OnMessageReceivedListener,
    CapabilityClient.OnCapabilityChangedListener {
    private lateinit var binding: ActivityLoginBinding

    var user = User("", "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        Wearable.getDataClient(this).addListener(this)
        Wearable.getMessageClient(this).addListener(this)
        Wearable.getCapabilityClient(this)
            .addListener(this, Uri.parse("wear://"), CapabilityClient.FILTER_REACHABLE)

        supportActionBar!!.hide()
        floatLogo()
    }

    override fun onPause() {
        super.onPause()
        Wearable.getDataClient(this).removeListener(this)
        Wearable.getMessageClient(this).removeListener(this)
        Wearable.getCapabilityClient(this).removeListener(this)
    }

    override fun onDataChanged(p0: DataEventBuffer) {
        Log.d(TAG, "onDataChanged: ")
    }

    override fun getAmbientCallback(): AmbientModeSupport.AmbientCallback {
        TODO("Not yet implemented")
    }

    private val TAG_MESSAGE_RECEIVED = "receive1"

    override fun onMessageReceived(p0: MessageEvent) {
        try {
            Log.d(TAG_MESSAGE_RECEIVED, "onMessageReceived event received")
            val s1 = String(p0.data, StandardCharsets.UTF_8)
            val messageEventPath: String = p0.path

            Log.d(
                TAG_MESSAGE_RECEIVED,
                "onMessageReceived() A message from watch was received:"
                        + p0.requestId
                        + " "
                        + messageEventPath
                        + " "
                        + s1
            )
            val tokens = s1.split(" ")

            Log.d(TAG, "onMessageReceived: ${tokens[2]}")
            WearApplicationClass.sharedPreferencesUtil.accessToken =
                tokens[2]
            WearApplicationClass.sharedPreferencesUtil.addUser(User(tokens[0], tokens[1]))

            val intent = Intent(this, DonateActivity::class.java)
            startActivity(intent)

            finish()
        } catch (e: Exception) {
            Log.d(TAG_MESSAGE_RECEIVED, "Handled in onMessageReceived")
            e.printStackTrace()
        }
    }

    override fun onCapabilityChanged(p0: CapabilityInfo) {

    }

    private fun floatLogo(){
        Glide.with(applicationContext).load(R.raw.pop)
            .into(object : DrawableImageViewTarget(binding.ivLogo) {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    if (resource is GifDrawable) {
                        resource.setLoopCount(1)
                    }
                    super.onResourceReady(resource, transition)
                }
            })

        val animationUp = AnimationUtils.loadAnimation(applicationContext, R.anim.login_logo_up)
        binding.ivLogo.animation = animationUp

        val animationDown = AnimationUtils.loadAnimation(applicationContext, R.anim.login_text_down)
        binding.tvLoginInfo.animation = animationDown
    }
}