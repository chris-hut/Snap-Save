package hey.rich.snapsaver;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GalleryListViewAdapter extends ArrayAdapter {
	Context c;

	public GalleryListViewAdapter(Context c, int resId,
			List<PictureVideoFile> items) {
		super(c, resId, items);
		this.c = c;
	}

	private class ViewHolder {
		ImageView iV;
		TextView tvTitle;
		TextView tvTime;
	}

	public View getView(int pos, View cView, ViewGroup parent) {
		ViewHolder holder = null;
		PictureVideoFile file = (PictureVideoFile) getItem(pos);

		LayoutInflater mInflater = (LayoutInflater) c
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if(cView == null){
			cView = mInflater.inflate(R.layout.gallery_list_item, null);
			holder = new ViewHolder();
			holder.iV = (ImageView) cView.findViewById(R.id.file_thumbnail);
			holder.tvTitle = (TextView) cView.findViewById(R.id.file_name);
			holder.tvTime = (TextView) cView.findViewById(R.id.file_time_stamp);
			cView.setTag(holder);
		}else{
			holder = (ViewHolder) cView.getTag();
		}
		
		holder.iV.setImageDrawable(file.getImage());
		holder.tvTitle.setText(file.getTitle());
		holder.tvTime.setText(file.getDateModified());
		
		return cView;
	}

}
