package com.example.adminaplikasicapstone.ui

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.adminaplikasicapstone.MainActivity
import com.example.adminaplikasicapstone.R
import com.example.adminaplikasicapstone.models.DisasterCaseDataModels
import com.example.adminaplikasicapstone.utils.firestore.FirestoreServices

class DetailLaporanActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var disasterType:TextView
    private lateinit var disasterLocation:TextView
    private lateinit var disasterPhoto:ImageView
    private lateinit var reportByEmail:TextView
    private lateinit var disasterTime:TextView
    private lateinit var disasterMapDetailLocation:TextView
    private lateinit var disasterDetailText:TextView
    private lateinit var btn_startProgress:Button
    private lateinit var btn_completeProgress_ifWaiting:Button
    private lateinit var btn_completeProgress_ifOnProgress:Button
    private lateinit var linearLayoutIfWaiting:LinearLayout
    private lateinit var linearLayoutIfOnProgress:LinearLayout
    private lateinit var linearLayoutIfProgressComplete:LinearLayout

    private lateinit var extras:DisasterCaseDataModels
    companion object{
        const val DISASTER_CASE_DATA = "DISASTER_CASE_DATA"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_laporan)
        supportActionBar?.title = "Detail Laporan"

        initializationIdLayout()
        setDataView()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        shareMessageToOfficerViaWhatsapp()
        return super.onOptionsItemSelected(item)
    }

    private fun setDataView() {

        extras = intent?.getParcelableExtra<DisasterCaseDataModels>(DISASTER_CASE_DATA) as DisasterCaseDataModels

        if (extras.disasterCaseStatus.toString()=="waiting"){
            linearLayoutIfWaiting.visibility = View.VISIBLE
            linearLayoutIfOnProgress.visibility = View.INVISIBLE
            linearLayoutIfProgressComplete.visibility = View.INVISIBLE
        }else if (extras.disasterCaseStatus.toString()=="onProgress"){
            linearLayoutIfWaiting.visibility = View.INVISIBLE
            linearLayoutIfOnProgress.visibility = View.VISIBLE
            linearLayoutIfProgressComplete.visibility = View.INVISIBLE
        }else if (extras.disasterCaseStatus.toString()=="complete"){
            linearLayoutIfWaiting.visibility = View.INVISIBLE
            linearLayoutIfOnProgress.visibility = View.INVISIBLE
            linearLayoutIfProgressComplete.visibility = View.VISIBLE
        }

        disasterLocation.text = extras.disasterLocation.toString()
        Glide.with(disasterPhoto).load(extras.disasterCaseDataPhoto).into(disasterPhoto)
        reportByEmail.text = extras.reportByEmail.toString()
        disasterTime.text = extras.disasterDateTime.toString()
        disasterDetailText.text = extras.disasterCaseDetail.toString()
    }

    private fun initializationIdLayout() {
        disasterLocation = findViewById(R.id.detailLaporanActivity_disasterLocation)
        disasterPhoto = findViewById(R.id.detailLaporanActivity_disasterPhoto)
        reportByEmail = findViewById(R.id.detailLaporanActivity_reportByEmail)
        disasterTime = findViewById(R.id.detailLaporanActivity_disasterTime)
        disasterMapDetailLocation = findViewById(R.id.detailLaporanActivity_disasterDetailMapLocation)
        disasterDetailText = findViewById(R.id.detailLaporanActivity_disasterDetailText)
        btn_startProgress = findViewById(R.id.detailLaporanActivity_btn_startProgress)
        btn_completeProgress_ifWaiting = findViewById(R.id.detailLaporanActivity_btn_completeProgress_if_waiting)
        linearLayoutIfWaiting = findViewById(R.id.detailLaporanActivity_linearlayout_if_waiting)
        btn_completeProgress_ifOnProgress = findViewById(R.id.detailLaporanActivity_btn_completeProgress_if_onprogress)
        linearLayoutIfOnProgress = findViewById(R.id.detailLaporanActivity_linearlayout_if_onProgress)
        linearLayoutIfProgressComplete = findViewById(R.id.detailLaporanActivity_linearlayout_if_progressComplete)

        disasterMapDetailLocation.setOnClickListener(this)
        disasterPhoto.setOnClickListener(this)
        disasterDetailText.setOnClickListener(this)
        btn_startProgress.setOnClickListener(this)
        btn_completeProgress_ifWaiting.setOnClickListener(this)
        btn_completeProgress_ifOnProgress.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.detailLaporanActivity_disasterPhoto->{
                val intent = Intent(this, DetailPhotoActivity::class.java)
                intent.putExtra("PHOTO", "${extras.disasterCaseDataPhoto}")
                startActivity(intent)
            }
            R.id.detailLaporanActivity_disasterDetailMapLocation->{
                val latitude = extras.disasterLatitude.toString()
                val longitude = extras.disasterLongitude.toString()
                val gmmIntentUri = Uri.parse("http://maps.google.com/maps?q=loc:$latitude,$longitude")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)
            }
            R.id.detailLaporanActivity_btn_startProgress->{
                startProgressDisasterCase()
            }
            R.id.detailLaporanActivity_btn_completeProgress_if_waiting->{
                val intent = Intent(this, DetailLaporanSelesaiActivity::class.java)
                startActivity(intent)
            }
            R.id.detailLaporanActivity_btn_completeProgress_if_onprogress->{
                val intent = Intent(this, DetailLaporanSelesaiActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun startProgressDisasterCase() {
        var firestoreServices = FirestoreServices()
        var updateStatusQuery = firestoreServices.DisasterCaseData().updateStatus("onProgress", extras.disasterCaseID.toString())
        updateStatusQuery.addOnCompleteListener {
            if (it.isSuccessful){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener {
            println("GAGALLLLLLLLLLLLLLLLL")
        }
    }

    private fun shareMessageToOfficerViaWhatsapp() {
        val latitude = extras.disasterLatitude.toString()
        val longitude = extras.disasterLongitude.toString()
        val googleMapLink = "http://maps.google.com/maps?q=loc:$latitude,$longitude"
        val message = setMessageToOfficer(googleMapLink, extras.disasterLocation,
                extras.reportByEmail, extras.disasterCaseDetail)
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.setPackage("com.whatsapp")
        intent.putExtra(Intent.EXTRA_TEXT, message)
        startActivity(intent)
    }

    private fun setMessageToOfficer(
            googleMapLink: String,
            disasterLocation: String?,
            reportByEmail: String?,
            disasterCaseDetail: String?
    ):String {
        var messageToUser = getUrlMessageLinkToUser(extras.reportByPhoneNumber.toString(), extras.reportByEmail.toString())
        var message = "KASUS DETAIL 'TIPE BENCANA' $disasterLocation" +
                "\n" +
                "\nDI LAPORKAN OLEH $reportByEmail" +
                "\n" +
                "\nSILAHKAN CHAT PELAPOR MELALUI $messageToUser" +
                "\n" +
                "\nALAMAT LOKASI BENCANA : $googleMapLink" +
                "\n" +
                "\nDETAIL KETERANGAN : $disasterCaseDetail"
        return message
    }

    private fun getUrlMessageLinkToUser(phoneNumberUser:String, emailUser:String): String {
        var url = "https://api.whatsapp.com/send?phone=+62$phoneNumberUser&text=Halo%20$emailUser,%20INI%20PETUGAS%20ANCANA"
        return url
    }
}