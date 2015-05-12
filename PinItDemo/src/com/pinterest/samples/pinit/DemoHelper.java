
package com.pinterest.samples.pinit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.net.URL;

public class DemoHelper {

    private static final String TAG = DemoMainActivity.TAG;

    public static class RemoteImageTask extends AsyncTask<Void, Void, Bitmap> {
        ImageView _image;
        String _imageSource;
        TaskCallback _callback;

        public RemoteImageTask(ImageView image, String imageSource) {
            this(image, imageSource, null);
        }

        public RemoteImageTask(ImageView image, String imageSource, TaskCallback callback) {
            _image = image;
            _imageSource = imageSource;
            _callback = callback;
        }

        protected Bitmap doInBackground(Void... params) {
            URL url;
            Bitmap bmp = null;
            try {
                url = new URL(_imageSource);
                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (Exception ignored) {
                Log.e(TAG, "Exception", ignored);
            }

            return bmp;
        }

        protected void onPostExecute(Bitmap bmp) {
            _image.setImageBitmap(bmp);
            if (_callback != null)
                _callback.onTaskFinished(bmp);
        }
    }

    public interface TaskCallback {
        public void onTaskFinished(final Bitmap bmp);
    }
}
