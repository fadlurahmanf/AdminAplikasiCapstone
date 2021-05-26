package com.example.adminaplikasicapstone.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageSwitcher
import android.widget.ImageView
import com.example.adminaplikasicapstone.R

class DetailLaporanSelesaiActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var imagesResult:ImageSwitcher
    private lateinit var btn_next:Button
    private lateinit var imageView:ImageView
    private lateinit var btn_previous:Button

    private var positionImage = 0

    private val PICK_IMAGES_CODE = 0

    private var pickedImages: ArrayList<Uri>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_laporan_selesai)

        initializationIdLayout()

        pickedImages = ArrayList()

    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.detailLaporanSelesaiActivity_image->{
                pickImageFromGallery()
            }
            R.id.detailLaporanSelesaiActivity_imageViewLayout->{
                pickImageFromGallery()
            }
            R.id.detailLaporanSelesaiActivity_btnPrevious->{
                if (positionImage>0){
                    positionImage--
                    imagesResult.setImageURI(pickedImages!![positionImage])
                }else{
                    positionImage = pickedImages!!.size-1
                    imagesResult.setImageURI(pickedImages!![positionImage])
                }
            }
            R.id.detailLaporanSelesaiActivity_btnNext->{
                if (positionImage<pickedImages!!.size-1){
                    positionImage++
                    imagesResult.setImageURI(pickedImages!![positionImage])
                }else{
                    positionImage=0
                    imagesResult.setImageURI(pickedImages!![positionImage])
                }
            }
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
                if (data!!.clipData!=null){
                    //PICK MULTIPLE IMAGE
                    val count = data.clipData!!.itemCount
                    pickedImages!!.clear()
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
                    imagesResult.setImageURI(image)
                    positionImage=0
                    imageView.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun initializationIdLayout() {
        imagesResult = findViewById(R.id.detailLaporanSelesaiActivity_image)
        btn_previous = findViewById(R.id.detailLaporanSelesaiActivity_btnPrevious)
        btn_next = findViewById(R.id.detailLaporanSelesaiActivity_btnNext)
        imageView = findViewById(R.id.detailLaporanSelesaiActivity_imageViewLayout)

        imagesResult.setOnClickListener(this)
        btn_previous.setOnClickListener(this)
        btn_next.setOnClickListener(this)
        imageView.setOnClickListener(this)

        //SetUp Image Switcher
        imagesResult.setFactory{ImageView(applicationContext)}
    }
}