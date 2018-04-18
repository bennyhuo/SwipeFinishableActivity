package com.bennyhuo.swipefinishable.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.bennyhuo.swipefinishable.SwipeFinishable;
import com.bennyhuo.swipefinishable.SwipeFinishable.SwipeFinishableActivity;
import com.bennyhuo.swipefinishable.SwipeFinishablePlugin;

/**
 * Created by benny on 9/24/16.
 *
 * You can simply extend BaseSwipeFinishableActivity for convenience.
 */
public class DetailActivity extends Activity implements SwipeFinishableActivity {
    public static final String TAG = "DetailActivity";

    public static final String TITLE = "title";

    SwipeFinishablePlugin plugin = new SwipeFinishablePlugin(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
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
    public SwipeFinishablePlugin getSwipeFinishablePlugin() {
        return plugin;
    }

    @Override
    public void finishThisActivity() {
        super.finish();
    }

    @Override
    public void finish() {
        SwipeFinishable.INSTANCE.finishCurrentActivity();
    }
}
