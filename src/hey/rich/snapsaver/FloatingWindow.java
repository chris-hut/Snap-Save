package hey.rich.snapsaver;

import wei.mark.standout.StandOutWindow;
import wei.mark.standout.constants.StandOutFlags;
import wei.mark.standout.ui.Window;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

public class FloatingWindow extends StandOutWindow {

	// The buttons
	private static Button buttonPictures = null;
	private static Button buttonVideos = null;
	private static Button buttonBoth = null;

	// My file manager
	private static FileManager mFileManager;

	@Override
	public String getAppName() {
		return "FloatingSnapSaverWindow";
	}

	@Override
	public int getAppIcon() {
		return android.R.drawable.ic_menu_close_clear_cancel;
	}

	@Override
	public void createAndAttachView(int id, FrameLayout frame) {
		// Create a new layout from body.xml
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.floating_window_layout, frame,
				true);

		// Set up a button or two
		buttonPictures = (Button) view
				.findViewById(R.id.button_floating_pictures);
		buttonVideos = (Button) view.findViewById(R.id.button_floating_videos);
		buttonBoth = (Button) view.findViewById(R.id.button_floating_both);

		// make my file manager
		mFileManager = new FileManager();

		setTheOnClickListeners();
	}

	/** Sets the onClickListeners for the buttons */
	private void setTheOnClickListeners() {
		if (buttonPictures != null && buttonVideos != null
				&& buttonBoth != null) {
			buttonPictures.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// Copy them pictures
					mFileManager.copySnapChatPictures(getApplicationContext());
					// Let the world know we are finished
					Toast.makeText(getBaseContext(),
							getString(R.string.toast_completed_pictures),
							Toast.LENGTH_SHORT).show();
				}
			});

			buttonVideos.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// Copy them videos
					mFileManager.copySnapChatVideos(getApplicationContext());

					// Rename them videos
					//mFileManager.renameSnapChatVideos();
					Toast.makeText(getBaseContext(),
							getString(R.string.toast_completed_videos),
							Toast.LENGTH_SHORT).show();
				}
			});

			buttonBoth.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// Copy both of them!
					mFileManager.copySnapChatPictures(getApplicationContext());
					mFileManager.copySnapChatVideos(getApplicationContext());

					// Rename all of those things
					//mFileManager.renameSnapChatPictures();
					//mFileManager.renameSnapChatVideos();
					Toast.makeText(getBaseContext(),
							getString(R.string.toast_completed_both),
							Toast.LENGTH_SHORT).show();
				}
			});
		} else {
			// Something is really wrong
		}
	}

	// the window will be centered
	@Override
	public StandOutLayoutParams getParams(int id, Window window) {
		return new StandOutLayoutParams(id, 250, 300,
				StandOutLayoutParams.CENTER, StandOutLayoutParams.CENTER);
	}

	// move the window by dragging the view
	@Override
	public int getFlags(int id) {
		return super.getFlags(id) | StandOutFlags.FLAG_BODY_MOVE_ENABLE
				| StandOutFlags.FLAG_WINDOW_FOCUSABLE_DISABLE;
	}

	@Override
	public String getPersistentNotificationMessage(int id) {
		return "Click to close the Window";
	}

	@Override
	public Intent getPersistentNotificationIntent(int id) {
		return StandOutWindow.getCloseIntent(this, FloatingWindow.class, id);
	}
}
