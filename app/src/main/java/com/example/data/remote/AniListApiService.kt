package com.example.data.remote

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class AniListApiService {

    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    private fun wrapJsonValue(value: Any?): Any {
        return when (value) {
            null -> JSONObject.NULL
            is Collection<*> -> {
                val array = JSONArray()
                value.forEach { array.put(wrapJsonValue(it)) }
                array
            }
            is Array<*> -> {
                val array = JSONArray()
                value.forEach { array.put(wrapJsonValue(it)) }
                array
            }
            is Map<*, *> -> {
                val obj = JSONObject()
                value.forEach { (k, v) ->
                    if (k != null) obj.put(k.toString(), wrapJsonValue(v))
                }
                obj
            }
            else -> value
        }
    }

    suspend fun executeGraphQL(query: String, variables: Map<String, Any?> = emptyMap()): String = withContext(Dispatchers.IO) {
        val payload = JSONObject()
        payload.put("query", query)

        val varsJson = JSONObject()
        variables.forEach { (key, value) ->
            if (value != null) {
                varsJson.put(key, wrapJsonValue(value))
            }
        }
        payload.put("variables", varsJson)

        val requestBody = payload.toString().toRequestBody(jsonMediaType)
        val request = Request.Builder()
            .url(AniListGraphQL.BASE_URL)
            .post(requestBody)
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")
            .build()

        try {
            val response = client.newCall(request).execute()
            val bodyString = response.body?.string() ?: ""
            if (!response.isSuccessful) {
                Log.e("AniListApiService", "HTTP ${response.code}: $bodyString")
                throw Exception("AniList API Error: ${response.code}")
            }
            bodyString
        } catch (e: Exception) {
            Log.e("AniListApiService", "GraphQL request failed", e)
            throw e
        }
    }
}
