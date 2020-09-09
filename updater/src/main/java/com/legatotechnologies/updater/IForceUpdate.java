package com.legatotechnologies.updater;

import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;

import org.json.JSONObject;

/**
 * Created by davidng on 6/21/17.
 */

interface IForceUpdate {
    ForceUpdate setNotificationTime(long number, int type);

    ForceUpdate setNotificationTime(long millisecond);

    ForceUpdate setJSON(JSONObject jsonObject);

    ForceUpdate setJSONString(String jsonString);

    ForceUpdate setTheme(@StyleRes int style);

    ForceUpdate setLang(Language lang);

    ForceUpdate setCustomView(@NonNull View view);

    ForceUpdate setCustomView(@LayoutRes int resId);

    ForceUpdate setShouldHideButtons(boolean disabled);

    ForceUpdate setShouldHideTitle(boolean disabled);

    ForceUpdate setOptionalListener(UtilsDialog.OnOptionalDialogDismissListener listener);

    ForceUpdate start();
}
