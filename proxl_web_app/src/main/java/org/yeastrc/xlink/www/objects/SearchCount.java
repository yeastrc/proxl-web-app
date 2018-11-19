package org.yeastrc.xlink.www.objects;

/**
 * Search count display on web page
 *
 */
public class SearchCount {

	private int projectSearchId;
	private int searchId;
	private int count;
	
	
	public int getSearchId() {
		return searchId;
	}
	public void setSearchId(int searchId) {
		this.searchId = searchId;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getProjectSearchId() {
		return projectSearchId;
	}
	public void setProjectSearchId(int projectSearchId) {
		this.projectSearchId = projectSearchId;
	}
}
