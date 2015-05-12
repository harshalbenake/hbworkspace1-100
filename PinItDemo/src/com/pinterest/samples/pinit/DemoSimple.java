
package com.pinterest.samples.pinit;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.pinterest.pinit.PinItButton;
import com.pinterest.pinit.PinItListener;

/**
 * The simple demo to show a image from Web, and use pinit button to pin it.
 *
 */
public class DemoSimple extends Activity {

    private static final String TAG = DemoMainActivity.TAG;
    private static final String IMAGE_SOURCE = "http://placekitten.com/500/400";
    private static final String WEB_URL = "http://placekitten.com";

    private ImageView mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_simple);

        Resources res = getResources();
        final String description = res.getString(R.string.pin_desc_kitten);
        ((TextView) findViewById(R.id.desc_tv)).setText(description);
        mImage = (ImageView) findViewById(R.id.source_iv);

        // Show the remote image in ImageView.
        new DemoHelper.RemoteImageTask(mImage, IMAGE_SOURCE).execute();

        PinItButton pinIt = (PinItButton) findViewById(R.id.pin_bt);
        pinIt.setImageUrl(IMAGE_SOURCE);
        pinIt.setUrl(WEB_URL);
        pinIt.setDescription(description);
        pinIt.setListener(_listener);
    }

    PinItListener _listener = new PinItListener() {

        @Override
        public void onStart() {
            super.onStart();
            Log.i(TAG, "PinItListener.onStart");
        }

        @Override
        public void onComplete(boolean completed) {
            super.onComplete(completed);
            Log.i(TAG, "PinItListener.onComplete");
        }

        @Override
        public void onException(Exception e) {
            super.onException(e);
            Log.i(TAG, "PinItListener.onException");
        }

    };
}
