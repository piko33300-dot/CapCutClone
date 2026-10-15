package com.capcutclone.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capcutclone.app.databinding.ActivityVideoEditingBinding

class VideoEditingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVideoEditingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoEditingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var playing = false
        binding.btnPlayPause.setOnClickListener {
            playing = !playing
            binding.btnPlayPause.setImageResource(
                if (playing) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play
            )
        }

        binding.btnTrim.setOnClickListener { }
        binding.btnSplit.setOnClickListener { }
        binding.btnText.setOnClickListener { }
        binding.btnAiTools.setOnClickListener { }
        binding.btnExport.setOnClickListener { }
    }
}
