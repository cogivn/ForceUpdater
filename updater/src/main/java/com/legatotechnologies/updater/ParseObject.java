package com.legatotechnologies.updater;


import com.legatotechnologies.updater.models.Version;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by davidng on 6/21/17.
 */

public class ParseObject {
    private static final int FORCED_UPDATE = 1;
    static JSONObject getVersionObject(String value) {
        JSONObject obj = null;
        try {
            obj = new JSONObject(value).getJSONObject("data").getJSONObject("mobile_version");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    static JSONObject getVersionObject(JSONObject obj) {
        JSONObject version = null;
        try {
            version = obj.getJSONObject("data").getJSONObject("mobile_version");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return version;
    }

    static URL stringToURL(String link) {
        URL url = null;
        try {
            url = new URL(link);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    static Version.UpdateType findUpdateType(int value) {
        return value == FORCED_UPDATE? Version.UpdateType.FORCED : Version.UpdateType.OPTIONAL;
    }

//    static Language getLanguage(String laType) {
//        switch (laType) {
//            case "TW":
//                return Language.Chinese_Trad;
//            case "CN":
//                return Language.Chinese_Simp;
//            default:
//                return Language.Eng;
//        }
//    }
}
