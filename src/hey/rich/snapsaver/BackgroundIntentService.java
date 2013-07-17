package hey.rich.snapsaver;

import java.util.Iterator;
import java.util.List;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import eu.chainfire.libsuperuser.Shell;

public class BackgroundIntentService extends IntentService {

	public static final String ACTION_COPY_DIRECTORY = "copy_snap_pictures";
	private static final String DIRECTORY_FROM = "fromDirectory";
	private static final String DIRECTORY_TO = "toDirectory";
	private static final String COPY_COMMAND = "cp ";

	private static final boolean DEBUG_LOG_FLAG = true;
	private static final String LOG_TAG = "BackgroundIntentService";

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
			if (DEBUG_LOG_FLAG)
				Log.d(LOG_TAG, "action or extras was null.");
			return;
		}

		if (action.equals(ACTION_COPY_DIRECTORY)) {
			if (DEBUG_LOG_FLAG)
				Log.d(LOG_TAG, "Intent action was copy directory.");
			copySnapDirectory(intent.getExtras().getString(DIRECTORY_FROM),
					intent.getExtras().getString(DIRECTORY_TO));
		}
	}

	/**
	 * Will copy the snapchat pictures to the directory specified
	 * 
	 * @param fromDirectory
	 *            The directory to copy all files from
	 * @param toDirectory
	 *            The directory to copy all of the files to
	 */
	protected void copySnapDirectory(String fromDirectory, String toDirectory) {
		// We are running in a background thread here!
		String command;
		List<String> result;
		command = COPY_COMMAND + fromDirectory + "* " + toDirectory;
		if (DEBUG_LOG_FLAG)
			Log.d(LOG_TAG, "Command is: " + command);

		result = Shell.SU.run(command);
		if (DEBUG_LOG_FLAG) {
			Iterator<String> it = result.iterator();
			while(it.hasNext()){
				Log.d(LOG_TAG, it.next());
			}
		}
	}

}
