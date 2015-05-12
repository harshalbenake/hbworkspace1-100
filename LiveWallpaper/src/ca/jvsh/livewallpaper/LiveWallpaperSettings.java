package ca.jvsh.livewallpaper;

import ca.jvsh.livewallpaper.R;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class LiveWallpaperSettings extends PreferenceActivity
	implements SharedPreferences.OnSharedPreferenceChangeListener
{
	@Override
	protected void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		getPreferenceManager().setSharedPreferencesName(LiveWallpaper.SHARED_PREFS_NAME);
		addPreferencesFromResource(R.xml.livewallpaper_settings);
		getPreferenceManager().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
	}

	@Override
	protected void onDestroy()
	{
		getPreferenceManager().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
		super.onDestroy();
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key)
	{
	}
}