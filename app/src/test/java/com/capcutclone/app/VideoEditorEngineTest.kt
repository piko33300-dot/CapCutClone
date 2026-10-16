package com.capcutclone.app

import com.capcutclone.app.engine.VideoEditorEngine
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
    fun testSpeedAdjustment() {
        val engine = VideoEditorEngine()
        val clip = engine.addClip("sample.mp4", 0L, 10000L)

        engine.setSpeed(clip.id, 2.0f)
        assertEquals(5000L, clip.durationMs)
    }

    @Test
    fun testTextOverlayAndExport() {
        val engine = VideoEditorEngine()
        engine.addClip("sample.mp4", 0L, 10000L)
        val overlay = engine.addTextOverlay("Title Text", 0L, 3000L)

        assertEquals(1, engine.project.textOverlays.size)
        assertEquals("Title Text", overlay.text)

        val exportCmd = engine.generateExportCommand("output.mp4")
        assertTrue(exportCmd.contains("-i sample.mp4"))
        assertTrue(exportCmd.contains("output.mp4"))
    }
}
