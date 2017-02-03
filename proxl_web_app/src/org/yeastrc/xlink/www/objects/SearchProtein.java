package org.yeastrc.xlink.www.objects;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.objects.ProteinSequenceObject;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.searcher.SearchUtils;

public class SearchProtein implements IProtein {
	
	private static final Logger log = Logger.getLogger(SearchProtein.class);
			
	// Instantiate SearchProtein
	public SearchProtein( SearchDTO search, ProteinSequenceObject protein ) {
		this.search = search;
		this.proteinSequenceObject = protein;
	}
	
	public int hashCode() {
		return ("" + this.getProteinSequenceObject().getProteinSequenceId() + "-" + this.getSearch().getSearchId() ).hashCode();
	}
	
	public boolean equals( Object o ) {
		if( !( o instanceof SearchProtein ) ) return false;
		
		if( ((SearchProtein)o).getProteinSequenceObject().getProteinSequenceId() != this.getProteinSequenceObject().getProteinSequenceId() )
			return false;
		
		if( ((SearchProtein)o).getSearch().getSearchId() != this.getSearch().getSearchId() )
			return false;
		
		return true;
	}
	

	public String getName() throws Exception {

		try {
			if( this.name == null )
				this.name = SearchUtils.getProteinNameForSearch( this );

			return this.name;

		} catch ( Exception e ) {

			String msg = "Exception in getName()";

			log.error( msg, e );

			throw e;
		}
	}

	public String getDescription() throws Exception {

		try {
			if( this.description == null )
				this.description = SearchUtils.getProteinDescriptionForSearch( this );

			return this.description;

		} catch ( Exception e ) {

			String msg = "Exception in getDescription()";

			log.error( msg, e );

			throw e;
		}
	}
	
	public ProteinSequenceObject getProteinSequenceObject() {
		return proteinSequenceObject;
	}

	public void setProteinSequenceObject(ProteinSequenceObject proteinSequenceObject) {
		this.proteinSequenceObject = proteinSequenceObject;
	}
	
	public SearchDTO getSearch() {
		return search;
	}

	public void setSearch(SearchDTO search) {
		this.search = search;
	}


	private ProteinSequenceObject proteinSequenceObject;
	private SearchDTO search;
	private String name;
	private String description;
	
}
