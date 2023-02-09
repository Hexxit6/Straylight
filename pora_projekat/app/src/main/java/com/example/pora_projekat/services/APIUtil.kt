package com.example.pora_projekat.services

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.util.Base64OutputStream
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream

object APIUtil {

    private val client = OkHttpClient()

    fun uploadFile(filepath: String, url: String, lat: String, lng: String, mime: MediaType) {
        val file = File(filepath)
        val base64 = convertFileToBase64(file)
        val mode: String = mime.toString()

        val data = """{"mode":"$mode","lat":"$lat","lng":"$lng","file":"$base64"}""".toRequestBody(MIME_JSON)
        val request = Request.Builder()
            .url(url)
            .post(data)
            .build()
        Thread {
            try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    println(response.body!!.string())
                }
            } catch (e: Exception) {
                Log.d("APIUtil-File", "Error when sending file:\n$e")
            }
        }.start()
    }

    fun uploadStream(uri: Uri, url: String, lat: String, lng: String, mime: MediaType, context: Context) {
        val inStream = context.contentResolver.openInputStream(uri)
        val base64 = inStream.let {
            inStream.use {
                ByteArrayOutputStream().use { outputStream ->
                    Base64OutputStream(outputStream, Base64.DEFAULT)!!.use { base64FilterStream ->
                        it!!.copyTo(base64FilterStream)
                        base64FilterStream.flush()
                    }
                    outputStream.toString()
                }
            }
        }
        val mode: String = mime.toString()

        val data = """{"mode":"$mode","lat":"$lat","lng":"$lng","file":"$base64"}""".toRequestBody(MIME_JSON)
        val request = Request.Builder()
            .url(url)
            .post(data)
            .build()
        Thread {
            try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    println(response.body!!.string())
                }
            } catch (e: Exception) {
                Log.d("APIUtil-File", "Error when sending file:\n$e")
            }
        }.start()
    }

    fun uploadSimulation(url: String, lat: String, lng: String, mode: SimulationMode) {
        val json = """{"mode":"$mode","lat":"$lat","lng":"$lng"}"""
        val data: RequestBody = json.toRequestBody(MIME_JSON)
        val request = Request.Builder()
            .url(url)
            .post(data)
            .build()
        Thread {
            try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    println(response.body!!.string())
                }
            } catch (e: Exception) {
                Log.d("APIUtil-Simulation", "Error when sending file:\n$e")
            }
        }.start()
    }

    private fun convertFileToBase64(file: File): String {
        return ByteArrayOutputStream().use { outputStream ->
            Base64OutputStream(outputStream, Base64.DEFAULT).use { base64FilterStream ->
                file.inputStream().use { inputStream ->
                    inputStream.copyTo(base64FilterStream)
                }
            }
            return@use outputStream.toString()
        }
    }

    enum class SimulationMode {
        IMAGE_NORMAL,
        IMAGE_POLLUTED,
        AUDIO_NORMAL,
        AUDIO_POLLUTED
    }

    val BASE_URL = "http://straylight.ignorit.al:9001/neuralnet/"
    val MIME_JPEG = "image/jpeg".toMediaType()
    val MIME_MP3 = "audio/mpeg".toMediaType()
    val MIME_JSON = "application/json".toMediaType()
}
