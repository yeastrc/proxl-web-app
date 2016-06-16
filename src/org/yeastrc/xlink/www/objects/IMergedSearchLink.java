package org.yeastrc.xlink.www.objects;

import java.util.Collection;

import org.yeastrc.xlink.www.dto.SearchDTO;

/**
 * generic to merged search links, currently in m
 *
 */
public interface IMergedSearchLink {

	/**
	 * Get the searches for this link.  This is the keyset for the Map with keys are searches and and data are the links per search 
	 * @return
	 */
	public Collection<SearchDTO> getSearches();
		
}
