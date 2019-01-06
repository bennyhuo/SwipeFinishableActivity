package com.bennyhuo.swipefinishable;

import android.app.Activity;

/**
 * Created by benny on 9/19/16.
 */
public abstract class BaseSwipeFinishableActivity extends Activity implements SwipeFinishable.SwipeFinishableActivity {
    public static final String TAG = "BaseSwipeFinishableActivity";

    @Override
    public void finish() {
        SwipeFinishable.INSTANCE.finishCurrentActivity();
    }
}
