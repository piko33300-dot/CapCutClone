package com.capcutclone.app.model

data class VideoClip(
    val id: String,
    val path: String,
    var startMs: Long,
    var endMs: Long,
    var speed: Float = 1.0f
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
    var positionY: Float = 0.5f
)

data class CaptionItem(
    val text: String,
    val startMs: Long,
    val endMs: Long
)

data class EditingProject(
    val id: String,
    var name: String,
    val clips: MutableList<VideoClip> = mutableListOf(),
    val textOverlays: MutableList<TextOverlay> = mutableListOf(),
    val captions: MutableList<CaptionItem> = mutableListOf()
) {
    fun getTotalDurationMs(): Long {
        return clips.sumOf { it.durationMs }
    }
}
