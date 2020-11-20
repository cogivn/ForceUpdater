package com.legatotechnologies.updater;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
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
            @Nullable final View view,
            final Version version,
            final long hours,
            final int type,
            final boolean isDisabledButtonActions,
            final OnForceUpdateActionCallback okButtonCallback,
            final OnOptionalDialogDismissListener listener) {

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context, themeRes)
                .setView(view)
                .setCancelable(false);

        if (view == null) {
            builder.setTitle(version.getLanguage().getTitle());
            builder.setMessage(version.getMessage());

            if (!isDisabledButtonActions) {
                builder.setPositiveButton(version.getLanguage().getPos_btn(), (d, id) ->
                        forceProcessPositiveButtonAction(context, version, okButtonCallback));
                builder.setNegativeButton(version.getLanguage().getNeg_btn(), (d, id) ->
                        forceProcessNegativeButtonAction(context, version, listener));
                builder.setNeutralButton(version.getLanguage().getNeutral_btn(), (d, id) ->
                        forceProcessNeutralButtonAction(context, version, hours, type, listener));
            }
        } else {
            final TextView tvTitle = view.findViewById(R.id.tv_title_dialog);
            final TextView tvContent = view.findViewById(R.id.tv_content_dialog);
            final Button btnOk = view.findViewById(R.id.btn_ok_dialog);
            final Button btnCancel = view.findViewById(R.id.btn_skip_dialog);
            final Button btnNeutral = view.findViewById(R.id.btn_update_later_dialog);

            if (tvTitle != null) tvTitle.setText(version.getLanguage().getTitle());
            if (tvContent != null) tvContent.setText(version.getMessage());

            if (btnOk != null) {
                btnOk.setOnClickListener(ok -> {
                    forceProcessPositiveButtonAction(context, version, okButtonCallback);
                });
            } else if (!isDisabledButtonActions) {
                builder.setPositiveButton(version.getLanguage().getPos_btn(), (d, id) ->
                        forceProcessPositiveButtonAction(context, version, okButtonCallback));
            }

            if (btnCancel != null) {
                btnCancel.setOnClickListener(cancel -> {
                    forceProcessNegativeButtonAction(context, version, listener);
                });
            } else if (!isDisabledButtonActions) {
                builder.setNegativeButton(version.getLanguage().getNeg_btn(), (d, id) ->
                        forceProcessNegativeButtonAction(context, version, listener));
            }

            if (btnNeutral != null) {
                btnNeutral.setOnClickListener(neutral -> {
                    forceProcessNeutralButtonAction(context, version, hours, type, listener);
                });
            } else if (!isDisabledButtonActions) {
                builder.setNeutralButton(version.getLanguage().getNeutral_btn(), (d, id) ->
                        forceProcessNeutralButtonAction(context, version, hours, type, listener));
            }
        }
        builder.setOnDismissListener(dialog -> {
            if (view == null) return;
            ViewGroup vg = (ViewGroup) view.getParent();
            if (vg != null) vg.removeView(view);
        });
        return builder.create();
    }

    static AlertDialog setForceUpdateDialog(
            final Context context,
            final int themeRes,
            final Version version,
            @Nullable final View view,
            final boolean isOverrideButtonActions,
            OnForceUpdateActionCallback okButtonCallback
    ) {
        final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context, themeRes)
                .setCancelable(false)
                .setView(view);

        if (view == null) {
            builder.setTitle(version.getLanguage().getTitle());
            builder.setMessage(version.getMessage());

            if (!isOverrideButtonActions) {
                builder.setPositiveButton(version.getLanguage().getPos_btn(), (d, id) ->
                        forceProcessPositiveButtonAction(context, version, okButtonCallback));
            }
        } else {
            final TextView tvTitle = view.findViewById(R.id.tv_title_dialog);
            final TextView tvContent = view.findViewById(R.id.tv_content_dialog);
            if (tvTitle != null) tvTitle.setText(version.getLanguage().getTitle());
            if (tvContent != null) tvContent.setText(version.getMessage());

            final Button btnOk = view.findViewById(R.id.btn_ok_dialog);

            if (btnOk != null) {
                btnOk.setOnClickListener(ok -> {
                    forceProcessPositiveButtonAction(context, version, okButtonCallback);
                });
            } else if (!isOverrideButtonActions) {
                builder.setPositiveButton(version.getLanguage().getPos_btn(), (d, id) ->
                        forceProcessPositiveButtonAction(context, version, okButtonCallback));
            }
        }
        builder.setOnDismissListener(dialog -> {
            if (view == null) return;
            ViewGroup vg = (ViewGroup) view.getParent();
            if (vg != null) vg.removeView(view);
        });
        return builder.create();
    }

    public interface OnOptionalDialogDismissListener {
        void onOptionalDialogDismiss();
    }

    interface OnForceUpdateActionCallback {
        void onOkButtonCalled();
    }

    public static void forceProcessPositiveButtonAction(
            Context context,
            Version version,
            OnForceUpdateActionCallback callback) {
        if (version.getUrl() != null) {
            Utils.goToUpdate(context, version.getUrl());
            callback.onOkButtonCalled();
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
