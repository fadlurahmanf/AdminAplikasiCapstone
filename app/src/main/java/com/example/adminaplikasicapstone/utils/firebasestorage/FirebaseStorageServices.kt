package com.example.adminaplikasicapstone.utils.firebasestorage

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class FirebaseStorageServices {
    var firebaseStorageServices = FirebaseStorage.getInstance().reference
    inner class DisasterCaseData{
        fun getImageByName(){
            var getPath = firebaseStorageServices.child("disasterCaseData/0af9bf03-612a-41ed-aa5d-7342d358b805.png").downloadUrl
            getPath.addOnCompleteListener {
                println(it.result)
            }.addOnFailureListener {
                println(it.message)
            }
        }
        fun getImageURLByName(name:String): Task<Uri> {
            println("KEDUA")
            return firebaseStorageServices.child("disasterCaseData/${name}").downloadUrl
        }
        fun getImage(name: String): StorageReference {
            return firebaseStorageServices.child("disasterCaseData/${name}")
        }
    }
}