package com.bennyhuo.swipefinishable.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bennyhuo.swipefinishable.SwipeFinishable;

public class MainActivity extends Activity {
    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button useSwipeFinishable = (Button) findViewById(R.id.useSwipeFinishable);
        Button useBuiltin = (Button) findViewById(R.id.useBuiltin);

        useSwipeFinishable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                SwipeFinishable.INSTANCE.startActivity(intent);
            }
        });

        useBuiltin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DetailActivity2.class);
                startActivity(intent);
            }
        });
    }
}
