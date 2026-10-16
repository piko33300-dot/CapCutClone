package com.capcutclone.app

import com.capcutclone.app.engine.VideoEditorEngine
import com.capcutclone.app.model.ChromaKeySettings
import com.capcutclone.app.model.ColorGradingSettings
import com.capcutclone.app.model.KeyframePoint
import com.capcutclone.app.model.SpeedCurve
import com.capcutclone.app.model.VideoTemplate
import org.junit.Assert.*
import org.junit.Test

class VideoEditorEngineTest {

    @Test
    fun testAddClipAndDuration() {
        val engine = VideoEditorEngine()
        val clip = engine.addClip("sample.mp4", 0L, 5000L)

        assertEquals(1, engine.project.clips.size)
        assertEquals(5000L, clip.durationMs)
        assertEquals(5000L, engine.project.getTotalDurationMs())
    }

    @Test
    fun testTrimClip() {
        val engine = VideoEditorEngine()
        val clip = engine.addClip("sample.mp4", 0L, 10000L)

        val success = engine.trimClip(clip.id, 2000L, 8000L)
        assertTrue(success)
        assertEquals(2000L, clip.startMs)
        assertEquals(8000L, clip.endMs)
        assertEquals(6000L, clip.durationMs)
    }

    @Test
    fun testSplitClip() {
        val engine = VideoEditorEngine()
        val clip = engine.addClip("sample.mp4", 0L, 10000L)

        val result = engine.splitClip(clip.id, 4000L)
        assertNotNull(result)

        val (first, second) = result!!
        assertEquals(2, engine.project.clips.size)
        assertEquals(0L, first.startMs)
        assertEquals(4000L, first.endMs)
        assertEquals(4000L, second.startMs)
        assertEquals(10000L, second.endMs)
    }

    @Test
    fun testSpeedAdjustmentAndSpeedCurve() {
        val engine = VideoEditorEngine()
        val clip = engine.addClip("sample.mp4", 0L, 10000L)

        engine.setSpeed(clip.id, 2.0f)
        assertEquals(5000L, clip.durationMs)

        val curve = SpeedCurve("Montage", listOf(0.0f to 0.5f, 0.5f to 2.0f, 1.0f to 1.0f))
        val curveSet = engine.setSpeedCurve(clip.id, curve)
        assertTrue(curveSet)
        assertEquals("Montage", clip.speedCurve?.name)
    }

    @Test
    fun testKeyframeAndChromaKeyAndColorGrading() {
        val engine = VideoEditorEngine()
        val clip = engine.addClip("sample.mp4", 0L, 10000L)

        val keyframe = KeyframePoint(timestampMs = 1000L, scale = 1.5f)
        assertTrue(engine.addKeyframeToClip(clip.id, keyframe))
        assertEquals(1, clip.keyframes.size)

        val chroma = ChromaKeySettings(enabled = true, targetColorHex = "#00FF00")
        assertTrue(engine.setChromaKeySettings(clip.id, chroma))
        assertTrue(clip.chromaKey.enabled)

        val color = ColorGradingSettings(brightness = 0.2f, contrast = 1.1f)
        assertTrue(engine.setColorGradingSettings(clip.id, color))
        assertEquals(0.2f, clip.colorGrading.brightness, 0.001f)
    }

    @Test
    fun testAudioTrackAndProxyModeAndTemplates() {
        val engine = VideoEditorEngine()
        val audio = engine.addAudioTrack("bgm.mp3", 0L, 10000L, isVoiceover = false)
        assertEquals(1, engine.project.audioTracks.size)
        assertFalse(audio.isVoiceover)

        engine.toggleProxyMode(true)
        assertTrue(engine.project.isProxyModeEnabled)

        val template = VideoTemplate("t1", "Viral Reel", 10000L, 2, "Test Template")
        val applied = engine.applyTemplate(template, listOf("clip1.mp4", "clip2.mp4"))
        assertTrue(applied)
        assertEquals(2, engine.project.clips.size)

        val exportCmd = engine.generateExportCommand("output.mp4")
        assertTrue(exportCmd.contains("-preset ultrafast"))
    }
}
