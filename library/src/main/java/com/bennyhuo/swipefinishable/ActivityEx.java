package com.bennyhuo.swipefinishable;

import android.app.Activity;
import android.app.ActivityOptions;
import android.os.Build;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ActivityEx {
    public static final String TAG = ActivityEx.class.getSimpleName();

    private static Method convertToTranslucentMethod;
    private static Method finishMethod;

    static {
        try {
            Class translucentConversionListenerClass = Class.forName("android.app.Activity$TranslucentConversionListener");
            convertToTranslucentMethod = Activity.class.getDeclaredMethod("convertToTranslucent", translucentConversionListenerClass, ActivityOptions.class);
            convertToTranslucentMethod.setAccessible(true);

            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                finishMethod = Activity.class.getDeclaredMethod("finish", int.class);
            } else {
                finishMethod = Activity.class.getDeclaredMethod("finish", boolean.class);
            }

            finishMethod.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final Activity activity;

    public ActivityEx(Activity activity) {
        this.activity = activity;
    }

    public void convertToTranslucent() {
        try {
            convertToTranslucentMethod.invoke(activity, null , null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void finish(){
        try {
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                finishMethod.invoke(activity, 0);
            } else {
                finishMethod.invoke(activity, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
