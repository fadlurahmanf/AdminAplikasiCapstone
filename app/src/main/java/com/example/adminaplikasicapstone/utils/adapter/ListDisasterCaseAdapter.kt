package com.example.adminaplikasicapstone.utils.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.adminaplikasicapstone.R
import com.example.adminaplikasicapstone.models.DisasterCaseDataModels

class ListDisasterCaseAdapter(var listDisasterCase: ArrayList<DisasterCaseDataModels>):RecyclerView.Adapter<ListDisasterCaseAdapter.ListViewHolder>() {
    private lateinit var onItemClickCallback:OnItemClickCallback

    interface OnItemClickCallback {
        fun onItemClicked(data:DisasterCaseDataModels)
    }

    fun setOnItemClickCallback(onItemClickCallback:OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    inner class ListViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
        var reportByEmail:TextView = itemView.findViewById(R.id.item_disasterReportBy)
        var disasterCasePhoto:ImageView = itemView.findViewById(R.id.item_image_disasterCase)
        var disasterCaseStatusText:TextView = itemView.findViewById(R.id.item_disaster_case_status)
        var disasterCaseLocation:TextView = itemView.findViewById(R.id.item_disaster_location)
        var disasterCaseDateTime:TextView = itemView.findViewById(R.id.item_disastercase_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_disaster_case, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listDisasterCase.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var disasterCaseData = listDisasterCase[position]

        Glide.with(holder.disasterCasePhoto).load(disasterCaseData.disasterCaseDataPhoto).into(holder.disasterCasePhoto)
        holder.disasterCaseStatusText.text = disasterCaseData.disasterCaseStatus.toString()
        holder.reportByEmail.text = disasterCaseData.reportByEmail.toString()
        holder.disasterCaseLocation.text = disasterCaseData.disasterLocation.toString()
        holder.disasterCaseDateTime.text = disasterCaseData.disasterDateTime.toString()
        if (disasterCaseData.disasterCaseStatus=="waiting"){
            holder.disasterCaseStatusText.setTextColor(Color.rgb(128,0,0))
        }else if (disasterCaseData.disasterCaseStatus=="onProgress"){
            holder.disasterCaseStatusText.setTextColor(Color.rgb(52,204,255))
        }else if (disasterCaseData.disasterCaseStatus=="complete"){
            holder.disasterCaseStatusText.setTextColor(Color.rgb(50,205,50))
        }

        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(listDisasterCase[holder.adapterPosition])
        }
    }
}