package hey.rich.snapsaver;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.ResultReceiver;
import android.util.Log;
import eu.chainfire.libsuperuser.Shell;

public class FileManager {

	/** Debug log flag. */
	private static final boolean DEBUG_LOG_FLAG = true;

	/** Length of the snapchat picture extensions */
	private static final int SNAPCHAT_PICTURE_EXTENSION_LENGTH = 8;
	/** Log Tag */
	private static final String LOG_TAG = "FileManager";

	/** Location of snap chat pictures storage directory */
	private final String mPictureStorageDirectory = "/data/data/com.snapchat.android/cache/received_image_snaps/";

	/** Location of snap chat videos storage directory */
	private final String mVideoStorageDirectory = "/data/media/0/Android/data/com.snapchat.android/cache/received_video_snaps/";

	/** Location of new snap chat picture save directory */
	private String mPictureSaveDirectory;

	/** Location of new snap chat video save directory */
	private String mVideoSaveDirectory;

	public FileManager() {
		String root = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		setVideoSaveDirectory(root + "/snaps/");
		setPictureSaveDirectory(root + "/snaps/");
	}

	/**
	 * Creates the FileManager object with the one argument constructor. Sets
	 * the default save directories and the context to what is passed in.
	 * 
	 * @param context
	 *            The Context for the BackGroundTask
	 */
	public FileManager(Context context) {
		super();
	}

	/**
	 * Creates the FileManager object with the pictureDirectory, videoDirectory,
	 * and context.
	 * 
	 * @param pictureSaveDirectory
	 *            directory to save pictures in
	 * @param videoSaveDirectory
	 *            directory to save videos in
	 * @param context
	 *            Context for the BackGroundTask
	 */
	public FileManager(String pictureSaveDirectory, String videoSaveDirectory) {
		setVideoSaveDirectory(videoSaveDirectory);
		setPictureSaveDirectory(pictureSaveDirectory);
	}

	/**
	 * Copies snapChat pictures to the correct directory.
	 * 
	 * @boolean true iff the copy was successful
	 */
	public boolean copySnapChatPictures(Context context) {
		copySUDirectory(mPictureStorageDirectory, mPictureSaveDirectory,
				context);
		return true;
	}

	/**
	 * Copies snapChat videos to the correct directory
	 * 
	 * @boolean true iff the copy was successful
	 */
	public boolean copySnapChatVideos(Context context) {
		copySUDirectory(mVideoStorageDirectory, mVideoSaveDirectory, context);
		return true;
	}

	/**
	 * Renames snapChat pictures in the correct directory that follow the
	 * correct regex pattern.
	 * 
	 * @return true iff the rename operation is successful
	 */
	public boolean renameSnapChatPictures() {
		return renameFilesFromDirectory(mFilterPictures, mPictureSaveDirectory,
				SNAPCHAT_PICTURE_EXTENSION_LENGTH);
	}

	/** FileFilter that parses snap chat pictures */
	private FileFilter mFilterPictures = new FileFilter() {
		@Override
		public boolean accept(File pathName) {
			String fName = pathName.getAbsolutePath();
			// File must exist
			if (!pathName.isFile()) {
				if (DEBUG_LOG_FLAG) {
					Log.d(LOG_TAG, "File does not exist.");
					Log.d(LOG_TAG, "File name was: " + fName);
				}
				return false;
			}

			// File must end in .nomedia
			if (!pathName.getAbsolutePath().substring(fName.length() - 8)
					.equals(".nomedia")) {
				if (DEBUG_LOG_FLAG) {
					Log.d(LOG_TAG, "File did not end in .nomedia.");
					Log.d(LOG_TAG, "File name was: " + fName);
				}
				return false;
			}
			// File must be like: h1a81hurcs00h1368487198510.jpg.nomedia
			if (!pathName.getAbsolutePath().matches(
					mPictureSaveDirectory
							+ "h1a81hurcs00h[0-9]{13,}.jpg.nomedia")) {
				if (DEBUG_LOG_FLAG) {
					Log.d(LOG_TAG, "File was not good looking.");
					Log.d(LOG_TAG, "File name was: " + fName);
					Log.d(LOG_TAG, "File should look more like: "
							+ mPictureSaveDirectory
							+ "h1a81hurcs00h[0-9]{13,}.jpg.nomedia");
				}
				return false;
			}
			return true;
		}
	};

