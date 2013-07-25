package hey.rich.snapsaver;

import wei.mark.standout.StandOutWindow;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

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

	// Log constants
	/** Flag for Debug logs. */
	private final boolean DEBUG_LOG_FLAG =  true;

	/** File Manager */
	private FileManager mFileManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS); // Must be
																		// before
																		// setContentView
		setContentView(R.layout.activity_main);

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
					if(DEBUG_LOG_FLAG) Log.d(LOG_TAG, "Trying to start Snapchat app");
					// Start snapchat app
					PackageManager pm = getApplicationContext()
							.getPackageManager();
					Intent startSnapChatIntent = pm
							.getLaunchIntentForPackage(SNAPCHAT_PACKAGE_NAME);
					if(startSnapChatIntent != null){
						getApplicationContext().startActivity(startSnapChatIntent);
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
	 * Creates a dialog to allow the user to change their current directory
	 * Modified from mkoyong:
	 * http://www.mkyong.com/android/android-custom-dialog-example/
	 */
	private void changeDirectory(Context context) {
		final Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.change_directory);
		dialog.setTitle(R.string.dialog_new_directory);

		// Set the custom dialog components
		final EditText text = (EditText) dialog
				.findViewById(R.id.dialog_edit_text);

		// Setting text to be what every it is
		text.setText(storageLocation);
		Button saveButton = (Button) dialog
				.findViewById(R.id.dialog_positive_button);
		Button cancelButton = (Button) dialog
				.findViewById(R.id.dialog_negative_button);

		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO: Save the text
				storageLocation = text.getText().toString();

				// Dismiss the dialog
				dialog.dismiss();

				// Set the EditText to be the storageLocation text
				text.setText(storageLocation);
			}
		});

		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Dismiss the dialog
				dialog.dismiss();
			}
		});

		dialog.show();
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
}
