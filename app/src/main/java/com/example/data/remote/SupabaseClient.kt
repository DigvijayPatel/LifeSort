package com.example.data.remote

import android.content.Context
import android.util.Log
import com.example.BuildConfig
import com.example.data.model.LifeGrowthSnapshotEntity
import com.example.data.model.TaskEntity
import com.example.data.model.TaskNoteEntity
import com.example.data.model.TaskPhaseEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class SupabaseClient(context: Context) {

    private val prefs = context.getSharedPreferences("life_sort_supabase_prefs", Context.MODE_PRIVATE)

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    var supabaseUrl: String
        get() {
            val saved = prefs.getString("supabase_url", null)
            return if (!saved.isNullOrBlank()) saved else BuildConfig.SUPABASE_URL
        }
        set(value) {
            prefs.edit().putString("supabase_url", value.trim()).apply()
        }

    var supabaseAnonKey: String
        get() {
            val saved = prefs.getString("supabase_anon_key", null)
            return if (!saved.isNullOrBlank()) saved else BuildConfig.SUPABASE_ANON_KEY
        }
        set(value) {
            prefs.edit().putString("supabase_anon_key", value.trim()).apply()
        }

    fun isConfigured(): Boolean {
        val url = supabaseUrl
        val key = supabaseAnonKey
        return url.isNotBlank() && !url.contains("placeholder.supabase.co") && key.isNotBlank() && key != "DEFAULT_ANON_KEY"
    }

    suspend fun testConnection(): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        if (!isConfigured()) {
            return@withContext Pair(false, "Supabase URL or Anon Key is missing or placeholder.")
        }

        try {
            val cleanUrl = supabaseUrl.trimEnd('/')
            val endpoint = "$cleanUrl/rest/v1/"
            val request = Request.Builder()
                .url(endpoint)
                .addHeader("apikey", supabaseAnonKey)
                .addHeader("Authorization", "Bearer $supabaseAnonKey")
                .get()
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful || response.code == 404 || response.code == 200) {
                Pair(true, "Connected to Supabase successfully (Status: ${response.code})")
            } else {
                Pair(false, "Supabase returned HTTP ${response.code}: ${response.message}")
            }
        } catch (e: Exception) {
            Pair(false, "Connection error: ${e.localizedMessage}")
        }
    }

    suspend fun syncTask(task: TaskEntity): Boolean = withContext(Dispatchers.IO) {
        if (!isConfigured()) return@withContext false
        try {
            val cleanUrl = supabaseUrl.trimEnd('/')
            val endpoint = "$cleanUrl/rest/v1/tasks"
            val json = JSONObject().apply {
                put("id", task.id)
                put("title", task.title)
                put("description", task.description)
                put("clarity_score", task.clarityScore)
                put("impact_score", task.impactScore)
                put("urgency_score", task.urgencyScore)
                put("energy_level", task.energyLevel)
                put("estimated_minutes", task.estimatedMinutes)
                put("actual_minutes_spent", task.actualMinutesSpent)
                put("category", task.category)
                put("status", task.status)
                put("computed_rank", task.computedRank)
                put("ai_rationale", task.aiRationale)
                put("interruption_reason", task.interruptionReason ?: JSONObject.NULL)
                put("paused_at", task.pausedAt ?: JSONObject.NULL)
                put("completed_at", task.completedAt ?: JSONObject.NULL)
                put("updated_at", task.updatedAt)
            }

            val body = json.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(endpoint)
                .addHeader("apikey", supabaseAnonKey)
                .addHeader("Authorization", "Bearer $supabaseAnonKey")
                .addHeader("Prefer", "resolution=merge-duplicates")
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            response.isSuccessful
        } catch (e: Exception) {
            Log.w("SupabaseClient", "Failed to sync task: ${e.message}")
            false
        }
    }

    suspend fun syncSnapshots(snapshots: List<LifeGrowthSnapshotEntity>): Boolean = withContext(Dispatchers.IO) {
        if (!isConfigured() || snapshots.isEmpty()) return@withContext false
        try {
            val cleanUrl = supabaseUrl.trimEnd('/')
            val endpoint = "$cleanUrl/rest/v1/life_growth_history"
            val array = JSONArray()
            for (snap in snapshots) {
                val json = JSONObject().apply {
                    put("id", snap.id)
                    put("timestamp", snap.timestamp)
                    put("growth_score", snap.growthScore)
                    put("mindful_focus_score", snap.mindfulFocusScore)
                    put("execution_harmony_score", snap.executionHarmonyScore)
                    put("mental_clarity_score", snap.mentalClarityScore)
                    put("cosmic_momentum_score", snap.cosmicMomentumScore)
                    put("day_label", snap.dayLabel)
                    put("event_note", snap.eventNote)
                }
                array.put(json)
            }

            val body = array.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(endpoint)
                .addHeader("apikey", supabaseAnonKey)
                .addHeader("Authorization", "Bearer $supabaseAnonKey")
                .addHeader("Prefer", "resolution=merge-duplicates")
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            response.isSuccessful
        } catch (e: Exception) {
            Log.w("SupabaseClient", "Failed to sync snapshots: ${e.message}")
            false
        }
    }
}
