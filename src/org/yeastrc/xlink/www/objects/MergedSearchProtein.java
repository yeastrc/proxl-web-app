package org.yeastrc.xlink.www.objects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.yeastrc.xlink.dto.NRProteinDTO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.www.searcher.SearchUtils;

public class MergedSearchProtein implements IProtein {
	
	/**
	 * 
	 * @param searches The searches that provide a context for the naming of this protein
	 * @param protein The protein
	 */
	public MergedSearchProtein( Collection<SearchDTO> searches, NRProteinDTO protein ) {
		this.searchs = searches;
		this.nrProtein = protein;
	}
	
	// Get the name(s) for this protein from the searches
	public String getName() throws Exception {
		Set<String> names = new HashSet<String>();
		
		for( SearchDTO search : searchs ) {
			String name = SearchUtils.getProteinNameForSearch( new SearchProtein( search, nrProtein ) );
			if( name != null )
				names.add( name );
		}
		
		List<String> nameList = new ArrayList<String>();
		nameList.addAll( names );		
		Collections.sort( nameList );
		
		return StringUtils.join( names, ", " );
	}
	
	// Get the description(s) for this protein from the searches
	public String getDescription() throws Exception {
		Set<String> descriptions = new HashSet<String>();
		
		for( SearchDTO search : searchs ) {
			String description = SearchUtils.getProteinDescriptionForSearch( new SearchProtein( search, nrProtein ) );
			if( description != null )
				descriptions.add( description );
		}
		
		return StringUtils.join( descriptions, ", " );
	}
	

	public NRProteinDTO getNrProtein() {
		return nrProtein;
	}

	public Collection<SearchDTO> getSearchs() {
		return searchs;
	}


	private final NRProteinDTO nrProtein;
	private final Collection<SearchDTO> searchs;
	
}
