package org.yeastrc.xlink.www.forms;

import org.apache.struts.action.ActionForm;

public class DownloadStringAsFileForm extends ActionForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1968252603724389215L;
	
	private String content;
	private String mimetype;
	private String filename;
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getMimetype() {
		return mimetype;
	}
	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}

}
