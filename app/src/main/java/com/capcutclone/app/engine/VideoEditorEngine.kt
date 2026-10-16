package com.capcutclone.app.engine

import com.capcutclone.app.model.AudioTrack
import com.capcutclone.app.model.CaptionItem
import com.capcutclone.app.model.ChromaKeySettings
import com.capcutclone.app.model.ColorGradingSettings
import com.capcutclone.app.model.EditingProject
import com.capcutclone.app.model.KeyframePoint
import com.capcutclone.app.model.SpeedCurve
import com.capcutclone.app.model.TextOverlay
import com.capcutclone.app.model.VideoClip
import com.capcutclone.app.model.VideoTemplate
import java.util.UUID

class VideoEditorEngine(
    val project: EditingProject = EditingProject(UUID.randomUUID().toString(), "Untitled Project")
) {

    fun addClip(path: String, startMs: Long = 0L, endMs: Long = 10000L): VideoClip {
        val clip = VideoClip(
            id = UUID.randomUUID().toString(),
            path = path,
            startMs = startMs,
            endMs = endMs
        )
        project.clips.add(clip)
        return clip
    }

    fun trimClip(clipId: String, newStartMs: Long, newEndMs: Long): Boolean {
        val clip = project.clips.find { it.id == clipId } ?: return false
        if (newStartMs >= newEndMs || newStartMs < 0) return false
        clip.startMs = newStartMs
        clip.endMs = newEndMs
        return true
    }

    fun splitClip(clipId: String, splitPointMs: Long): Pair<VideoClip, VideoClip>? {
        val index = project.clips.indexOfFirst { it.id == clipId }
        if (index == -1) return null

        val original = project.clips[index]
        if (splitPointMs <= original.startMs || splitPointMs >= original.endMs) return null

        val firstClip = original.copy(endMs = splitPointMs)
        val secondClip = VideoClip(
            id = UUID.randomUUID().toString(),
            path = original.path,
            startMs = splitPointMs,
            endMs = original.endMs,
            speed = original.speed
        )

        project.clips[index] = firstClip
        project.clips.add(index + 1, secondClip)

        return Pair(firstClip, secondClip)
    }

    fun setSpeed(clipId: String, speed: Float): Boolean {
        if (speed <= 0f) return false
        val clip = project.clips.find { it.id == clipId } ?: return false
        clip.speed = speed
        return true
    }

    fun setSpeedCurve(clipId: String, speedCurve: SpeedCurve): Boolean {
        val clip = project.clips.find { it.id == clipId } ?: return false
        clip.speedCurve = speedCurve
        return true
    }

    fun addKeyframeToClip(clipId: String, keyframe: KeyframePoint): Boolean {
        val clip = project.clips.find { it.id == clipId } ?: return false
        clip.keyframes.removeAll { it.timestampMs == keyframe.timestampMs }
        clip.keyframes.add(keyframe)
        clip.keyframes.sortBy { it.timestampMs }
        return true
    }

    fun setChromaKeySettings(clipId: String, chromaKey: ChromaKeySettings): Boolean {
        val clip = project.clips.find { it.id == clipId } ?: return false
        clip.chromaKey = chromaKey
        return true
    }

    fun setColorGradingSettings(clipId: String, colorGrading: ColorGradingSettings): Boolean {
        val clip = project.clips.find { it.id == clipId } ?: return false
        clip.colorGrading = colorGrading
        return true
    }

    fun addAudioTrack(path: String, startMs: Long, endMs: Long, isVoiceover: Boolean = false): AudioTrack {
        val audioTrack = AudioTrack(
            id = UUID.randomUUID().toString(),
            path = path,
            startMs = startMs,
            endMs = endMs,
            isVoiceover = isVoiceover
        )
        project.audioTracks.add(audioTrack)
        return audioTrack
    }

    fun addTextOverlay(text: String, startMs: Long, endMs: Long): TextOverlay {
        val overlay = TextOverlay(
            id = UUID.randomUUID().toString(),
            text = text,
            startMs = startMs,
            endMs = endMs
        )
        project.textOverlays.add(overlay)
        return overlay
    }

    fun setCaptions(captions: List<CaptionItem>) {
        project.captions.clear()
        project.captions.addAll(captions)
    }

    fun toggleProxyMode(enabled: Boolean) {
        project.isProxyModeEnabled = enabled
    }

    fun applyTemplate(template: VideoTemplate, clipPaths: List<String>): Boolean {
        if (clipPaths.size < template.clipSlotsCount) return false
        project.clips.clear()
        val durationPerClip = template.durationMs / template.clipSlotsCount
        clipPaths.take(template.clipSlotsCount).forEachIndexed { index, path ->
            val startMs = index * durationPerClip
            val endMs = (index + 1) * durationPerClip
            addClip(path = path, startMs = startMs, endMs = endMs)
        }
        return true
    }

    fun generateExportCommand(outputPath: String): String {
        val inputArgs = project.clips.joinToString(" ") { "-i ${it.path}" }
        val audioArgs = project.audioTracks.joinToString(" ") { "-i ${it.path}" }
        val duration = project.getTotalDurationMs() / 1000.0
        val proxyFlag = if (project.isProxyModeEnabled) "-preset ultrafast" else "-preset medium"
        return "ffmpeg $inputArgs $audioArgs -t $duration -c:v libx264 $proxyFlag $outputPath"
    }
}
