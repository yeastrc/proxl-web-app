package org.yeastrc.xlink.www.objects;

public class ProjectTitleAbstractAdminResult {

	private boolean status;
	private String title;
	private String titleHeaderDisplay;
	private String abstractText;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTitleHeaderDisplay() {
		return titleHeaderDisplay;
	}
	public void setTitleHeaderDisplay(String titleHeaderDisplay) {
		this.titleHeaderDisplay = titleHeaderDisplay;
	}
	public String getAbstractText() {
		return abstractText;
	}
	public void setAbstractText(String abstractText) {
		this.abstractText = abstractText;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}

}
