package com.legatotechnologies.updater;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.net.URL;
import java.util.Locale;

/**
 * Created by davidng on 6/21/17.
 */

public class Utils {
    private static final String ERROR_MESSAGE_URL = "The link is invalid";

    static String getAppInstalledVersion(Context context) {
        String version = "0.0.0.0";

        try {
            version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    static String getLocaleCountry() {
        Locale locale = Locale.getDefault();
        return locale.getCountry();
    }

    static boolean compareVersion(String currentVersion, String newestVersion) {
        if (currentVersion != null
                && currentVersion.length() > 0
                && newestVersion != null
                && newestVersion.length() > 0) {
            String[] current = currentVersion.split("\\.");
            String[] newest = newestVersion.split("\\.");
            if (newest.length < current.length) return false;
            if (newest.length == current.length) {
                boolean result = false;
                for (int i = 0; i < newest.length; i++) {
                    int currV = parse(current[i]);
                    int newV = parse(newest[i]);
                    if (currV > newV) break; // for case current = 1.0.0 and newest = 0.0.1
                    else if (newV > currV) {
                        result = true;
                        break;
                    }
                }
                return result;
            }
        }
        return false;
    }

    private static int parse(String num) {
        try {
            return Integer.parseInt(num);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    static boolean isValidUrl(String link) {
        if (TextUtils.isEmpty(link)) return false;
        if (!link.trim().startsWith("https://") && !link.trim().startsWith("http://")) {
            link = "https://" + link;
        }
        return Patterns.WEB_URL.matcher(link).matches();
    }

    static void showErrorMessage(Context context) {
        Toast.makeText(context, ERROR_MESSAGE_URL, Toast.LENGTH_SHORT).show();

    }

    static void setPreferenceForLaterUpdate(Context context, boolean updateLater, long millis) {
        UpdatePreference updatePreference = new UpdatePreference(context);
        updatePreference.setUpdateLater(updateLater);
        updatePreference.setLaterNotificationTime(millis);
    }

    static void setPreferenceForSkip(Context context, String version) {
        UpdatePreference updatePreference = new UpdatePreference(context);
        updatePreference.setSkipVersion(version);
    }

    static void setPreferenceLastVersion(Context context, String version) {
        UpdatePreference updatePreference = new UpdatePreference(context);
        updatePreference.setLastVersion(version);
    }

    static void goToUpdate(Context context, URL url) {
        Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url.toString()));
        context.startActivity(intent);
    }

    static void setButtonColor(AlertDialog dialog) {
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#FF4081"));
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.parseColor("#FF4081"));
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#FF4081"));
    }

    static String addStartString(String link) {
        if (!link.trim().startsWith("https://") && !link.trim().startsWith("http://")) {
            link = "http://" + link;
        }
        return link;
    }

    //    static boolean checkValidVersion(String version) {
//        if (version == null) return false;
//        if (!version.matches("[0-9]+(\\.[0-9]+)*")) return false;
//
//        return true;
//    }

//    static boolean compareVersion(String currentVersion, String newestVersion) {
//        String[] current = currentVersion.split("\\.");
//        String[] newest = newestVersion.split("\\.");
//        int length = Math.max(current.length, newest.length);
//        for (int i = 0; i < length; i++) {
//            int currentPart = i < current.length ? Integer.parseInt(current[i]) : 0;
//            int newtPart = i < newest.length ? Integer.parseInt(newest[i]) : 0;
//            if (currentPart != newtPart ) return false;
//        }
//        return true;
//    }
}
