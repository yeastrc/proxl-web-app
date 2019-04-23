package org.yeastrc.xlink.www.searcher_results;

/**
 * Result entry from SavedViewListForProjectIdSearcher
 *
 */
public class SavedViewListForProjectIdItem {

	private int id;
	private String label;
	private String url;
	private int authUserIdCreated;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getAuthUserIdCreated() {
		return authUserIdCreated;
	}
	public void setAuthUserIdCreated(int userIdCreated) {
		this.authUserIdCreated = userIdCreated;
	}

}
