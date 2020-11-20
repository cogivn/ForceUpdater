package com.legatotechnologies.v2.updater.preference

import android.content.Context

data class Config(
    var preferenceName: String,
    var preferenceMode: Int = Context.MODE_PRIVATE,
    var isSecure: Boolean = false
)