	/** FileFilter that parses snapchat vidoes */
	private FileFilter mFilterVideos = new FileFilter() {

		@Override
		public boolean accept(File pathName) {
			String fName = pathName.getAbsolutePath();
			// File must exist
			if (!pathName.isFile()) {
				if (DEBUG_LOG_FLAG) {
					Log.d(LOG_TAG, "File does not exist.");
					Log.d(LOG_TAG, "File name was: " + fName);
				}
				return false;
			}

			// File must end in .nomedia
			if (!pathName.getAbsolutePath().substring(fName.length() - 8)
					.equals(".nomedia")) {
				if (DEBUG_LOG_FLAG) {
					Log.d(LOG_TAG, "File did not end in .nomedia.");
					Log.d(LOG_TAG, "File name was: " + fName);
				}
				return false;
			}
			// File must be like: sesrh_dlw211374551611445.mp4.nomedia
			if (!fName.matches(mVideoSaveDirectory
					+ "sesrh_dlw[0-9]{15,}.mp4.nomedia")) {
				if (DEBUG_LOG_FLAG) {
					Log.d(LOG_TAG, "File was not good looking.");
					Log.d(LOG_TAG, "File name was: " + fName);
					Log.d(LOG_TAG, "File should look more like: "
							+ mVideoSaveDirectory
							+ "sesrh_dlw[0-9]{15,}.mp4.nomedia");
				}
				return false;
			}
			return true;
		}
	};

	/**
	 * Renames snapChat videos in the correct directory that follow the correct
	 * regex pattern.
	 * 
	 * @return true iff the rename operation is successful
	 */
	public boolean renameSnapChatVideos() {
		return renameFilesFromDirectory(mFilterVideos, mVideoSaveDirectory,
				SNAPCHAT_PICTURE_EXTENSION_LENGTH);
	}

