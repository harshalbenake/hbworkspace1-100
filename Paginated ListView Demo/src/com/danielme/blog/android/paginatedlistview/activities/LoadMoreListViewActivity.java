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

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.danielme.blog.android.paginatedlistview.CustomArrayAdapter;
import com.danielme.blog.android.paginatedlistview.Datasource;
import com.danielme.blog.android.paginatedlistview.R;

public class LoadMoreListViewActivity extends AbstractListViewActivity
{

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.endless);
		datasource = Datasource.getInstance();

		footerView = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_loadmore, null, false);
		getListView().addFooterView(footerView, null, false);
		setListAdapter(new CustomArrayAdapter(this, datasource.getData(0, PAGESIZE)));
		getListView().removeFooterView(footerView);

		getListView().setOnScrollListener(new OnScrollListener()
		{
			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1)
			{
				// nothing here
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
			{				
				if (load(firstVisibleItem, visibleItemCount, totalItemCount))
				{
					loading = true;
					footerView.findViewById(R.id.buttonLoadMore).setVisibility(View.VISIBLE);
					footerView.findViewById(R.id.progressBar1).setVisibility(View.GONE);
					getListView().addFooterView(footerView, null, false);
				}
			}
		});

		updateDisplayingTextView();

	}

	public void loadMore(View view)
	{
		footerView.findViewById(R.id.buttonLoadMore).setVisibility(View.GONE);
		footerView.findViewById(R.id.progressBar1).setVisibility(View.VISIBLE);
		(new LoadNextPage()).execute("");
	}

}