package com.capcutclone.app.ai

import android.content.Context
import com.capcutclone.app.model.CaptionItem
import com.capcutclone.app.model.VideoClip
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

    suspend fun applyAutoCutout(clip: VideoClip): Boolean {
        return withContext(Dispatchers.IO) {
            clip.aiFeatures.autoCutoutEnabled = true
            true
        }
    }

    suspend fun applyFaceRetouch(clip: VideoClip, level: Float): Boolean {
        return withContext(Dispatchers.IO) {
            clip.aiFeatures.faceRetouchLevel = level.coerceIn(0.0f, 1.0f)
            true
        }
    }

    suspend fun enableMotionTracking(clip: VideoClip, enabled: Boolean): Boolean {
        return withContext(Dispatchers.IO) {
            clip.aiFeatures.motionTrackingEnabled = enabled
            true
        }
    }

    suspend fun applyMagicEraserRegion(clip: VideoClip, regionBoundingBox: String): Boolean {
        return withContext(Dispatchers.IO) {
            clip.aiFeatures.magicEraserRegions.add(regionBoundingBox)
            true
        }
    }

    suspend fun generateAiDubbing(captions: List<CaptionItem>, targetLanguage: String): List<CaptionItem> {
        return withContext(Dispatchers.IO) {
            captions.map { caption ->
                caption.copy(translatedText = "[$targetLanguage] ${caption.text}")
            }
        }
    }
}
