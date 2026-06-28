package com.example.firebase

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiService {
    private const val TAG = "GeminiService"
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun getAIRecommendation(budget: String, brandPref: String, style: String, otherInfo: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.e(TAG, "Gemini API key is not configured or placeholder.")
            return@withContext "API Key not configured. Please enter your GEMINI_API_KEY in the Secrets panel."
        }

        val prompt = """
            You are the AutoMarket Hub Elite AI assistant. Recommend 2 luxury cars from BMW, Audi, Mercedes-Benz, or Porsche based on the following:
            - Budget: $budget
            - Brand Preference: $brandPref
            - Body Style/Preference: $style
            - Additional requirements: $otherInfo
            
            Provide a short, extremely premium, and structured response in markdown. Focus on specifications, estimated price, and why it perfectly matches their requirements. Keep the styling clean and elite.
        """.trimIndent()

        val jsonRequest = JSONObject().apply {
            val contentsArray = org.json.JSONArray().apply {
                val contentObj = JSONObject().apply {
                    val partsArray = org.json.JSONArray().apply {
                        val partObj = JSONObject().apply {
                            put("text", prompt)
                        }
                        put(partObj)
                    }
                    put("parts", partsArray)
                }
                put(contentObj)
            }
            put("contents", contentsArray)
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = jsonRequest.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
            .post(requestBody)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errBody = response.body?.string() ?: ""
                    Log.e(TAG, "Request failed code ${response.code}: $errBody")
                    return@withContext "Failed to get recommendation from Gemini API. (Code: ${response.code})"
                }
                val resBody = response.body?.string() ?: ""
                val jsonObj = JSONObject(resBody)
                val candidates = jsonObj.getJSONArray("candidates")
                val firstCandidate = candidates.getJSONObject(0)
                val content = firstCandidate.getJSONObject("content")
                val parts = content.getJSONArray("parts")
                val text = parts.getJSONObject(0).getString("text")
                return@withContext text
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during API call", e)
            return@withContext "Error fetching AI recommendations: ${e.localizedMessage}"
        }
    }
}
