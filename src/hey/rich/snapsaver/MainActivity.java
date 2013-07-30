package hey.rich.snapsaver;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;

import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

import eu.chainfire.libsuperuser.Shell;

public class MainActivity extends FragmentActivity {

	private static boolean mHaveRoot = false;

	// Log constants
	/** Flag for Debug logs. */
	private final boolean DEBUG_LOG_FLAG = true;
	private static final String LOG_TAG = "MainActivity";

	// Fragment stuff
	SnapSaverFragmentAdapter mAdapter;
	ViewPager mPager;
	PageIndicator mIndicator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if(DEBUG_LOG_FLAG) Log.d(LOG_TAG, "Started snapsaver.");
		
		// TODO: figure out a nice system for calling root and playing with the result - callbacks?
		//(new GetRoot()).execute();
		
		mAdapter = new SnapSaverFragmentAdapter(getSupportFragmentManager());
		
		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);
		
		mIndicator = (TitlePageIndicator) findViewById(R.id.indicator);
		mIndicator.setViewPager(mPager);

	}

	/**
	 * Updates the current status of the application regarding root status
	 * 
	 * @param root
	 *            true if we have root access, false otherwise
	 */
	private void updateRootStatus(boolean root) {
		mHaveRoot = root;

		if (mHaveRoot) {
			// We have root access
			// TODO: No strings hardcoded
			Toast.makeText(getBaseContext(),
					getString(R.string.toast_successfully_got_root),
					Toast.LENGTH_SHORT);

			if (DEBUG_LOG_FLAG)
				Log.d(LOG_TAG, "Got root access, enabling buttons.");

		} else {
			// We dont have root
			if (DEBUG_LOG_FLAG)
				Log.d(LOG_TAG, "Don't have root, disabling buttons.");
			new AlertDialog.Builder(this)
					.setTitle(getString(R.string.dialog_no_root_title))
					.setMessage(getString(R.string.dialog_no_root_message))
					.setPositiveButton(
							getString(R.string.dialog_no_root_positive_button),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
								}
							}).show();
		}
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
