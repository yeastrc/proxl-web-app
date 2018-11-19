package org.yeastrc.xlink.www.objects;

import java.util.List;

import org.yeastrc.xlink.www.dto.XLinkUserDTO;

public class ListAllUsersResponse {

	private boolean status;
	
	private List<XLinkUserDTO> users;
//	private XLinkUserDTO currentUser;

	
	public List<XLinkUserDTO> getUsers() {
		return users;
	}
	public void setUsers(List<XLinkUserDTO> users) {
		this.users = users;
	}
	
	
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}

}
