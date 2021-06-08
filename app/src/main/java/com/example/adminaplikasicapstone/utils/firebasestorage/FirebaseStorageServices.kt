package com.example.adminaplikasicapstone.utils.firebasestorage

import android.net.Uri
import com.example.adminaplikasicapstone.utils.firestore.FirestoreObject
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask


class FirebaseStorageServices {
    var firebaseStorageServices = FirebaseStorage.getInstance().reference
    inner class DisasterCaseData{
        fun getImageURLByName(name:String): Task<Uri> {
            return firebaseStorageServices.child("${FirestoreObject.COLLECTION_DISASTER_CASE_DATA}/${name}").downloadUrl
        }
        fun putImage(idPhoto:String, imageUri: Uri): UploadTask {
            return firebaseStorageServices.child("disasterCaseDataComplete/${idPhoto.toString()}/${idPhoto.toString()}.png").putFile(imageUri)
        }
    }
    inner class DisasterDataComplete{
        fun putImage(documentID:String, photoName:String, imageUri: Uri): UploadTask {
            return firebaseStorageServices.child("${FirestoreObject.COLLECTION_DISASTER_CASE_DATA_COMPLETE}/${documentID}/${photoName.toString()}")
                    .putFile(imageUri)
        }
        fun getURLimage(documentID: String, photoName: String): Task<Uri> {
            return firebaseStorageServices.child("${FirestoreObject.COLLECTION_DISASTER_CASE_DATA_COMPLETE}/${documentID}/${photoName.toString()}").downloadUrl
        }
    }
}