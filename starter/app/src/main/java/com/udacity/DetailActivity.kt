package com.udacity

import android.app.NotificationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_DOWNLOAD_STATUS = "download_status_key"
        const val EXTRA_NAME = "name_key"
    }

    private lateinit var downloadStatus: String
    private lateinit var repositoryName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        downloadStatus = intent.getStringExtra(EXTRA_DOWNLOAD_STATUS).toString()
        repositoryName = intent.getStringExtra(EXTRA_NAME).toString()

        download_status.text = downloadStatus
        repository_name.text = repositoryName

        ok_button.setOnClickListener {
            finishAndRemoveTask()
            val notificationManager =
                ContextCompat.getSystemService(
                    application,
                    NotificationManager::class.java
                ) as NotificationManager
            notificationManager.cancelNotifications()
        }
    }

}
