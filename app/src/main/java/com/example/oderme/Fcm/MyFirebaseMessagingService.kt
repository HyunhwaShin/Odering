package com.example.oderme.Fcm

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.oderme.MainActivity
import com.example.oderme.R
import com.example.oderme.ui.menu.MenuSelectActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(mRemoteMessage: RemoteMessage) {
        super.onMessageReceived(mRemoteMessage)

        val sented = mRemoteMessage.data["sented"]

        val marketName = mRemoteMessage.data["marketName"]

        val sharedPref = getSharedPreferences("PREFS", Context.MODE_PRIVATE)

        val currentOnlineUser = sharedPref.getString("currentUser","none")

        val firebaseUser = FirebaseAuth.getInstance().currentUser

        val marketUID = mRemoteMessage.data["marketUID"]

        if(firebaseUser!= null && sented == firebaseUser.uid){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                sendOreoNotification(mRemoteMessage)

            }else{
                sendNotification(mRemoteMessage)
            }
        }else if (sented != null){

        }

    }

    private fun sendNotification(mRemoteMessage: RemoteMessage) {

        val marketName = mRemoteMessage.data["marketName"]
        val icon = mRemoteMessage.data["icon"]
        val title = mRemoteMessage.data["title"]
        val body = mRemoteMessage.data["body"]
        val isClick = mRemoteMessage.data["isClick"]

        val notification = mRemoteMessage.notification
        val j = marketName!!.replace("[\\D]".toRegex(),"").toInt()
        val intent = Intent(this, MenuSelectActivity::class.java)
        val bundle = Bundle()
        bundle.putString("marketName",marketName)
        bundle.putString("isClick",isClick)
        intent.putExtras(bundle)

        val pendingIntent = PendingIntent.getActivity(this, j, intent , PendingIntent.FLAG_ONE_SHOT)

        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder : NotificationCompat.Builder = NotificationCompat.Builder(this)
            .setSmallIcon(icon!!.toInt())
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSound)
            .setContentIntent(pendingIntent)

        val noti = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        var i=0
        if(j>0){
            i=j
        }

        noti.notify(i,builder.build())

    }

    private fun sendOreoNotification(mRemoteMessage: RemoteMessage) {

        val marketName = mRemoteMessage.data["marketName"]
        val icon = mRemoteMessage.data["icon"]
        val title = mRemoteMessage.data["title"]
        val body = mRemoteMessage.data["body"]
        val isClick = mRemoteMessage.data["isClick"]

        val notification = mRemoteMessage.notification
        val j = Math.floor(Math.random() * 10000).toInt()
        val intent = Intent(this, MenuSelectActivity::class.java)
        val bundle = Bundle()
        bundle.putString("marketName",marketName)
        bundle.putString("isClick",isClick)
        intent.putExtras(bundle)

        val pendingIntent = PendingIntent.getActivity(this, j, intent , PendingIntent.FLAG_ONE_SHOT)

        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val oreoNotification = OreoNotification(this)

        val builder : Notification.Builder = oreoNotification.getOreoNotification(title,body,pendingIntent,defaultSound,icon)

        oreoNotification.getManager!!.notify(j,builder.build())

    }

}