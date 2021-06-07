package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

private const val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(messageBody: String, repositoryName: String, downloadStatus: String, applicationContext: Context) {
    val detailIntent = Intent(applicationContext, DetailActivity::class.java)
    detailIntent.putExtra(DetailActivity.EXTRA_DOWNLOAD_STATUS, downloadStatus)
    detailIntent.putExtra(DetailActivity.EXTRA_NAME, repositoryName)
    val detailPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        detailIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )


    //todo customize notification style - bold title
    val style = NotificationCompat.InboxStyle()
        .setBigContentTitle(applicationContext.getString(R.string.notification_title))
        .addLine(applicationContext.getString(R.string.notification_description))

    val builder = NotificationCompat.Builder(applicationContext, applicationContext.getString(R.string.notification_channel_id))
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(messageBody)
        .addAction(
            R.drawable.ic_assistant_black_24dp,
            applicationContext.getString(R.string.notification_button),
            detailPendingIntent)
        .setStyle(style)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}