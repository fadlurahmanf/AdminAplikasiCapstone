package com.example.adminaplikasicapstone.ui.complete

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.example.adminaplikasicapstone.R
import com.example.adminaplikasicapstone.models.DisasterCaseDataModels
import com.example.adminaplikasicapstone.ui.DetailLaporanActivity
import com.example.adminaplikasicapstone.ui.DetailMultiplePhotoActivity
import com.example.adminaplikasicapstone.ui.DetailSinglePhotoActivity
import com.example.adminaplikasicapstone.utils.firebasestorage.FirebaseStorageServices

class DetailLaporanSelesai : AppCompatActivity(), View.OnClickListener {


    private lateinit var listImageReportByAdmin:ArrayList<String>
    private var listImageURLreportByAdmin:ArrayList<String> = ArrayList<String>()

    private lateinit var openImagePhotoByAdmin:TextView
    private lateinit var disasterCityLocation:TextView
    private lateinit var reportByEmail:TextView
    private lateinit var disasterDateTime:TextView
    private lateinit var disasterDetailMapLocation:TextView
    private lateinit var detailByUser:TextView
    private lateinit var detailByAdmin:TextView


    companion object{
        const val LIST_IMAGE_REPORT_BY_ADMIN = "LIST_IMAGE_REPORT_BY_ADMIN"
    }

    private lateinit var imageReportByUser:ImageView
    private lateinit var extras:DisasterCaseDataModels
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_laporan_selesai)
        initializationIdLayout()
        setViewDataFromIntent()
    }

    private fun setViewDataFromIntent(){
        disasterCityLocation.text = extras.disasterLocation.toString().toUpperCase()
        reportByEmail.text = extras.reportByEmail.toString()
        disasterDateTime.text = extras.disasterDateTime.toString()
        detailByUser.text = extras.disasterCaseDetail.toString()
        detailByAdmin.text = extras.disasterCaseDetailByAdmin.toString()
        Glide.with(imageReportByUser).load(extras.disasterCaseDataPhoto).into(imageReportByUser)
    }

    private fun initializationIdLayout() {
        imageReportByUser = findViewById(R.id.detailLaporanSelesaiActivity_image)
        disasterDateTime = findViewById(R.id.detailLaporanSelesaiActivity_disasterDateTime)
        reportByEmail = findViewById(R.id.detailLaporanSelesaiActivity_reportByEmail)
        disasterCityLocation = findViewById(R.id.detailLaporanSelesaiActivity_disasterCityLocation)
        openImagePhotoByAdmin = findViewById(R.id.detailLaporanSelesaiActivity_openPhotoFromAdmin)
        detailByAdmin = findViewById(R.id.detailLaporanSelesaiActivity_detailByAdmin)
        detailByUser = findViewById(R.id.detailLaporanSelesaiActivity_detailByUser)
        disasterDetailMapLocation = findViewById(R.id.detailLaporanSelesaiActivity_openDetailMapsLocation)

        imageReportByUser.setOnClickListener(this)
        openImagePhotoByAdmin.setOnClickListener(this)
        disasterDetailMapLocation.setOnClickListener(this)

        extras = intent.getParcelableExtra<DisasterCaseDataModels>(DetailLaporanActivity.DISASTER_CASE_DATA) as DisasterCaseDataModels
        listImageReportByAdmin = intent.getStringArrayListExtra(LIST_IMAGE_REPORT_BY_ADMIN) as ArrayList<String>
        listImageURLreportByAdmin.clear()
        listImageReportByAdmin.forEach {
            var firebaseStorageServices = FirebaseStorageServices()
            firebaseStorageServices.DisasterDataComplete().getURLimage(extras.disasterCaseID.toString(), it.toString()).addOnCompleteListener {
                if (it.isSuccessful){
                    listImageURLreportByAdmin.add(it.result.toString())
                }
            }.addOnFailureListener {
                Toast.makeText(this, "${it.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.detailLaporanSelesaiActivity_image->{
                val intent = Intent(this, DetailSinglePhotoActivity::class.java)
                intent.putExtra("PHOTO", extras.disasterCaseDataPhoto)
                startActivity(intent)
            }
            R.id.detailLaporanSelesaiActivity_openPhotoFromAdmin->{
                val intent = Intent(this, DetailMultiplePhotoActivity::class.java)
                intent.putStringArrayListExtra(DetailMultiplePhotoActivity.LIST_IMAGE_URL_REPORT_BY_ADMIN, listImageURLreportByAdmin)
                startActivity(intent)
            }
            R.id.detailLaporanSelesaiActivity_openDetailMapsLocation->{
                val gmmIntentUri = Uri.parse("http://maps.google.com/maps?q=loc:${extras.disasterLatitude.toString()},${extras.disasterLongitude.toString()}")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)
            }
        }
    }
}