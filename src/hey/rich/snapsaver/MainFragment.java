package hey.rich.snapsaver;

import wei.mark.standout.StandOutWindow;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MainFragment extends Fragment {

	// Strings
	private String mStorageLocation;
	public static final String TITLE = "Snap Saver";
	private static final String LOG_TAG = "MainFragment";
	private static final String SNAPCHAT_PACKAGE_NAME = "com.snapchat.android";
	private static final String PREFS_NAME = "MAIN_FRAGMENT_SHARED_PREFERENCES";
	private static final String SHARED_PREF_STORAGE_SNAPCHAT = "SNAPCHAT_STORAGE";
	private static final String SHARED_PREF_START_SNAPCHAT = "SNAPCHAT_START_ON_FLOAT";

	// Flags
	private static final boolean DEBUG_LOG_FLAG = true;
	private static boolean mStartSnapChat = false;

	// Should be updated from the main activity
	private boolean mHaveRoot = false;

	// Callback to calling activity
	private MainFragmentListener mCallback;

	// Container activity must implement this interface
	public interface MainFragmentListener {
		public void onPassData();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Ensure that container activity has implemented callback interface
		try {
			mCallback = (MainFragmentListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement MainFragmentListener.");
		}
		
	}

	private FileManager mFileManager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.main, container, false);

		// We want some options!
		setHasOptionsMenu(true);

		// Update root status
		mHaveRoot = MainActivity.getRootStatus();

		Button buttonPicture = (Button) view.findViewById(R.id.button_copy_picture);
		Button buttonVideo = (Button) view.findViewById(R.id.button_copy_video);
		Button buttonBoth = (Button) view.findViewById(R.id.button_both);
		Button buttonFloater = (Button) view.findViewById(R.id.button_floater);

		buttonPicture.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Copy pictures
				if (mHaveRoot) {
					mFileManager.copySnapChatPictures(getActivity()
							.getApplicationContext());
				} else {
					if (DEBUG_LOG_FLAG)
						Log.d(LOG_TAG,
								"Even though picture button should have been disabled we clicked it.");
				}
			}
		});

		buttonVideo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Copy videos
				if (mHaveRoot) {
					mFileManager.copySnapChatVideos(getActivity()
							.getApplicationContext());
				} else {
					if (DEBUG_LOG_FLAG)
						Log.d(LOG_TAG,
								"Even though video button should have been disabled we clicked it.");
				}
			}
		});

		buttonBoth.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Copy pics and videos
				if (mHaveRoot) {
					mFileManager.copySnapChatPictures(getActivity()
							.getApplicationContext());
					mFileManager.copySnapChatVideos(getActivity()
							.getApplicationContext());
				} else {
					if (DEBUG_LOG_FLAG)
						Log.d(LOG_TAG,
								"Even though both button should have been disabled we somehow clicked it.");
				}
			}
		});

		buttonFloater.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Launch that floater
				closeFloatingWindow();
				if (mHaveRoot) {
					// Launch the snapchat application
					if (mStartSnapChat) {
						if (DEBUG_LOG_FLAG)
							Log.d(LOG_TAG, "Trying to start Snapchat app");
						// Start snapchat app
						PackageManager pm = getActivity()
								.getApplicationContext().getPackageManager();
						Intent startSnapChatIntent = pm
								.getLaunchIntentForPackage(SNAPCHAT_PACKAGE_NAME);
						if (startSnapChatIntent != null) {
							getActivity().getApplicationContext()
									.startActivity(startSnapChatIntent);
						}

					}

					launchFloatingWindow();
				} else {
					if (DEBUG_LOG_FLAG)
						Log.d(LOG_TAG,
								"Even though both button should be disabled we somehow clicked it.");
				}
			}
		});

		if(DEBUG_LOG_FLAG) Log.d(LOG_TAG, "We got roots? for the buttons: " + mHaveRoot);
		buttonBoth.setEnabled(mHaveRoot);
		buttonFloater.setEnabled(mHaveRoot);
		buttonPicture.setEnabled(mHaveRoot);
		buttonVideo.setEnabled(mHaveRoot);
		
		return view;
	}

	/**
	 * Launches the floating window.
	 * 
	 * TODO: Launch different kinds of floating windows.
	 */
	private void launchFloatingWindow() {
		StandOutWindow.show(getActivity(), FloatingWindow.class,
				StandOutWindow.DEFAULT_ID);
	}

	/**
	 * Closes the floating window.
	 * 
	 * TODO: Choose what window you want
	 */
	private void closeFloatingWindow() {
		StandOutWindow.closeAll(getActivity(), FloatingWindow.class);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.main, menu);
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

	@Override
	public void onPause() {
		super.onPause();
		// TODO: No more hardcoded strings
		SharedPreferences settings = getActivity().getSharedPreferences(
				PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		// TODO: No strings at all should be hardcoded
		editor.putBoolean(SHARED_PREF_START_SNAPCHAT, mStartSnapChat);
		editor.putString(SHARED_PREF_STORAGE_SNAPCHAT, mStorageLocation);
		if (DEBUG_LOG_FLAG)
			Log.d(LOG_TAG, "Just saved mStartSnapChat as: " + mStartSnapChat);
		// Commit the preferences
		editor.commit();
	}

	@Override
	public void onResume() {
		super.onResume();
		SharedPreferences settings = getActivity().getSharedPreferences(
				PREFS_NAME, 0);
		mStartSnapChat = settings.getBoolean(SHARED_PREF_START_SNAPCHAT, false);
		mStorageLocation = settings.getString(SHARED_PREF_STORAGE_SNAPCHAT,
				mStorageLocation);
		if (DEBUG_LOG_FLAG)
			Log.d(LOG_TAG, "Just loaded mStartSnapChat as: " + mStartSnapChat);

		// Creating new filemanager
		mFileManager = new FileManager();
	}
}
