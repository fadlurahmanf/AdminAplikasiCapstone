package com.example.adminaplikasicapstone.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.adminaplikasicapstone.R

class DetailPhotoActivity : AppCompatActivity() {
    private lateinit var disasterPhoto:ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_photo)
        disasterPhoto = findViewById(R.id.detailPhotoActivity_disasterImage)

        var extras = intent.getStringExtra("PHOTO")

        Glide.with(disasterPhoto).load(extras).into(disasterPhoto)
    }
}