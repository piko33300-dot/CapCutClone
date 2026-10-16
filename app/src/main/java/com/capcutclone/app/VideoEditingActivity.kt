package com.capcutclone.app

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.capcutclone.app.ai.AiManager
import com.capcutclone.app.databinding.ActivityVideoEditingBinding
import com.capcutclone.app.engine.VideoEditorEngine
import kotlinx.coroutines.launch

class VideoEditingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVideoEditingBinding
    private val editorEngine = VideoEditorEngine()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoEditingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize a default clip for editing
        val initialClip = editorEngine.addClip("sample_video.mp4", 0L, 10000L)
        updateStatus("Loaded default clip (${initialClip.durationMs / 1000}s)")

        var playing = false
        binding.btnPlayPause.setOnClickListener {
            playing = !playing
            binding.btnPlayPause.setImageResource(
                if (playing) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play
            )
            updateStatus(if (playing) "Playing..." else "Paused")
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnTrim.setOnClickListener {
            if (editorEngine.project.clips.isNotEmpty()) {
                val clip = editorEngine.project.clips.first()
                val success = editorEngine.trimClip(clip.id, clip.startMs + 500L, clip.endMs - 500L)
                if (success) {
                    Toast.makeText(this, "Trimmed 0.5s from start & end", Toast.LENGTH_SHORT).show()
                    updateStatus("Clips: ${editorEngine.project.clips.size}, Total Duration: ${editorEngine.project.getTotalDurationMs()}ms")
                }
            } else {
                Toast.makeText(this, "No clips to trim", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSplit.setOnClickListener {
            if (editorEngine.project.clips.isNotEmpty()) {
                val clip = editorEngine.project.clips.first()
                val midPoint = (clip.startMs + clip.endMs) / 2
                val splitResult = editorEngine.splitClip(clip.id, midPoint)
                if (splitResult != null) {
                    Toast.makeText(this, "Split clip into 2 segments", Toast.LENGTH_SHORT).show()
                    updateStatus("Clips count: ${editorEngine.project.clips.size}")
                } else {
                    Toast.makeText(this, "Cannot split clip at point $midPoint", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "No clips to split", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnText.setOnClickListener {
            val input = EditText(this)
            input.hint = "Enter text overlay..."
            AlertDialog.Builder(this)
                .setTitle("Add Text Overlay")
                .setView(input)
                .setPositiveButton("Add") { _, _ ->
                    val text = input.text.toString().trim()
                    if (text.isNotEmpty()) {
                        val overlay = editorEngine.addTextOverlay(text, 0L, 5000L)
                        Toast.makeText(this, "Added overlay: ${overlay.text}", Toast.LENGTH_SHORT).show()
                        updateStatus("Text overlays count: ${editorEngine.project.textOverlays.size}")
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        binding.btnAiTools.setOnClickListener {
            lifecycleScope.launch {
                val captions = AiManager.generateCaptionItems("sample_video.mp4")
                editorEngine.setCaptions(captions)
                Toast.makeText(this@VideoEditingActivity, "Generated ${captions.size} AI captions!", Toast.LENGTH_LONG).show()
                updateStatus("Captions loaded: ${captions.size}")
            }
        }

        binding.btnExport.setOnClickListener {
            val exportCmd = editorEngine.generateExportCommand("exported_video.mp4")
            AlertDialog.Builder(this)
                .setTitle("Export Command Generated")
                .setMessage(exportCmd)
                .setPositiveButton("OK", null)
                .show()
        }
    }

    private fun updateStatus(status: String) {
        binding.tvProjectName.text = "Project | $status"
    }
}
