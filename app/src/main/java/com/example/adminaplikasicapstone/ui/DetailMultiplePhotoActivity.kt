package com.example.adminaplikasicapstone.ui

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageSwitcher
import android.widget.ImageView
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.adminaplikasicapstone.R

class DetailMultiplePhotoActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var images:ImageView
    private lateinit var btn_previous:ImageView
    private lateinit var btn_next:ImageView
    private var listURLimageReportByAdmin:ArrayList<String> = ArrayList<String>()
    private var position:Int = 0
    companion object{
        const val LIST_IMAGE_URL_REPORT_BY_ADMIN = "LIST_IMAGE_URL_REPORT_BY_ADMIN"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_multiple_photo)
        initializationIdLayout()
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.detailMultiplePhoto_btn_previous->{
                if (position>0){
                    position--
                    Glide.with(images).load(listURLimageReportByAdmin[position]).into(images)
                }else{
                    position=listURLimageReportByAdmin.size-1
                    Glide.with(images).load(listURLimageReportByAdmin[position]).into(images)
                }
            }
            R.id.detailMultiplePhoto_btn_next->{
                if (position<listURLimageReportByAdmin.size-1){
                    position++
                    Glide.with(images).load(listURLimageReportByAdmin[position]).into(images)
                }else{
                    position=0
                    Glide.with(images).load(listURLimageReportByAdmin[position]).into(images)
                }
            }
        }
    }

    private fun initializationIdLayout() {
        images = findViewById(R.id.detailMultiplePhoto_image)
        btn_previous = findViewById(R.id.detailMultiplePhoto_btn_previous)
        btn_next = findViewById(R.id.detailMultiplePhoto_btn_next)

        btn_previous.setOnClickListener(this)
        btn_next.setOnClickListener(this)

        listURLimageReportByAdmin = intent.getStringArrayListExtra(LIST_IMAGE_URL_REPORT_BY_ADMIN) as ArrayList<String>

        Glide.with(images).load(listURLimageReportByAdmin[position]).into(images)

    }
}