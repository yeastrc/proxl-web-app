package org.yeastrc.xlink.www.objects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.yeastrc.xlink.www.objects.ProteinSequenceVersionObject;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders.Cached_ProteinDescriptionFor_SearchProtein;
import org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders.Cached_ProteinNameFor_SearchProtein;
import org.yeastrc.xlink.www.searcher_via_cached_data.request_objects_for_searchers_for_cached_data.ProteinDescriptionFor_SearchProtein_Request;
import org.yeastrc.xlink.www.searcher_via_cached_data.request_objects_for_searchers_for_cached_data.ProteinNameFor_SearchProtein_Request;
import org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data.ProteinDescriptionFor_SearchProtein_Result;
import org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data.ProteinNameFor_SearchProtein_Result;

public class MergedSearchProtein implements IProtein {
	/**
	 * 
	 * @param searches The searches that provide a context for the naming of this protein
	 * @param protein The protein
	 */
	public MergedSearchProtein( Collection<SearchDTO> searches, ProteinSequenceVersionObject protein ) {
		this.searchs = searches;
		this.proteinSequenceVersionObject = protein;
	}
	// Get the name(s) for this protein from the searches
	public String getNameLowerCase() throws Exception {
		if ( ! nameLowercaseSet ) {
			nameLowercase = getName().toLowerCase();
		}
		return nameLowercase;
	}
	// Get the name(s) for this protein from the searches
	@Override
	public String getName() throws Exception {
		if ( ! nameSet ) {
			Set<String> names = new HashSet<String>();
			for( SearchDTO search : searchs ) {
				ProteinNameFor_SearchProtein_Request proteinNameFor_SearchProtein_Request = new ProteinNameFor_SearchProtein_Request();
				proteinNameFor_SearchProtein_Request.setSearchId( search.getSearchId() );
				proteinNameFor_SearchProtein_Request.setProteinSequenceVersionId( this.getProteinSequenceVersionObject().getProteinSequenceVersionId() );
				ProteinNameFor_SearchProtein_Result proteinNameFor_SearchProtein_Result = 
						Cached_ProteinNameFor_SearchProtein.getInstance()
						.getProteinNameFor_SearchProtein_Result( proteinNameFor_SearchProtein_Request );
				String name = proteinNameFor_SearchProtein_Result.getProteinName();
				if( name != null )
					names.add( name );
			}
			List<String> nameList = new ArrayList<String>();
			nameList.addAll( names );		
			Collections.sort( nameList );
			name = StringUtils.join( names, ", " );
		}
		return name;
	}
	// Get the description(s) for this protein from the searches
	@Override
	public String getDescription() throws Exception {
		Set<String> descriptions = new HashSet<String>();
		for( SearchDTO search : searchs ) {
			ProteinDescriptionFor_SearchProtein_Request proteinDescriptionFor_SearchProtein_Request = new ProteinDescriptionFor_SearchProtein_Request();
			proteinDescriptionFor_SearchProtein_Request.setSearchId( search.getSearchId() );
			proteinDescriptionFor_SearchProtein_Request.setProteinSequenceVersionId( this.getProteinSequenceVersionObject().getProteinSequenceVersionId() );
			ProteinDescriptionFor_SearchProtein_Result proteinDescriptionFor_SearchProtein_Result = 
					Cached_ProteinDescriptionFor_SearchProtein.getInstance()
					.getProteinDescriptionFor_SearchProtein_Result( proteinDescriptionFor_SearchProtein_Request );
			String description = proteinDescriptionFor_SearchProtein_Result.getProteinDescription();	
			if( description != null )
				descriptions.add( description );
		}
		return StringUtils.join( descriptions, ", " );
	}
	@Override
	public ProteinSequenceVersionObject getProteinSequenceVersionObject() {
		return proteinSequenceVersionObject;
	}
	public Collection<SearchDTO> getSearchs() {
		return searchs;
	}
	
	private String name;
	private boolean nameSet;
	private String nameLowercase;
	private boolean nameLowercaseSet;
	private final ProteinSequenceVersionObject proteinSequenceVersionObject;
	private final Collection<SearchDTO> searchs;
}
