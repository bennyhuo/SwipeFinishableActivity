package com.bennyhuo.swipefinishable;

import android.view.MotionEvent;

/**
 * Created by benny on 7/25/16.
 */
public interface TouchEventInterceptor {
    boolean shouldInterceptTouchEvent(MotionEvent event);

    boolean onTouch(MotionEvent event);

    boolean onDispatchTouchEvent(MotionEvent event);
}
