package com.legatotechnologies.updater;

import android.content.Context;


import androidx.appcompat.app.AlertDialog;

import com.legatotechnologies.updater.models.Version;

import org.json.JSONException;
import org.json.JSONObject;
/**
 * Created by davidng on 6/21/17.
 */

public class ForceUpdate implements IForceUpdate{

    public static final int Milli = 0;
    public static final int Minute = 1;
    public static final int Hour = 2;
    public static final int Day = 3;

    private static final String TAG = "ForceUpdate";
    private static final int DEFAULT_NOTIFICATION_TIME = 1000 * 60 * 60 * 24;
    private static final int DEFAULT_TYPE = 0;
    private static final String DEFAULT_MESSAGE = "New Update Available";
    private static final String DEFAULT_URL = "";
    private static final int DEFAULT_UPDATE_TYPE = 0;

    private Context mContext;
    private UpdatePreference mUpdatePreference;
    private JSONObject mJsonObject;
    private String mJsonString;
    private Version mVersion;
    private AlertDialog mAlertDialog;
    private UtilsDialog.OnOptionalDialogDismissListener mListener;
    private Language mLanguage;
    private long mMillisecond;
    private int mType;

    public ForceUpdate(Context context) {
        mContext = context;
        mUpdatePreference = new UpdatePreference(context);
        mJsonObject = null;
        mJsonString = null;
        mVersion = null;
        mLanguage = Language.Eng;
        mMillisecond = DEFAULT_NOTIFICATION_TIME;
        mType = DEFAULT_TYPE;
    }

    @Override
    public ForceUpdate setJSON(JSONObject jsonObject) {
        mJsonObject = jsonObject;
        return this;
    }

    @Override
    public ForceUpdate setJSONString(String jsonString) {
        mJsonString = jsonString;
        return this;
    }

    @Override
    public ForceUpdate setNotificationTime(long number, int type) {
        mMillisecond = number;
        mType = type;
        return this;
    }

    @Override
    public ForceUpdate setNotificationTime(long millisecond) {
        mMillisecond = millisecond;
        mType = Milli;
        return this;
    }

    @Override
    public ForceUpdate setLang(Language lang) {
        mLanguage = lang;
        return this;
    }

    @Override
    public ForceUpdate setOptionalListener(UtilsDialog.OnOptionalDialogDismissListener listener) {
        mListener = listener;
        return this;
    }

    @Override
    public void start(){
        if (mJsonObject != null) { //parse JSONObject
            mJsonObject = ParseObject.getVersionObject(mJsonObject);
        } else if (mJsonString != null) { // parse JsonString
            mJsonObject = ParseObject.getVersionObject(mJsonString);
        } else {
            return;
        }

        parseObject();

    }

    private void parseObject() throws illegalUrlException {
        if (mJsonObject != null) {
            if (!Utils.isValidUrl(mJsonObject.optString("dl_link").trim())){
                throw new illegalUrlException();
            }

            // second @para set default value;
            mVersion = new Version(mJsonObject.optString("version", Utils.getAppInstalledVersion(mContext)),
                    mJsonObject.optString("popup_message", DEFAULT_MESSAGE),
                    ParseObject.stringToURL(Utils.addStartString(mJsonObject.optString("dl_link"))),
                    ParseObject.findUpdateType(mJsonObject.optInt("force_update", DEFAULT_UPDATE_TYPE)),
                    mLanguage);//Utils.getLocaleCountry()
            init();
        }
    }

    private void init() {
        if (!Utils.compareVersion(Utils.getAppInstalledVersion(mContext), mVersion.getLatestVersion())) {
            return;
        }

        if (mVersion.getUpdateType() == Version.UpdateType.FORCED) {
            initForceDialog();
            Utils.setPreferenceForSkip(mContext, null); // clear previous skipPreference
            return;
        }

        if (mUpdatePreference.getSkipVersion() != null) {
            if (isSkipPreference()) {
                return;
            } else {
                Utils.setPreferenceForSkip(mContext, null);
                initOptionDialog();
                return;
            }
        }

        if (isLaterPreference() && !isVersionNoChange()) {

        } else {
            initOptionDialog();
        }
    }

    private boolean isSkipPreference() {
        return !Utils.compareVersion(mUpdatePreference.getSkipVersion(), mVersion.getLatestVersion());
    }

    private boolean isVersionNoChange(){
        return Utils.compareVersion(mUpdatePreference.getLastVersion(), mVersion.getLatestVersion());
    }

    private boolean isLaterPreference() {
        if (mUpdatePreference.isUpdateLater() && UtilsTime.getSystemCurrentTime() < mUpdatePreference.getLaterNotificationTime()) {
            //Log.i(TAG, "Remainder Me Later is true but the time is not up");
            return true;
        } else if (mUpdatePreference.isUpdateLater() && UtilsTime.getSystemCurrentTime() >= mUpdatePreference.getLaterNotificationTime()) {
//            Log.i(TAG, "Time is up, the remind me is shown");
            Utils.setPreferenceForLaterUpdate(mContext, false, 0L); // set the default preference
            return false;
        }
            //Log.i(TAG, "isLater preference is false");
        return false;
    }

    private void initForceDialog() {
        mAlertDialog = UtilsDialog.setForceUpdateDialog(mContext, mVersion);
        mAlertDialog.show();
        Utils.setButtonColor(mAlertDialog);
    }

    private void initOptionDialog() {
        mAlertDialog = UtilsDialog.setOptionalUpdateDialog(mContext, mVersion, mMillisecond, mType, mListener);
        mAlertDialog.show();
        Utils.setButtonColor(mAlertDialog);
    }

    public static JSONObject initUpdateJSon(String link, String version, String message,
                                            int forceUpdate) {
        JSONObject dataObject = new JSONObject();
        try {
            JSONObject mobileVersionData = new JSONObject();
            mobileVersionData.put("dl_link", link);
            mobileVersionData.put("version", version);
            mobileVersionData.put("popup_message", message);
            mobileVersionData.put("force_update", forceUpdate);
            JSONObject mobileObject = new JSONObject();
            mobileObject.put("mobile_version", mobileVersionData);
            dataObject.put("data", mobileObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dataObject;
    }
}
