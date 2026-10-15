package com.capcutclone.app.ai

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AiManager {
    suspend fun generateCaptions(context: Context, videoPath: String, useCloud: Boolean, apiKey: String?): String {
        return withContext(Dispatchers.IO) {
            ""
        }
    }
}
