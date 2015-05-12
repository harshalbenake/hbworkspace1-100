package  com.example.autocompletetextimagedemo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;



/**
 * Loads the image in background using a thread pool.
 * @author Shailendra Patil
 */
public class ImageLoader
{
	MemoryCache mMemoryCache = new MemoryCache();
	FileCache mFileCache;
	private Map<ImageView, String> mImageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
	ExecutorService mExecutorService;
	Bitmap mNoImageBitmap = null;
	Drawable mDrawable = null;
	Context mContext;
	String mNoImageUrl;
	private int mNoImageResourceId;

	/**
	 * Default constructor
	 * @param mContext
	 */
	public ImageLoader(Context context, String noImageUrl)
	{
//		System.out.print("In image loader block");

		// new change
		mNoImageResourceId=R.drawable.ic_launcher;

		mContext = context;
		mNoImageUrl = noImageUrl;
		try
		{
			mFileCache = new FileCache(context);
			mExecutorService = Executors.newFixedThreadPool(5);

			try
			{
				if(mNoImageBitmap == null)
				{
					if(mNoImageUrl==null || mNoImageUrl.equalsIgnoreCase("") || mNoImageUrl.equalsIgnoreCase("No record found"))
					{
						mNoImageBitmap = BitmapFactory.decodeResource(context.getResources(), mNoImageResourceId);
					}
					else
					{
						mNoImageBitmap = mMemoryCache.get(mNoImageUrl);
						if(mNoImageBitmap == null)
							mNoImageBitmap =  getBitmap(mNoImageUrl);
					}
				}
			}
			catch(OutOfMemoryError e)
			{
				e.printStackTrace();
				e.printStackTrace();
			}
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
	 * Starts the process for downloading the image and setting to the image
	 * view provided. ProgressBar is set to visibility gone once the image is
	 * set to the image view.
	 * 
	 * @param url
	 * @param imageView
	 * @param progressBar
	 */
	public void displayImage(String url, ImageView imageView, ProgressBar progressBar, int mNoImage)
	{
		try
		{
			System.out.println("displayImage url"+url);
			if(url.equalsIgnoreCase("") || url.equalsIgnoreCase("No Record Found"))
			{
				if(mNoImageBitmap != null)
					imageView.setImageBitmap(mNoImageBitmap);

				if(progressBar != null)
					progressBar.setVisibility(View.GONE);
				imageView.setVisibility(View.VISIBLE);
				return;
			}

			if(progressBar != null)
				progressBar.setVisibility(View.VISIBLE);
			imageView.setVisibility(View.GONE);

			mImageViews.put(imageView, url);
			Bitmap bitmap = mMemoryCache.get(url);

			if(bitmap != null)
			{
				imageView.setImageBitmap(bitmap);

				if(progressBar != null)
					progressBar.setVisibility(View.GONE);
				imageView.setVisibility(View.VISIBLE);
				imageView.invalidate();
			}
			else
			{
				if(progressBar != null)
					progressBar.setVisibility(View.VISIBLE);
				queuePhoto(url, imageView, progressBar);

				if(mDrawable != null)
				{
					imageView.setImageDrawable(mDrawable);
				}
				else
				{
					imageView.setImageResource(mNoImage);
					/*bitmap =  getBitmap(mNoImageUrl);
					if(bitmap != null)
						imageView.setImageBitmap(bitmap);
					else
						imageView.setImageBitmap(mNoImageBitmap);*/
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			imageView.setImageResource(mNoImage);
		}
		catch(Error e)
		{
			e.printStackTrace();
			imageView.setImageResource(mNoImage);
		}
	}

	/**
	 * 
	 * @param url
	 * @param imageView
	 * @param isModifyURLForDensity
	 */
	public void displayImage(String url, ImageView imageView)
	{
		try
		{

			//if(url.equalsIgnoreCase("") || url.equalsIgnoreCase("No Record Found"))
			if(url==null || url.equalsIgnoreCase("") || url.equalsIgnoreCase("No Record Found"))
			{
				if(mNoImageBitmap != null)
					imageView.setImageBitmap(mNoImageBitmap);
				else
					imageView.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), mNoImageResourceId));

				imageView.setVisibility(View.VISIBLE);
				return;
			}

			mImageViews.put(imageView, url);
			Bitmap bitmap = mMemoryCache.get(url);
			if(bitmap != null)
			{
				imageView.setImageBitmap(bitmap);

				imageView.setVisibility(View.VISIBLE);
			}
			else
			{

				//queuePhoto(url, imageView, progressBar);
				queuePhoto(url, imageView);

				if(mDrawable != null)
				{
					imageView.setImageDrawable(mDrawable);
				}
				else
				{
					/*bitmap =  getBitmap(mNoImageUrl);
					if(bitmap != null)
						imageView.setImageBitmap(bitmap);
					else
						imageView.setImageBitmap(mNoImageBitmap);*/
				}
			}
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
	 * Displays image from server on image view if url has a image or it displays given noImageResource image on image view.
	 * @param url
	 * @param imageView
	 * @param noImageResourceId
	 * @param isModifyURLForDensity
	 */
	public void displayImage(String url, ImageView imageView,int noImageResourceId)
	{
		mNoImageResourceId=noImageResourceId;
		try
		{
			url = url.replaceAll(" ", "%20");
			mImageViews.put(imageView, url);
			Bitmap bitmap = mMemoryCache.get(url);


			if(bitmap != null)
			{
				imageView.setImageBitmap(bitmap);
				imageView.setVisibility(View.VISIBLE);
			}
			else
			{
				queuePhoto(url, imageView);
				//
				//				if(mDrawable != null)
				//				{
				//					imageView.setImageDrawable(mDrawable);
				//				}
				//				else
				//				{
				//setting black bg when mNoImageResourceId is set to 0 (used in (Thread listing)video/audio)
				//				if(mNoImageResourceId==0){
				//					imageView.setBackgroundColor(mContext.getResources().getColor(R.color.black));
				//				}else {
				imageView.setImageResource(mNoImageResourceId);
			}
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
	 * Queues all the photos for downloading.
	 * 
	 * @param url
	 * @param imageView
	 * @param progressBar
	 */
	private void queuePhoto(String url, ImageView imageView, ProgressBar progressBar)
	{
		try
		{
			PhotoToLoad photoToLoad = new PhotoToLoad(url, imageView, progressBar);
			mExecutorService.submit(new PhotosLoader(photoToLoad));
		}
		catch(Exception e)
		{
			//			System.out.println("EXP Q Photo");
			e.printStackTrace();

		}
		catch(Error e)
		{
			//			System.out.println("ERROR Q Photo");
			e.printStackTrace();
		}
	}


	private void queuePhoto(String url, ImageView imageView)
	{
		try
		{
			PhotoToLoad photoToLoad = new PhotoToLoad(url, imageView);
			mExecutorService.submit(new PhotosLoader(photoToLoad));
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

	public boolean getBitmapFromCache(String url)
	{
		File f = mFileCache.getFile(url);
		return f.exists();
	}

	/**
	 * fetches the bitmap from the cache directory.
	 * @param url
	 * @return
	 */
	public Bitmap getBitmap(String url)
	{
//		System.out.println("Get this bitmap from server "+url);
		Bitmap bitmap = null;
		try
		{
			File f = mFileCache.getFile(url);
			// from SD cache
			if(f.exists())
			{
				Bitmap b = decodeFile(f);
				if(b != null)
					return b;
			}
			// from web
			try
			{
				/*if(LibUtility.isConnected(mContext) && !url.equalsIgnoreCase("") && !url.equalsIgnoreCase("No record found"))
				{*/
				URL imageUrl = new URL(url);
				HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
				conn.setConnectTimeout(30000);
				conn.setReadTimeout(30000);
				conn.setInstanceFollowRedirects(true);
				InputStream is = conn.getInputStream();
				OutputStream os = new FileOutputStream(f);
				copyStream(is, os);
				is.close();	///
				os.close();
				bitmap = decodeFile(f);

				//				System.out.println("After getting Bimap of URL "+url+" size height "+bitmap.getHeight()+" width "+bitmap.getWidth());

				return bitmap;
				//}
			}
			catch(OutOfMemoryError e)
			{
				//				System.out.println("OUT OF MEM");
				e.printStackTrace();
			}
			catch(Exception e)
			{
				//				System.out.println("EXP GET BITMAP");
				e.printStackTrace();
			}
			catch(Error e)
			{
				//				System.out.println("ERR GET BITMAP");
				e.printStackTrace();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		catch(Error e)
		{
			e.printStackTrace();
		}

		if(mNoImageUrl==null || mNoImageUrl.equalsIgnoreCase("") || mNoImageUrl.equalsIgnoreCase("No record found"))
		{
			bitmap = BitmapFactory.decodeResource(mContext.getResources(),mNoImageResourceId );
		}

		return bitmap;
	}

	public  void copyStream(InputStream is, OutputStream os)
	{
		final int buffer_size = 1024;
		try
		{
			byte[] bytes = new byte[buffer_size];
			for(;;)
			{
				int count = is.read(bytes, 0, buffer_size);
				if(count == -1)
					break;
				os.write(bytes, 0, count);
			}
		}
		catch(Exception ex)
		{

		}
	}



	/**
	 * decodes image and scales it to reduce memory consumption
	 * 
	 * @param f
	 * @return
	 */
	private Bitmap decodeFile(File f)
	{
		try
		{
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, options);

			options.inSampleSize = 1;// calculateInSampleSize(options, 300,
			// 300);

			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, options);
		}
		catch(OutOfMemoryError e)
		{
			e.printStackTrace();
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
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

	public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
	{
		try
		{
			// Raw height and width of image
			final int height = options.outHeight;
			final int width = options.outWidth;
			int inSampleSize = 1;

			if(height > reqHeight || width > reqWidth)
			{
				if(width > height)
					inSampleSize = Math.round((float) height / (float) reqHeight);
				else
					inSampleSize = Math.round((float) width / (float) reqWidth);
			}

			return inSampleSize;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		catch(Error e)
		{
			e.printStackTrace();
		}
		return 1;
	}

	/**
	 * Task for the queue
	 * 
	 * @author Raj Trivedi
	 */
	private class PhotoToLoad
	{
		public String url;
		public ImageView imageView;
		public ProgressBar progressBar;

		public PhotoToLoad(String u, ImageView i, ProgressBar p)
		{
			url = u;
			imageView = i;
			progressBar = p;
		}

		public PhotoToLoad(String u, ImageView i)
		{
			url = u;
			imageView = i;
		}
	}

	public void storeImageInCache(final Handler handler, final String url)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					Bitmap bitmap = getBitmap(url);
					if(handler != null && bitmap != null)
					{
						Message message = new Message();
						message.obj = bitmap;
						handler.handleMessage(message);
					}
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
		}).start();
	}

	/**
	 * Downloads the image data.
	 * 
	 * @author Raj Trivedi
	 */
	class PhotosLoader implements Runnable
	{
		PhotoToLoad photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad)
		{
			this.photoToLoad = photoToLoad;
		}

		@Override
		public void run()
		{
			try
			{
				if(imageViewReused(photoToLoad))
					return;

				Bitmap bmp = getBitmap(photoToLoad.url);
				mMemoryCache.put(photoToLoad.url, bmp);

				if(imageViewReused(photoToLoad))
					return;

				BitmapDisplayer bitmapDisplayer = new BitmapDisplayer(bmp, photoToLoad);

				//AMOL 
				Object obj=photoToLoad.imageView.getContext();
				Activity activity=null;
				/*if(obj instanceof ApplicationClass){
//					System.out.println("ApplicationClass instance");
					activity = (Activity)mContext;
				}else*/ if(obj instanceof Activity){
//					System.out.println("Activity instance");
					activity = (Activity) obj;
				}
				activity.runOnUiThread(bitmapDisplayer);
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

	/**
	 * 
	 * @param photoToLoad
	 * @return
	 */
	boolean imageViewReused(PhotoToLoad photoToLoad)
	{
		try
		{
			String tag = mImageViews.get(photoToLoad.imageView);
			if(tag == null || !tag.equals(photoToLoad.url))
				return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		catch(Error e)
		{
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Used to display bitmap in the UI thread
	 * 
	 * @author Raj Trivedi
	 */
	class BitmapDisplayer implements Runnable
	{
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p)
		{
			bitmap = b;
			photoToLoad = p;
		}

		public void run()
		{
			try
			{
				if(imageViewReused(photoToLoad))
				{
					photoToLoad.progressBar.setVisibility(View.GONE);
					photoToLoad.imageView.setVisibility(View.VISIBLE);
					return;
				}

				if(bitmap != null)
				{
					photoToLoad.imageView.setImageBitmap(bitmap);
					if(photoToLoad.progressBar != null)
						photoToLoad.progressBar.setVisibility(View.GONE);
					photoToLoad.imageView.setVisibility(View.VISIBLE);
				}
				else
				{
					if(mDrawable != null)
					{
						photoToLoad.imageView.setImageDrawable(mDrawable);
						if(photoToLoad.progressBar != null)
							photoToLoad.progressBar.setVisibility(View.GONE);
						photoToLoad.imageView.setVisibility(View.VISIBLE);
					}
					else
					{
						//photoToLoad.imageView.setImageBitmap(getBitmap(mNoImageUrl));
						photoToLoad.imageView.setImageResource(mNoImageResourceId);
						if(photoToLoad.progressBar != null)
							photoToLoad.progressBar.setVisibility(View.GONE);
						photoToLoad.imageView.setVisibility(View.VISIBLE);
					}
				}
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

	/**
	 * Clears the cache directory
	 */
	public void clearCache()
	{
		System.gc();
		mMemoryCache.clear();
		mFileCache.clear();
	}
}
