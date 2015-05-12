package  com.example.linkedinbest;

import java.io.File;

import android.content.Context;

/**
 * File cache holds all the cache related files in the cache folder of external
 * storage device or internal cache folder.
 * @author Raj Trivedi
 */
public class FileCache
{
	private File cacheDir;

	/**
	 * Default constructor
	 * 
	 * @param context
	 */
	public FileCache(Context context)
	{
		try
		{
			cacheDir = context.getDir("LazyList", Context.MODE_PRIVATE); 
			 
			
//			if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
//				 cacheDir = new
//				 File(android.os.Environment.getExternalStorageDirectory(),"LazyList");
//				cacheDir = context.getExternalCacheDir();
//			}else{
//				cacheDir = context.getCacheDir();
//			}
			if(!cacheDir.exists())
				cacheDir.mkdirs();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		catch(Error e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * fetches the file for a given absolute path
	 * 
	 * @param url
	 * @return
	 */
	public File getFile(String url)
	{
		try
		{
			String filename = String.valueOf(url.hashCode());
			File f = new File(cacheDir, filename);
			return f;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		catch(Error e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Clears the cache directory.
	 */
	public void clear()
	{
		try
		{
			File[] files = cacheDir.listFiles();
			if(files == null)
				return;
			for(File f : files)
				f.delete();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		catch(Error e)
		{
			e.printStackTrace();
		}
	}
}