package com.bennyhuo.swipefinishable.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.bennyhuo.swipefinishable.SwipeFinishable;

public class MainActivity extends Activity {

    ListView listView;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.list);
        adapter = new ArrayAdapter<>(this, R.layout.list_item, R.id.title);
        listView.setAdapter(adapter);

        adapter.addAll("Item 1", "Item 2", "Item 3");

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra(DetailActivity.TITLE, adapter.getItem(position));
                SwipeFinishable.INSTANCE.startActivity(intent);
            }
        });
    }
}
