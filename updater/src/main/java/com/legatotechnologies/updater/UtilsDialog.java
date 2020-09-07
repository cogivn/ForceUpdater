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
            final Version version,
            final long numHours,
            final int type,
            final OnOptionalDialogDismissListener listener) {
        return new MaterialAlertDialogBuilder(context, R.style.AlertDialogCustom)
                .setTitle(version.getLanguage().getTitle())
                .setMessage(version.getMessage())
                .setCancelable(false)
                .setPositiveButton(version.getLanguage().getPos_btn(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int id) {
                        if (version.getUrl() != null) {
                            Utils.goToUpdate(context, version.getUrl());
                        } else {
                            Utils.showErrorMessage(context);
                        }
                    }
                })
                .setNegativeButton(version.getLanguage().getNeg_btn(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int id) {
                        Utils.setPreferenceForSkip(context, version.getLatestVersion());
                        if (listener != null) {
                            listener.onOptionalDialogDismiss();
                        }
                    }
                })
                .setNeutralButton(version.getLanguage().getNeutral_btn(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int id) {
                        Utils.setPreferenceLastVersion(context, version.getLatestVersion());
                        Utils.setPreferenceForLaterUpdate(context, isLaterUpdate, UtilsTime.calculateNotificationTime(numHours, type));
                        if (listener != null) {
                            listener.onOptionalDialogDismiss();
                        }
                    }
                }).create();

    }

    static AlertDialog setForceUpdateDialog(final Context context, final Version version) {
        final AlertDialog dialog = new MaterialAlertDialogBuilder(context, R.style.AlertDialogCustom)
                .setTitle(version.getLanguage().getTitle())
                .setMessage(version.getMessage())
                .setCancelable(false)
                .setPositiveButton(version.getLanguage().getPos_btn(), null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = (dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (version.getUrl() != null) {
                            Utils.goToUpdate(context, version.getUrl());
                        } else {
                            Utils.showErrorMessage(context);
                        }
                    }
                });
            }
        });
        return dialog;
    }

    public interface OnOptionalDialogDismissListener {
        void onOptionalDialogDismiss();

    }
}
