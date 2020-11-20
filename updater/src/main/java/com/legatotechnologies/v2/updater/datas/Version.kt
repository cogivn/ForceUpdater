package com.legatotechnologies.v2.updater.datas

import android.content.Context
import android.os.Parcelable
import com.google.gson.Gson
import com.legatotechnologies.v2.updater.ForceUpdateFlow
import com.legatotechnologies.v2.updater.datas.enums.UpdateType
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Version(
    var latestVersion: String = "",
    var url: String = "",
    var content: String = "",
    var type: UpdateType = UpdateType.OPTIONAL
) : Parcelable {
    override fun toString(): String {
        return "Version=${Gson().toJson(this)}"
    }
}