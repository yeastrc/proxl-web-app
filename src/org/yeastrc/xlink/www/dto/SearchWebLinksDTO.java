package org.yeastrc.xlink.www.dto;

import javax.xml.bind.annotation.*;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

public class SearchWebLinksDTO {
	
	



	private String linkUrl;
	private String linkLabel;
	private DateTime dateTime;	
	private int searchid;
	private int authUserId;

	private int id;
	
	
	
	@XmlElement
	public String getDateTimeString() {
		return this.dateTime.toString( DateTimeFormat.forPattern("yyyy-MM-dd") );
	}
	
	
	@XmlTransient
	public DateTime getDateTime() {
		return dateTime;
	}
	public void setDateTime(DateTime dateTime) {
		this.dateTime = dateTime;
	}

	

	public String getLinkUrl() {
		return linkUrl;
	}
	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}
	public String getLinkLabel() {
		return linkLabel;
	}
	public void setLinkLabel(String linkLabel) {
		this.linkLabel = linkLabel;
	}
	public int getSearchid() {
		return searchid;
	}
	public void setSearchid(int searchid) {
		this.searchid = searchid;
	}
	public int getAuthUserId() {
		return authUserId;
	}
	public void setAuthUserId(int authUserId) {
		this.authUserId = authUserId;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
}

//CREATE TABLE search_web_links (
//id INT UNSIGNED NOT NULL AUTO_INCREMENT,
//search_id INT UNSIGNED NOT NULL,
//auth_user_id INT UNSIGNED NULL,
//link_url VARCHAR(600) NOT NULL,
//link_label VARCHAR(400) NOT NULL,
//link_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

