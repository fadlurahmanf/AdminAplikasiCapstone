package com.example.adminaplikasicapstone.utils.firestore

import com.example.adminaplikasicapstone.utils.firestore.FirestoreObject.COLLECTION_ADMIN_DATA
import com.example.adminaplikasicapstone.utils.firestore.FirestoreObject.COLLECTION_DISASTER_CASE_DATA
import com.example.adminaplikasicapstone.utils.firestore.FirestoreObject.COL_DISASTER_CASE_DATE
import com.example.adminaplikasicapstone.utils.firestore.FirestoreObject.COL_DISASTER_CASE_STATUS
import com.example.adminaplikasicapstone.utils.firestore.FirestoreObject.COL_EMAIL
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*

class FirestoreServices {
    var firestoreService = FirebaseFirestore.getInstance()
    inner class AdminData{
        fun insertAdminData(adminData:MutableMap<String, Any>): Task<Void> {
            return firestoreService.collection(COLLECTION_ADMIN_DATA).document(adminData[COL_EMAIL].toString()).set(adminData)
        }
        fun checkIsEmailIsInAdminData(email:String): Task<QuerySnapshot> {
            return firestoreService.collection(COLLECTION_ADMIN_DATA).get()
        }
    }
    inner class DisasterCaseData{
        fun getAllDisasterCaseDataByStatus(status:String): Task<QuerySnapshot> {
            return firestoreService.collection(COLLECTION_DISASTER_CASE_DATA)
                    .orderBy(COL_DISASTER_CASE_DATE, Query.Direction.ASCENDING)
                    .get()

        }
    }
}