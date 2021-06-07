package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
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
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
        private const val DOWNLOAD_SUCCESS = "Success"
        private const val DOWNLOAD_FAIL = "Fail"
    }

    private var downloadID: Long = 0
    private lateinit var url: String
    private lateinit var name: String
    private lateinit var status: String
    private lateinit var notificationManager: NotificationManager
    private lateinit var downloadManager: DownloadManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        notificationManager =
            ContextCompat.getSystemService(
                application,
                NotificationManager::class.java
            ) as NotificationManager
        notificationManager.cancelNotifications()
        createChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name)
        )

        //download manager example:https://github.com/commonsguy/cw-android/blob/master/Internet/Download/src/com/commonsware/android/download/DownloadDemo.java
        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val intentFilter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        intentFilter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED)
        registerReceiver(receiver, intentFilter)

        custom_button.setOnClickListener {
            Log.d("rebeca", "clicked on custom button")
            when(radioGroup.checkedRadioButtonId) {
                R.id.radioButtonGlide -> {
                    name = getString(R.string.glide_download_option_label)
                    url = getString(R.string.glide_download_link)
                }
                R.id.radioButtonLoadApp -> {
                    name = getString(R.string.load_app_download_option_label)
                    url = getString(R.string.load_app_download_link)
                }
                R.id.radioButtonRetrofit -> {
                    name = getString(R.string.retrofit_download_option_label)
                    url = getString(R.string.retrofit_download_link)
                }
                else -> {
                    name = ""
                    url = ""
                }
            }
            download()
        }
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

        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    private fun queryDownloadStatus() {
        val cursor = downloadManager.query(DownloadManager.Query().setFilterById(downloadID))

        if (cursor == null) {
            status = DOWNLOAD_FAIL
            return
        }

        cursor.moveToFirst()
        val statusCode = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
        status = when (statusCode) {
            DownloadManager.STATUS_SUCCESSFUL -> DOWNLOAD_SUCCESS
            else -> DOWNLOAD_FAIL
        }
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
        queryDownloadStatus()
        notificationManager.sendNotification(application.getString(R.string.notification_description), name, status, application)
    }

}
