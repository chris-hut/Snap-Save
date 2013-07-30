package hey.rich.snapsaver;

import com.viewpagerindicator.IconPagerAdapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SnapSaverFragmentAdapter extends FragmentPagerAdapter implements IconPagerAdapter{

	private static final int NUMBER_OF_FRAGMENTS = 2;

	public SnapSaverFragmentAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {
		case 0:
			return new MainFragment();

		case 1:
			return new GalleryFragment();
		default:
			return null;
		}
	}

	@Override
	public int getCount() {
		return NUMBER_OF_FRAGMENTS;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		switch (position) {
		case 0:
			return MainFragment.TITLE;
		case 1:
			return GalleryFragment.TITLE;
		default:
			return null;
		}
	}
	
	@Override
	public int getIconResId(int index){
		switch(index){
		// TODO: Use actualy icons for this?
		default: 
			return R.drawable.ic_launcher;
		}
	}
}
