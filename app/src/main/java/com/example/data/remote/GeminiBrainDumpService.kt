package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.EnergyLevel
import com.example.data.model.TaskCategory
import com.example.domain.engine.DeterministicRankingEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class ExtractedTaskDto(
    val title: String,
    val description: String,
    val clarityScore: Float,
    val impactScore: Float,
    val urgencyScore: Float,
    val energyLevel: EnergyLevel,
    val estimatedMinutes: Int,
    val category: TaskCategory,
    val phases: List<String>,
    val aiRationale: String,
    val computedRank: Float
)

class GeminiBrainDumpService {
    suspend fun generateCompanionCheckIn(taskTitle: String, phases: List<String>, notes: List<String>): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "YOUR_API_KEY_HERE") {
            return@withContext "I'm here with you on '$taskTitle'. Keep up the steady momentum, friend! ✦"
        }

        val prompt = """
            You are a supportive, insightful, and calm AI companion guiding the user through their current focus session.
            The user is currently working on: "$taskTitle".
            Their remaining phases are: ${phases.joinToString(", ")}.
            Their latest notes/reflections on this task: ${notes.joinToString(" | ")}.

            Act like a wise friend. Briefly check in on their progress, address any hurdles mentioned in their notes, and gently guide them to the next step.
            Keep it under 3 sentences. Be warm and cosmic. Do not use emojis except maybe one.
        """.trimIndent()

        val jsonBody = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", prompt) })
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.7)
            })
        }

        val request = Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey")
            .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
            .build()

        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    val root = JSONObject(responseBody)
                    val candidates = root.optJSONArray("candidates")
                    if (candidates != null && candidates.length() > 0) {
                        val firstCandidate = candidates.getJSONObject(0)
                        val content = firstCandidate.optJSONObject("content")
                        val parts = content?.optJSONArray("parts")
                        if (parts != null && parts.length() > 0) {
                            val text = parts.getJSONObject(0).optString("text")
                            return@withContext text.trim()
                        }
                    }
                }
            }
            "I'm here with you on '$taskTitle'. Let's flow. ✦"
        } catch (e: Exception) {
            "I'm here with you on '$taskTitle'. Let's flow. ✦"
        }
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun extractTasksFromBrainDump(
        rawBrainDumpText: String,
        customApiKey: String? = null
    ): List<ExtractedTaskDto> = withContext(Dispatchers.IO) {
        val trimmed = rawBrainDumpText.trim()
        if (trimmed.isBlank()) {
            return@withContext emptyList()
        }

        val resolvedApiKey = customApiKey?.takeIf { it.isNotBlank() }
            ?: BuildConfig.GEMINI_API_KEY.takeIf { it.isNotBlank() && it != "DEFAULT_GEMINI_KEY" }

        // If no valid Gemini API key is configured or offline, use our deterministic NLP parser
        if (resolvedApiKey == null) {
            Log.d("GeminiBrainDump", "No Gemini API key found, using deterministic cosmic extraction engine.")
            return@withContext extractDeterministically(trimmed)
        }

        try {
            val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$resolvedApiKey"

            val prompt = """
                You are the contemplative cosmic task sorter for LIFE SORT (inspired by mindful clarity and ecstatic execution).
                Extract actionable tasks from this user brain dump. Break complex tasks into 2-4 phases.
                
                Brain Dump:
                $trimmed
                
                Respond ONLY with a valid JSON array. Each element must be a JSON object with:
                - title (string, concise, mindful action verb)
                - description (string, why and context)
                - clarityScore (number 10-100)
                - impactScore (number 10-100)
                - urgencyScore (number 10-100)
                - energyLevel ("Low", "Medium", or "High")
                - estimatedMinutes (integer, realistic, e.g. 15, 25, 45, 60)
                - category ("Spiritual", "Creative", "Health", "Work", "Personal", or "Relational")
                - phases (array of string sub-steps)
                - aiRationale (string, a brief meditative insight on why doing this brings peace/growth)
            """.trimIndent()

            val requestJson = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        })
                    })
                }
                put("contents", contentsArray)
                put("generationConfig", JSONObject().apply {
                    put("responseMimeType", "application/json")
                    put("temperature", 0.2)
                })
            }

            val body = requestJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(endpoint)
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                Log.w("GeminiBrainDump", "Gemini API call failed code: ${response.code}, falling back to deterministic parser")
                return@withContext extractDeterministically(trimmed)
            }

            val responseBodyString = response.body?.string() ?: ""
            val jsonRoot = JSONObject(responseBodyString)
            val candidates = jsonRoot.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text") ?: ""

            parseAndValidateJsonResponse(text, trimmed)
        } catch (e: Exception) {
            Log.e("GeminiBrainDump", "Error calling Gemini API: ${e.message}, falling back to deterministic parser")
            extractDeterministically(trimmed)
        }
    }

    private fun parseAndValidateJsonResponse(jsonText: String, fallbackText: String): List<ExtractedTaskDto> {
        return try {
            val cleaned = jsonText.trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()

            val jsonArray = JSONArray(cleaned)
            val result = mutableListOf<ExtractedTaskDto>()

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val title = obj.optString("title", "Conscious Action ${i + 1}").takeIf { it.isNotBlank() } ?: "Conscious Action ${i + 1}"
                val desc = obj.optString("description", "")
                val clarity = obj.optDouble("clarityScore", 70.0).toFloat().coerceIn(10f, 100f)
                val impact = obj.optDouble("impactScore", 70.0).toFloat().coerceIn(10f, 100f)
                val urgency = obj.optDouble("urgencyScore", 50.0).toFloat().coerceIn(10f, 100f)
                val energy = EnergyLevel.fromString(obj.optString("energyLevel", "Medium"))
                val estMinutes = obj.optInt("estimatedMinutes", 25).coerceIn(5, 480)
                val category = TaskCategory.fromString(obj.optString("category", "Personal"))

                val phasesList = mutableListOf<String>()
                val rawPhases = obj.optJSONArray("phases")
                if (rawPhases != null) {
                    for (j in 0 until rawPhases.length()) {
                        val phaseText = rawPhases.optString(j)
                        if (phaseText.isNotBlank()) phasesList.add(phaseText)
                    }
                }
                if (phasesList.isEmpty() && estMinutes > 30) {
                    phasesList.add("Phase 1: Ground and organize parameters")
                    phasesList.add("Phase 2: Focused core execution")
                    phasesList.add("Phase 3: Integration and completion review")
                }

                val aiRationale = obj.optString("aiRationale", "Completing this creates stillness and harmonic momentum in your life.")
                val rank = DeterministicRankingEngine.calculateTaskRank(impact, urgency, clarity, energy)

                result.add(
                    ExtractedTaskDto(
                        title = title,
                        description = desc,
                        clarityScore = clarity,
                        impactScore = impact,
                        urgencyScore = urgency,
                        energyLevel = energy,
                        estimatedMinutes = estMinutes,
                        category = category,
                        phases = phasesList,
                        aiRationale = aiRationale,
                        computedRank = rank
                    )
                )
            }

            if (result.isEmpty()) extractDeterministically(fallbackText) else result
        } catch (e: Exception) {
            Log.w("GeminiBrainDump", "Failed to parse JSON from model: ${e.message}")
            extractDeterministically(fallbackText)
        }
    }

    /**
     * Deterministic NLP extractor handles offline mode, ambiguous input, empty inputs, or missing API keys.
     * Extracts discrete tasks, detects energy/impact keywords, generates phases, and calculates ranks deterministically.
     */
    fun extractDeterministically(text: String): List<ExtractedTaskDto> {
        val lines = text.split("\n", ";", ".")
            .map { it.trim().trimStart('-', '*', '•', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.', ' ') }
            .filter { it.length >= 3 }

        if (lines.isEmpty()) {
            return listOf(
                ExtractedTaskDto(
                    title = "Breathe and clarify intentions",
                    description = "Take three slow conscious breaths and observe what truly requires your presence.",
                    clarityScore = 85f,
                    impactScore = 80f,
                    urgencyScore = 60f,
                    energyLevel = EnergyLevel.LOW,
                    estimatedMinutes = 10,
                    category = TaskCategory.SPIRITUAL,
                    phases = listOf("Sit upright with spine aligned", "Three deep diaphragmatic breaths", "Name single key intention"),
                    aiRationale = "When inner space is still, every action carries maximum potency.",
                    computedRank = DeterministicRankingEngine.calculateTaskRank(80f, 60f, 85f, EnergyLevel.LOW)
                )
            )
        }

        return lines.take(6).mapIndexed { index, line ->
            val lower = line.lowercase()
            val category = when {
                "meditat" in lower || "breath" in lower || "yoga" in lower || "pray" in lower || "walk" in lower -> TaskCategory.SPIRITUAL
                "code" in lower || "write" in lower || "design" in lower || "art" in lower || "draft" in lower -> TaskCategory.CREATIVE
                "gym" in lower || "workout" in lower || "sleep" in lower || "eat" in lower || "diet" in lower -> TaskCategory.HEALTH
                "email" in lower || "client" in lower || "report" in lower || "meeting" in lower || "deploy" in lower -> TaskCategory.WORK
                "call" in lower || "mom" in lower || "friend" in lower || "family" in lower || "talk" in lower -> TaskCategory.RELATIONAL
                else -> TaskCategory.PERSONAL
            }

            val energy = when {
                "quick" in lower || "easy" in lower || "read" in lower -> EnergyLevel.LOW
                "deep" in lower || "hard" in lower || "create" in lower || "build" in lower -> EnergyLevel.HIGH
                else -> EnergyLevel.MEDIUM
            }

            val estMinutes = when {
                "quick" in lower -> 15
                "review" in lower -> 20
                "build" in lower || "write" in lower -> 45
                else -> 25 + (index * 5)
            }

            val impact = (65f + (index * 5) % 30).coerceIn(40f, 95f)
            val urgency = (50f + (30 - index * 6)).coerceIn(35f, 90f)
            val clarity = (70f + (index * 7) % 25).coerceIn(50f, 95f)

            val phases = if (estMinutes >= 30) {
                listOf(
                    "Phase 1: Setup and mental grounding",
                    "Phase 2: Deep unbroken immersion",
                    "Phase 3: Final refinement and closure"
                )
            } else {
                listOf(
                    "Phase 1: Prepare space and materials",
                    "Phase 2: Execute single-point action"
                )
            }

            val rank = DeterministicRankingEngine.calculateTaskRank(impact, urgency, clarity, energy)

            ExtractedTaskDto(
                title = line.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                description = "Extracted from brain dump. Mindfully prioritized for conscious execution.",
                clarityScore = clarity,
                impactScore = impact,
                urgencyScore = urgency,
                energyLevel = energy,
                estimatedMinutes = estMinutes,
                category = category,
                phases = phases,
                aiRationale = "Completing '$line' dissolves mental clutter and restores cosmic momentum.",
                computedRank = rank
            )
        }
    }
}
