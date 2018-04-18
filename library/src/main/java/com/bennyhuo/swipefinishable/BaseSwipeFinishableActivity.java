package com.bennyhuo.swipefinishable;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by benny on 9/19/16.
 */
public abstract class BaseSwipeFinishableActivity extends Activity implements SwipeFinishable.SwipeFinishableActivity {
    public static final String TAG = "BaseSwipeFinishableActivity";

    private SwipeFinishablePlugin swipeFinishablePlugin = new SwipeFinishablePlugin(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        swipeFinishablePlugin.onCreate();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        swipeFinishablePlugin.onPostCreate();
    }

    @Override
    protected void onStart() {
        super.onStart();
        swipeFinishablePlugin.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        swipeFinishablePlugin.onStop();
    }

    @Override
    public void finish() {
        swipeFinishablePlugin.finish();
    }

    @Override
    public SwipeFinishablePlugin getSwipeFinishablePlugin(){
        return swipeFinishablePlugin;
    }

    @Override
    public void finishThisActivity() {
        super.finish();
    }
}
