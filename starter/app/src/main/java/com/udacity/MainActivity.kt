package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {
    companion object {
        private const val CHANNEL_ID = "channelId"
        private const val TAG = "MainActivity"
    }

    private var downloadID: Long = 0
    private lateinit var url: String
    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        notificationManager =
            ContextCompat.getSystemService(
                application,
                NotificationManager::class.java
            ) as NotificationManager

        // todo call when applied
        notificationManager.cancelNotifications()

        //download manager example:https://github.com/commonsguy/cw-android/blob/master/Internet/Download/src/com/commonsware/android/download/DownloadDemo.java
        val intentFilter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        intentFilter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED)
        registerReceiver(receiver, intentFilter)


        custom_button.setOnClickListener {
            Log.d("rebeca", "clicked on custom button")
            url = when(radioGroup.checkedRadioButtonId) {
                R.id.radioButtonGlide -> getString(R.string.glide_download_link)
                R.id.radioButtonLoadApp -> getString(R.string.load_app_download_link)
                R.id.radioButtonRetrofit -> getString(R.string.retrofit_download_link)
                else -> ""
            }
            download()
        }

        createChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name)
        )
    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(receiver)
    }

    //download manager methods
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when {
                intent == null -> Log.e(TAG, "Intent is null")
                intent.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE -> {
                    val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                    Log.d(TAG, "id download is $id")
                    notifyDownloadStatus()
                }
                intent.action == DownloadManager.ACTION_NOTIFICATION_CLICKED -> {
                    Log.d(TAG, "Do nothing when user clicks Android download notification")
                }
                else -> Log.e(TAG, "Unknown broadcast")
            }
        }
    }

    private fun download() {
        if (url == "") {
            Toast.makeText(this, R.string.toast_message_please_select_the_file_to_download, Toast.LENGTH_LONG).show()
            return
        }

        custom_button.loadAnimation()
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "master.zip")

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    // notification methods
    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.download_notification_channel_description)

            val notificationManager = this.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun notifyDownloadStatus() {
        notificationManager.sendNotification(application.getString(R.string.notification_description), application)
    }

}
