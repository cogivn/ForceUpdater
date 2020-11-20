package com.legatotechnologies.v2.updater

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import java.util.*

object ForceUpdateFlow {
    private const val ERROR_MESSAGE_URL = "The link is invalid"

    private fun isValidUrl(link: String): Boolean {
        var newLink = link
        if (TextUtils.isEmpty(newLink)) return false
        if (!newLink.trim { it <= ' ' }.startsWith("https://") && !newLink.trim { it <= ' ' }
                .startsWith("http://")) {
            newLink = "https://$newLink"
        }
        return Patterns.WEB_URL.matcher(newLink).matches()
    }

    fun getAppInstalledVersion(context: Context): String {
        var version = "0.0.0.0"
        try {
            version = context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return version
    }

    fun compareVersion(currentVersion: String, newestVersion: String): Boolean {
        if (currentVersion.isNotEmpty() && newestVersion.isNotEmpty()) {
            val current = currentVersion.split(".").map { catching(it) }
            val newest = newestVersion.split(".").map { catching(it) }
            return when {
                newest.size < current.size -> return false
                newest.size == current.size -> {
                    var result = false
                    newest.forEachIndexed { index, data ->
                        if (data > current[index]) {
                            result = true
                            return@forEachIndexed
                        }
                    }
                    return result
                }
                else -> false
            }
        }
        return false
    }

    private fun catching(toInt: String) = try {
        toInt.toInt()
    } catch (ex: Exception) {
        0
    }

    fun setPreferenceForLaterUpdate(context: Context, updateLater: Boolean, millis: Long) {
        val updatePreference = UpdatePreference(context)
        updatePreference.isUpdateLater = updateLater
        updatePreference.laterNotificationTime = System.currentTimeMillis() + millis
    }

    fun setPreferenceForSkip(context: Context, version: String?) {
        val updatePreference = UpdatePreference(context)
        updatePreference.skipVersion = version ?: ""
    }

    fun setPreferenceLastVersion(context: Context, version: String?) {
        val updatePreference = UpdatePreference(context)
        updatePreference.lastVersion = version ?: ""
    }

    fun goToUpdate(context: Context, url: String) {
        if (!isValidUrl(url)) return
        val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(url))
        context.startActivity(intent)
    }

    fun showErrorMessage(context: Context?) {
        Toast.makeText(context, ERROR_MESSAGE_URL, Toast.LENGTH_SHORT).show()
    }
}