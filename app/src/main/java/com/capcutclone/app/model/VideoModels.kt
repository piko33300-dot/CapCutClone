package com.capcutclone.app.model

data class KeyframePoint(
    val timestampMs: Long,
    val positionX: Float = 0.5f,
    val positionY: Float = 0.5f,
    val scale: Float = 1.0f,
    val rotation: Float = 0.0f,
    val opacity: Float = 1.0f
)

data class SpeedCurve(
    val name: String = "Custom",
    val points: List<Pair<Float, Float>> = listOf(0.0f to 1.0f, 1.0f to 1.0f) // (timeProgress, speedMultiplier)
)

data class ChromaKeySettings(
    var enabled: Boolean = false,
    var targetColorHex: String = "#00FF00",
    var similarity: Float = 0.4f,
    var smoothness: Float = 0.1f
)

data class ColorGradingSettings(
    var brightness: Float = 0.0f,   // -1.0 to 1.0
    var contrast: Float = 1.0f,     // 0.0 to 2.0
    var saturation: Float = 1.0f,   // 0.0 to 2.0
    var temperature: Float = 0.0f,  // -1.0 to 1.0
    var lutPath: String? = null
)

data class AudioTrack(
    val id: String,
    val path: String,
    var startMs: Long,
    var endMs: Long,
    var volume: Float = 1.0f,
    var isVoiceover: Boolean = false
)

data class VideoTemplate(
    val id: String,
    val title: String,
    val durationMs: Long,
    val clipSlotsCount: Int,
    val description: String
)

data class AiFeatureSettings(
    var autoCutoutEnabled: Boolean = false,
    var faceRetouchLevel: Float = 0.0f,      // 0.0 to 1.0
    var motionTrackingEnabled: Boolean = false,
    var magicEraserRegions: MutableList<String> = mutableListOf()
)

data class VideoClip(
    val id: String,
    val path: String,
    var startMs: Long,
    var endMs: Long,
    var speed: Float = 1.0f,
    var speedCurve: SpeedCurve? = null,
    val keyframes: MutableList<KeyframePoint> = mutableListOf(),
    var chromaKey: ChromaKeySettings = ChromaKeySettings(),
    var colorGrading: ColorGradingSettings = ColorGradingSettings(),
    var aiFeatures: AiFeatureSettings = AiFeatureSettings()
) {
    val durationMs: Long
        get() = ((endMs - startMs) / speed).toLong()
}

data class TextOverlay(
    val id: String,
    var text: String,
    var startMs: Long,
    var endMs: Long,
    var positionX: Float = 0.5f,
    var positionY: Float = 0.5f,
    val keyframes: MutableList<KeyframePoint> = mutableListOf()
)

data class CaptionItem(
    val text: String,
    val startMs: Long,
    val endMs: Long,
    var translatedText: String? = null
)

data class EditingProject(
    val id: String,
    var name: String,
    val clips: MutableList<VideoClip> = mutableListOf(),
    val audioTracks: MutableList<AudioTrack> = mutableListOf(),
    val textOverlays: MutableList<TextOverlay> = mutableListOf(),
    val captions: MutableList<CaptionItem> = mutableListOf(),
    var isProxyModeEnabled: Boolean = false
) {
    fun getTotalDurationMs(): Long {
        return clips.sumOf { it.durationMs }
    }
}
