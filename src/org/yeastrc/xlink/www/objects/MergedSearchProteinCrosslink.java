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
import org.yeastrc.xlink.www.searcher.MergedSearchCrosslinkPeptideSearcher;
import org.yeastrc.xlink.www.searcher.SearchProteinCrosslinkSearcher;

/**
 * 
 *
 */
public class MergedSearchProteinCrosslink implements IProteinCrosslink, IMergedSearchLink {

	private static final Logger log = Logger.getLogger(MergedSearchProteinCrosslink.class);
	
			
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

	

	public Map<SearchDTO, SearchProteinCrosslink> getSearchProteinCrosslinks() throws Exception {
		
		try {

			//  Used in Web service for isFilterOnlyOnePSM and isFilterOnlyOnePeptide


			if ( searchProteinCrosslinks == null ) {

				searchProteinCrosslinks = new TreeMap<SearchDTO, SearchProteinCrosslink>();


				for ( SearchDTO search : searches ) {
					
					int searchId = search.getId();
					
					SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel = searcherCutoffValuesRootLevel.getPerSearchCutoffs( searchId );

					if ( searcherCutoffValuesSearchLevel == null ) {

						String msg = "No searcherCutoffValuesSearchLevel found in searcherCutoffValuesRootLevel for search id: " + searchId;
						log.error( msg );
						throw new Exception(msg);
					}

					SearchProteinCrosslinkWrapper searchProteinCrosslinkWrapper =
							SearchProteinCrosslinkSearcher.getInstance().search(
									search, 
									searcherCutoffValuesSearchLevel, 
									this.getProtein1().getNrProtein(),
									this.getProtein2().getNrProtein(),
									this.getProtein1Position(),
									this.getProtein2Position()
									);

					if( searchProteinCrosslinkWrapper != null ) {

						SearchProteinCrosslink tlink = searchProteinCrosslinkWrapper.getSearchProteinCrosslink();

						if( tlink != null ) {
							searchProteinCrosslinks.put( search, tlink );
						}
					}
				}
			}

			return searchProteinCrosslinks;

		} catch (Exception e ) {

			String msg = "Error in getSearchProteinCrosslinks(): " + e.toString();

			log.error( msg, e );

			throw e;
		}
	}

	public void setSearchProteinCrosslinks(
			Map<SearchDTO, SearchProteinCrosslink> searchProteinCrosslinks) {
		
		this.searchProteinCrosslinks = searchProteinCrosslinks;
	}



	
	
	
	public MergedSearchProtein getProtein1() {
		return protein1;
	}
	public void setProtein1(MergedSearchProtein protein1) {
		this.protein1 = protein1;
	}
	public MergedSearchProtein getProtein2() {
		return protein2;
	}
	public void setProtein2(MergedSearchProtein protein2) {
		this.protein2 = protein2;
	}
	public int getProtein1Position() {
		return protein1Position;
	}
	public void setProtein1Position(int protein1Position) {
		this.protein1Position = protein1Position;
	}
	public int getProtein2Position() {
		return protein2Position;
	}
	public void setProtein2Position(int protein2Position) {
		this.protein2Position = protein2Position;
	}
	
	public int getNumPsms() throws Exception {

		try {

			if( this.numPsms == null ) {
		
			
			//  Use code in  SearchProteinCrosslink.getNumPsms() for each search
		
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
						.getNumPeptidesPSMsForCrosslink(
								searchId,
								searcherCutoffValuesSearchLevel,
								this.getProtein1().getNrProtein().getNrseqId(),
								this.getProtein2().getNrProtein().getNrseqId(),
								this.getProtein1Position(),
								this.getProtein2Position(),
								YRC_NRSEQUtils.getDatabaseIdFromName( search.getFastaFilename() ) );
				
				
				totalNumPsms += numPeptidesPSMsForProteinCriteriaResult.getNumPSMs();
				
			}
			
			this.numPsms = totalNumPsms;
		}
		
		return this.numPsms;
		

		} catch ( Exception e ) {

			String msg = "Exception in getNumPsms()";

			log.error( msg, e );

			throw e;
		}
		
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

	
	public int getNumLinkedPeptides() throws Exception {

		try {


			if( this.numLinkedPeptides == -1 ) {

//				if ( true ) {
//					
//					throw new Exception("NOT IMPLEMENTED");
//				}

				this.numLinkedPeptides = MergedSearchCrosslinkPeptideSearcher.getInstance().getNumLinkedPeptides( this );
				
			}

			return this.numLinkedPeptides;

		} catch ( Exception e ) {

			String msg = "Exception in getNumLinkedPeptides( MergedSearchProteinCrosslink crosslink ): " 
					+ " this.getProtein1().getNrProtein().getNrseqId(): " + this.getProtein1().getNrProtein().getNrseqId()
					+ " this.getProtein2().getNrProtein().getNrseqId(): " + this.getProtein2().getNrProtein().getNrseqId();

			log.error( msg, e );

			throw e;
		}
	}
	public int getNumUniqueLinkedPeptides() throws Exception {
		
		try {
			
			if( this.numUniqueLinkedPeptides == -1 )
				this.numUniqueLinkedPeptides = MergedSearchCrosslinkPeptideSearcher.getInstance().getNumUniqueLinkedPeptides( this );

			return this.numUniqueLinkedPeptides;
			
		} catch ( Exception e ) {
			
			String msg = "Exception in getNumUniqueLinkedPeptides( MergedSearchProteinCrosslink crosslink ): " 
					+ " this.getProtein1().getNrProtein().getNrseqId(): " + this.getProtein1().getNrProtein().getNrseqId()
					+ " this.getProtein2().getNrProtein().getNrseqId(): " + this.getProtein2().getNrProtein().getNrseqId();
			
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



	

	private MergedSearchProtein protein1;
	private MergedSearchProtein protein2;
	private int protein1Position;
	private int protein2Position;
	
	private Integer numPsms;
	private int numUniquePsms;

	private int numLinkedPeptides = -1;

	private int numUniqueLinkedPeptides = -1;

	
	private List<SearchDTO> searches;
	
	private Map<SearchDTO, SearchProteinCrosslink> searchProteinCrosslinks;
	

	




	private SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel;

	/**
	 * Used for display on web page
	 */
	private List<AnnValuePeptPsmListsPair> peptidePsmAnnotationValueListsForEachSearch;


}
