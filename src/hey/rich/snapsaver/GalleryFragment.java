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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class GalleryFragment extends ListFragment {

	public static final String TITLE = "Gallery";

	private static final boolean DEBUG_LOG_TAG = true;
	private static final String LOG_TAG = "GalleryFragment";

	ListView lView;
	List<PictureVideoFile> files;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Not sure if I should do all of this from this, the onCreateView or if
		// I should just do it from onCreate
		files = new ArrayList<PictureVideoFile>();
		files = populateFiles();

		lView = (ListView) getActivity().findViewById(android.R.id.list);
		GalleryListViewAdapter adapter = new GalleryListViewAdapter(
				getActivity(), R.layout.gallery_list_item, files);
		lView.setAdapter(adapter);

		if (DEBUG_LOG_TAG)
			Log.d(LOG_TAG, "Just set adapter.");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.gallery, container, false);
	}

	private ArrayList<PictureVideoFile> populateFiles() {

		ArrayList<PictureVideoFile> items = new ArrayList<PictureVideoFile>();

		String name, time;
		Date date;
		Drawable image;
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss");

		// TODO: Don't hardcode this anymore
		File saveDirectory = new File("/storage/sdcard0/snaps/");
		if(saveDirectory.exists() && saveDirectory.isDirectory()){
			// Load the stuff 
			File[] files = saveDirectory.listFiles();
			
			for(File f : files){
				name = f.getAbsolutePath();
				date = new Date(f.lastModified());
				time = df.format(date);
				image = getResources().getDrawable(R.drawable.ic_launcher);
				items.add(new PictureVideoFile(image, name, time));
			}
		}else{
			name = "Didn't work :(";
			time = "Oh no!";
			image = getResources().getDrawable(R.drawable.ic_launcher);
			items.add(new PictureVideoFile(image, name, time));
		}
		

		return items;
	}

}
