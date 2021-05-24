package com.example.adminaplikasicapstone.ui

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.adminaplikasicapstone.R
import com.example.adminaplikasicapstone.models.DisasterCaseDataModels

class DetailLaporanActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var disasterType:TextView
    private lateinit var disasterLocation:TextView
    private lateinit var disasterPhoto:ImageView
    private lateinit var reportByEmail:TextView
    private lateinit var disasterTime:TextView
    private lateinit var disasterMapDetailLocation:TextView
    private lateinit var disasterDetailText:TextView
    private lateinit var extras:DisasterCaseDataModels
    companion object{
        const val DISASTER_CASE_DATA = "DISASTER_CASE_DATA"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_laporan)
        supportActionBar?.title = "Detail Laporan"

        initializationIdLayout()
        setDataView()
    }

    private fun setDataView() {
//        var extras = intent?.getParcelableExtra<DisasterCaseDataModels>(DISASTER_CASE_DATA) as DisasterCaseDataModels

        disasterLocation.text = extras.disasterLocation.toString()
        Glide.with(disasterPhoto).load(extras.disasterCaseDataPhoto).into(disasterPhoto)
        reportByEmail.text = extras.reportByEmail.toString()
        disasterTime.text = extras.disasterDateTime.toString()
    }

    private fun initializationIdLayout() {
        disasterLocation = findViewById(R.id.detailLaporanActivity_disasterLocation)
        disasterPhoto = findViewById(R.id.detailLaporanActivity_disasterPhoto)
        reportByEmail = findViewById(R.id.detailLaporanActivity_reportByEmail)
        disasterTime = findViewById(R.id.detailLaporanActivity_disasterTime)
        disasterMapDetailLocation = findViewById(R.id.detailLaporanActivity_disasterDetailMapLocation)
        disasterDetailText = findViewById(R.id.detailLaporanActivity_disasterDetailText)

        disasterMapDetailLocation.setOnClickListener(this)
        disasterPhoto.setOnClickListener(this)
        disasterDetailText.setOnClickListener(this)

        extras = intent?.getParcelableExtra<DisasterCaseDataModels>(DISASTER_CASE_DATA) as DisasterCaseDataModels
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.detailLaporanActivity_disasterPhoto->{
                val intent = Intent(this, DetailPhotoActivity::class.java)
                intent.putExtra("PHOTO", "https://firebasestorage.googleapis.com/v0/b/ancana-b21-cap0252.appspot.com/o/disasterCaseData%2F0af9bf03-612a-41ed-aa5d-7342d358b805.png?alt=media&token=6c5f3f4b-3242-4085-a2cf-f68f5f956680")
                startActivity(intent)
            }
            R.id.detailLaporanActivity_disasterDetailMapLocation->{
//                var extras = intent?.getParcelableExtra<DisasterCaseDataModels>(DISASTER_CASE_DATA) as DisasterCaseDataModels
                val latitude = extras.disasterLatitude.toString()
                val longitude = extras.disasterLongitude.toString()
                val gmmIntentUri = Uri.parse("http://maps.google.com/maps?q=loc:$latitude,$longitude")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)
            }
            R.id.detailLaporanActivity_disasterDetailText->{
                val latitude = extras.disasterLatitude.toString()
                val longitude = extras.disasterLongitude.toString()
                val googleMapLink = "http://maps.google.com/maps?q=loc:$latitude,$longitude"
                val message = setMessage(latitude, longitude, googleMapLink)
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.setPackage("com.whatsapp")
                intent.putExtra(Intent.EXTRA_TEXT, message)
                startActivity(intent)
            }
        }
    }

    private fun setMessage(
        latitude: String,
        longitude: String,
        googleMapLink: String
    ):String {
        var message = "KASUS DETAIL LAPORAN" +
                "\nALAMAT DI DEPOK" +
                "\nALAMAT DETAIL : $googleMapLink" +
                "NOMOR TELEPON SEKIAN"
        return message
    }
}