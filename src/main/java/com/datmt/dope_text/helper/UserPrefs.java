package com.datmt.dope_text.helper;

import java.util.prefs.Preferences;

public class UserPrefs {

    private static final String DB_LOCATION = "DB_LOCATION";


    public static Preferences getPrefs() {
        Preferences prefs = Preferences.userNodeForPackage(UserPrefs.class);
        return prefs;
    }


    public static String getValue(String prefName, String def) {
        return getPrefs().get(prefName, def);
    }

    public static void setValue(String prefName, String value) {
        getPrefs().put(prefName, value);
    }

    public static String getDbLocation() {
        return getValue(DB_LOCATION, "");
    }

    public static void setDbLocation(String key) {
        setValue(DB_LOCATION, key);
    }

}