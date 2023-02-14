package com.ssafy.popcon.ui.login

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.wear.ambient.AmbientModeSupport
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

        actionBar?.hide()
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
}