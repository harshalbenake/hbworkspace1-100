
package com.pinterest.samples.pinit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.pinterest.pinit.PinItButton;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * The main view of the pinit demo.
 * Show a list of pinit examples.
 *
 */
public class DemoMainActivity extends Activity {

    public static final String TAG = "Demo Activity";
    /**
     * Please generate your Client ID at http://developers.pinterest.com/manage/ , and put it here.
     */
    public static final String CLIENT_ID = "1436482";
    private ListView mList;
    private ArrayAdapter<String> mAdapter;

    // Create and populate a List of planet names.
    private static final String[] EXAMPLES = new String[] {
        "1. Simple PinIt Demo", "2. PinIt from Gallery", "3. PinIt in ListView"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        mList = (ListView) findViewById(R.id.list);
        ArrayList<String> examples = new ArrayList<String>(Arrays.asList(EXAMPLES));

        // Create ArrayAdapter using the list.
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, examples);
        mList.setAdapter(mAdapter);
        mList.setOnItemClickListener(mListener);

        PinItButton.setDebugMode(true);
        PinItButton.setPartnerId(CLIENT_ID);
    }

    private OnItemClickListener mListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            Intent intent = null;
            final Activity activity = DemoMainActivity.this;
            switch (arg2) {
                case 0:
                    intent = new Intent(activity, DemoSimple.class);
                    break;
                case 1:
                    intent = new Intent(activity, DemoFromGallery.class);
                    break;
                case 2:
                    intent = new Intent(activity, DemoListView.class);
                    break;
                default:
                    return;
            }
            activity.startActivity(intent);
        }
    };
}
