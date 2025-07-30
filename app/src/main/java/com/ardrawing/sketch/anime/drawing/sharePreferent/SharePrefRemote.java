package com.ardrawing.sketch.anime.drawing.sharePreferent;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.amazic.ads.util.AdsConsentManager;

public class SharePrefRemote {
    public static String ads_visibility = "ads_visibility";
    public static String inter_splash = "inter_splash";
    public static String native_language = "native_language";
    public static String banner_home = "banner_home";
    public static String native_intro = "native_intro";
    public static String inter_intro = "inter_intro";
    public static String appopen_resume = "appopen_resume";
    public static String inter_home = "inter_home";
    public static String inter_category = "inter_category";
    public static String inter_draw = "inter_draw";
    public static String native_category = "native_category";
    public static String banner_draw = "banner_draw";
    public static String native_tutorial = "native_tutorial";
    public static String interval_between_interstitial = "interval_between_interstitial";
    public static String native_permission = "native_permission";

    public static boolean get_config(Context context, String name_config) {
        SharedPreferences pre = context.getSharedPreferences("remote_fill", Context.MODE_PRIVATE);
        if (name_config.equals("style_screen"))
            return pre.getBoolean(name_config, false);
        else
            return pre.getBoolean(name_config, true) && AdsConsentManager.getConsentResult(context) && haveNetworkConnection(context);
    }

    public static void set_config(Context context, String name_config, boolean config) {
        SharedPreferences pre = context.getSharedPreferences("remote_fill", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putBoolean(name_config, config);
        editor.apply();
    }


    public static String get_config_string(Context context, String name_config) {
        SharedPreferences pre = context.getSharedPreferences("remote_fill", Context.MODE_PRIVATE);
        return pre.getString(name_config, "");
    }

    public static void set_config_string(Context context, String name_config, String config) {
        SharedPreferences pre = context.getSharedPreferences("remote_fill", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putString(name_config, config);
        editor.apply();
    }

    public static Long get_config_long(Context context, String name_config) {
        SharedPreferences pre = context.getSharedPreferences("remote_fill", Context.MODE_PRIVATE);
        return pre.getLong(name_config, 0);
    }

    public static void set_config_long(Context context, String name_config, Long config) {
        SharedPreferences pre = context.getSharedPreferences("remote_fill", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putLong(name_config, config);
        editor.apply();
    }

    public static boolean haveNetworkConnection(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected()) haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected()) haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}