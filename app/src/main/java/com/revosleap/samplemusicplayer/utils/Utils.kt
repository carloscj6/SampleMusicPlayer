package com.revosleap.samplemusicplayer.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
        var image = BitmapFactory.decodeResource(context.resources, R.drawable.headphones)
        return image
    }
}
