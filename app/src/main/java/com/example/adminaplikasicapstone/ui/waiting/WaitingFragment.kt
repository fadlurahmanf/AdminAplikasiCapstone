package com.example.adminaplikasicapstone.ui.waiting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.adminaplikasicapstone.R
import com.example.adminaplikasicapstone.models.DisasterCaseDataModels
import com.example.adminaplikasicapstone.utils.ConvertTime
import com.example.adminaplikasicapstone.utils.adapter.ListDisasterCaseAdapter
import com.example.adminaplikasicapstone.utils.firebasestorage.FirebaseStorageServices
import com.example.adminaplikasicapstone.utils.firestore.FirestoreObject.COL_DISASTER_CASE_DATE
import com.example.adminaplikasicapstone.utils.firestore.FirestoreObject.COL_DISASTER_CASE_IMAGE
import com.example.adminaplikasicapstone.utils.firestore.FirestoreObject.COL_DISASTER_CASE_STATUS
import com.example.adminaplikasicapstone.utils.firestore.FirestoreObject.COL_DISASTER_LATITUDE
import com.example.adminaplikasicapstone.utils.firestore.FirestoreObject.COL_DISASTER_LOCATION
import com.example.adminaplikasicapstone.utils.firestore.FirestoreObject.COL_DISASTER_LONGITUDE
import com.example.adminaplikasicapstone.utils.firestore.FirestoreObject.COL_DISASTER_REPORT_BY_EMAIL
import com.example.adminaplikasicapstone.utils.firestore.FirestoreServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class WaitingFragment : Fragment(), View.OnClickListener {

    private lateinit var waitingViewModel: WaitingViewModel
    private lateinit var loadingProgressBar:ProgressBar
    private lateinit var recycleViewLayout:RecyclerView

    private lateinit var coba:Button


    private var listDisasterCaseData:ArrayList<DisasterCaseDataModels> = ArrayList<DisasterCaseDataModels>()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        waitingViewModel =
                ViewModelProvider(this).get(WaitingViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_waiting, container, false)
//        val textView: TextView = root.findViewById(R.id.text_home)
//        waitingViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializationIdLayout(view)
        getAllDisasterCaseData()

    }

    private var disasterModel = DisasterCaseDataModels()

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.coba->{
                getAllDisasterCaseData()
            }
        }
    }

    private fun setRecycleView() {
        recycleViewLayout.layoutManager = LinearLayoutManager(this.context)
        var adapter = ListDisasterCaseAdapter(listDisasterCaseData)
        recycleViewLayout.adapter = adapter
    }

    private fun getAllDisasterCaseData(){
        var firestoreServices = FirestoreServices()
        var getQuery = firestoreServices.DisasterCaseData().getAllDisasterCaseDataByStatus(status = "waiting")
        listDisasterCaseData.clear()
        getQuery.addOnCompleteListener {
            GlobalScope.launch(Dispatchers.IO) {
                if (it.isSuccessful){
                    loadingProgressBar.visibility = View.INVISIBLE
                    for (document in it.result!!) {
                        if (document[COL_DISASTER_CASE_STATUS].toString()=="waiting"){
                            var firebaseStorageServices = FirebaseStorageServices()
                            var disasterCaseDataModels = DisasterCaseDataModels()
                            disasterCaseDataModels.reportByEmail = document[COL_DISASTER_REPORT_BY_EMAIL].toString()
                            disasterCaseDataModels.disasterLocation = document[COL_DISASTER_LOCATION].toString()
                            disasterCaseDataModels.disasterLatitude = document[COL_DISASTER_LATITUDE].toString()
                            disasterCaseDataModels.disasterLongitude = document[COL_DISASTER_LONGITUDE].toString()
                            disasterCaseDataModels.disasterCaseStatus = document[COL_DISASTER_CASE_STATUS].toString()
                            var urlImage = firebaseStorageServices.DisasterCaseData().getImageURLByName(document[COL_DISASTER_CASE_IMAGE].toString()).await()
                            disasterCaseDataModels.disasterCaseDataPhoto = urlImage.toString()
                            disasterCaseDataModels.disasterDateTime = ConvertTime.getTimeByTimeStamp(document[COL_DISASTER_CASE_DATE].toString().toLong()).toString()
                            listDisasterCaseData.add(disasterCaseDataModels)
                        }
                    }
                    withContext(Dispatchers.Main){
                        setRecycleView()
                    }
                }
            }
        }.addOnFailureListener {
                loadingProgressBar.visibility = View.VISIBLE
            }
    }

    private fun initializationIdLayout(view: View) {
        recycleViewLayout = view.findViewById(R.id.waitingfragment_recycleviewlayout)
        loadingProgressBar = view.findViewById(R.id.waitingfragment_loadingbar)
        coba = view.findViewById(R.id.coba)

        coba.setOnClickListener(this)
    }
}