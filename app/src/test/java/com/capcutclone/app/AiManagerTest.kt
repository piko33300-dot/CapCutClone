package com.capcutclone.app

import com.capcutclone.app.ai.AiManager
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
}
