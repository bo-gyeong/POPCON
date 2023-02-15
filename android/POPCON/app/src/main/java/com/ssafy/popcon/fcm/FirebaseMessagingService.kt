package com.ssafy.popcon.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ssafy.popcon.R
import com.ssafy.popcon.ui.common.MainActivity

@RequiresApi(Build.VERSION_CODES.O)
class FirebaseMessagingService: FirebaseMessagingService() {
    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        //MainActivity.getInstance()!!.uploadToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        createNotificationChannel(MainActivity.channel_id, "user")

        var msgTitle = ""
        var msgContent = ""

        if (message.notification != null){  // notification이 있는 경우 foreground처리
            message.notification.let {
                msgTitle = it!!.title.toString()
                msgContent = it.body.toString()
            }
        } else{  // background 에 있을경우 혹은 foreground에 있을경우 두 경우 모두
            val data = message.data
            msgTitle = data.get("title").toString()
            msgContent = data.get("body").toString()
        }

        val mainIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val mainPendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, mainIntent, PendingIntent.FLAG_IMMUTABLE)
        val builder = NotificationCompat.Builder(this, MainActivity.channel_id)
            .setSmallIcon(R.drawable.ic_popcon_round)
            .setContentTitle(msgTitle)
            .setContentText(msgContent)
            .setAutoCancel(true)
            .setContentIntent(mainPendingIntent)

        NotificationManagerCompat.from(this).apply {
            notify(101, builder.build())
        }
    }

    private fun createNotificationChannel(id: String, name: String){
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(id, name, importance)

        val notificationManager: NotificationManager
            = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(channel)
    }
}