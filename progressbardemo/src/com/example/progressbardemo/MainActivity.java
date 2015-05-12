package com.example.progressbardemo;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.ProgressDialog;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
	Button btnStartProgress;
	ProgressDialog progressBar;
	private int progressBarStatus = 0;
	private Handler progressBarHandler = new Handler();
 
	private long fileSize = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		addListenerOnButton();
	}


	public void addListenerOnButton() {
 
		btnStartProgress = (Button) findViewById(R.id.btnStartProgress);
		btnStartProgress.setOnClickListener(
                 new OnClickListener() {
 
		   @Override
		   public void onClick(View v) {
 
			// prepare for a progress bar dialog
			progressBar = new ProgressDialog(v.getContext());
			progressBar.setCancelable(true);
			progressBar.setMessage("File downloading ...");
			progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressBar.setProgress(0);
			progressBar.setMax(100);
			progressBar.show();
 
			//reset progress bar status
			progressBarStatus = 0;
 
			//reset filesize
			fileSize = 0;
 
			new Thread(new Runnable() {
			  public void run() {
				while (progressBarStatus < 100) {
 
				  // process some tasks
				  progressBarStatus = doSomeTasks();
 
				  // your computer is too fast, sleep 1 second
				  try {
					Thread.sleep(1000);
				  } catch (InterruptedException e) {
					e.printStackTrace();
				  }
 
				  // Update the progress bar
				  progressBarHandler.post(new Runnable() {
					public void run() {
					  progressBar.setProgress(progressBarStatus);
					}
				  });
				}
 
				// ok, file is downloaded,
				if (progressBarStatus >= 100) {
 
					// sleep 2 seconds, so that you can see the 100%
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
 
					// close the progress bar dialog
					progressBar.dismiss();
				}
			  }
		       }).start();
 
	           }
 
                });
 
        }
 
	// file download simulator... a really simple
	public int doSomeTasks() {
 
		while (fileSize <= 1000000) {
 
			fileSize++;
 
			if (fileSize == 200000) {
				return 20;
			} else if (fileSize == 400000) {
				return 40;
			} else if (fileSize == 600000) {
				return 60;
			}
			else if (fileSize == 800000) {
				return 80;
			}
			else if (fileSize == 1000000) {
				return 100;
			}
			// ...add your own
 
		}
 
		return 100;
 
	}
}
