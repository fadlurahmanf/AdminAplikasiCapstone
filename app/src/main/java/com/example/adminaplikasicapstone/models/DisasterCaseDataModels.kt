package com.example.adminaplikasicapstone.models

import android.os.Parcel
import android.os.Parcelable

data class DisasterCaseDataModels(
        var disasterType:String?="",
        var reportByEmail:String?="",
        var disasterCaseDataPhoto:String?="",
        var disasterLocation:String?="",
        var disasterLatitude:String?="",
        var disasterLongitude:String?="",
        var disasterCaseStatus:String?="",
        var disasterDateTime:String?=""
):Parcelable {
        constructor(parcel: Parcel) : this(
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString()
        ) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeString(disasterType)
                parcel.writeString(reportByEmail)
                parcel.writeString(disasterCaseDataPhoto)
                parcel.writeString(disasterLocation)
                parcel.writeString(disasterLatitude)
                parcel.writeString(disasterLongitude)
                parcel.writeString(disasterCaseStatus)
                parcel.writeString(disasterDateTime)
        }

        override fun describeContents(): Int {
                return 0
        }

        companion object CREATOR : Parcelable.Creator<DisasterCaseDataModels> {
                override fun createFromParcel(parcel: Parcel): DisasterCaseDataModels {
                        return DisasterCaseDataModels(parcel)
                }

                override fun newArray(size: Int): Array<DisasterCaseDataModels?> {
                        return arrayOfNulls(size)
                }
        }
}