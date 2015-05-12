package com.example.listview_baseadapter_getitemviewtype;

import java.util.ArrayList;
import java.util.TreeSet;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int MAX_NUMBER_CONTENT = 9;
    private MyCustomAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new MyCustomAdapter();
        for (int i = 0; i < MAX_NUMBER_CONTENT; i++) {
            mAdapter.addItem("Demo text " + i);
           
        }
       // setListAdapter(mAdapter);
        setContentView(R.layout.activity_main);
        ListView listView=(ListView)findViewById(R.id.listview1);
        listView.setAdapter(mAdapter);
    } 

    private class MyCustomAdapter extends BaseAdapter {

        private static final int ITEM_TYPE_ONE = 1;
        private static final int ITEM_TYPE_TWO = 2;
        private static final int ITEM_TYPE_THREE= 3;
        private static final int ITEM_TYPE_MAX_COUNT = ITEM_TYPE_ONE+ITEM_TYPE_TWO+ITEM_TYPE_THREE+1;

        private ArrayList<String> mData = new ArrayList<String>();
        private LayoutInflater mInflater;

        public MyCustomAdapter() {
            mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void addItem(final String item) {
            mData.add(item);
            // The notification is not necessary since the items are not added dynamically
            //notifyDataSetChanged();
        }

        

        @Override
        public int getItemViewType(int position) {
           // return mSeparatorSet.contains(position) ? ITEM_TYPE_TWO: ITEM_TYPE_ONE;
        	int type;
        	if(position<=2)
        	{
        		type=ITEM_TYPE_ONE;
        	}
        	else if (position>2 && position<=5) {
        		type=ITEM_TYPE_TWO;
			}
        	else {
        		type=ITEM_TYPE_THREE;
			}
        	return type;
        }

        @Override
        public int getViewTypeCount() {
            return ITEM_TYPE_MAX_COUNT;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public String getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            int type = getItemViewType(position);
            Log.v(LOG_TAG, "getView " + position + " " + convertView + " type = " + type);
            if (convertView == null) {
                holder = new ViewHolder();
                switch (type) {
                case ITEM_TYPE_ONE:
                    convertView = mInflater.inflate(R.layout.item_view_one,  null);
                    holder.textView1 = (TextView)convertView.findViewById(R.id.textView1);
                    holder.imageView=(ImageView)convertView.findViewById(R.id.imageView1);
                    break;
                case ITEM_TYPE_TWO:
                    convertView = mInflater.inflate(R.layout.item_view_two, null);
                    holder.textView1 = (TextView)convertView.findViewById(R.id.textView1);
                    holder.imageView=(ImageView)convertView.findViewById(R.id.imageView1);
                    holder.textView2 = (TextView)convertView.findViewById(R.id.textView2);
                    break;
                case ITEM_TYPE_THREE:
                    convertView = mInflater.inflate(R.layout.item_view_three, null);
                    holder.textView1 = (TextView)convertView.findViewById(R.id.textView1);
                    holder.imageView=(ImageView)convertView.findViewById(R.id.imageView1);
                    break;
                    
                default:
                    break;
                }
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            holder.textView1.setText(mData.get(position));
            return convertView;
        }
    }

    public static class ViewHolder {
        public TextView textView1;
        public ImageView imageView;
        public TextView textView2;
    }
}
