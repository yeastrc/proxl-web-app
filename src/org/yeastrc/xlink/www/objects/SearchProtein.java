package org.yeastrc.xlink.www.objects;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dto.NRProteinDTO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.searcher.SearchUtils;

public class SearchProtein implements IProtein {
	
	private static final Logger log = Logger.getLogger(SearchProtein.class);
			
	// Instantiate SearchProtein
	public SearchProtein( SearchDTO search, NRProteinDTO protein ) {
		this.search = search;
		this.nrProtein = protein;
	}
	
	public int hashCode() {
		return ("" + this.getNrProtein().getNrseqId() + "-" + this.getSearch().getId() ).hashCode();
	}
	
	public boolean equals( Object o ) {
		if( !( o instanceof SearchProtein ) ) return false;
		
		if( ((SearchProtein)o).getNrProtein().getNrseqId() != this.getNrProtein().getNrseqId() )
			return false;
		
		if( ((SearchProtein)o).getSearch().getId() != this.getSearch().getId() )
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
	
	public NRProteinDTO getNrProtein() {
		return nrProtein;
	}

	public void setNrProtein(NRProteinDTO nrProtein) {
		this.nrProtein = nrProtein;
	}
	
	public SearchDTO getSearch() {
		return search;
	}

	public void setSearch(SearchDTO search) {
		this.search = search;
	}


	private NRProteinDTO nrProtein;
	private SearchDTO search;
	private String name;
	private String description;
	
}
