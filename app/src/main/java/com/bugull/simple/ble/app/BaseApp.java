package com.bugull.simple.ble.app;

import android.app.Application;

/**
 *
 * Created by leaf on 2017/12/12.
 */

public class BaseApp extends Application{
    private static BaseApp instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
    public static BaseApp getInstance(){
        return instance;
    }
}
