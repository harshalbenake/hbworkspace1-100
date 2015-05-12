package com.pulltorefreshdemo.activity;

import java.util.Arrays;
import java.util.LinkedList;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.ArrayAdapter;

import com.markupartist.android.example.pulltorefresh.R;
import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;

public class PullToRefreshActivity extends ListActivity {    
    private LinkedList<String> mListItems;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		 StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        setContentView(R.layout.pull_to_refresh);

     
        // Set a listener to be invoked when the list should be refreshed.
        ((PullToRefreshListView) getListView()).setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Do work to refresh the list here.
                new GetDataTask().execute();
            }
        });

        mListItems = new LinkedList<String>();
        mListItems.addAll(Arrays.asList(mStrings));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mListItems);

        setListAdapter(adapter);
    }

    private class GetDataTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {
            // Simulates a background job.
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                ;
            }
            return mStrings;
        }

        @Override
        protected void onPostExecute(String[] result) {
            mListItems.addFirst("Added Refresh at first");
            mListItems.add("added default harshal");
            mListItems.addLast("Added Refresh at last");

            // Call onRefreshComplete when the list has been refreshed.
            ((PullToRefreshListView) getListView()).onRefreshComplete();

            super.onPostExecute(result);
        }
    }

    private String[] mStrings = {
            "AAA", "BBB", "CCC",
            "DDD", "EEE", "FFF", "GGG", "HHH",
           };
}
