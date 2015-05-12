package com.example.linkedinbest;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * Class contains the Memory Cache
 * @author Raj Trivedi
 */
public class MemoryCache
{
	
	LruCache<String, Bitmap> lruCache=new LruCache<String, Bitmap>(1024*1024*2);
	/**
	 * @param id
	 * @return
	 */
	public Bitmap get(String id)
	{
		return lruCache.get(id);
	}

	/**
	 * @param id
	 * @param bitmap
	 */
	public void put(String key, Bitmap bitmap)
	{
		try
		{
			synchronized (lruCache) {
			     if (lruCache.get(key) == null) {
			    	 lruCache.put(key, bitmap);
			     
			   }}
		}
		catch(Throwable th)
		{
			th.printStackTrace();
		}
	}

	/**
     * 
     */
	public void clear()
	{
		lruCache.evictAll();
	}

	
}