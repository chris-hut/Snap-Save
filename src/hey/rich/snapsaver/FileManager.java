package hey.rich.snapsaver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import android.util.Log;

public class FileManager {

	/** Debug log flag. */
	private static final boolean DEBUG_LOG_FLAG = true;
	/** Log Tag */
	private static final String LOG_TAG = "FileManager";

	public FileManager() {
		// TODO: Figure out what this file manager needs
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
	public boolean copySUDirectory(String from, String to) {
		File directoryCheck;
		// Check if from directory exists
		directoryCheck = new File(from);
		if((directoryCheck.isDirectory()) || (directoryCheck.exists())){
			// This directory does not exist
			if(DEBUG_LOG_FLAG) Log.d(LOG_TAG, "Directory: " + from + " does not exist.");
		}
		
		String copyString = "cp " + from + " " + to;
		if (DEBUG_LOG_FLAG)
			Log.d(LOG_TAG, "CopyString: " + copyString);

		try {
			Process suProcess = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(
					suProcess.getOutputStream());

			BufferedReader b = new BufferedReader(new InputStreamReader(
					suProcess.getInputStream()));
			String line;

			os.writeBytes(copyString + "\n");
			os.flush();
			os.writeBytes("exit\n");
			os.flush();

			while (((line = b.readLine()) != null) && (DEBUG_LOG_FLAG)) {
				Log.d(LOG_TAG, line);
			}

			int suProcessRetval = suProcess.waitFor();

			if (suProcessRetval != 255) {
				// We got root
				// TODO: Actually check if we are copying the file or not.
				return true;
			} else {
				// Didn't get root
				Log.w(LOG_TAG, "We didn't get root");
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			Log.w(LOG_TAG, "Error getting root.");
			e.printStackTrace();
			return false;
		}

		// Check if to directory exists

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
}
