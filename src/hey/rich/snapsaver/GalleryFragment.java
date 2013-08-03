package hey.rich.snapsaver;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SlidingDrawer;

public class GalleryFragment extends ListFragment {

	public static final String TITLE = "Gallery";

	private static final boolean DEBUG_LOG_TAG = true;
	private static final String LOG_TAG = "GalleryFragment";

	ListView lView;
	List<PictureVideoFile> files;
	GalleryListViewAdapter adapter;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		files = new ArrayList<PictureVideoFile>();

		lView = (ListView) getActivity().findViewById(android.R.id.list);
		adapter = new GalleryListViewAdapter(
				getActivity(), R.layout.gallery_list_item, files);
		lView.setAdapter(adapter);
		
		populateFiles();

		if (DEBUG_LOG_TAG)
			Log.d(LOG_TAG, "Just set adapter.");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		// Get some settings
		setHasOptionsMenu(true);
		return inflater.inflate(R.layout.gallery, container, false);
	}

	private void populateFiles() {


		String name, time;
		Date date;
		Drawable image;
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss");

		// TODO: Don't hardcode this anymore
		File saveDirectory = new File("/storage/sdcard0/snaps/");
		if(saveDirectory.exists() && saveDirectory.isDirectory()){
			// Load the stuff 
			File[] fileList = saveDirectory.listFiles();
			
			for(File f : fileList){
				name = f.getAbsolutePath();
				date = new Date(f.lastModified());
				time = df.format(date);
				image = getResources().getDrawable(R.drawable.ic_launcher);
				files.add(new PictureVideoFile(image, name, time));
				if(DEBUG_LOG_TAG) Log.d(LOG_TAG, "Added file: " + name);
			}
		}else{
			name = "Didn't work :(";
			time = "Oh no!";
			image = getResources().getDrawable(R.drawable.ic_launcher);
			files.add(new PictureVideoFile(image, name, time));
		}
		adapter.notifyDataSetChanged();

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		inflater.inflate(R.menu.menu_gallery, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.menu_gallery_refresh:
			if(DEBUG_LOG_TAG) Log.d(LOG_TAG, "Refreshing gallery.");
			// Can't just set files to be a new ArrayList, that means that it won't be the 
			// same object that is connected to my adapter! need to use clear and add.
			files.clear();
			populateFiles();
			if(DEBUG_LOG_TAG) Log.d(LOG_TAG, "Size of files is: " + files.size());
			adapter.notifyDataSetChanged();
			if(DEBUG_LOG_TAG) Log.d(LOG_TAG, "Size of adapter is: " + adapter.getCount());
		return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
