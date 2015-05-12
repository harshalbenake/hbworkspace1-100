/*
 * Copyright (C) 2012 Daniel Medina <http://danielme.com>
 * 
 * This file is part of "Android Paginated ListView Demo".
 * 
 * "Android Paginated ListView Demo" is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * "Android Paginated ListView Demo" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License version 3
 * along with this program.  If not, see <http://www.gnu.org/licenses/gpl-3.0.html/>
 */
package com.danielme.blog.android.paginatedlistview.activities;

import java.util.List;

import com.danielme.blog.android.paginatedlistview.CustomArrayAdapter;
import com.danielme.blog.android.paginatedlistview.Datasource;
import com.danielme.blog.android.paginatedlistview.R;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public abstract class AbstractListViewActivity extends ListActivity
{

	protected Datasource datasource;

	protected static final int PAGESIZE = 10;

	protected TextView textViewDisplaying;

	protected View footerView;

	protected boolean loading = false;

	protected class LoadNextPage extends AsyncTask<String, Void, String>
	{
		private List<String> newData = null;

		@Override
		protected String doInBackground(String... arg0)
		{
			// para que de tiempo a ver el footer ;)
			try
			{
				Thread.sleep(1500);
			}
			catch (InterruptedException e)
			{
				Log.e("AbstractListActivity", e.getMessage());
			}
			newData = datasource.getData(getListAdapter().getCount(), PAGESIZE);
			return null;
		}

		@Override
		protected void onPostExecute(String result)
		{
			CustomArrayAdapter customArrayAdapter = ((CustomArrayAdapter) getListAdapter());
			for (String value : newData)
			{
				customArrayAdapter.add(value);
			}
			customArrayAdapter.notifyDataSetChanged();

			getListView().removeFooterView(footerView);
			updateDisplayingTextView();
			loading = false;
		}

	}

	protected void updateDisplayingTextView()
	{
		textViewDisplaying = (TextView) findViewById(R.id.displaying);
		String text = getString(R.string.display);
		text = String.format(text, getListAdapter().getCount(), datasource.getSize());
		textViewDisplaying.setText(text);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		Toast.makeText(this, getListAdapter().getItem(position) + " " + getString(R.string.selected), Toast.LENGTH_SHORT).show();
	}
	
	protected boolean load(int firstVisibleItem, int visibleItemCount, int totalItemCount)
	{
		boolean lastItem = firstVisibleItem + visibleItemCount == totalItemCount && getListView().getChildAt(visibleItemCount -1) != null && getListView().getChildAt(visibleItemCount-1).getBottom() <= getListView().getHeight();
		boolean moreRows = getListAdapter().getCount() < datasource.getSize();
		return moreRows && lastItem && !loading;
		
	}
}