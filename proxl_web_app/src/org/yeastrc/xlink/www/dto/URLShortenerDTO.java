package org.yeastrc.xlink.www.dto;

/**
 * table url_shortener
 *
 */
public class URLShortenerDTO {

	private int id;
	private String shortenedUrlKey;
	private Integer authUserId;
	private String url;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Integer getAuthUserId() {
		return authUserId;
	}
	public void setAuthUserId(Integer authUserId) {
		this.authUserId = authUserId;
	}
	public String getShortenedUrlKey() {
		return shortenedUrlKey;
	}
	public void setShortenedUrlKey(String shortenedUrlKey) {
		this.shortenedUrlKey = shortenedUrlKey;
	}
	
}
