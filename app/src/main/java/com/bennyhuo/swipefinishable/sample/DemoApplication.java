package com.bennyhuo.swipefinishable.sample;

import android.app.Application;

import com.bennyhuo.swipefinishable.SwipeFinishable;

/**
 * Created by benny on 9/24/16.
 */
public class DemoApplication extends Application {
    public static final String TAG = "DemoApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        SwipeFinishable.INSTANCE.init(this);
    }
}
