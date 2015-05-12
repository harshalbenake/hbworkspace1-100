package ca.jvsh.livewallpaper;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.WindowManager;

public class LiveWallpaper extends WallpaperService
{

	public static final String	SHARED_PREFS_NAME	= "livewallpapersettings";

	@Override
	public void onCreate()
	{
		super.onCreate();
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}

	@Override
	public Engine onCreateEngine()
	{
		return new TestPatternEngine();
	}

	class TestPatternEngine extends Engine implements
			SharedPreferences.OnSharedPreferenceChangeListener
	{

		private final Handler		mHandler		= new Handler();
		private float				mTouchX			= -1;
		private float				mTouchY			= -1;
		private final Paint			mPaint			= new Paint();
		private final Runnable		mDrawPattern	= new Runnable()
													{
														public void run()
														{
															drawFrame();
														}
													};
		private boolean				mVisible;
		private SharedPreferences	mPreferences;

		private Rect				mRectFrame;

		private Rect[]				mColorRectangles;
		private int[]				rectColor;
		private int					mRectCount;

		// private
		private Rect				mGradientRect;
		GradientDrawable			mGradient;
		private boolean				mHorizontal		= false;
		private int					mFrameCounter	= 0;
		private boolean				mMotion			= true;
		private String				mShape			= "smpte";

		TestPatternEngine()
		{
			final Paint paint = mPaint;
			paint.setColor(0xffffffff);
			paint.setAntiAlias(true);
			paint.setStrokeWidth(2);
			paint.setStrokeCap(Paint.Cap.ROUND);
			paint.setStyle(Paint.Style.STROKE);

			mPreferences = LiveWallpaper.this.getSharedPreferences(SHARED_PREFS_NAME, 0);
			mPreferences.registerOnSharedPreferenceChangeListener(this);
			onSharedPreferenceChanged(mPreferences, null);
		}

		public void onSharedPreferenceChanged(SharedPreferences prefs,
				String key)
		{
			mShape = prefs.getString("livewallpaper_testpattern", "smpte");
			mMotion = prefs.getBoolean("livewallpaper_movement", true);
			readColors();
		}

		private void readColors()
		{

			int pid = getResources().getIdentifier(mShape + "colors", "array", getPackageName());

			rectColor = getResources().getIntArray(pid);
			mRectCount = rectColor.length;
			mColorRectangles = new Rect[mRectCount];

			System.out.println("mRectCount "+mRectCount);
			initFrameParams();
		}

		@Override
		public void onCreate(SurfaceHolder surfaceHolder)
		{
			super.onCreate(surfaceHolder);
			setTouchEventsEnabled(true);
		}

		@Override
		public void onDestroy()
		{
			super.onDestroy();
			mHandler.removeCallbacks(mDrawPattern);
		}

		@Override
		public void onVisibilityChanged(boolean visible)
		{
			mVisible = visible;
			if (visible)
			{
				drawFrame();
			}
			else
			{
				mHandler.removeCallbacks(mDrawPattern);
			}
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format,
				int width, int height)
		{
			super.onSurfaceChanged(holder, format, width, height);

			initFrameParams();

			drawFrame();
		}

		@Override
		public void onSurfaceCreated(SurfaceHolder holder)
		{
			super.onSurfaceCreated(holder);
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder)
		{
			super.onSurfaceDestroyed(holder);
			mVisible = false;
			mHandler.removeCallbacks(mDrawPattern);
		}

		@Override
		public void onOffsetsChanged(float xOffset, float yOffset, float xStep,
				float yStep, int xPixels, int yPixels)
		{

			drawFrame();
		}

		/*
		 * Store the position of the touch event so we can use it for drawing
		 * later
		 */
		@Override
		public void onTouchEvent(MotionEvent event)
		{
			if (event.getAction() == MotionEvent.ACTION_MOVE)
			{
				mTouchX = event.getX();
				mTouchY = event.getY();
			}
			else
			{
				mTouchX = -1;
				mTouchY = -1;
			}
			super.onTouchEvent(event);
		}

		/*
		 * Draw one frame of the animation. This method gets called repeatedly
		 * by posting a delayed Runnable. You can do any drawing you want in
		 * here. This example draws a wireframe cube.
		 */
		void drawFrame()
		{
			final SurfaceHolder holder = getSurfaceHolder();

			Canvas c = null;
			try
			{
				c = holder.lockCanvas();
				if (c != null)
				{
					// draw something
					drawPattern(c);
					drawTouchPoint(c);
				}
			}
			finally
			{
				if (c != null)
					holder.unlockCanvasAndPost(c);
			}

			mHandler.removeCallbacks(mDrawPattern);
			if (mVisible)
			{
				mHandler.postDelayed(mDrawPattern, 1000 / 25);
			}
		}

		void drawPattern(Canvas c)
		{
			c.save();
			c.drawColor(0xff000000);

			Paint paint = new Paint();
			if (mMotion)
			{
				mFrameCounter++;
				if (mHorizontal)
				{
					int right;
					int left;
					if (mFrameCounter > mRectFrame.right)
						mFrameCounter = 0;
					
					for (int i = 0; i < mRectCount; i++)
					{
						paint.setColor(rectColor[i]);

						right = mColorRectangles[i].right + mFrameCounter;
						left = mColorRectangles[i].left + mFrameCounter;

						if(right > mRectFrame.right)
						{
							c.drawRect(left - mRectFrame.right, mColorRectangles[i].top, right - mRectFrame.right, mColorRectangles[i].bottom, paint);
						}

						if(left < mRectFrame.right)
						{
							c.drawRect(left, mColorRectangles[i].top, right, mColorRectangles[i].bottom, paint);
						}
					}
					
					if(mShape.compareToIgnoreCase("smpte") == 0)
					{
						right =mGradientRect.right + mFrameCounter;
						left = mGradientRect.left + mFrameCounter;
						if(right > mRectFrame.right)
						{
							mGradient.setBounds(left - mRectFrame.right, mGradientRect.top, right - mRectFrame.right, mGradientRect.bottom);
							mGradient.draw(c);
						}

						if(left < mRectFrame.right)
						{
							mGradient.setBounds(left, mGradientRect.top, right, mGradientRect.bottom);
							mGradient.draw(c);
						}
					}
				}
				else
				{
					int top;
					int bottom;
					if (mFrameCounter > mRectFrame.bottom)
						mFrameCounter = 0;

					
					for (int i = 0; i < mRectCount; i++)
					{
						paint.setColor(rectColor[i]);

						top = mColorRectangles[i].top + mFrameCounter;
						bottom = mColorRectangles[i].bottom + mFrameCounter;

						if(bottom > mRectFrame.bottom)
						{
							c.drawRect(mColorRectangles[i].left, top - mRectFrame.bottom, mColorRectangles[i].right, bottom - mRectFrame.bottom, paint);
						}

						if(top < mRectFrame.bottom)
						{
							c.drawRect(mColorRectangles[i].left, top, mColorRectangles[i].right, bottom, paint);
						}
					}
					
					if(mShape.compareToIgnoreCase("smpte") == 0)
					{
						top = mGradientRect.top + mFrameCounter;
						bottom = mGradientRect.bottom + mFrameCounter;

						if(bottom > mRectFrame.bottom)
						{
							mGradient.setBounds(mGradientRect.left, top - mRectFrame.bottom, mGradientRect.right, bottom - mRectFrame.bottom);
							mGradient.draw(c);
						}

						if(top < mRectFrame.bottom)
						{
							mGradient.setBounds(mGradientRect.left, top, mGradientRect.right, bottom);
							mGradient.draw(c);
						}

					}
				}
			}
			else
			{
				for (int i = 0; i < mRectCount; i++)
				{
					paint.setColor(rectColor[i]);
					c.drawRect(mColorRectangles[i], paint);
				}

				if(mShape.compareToIgnoreCase("smpte") == 0)
				{
					mGradient.setBounds(mGradientRect);
					mGradient.draw(c);
				}
			}
			c.restore();
		}

		void drawTouchPoint(Canvas c)
		{
			if (mTouchX >= 0 && mTouchY >= 0)
			{
				c.drawCircle(mTouchX, mTouchY, 80, mPaint);
			}
		}

		void initFrameParams()
		{
			DisplayMetrics metrics = new DisplayMetrics();
			Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
			display.getMetrics(metrics);

			mRectFrame = new Rect(0, 0, metrics.widthPixels, metrics.heightPixels);

			
			int rotation = display.getOrientation();
			if(rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180)
				mHorizontal = false;
			else
				mHorizontal = true;

			System.out.println("mHorizontal "+mHorizontal);
			System.out.println("mShape "+mShape);
			if(mShape.compareToIgnoreCase("smpte") == 0)
			{
				System.out.println("mShape == smpte");
			
				CreateSmpte();
			}
			else if(mShape.compareToIgnoreCase("bars") == 0)
			{
				System.out.println("mShape == bars");
				CreateBars();
			}
			else
			{
				System.out.println("mShape == ebu");
				CreateEbu();
			}
		}
		
		private void CreateSmpte()
		{
			if(mHorizontal)
			{
				int topHeight = mRectFrame.bottom * 7 / 12;
				int bottomHeight = mRectFrame.bottom * 3 / 4;
				int wideColumnWidth = mRectFrame.right / 8;
				int narrowColumnWidth = mRectFrame.right * 3 / 28;

				mColorRectangles[0] = new Rect(0, 0, wideColumnWidth, topHeight);
				for (int i = 1; i < 8; i++)
				{
					mColorRectangles[i] = new Rect(mColorRectangles[i - 1].right, 0, mColorRectangles[i - 1].right + narrowColumnWidth, topHeight);
				}

				mColorRectangles[8] = new Rect(mColorRectangles[7].right, 0, mRectFrame.right, topHeight);

				for (int i = 0; i < 2; i++)
				{
					int middleTop = mRectFrame.bottom * (7 + i) / 12;
					int middleBottom = mRectFrame.bottom * (8 + i) / 12;
					mColorRectangles[i + 9] = new Rect(0, middleTop, wideColumnWidth, middleBottom);
					mColorRectangles[i + 11] = new Rect(wideColumnWidth, middleTop, narrowColumnWidth + wideColumnWidth, middleBottom);
					mColorRectangles[i + 13] = new Rect(narrowColumnWidth * 7 + wideColumnWidth, middleTop, mRectFrame.right, middleBottom);
				}

				mColorRectangles[15] = new Rect(narrowColumnWidth + wideColumnWidth, topHeight, narrowColumnWidth * 7 + wideColumnWidth, mRectFrame.bottom * 8 / 12);

				mGradientRect = new Rect(mColorRectangles[15].left, mColorRectangles[15].bottom, mColorRectangles[15].right, mRectFrame.bottom * 9 / 12);
				mGradient = new GradientDrawable(Orientation.LEFT_RIGHT, new int[] { 0xff050505, 0xfffdfdfd });
				mGradient.setBounds(mGradientRect);

				mColorRectangles[16] = new Rect(0, bottomHeight, wideColumnWidth, mRectFrame.right);
				mColorRectangles[17] = new Rect(mColorRectangles[16].right, bottomHeight, mRectFrame.right * 9 / 56 + mColorRectangles[16].right, mRectFrame.bottom);
				mColorRectangles[18] = new Rect(mColorRectangles[17].right, bottomHeight, mRectFrame.right * 3 / 14 + mColorRectangles[17].right, mRectFrame.bottom);
				mColorRectangles[19] = new Rect(mColorRectangles[18].right, bottomHeight, mRectFrame.right * 45 / 448 + mColorRectangles[18].right, mRectFrame.bottom);
				for (int i = 20; i < 25; i++)
				{
					mColorRectangles[i] = new Rect(mColorRectangles[i - 1].right, bottomHeight, mRectFrame.right * 15 / 448 + mColorRectangles[i - 1].right, mRectFrame.right);
				}
				mColorRectangles[25] = new Rect(mColorRectangles[24].right, bottomHeight, narrowColumnWidth + mColorRectangles[24].right, mRectFrame.bottom);
				mColorRectangles[26] = new Rect(mColorRectangles[25].right, bottomHeight, mRectFrame.right, mRectFrame.bottom);
			}
			else
			{
				int topHeight = mRectFrame.right * 5 / 12;
				int bottomHeight = mRectFrame.right / 4;
				int wideColumnWidth = mRectFrame.bottom / 8;
				int narrowColumnWidth = mRectFrame.bottom * 3 / 28;

				mColorRectangles[0] = new Rect(topHeight, 0, mRectFrame.bottom, wideColumnWidth);

				for (int i = 1; i < 8; i++)
				{
					mColorRectangles[i] = new Rect(topHeight, mColorRectangles[i - 1].bottom, mRectFrame.right, narrowColumnWidth + mColorRectangles[i - 1].bottom);
				}

				mColorRectangles[8] = new Rect(topHeight, mColorRectangles[7].bottom, mRectFrame.right, mRectFrame.bottom);

				for (int i = 0; i < 2; i++)
				{
					int middleLeft = mRectFrame.right * (4 - i) / 12;
					int middleRight = mRectFrame.right * (5 - i) / 12;
					mColorRectangles[i + 9] = new Rect(middleLeft, 0, middleRight, wideColumnWidth);
					mColorRectangles[i + 11] = new Rect(middleLeft, wideColumnWidth, middleRight, narrowColumnWidth + wideColumnWidth);
					mColorRectangles[i + 13] = new Rect(middleLeft, narrowColumnWidth * 7 + wideColumnWidth, middleRight, mRectFrame.bottom);
				}
				mColorRectangles[15] = new Rect(mRectFrame.right * 4 / 12, narrowColumnWidth + wideColumnWidth, mRectFrame.right * 5 / 12, narrowColumnWidth * 7 + wideColumnWidth);

				mGradientRect = new Rect(mRectFrame.right * 3 / 12, mColorRectangles[15].top, mColorRectangles[15].left, mColorRectangles[15].bottom);
				mGradient = new GradientDrawable(Orientation.TOP_BOTTOM, new int[] { 0xff050505, 0xfffdfdfd });
				mGradient.setBounds(mGradientRect);

				mColorRectangles[16] = new Rect(0, 0, bottomHeight, wideColumnWidth);
				mColorRectangles[17] = new Rect(0, mColorRectangles[16].bottom, bottomHeight, mRectFrame.bottom * 9 / 56 + mColorRectangles[16].bottom);
				mColorRectangles[18] = new Rect(0, mColorRectangles[17].bottom, bottomHeight, mRectFrame.bottom * 3 / 14 + mColorRectangles[17].bottom);
				mColorRectangles[19] = new Rect(0, mColorRectangles[18].bottom, bottomHeight, mRectFrame.bottom * 45 / 448 + mColorRectangles[18].bottom);
				for (int i = 20; i < 25; i++)
				{
					mColorRectangles[i] = new Rect(0, mColorRectangles[i - 1].bottom, bottomHeight, mRectFrame.bottom * 15 / 448 + mColorRectangles[i - 1].bottom);
				}
				mColorRectangles[25] = new Rect(0, mColorRectangles[24].bottom, bottomHeight, narrowColumnWidth + mColorRectangles[24].bottom);
				mColorRectangles[26] = new Rect(0, mColorRectangles[25].bottom, bottomHeight, mRectFrame.bottom);
			}
		}

		private void CreateBars()
		{
			if(mHorizontal)
			{
				int narrowColumnWidth = mRectFrame.right / 7;
				int wideColumnWidth = mRectFrame.right * 5 / 28;
				int narrowestColumnWidth = mRectFrame.right / 21;
				
				int topColumnHeight = mRectFrame.bottom *2/3;
				int middleColumnHeight = mRectFrame.bottom /12;
				
				mColorRectangles[0] = new Rect(0, 0, narrowColumnWidth, topColumnHeight);		
				for (int i = 1; i < 7; i++)
				{
					mColorRectangles[i] = new Rect(mColorRectangles[i - 1].right, 0, mColorRectangles[i - 1].right + narrowColumnWidth, topColumnHeight);		
				}

				mColorRectangles[7] = new Rect(0, mColorRectangles[0].bottom, narrowColumnWidth, mColorRectangles[0].bottom + middleColumnHeight);		
				for (int i = 8; i < 14; i++)
				{
					mColorRectangles[i] = new Rect(mColorRectangles[i - 1].right, mColorRectangles[7].top, mColorRectangles[i - 1].right + narrowColumnWidth, mColorRectangles[7].bottom);		
				}

				mColorRectangles[14] = new Rect(0, mColorRectangles[7].bottom, wideColumnWidth, mRectFrame.bottom);		
				for (int i = 15; i < 18; i++)
				{
					mColorRectangles[i] = new Rect(mColorRectangles[i - 1].right, mColorRectangles[14].top, mColorRectangles[i - 1].right + wideColumnWidth, mRectFrame.bottom);		
				}

				mColorRectangles[18] = new Rect(mColorRectangles[17].right, mColorRectangles[17].top, mColorRectangles[17].right + narrowestColumnWidth, mRectFrame.bottom);		
				for (int i = 19; i < 21; i++)
				{
					mColorRectangles[i] = new Rect(mColorRectangles[i - 1].right, mColorRectangles[14].top, mColorRectangles[i - 1].right + narrowestColumnWidth, mRectFrame.bottom);		
				}
				mColorRectangles[21] = new Rect(mColorRectangles[20].right, mColorRectangles[17].top, mColorRectangles[6].right, mRectFrame.bottom);		

			}
			else
			{
				int narrowColumnWidth = mRectFrame.bottom / 7;
				int wideColumnWidth = mRectFrame.bottom * 5 / 28;
				int narrowestColumnWidth = mRectFrame.bottom / 21;
				
				int topColumnHeight = mRectFrame.right /3;
				int middleColumnHeight = mRectFrame.right /12;
				
				mColorRectangles[0] = new Rect(topColumnHeight, 0, mRectFrame.right, narrowColumnWidth);		
				for (int i = 1; i < 7; i++)
				{
					mColorRectangles[i] = new Rect(topColumnHeight, mColorRectangles[i - 1].bottom, mRectFrame.right, mColorRectangles[i - 1].bottom + narrowColumnWidth);		
				}

				mColorRectangles[7] = new Rect(mColorRectangles[0].left + middleColumnHeight, 0, mColorRectangles[0].left, narrowColumnWidth);		
				for (int i = 8; i < 14; i++)
				{
					mColorRectangles[i] = new Rect(mColorRectangles[7].left, mColorRectangles[i - 1].bottom, mColorRectangles[7].right, mColorRectangles[i - 1].bottom + narrowColumnWidth);		
				}

				mColorRectangles[14] = new Rect(0, 0, mColorRectangles[7].right,  wideColumnWidth);		
				for (int i = 15; i < 18; i++)
				{
					mColorRectangles[i] = new Rect(0, mColorRectangles[i - 1].bottom, mColorRectangles[7].right, mColorRectangles[i - 1].bottom + wideColumnWidth);		
				}

				mColorRectangles[18] = new Rect(0, mColorRectangles[17].bottom, mColorRectangles[7].right, mColorRectangles[17].bottom + narrowestColumnWidth);		
				for (int i = 19; i < 21; i++)
				{
					mColorRectangles[i] = new Rect(0, mColorRectangles[i - 1].bottom, mColorRectangles[7].right, mColorRectangles[i - 1].bottom + narrowestColumnWidth);		
				}
				mColorRectangles[21] = new Rect(0, mColorRectangles[20].bottom, mColorRectangles[7].right, mRectFrame.bottom);		
				
			}
		}

		private void CreateEbu()
		{
			if(mHorizontal)
			{
				int narrowColumnWidth = mRectFrame.right / 8;

				mColorRectangles[0] = new Rect(0, 0, narrowColumnWidth, mRectFrame.bottom);		
				for (int i = 1; i < 8; i++)
				{
					mColorRectangles[i] = new Rect(mColorRectangles[i - 1].right, 0, mColorRectangles[i - 1].right + narrowColumnWidth, mRectFrame.bottom);		
				}

			}
			else
			{
				int narrowColumnWidth = mRectFrame.bottom / 8;


				mColorRectangles[0] = new Rect(0, 0, mRectFrame.right, narrowColumnWidth);
				for (int i = 1; i < 8; i++)
				{
					mColorRectangles[i] = new Rect(0, mColorRectangles[i - 1].bottom, mRectFrame.right, narrowColumnWidth + mColorRectangles[i - 1].bottom);
				}
			}
		}
	}
}