package com.legatotechnologies.updater;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import com.legatotechnologies.updater.models.Version;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by davidng on 6/21/17.
 */

public class ForceUpdate implements IForceUpdate {

    public static final int Milli = 0;
    public static final int Minute = 1;
    public static final int Hour = 2;
    public static final int Day = 3;
    private static final String TAG = ForceUpdate.class.getSimpleName();
    private static final int DEFAULT_NOTIFICATION_TIME = 1000 * 60 * 60 * 24;
    private static final int DEFAULT_TYPE = 0;
    private static final String DEFAULT_MESSAGE = "New Update Available";
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
    private View mCustomView;
    private int mCustomThemeRes;
    private UpdaterLifecycleObserver mLifecycleObserver;
    private boolean isShouldBeHideButtons = false;
    private boolean isAcceptToReOpenDialog = false;

    public ForceUpdate(Context context, LifecycleOwner owner) {
        mContext = context;
        mLifecycleObserver = new UpdaterLifecycleObserver(owner);
        owner.getLifecycle().addObserver(mLifecycleObserver);
        mUpdatePreference = new UpdatePreference(context);
        mJsonObject = null;
        mJsonString = null;
        mVersion = null;
        mLanguage = Language.Eng;
        mMillisecond = DEFAULT_NOTIFICATION_TIME;
        mType = DEFAULT_TYPE;
        mCustomThemeRes = R.style.Theme_ForceUpdateAlertDialog;
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
    public ForceUpdate setCustomView(@NonNull View view) {
        this.mCustomView = view;
        return this;
    }

    @Override
    public ForceUpdate setCustomView(int resId) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        this.mCustomView = inflater.inflate(resId, null);
        return this;
    }

    @Override
    public ForceUpdate setShouldHideButtons(boolean disabled) {
        this.isShouldBeHideButtons = disabled;
        return this;
    }

    @Override
    public ForceUpdate setTheme(int style) {
        mCustomThemeRes = style;
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

    public Version getVersion() {
        return this.mVersion;
    }

    public long getMilliseconds() {
        return this.mMillisecond;
    }

    public boolean isDialogShowing() {
        return mAlertDialog != null && mAlertDialog.isShowing();
    }

    @Override
    public ForceUpdate start() {
        if (mJsonObject != null) { //parse JSONObject
            mJsonObject = ParseObject.getVersionObject(mJsonObject);
        } else if (mJsonString != null) { // parse JsonString
            mJsonObject = ParseObject.getVersionObject(mJsonString);
        }
        mLifecycleObserver.mUpdater = this;
        launch();
        return this;
    }

    void launch() {
        if (mJsonObject != null) {
            if (!Utils.isValidUrl(mJsonObject.optString("dl_link").trim())) {
                throw new illegalUrlException();
            }

            // second @para set default value;
            mVersion = new Version(mJsonObject.optString("version", Utils.getAppInstalledVersion(mContext)),
                    mJsonObject.optString("popup_message", DEFAULT_MESSAGE),
                    ParseObject.stringToURL(Utils.addStartString(mJsonObject.optString("dl_link"))),
                    ParseObject.findUpdateType(mJsonObject.optInt("force_update", DEFAULT_UPDATE_TYPE)),
                    mLanguage);//Utils.getLocaleCountry()
            init();
            Log.v(TAG, "launch func called");
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
            if (!isSkipPreference()) {
                Utils.setPreferenceForSkip(mContext, null);
                initOptionDialog();
            }
            return;
        }

        if (!isLaterPreference() || isVersionNoChange()) {
            initOptionDialog();
        }
    }

    private boolean isSkipPreference() {
        return !Utils.compareVersion(mUpdatePreference.getSkipVersion(), mVersion.getLatestVersion());
    }

    private boolean isVersionNoChange() {
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
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            Log.v(TAG, "ForceUpdate dialog showing. Ignore start action.");
            return;
        }
        UtilsDialog.OnForceUpdateActionCallback callback = () -> {
            isAcceptToReOpenDialog = true;
        };
        mAlertDialog = UtilsDialog.setForceUpdateDialog(
                mContext,
                mCustomThemeRes,
                mVersion,
                mCustomView,
                isShouldBeHideButtons,
                callback
        );
        mAlertDialog.show();
        Utils.setButtonColor(mAlertDialog);
    }

