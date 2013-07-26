package hey.rich.snapsaver;

import wei.mark.standout.StandOutWindow;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;
import eu.chainfire.libsuperuser.Shell;

public class MainActivity extends Activity {

	// On Clickers
	private Button buttonPicture;
	private Button buttonVideo;
	private Button buttonBoth;
	private Button buttonFloater;

	// Strings
	private String storageLocation;
	private static final String STRING_PREFERENCES = "stringPrefs";
	private static final String SNAPCHAT_PACKAGE_NAME = "com.snapchat.android";
	private static final String LOG_TAG = "MainActivity";

	// Shared Preferences
	private SharedPreferences prefs;
	private SharedPreferences.Editor prefEditor;

	// Flags
	/** Flag for starting snapchat when floating window is opened. */
	private static boolean mStartSnapChat;
	/** Flag declaring if we have root */
	private static boolean mHaveRoot = false;

	// Log constants
	/** Flag for Debug logs. */
	private final boolean DEBUG_LOG_FLAG = true;

	/** File Manager */
	private FileManager mFileManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS); // Must be
																		// before
																		// setContentView
		setContentView(R.layout.activity_main);

		// Check for root if we don't already have it
		(new GetRoot()).execute();

		prefs = getSharedPreferences(STRING_PREFERENCES, MODE_PRIVATE);
		prefEditor = prefs.edit();

		buttonPicture = (Button) findViewById(R.id.button_copy_picture);
		buttonVideo = (Button) findViewById(R.id.button_copy_video);
		buttonBoth = (Button) findViewById(R.id.button_both);
		buttonFloater = (Button) findViewById(R.id.button_floater);

		this.buttonOnClick();
	}

	/** Sets up the buttons onClickListeners */
	private void buttonOnClick() {
		buttonPicture.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Copy pictures
				mFileManager.copySnapChatPictures(getApplicationContext());
			}
		});

		buttonVideo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Copy videos
				mFileManager.copySnapChatVideos(getApplicationContext());
			}
		});

		buttonBoth.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Copy pics and videos
				mFileManager.copySnapChatPictures(getApplicationContext());
				mFileManager.copySnapChatVideos(getApplicationContext());
			}
		});

		buttonFloater.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Launch that floater
				closeFloatingWindow();

				// Launch the snapchat application
				if (mStartSnapChat) {
					if (DEBUG_LOG_FLAG)
						Log.d(LOG_TAG, "Trying to start Snapchat app");
					// Start snapchat app
					PackageManager pm = getApplicationContext()
							.getPackageManager();
					Intent startSnapChatIntent = pm
							.getLaunchIntentForPackage(SNAPCHAT_PACKAGE_NAME);
					if (startSnapChatIntent != null) {
						getApplicationContext().startActivity(
								startSnapChatIntent);
					}

				}
				launchFloatingWindow();

			}
		});
	}

	/**
	 * Launches the floating window.
	 * 
	 * TODO: Launch different kinds of floating windows.
	 */
	private void launchFloatingWindow() {
		StandOutWindow.show(this, FloatingWindow.class,
				StandOutWindow.DEFAULT_ID);
	}

	/**
	 * Closes the floating window.
	 * 
	 * TODO: Choose what window you want
	 */
	private void closeFloatingWindow() {
		StandOutWindow.closeAll(this, FloatingWindow.class);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.menu_check_box_start_snapchat_on_float: {
			// Toggle the checkbox

			if (DEBUG_LOG_FLAG)
				Log.d(LOG_TAG,
						"Checkbox checked, current value: " + item.isChecked());

			boolean alreadyChecked = item.isChecked();

			item.setChecked(!alreadyChecked);

			// Set the value of flag
			mStartSnapChat = item.isChecked();

			return true;
		}
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Updates the current status of the application regarding root status
	 * 
	 * @param root
	 *            true if we have root access, false otherwise
	 */
	private void updateRootStatus(boolean root) {
		mHaveRoot = root;

		buttonBoth.setEnabled(mHaveRoot);
		buttonFloater.setEnabled(mHaveRoot);
		buttonPicture.setEnabled(mHaveRoot);
		buttonVideo.setEnabled(mHaveRoot);

		if (mHaveRoot) {
			// We have root access
			// TODO: No strings hardcoded
			Toast.makeText(getBaseContext(), "Got root access.",
					Toast.LENGTH_SHORT);

			if (DEBUG_LOG_FLAG)
				Log.d(LOG_TAG, "Got root access, enabling buttons.");

		} else {
			// We dont have root
			if (DEBUG_LOG_FLAG)
				Log.d(LOG_TAG, "Don't have root, disabling buttons.");
			// TODO: Throw up a dialog
			new AlertDialog.Builder(this)
					.setTitle("Error getting root.")
					.setMessage(
							"Sorry I wasn't able to get root access. Check if you have root access on your device and restart this application.")
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
								}
							}).show();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		prefs = this.getSharedPreferences("hey.rich.SnapSaver",
				Context.MODE_PRIVATE);
		prefEditor.putString("STORAGE_LOCATION", storageLocation);
		prefEditor.putBoolean("START_SNAPCHAT", mStartSnapChat);
		prefEditor.commit();
	}

	@Override
	protected void onResume() {
		super.onResume();
		storageLocation = prefs.getString("storageLocation",
				getString(R.string.default_location));
		if (DEBUG_LOG_FLAG)
			Log.d("Snaps", "storageLocation set to: " + storageLocation);
		mStartSnapChat = prefs.getBoolean("START_SNAPCHAT", false);

		// Creating new filemanager
		mFileManager = new FileManager();

	}

	/** BackgroundTask to ask for SU permissions */
	private class GetRoot extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPostExecute(Boolean root) {
			updateRootStatus(root);
		}

		@Override
		protected Boolean doInBackground(Void... arg0) {
			return Boolean.valueOf(Shell.SU.available());
		}

	}

}
