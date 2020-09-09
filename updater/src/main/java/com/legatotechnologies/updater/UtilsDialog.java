package com.legatotechnologies.updater;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.legatotechnologies.updater.models.Version;

/**
 * Created by davidng on 6/21/17.
 */

public class UtilsDialog {
    private static final boolean isLaterUpdate = true;


    static AlertDialog setOptionalUpdateDialog(
            final Context context,
            final int themeRes,
            final View view,
            final Version version,
            final long hours,
            final int type,
            final boolean isDisabledButtonActions,
            final boolean isHideDialogTitle,
            final OnOptionalDialogDismissListener listener) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context, themeRes)
                .setView(view)
                .setCancelable(false);
        if (!isHideDialogTitle) {
            builder.setTitle(version.getLanguage().getTitle());
            builder.setMessage(version.getMessage());
        }
        if (!isDisabledButtonActions) {
            builder.setPositiveButton(version.getLanguage().getPos_btn(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface d, int id) {
                    forceProcessPositiveButtonAction(context, version);
                }
            });
            builder.setNegativeButton(version.getLanguage().getNeg_btn(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface d, int id) {
                    forceProcessNegativeButtonAction(context, version, listener);
                }
            });
            builder.setNeutralButton(version.getLanguage().getNeutral_btn(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface d, int id) {
                    forceProcessNeutralButtonAction(context, version, hours, type, listener);
                }
            });
        }
        return builder.create();

    }

    static AlertDialog setForceUpdateDialog(
            final Context context,
            final int themeRes,
            final Version version,
            final View view,
            final boolean isOverrideButtonActions,
            final boolean isHideDialogTitle
    ) {
        final AlertDialog dialog = new MaterialAlertDialogBuilder(context, themeRes)
                .setCancelable(false)
                .setView(view)
                .setPositiveButton(version.getLanguage().getPos_btn(), null)
                .create();
        if (!isHideDialogTitle) {
            dialog.setTitle(version.getLanguage().getTitle());
            dialog.setMessage(version.getMessage());
        }
        if (!isOverrideButtonActions) {
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    Button button = (dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            forceProcessPositiveButtonAction(context, version);
                        }
                    });
                }
            });
        }
        return dialog;
    }

    public interface OnOptionalDialogDismissListener {
        void onOptionalDialogDismiss();
    }

    public static void forceProcessPositiveButtonAction(Context context, Version version) {
        if (version.getUrl() != null) {
            Utils.goToUpdate(context, version.getUrl());
        } else {
            Utils.showErrorMessage(context);
        }
    }

    public static void forceProcessNegativeButtonAction(
            Context context,
            Version version,
            OnOptionalDialogDismissListener listener) {
        Utils.setPreferenceForSkip(context, version.getLatestVersion());
        if (listener != null) {
            listener.onOptionalDialogDismiss();
        }
    }

    public static void forceProcessNeutralButtonAction(
            Context context,
            Version version,
            long milliseconds,
            int type,
            OnOptionalDialogDismissListener listener) {
        long notificationTime = UtilsTime.calculateNotificationTime(milliseconds, type);
        Utils.setPreferenceLastVersion(context, version.getLatestVersion());
        Utils.setPreferenceForLaterUpdate(context, isLaterUpdate, notificationTime);
        if (listener != null) {
            listener.onOptionalDialogDismiss();
        }
    }
}
