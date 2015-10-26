package org.yeastrc.xlink.www.objects;

import java.util.List;

import org.yeastrc.xlink.www.dto.XLinkUserDTO;

/**
 * This is returned from the web service UserLookupService
 *
 */
public class UserQueryResult {

	private List<XLinkUserDTO> queryResultList;

	public List<XLinkUserDTO> getQueryResultList() {
		return queryResultList;
	}

	public void setQueryResultList(List<XLinkUserDTO> queryResultList) {
		this.queryResultList = queryResultList;
	}

}
