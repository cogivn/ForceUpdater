package com.legatotechnologies.updater;

import org.json.JSONObject;

/**
 * Created by davidng on 6/21/17.
 */

interface IForceUpdate {
    ForceUpdate setNotificationTime(long number, int type);

    ForceUpdate setNotificationTime(long millisecond);

    ForceUpdate setJSON(JSONObject jsonObject);

    ForceUpdate setJSONString(String jsonString);

    ForceUpdate setLang(Language lang);

    ForceUpdate setOptionalListener(UtilsDialog.OnOptionalDialogDismissListener listener);

    void start();
}
