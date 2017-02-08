package org.yeastrc.xlink.dto;

import javax.xml.bind.annotation.*;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;


public class SearchCommentDTO {
	
	
	public SearchCommentDTO( int projectSearchid, String comment, int authUserId ) {
		this.projectSearchid = projectSearchid;
		this.comment = comment;
		this.authUserId = authUserId;
	}

	
	public SearchCommentDTO() { }
	
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	@XmlTransient //  Does not stop JACKSON from serializing to JSON
	public DateTime getDateTime() {
		return dateTime;
	}
	public void setDateTime(DateTime dateTime) {
		this.dateTime = dateTime;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getProjectSearchid() {
		return projectSearchid;
	}

	public void setProjectSearchid(int projectSearchid) {
		this.projectSearchid = projectSearchid;
	}

	@XmlTransient //  Does not stop JACKSON from serializing to JSON
	public Integer getAuthUserId() {
		return authUserId;
	}


	public void setAuthUserId(Integer authUserId) {
		this.authUserId = authUserId;
	}

	
	@XmlElement
	public String getDateTimeString() {
		
		if ( this.dateTime == null ) {
			return "";
		}
		
		return this.dateTime.toString( DateTimeFormat.forPattern("yyyy-MM-dd") );
	}

	private String comment;
	private DateTime dateTime;	
	private int projectSearchid;
	private Integer authUserId;

	private int id;
	
}
