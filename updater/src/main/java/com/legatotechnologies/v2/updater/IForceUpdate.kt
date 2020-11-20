package com.legatotechnologies.v2.updater

import android.view.View
import androidx.annotation.DimenRes
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import com.legatotechnologies.v2.updater.datas.enums.UpdateType
import java.util.*

interface IForceUpdate {
    fun setLifecycle(owner: LifecycleOwner): IForceUpdate
    fun setNotificationTime(millisecond: Long): IForceUpdate
    fun setUrl(url: String): IForceUpdate
    fun setVersion(version: String): IForceUpdate
    fun setContent(content: String): IForceUpdate
    fun setRounded(@DimenRes radius: Int): IForceUpdate
    fun setForceUpdate(isForceUpdate: Boolean): IForceUpdate
    fun setLocale(locale: Locale): IForceUpdate
    fun setTheme(@StyleRes style: Int): IForceUpdate
    fun setCustomView(view: View): IForceUpdate
    fun setCustomView(@LayoutRes resId: Int): IForceUpdate
    fun setOptionalListener(listener: OnOptionalDialogDismissListener?): IForceUpdate
    fun start(manager: FragmentManager)
}

fun interface OnOptionalDialogDismissListener {
    fun onOptionalDialogDismiss()
}