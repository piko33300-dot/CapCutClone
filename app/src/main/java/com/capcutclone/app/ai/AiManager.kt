package com.capcutclone.app.ai

import android.content.Context
import com.capcutclone.app.model.CaptionItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AiManager {
    suspend fun generateCaptions(context: Context, videoPath: String, useCloud: Boolean, apiKey: String?): String {
        return withContext(Dispatchers.IO) {
            val captionList = generateCaptionItems(videoPath)
            captionList.joinToString("\n") { "[${it.startMs}ms - ${it.endMs}ms] ${it.text}" }
        }
    }

    fun generateCaptionItems(videoPath: String): List<CaptionItem> {
        return listOf(
            CaptionItem("Welcome to CapCutClone!", 0L, 2000L),
            CaptionItem("This is an AI generated auto-caption.", 2000L, 5000L),
            CaptionItem("Enjoy editing your video!", 5000L, 8000L)
        )
    }
}
