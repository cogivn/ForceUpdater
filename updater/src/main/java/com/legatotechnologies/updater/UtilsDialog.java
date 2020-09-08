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
            final OnOptionalDialogDismissListener listener) {
        return new MaterialAlertDialogBuilder(context, themeRes)
                .setTitle(version.getLanguage().getTitle())
                .setMessage(version.getMessage())
                .setView(view)
                .setCancelable(false)
                .setPositiveButton(version.getLanguage().getPos_btn(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int id) {
                        if (isDisabledButtonActions) return;
                        forceProcessPositiveButtonAction(context, version);
                    }
                })
                .setNegativeButton(version.getLanguage().getNeg_btn(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int id) {
                        if (isDisabledButtonActions) return;
                        forceProcessNegativeButtonAction(context, version, listener);
                    }
                })
                .setNeutralButton(version.getLanguage().getNeutral_btn(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int id) {
                        if (isDisabledButtonActions) return;
                        forceProcessNeutralButtonAction(context, version, hours, type, listener);
                    }
                }).create();

    }

    static AlertDialog setForceUpdateDialog(
            final Context context,
            final int themeRes,
            final Version version,
            final View view,
            final boolean isOverrideButtonActions
    ) {
        final AlertDialog dialog = new MaterialAlertDialogBuilder(context, themeRes)
                .setTitle(version.getLanguage().getTitle())
                .setMessage(version.getMessage())
                .setCancelable(false)
                .setView(view)
                .setPositiveButton(version.getLanguage().getPos_btn(), null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = (dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isOverrideButtonActions) return;
                        forceProcessPositiveButtonAction(context, version);
                    }
                });
            }
        });
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
            long hours,
            int type,
            OnOptionalDialogDismissListener listener) {
        long notificationTime = UtilsTime.calculateNotificationTime(hours, type);
        Utils.setPreferenceLastVersion(context, version.getLatestVersion());
        Utils.setPreferenceForLaterUpdate(context, isLaterUpdate, notificationTime);
        if (listener != null) {
            listener.onOptionalDialogDismiss();
        }
    }
}
