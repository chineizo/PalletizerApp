package m1app.com.albertsons.palletizerandroid.utility;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import m1app.com.albertsons.palletizerandroid.config.Config;
import m1app.com.albertsons.palletizerandroid.pojo.LineCode;
import m1app.com.albertsons.palletizerandroid.pojo.Setting;
import m1app.com.albertsons.palletizerandroid.pojo.User;


public class Prefs {

    private static String ENVIRONMENT = "environment";
    private static String LINE = "line";
//    private static String LINE_CODE = "line_code";
    private static String COMPANY_CODE = "company_code";
    private static String FACILITY_CODE = "facility_code";
    private static String USER = "user";
    private static String SETTING = "setting";
    private final String preferenceKey = "m1_prefs";
    private SharedPreferences sharedPreferences;

    public Prefs(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferenceKey, Context.MODE_PRIVATE);
        this.sharedPreferences = sharedPreferences;
    }

    private SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }


    public void setSetting (@NonNull Setting setting) {
        Gson gson = new Gson();
        String serialized = gson.toJson(setting);
        getSharedPreferences()
                .edit()
                .putString(SETTING, serialized)
                .commit();
    }

    public Setting getSetting () {
        String setting = getSharedPreferences().getString(SETTING, null);
        if (setting != null) {
            Gson gson = new Gson();
            return gson.fromJson(setting, Setting.class);
        }
        Setting config = new Setting();
        config.setCompanyNumber("100");
        config.setFacilityCode("273");
        return config;
    }

    public User getUser() {
        String user = getSharedPreferences().getString(USER, null);
        if (user != null) {
            Gson gson = new Gson();
            return gson.fromJson(user, User.class);
        }
        return new User();
    }

    public void setUser(@NonNull User user) {
        Gson gson = new Gson ();
        String serialized = gson.toJson(user);
        getSharedPreferences()
                .edit()
                .putString(USER, serialized)
                .commit();
    }

    public String getEnvironment() {
        if (Utils.isDebuggable()) {
            return getSharedPreferences().getString(ENVIRONMENT, Config.Environment.DEV);
        }
        return getSharedPreferences().getString(ENVIRONMENT, Config.Environment.PROD);
    }

    public void setEnvironment(@NonNull String environment) {
        getSharedPreferences()
                .edit()
                .putString(ENVIRONMENT, environment)
                .commit();
    }

    public void setUserName (String userName) {
        getSharedPreferences().edit().putString("USERNAME", userName);
    }

    public String getUserName () {
        return getSharedPreferences().getString("USERNAME",  "INFORBC\\M3API");
    }


    public void setPassword (@NonNull String password) {
        getSharedPreferences().edit().putString("PASSWORD", password).commit();
    }


    public String getPassword () {
        return getSharedPreferences().getString("PASSWORD", "P@ssw0rd123!@#");
    }

    public void setSettingsPassword (@NonNull String password) {
        getSharedPreferences().edit().putString("SETTINGS_PASSWORD", password);
    }

    public String getSettingsPassword () {
        return getSharedPreferences().getString("SETTINGS_PASSWORD", "2018");
    }

    public void settingAdminPassword (@NonNull String password) {
        getSharedPreferences().edit().putString("ADMIN_SETTINGS_PASSWORD", password);
    }

    public String getAdminPassword() {
        return getSharedPreferences().getString("ADMIN_SETTINGS_PASSWORD", "dev2018");
    }

//
//    public String getNumberSeriesType() {
//        return getSharedPreferences().getString(NUMBER_SERIES_TYPE, Config.NumberSeriesType.TA);
//    }
//
//    public void setNumberSeriesType(@NonNull String seriesType) {
//        getSharedPreferences()
//                .edit()
//                .putString(NUMBER_SERIES_TYPE, seriesType)
//                .commit();
//    }

    public String getLine() {
        return getSharedPreferences().getString(LINE, Config.Line.A);
    }

    public void setLine(@NonNull String line) {
        getSharedPreferences()
                .edit()
                .putString(LINE, line)
                .commit();
    }

    public LineCode getLineCode (String key) {
        String lineCode = getSharedPreferences().getString(key, null);
        if (lineCode != null) {
            Gson gson = new Gson();
            return gson.fromJson(lineCode, LineCode.class);
        }
        return new LineCode();
    }

    public void setLineCode (@NonNull String key, LineCode lineCode) {
        Gson gson = new Gson ();
        String serialized = gson.toJson(lineCode);
        getSharedPreferences()
                .edit()
                .putString(key, serialized)
                .commit();
    }


//    public String getCompanyCode() {
//        return getSharedPreferences().getString(COMPANY_CODE, "100");
//    }
//
//    public void setCompanyCode(@NonNull String companyCode) {
//        getSharedPreferences()
//                .edit()
//                .putString(COMPANY_CODE, companyCode)
//                .commit();
//    }
//
//
//
//    public String getFacilityCode() {
//        return getSharedPreferences().getString(FACILITY_CODE, "273");
//    }
//
//    public void setFacilityCode(@NonNull String facilityCode) {
//        getSharedPreferences()
//                .edit()
//                .putString(FACILITY_CODE, facilityCode)
//                .commit();
//    }

}
