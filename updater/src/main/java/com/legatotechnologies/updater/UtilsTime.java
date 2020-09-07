package com.legatotechnologies.updater;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by davidng on 6/21/17.
 */

public class UtilsTime {
    private static final long ONE_HOUR = 3600000L;
    private static final long ONE_DAY = 86400000L;
    private static final long ONE_MIN = 60000L;

    public static long calculateNotificationTime(long num, int type){
        long currentTime = getSystemCurrentTime();
        long result = 0;
        switch (type){
            case ForceUpdate.Minute:
                result = currentTime + (num * ONE_MIN);
                break;
            case ForceUpdate.Hour:
                result = currentTime +  (num * ONE_HOUR);
                break;
            case ForceUpdate.Day:
                result = currentTime + (num * ONE_DAY);
                break;
            case ForceUpdate.Milli:
                result = num;
                break;
            default:
                result = num;
                break;
        }
        return result;
    }

    // Return current time in milli
    public static long getSystemCurrentTime() {
        return System.currentTimeMillis() ;
    }


    //Ignore this method
    public static String getTodayDayandTime(long millis) {
        return (new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())).format(new Date(millis));
    }
}
