
package com.pinterest.samples.pinit;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Random;

public class DemoListView extends Activity {

    private static final String TAG = DemoMainActivity.TAG;
    private static final String IMAGE_SOURCE_BASE = "http://placekitten.com/";

    // Total number in list view.
    private static final int N = 20;
    private ArrayList<Point> mSource = new ArrayList<Point>();
    private Random mRandom = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_listview);

        initSource();
        ListView list = (ListView) findViewById(R.id.list);
        DemoListAdapter adapter = new DemoListAdapter(this, mSource);
        list.setAdapter(adapter);
    }

    private void initSource() {
        int w, h;
        for (int i = 0; i < N; i++) {
            // Generate w, h range from [1,5] and [1,4] respectively.
            w = mRandom.nextInt(5) + 1;
            h = mRandom.nextInt(4) + 1;
            w *= 100;
            h *= 100;
            mSource.add(new Point(w, h));
        }
    }
}
