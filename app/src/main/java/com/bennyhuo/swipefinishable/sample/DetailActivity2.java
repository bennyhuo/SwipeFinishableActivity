package com.bennyhuo.swipefinishable.sample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

/**
 * Created by benny on 9/24/16.
 *
 *  This Activity use the system built swipe back feature.
 */
public class DetailActivity2 extends Activity {
    public static final String TAG = "DetailActivity";

    public static final String TITLE = "title";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_SWIPE_TO_DISMISS);
        overridePendingTransition(0,0);
        setContentView(R.layout.activity_detail2);
        final TextView titleView = (TextView) findViewById(R.id.title);

        titleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "ViewRoot: " + titleView.getRootView().getParent());
                Log.d(TAG, "isHWA: " + titleView.isHardwareAccelerated());
            }
        });
    }
}
