package com.tghrsyahptra.dicodingeventapp.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.BigPictureStyle
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.tghrsyahptra.dicodingeventapp.R
import com.tghrsyahptra.dicodingeventapp.data.remote.EventRepository
import com.tghrsyahptra.dicodingeventapp.di.Injection
import java.text.SimpleDateFormat
import java.util.Locale

class MyWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    companion object {
        private val TAG = MyWorker::class.java.simpleName
        const val EXTRA_EVENT = "event"
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "channel_1"
        const val CHANNEL_NAME = "event channel"
    }

    private val repository: EventRepository = Injection.provideRepository(context)

    override suspend fun doWork(): Result {
        return try {
            val latestEventResponse = repository.getLatestEvent()
            if (latestEventResponse != null && latestEventResponse.listEvents?.isNotEmpty() == true) {
                val event = latestEventResponse.listEvents.first()
                val title = "Pengingat Acara: ${formatEventDate(event.beginTime)}"
                val description = "${event.name}."
                val imageUrl = event.mediaCover
                val link = event.link

                if (imageUrl != null && link != null) {
                    showNotification(title, description, imageUrl, link)
                }
            }
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Kesalahan saat mengambil acara terbaru: ${e.message}")
            Result.failure()
        }
    }

    private fun showNotification(
        title: String,
        description: String,
        imageUrl: String,
        link: String
    ) {
        // Intent untuk mengklik notifikasi
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Memuat gambar dari URL menggunakan Glide
        Glide.with(applicationContext)
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_notifications)
                        .setContentTitle(title)
                        .setContentText(description)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setStyle(BigPictureStyle().bigPicture(resource))
                        .setDefaults(NotificationCompat.DEFAULT_ALL)

                    // Membuat channel notifikasi untuk Android Oreo ke atas
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val channel = NotificationChannel(
                            CHANNEL_ID,
                            CHANNEL_NAME,
                            NotificationManager.IMPORTANCE_HIGH
                        )
                        notification.setChannelId(CHANNEL_ID)
                        notificationManager.createNotificationChannel(channel)
                    }

                    notificationManager.notify(NOTIFICATION_ID, notification.build())
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Placeholder saat gambar tidak tersedia
                }
            })
    }

    private fun formatEventDate(dateString: String?): String {
        // Mengonversi string tanggal menjadi format yang diinginkan
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = inputFormat.parse(dateString)

        // Mengatur format output
        val outputFormat = SimpleDateFormat("d MMM, HH:mm", Locale.getDefault())
        return outputFormat.format(date)
    }
}