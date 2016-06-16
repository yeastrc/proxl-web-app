package org.yeastrc.xlink.www.objects;

import org.yeastrc.xlink.www.dto.PDBFileDTO;


public class WWWPDBFile {

	
	private PDBFileDTO dto;
	private boolean canEdit;
	
	public PDBFileDTO getDto() {
		return dto;
	}
	public void setDto(PDBFileDTO dto) {
		this.dto = dto;
	}
	public boolean isCanEdit() {
		return canEdit;
	}
	public void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}
	
	
}
