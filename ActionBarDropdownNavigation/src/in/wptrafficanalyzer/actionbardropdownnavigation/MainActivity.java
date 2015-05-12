package in.wptrafficanalyzer.actionbardropdownnavigation;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class MainActivity extends Activity {
    
	
	/** An array of strings to populate dropdown list */
	String[] actions = new String[] {
		"Bookmark",
		"Subscribe",
		"Share"
	};
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        
        /** Create an array adapter to populate dropdownlist */
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, actions);
        
        
        /** Enabling dropdown list navigation for the action bar */
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        
        /** Defining Navigation listener */
        ActionBar.OnNavigationListener navigationListener = new OnNavigationListener() {
			
			@Override
			public boolean onNavigationItemSelected(int itemPosition, long itemId) {
				Toast.makeText(getBaseContext(), "You selected : " + actions[itemPosition]  , Toast.LENGTH_SHORT).show();
				return false;
			}
		};
	
		/** Setting dropdown items and item navigation listener for the actionbar */
		getActionBar().setListNavigationCallbacks(adapter, navigationListener);        
    }
}