	/**
	 * Renames a directory based on if it matches a provided FileFileter. This
	 * rename operation will remove the last {@link length} characters from the
	 * current name.
	 * 
	 * @param fFilter
	 *            the file filter to choose the files format we want
	 * @param directory
	 *            the directory that the files are in
	 * @param length
	 * @return true iff the rename operation is successful
	 */
	private boolean renameFilesFromDirectory(FileFilter fFilter,
			String directory, int length) {
		String tempRename;
		File f;
		File[] files;

		// rename the files now
		try {
			f = new File(directory);

			files = f.listFiles(fFilter);

			for (File path : files) {
				tempRename = path.getAbsolutePath();

				if (DEBUG_LOG_FLAG)
					Log.d(LOG_TAG, "Old name: " + tempRename);

				tempRename = tempRename.substring(0, (path.getAbsolutePath()
						.length() - length));

				if (DEBUG_LOG_FLAG)
					Log.d(LOG_TAG, "New name: " + tempRename);

				if (!path.renameTo(new File(tempRename))) {
					if (DEBUG_LOG_FLAG)
						Log.d(LOG_TAG, "File was not renamed correctly");
					return false;
				}
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
			return false;
		}
		// all files were renamed successfully
		if (DEBUG_LOG_FLAG)
			Log.d(LOG_TAG, "All files were renamed successfully.");
		return true;
	}

	/**
	 * This method will copy a SU directory @from to directory @to. If either
	 * directory doesn't exist we will return false. This method performs the
	 * copy using a command line copy command.
	 * 
	 * @param from
	 *            The absolute path of the from directory
	 * @param to
	 *            The absolute path of the to directory
	 * @return True if the copy was successful otherwise false
	 */
	public void copySUDirectory(String from, String to, Context context) {
		File directoryCheck;
		// Check if from directory exists
		directoryCheck = new File(from);
		if (!(directoryCheck.isDirectory()) || !(directoryCheck.exists())) {
			// This directory does not exist
			if (DEBUG_LOG_FLAG)
				Log.d(LOG_TAG, "Directory: " + from + " does not exist.");
		}

		final boolean copyPictures = from.equals(mPictureStorageDirectory);

		String copyString = "cp " + from + " " + to;
		if (DEBUG_LOG_FLAG)
			Log.d(LOG_TAG, "CopyString: " + copyString);

		Bundle bundleDirectory = new Bundle();
		bundleDirectory.putString("fromDirectory", from);
		bundleDirectory.putString("toDirectory", to);

		Intent startBackgroundService = new Intent(
				BackgroundIntentService.ACTION_COPY_DIRECTORY, null, context,
				BackgroundIntentService.class);
		startBackgroundService.putExtra("fromDirectory", from);
		startBackgroundService.putExtra("toDirectory", to);
		startBackgroundService.putExtra(
				BackgroundIntentService.RESULT_RECEIVER_TAG,
				new ResultReceiver(null) {
					@Override
					protected void onReceiveResult(int resultCode,
							Bundle resultDate) {
						if (resultCode == BackgroundIntentService.RESULT_COPY_SUCCESSFUL) {
							if (DEBUG_LOG_FLAG)
								Log.d(LOG_TAG,
										"Copy of pictures was successful, renaming files now.");
							if (copyPictures) {
								renameSnapChatPictures();
							} else {
								renameSnapChatVideos();
							}
						} else if (resultCode == BackgroundIntentService.RESULT_COPY_FAIL) {
							if (DEBUG_LOG_FLAG)
								Log.d(LOG_TAG,
										"Copy of pictures was unsuccessful.");
						}
					}
				});

		context.startService(startBackgroundService);

		// BackgroundIntentService.performAction(context,BackgroundIntentService.ACTION_COPY_DIRECTORY,
		// bundleDirectory);

		// Check if to directory exists
		// TODO: Implement check to double check that we copied them files

	}

	public String getVideoSaveDirectory() {
		return mVideoSaveDirectory;
	}

	public void setVideoSaveDirectory(String videoSaveDirectory) {
		this.mVideoSaveDirectory = videoSaveDirectory;
	}

	public String getPictureSaveDirectory() {
		return mPictureSaveDirectory;
	}

	public void setPictureSaveDirectory(String pictureSaveDirectory) {
		this.mPictureSaveDirectory = pictureSaveDirectory;
	}

	// SU stuff
	// Based of Chainfires libsuperuser_example
	private class BackGroundTask extends AsyncTask<String, Void, List<String>> {
		private Context context = null;
		private boolean suAvailable = false;
		private List<String> suResult = null;

		public BackGroundTask() {
			suResult = new ArrayList<String>();
		}

		public BackGroundTask setContext(Context c) {
			this.context = c;
			return this;
		}

		@Override
		protected void onPreExecute() {
			((MainActivity) context)
					.setProgressBarIndeterminateVisibility(true);
		}

		@Override
		protected List<String> doInBackground(String... params) {
			// Some SU stuff
			suResult = null;
			suAvailable = Shell.SU.available();
			if (suAvailable) {
				suResult = Shell.SU.run(params);
				// TODO: Update progress if this is taking a long time
			}

			return suResult;
		}

		@Override
		protected void onPostExecute(List<String> result) {
			// Output handling
			if (DEBUG_LOG_FLAG) {
				Log.d(LOG_TAG, "Root? " + suAvailable);
				Log.d(LOG_TAG, "Result: ");
				Iterator<String> it = result.iterator();
				while (it.hasNext()) {
					Log.d(LOG_TAG, it.next());
				}
			}
			((MainActivity) context)
					.setProgressBarIndeterminateVisibility(false);
		}
	}

}
