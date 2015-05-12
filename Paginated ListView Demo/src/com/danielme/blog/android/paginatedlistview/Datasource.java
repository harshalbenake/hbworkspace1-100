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

package com.danielme.blog.android.paginatedlistview;

import java.util.ArrayList;
import java.util.List;

/**
 * Test data.
 * @author danielme.com
 *
 */
public class Datasource
{
	//Singleton pattern
	private static Datasource datasource = null;
	
	private List<String> data = null;
	
	private static final int SIZE = 74;
	
	public static Datasource getInstance()
	{
		if (datasource == null)
		{
			datasource = new Datasource();
		}
		return datasource;
	}
	
	private Datasource()
	{
		data = new ArrayList<String>(SIZE);
		for (int i =1 ; i <= SIZE; i++)
		{
			data.add("row " + i);
		}		
	}
	
	public int getSize()
	{
		return SIZE;
	}
	
	/**
	 * Returns the elements in a <b>NEW</b> list.
	 */
	public List<String> getData(int offset, int limit)
	{
		List<String> newList = new ArrayList<String>(limit);
		int end = offset + limit;
		if (end > data.size())
		{
			end = data.size();
		}
		newList.addAll(data.subList(offset, end));
		return newList;		
	}

}