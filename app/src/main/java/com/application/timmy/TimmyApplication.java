package com.application.timmy;

import android.app.Application;

public class TimmyApplication extends Application {

    // application is already a singleton
    private static TimmyApplication instance;

    public static TimmyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

}
