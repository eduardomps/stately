package com.lloydtorres.stately.core;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.crashlytics.android.Crashlytics;
import com.orm.SugarApp;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Lloyd on 2016-01-29.
 * This class is used for app-wide changes.
 */
public class StatelyApp extends SugarApp {
    private SharedPreferences storage; // shared preferences

    @Override
    public void onCreate() {
        super.onCreate();

        // analytics
        storage = PreferenceManager.getDefaultSharedPreferences(this);
        if (storage.getBoolean("setting_crashreport", true))
        {
            Fabric.with(this, new Crashlytics());
        }
    }
}