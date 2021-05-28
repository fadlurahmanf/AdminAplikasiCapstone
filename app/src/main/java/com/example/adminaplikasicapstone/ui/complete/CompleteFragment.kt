package com.example.adminaplikasicapstone.ui.complete

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.adminaplikasicapstone.R
import com.example.adminaplikasicapstone.models.DisasterCaseDataModels
import com.example.adminaplikasicapstone.ui.DetailLaporanActivity
import com.example.adminaplikasicapstone.utils.ConvertTime
import com.example.adminaplikasicapstone.utils.adapter.ListDisasterCaseAdapter
import com.example.adminaplikasicapstone.utils.firebasestorage.FirebaseStorageServices
import com.example.adminaplikasicapstone.utils.firestore.FirestoreObject
import com.example.adminaplikasicapstone.utils.firestore.FirestoreServices
import com.google.firebase.storage.StorageException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CompleteFragment : Fragment() {

    private lateinit var completeViewModel: CompleteViewModel

    private lateinit var recycleViewLayout:RecyclerView
    private lateinit var loadingBar:ProgressBar
    private lateinit var textIsDisasterCaseDataIsEmpty:TextView

    private var listImageReportByAdmin:ArrayList<String> = ArrayList<String>()

    private var listDisasterCaseData:ArrayList<DisasterCaseDataModels> = ArrayList<DisasterCaseDataModels>()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        completeViewModel =
                ViewModelProvider(this).get(CompleteViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_complete, container, false)
//        val textView: TextView = root.findViewById(R.id.text_notifications)
//        completeViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializationIdLayout(view)
        getAllDisasterCaseDataCompleted()
        setRecycleViewLayout()
    }

    private fun setRecycleViewLayout(){
        recycleViewLayout.layoutManager = LinearLayoutManager(this.context)
        var adapter = ListDisasterCaseAdapter(listDisasterCaseData)
        recycleViewLayout.adapter = adapter

        adapter.setOnItemClickCallback(object : ListDisasterCaseAdapter.OnItemClickCallback{
            override fun onItemClicked(data: DisasterCaseDataModels) {
                var disasterData:DisasterCaseDataModels = data
                val intent = Intent(this@CompleteFragment.context, DetailLaporanSelesai::class.java)
                intent.putExtra(DetailLaporanActivity.DISASTER_CASE_DATA, disasterData)
                intent.putStringArrayListExtra(DetailLaporanSelesai.LIST_IMAGE_REPORT_BY_ADMIN, listImageReportByAdmin)
                startActivity(intent)
            }

        })
    }

    private fun getAllDisasterCaseDataCompleted(){
        var firestoreServices = FirestoreServices()
        var getQuery = firestoreServices.DisasterCaseDataComplete().gerAllDisasterCaseDataComplete()
        listDisasterCaseData.clear()
        getQuery.addOnCompleteListener {
            GlobalScope.launch(Dispatchers.IO){
                if (it.isSuccessful){
                    for (document in it.result!!){
                        var firebaseStorageServices = FirebaseStorageServices()
                        var disasterCaseDataModels = DisasterCaseDataModels()
                        disasterCaseDataModels.disasterCaseID = document[FirestoreObject.COL_DISASTER_CASE_ID].toString()
                        disasterCaseDataModels.reportByEmail = document[FirestoreObject.COL_DISASTER_REPORT_BY_EMAIL].toString()
                        disasterCaseDataModels.disasterLocation = document[FirestoreObject.COL_DISASTER_LOCATION].toString()
                        disasterCaseDataModels.disasterLatitude = document[FirestoreObject.COL_DISASTER_LATITUDE].toString()
                        disasterCaseDataModels.disasterLongitude = document[FirestoreObject.COL_DISASTER_LONGITUDE].toString()
                        disasterCaseDataModels.disasterCaseStatus = document[FirestoreObject.COL_DISASTER_CASE_STATUS].toString()
                        listImageReportByAdmin = document[FirestoreObject.COL_LIST_IMAGE_REPORT_BY_ADMIN] as ArrayList<String>
                        try {
                            var urlImage = firebaseStorageServices.DisasterCaseData().getImageURLByName(document[FirestoreObject.COL_DISASTER_CASE_IMAGE].toString()).await()
                            disasterCaseDataModels.disasterCaseDataPhoto = urlImage.toString()
                        }catch (e: StorageException){
                            println("NGELEMPAR ERROR BUKANNYA EXCEPTION, DATA DI FIREBASE STORAGE GA ADA")
                        }
                        disasterCaseDataModels.disasterDateTime = document[FirestoreObject.COL_DISASTER_CASE_DATE].toString()
                        disasterCaseDataModels.disasterCaseDetail = document[FirestoreObject.COL_DISASTER_CASE_DETAIL].toString()
                        disasterCaseDataModels.disasterCaseDetailByAdmin = document[FirestoreObject.COL_DETAIL_BY_ADMIN].toString()
                        disasterCaseDataModels.reportByPhoneNumber = document[FirestoreObject.COL_DISASTER_REPORT_BY_PHONE_NUMBER].toString()
                        listDisasterCaseData.add(disasterCaseDataModels)
                    }
                    withContext(Dispatchers.Main){
                        if (listDisasterCaseData.isEmpty()){
                            textIsDisasterCaseDataIsEmpty.visibility = View.VISIBLE
                        }
                        loadingBar.visibility = View.INVISIBLE
                        setRecycleViewLayout()
                    }
                }
            }
        }.addOnFailureListener {
            Toast.makeText(this.context, "${it.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun initializationIdLayout(view: View) {
        recycleViewLayout = view.findViewById(R.id.fragmentComplete_recycleviewlayout)
        textIsDisasterCaseDataIsEmpty = view.findViewById(R.id.fragmentComplete_isDisasterCaseIsEmpty)
        loadingBar = view.findViewById(R.id.fragmentComplete_loadingBar)

        loadingBar.visibility = View.VISIBLE
        textIsDisasterCaseDataIsEmpty.visibility = View.INVISIBLE
    }
}