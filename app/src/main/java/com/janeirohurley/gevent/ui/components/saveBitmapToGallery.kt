package com.janeirohurley.gevent.ui.components

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream

fun saveBitmapToGallery(
    context: Context,
    bitmap: Bitmap
) {
    val filename = "ticket_${System.currentTimeMillis()}.png"

    val fos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val resolver = context.contentResolver
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(
                MediaStore.Images.Media.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + "/Tickets"
            )
        }
        val uri = resolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        )
        resolver.openOutputStream(uri!!)
    } else {
        FileOutputStream(
            File(
                Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES
                ),
                filename
            )
        )
    }

    fos?.use {
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
    }
}
