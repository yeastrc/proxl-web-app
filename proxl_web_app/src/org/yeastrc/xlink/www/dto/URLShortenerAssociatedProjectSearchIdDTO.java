package org.yeastrc.xlink.www.dto;

/**
 * table url_shortener_associated_project_search_id
 *
 */
public class URLShortenerAssociatedProjectSearchIdDTO {

	private int urlShortenerId;
	private int projectSearchId;
	
	public int getUrlShortenerId() {
		return urlShortenerId;
	}
	public void setUrlShortenerId(int urlShortenerId) {
		this.urlShortenerId = urlShortenerId;
	}
	public int getProjectSearchId() {
		return projectSearchId;
	}
	public void setProjectSearchId(int searchId) {
		this.projectSearchId = searchId;
	}
	
}
