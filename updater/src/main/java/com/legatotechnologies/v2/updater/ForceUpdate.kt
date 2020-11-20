package com.legatotechnologies.v2.updater

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.annotation.DimenRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.legatotechnologies.updater.R
import com.legatotechnologies.updater.UtilsTime
import com.legatotechnologies.v2.updater.datas.Version
import com.legatotechnologies.v2.updater.datas.enums.UpdateType
import com.legatotechnologies.v2.updater.ui.ForceUpdateDialogFragment
import com.legatotechnologies.v2.updater.ui.ForceUpdateDialogFragment.Arguments
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug
import org.jetbrains.anko.info
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class ForceUpdate private constructor(
    private val context: Context
) : IForceUpdate, AnkoLogger {
    private val mVersion: Version = Version(
        latestVersion = ForceUpdateFlow.getAppInstalledVersion(context)
    )
    private var mLocale: Locale = Locale.ENGLISH
    private var mThemeResourceId: Int = R.style.Theme_ForceUpdateAlertDialog
    private var mNotificationTime: Long = DEFAULT_NOTIFICATION_TIME
    private var mCustomView: View? = null
    private val mIShowing = AtomicBoolean(false)
    private var mDismissDialogListener: OnOptionalDialogDismissListener? = null
    private var mDialog: ForceUpdateDialogFragment? = null

    private val mEventObserver = LifecycleEventObserver { _, event ->
        when (event) {
            Lifecycle.Event.ON_DESTROY -> {
                mIShowing.set(false)
            }
            else -> Unit
        }
    }

    init {
        val args = Bundle()
        mDialog = ForceUpdateDialogFragment.getInstance(args)
    }

    override fun setLifecycle(owner: LifecycleOwner): IForceUpdate {
        owner.lifecycle.removeObserver(mEventObserver)
        owner.lifecycle.addObserver(mEventObserver)
        return this
    }

    override fun setNotificationTime(millisecond: Long): IForceUpdate {
        mNotificationTime = millisecond
        return this
    }

    override fun setUrl(url: String): IForceUpdate {
        mVersion.url = url
        return this
    }

    override fun setVersion(version: String): IForceUpdate {
        mVersion.latestVersion = version
        return this
    }

    override fun setContent(content: String): IForceUpdate {
        mVersion.content = content
        return this
    }

    override fun setForceUpdate(isForceUpdate: Boolean): IForceUpdate {
        mVersion.type = if (isForceUpdate) UpdateType.FORCE else UpdateType.OPTIONAL
        return this
    }

    override fun setRounded(@DimenRes radius: Int): IForceUpdate {
        mDialog?.setRadius(radius)
        return this
    }

    override fun setLocale(locale: Locale): IForceUpdate {
        mLocale = locale
        return this
    }

    override fun setTheme(style: Int): IForceUpdate {
        mThemeResourceId = style
        return this
    }

    override fun setCustomView(view: View): IForceUpdate {
        mCustomView = view
        return this
    }

    override fun setCustomView(@LayoutRes resId: Int): IForceUpdate {
        mDialog?.setContentView(resId)
        return this
    }

    override fun setOptionalListener(listener: OnOptionalDialogDismissListener?): IForceUpdate {
        mDismissDialogListener = listener
        return this
    }

    override fun start(manager: FragmentManager) {
        val lastedVersion = ForceUpdateFlow.getAppInstalledVersion(context)
        val isVersionNotSame = ForceUpdateFlow.compareVersion(lastedVersion, mVersion.latestVersion)
        if (!isVersionNotSame) return
        if (!mIShowing.getAndSet(true)) {
            val dialog = mDialog ?: return
            dialog.arguments = Bundle().apply {
                putParcelable(
                    ForceUpdateDialogFragment.CONTENT,
                    Arguments(
                        version = mVersion,
                        notificationSkipTime = mNotificationTime
                    )
                )
            }
            dialog.setOnDismissListener {
                mDismissDialogListener?.onOptionalDialogDismiss()
                mIShowing.set(false)
            }

            val shouldBeShowPopup = when {
                this.mVersion.type == UpdateType.FORCE -> {
                    ForceUpdateFlow.setPreferenceForSkip(context, null)
                    true
                }
                hasSkipVersion() && !isSkipPreference() -> {
                    ForceUpdateFlow.setPreferenceForSkip(context, null)
                    true
                }
                !isLaterPreference() || isVersionNoChange() -> true
                else -> false
            }
            if (!shouldBeShowPopup) return

            dialog.setStyle(DialogFragment.STYLE_NO_TITLE, mThemeResourceId)
            if (!dialog.isAdded) {
                dialog.show(manager, FRAGMENT_TAG)
            }
        } else debug("Dialog is showing! We will ignore this action.")
    }

    private fun isSkipPreference(): Boolean {
        val prefs = UpdatePreference(context)
        return !ForceUpdateFlow.compareVersion(prefs.skipVersion, mVersion.latestVersion)
    }

    private fun hasSkipVersion(): Boolean {
        val prefs = UpdatePreference(context)
        return prefs.skipVersion.isNotEmpty()
    }

    private fun isVersionNoChange(): Boolean {
        val prefs = UpdatePreference(context)
        return ForceUpdateFlow.compareVersion(prefs.lastVersion, mVersion.latestVersion)
    }

    private fun isLaterPreference(): Boolean {
        val prefs = UpdatePreference(context)
        if (prefs.isUpdateLater && UtilsTime.getSystemCurrentTime() < prefs.laterNotificationTime) {
            info("Remainder Me Later is true but the time is not up")
            return true
        } else if (prefs.isUpdateLater
            && System.currentTimeMillis() >= prefs.laterNotificationTime
        ) {
            // set the default preference
            ForceUpdateFlow.setPreferenceForLaterUpdate(
                context,
                false,
                0L
            )
            return false
        }
        info("isLater preference is false")
        return false
    }

    companion object {
        const val FRAGMENT_TAG = "force_update_dialog"
        val DEFAULT_NOTIFICATION_TIME = TimeUnit.MINUTES.toMillis(5)

        @Volatile
        private var sInstance: ForceUpdate? = null

        @JvmStatic
        fun with(context: Context): ForceUpdate = sInstance ?: synchronized(this) {
            sInstance ?: ForceUpdate(context).also { sInstance = it }
        }
    }
}