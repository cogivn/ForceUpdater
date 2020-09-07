package com.legatotechnologies.updater;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
/**
 * Created by davidng on 6/21/17.
 */

public class UpdatePreference {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final String PREF_NOTIFICATION_TIME = "laterNotificationTime";
    private static final String PREF_UPDATE_LATER = "updateLater";
    private static final String PREF_SKIP_VERSION = "skipVersion";
    private static final String PREF_LAST_VERSION = "lastVersion";

    UpdatePreference(Context context) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.editor = sharedPreferences.edit();
    }

    boolean isUpdateLater () {
        return sharedPreferences.getBoolean(PREF_UPDATE_LATER, false);
    }

    void setUpdateLater(boolean value) {
        editor.putBoolean(PREF_UPDATE_LATER, value)
                .commit();
    }

    long getLaterNotificationTime() {
        return sharedPreferences.getLong(PREF_NOTIFICATION_TIME, 0L);
    }

    void setLaterNotificationTime(long value) {
        editor.putLong(PREF_NOTIFICATION_TIME, value)
                .commit();
    }

    String getSkipVersion() {
        return sharedPreferences.getString(PREF_SKIP_VERSION, null);
    }

    void setSkipVersion(String value) {
        editor.putString(PREF_SKIP_VERSION, value)
                .commit();
    }

    String getLastVersion(){
        return sharedPreferences.getString(PREF_LAST_VERSION, null);
    }

    void setLastVersion(String version){
        editor.putString(PREF_LAST_VERSION, version)
                .commit();
    }
}
