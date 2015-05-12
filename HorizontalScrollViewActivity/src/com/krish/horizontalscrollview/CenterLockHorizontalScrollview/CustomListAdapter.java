package com.krish.horizontalscrollview.CenterLockHorizontalScrollview;

import java.util.ArrayList;

import com.krish.horizontalscrollview.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CustomListAdapter extends ArrayAdapter<String> {
	Context context;
	private ArrayList<String> list;
	int layoutId;
	Holder holder;
	public View view;
	public int currPosition = 0;

	public CustomListAdapter(Context context, int textViewResourceId,
			ArrayList<String> list) {
		super(context, android.R.layout.simple_list_item_1, list);
		this.context = context;
		this.list = list;
		layoutId = textViewResourceId;

	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public String getItem(int position) {
		return list.get(position);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		RelativeLayout layout;
		if (convertView == null)
		{
			layout = (RelativeLayout) View.inflate(context, layoutId, null);
			holder = new Holder();
			holder.title = (TextView) layout.findViewById(R.id.txtNewsSource);
			holder.aapimage=(ImageView)layout.findViewById(R.id.imageView1);
			layout.setTag(holder);
		} 
		else 
		{
			layout = (RelativeLayout) convertView;
			view = layout;
			holder = (Holder) layout.getTag();
		}
		String newsSource = getItem(position);
		holder.title.setText(newsSource);
		return layout;
	}

	private class Holder {
		public TextView title;
		public ImageView aapimage;
	}
	
	public int getCurrentPosition(){
		return currPosition;
	}
}
