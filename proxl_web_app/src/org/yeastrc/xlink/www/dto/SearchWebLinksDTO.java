package org.yeastrc.xlink.www.dto;

import javax.xml.bind.annotation.*;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

public class SearchWebLinksDTO {
	
	



	private String linkUrl;
	private String linkLabel;
	private DateTime dateTime;	
	private int projectSearchid;
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
	public int getProjectSearchid() {
		return projectSearchid;
	}
	public void setProjectSearchid(int projectSearchid) {
		this.projectSearchid = projectSearchid;
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

