package hey.rich.snapsaver;

import android.graphics.drawable.Drawable;

public class PictureVideoFile {
	private Drawable image;
	private String title;
	private String dateModified;

	public PictureVideoFile(Drawable image, String title, String dateModified) {
		this.image = image;
		this.title = title;
		this.dateModified = dateModified;
	}
	
	@Override
	public String toString(){
		return title;
	}
	/**
	 * @return the image
	 */
	public Drawable getImage() {
		return image;
	}

	/**
	 * @param image the image to set
	 */
	public void setImage(Drawable image) {
		this.image = image;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the dateModified
	 */
	public String getDateModified() {
		return dateModified;
	}

	/**
	 * @param dateModified the dateModified to set
	 */
	public void setDateModified(String dateModified) {
		this.dateModified = dateModified;
	}

}
