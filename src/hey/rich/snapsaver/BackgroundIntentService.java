package hey.rich.snapsaver;

import java.util.Iterator;
import java.util.List;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import eu.chainfire.libsuperuser.Shell;

public class BackgroundIntentService extends IntentService {

	// Final values
	public static final String ACTION_COPY_DIRECTORY = "copy_snap_pictures";
	public static final String RESULT_RECEIVER_TAG = "resultReceiver";
	public static final int RESULT_COPY_SUCCESSFUL = 1;
	public static final int RESULT_COPY_FAIL = 2;
	private static final String DIRECTORY_FROM = "fromDirectory";
	private static final String DIRECTORY_TO = "toDirectory";
	private static final String COPY_COMMAND = "cp ";

	// Logging info
	private static final boolean DEBUG_LOG_FLAG = true;
	private static final String LOG_TAG = "BackgroundIntentService";

	/** Used to send data back to the calling activity */
	private static ResultReceiver rReceiver;

	public static void performAction(Context context, String action) {
		performAction(context, action, null);
	}

	public static void performAction(Context context, String action,
			Bundle extras) {
		if ((context == null) || (action == null) || (action.equals(""))) {
			if (DEBUG_LOG_FLAG)
				Log.d(LOG_TAG, "Context or action was null.");
			return;
		}

		Intent svc = new Intent(context, BackgroundIntentService.class);
		svc.setAction(action);
		if (extras != null)
			svc.putExtras(extras);
		context.startService(svc);
	}

	public BackgroundIntentService() {
		// If you forget this, the app will crash
		super("BackgroundIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String action = intent.getAction();
		if ((action == null)
				|| (action.equals("") || (intent.getExtras() == null))) {
			if (DEBUG_LOG_FLAG) {
				Log.d(LOG_TAG, "action or extras was null.");
				Log.d(LOG_TAG, "extras were null: " + (intent.getExtras() == null));
				Log.d(LOG_TAG, "action was: " + action);
			}
			return;
		}

		if (action.equals(ACTION_COPY_DIRECTORY)) {
			if (DEBUG_LOG_FLAG)
				Log.d(LOG_TAG, "Intent action was copy directory.");

			rReceiver = intent.getParcelableExtra(RESULT_RECEIVER_TAG);
			if (DEBUG_LOG_FLAG)
				Log.d(LOG_TAG, "Got a receiver back to the calling activity.");

			int resultCode = copySnapDirectory(
					intent.getExtras().getString(DIRECTORY_FROM), intent
							.getExtras().getString(DIRECTORY_TO));

			rReceiver.send(resultCode, new Bundle());
		}
	}

	/**
	 * Will copy the snapchat pictures to the directory specified
	 * 
	 * @param fromDirectory
	 *            The directory to copy all files from
	 * @param toDirectory
	 *            The directory to copy all of the files to
	 * @return Returns {@link RESULT_COPY_SUCCESSFUL} if copy was successful
	 *         else {@link RESULT_COPY_FAIL}
	 */
	protected int copySnapDirectory(String fromDirectory, String toDirectory) {
		// We are running in a background thread here!
		String command;
		List<String> result;
		int retVal = RESULT_COPY_SUCCESSFUL;
		command = COPY_COMMAND + fromDirectory + "* " + toDirectory;
		if (DEBUG_LOG_FLAG)
			Log.d(LOG_TAG, "Command is: " + command);

		result = Shell.SU.run(command);
		Iterator<String> it = result.iterator();
		String line;
		while (it.hasNext()) {
			line = it.next();
			if (DEBUG_LOG_FLAG)
				Log.d(LOG_TAG, line);
			if (line.matches("No such file or directory")) {
				retVal = RESULT_COPY_FAIL;
			}
		}
		return retVal;
	}

}
