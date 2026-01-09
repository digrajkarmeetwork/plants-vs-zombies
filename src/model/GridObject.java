package model;

import java.io.Serializable;

public abstract class GridObject implements Serializable{
	String objectTitle;

	/**
	 * This method gets objectTitle.
	 */
	public String getObjectTitle() {
		return objectTitle;
	}
}