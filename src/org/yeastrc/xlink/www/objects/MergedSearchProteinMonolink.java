package org.yeastrc.xlink.www.objects;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.searchers.NumPsmsForProteinCriteriaSearcher;
import org.yeastrc.xlink.www.searcher.MergedSearchMonolinkPeptideSearcher;
import org.yeastrc.xlink.www.searcher.SearchProteinMonolinkSearcher;

/**
 * 
 *
 */
public class MergedSearchProteinMonolink {
	
	private static final Logger log = Logger.getLogger(MergedSearchProteinMonolink.class);
			

	/* 
	 * The searches for the records that were found for this specific set of query keys
	 */
	public Collection<SearchDTO> getSearches() {
		
		return searches;
	}
	

	public void setSearches(List<SearchDTO> searches) {
		this.searches = searches;
	}


	public SearcherCutoffValuesRootLevel getSearcherCutoffValuesRootLevel() {
		return searcherCutoffValuesRootLevel;
	}


	public void setSearcherCutoffValuesRootLevel(
			SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel) {
		this.searcherCutoffValuesRootLevel = searcherCutoffValuesRootLevel;
	}
	
	public MergedSearchProtein getProtein() {
		return protein;
	}
	public void setProtein(MergedSearchProtein protein) {
		this.protein = protein;
	}
	public int getProteinPosition() {
		return proteinPosition;
	}
	public void setProteinPosition(int proteinPosition) {
		this.proteinPosition = proteinPosition;
	}

	public int getNumUniquePsms() {
		return numUniquePsms;
	}
	public void setNumUniquePsms(int numUniquePsms) {
		this.numUniquePsms = numUniquePsms;
	}
	
	
	public int getNumPeptides() throws Exception {

		try {
		
			if( this.numPeptides == -1 )
				this.numPeptides = MergedSearchMonolinkPeptideSearcher.getInstance().getNumPeptides( this );

			return this.numPeptides;

		} catch ( Exception e ) {
			
			log.error( "Exception in getNumPeptides()", e );
			throw e;
		}
	}
	public int getNumUniquePeptides() throws Exception {

		try {

			if( this.numUniquePeptides == -1 )
				this.numUniquePeptides = MergedSearchMonolinkPeptideSearcher.getInstance().getNumUniquePeptides( this );

			return this.numUniquePeptides;

		} catch ( Exception e ) {
			
			log.error( "Exception in getNumUniquePeptides()", e );
			throw e;
		}
	}
	
	public void setNumPsms(Integer numPsms) {
		
		// TODO  TEMP commented out
		this.numPsms = numPsms;
	}


	public int getNumPsms() throws Exception {
		
		try {
			

			//  Use code in  SearchProteinMonolink.getNumPsms() for each search
//			
//			NumPsmsForProteinCriteriaSearcher.getInstance().getNumPsmsForMonolink(
//					this.getSearch().getId(),
//					this.getSearcherCutoffValuesSearchLevel(),
//					this.getProtein1().getNrProtein().getNrseqId(),
//					this.getProtein2().getNrProtein().getNrseqId(),
//					this.getProtein1Position(),
//					this.getProtein2Position() );

			int totalNumPsms = 0;
			
			for ( SearchDTO search : searches ) {
								
				int searchId = search.getId();
				
				SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel = searcherCutoffValuesRootLevel.getPerSearchCutoffs( searchId );
				
				if ( searcherCutoffValuesSearchLevel == null ) {
					
					String msg = "No searcherCutoffValuesSearchLevel found in searcherCutoffValuesRootLevel for search id: " + searchId;
					log.error( msg );
					throw new Exception(msg);
				}
				
				
				int numPsmsForSearch = NumPsmsForProteinCriteriaSearcher.getInstance().getNumPsmsForMonolink(
						searchId,
						searcherCutoffValuesSearchLevel,
						this.getProtein().getNrProtein().getNrseqId(),
						this.getProteinPosition() );
				
				totalNumPsms += numPsmsForSearch;
			}
			
			this.numPsms = totalNumPsms;

			return this.numPsms;
			
		} catch ( Exception e ) {
			
			log.error( "Exception in getNumPsms()", e );
			throw e;
		}
	}


	public Map<SearchDTO, SearchProteinMonolink> getSearchProteinMonolinks() throws Exception {
		
		if ( searchProteinMonolinks == null ) {

			// add search-level info for the protein looplinks:
			
			searchProteinMonolinks = new TreeMap<SearchDTO, SearchProteinMonolink>();
			

			for ( SearchDTO search : searches ) {

				int searchId = search.getId(); 

				SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel = searcherCutoffValuesRootLevel.getPerSearchCutoffs( searchId );

				if ( searcherCutoffValuesSearchLevel == null ) {

					String msg = "No searcherCutoffValuesSearchLevel found in searcherCutoffValuesRootLevel for search id: " + searchId;
					log.error( msg );
					throw new Exception(msg);
				}

				SearchProteinMonolinkWrapper searchProteinMonolinkWrapper = 
						SearchProteinMonolinkSearcher.getInstance().searchOnSearch(
								search, 
								searcherCutoffValuesSearchLevel, 
								this.getProtein().getNrProtein(),
								this.getProteinPosition()
								);
				
				if ( searchProteinMonolinkWrapper != null ) {

					SearchProteinMonolink tlink = searchProteinMonolinkWrapper.getSearchProteinMonolink(); 

					if( tlink != null ) {
						searchProteinMonolinks.put( search, tlink );
					}
				}
			}
			
		}
		
		return searchProteinMonolinks;
	}

	
//	public Map<SearchDTO, SearchProteinMonolink> getSearchProteinMonolinks() {
//
//		try {
//
//			throw new RuntimeException("Not Supported");
//
////			return searchProteinMonolinks;
//
//
//		} catch ( RuntimeException e ) {
//
//			log.error( "Exception in getSearchProteinMonolinks()", e );
//			throw e;
//		}
//	}
//	public void setSearchProteinMonolinks(
//			Map<SearchDTO, SearchProteinMonolink> searchProteinMonolinks) {
//		this.searchProteinMonolinks = searchProteinMonolinks;
//	}

	public int getNumSearches() {

		try {

			throw new RuntimeException("Not Supported");


//			if( this.searchProteinMonolinks == null ) return 0;
//			return this.searchProteinMonolinks.keySet().size();


		} catch ( RuntimeException e ) {

			log.error( "Exception in getNumSearches()", e );
			throw e;
		}
	}

//	public Collection<SearchDTO> getSearches() {
//		return this.searchProteinMonolinks.keySet();
//	}
	
	

	private MergedSearchProtein protein;
	private int proteinPosition;
	
	private Integer numPsms;


	private int numUniquePsms = -999;

	private int numPeptides = -1;
	

	private List<SearchDTO> searches;
	
	private Map<SearchDTO, SearchProteinMonolink> searchProteinMonolinks;
	
	private int numUniquePeptides = -1;
	

	
	private SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel;

}
