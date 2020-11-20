package com.legatotechnologies.v2.updater

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.legatotechnologies.v2.updater.preference.SharePrefUtil

class UpdatePreference internal constructor(context: Context) {
    private val preferences: SharePrefUtil = SharePrefUtil.with(context)
        .setName("force_update_prefs")
        .setSecure(false)
        .ok()

    var isUpdateLater: Boolean
        get() = preferences.readBoolean(PREF_UPDATE_LATER, false)
        set(value) = preferences.saveBoolean(PREF_UPDATE_LATER, value)

    var laterNotificationTime: Long
        get() = preferences.readLong(PREF_NOTIFICATION_TIME, 0L)
        set(value) = preferences.saveLong(PREF_NOTIFICATION_TIME, value)

    var skipVersion: String
        get() = preferences.readString(PREF_SKIP_VERSION, "")
        set(value) = preferences.saveString(PREF_SKIP_VERSION, value)

    var lastVersion: String
        get() = preferences.readString(PREF_LAST_VERSION, "")
        set(version) = preferences.saveString(PREF_LAST_VERSION, version)

    companion object {
        private const val PREF_NOTIFICATION_TIME = "later_notification_time"
        private const val PREF_UPDATE_LATER = "update_later"
        private const val PREF_SKIP_VERSION = "skip_version"
        private const val PREF_LAST_VERSION = "last_version"
    }
}