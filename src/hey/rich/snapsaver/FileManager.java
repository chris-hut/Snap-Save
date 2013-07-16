package hey.rich.snapsaver;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
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

	/** Context to be used from the BackGroundTask */
	private Context mContext = null;

	public FileManager() {
		setVideoSaveDirectory("/storage/sdcard0/snaps");
		setPictureSaveDirectory("/storage/sdcard0/snaps");
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
		this.mContext = context;
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
	public FileManager(String pictureSaveDirectory, String videoSaveDirectory,
			Context context) {
		setVideoSaveDirectory(videoSaveDirectory);
		setPictureSaveDirectory(pictureSaveDirectory);
		this.mContext = context;
	}

	/**
	 * Copies snapChat pictures to the correct directory.
	 * 
	 * @boolean true iff the copy was successful
	 */
	public boolean copySnapChatPictures() {
		copySUDirectory(mPictureStorageDirectory, mPictureSaveDirectory);
		return true;
	}

	/**
	 * Copies snapChat videos to the correct directory
	 * 
	 * @boolean true iff the copy was successful
	 */
	public boolean copySnapChatVideos() {
		copySUDirectory(mVideoStorageDirectory, mVideoSaveDirectory);
		return true;
	}

	/**
	 * Renames snapChat pictures in the correct directory that follow the
	 * correct regex pattern.
	 * 
	 * @return true iff the rename operation is successful
	 */
	public boolean renameSnapChatPictures() {
		String tempRename;
		File f;
		File[] files;
		
		// Rename the files now pls
		try{
			f = new File(mPictureSaveDirectory);
			
			files = f.listFiles(mFilterPictures);
			
			for(File path : files){
				tempRename = path.getAbsolutePath();
				
				if(DEBUG_LOG_FLAG) Log.d(LOG_TAG, "Old name: " + tempRename);
				
				tempRename = tempRename.substring(0, tempRename.length() - SNAPCHAT_PICTURE_EXTENSION_LENGTH);
				
				if(DEBUG_LOG_FLAG) Log.d(LOG_TAG, "New name: " + tempRename);
				
				if(!path.renameTo(new File(tempRename))){
					if (DEBUG_LOG_FLAG) Log.d(LOG_TAG, "File was not renamed corectly.");
					return false;
				}
			}
		}catch(NullPointerException e){
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/** FileFilter that parses snap chat pictures */
	private FileFilter mFilterPictures = new FileFilter() {
		@Override
		public boolean accept(File pathName) {
			// File must exist
			if (!pathName.isFile()) {
				if (DEBUG_LOG_FLAG) {
					Log.d(LOG_TAG, "File does not exist.");
					Log.d(LOG_TAG,
							"File name was: " + pathName.getAbsolutePath());
				}
				return false;
			}

			// File must end in .nomedia
			if (!pathName.getAbsolutePath()
					.substring(pathName.getAbsolutePath().length() - 8)
					.equals(".nomedia")) {
				if (DEBUG_LOG_FLAG) {
					Log.d(LOG_TAG, "File did not end in .nomedia.");
					Log.d(LOG_TAG,
							"File name was: " + pathName.getAbsolutePath());
				}
				return false;
			}
			// File must be like: h1a81hurcs00h1368487198510.jpg.nomedia
			if (!pathName.getAbsolutePath().matches(
					mPictureSaveDirectory + "/"
							+ "h1a81hurcs00h[0-9]{13,}.jpg.nomedia")) {
				if (DEBUG_LOG_FLAG) {
					Log.d(LOG_TAG, "File was not good looking.");
					Log.d(LOG_TAG,
							"File name was: " + pathName.getAbsolutePath());
					Log.d(LOG_TAG, "File should look more like: "
							+ mPictureSaveDirectory + "/"
							+ "h1a81hurcs00h[0-9]{13,}.jpg.nomedia");
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
	public void copySUDirectory(String from, String to) {
		File directoryCheck;
		// Check if from directory exists
		directoryCheck = new File(from);
		if ((directoryCheck.isDirectory()) || (directoryCheck.exists())) {
			// This directory does not exist
			if (DEBUG_LOG_FLAG)
				Log.d(LOG_TAG, "Directory: " + from + " does not exist.");
		}

		String copyString = "cp " + from + " " + to;
		if (DEBUG_LOG_FLAG)
			Log.d(LOG_TAG, "CopyString: " + copyString);

		if (mContext != null) {
			(new BackGroundTask()).setContext(mContext).execute(copyString);
		} else {
			if (DEBUG_LOG_FLAG)
				Log.d(LOG_TAG, "No context - cannot start background task.");
		}

		// Check if to directory exists
		// TODO: Implement check to double check that we copied them files

	}

	/**
	 * This method will rename the file @oldName to @newName
	 * 
	 * @param oldName
	 *            The absolute path to the file to rename
	 * @param newName
	 *            The absolute path to the new files name
	 * @return True if the rename was successful otherwise false
	 */
	public boolean renameFile(String oldName, String newName) {
		File f;

		f = new File(oldName);

		// Check if old name exists
		if (!f.exists()) {
			// File does not exist
			if (DEBUG_LOG_FLAG)
				Log.d(LOG_TAG, "File: " + oldName + " does not exist.");
			// TODO: Alert the user that something went wrong
			return false;
		}

		if (f.renameTo(new File(newName))) {
			if (DEBUG_LOG_FLAG)
				Log.d(LOG_TAG, "File was renamed to " + newName
						+ "successfully.");
			return true;

		} else {
			if (DEBUG_LOG_FLAG)
				Log.d(LOG_TAG, "Error renaming file to: " + newName);
			// TODO: Alert user that rename operation wasn't successful
			return false;
		}
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
