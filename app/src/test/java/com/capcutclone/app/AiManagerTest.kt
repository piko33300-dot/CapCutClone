package com.capcutclone.app

import com.capcutclone.app.ai.AiManager
import com.capcutclone.app.model.VideoClip
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class AiManagerTest {

    @Test
    fun testGenerateCaptionItems() {
        val captions = AiManager.generateCaptionItems("test_video.mp4")
        assertFalse(captions.isEmpty())
        assertEquals(3, captions.size)
        assertEquals("Welcome to CapCutClone!", captions[0].text)
        assertEquals(0L, captions[0].startMs)
    }

    @Test
    fun testAiCutoutAndRetouchAndEraserAndDubbing() = runBlocking {
        val clip = VideoClip("c1", "test.mp4", 0L, 5000L)

        assertTrue(AiManager.applyAutoCutout(clip))
        assertTrue(clip.aiFeatures.autoCutoutEnabled)

        assertTrue(AiManager.applyFaceRetouch(clip, 0.8f))
        assertEquals(0.8f, clip.aiFeatures.faceRetouchLevel, 0.001f)

        assertTrue(AiManager.applyMagicEraserRegion(clip, "10,10,100,100"))
        assertEquals(1, clip.aiFeatures.magicEraserRegions.size)

        val captions = AiManager.generateCaptionItems("test.mp4")
        val dubbed = AiManager.generateAiDubbing(captions, "AR")
        assertEquals(3, dubbed.size)
        assertTrue(dubbed[0].translatedText?.contains("[AR]") == true)
    }
}
