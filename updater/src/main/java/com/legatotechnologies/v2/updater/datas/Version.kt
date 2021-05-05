package com.legatotechnologies.v2.updater.datas

import android.os.Parcelable
import com.google.gson.Gson
import com.legatotechnologies.v2.updater.datas.enums.UpdateType
import kotlinx.parcelize.Parcelize

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