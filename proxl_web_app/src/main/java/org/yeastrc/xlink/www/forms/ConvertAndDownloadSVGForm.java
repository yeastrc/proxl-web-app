package org.yeastrc.xlink.www.forms;

import org.apache.struts.action.ActionForm;

public class ConvertAndDownloadSVGForm extends ActionForm {


	/**
	 * 
	 */
	private static final long serialVersionUID = -1152989715725810740L;

	public String getSvgString() {
		return svgString;
	}
	public void setSvgString(String svgString) {
		this.svgString = svgString;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getBrowserURL() {
		return browserURL;
	}
	public void setBrowserURL(String browserURL) {
		this.browserURL = browserURL;
	}
	
	
	private String svgString;
	private String fileType;
	private String browserURL;

}