    private void initOptionDialog() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            Log.v(TAG, "ForceUpdate dialog showing. Ignore start action.");
            return;
        }
        boolean isChangingConfigurations = mLifecycleObserver.getConfigurationChanged();
        boolean isAlertDialogShowing = mLifecycleObserver.isAlertDialogShowing();
        if (isChangingConfigurations && !isAlertDialogShowing) {
            return;
        }
        UtilsDialog.OnForceUpdateActionCallback callback = () -> {
            isAcceptToReOpenDialog = true;
        };
        mAlertDialog = UtilsDialog.setOptionalUpdateDialog(
                mContext,
                mCustomThemeRes,
                mCustomView,
                mVersion,
                mMillisecond,
                mType,
                isShouldBeHideButtons,
                callback,
                mListener);
        mAlertDialog.show();
        Utils.setButtonColor(mAlertDialog);
    }

    private static final class UpdaterLifecycleObserver implements androidx.lifecycle.LifecycleObserver {
        private ForceUpdate mUpdater;
        private LifecycleOwner mOwner;

        private static final String CONFIGURATION_CHANGED = "configurations_changed";
        private static final String ALERT_DIALOG_SHOWING = "is_dialog_showing";

        private UpdaterLifecycleObserver(LifecycleOwner owner) {
            this.mOwner = owner;
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        void onResumed() {
            try {
                Log.v(TAG, "LifecycleOwner resumed");
                if (mUpdater == null) return;
                if (mUpdater.isAcceptToReOpenDialog) {
                    mUpdater.isAcceptToReOpenDialog = false;
                    mUpdater.launch();
                    Log.v(TAG, "relaunch at resume state.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        void onStopped() {
            boolean isDialogShowing = mUpdater.isDialogShowing();
            Activity activity = getActivity();
            if (activity != null) {
                boolean isChangingConfigurations = activity.isChangingConfigurations();
                activity.getIntent().putExtra(ALERT_DIALOG_SHOWING, isDialogShowing);
                activity.getIntent().putExtra(CONFIGURATION_CHANGED, isChangingConfigurations);
                if (isChangingConfigurations && isDialogShowing) {
                    // prevent memory leak when user change configuration.
                    mUpdater.mAlertDialog.dismiss();
                }
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        void onDestroyed() {
            Activity activity = getActivity();
            if (activity != null) {
                boolean isChangingConfigurations = activity.isChangingConfigurations();
                activity.getIntent().putExtra(CONFIGURATION_CHANGED, isChangingConfigurations);
            }
            Log.v(TAG, "LifecycleOwner destroyed");
            mOwner.getLifecycle().removeObserver(this);
            mUpdater = null;
        }

        @Nullable
        private Activity getActivity() {
            Activity activity = null;
            if (mOwner instanceof Activity) activity = (Activity) mOwner;
            else if (mOwner instanceof Fragment) {
                Fragment fragment = (Fragment) mOwner;
                activity = fragment.getActivity();
            }
            return activity;
        }

        private boolean getConfigurationChanged() {
            Activity activity = getActivity();
            if (activity != null) {
                return activity.getIntent().getBooleanExtra(
                        CONFIGURATION_CHANGED,
                        activity.isChangingConfigurations());
            }
            return false;
        }

        private boolean isAlertDialogShowing() {
            Activity activity = getActivity();
            if (activity != null) {
                return activity.getIntent().getBooleanExtra(
                        ALERT_DIALOG_SHOWING, false);
            }
            return false;
        }
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
