package org.yeastrc.xlink.www.forms;

import org.apache.struts.action.ActionForm;

/**
 * Standard form that contains single property for a single request JSON string
 * 
 * Used for downloads and maybe other uses where only a POST of a single JSON string needs to be supported
 *
 */
public class SingleRequestJSONStringFieldForm extends ActionForm {
	
	private static final long serialVersionUID = 1L;

	private String requestJSONString; //  Form Parameter Name.  JSON encoded data

	public String getRequestJSONString() {
		return requestJSONString;
	}
	public void setRequestJSONString(String requestJSONString) {
		this.requestJSONString = requestJSONString;
	}

}
