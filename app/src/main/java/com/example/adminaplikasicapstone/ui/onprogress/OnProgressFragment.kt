package com.example.adminaplikasicapstone.ui.onprogress

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.adminaplikasicapstone.R
import com.example.adminaplikasicapstone.models.DisasterCaseDataModels
import com.example.adminaplikasicapstone.utils.adapter.ListDisasterCaseAdapter
import com.example.adminaplikasicapstone.utils.ConvertTime
import com.example.adminaplikasicapstone.utils.firestore.FirestoreObject
import com.example.adminaplikasicapstone.utils.firestore.FirestoreServices

class OnProgressFragment : Fragment() {

    private lateinit var onProgressViewModel: OnProgressViewModel

    private lateinit var recycleviewlayout:RecyclerView

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
    }

    private fun getAllDisasterCaseOnProgressData(){
        var firestoreServices = FirestoreServices()
        var getQuery = firestoreServices.DisasterCaseData().getAllDisasterCaseDataByStatus(status = "onProgress")
        listDisasterCaseData.clear()
        getQuery.addOnCompleteListener {
            if (it.isSuccessful){
//                loadingProgressBar.visibility = View.INVISIBLE
                for (document in it.result!!) {
                    if (document[FirestoreObject.COL_DISASTER_CASE_STATUS].toString()=="onProgress"){
                        var disasterCaseDataModels = DisasterCaseDataModels()
                        disasterCaseDataModels.reportByEmail = document[FirestoreObject.COL_DISASTER_REPORT_BY_EMAIL].toString()
                        disasterCaseDataModels.disasterLocation = document[FirestoreObject.COL_DISASTER_LOCATION].toString()
                        disasterCaseDataModels.disasterLatitude = document[FirestoreObject.COL_DISASTER_LATITUDE].toString()
                        disasterCaseDataModels.disasterLongitude = document[FirestoreObject.COL_DISASTER_LONGITUDE].toString()
                        disasterCaseDataModels.disasterCaseStatus = document[FirestoreObject.COL_DISASTER_CASE_STATUS].toString()
                        disasterCaseDataModels.disasterDateTime = ConvertTime.getTimeByTimeStamp(document[FirestoreObject.COL_DISASTER_CASE_DATE].toString().toLong()).toString()
                        listDisasterCaseData.add(disasterCaseDataModels)
                    }
                }
                setRecycleViewLayout()
            }
        }.addOnFailureListener {
//            loadingProgressBar.visibility = View.VISIBLE
        }
    }

    private fun initializationIdLayout(view: View) {
        recycleviewlayout = view.findViewById(R.id.fragmentOnProgress_recycleviewlayout)
    }
}