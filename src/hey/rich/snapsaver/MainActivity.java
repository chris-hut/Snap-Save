package hey.rich.snapsaver;

import java.io.File;
import java.io.FileFilter;

import wei.mark.standout.StandOutWindow;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	// On Clickers
	private Button buttonPicture;
	private Button buttonVideo;
	private Button buttonBoth;
	private Button buttonFloater;

	// Strings
	private String storageLocation;
	private static final String STRING_PREFERENCES = "stringPrefs";

	// Shared Preferences
	private SharedPreferences prefs;
	private SharedPreferences.Editor prefEditor;

	// Booleans
	/** Whether pictures and videos should be in different directories */
	private boolean splitDirectory;

	// Log constants
	/** Flag for Debug logs. */
	private final boolean LOCAL_LOGD = true;
	/** Flag for Verbose logs. */
	private final boolean LOCAL_LOGV = true;

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
				copySnapChatDirectory(getString(R.string.location_pictures),
						storageLocation);

				// Rename pictures
				// TODO: Create method to check files to rename
			}
		});

		buttonVideo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Copy videos
				copySnapChatDirectory(getString(R.string.location_videos),
						storageLocation);

				// Rename videos
				// TODO: Create method to check files to rename
			}
		});

		buttonBoth.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Copy pics and videos
				copySnapChatDirectory(getString(R.string.location_pictures),
						storageLocation);
				copySnapChatDirectory(getString(R.string.location_videos),
						storageLocation);

				// Rename pics and videos
				// TODO: create method to check files to rename
			}
		});

		buttonFloater.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Launch that floater
				closeFloatingWindow();
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
	private void closeFloatingWindow(){
		StandOutWindow.closeAll(this, FloatingWindow.class);
	}

	private void copySnapChatDirectory(String from, String to) {
		
		//mFileManager.copySUDirectory(from, to, this);
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
		case R.id.settings_change_directory: {
			// Change directory
			changeDirectory(this);
			return true;
		}
		case R.id.settings_check_box_split_directory: {
			if (item.isChecked()) {
				splitDirectory = false;
				if (LOCAL_LOGV)
					Log.v("Snaps", "Split directory checkbox was unchecked");
				item.setChecked(false);
			} else {
				splitDirectory = true;
				if (LOCAL_LOGV)
					Log.v("Snaps", "Split directory checkbox was checked");
				item.setChecked(true);
			}
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
		prefEditor.putBoolean("SPLIT_DIRECTORY", splitDirectory);
		prefEditor.commit();
	}

	@Override
	protected void onResume() {
		super.onResume();
		storageLocation = prefs.getString("storageLocation",
				getString(R.string.default_location));
		if (LOCAL_LOGV)
			Log.v("Snaps", "storageLocation set to: " + storageLocation);
		splitDirectory = prefs.getBoolean("SPLIT_DIRECTORY", false);

		// Creating new filemanager
		mFileManager = new FileManager();

	}
}
