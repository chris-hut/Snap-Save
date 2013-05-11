package hey.rich.snapsaver;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	/** onCLickListeners */
	private Button buttonPicture;
	private Button buttonVideo;
	private Button buttonBoth;
	
	/** Strings */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		buttonPicture = (Button) findViewById(R.id.button_copy_picture);
		buttonVideo = (Button) findViewById(R.id.button_copy_video);
		buttonBoth = (Button) findViewById(R.id.button_both);
		

		this.buttonOnClick();
	}

	/** Sets up the buttons onClickListeners */
	private void buttonOnClick() {
		buttonPicture.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Copy Pictures
				copyDirectory("picture");
				// Rename Pictures
				renameAllFiles("/pictures/");
			}
		});

		buttonVideo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Copy videos
				copyDirectory("video");
				// Rename videos
				renameAllFiles("/videos/");
			}
		});

		buttonBoth.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Copy videos and pictures
			}
		});
	}

	/** Copy files to new directory */
	private void copyDirectory(String type) {
		String toastText = "";

		if (type.equals("picture")) {
			toastText = getString(R.string.progress_1_copy_picture)
					+ getString(R.string.location_save)
					+ getString(R.string.location_pictures);
		} else if (type.equals("video")) {
			toastText = getString(R.string.progress_1_copy_video)
					+ getString(R.string.location_save)
					+ getString(R.string.location_videos);
		}

		// Toast the copy
		Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT)
				.show();
		// Do copy
	}

	/** Rename all of the files */
	private void renameAllFiles(String directory) {
		String toastText = "";

		if (directory.equals("/pictures/")) {
			toastText = getString(R.string.progress_2_rename_picture)
					+ getString(R.string.location_save)
					+ getString(R.string.location_pictures);
		} else if (directory.equals("/videos/")) {
			toastText = getString(R.string.progress_2_rename_video)
					+ getString(R.string.location_save)
					+ getString(R.string.location_videos);
		}

		// Toast the copy
		Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT)
				.show();
		// Do copy
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
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	/**
	 * Creates a dialog to allow the user to change their current directory
	 * 
	 * Modified from mkoyong:
	 * http://www.mkyong.com/android/android-custom-dialog-example/
	 * */
	private void changeDirectory(Context context)
	 {
		 final Dialog dialog = new Dialog(context);
		 dialog.setContentView(R.layout.change_directory);
		 dialog.setTitle(R.string.dialog_new_directory);
		 
		 // Set the custom dialog components
		 EditText text = (EditText) dialog.findViewById(R.id.dialog_edit_text);
		 // TODO: Should the hint be the defualt save location or whatever they currently have as save location
		 Button saveButton = (Button) dialog.findViewById(R.id.dialog_positive_button);
		 Button cancelButton = (Button) dialog.findViewById(R.id.dialog_negative_button);
		 
		 saveButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO: Save the text
				
				// Dismiss the dialog
				dialog.dismiss();
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
	

}
