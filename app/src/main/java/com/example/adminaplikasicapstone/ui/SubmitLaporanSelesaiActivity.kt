package com.example.adminaplikasicapstone.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.adminaplikasicapstone.MainActivity
import com.example.adminaplikasicapstone.R
import com.example.adminaplikasicapstone.models.DisasterCaseDataModels
import com.example.adminaplikasicapstone.utils.firebasestorage.FirebaseStorageServices
import com.example.adminaplikasicapstone.utils.firestore.FirestoreObject
import com.example.adminaplikasicapstone.utils.firestore.FirestoreServices
import com.google.firebase.storage.StorageException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class SubmitLaporanSelesaiActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var extras:DisasterCaseDataModels

    private lateinit var imagesResult:ImageSwitcher
    private lateinit var btn_next:Button
    private lateinit var imageView:ImageView
    private lateinit var detailByAdmin:EditText
    private lateinit var btn_previous:Button
    private lateinit var btn_submit:Button
    private lateinit var linearlayoutDetail:LinearLayout
    private lateinit var loadingBar:ProgressBar

    private var positionImage = 0

    private val PICK_IMAGES_CODE = 0

    private var pickedImages: ArrayList<Uri>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submit_laporan_selesai)

        initializationIdLayout()

        pickedImages = ArrayList()

    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.submitLaporanSelesaiActivity_image->{
                pickImageFromGallery()
            }
            R.id.submitLaporanSelesaiActivity_imageViewLayout->{
                pickImageFromGallery()
            }
            R.id.submitLaporanSelesaiActivity_btnPrevious->{
                if (positionImage>0){
                    positionImage--
                    imagesResult.setImageURI(pickedImages!![positionImage])
                }else{
                    positionImage = pickedImages!!.size-1
                    imagesResult.setImageURI(pickedImages!![positionImage])
                }
            }
            R.id.submitLaporanSelesaiActivity_btnNext->{
                if (positionImage<pickedImages!!.size-1){
                    positionImage++
                    imagesResult.setImageURI(pickedImages!![positionImage])
                }else{
                    positionImage=0
                    imagesResult.setImageURI(pickedImages!![positionImage])
                }
            }
            R.id.submitLaporanSelesaiActivity_btnSubmit->{
                putMultipleImageToFirebase()
            }
        }
    }

    private fun putMultipleImageToFirebase(){
        loadingBar.visibility = View.VISIBLE
        imagesResult.visibility = View.INVISIBLE
        btn_previous.visibility = View.INVISIBLE
        btn_next.visibility = View.INVISIBLE
        linearlayoutDetail.visibility = View.INVISIBLE
        btn_submit.visibility = View.INVISIBLE
        val firebaseStorageServices = FirebaseStorageServices()
        var listNameOfImage:ArrayList<String> = ArrayList<String>()

        //SET NAME FOR EACH IMAGE
        for (i in 0 until pickedImages!!.size){
            var randomName:String = UUID.randomUUID().toString()
            listNameOfImage.add("${randomName.toString()}.png")
        }
        //PUT EVERY IMAGE TO FIREBASE
        GlobalScope.launch(Dispatchers.IO){
            for (i in 0 until listNameOfImage.size){
                var insert = firebaseStorageServices.DisasterDataComplete().putImage(extras.disasterCaseID.toString(), listNameOfImage[i], pickedImages!![i])
                insert.await()
            }
            insertDisasterDataCompletedToFirestore(listNameOfImage)
        }
    }

    private fun insertDisasterDataCompletedToFirestore(listNameOfImage:ArrayList<String>){
        var contentValue:MutableMap<String, Any> = HashMap()
        contentValue.put(FirestoreObject.COL_DISASTER_CASE_ID, extras.disasterCaseID.toString())
        contentValue.put(FirestoreObject.COL_DISASTER_TYPE, extras.disasterType.toString())
        contentValue.put(FirestoreObject.COL_DISASTER_CASE_DATE, extras.disasterDateTime.toString())
        contentValue.put(FirestoreObject.COL_DISASTER_CASE_DETAIL, extras.disasterCaseDetail.toString())
        contentValue.put(FirestoreObject.COL_DETAIL_BY_ADMIN, detailByAdmin.text.toString())
        contentValue.put(FirestoreObject.COL_DISASTER_REPORT_BY_EMAIL, extras.reportByEmail.toString())
        contentValue.put(FirestoreObject.COL_DISASTER_CASE_IMAGE, "${extras.disasterCaseID}.png")
        // SET CONTENT VALUE FOR IMAGE
        contentValue.put(FirestoreObject.COL_LIST_IMAGE_REPORT_BY_ADMIN, listNameOfImage)
        contentValue.put(FirestoreObject.COL_DISASTER_LOCATION, extras.disasterLocation.toString())
        contentValue.put(FirestoreObject.COL_DISASTER_LATITUDE, extras.disasterLatitude.toString())
        contentValue.put(FirestoreObject.COL_DISASTER_LONGITUDE, extras.disasterLongitude.toString())
        contentValue.put(FirestoreObject.COL_DISASTER_REPORT_BY_PHONE_NUMBER, extras.reportByPhoneNumber.toString())
        contentValue.put(FirestoreObject.COL_DISASTER_CASE_STATUS, "complete")
        var firestoreServices = FirestoreServices()
        var insertQuery = firestoreServices.DisasterCaseDataComplete().updateStatusToComplete(extras.disasterCaseID.toString(), contentValue)
        insertQuery.addOnCompleteListener {
            if (it.isSuccessful){
                updateStatusInDisasterCaseDataFirestore()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "${it.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateStatusInDisasterCaseDataFirestore(){
        var firestoreServices = FirestoreServices()
        firestoreServices.DisasterCaseData().updateStatus("complete", extras.disasterCaseID.toString()).addOnCompleteListener {
            if (it.isSuccessful){
                Toast.makeText(this, "BERHASIL", Toast.LENGTH_LONG).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "${it.message}", Toast.LENGTH_LONG).show()
            loadingBar.visibility = View.INVISIBLE
            imagesResult.visibility = View.VISIBLE
            btn_previous.visibility = View.VISIBLE
            btn_next.visibility = View.VISIBLE
            linearlayoutDetail.visibility = View.VISIBLE
            btn_submit.visibility = View.VISIBLE
        }
    }

    private fun pickImageFromGallery(){
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image(s)"), PICK_IMAGES_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode==PICK_IMAGES_CODE){
            if (resultCode==Activity.RESULT_OK){
                pickedImages!!.clear()
                if (data!!.clipData!=null){
                    //PICK MULTIPLE IMAGE
                    val count = data.clipData!!.itemCount
                    for (i in 0 until count){
                        val imageUri = data.clipData!!.getItemAt(i).uri
                        pickedImages!!.add(imageUri)
                    }
                    imagesResult.setImageURI(pickedImages?.get(0))
                    positionImage=0
                    imageView.visibility = View.INVISIBLE
                }else{
                    //PICK SINGLE IMAGE
                    val image = data.data
                    pickedImages!!.add(image!!)
                    imagesResult.setImageURI(image)
                    positionImage=0
                    imageView.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun initializationIdLayout() {
        imagesResult = findViewById(R.id.submitLaporanSelesaiActivity_image)
        btn_previous = findViewById(R.id.submitLaporanSelesaiActivity_btnPrevious)
        btn_next = findViewById(R.id.submitLaporanSelesaiActivity_btnNext)
        imageView = findViewById(R.id.submitLaporanSelesaiActivity_imageViewLayout)
        detailByAdmin = findViewById(R.id.submitLaporanSelesaiActivity_detailByAdmin)
        btn_submit = findViewById(R.id.submitLaporanSelesaiActivity_btnSubmit)
        linearlayoutDetail = findViewById(R.id.submitLaporanSelesaiActivity_linearlayoutdetail)
        loadingBar = findViewById(R.id.submitLaporanSelesaiActivity_loadingBar)

        loadingBar.visibility = View.INVISIBLE

        imagesResult.setOnClickListener(this)
        btn_previous.setOnClickListener(this)
        btn_next.setOnClickListener(this)
        btn_submit.setOnClickListener(this)
        imageView.setOnClickListener(this)

        //SetUp Image Switcher
        imagesResult.setFactory{ImageView(applicationContext)}

        //setup extras
        extras = intent?.getParcelableExtra<DisasterCaseDataModels>(DetailLaporanActivity.DISASTER_CASE_DATA) as DisasterCaseDataModels
    }
}

