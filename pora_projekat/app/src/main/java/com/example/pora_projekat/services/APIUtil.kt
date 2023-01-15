package com.example.pora_projekat.services

import android.util.Log
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException

object APIUtil {

    private val client = OkHttpClient()

    fun uploadFile(filepath: String, url: String, mime: MediaType) {
        val file = File(filepath)
        val request = Request.Builder()
            .url(url)
            .post(file.asRequestBody(mime))
            .build()
        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                println(response.body!!.string())
            }
        } catch (e: Exception) {
            Log.d("APIUtil", "Error when sending file")
        }
    }

    val URL = "http://straylight.ignorit.al:9001/"
    val MEDIA_TYPE_JPEG = "image/jpeg".toMediaType()
    val MEDIA_TYPE_MP3 = "audio/mpeg".toMediaType()
}