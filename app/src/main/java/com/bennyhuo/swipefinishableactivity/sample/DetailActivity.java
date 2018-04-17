package com.bennyhuo.swipefinishableactivity.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.bennyhuo.swipefinishableactivity.ActivityController;
import com.bennyhuo.swipefinishableactivity.ActivityController.SwipableActivity;
import com.bennyhuo.swipefinishableactivity.SwipeFinishablePlugin;

/**
 * Created by benny on 9/24/16.
 *
 * You can simply extend BaseSwipeFinishableActivity for convenience.
 */
public class DetailActivity extends Activity implements SwipableActivity {
    public static final String TAG = "DetailActivity";

    public static final String TITLE = "title";

    SwipeFinishablePlugin plugin = new SwipeFinishablePlugin(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        plugin.onCreate();
        TextView titleView = (TextView) findViewById(R.id.title);
        Intent intent = getIntent();
        if(intent != null){
            String title = intent.getStringExtra(TITLE);
            if(!TextUtils.isEmpty(title)){
                titleView.setText(title);
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        plugin.onPostCreate();
    }

    @Override
    protected void onStart() {
        super.onStart();
        plugin.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        plugin.onStop();
    }

    @Override
    public SwipeFinishablePlugin getSwipeFinishablePlugin() {
        return plugin;
    }

    @Override
    public void finishThisActivity() {
        super.finish();
    }

    @Override
    public void finish() {
        ActivityController.INSTANCE.finishCurrentActivity();
    }
}
