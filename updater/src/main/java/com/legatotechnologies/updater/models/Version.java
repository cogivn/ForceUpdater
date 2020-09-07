package com.legatotechnologies.updater.models;


import com.legatotechnologies.updater.Language;

import java.net.URL;

/**
 * Created by davidng on 6/21/17.
 */

public final class Version {
    private final String latestVersion;
    private final String message;
    private final URL url;
    private final UpdateType updateType;
    private final Language language;

    public enum UpdateType {
        FORCED, OPTIONAL
    }

    public Version(String latestVersion, String message, URL url, UpdateType updateType, Language language) {
        this.latestVersion = latestVersion;
        this.message = message;
        this.url = url;
        this.updateType = updateType;
        this.language = language;
    }

    public UpdateType getUpdateType() {
        return updateType;
    }

    public URL getUrl() {
        return url;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public String getMessage() {
        return message;
    }

    public Language getLanguage() {
        return language;
    }

    @Override
    public String toString() {
        return " latestVersion: " + getLatestVersion() +
                " Msg: " + getMessage()  +
                " Type: " + getUpdateType().toString() +
                " Lang: " +getLanguage().toString();
    }
}
