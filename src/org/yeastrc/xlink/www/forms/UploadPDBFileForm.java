package org.yeastrc.xlink.www.forms;

import org.apache.struts.action.ActionForm;
import org.apache.struts.upload.FormFile;

public class UploadPDBFileForm extends ActionForm {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9083896623834439808L;
	
	
	public FormFile getFile() {
		return file;
	}
	public void setFile(FormFile file) {
		this.file = file;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getProjectId() {
		return projectId;
	}
	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}
	
	
//	public String getVisibility() {
//		return visibility;
//	}
//	public void setVisibility(String visibility) {
//		this.visibility = visibility;
//	}



	private FormFile file;
	private String description;
	private int projectId;
	
	//  Not used
//	private String visibility;

}
