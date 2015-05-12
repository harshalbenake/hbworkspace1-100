
package com.pinterest.samples.pinit;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pinterest.pinit.PinItButton;
import com.pinterest.samples.pinit.DemoHelper.TaskCallback;

import java.util.ArrayList;
import java.util.HashMap;

public class DemoListAdapter extends BaseAdapter {

    private static final String TAG = DemoMainActivity.TAG;
    private static final String WEB_URL = "http://placekitten.com";
    private static final String IMAGE_SOURCE_BASE = "http://placekitten.com/";

    private ArrayList<Point> mSource = new ArrayList<Point>();
    private Activity mActivity;
    private LayoutInflater mInflater;

    final String mDescription;
    private HashMap<String, Bitmap> mCache = new HashMap<String, Bitmap>(20);

    public DemoListAdapter(Activity activity, ArrayList<Point> source) {
        mSource = source;
        mActivity = activity;
        mInflater = activity.getLayoutInflater();

        Resources res = activity.getResources();
        mDescription = res.getString(R.string.pin_desc_kitten);
    }

    @Override
    public int getCount() {
        return mSource.size();
    }

    @Override
    public Object getItem(int position) {
        return mSource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listview_item, null);
            holder = new ViewHolder();
            holder.mImage = (ImageView) convertView.findViewById(R.id.source_iv);
            holder.mText = (TextView) convertView.findViewById(R.id.desc_tv);
            holder.mPinIt = (PinItButton) convertView.findViewById(R.id.pin_bt);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Point size = (Point) getItem(position);

        String url = IMAGE_SOURCE_BASE + size.x + "/" + size.y;
        setDisplayImage(holder.mImage, url);

        String desc = mDescription + " with size " + size.x + " X " + size.y;
        holder.mText.setText(desc);

        holder.mPinIt.setImageUrl(url);
        holder.mPinIt.setDescription(desc);
        holder.mPinIt.setUrl(WEB_URL);

        return convertView;
    }

    private void setDisplayImage(final ImageView iv, final String url) {
        // Show the remote image in ImageView.
        if (getFromCache(url) != null) {
            iv.setImageBitmap(getFromCache(url));
        } else {
            new DemoHelper.RemoteImageTask(iv, url, new TaskCallback() {

                @Override
                public void onTaskFinished(Bitmap bmp) {
                    putToCache(url, bmp);
                }
            }).execute();
        }
    }

    private static class ViewHolder {
        ImageView mImage;
        TextView mText;
        PinItButton mPinIt;
    }

    // Get and put from the memory cache.
    private Bitmap getFromCache(String key) {
        return mCache.get(key);
    }

    private void putToCache(String key, Bitmap bmp) {
        if (getFromCache(key) == null) {
            mCache.put(key, bmp);
        }
    }
}
