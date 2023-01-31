package com.tea.counter.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Preference {

    public static final String NAME = "name";
    public static final String USER_TYPE = "userType";
    public static final String MOBILE_NO = "mobileNo";
    public static final String CITY = "city";
    public static final String ADDRESS = "address";
    public static final String IMG_URI = "img_url";
    public static final String FCM_TOKEN = "fcmToken";
    private static final String PREF_FILE = "teaApp";
    private static final String IS_LOGIN = "isLogin";

    public static boolean getIsLogin(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(IS_LOGIN, false);
    }

    public static void setIsLogin(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsPrivateEditor = sharedPreferences.edit();
        prefsPrivateEditor.putBoolean(IS_LOGIN, true);
        prefsPrivateEditor.apply();
    }

    // TODO NAME
    public static String getName(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getString(NAME, "");
    }

    public static void setName(Context context, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsPrivateEditor = sharedPreferences.edit();
        prefsPrivateEditor.putString(NAME, value);
        prefsPrivateEditor.apply();
    }

    // TODO FCM TOKEN
    public static String getFcmToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getString(FCM_TOKEN, "");
    }

    public static void setFcmToken(Context context, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsPrivateEditor = sharedPreferences.edit();
        prefsPrivateEditor.putString(FCM_TOKEN, value);
        prefsPrivateEditor.apply();
    }

    //TODO USER TYPE
    public static String getUserType(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getString(USER_TYPE, "");
    }

    public static void setUserType(Context context, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsPrivateEditor = sharedPreferences.edit();
        prefsPrivateEditor.putString(USER_TYPE, value);
        prefsPrivateEditor.apply();
    }

    // TODO MOBILE NUMBER
    public static String getMobileNo(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getString(MOBILE_NO, "");
    }

    public static void setMobileNo(Context context, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsPrivateEditor = sharedPreferences.edit();
        prefsPrivateEditor.putString(MOBILE_NO, value);
        prefsPrivateEditor.apply();
    }

    // TODO CITY

    public static String getCity(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getString(CITY, "");
    }

    public static void setCity(Context context, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsPrivateEditor = sharedPreferences.edit();
        prefsPrivateEditor.putString(CITY, value);
        prefsPrivateEditor.apply();
    }

    // TODO ADDRESS
    public static String getAddress(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getString(ADDRESS, "");
    }

    public static void setAddress(Context context, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsPrivateEditor = sharedPreferences.edit();
        prefsPrivateEditor.putString(ADDRESS, value);
        prefsPrivateEditor.apply();
    }

    // TODO IMAGE URL
    public static String getImgUri(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getString(IMG_URI, "");
    }

    public static void setImgUri(Context context, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsPrivateEditor = sharedPreferences.edit();
        prefsPrivateEditor.putString(IMG_URI, value);
        prefsPrivateEditor.apply();
    }


    public static void clearAllPref(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsPrivateEditor = sharedPreferences.edit();
        prefsPrivateEditor.clear();
        prefsPrivateEditor.apply();
    }

}
