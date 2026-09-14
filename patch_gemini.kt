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
