package com.ngurah.storyapp.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

fun generateTimestamp(): String = SimpleDateFormat(formatterDate, Locale.US).format(Date())

fun createTempFile(context: Context): File {
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val timestamp = generateTimestamp()
    val fileName = "${timestamp}.jpg"
    return File(storageDir, fileName)
}

fun copyInputStreamToFile(inputStream: InputStream, file: File) {
    inputStream.use { input ->
        FileOutputStream(file).use { output ->
            input.copyTo(output)
        }
    }
}

fun reduceImageFile(file: File): File {
    val options = BitmapFactory.Options().apply {
        inSampleSize = 1
    }
    val bitmap = BitmapFactory.decodeFile(file.path, options)
    var compressQuality = 100
    var streamLength: Int
    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5
    } while (streamLength > MAXIMAL_IMAGE_SIZE)
    bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
    return file
}

fun uriToFile(selectedImg: Uri, context: Context): File {
    val contentResolver: ContentResolver = context.contentResolver
    val myFile = createTempFile(context)

    contentResolver.openInputStream(selectedImg)?.use { inputStream ->
        copyInputStreamToFile(inputStream, myFile)
    }

    return myFile
}
