package com.ssafy.popcon.ui.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.wear.ambient.AmbientModeSupport
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.wearable.*
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import com.ssafy.popcon.R
import com.ssafy.popcon.config.WearApplicationClass
import com.ssafy.popcon.databinding.ActivityLoginBinding
import com.ssafy.popcon.dto.TokenResponse
import com.ssafy.popcon.dto.User
import com.ssafy.popcon.repository.user.WearRemoteDataSource
import com.ssafy.popcon.repository.user.WearRepository
import com.ssafy.popcon.ui.map.DonateActivity
import com.ssafy.popcon.util.SharedPreferencesUtil
import com.ssafy.popcon.util.WearRetrofitUtil
import com.ssafy.popcon.viewmodel.WearViewModel
import com.ssafy.popcon.viewmodel.ViewModelFactoryWear
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.nio.charset.StandardCharsets
import java.util.*

private const val TAG = "LoginFragment_싸피"

class LoginActivity : AppCompatActivity(), AmbientModeSupport.AmbientCallbackProvider ,DataClient.OnDataChangedListener,
    MessageClient.OnMessageReceivedListener,
    CapabilityClient.OnCapabilityChangedListener{
    private lateinit var binding: ActivityLoginBinding

    var user = User("", "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        //floatLogo()
        return super.onCreateView(name, context, attrs)
    }

    override fun onResume() {
        super.onResume()
        Wearable.getDataClient(this).addListener(this)
        Wearable.getMessageClient(this).addListener(this)
        Wearable.getCapabilityClient(this)
            .addListener(this, Uri.parse("wear://"), CapabilityClient.FILTER_REACHABLE)

        supportActionBar!!.hide()
        //SharedPreferencesUtil(this@LoginActivity).deleteUser()
    }

    override fun onPause() {
        super.onPause()
        Wearable.getDataClient(this).removeListener(this)
    }

    override fun onDataChanged(p0: DataEventBuffer) {
        Log.d(TAG, "onDataChangeddddd: ")
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
            SharedPreferencesUtil(this@LoginActivity).addUser(User(s1, "카카오"))

            Log.d(TAG, "onMessageReceived: ${tokens[2]}")
            WearApplicationClass.sharedPreferencesUtil.accessToken =
                tokens[2]
            WearApplicationClass.sharedPreferencesUtil.addUser(User(tokens[0], tokens[1]))

            val intent = Intent(this, DonateActivity::class.java)
            startActivity(intent)

            //finish()

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
                        (resource as GifDrawable).setLoopCount(1)
                        binding.tvLoginInfo.visibility = View.VISIBLE
                    }
                    super.onResourceReady(resource, transition)
                }
            })
    }
}