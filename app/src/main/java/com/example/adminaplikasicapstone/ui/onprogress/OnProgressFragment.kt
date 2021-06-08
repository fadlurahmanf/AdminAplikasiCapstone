package com.example.adminaplikasicapstone.ui.onprogress

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.adminaplikasicapstone.R
import com.example.adminaplikasicapstone.models.DisasterCaseDataModels
import com.example.adminaplikasicapstone.ui.DetailLaporanActivity
import com.example.adminaplikasicapstone.utils.adapter.ListDisasterCaseAdapter
import com.example.adminaplikasicapstone.utils.ConvertTime
import com.example.adminaplikasicapstone.utils.firebasestorage.FirebaseStorageServices
import com.example.adminaplikasicapstone.utils.firestore.FirestoreObject
import com.example.adminaplikasicapstone.utils.firestore.FirestoreServices
import com.google.firebase.storage.StorageException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class OnProgressFragment : Fragment() {

    private lateinit var onProgressViewModel: OnProgressViewModel

    private lateinit var recycleviewlayout:RecyclerView
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var textIsDisasterCaseDataIsEmpty:TextView

    private var listDisasterCaseData:ArrayList<DisasterCaseDataModels> = ArrayList<DisasterCaseDataModels>()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        onProgressViewModel =
                ViewModelProvider(this).get(OnProgressViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_onprogress, container, false)
//        val textView: TextView = root.findViewById(R.id.text_dashboard)
//        onProgressViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializationIdLayout(view)
        getAllDisasterCaseOnProgressData()

        setRecycleViewLayout()

    }

    private fun setRecycleViewLayout() {
        recycleviewlayout.layoutManager = LinearLayoutManager(this.context)
        var adapter = ListDisasterCaseAdapter(listDisasterCaseData)
        recycleviewlayout.adapter = adapter

        adapter.setOnItemClickCallback(object : ListDisasterCaseAdapter.OnItemClickCallback{
            override fun onItemClicked(data: DisasterCaseDataModels) {
                val disasterData:DisasterCaseDataModels = data
                var intent = Intent(this@OnProgressFragment.context, DetailLaporanActivity::class.java)
                intent.putExtra(DetailLaporanActivity.DISASTER_CASE_DATA, disasterData)
                startActivity(intent)
            }
        })

    }

    private fun getAllDisasterCaseOnProgressData(){
        var firestoreServices = FirestoreServices()
        var getQuery = firestoreServices.DisasterCaseData().getAllDisasterCaseDataByStatus(status = "onProgress")
        listDisasterCaseData.clear()
        getQuery.addOnCompleteListener {
            GlobalScope.launch(Dispatchers.IO) {
                if (it.isSuccessful){
                    for (document in it.result!!) {
                        if (document[FirestoreObject.COL_DISASTER_CASE_STATUS].toString()=="onProgress"){
                            var firebaseStorageServices = FirebaseStorageServices()
                            var disasterCaseDataModels = DisasterCaseDataModels()
                            disasterCaseDataModels.disasterCaseID = document[FirestoreObject.COL_DISASTER_CASE_ID].toString()
                            disasterCaseDataModels.disasterType = document[FirestoreObject.COL_DISASTER_TYPE].toString()
                            disasterCaseDataModels.reportByEmail = document[FirestoreObject.COL_DISASTER_REPORT_BY_EMAIL].toString()
                            disasterCaseDataModels.disasterLocation = document[FirestoreObject.COL_DISASTER_LOCATION].toString()
                            disasterCaseDataModels.disasterLatitude = document[FirestoreObject.COL_DISASTER_LATITUDE].toString()
                            disasterCaseDataModels.disasterLongitude = document[FirestoreObject.COL_DISASTER_LONGITUDE].toString()
                            disasterCaseDataModels.disasterCaseStatus = document[FirestoreObject.COL_DISASTER_CASE_STATUS].toString()
                            try {
                                var urlImage = firebaseStorageServices.DisasterCaseData().getImageURLByName(document[FirestoreObject.COL_DISASTER_CASE_IMAGE].toString()).await()
                                disasterCaseDataModels.disasterCaseDataPhoto = urlImage.toString()
                            }catch (e: StorageException){
                                println("NGELEMPAR ERROR BUKANNYA EXCEPTION, DATA DI FIREBASE STORAGE GA ADA")
                            }
                            disasterCaseDataModels.disasterDateTime = ConvertTime.getTimeByTimeStamp(document[FirestoreObject.COL_DISASTER_CASE_DATE].toString().toLong()).toString()
                            disasterCaseDataModels.disasterCaseDetail = document[FirestoreObject.COL_DISASTER_CASE_DETAIL].toString()
                            disasterCaseDataModels.reportByPhoneNumber = document[FirestoreObject.COL_DISASTER_REPORT_BY_PHONE_NUMBER].toString()
                            listDisasterCaseData.add(disasterCaseDataModels)
                        }
                    }
                    withContext(Dispatchers.Main){
                        if (listDisasterCaseData.isEmpty()){
                            textIsDisasterCaseDataIsEmpty.visibility = View.VISIBLE
                        }
                        loadingProgressBar.visibility = View.INVISIBLE
                        setRecycleViewLayout()
                    }
                }
            }
        }.addOnFailureListener {
            loadingProgressBar.visibility = View.VISIBLE
        }
    }

    private fun initializationIdLayout(view: View) {
        recycleviewlayout = view.findViewById(R.id.fragmentOnProgress_recycleviewlayout)
        loadingProgressBar = view.findViewById(R.id.fragmentOnProgress_loadingBar)
        textIsDisasterCaseDataIsEmpty = view.findViewById(R.id.fragmentOnProgress_isDisasterCaseIsEmpty)

        textIsDisasterCaseDataIsEmpty.visibility = View.INVISIBLE
    }
}