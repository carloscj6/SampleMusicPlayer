package com.revosleap.samplemusicplayer.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.VectorDrawable
import android.media.MediaMetadataRetriever

import com.revosleap.samplemusicplayer.R

import java.io.ByteArrayInputStream
import java.io.InputStream

object Utils {

    fun songArt(path: String, context: Context): Bitmap {
        val retriever = MediaMetadataRetriever()
        val inputStream: InputStream
        retriever.setDataSource(path)
        if (retriever.embeddedPicture != null) {
            inputStream = ByteArrayInputStream(retriever.embeddedPicture)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            retriever.release()
            return bitmap
        } else {
            return getLargeIcon(context)
        }
    }

    private fun getLargeIcon(context: Context): Bitmap {

        val vectorDrawable = context.getDrawable(R.drawable.music_notification) as VectorDrawable

        val largeIconSize = context.resources.getDimensionPixelSize(R.dimen.notification_large_dim)
        val bitmap = Bitmap.createBitmap(largeIconSize, largeIconSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        if (vectorDrawable != null) {
            vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
            vectorDrawable.alpha = 100
            vectorDrawable.draw(canvas)
        }

        return bitmap
    }
}
