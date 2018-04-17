package com.bennyhuo.swipefinishableactivity.sample;

import android.app.Application;

import com.bennyhuo.swipefinishableactivity.ActivityController;

/**
 * Created by benny on 9/24/16.
 */
public class DemoApplication extends Application {
    public static final String TAG = "DemoApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        ActivityController.INSTANCE.onCreate(this);
    }
}
