package org.yeastrc.xlink.www.objects;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.searcher_result_objects.NumPeptidesPSMsForProteinCriteriaResult;
import org.yeastrc.xlink.searchers.NumPeptidesPSMsForProteinCriteriaSearcher;
import org.yeastrc.xlink.utils.YRC_NRSEQUtils;
import org.yeastrc.xlink.www.searcher.MergedSearchLooplinkPeptideSearcher;
import org.yeastrc.xlink.www.searcher.SearchProteinLooplinkSearcher;

public class MergedSearchProteinLooplink implements IProteinLooplink, IMergedSearchLink {
	
	private static final Logger log = Logger.getLogger( MergedSearchProteinLooplink.class );

	/* 
	 * The searches for the records that were found for this specific set of query keys
	 */
	@Override
	public Collection<SearchDTO> getSearches() {
		
		return searches;
	}
	
	public void setSearches(List<SearchDTO> searches) {
		this.searches = searches;
	}

	


	public Map<SearchDTO, SearchProteinLooplink> getSearchProteinLooplinks() throws Exception {
		
		if ( searchProteinLooplinks == null ) {

			// add search-level info for the protein looplinks:
			
			searchProteinLooplinks = new TreeMap<SearchDTO, SearchProteinLooplink>();
			

			for ( SearchDTO search : searches ) {

				int searchId = search.getId(); 

				SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel = searcherCutoffValuesRootLevel.getPerSearchCutoffs( searchId );

				if ( searcherCutoffValuesSearchLevel == null ) {

					String msg = "No searcherCutoffValuesSearchLevel found in searcherCutoffValuesRootLevel for search id: " + searchId;
					log.error( msg );
					throw new Exception(msg);
				}

				SearchProteinLooplinkWrapper searchProteinLooplinkWrapper = 
						SearchProteinLooplinkSearcher.getInstance().search(
								search, 
								searcherCutoffValuesSearchLevel, 
								this.getProtein().getNrProtein(),
								this.getProteinPosition1(),
								this.getProteinPosition2()
								);
				
				if ( searchProteinLooplinkWrapper != null ) {

					SearchProteinLooplink tlink = searchProteinLooplinkWrapper.getSearchProteinLooplink(); 

					if( tlink != null ) {
						searchProteinLooplinks.put( search, tlink );
					}
				}
			}
			
		}
		
		return searchProteinLooplinks;
	}


	public void setSearchProteinLooplinks(
			Map<SearchDTO, SearchProteinLooplink> searchProteinLooplinks) {
		
		this.searchProteinLooplinks = searchProteinLooplinks;
	}



	
	public MergedSearchProtein getProtein() {
		return protein;
	}
	public void setProtein(MergedSearchProtein protein) {
		this.protein = protein;
	}
	public int getProteinPosition1() {
		return proteinPosition1;
	}
	public void setProteinPosition1(int proteinPosition1) {
		this.proteinPosition1 = proteinPosition1;
	}
	public int getProteinPosition2() {
		return proteinPosition2;
	}
	public void setProteinPosition2(int proteinPosition2) {
		this.proteinPosition2 = proteinPosition2;
	}

	public int getNumPsms() throws Exception {
		
		if( this.numPsms == null ) {
		

			//  Use code in  SearchProteinLooplink.getNumPsms() for each search

			int totalNumPsms = 0;
			
			for ( SearchDTO search : searches ) {
				
				int searchId = search.getId();
				
				SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel = searcherCutoffValuesRootLevel.getPerSearchCutoffs( searchId );
				
				if ( searcherCutoffValuesSearchLevel == null ) {
					
					String msg = "No searcherCutoffValuesSearchLevel found in searcherCutoffValuesRootLevel for search id: " + searchId;
					log.error( msg );
					throw new Exception(msg);
				}
				

				NumPeptidesPSMsForProteinCriteriaResult numPeptidesPSMsForProteinCriteriaResult =
						NumPeptidesPSMsForProteinCriteriaSearcher.getInstance()
						.getNumPeptidesPSMsForLooplink(
								searchId,
								searcherCutoffValuesSearchLevel,
								this.getProtein().getNrProtein().getNrseqId(),
								this.getProteinPosition1(),
								this.getProteinPosition2(),
								YRC_NRSEQUtils.getDatabaseIdFromName( search.getFastaFilename() ) );
				
				totalNumPsms += numPeptidesPSMsForProteinCriteriaResult.getNumPSMs();
				
			}
			
			this.numPsms = totalNumPsms;
		}
		
		return this.numPsms;
	}
	
	public void setNumPsms(Integer numPsms) {
		this.numPsms = numPsms;
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
				this.numPeptides = MergedSearchLooplinkPeptideSearcher.getInstance().getNumPeptides( this );

			return this.numPeptides;
		
		} catch ( Exception e ) {

			String msg = "Exception in getNumPeptides( MergedSearchProteinLooplink looplink ): " 
					+ " this.getProtein().getNrProtein().getNrseqId(): " + this.getProtein().getNrProtein().getNrseqId();

			log.error( msg, e );

			throw e;
		}
	}
	
	public int getNumUniquePeptides() throws Exception {

		try {
			
			if( this.numUniquePeptides == -1 )
				this.numUniquePeptides = MergedSearchLooplinkPeptideSearcher.getInstance().getNumUniquePeptides( this );

			return this.numUniquePeptides;

		} catch ( Exception e ) {

			String msg = "Exception in getNumUniquePeptides( MergedSearchProteinLooplink looplink ): " 
					+ " this.getProtein().getNrProtein().getNrseqId(): " + this.getProtein().getNrProtein().getNrseqId();

			log.error( msg, e );

			throw e;
		}
	}
	
	
	
	

	public int getNumSearches() {
		if( this.searches == null ) return 0;
		return this.searches.size();
	}


	public SearcherCutoffValuesRootLevel getSearcherCutoffValuesRootLevel() {
		return searcherCutoffValuesRootLevel;
	}

	public void setSearcherCutoffValuesRootLevel(
			SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel) {
		this.searcherCutoffValuesRootLevel = searcherCutoffValuesRootLevel;
	}

	public List<AnnValuePeptPsmListsPair> getPeptidePsmAnnotationValueListsForEachSearch() {
		return peptidePsmAnnotationValueListsForEachSearch;
	}

	public void setPeptidePsmAnnotationValueListsForEachSearch(
			List<AnnValuePeptPsmListsPair> peptidePsmAnnotationValueListsForEachSearch) {
		this.peptidePsmAnnotationValueListsForEachSearch = peptidePsmAnnotationValueListsForEachSearch;
	}


	private List<SearchDTO> searches;
	

	private Map<SearchDTO, SearchProteinLooplink> searchProteinLooplinks;


	private MergedSearchProtein protein;
	private int proteinPosition1;
	private int proteinPosition2;

	private SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel;



	private Integer numPsms;
	private int numUniquePsms;

	private int numPeptides = -1;
	

	private int numUniquePeptides = -1;
	
	/**
	 * For web display
	 */
	private List<AnnValuePeptPsmListsPair> peptidePsmAnnotationValueListsForEachSearch;

}
