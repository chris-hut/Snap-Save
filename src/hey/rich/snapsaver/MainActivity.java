package hey.rich.snapsaver;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	/** onCLickListeners */
	private Button buttonPicture;
	private Button buttonVideo;
	private Button buttonBoth;

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

	private void changeDirectory(Context context) {
		// Create dialog
		AlertDialog.Builder changeDirectoryDialogBuilder = new AlertDialog.Builder(
				context);

		// TODO: Convert to R.string
		changeDirectoryDialogBuilder.setTitle("Change Directory");

		changeDirectoryDialogBuilder.setMessage("Enter new directory:")
				.setCancelable(true);
		
		LayoutInflater dialogCreaterInflater = getLayoutInflater();

		View dialogLayout = dialogCreaterInflater.inflate(R.layout.change_directory, (ViewGroup) getCurrentFocus());
		changeDirectoryDialogBuilder.setView(dialogLayout);
		
		AlertDialog dialogDirectoryChanger = changeDirectoryDialogBuilder.create();
		dialogDirectoryChanger.show();

	}
}
