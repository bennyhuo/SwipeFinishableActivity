package com.bennyhuo.swipefinishable.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bennyhuo.swipefinishable.SwipeFinishable;
import com.bennyhuo.swipefinishable.SwipeFinishable.SwipeFinishableActivity;

/**
 * Created by benny on 9/24/16.
 */
public class SingleTaskActivity extends Activity implements SwipeFinishableActivity {
    public static final String TAG = "DetailActivity";

    public static final String TITLE = "title";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        final TextView titleView = (TextView) findViewById(R.id.title);
        titleView.setText("SingleTaskActivity");
        titleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SingleTaskActivity.this, DetailActivity.class);
                SwipeFinishable.INSTANCE.startActivity(intent);
            }
        });
    }

    @Override
    public void finish() {
        SwipeFinishable.INSTANCE.finishCurrentActivity();
    }
}
