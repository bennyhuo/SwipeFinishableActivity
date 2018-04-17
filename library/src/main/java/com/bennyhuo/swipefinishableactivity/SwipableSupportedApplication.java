package com.bennyhuo.swipefinishableactivity;

import android.app.Application;

/**
 * Created by benny on 9/24/16.
 */
public class SwipableSupportedApplication extends Application{
    public static final String TAG = "SwipableSupportedApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        ActivityController.INSTANCE.onCreate(this);
    }
}
