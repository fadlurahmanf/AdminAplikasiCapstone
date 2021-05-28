package com.example.adminaplikasicapstone.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.adminaplikasicapstone.R
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.TensorOperator
import org.tensorflow.lite.support.common.TensorProcessor
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp
import org.tensorflow.lite.support.label.TensorLabel
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.*


class CobaActivity : AppCompatActivity() {

    //MACHINE LEARNING NEEDS
    lateinit var tflite:Interpreter
    var imageSizeX:Int = 0
    var imageSizeY:Int = 0
    private lateinit var inputImageBuffer:TensorImage
    private lateinit var outputProbabilityBuffer:TensorBuffer
    private lateinit var probabilityProcessor:TensorProcessor
    private lateinit var bitmap:Bitmap
    var IMAGE_MEAN:Float = 0.0f
    var IMAGE_STD:Float = 1.0f
    var PROBABILITY_MEAN = 0.0f
    var PROBABILITY_STD = 255.0f
    private lateinit var labels:List<String>

    private lateinit var image:ImageView
    private lateinit var textMachineLearning:TextView
    private lateinit var submit_btn:Button

    companion object{
        const val REQUEST_PICK_FROM_GALLERY = 0
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coba)
        supportActionBar?.title = "COBA MACHINE LEARNING"

        image = findViewById(R.id.image_machinelearning)
        textMachineLearning = findViewById(R.id.hasilmachinelearning)
        submit_btn = findViewById(R.id.submit_machinelearning)

        image.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_PICK_FROM_GALLERY)
        }

        try {
            var file: MappedByteBuffer? = loadModelFile(this)
            tflite = Interpreter(file!!)
        }catch (e:IOException){
            Toast.makeText(this, "INI EXCEPTION TRY CATCH AWAL ${e.message}", Toast.LENGTH_LONG).show()
        }

        submit_btn.setOnClickListener {
            var imageTensorIndex:Int = 0
            var imageShape = tflite.getInputTensor(imageTensorIndex).shape()
            imageSizeX = imageShape[1]
            imageSizeY = imageShape[2]
            var imageDataType = tflite.getInputTensor(imageTensorIndex).dataType()

            var probabilityTensorIndex:Int = 0
            var probabilityShape = tflite.getOutputTensor(probabilityTensorIndex).shape()
            var probabilityDataType = tflite.getOutputTensor(probabilityTensorIndex).dataType()

            inputImageBuffer = TensorImage(imageDataType)
            outputProbabilityBuffer = TensorBuffer.createFixedSize(probabilityShape, probabilityDataType)
            probabilityProcessor = TensorProcessor.Builder().add(getPostprocessNormalizeOp()).build()

            inputImageBuffer = loadImage(bitmap)
            
            tflite.run(inputImageBuffer.buffer, outputProbabilityBuffer.buffer.rewind())
            showResult()
        }

    }

    private fun showResult() {
        try {
            labels = FileUtil.loadLabels(this, "machinelearningtext.txt")
        }catch (e:Exception){
            Toast.makeText(this, "INI EXCEPTION SHOW RESULT ${e.message}", Toast.LENGTH_LONG).show()
        }
        var labeledProperty = TensorLabel(labels, probabilityProcessor.process(outputProbabilityBuffer))
                .mapWithFloatValue
        var maxValueInMap:Float = (Collections.max(labeledProperty.values))

        for (entry in labeledProperty.entries) {
            if (entry.value == maxValueInMap) {

                textMachineLearning.setText(entry.key)
            }
        }
    }

    private fun loadImage(bitmap: Bitmap): TensorImage {
        inputImageBuffer.load(bitmap)

        var cropSize:Int = Math.min(bitmap.width, bitmap.height)
        var imageProcessor = ImageProcessor.Builder()
                .add(ResizeWithCropOrPadOp(cropSize, cropSize))
                .add(ResizeOp(imageSizeX, imageSizeY, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
                .add(getPreprocessNormalizeOp())
                .build()
        return imageProcessor.process(inputImageBuffer)
    }

    private fun getPreprocessNormalizeOp(): TensorOperator? {
        return NormalizeOp(IMAGE_MEAN, IMAGE_STD)
    }

    private fun getPostprocessNormalizeOp(): TensorOperator? {
        return NormalizeOp(PROBABILITY_MEAN, PROBABILITY_STD)
    }

    private fun loadModelFile(cobaActivity: CobaActivity): MappedByteBuffer? {
        var fileDescriptor = cobaActivity.assets.openFd("model.tflite")
        var inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        var fileChannel = inputStream.channel
        var startOffset:Long = fileDescriptor.startOffset
        var declaredLength:Long = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode== REQUEST_PICK_FROM_GALLERY && resultCode == Activity.RESULT_OK){
            var pickedImages = data?.data
            try {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, pickedImages)
                image.setImageBitmap(bitmap)
            }catch (e:IOException){
                Toast.makeText(this, "INI ERROR ON ACTIVITY RESULT ${e.message}", Toast.LENGTH_LONG).show()
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}