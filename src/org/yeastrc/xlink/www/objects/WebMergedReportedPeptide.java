package org.yeastrc.xlink.www.objects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.dto.PeptideDTO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.dto.UnifiedReportedPeptideLookupDTO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.searcher.PsmCountForSearchIdReportedPeptideIdSearcher;
import org.yeastrc.xlink.utils.XLinkUtils;
import org.yeastrc.xlink.www.searcher.ReportedPeptideIdsForSearchIdsUnifiedPeptideIdSearcher;




public class WebMergedReportedPeptide implements IMergedSearchLink {
	
	private static final Logger log = Logger.getLogger(WebMergedReportedPeptide.class);



	public String getLinkType() {
		
		if ( mergedSearchPeptideCrosslink != null ) {
			
			return XLinkUtils.CROSS_TYPE_STRING_UPPERCASE;
		}
		
		if ( mergedSearchPeptideLooplink != null ) {
			
			return XLinkUtils.LOOP_TYPE_STRING_UPPERCASE;
		}
		
		if ( mergedSearchPeptideUnlinked != null ) {
			
			return XLinkUtils.UNLINKED_TYPE_STRING_UPPERCASE;
		}
		
		if ( mergedSearchPeptideDimer != null ) {
			
			return XLinkUtils.DIMER_TYPE_STRING_UPPERCASE;
		}
		
		
		
		return "UNKNOWN";	
	}

	
	
	public void setNumPsms(int numPsms) {
		this.numPsms = numPsms;
		numPsmsSet = true;
	}


	public int getNumPsms() throws Exception {
		
		if ( numPsmsSet ) {
			
			return numPsms;
		}
		

//		num psms is always based on searching psm table for: search id, reported peptide id, and peptide and psm cutoffs.
//		reported peptide id can be gotten from unified reported peptide id and search id

		
		//   Use WebReportedPeptide.getNumPsms() code for each search id / reported peptide id
		
		try {

			int totalNumPsms = 0;

			List<ReportedPeptideIdsForSearchIdsUnifiedPeptideIdResult>  reppeptideIdSearchIdList = 
					ReportedPeptideIdsForSearchIdsUnifiedPeptideIdSearcher.getInstance()
					.getReportedPeptideIdsForSearchIdsAndUnifiedReportedPeptideId( searchIds, unifiedReportedPeptideId );
			
			for ( ReportedPeptideIdsForSearchIdsUnifiedPeptideIdResult item : reppeptideIdSearchIdList ) {
				
				int searchId = item.getSearchId();

				SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel = 
						searcherCutoffValuesRootLevel.getPerSearchCutoffs( searchId );
				
				if ( searcherCutoffValuesSearchLevel == null ) {
					
					searcherCutoffValuesSearchLevel = new SearcherCutoffValuesSearchLevel();
					
//					String msg = "Unable to get cutoffs for search id: " + searchId;
//					log.error( msg );
//					throw new Exception(msg);
				}

				int numPsms = PsmCountForSearchIdReportedPeptideIdSearcher.getInstance()
						.getPsmCountForSearchIdReportedPeptideId( item.getReportedPeptideId(), item.getSearchId(), searcherCutoffValuesSearchLevel );

				totalNumPsms += numPsms;
			}


			numPsms = totalNumPsms;

			numPsmsSet = true;

			return numPsms;
			
		} catch ( Exception e ) {
			
			String msg = "Error getting num psms";
			log.error( msg, e );
			throw e;
		}
	}
		


	public UnifiedReportedPeptideLookupDTO getUnifiedReportedPeptide() throws Exception {
		
		if ( mergedSearchPeptideCrosslink != null ) {
			
			return mergedSearchPeptideCrosslink.getUnifiedReportedPeptideLookupDTO();
		}

		if ( mergedSearchPeptideLooplink != null ) {

			return mergedSearchPeptideLooplink.getUnifiedReportedPeptideLookupDTO();
		}


		if ( mergedSearchPeptideUnlinked != null ) {

			return mergedSearchPeptideUnlinked.getUnifiedReportedPeptideLookupDTO();
		}

		if ( mergedSearchPeptideDimer != null ) {

			return mergedSearchPeptideDimer.getUnifiedReportedPeptideLookupDTO();
		}


		String msg =  "in getUnifiedReportedPeptide(): all of the references are null." ;
		
		log.error( msg );
		
		throw new RuntimeException( msg );
	}
	


	public PeptideDTO getPeptide1() throws Exception {
		
		if ( mergedSearchPeptideCrosslink != null ) {
			
			return mergedSearchPeptideCrosslink.getPeptide1();
		}
		
		if ( mergedSearchPeptideLooplink != null ) {
			
			return mergedSearchPeptideLooplink.getPeptide();
		}
		
		
		if ( mergedSearchPeptideUnlinked != null ) {
			
			return mergedSearchPeptideUnlinked.getPeptide();
		}
		
		if ( mergedSearchPeptideDimer != null ) {
			
			return mergedSearchPeptideDimer.getPeptide1();
		}

		return null;	
	}

	public PeptideDTO getPeptide2() throws Exception {
		
		if ( mergedSearchPeptideCrosslink != null ) {
			
			return mergedSearchPeptideCrosslink.getPeptide2();
		}
		
		if ( mergedSearchPeptideDimer != null ) {
			
			return mergedSearchPeptideDimer.getPeptide2();
		}
		
		return null;	
	}

	public String getPeptide1Position() throws Exception {
		
		if ( mergedSearchPeptideCrosslink != null ) {
			
			return Integer.toString( mergedSearchPeptideCrosslink.getPeptide1Position() );
		}
		
		if ( mergedSearchPeptideLooplink != null ) {
			
			return Integer.toString( mergedSearchPeptideLooplink.getPeptidePosition1() );
		}
		
		return "";	
	}

	public String getPeptide2Position() throws Exception {
		
		if ( mergedSearchPeptideCrosslink != null ) {
			
			return Integer.toString( mergedSearchPeptideCrosslink.getPeptide2Position() );
		}
		
		if ( mergedSearchPeptideLooplink != null ) {
			
			return Integer.toString( mergedSearchPeptideLooplink.getPeptidePosition2() );
		}
		
		return "";	
	}

	public String getModsStringPeptide1() {

		if ( mergedSearchPeptideCrosslink != null ) {
			
			return mergedSearchPeptideCrosslink.getModsStringPeptide1();
		}
		
		if ( mergedSearchPeptideLooplink != null ) {
			
			return mergedSearchPeptideLooplink.getModsStringPeptide();
		}
		
		
		if ( mergedSearchPeptideUnlinked != null ) {
			
			return mergedSearchPeptideUnlinked.getModsStringPeptide();
		}
		
		if ( mergedSearchPeptideDimer != null ) {
			
			return mergedSearchPeptideDimer.getModsStringPeptide1();
		}
		
		return "";	
	}
	public String getModsStringPeptide2() {
		
		if ( mergedSearchPeptideCrosslink != null ) {
			
			return mergedSearchPeptideCrosslink.getModsStringPeptide2();
		}

		if ( mergedSearchPeptideDimer != null ) {
			
			return mergedSearchPeptideDimer.getModsStringPeptide2();
		}
		
		
		return "";	
	}


	
	
	public Collection<SearchDTO> getSearches() {
		if ( mergedSearchPeptideCrosslink != null ) {
			
			return mergedSearchPeptideCrosslink.getSearches();
		}
		
		if ( mergedSearchPeptideLooplink != null ) {
			
			return mergedSearchPeptideLooplink.getSearches();
		}
		
		
		if ( mergedSearchPeptideUnlinked != null ) {
			
			return mergedSearchPeptideUnlinked.getSearches();
		}
		
		if ( mergedSearchPeptideDimer != null ) {
			
			return mergedSearchPeptideDimer.getSearches();
		}

		return null;	
	}

	

	public List<WebMergedProteinPosition> getPeptide1ProteinPositions() throws Exception {
		
		if ( mergedSearchPeptideCrosslink != null ) {
			
			return getPeptideProteinPositionsFromSearchProteinPositionList( mergedSearchPeptideCrosslink.getPeptide1ProteinPositions() );
		}
		
		if ( mergedSearchPeptideLooplink != null ) {
			
			return getPeptideProteinPositionsFromSearchProteinDoublePositionList( mergedSearchPeptideLooplink.getProteinPositions() );
		}
		
		if ( mergedSearchPeptideUnlinked != null ) {
			
			return getPeptideProteinsWithoutPositions( mergedSearchPeptideUnlinked.getProteinPositions() );
		}
		
		
		if ( mergedSearchPeptideDimer != null ) {
			
			return getPeptideProteinsWithoutPositions( mergedSearchPeptideDimer.getPeptide1ProteinPositions() );
		}
		
		return null;	
	}

	
	
	public List<WebMergedProteinPosition> getPeptide2ProteinPositions() throws Exception {
		
		if ( mergedSearchPeptideCrosslink != null ) {
			
			return getPeptideProteinPositionsFromSearchProteinPositionList( mergedSearchPeptideCrosslink.getPeptide2ProteinPositions() );

		}
		
		
		if ( mergedSearchPeptideDimer != null ) {
			
			return getPeptideProteinsWithoutPositions( mergedSearchPeptideDimer.getPeptide2ProteinPositions() );
		}

		return null;	

	}
	
	
	private List<WebMergedProteinPosition> getPeptideProteinsWithoutPositions( List<MergedSearchProteinPosition> searchProteinPositionPositionList ) {
		
		List<WebMergedProteinPosition> webProteinPositionList = new ArrayList<>();
		
		for ( MergedSearchProteinPosition searchProteinPosition : searchProteinPositionPositionList ) {
			
			WebMergedProteinPosition  webProteinPosition = new WebMergedProteinPosition();
			webProteinPositionList.add( webProteinPosition );
			
			webProteinPosition.setProtein(  searchProteinPosition.getProtein() );
			
			//  Position is not really a value
//			webProteinPosition.setPosition1( Integer.toString( searchProteinPosition.getPosition() ) );
		}
		
		return webProteinPositionList;
	}
	
	
	/**
	 * @param mergedSearchProteinDoublePositionList
	 * @return
	 */
	private List<WebMergedProteinPosition> getPeptideProteinPositionsFromSearchProteinDoublePositionList( List<MergedSearchProteinDoublePosition> mergedSearchProteinDoublePositionList ) {
		
		List<WebMergedProteinPosition> webMergedProteinPositionList = new ArrayList<>();
		
		for ( MergedSearchProteinDoublePosition mergedSearchProteinDoublePosition : mergedSearchProteinDoublePositionList ) {
			
			WebMergedProteinPosition  webMergedProteinPosition = new WebMergedProteinPosition();
			webMergedProteinPositionList.add( webMergedProteinPosition );
			
			webMergedProteinPosition.setProtein(  mergedSearchProteinDoublePosition.getProtein() );
			webMergedProteinPosition.setPosition1( Integer.toString( mergedSearchProteinDoublePosition.getPosition1() ) );
			webMergedProteinPosition.setPosition2( Integer.toString( mergedSearchProteinDoublePosition.getPosition2() ) );
		}
		
		return webMergedProteinPositionList;
	}
	
	
	
	/**
	 * @param searchProteinPositionList
	 * @return
	 */
	private List<WebMergedProteinPosition> getPeptideProteinPositionsFromSearchProteinPositionList( List<MergedSearchProteinPosition> mergedSearchProteinPositionList ) {
		
		List<WebMergedProteinPosition> webMergedProteinPositionList = new ArrayList<>();
		
		for ( MergedSearchProteinPosition mergedSearchProteinPosition : mergedSearchProteinPositionList ) {
			
			WebMergedProteinPosition  webMergedProteinPosition = new WebMergedProteinPosition();
			webMergedProteinPositionList.add( webMergedProteinPosition );
			
			webMergedProteinPosition.setProtein(  mergedSearchProteinPosition.getProtein() );
			webMergedProteinPosition.setPosition1( Integer.toString( mergedSearchProteinPosition.getPosition() ) );
			
		}
		
		return webMergedProteinPositionList;
	}
	
	
	

	public List<SearchBooleanWrapper> getSearchContainsPeptide() {
		return searchContainsPeptide;
	}

	public void setSearchContainsPeptide(
			List<SearchBooleanWrapper> searchContainsPeptide) {
		this.searchContainsPeptide = searchContainsPeptide;
	}

	public MergedSearchPeptideCrosslink getMergedSearchPeptideCrosslink() {
		return mergedSearchPeptideCrosslink;
	}


	public void setMergedSearchPeptideCrosslink(
			MergedSearchPeptideCrosslink mergedSearchPeptideCrosslink) {
		this.mergedSearchPeptideCrosslink = mergedSearchPeptideCrosslink;
	}
	
	
	public MergedSearchPeptideLooplink getMergedSearchPeptideLooplink() {
		return mergedSearchPeptideLooplink;
	}

	public void setMergedSearchPeptideLooplink(
			MergedSearchPeptideLooplink mergedSearchPeptideLooplink) {
		this.mergedSearchPeptideLooplink = mergedSearchPeptideLooplink;
	}

	public MergedSearchPeptideUnlinked getMergedSearchPeptideUnlinked() {
		return mergedSearchPeptideUnlinked;
	}





	public void setMergedSearchPeptideUnlinked(
			MergedSearchPeptideUnlinked mergedSearchPeptideUnlinked) {
		this.mergedSearchPeptideUnlinked = mergedSearchPeptideUnlinked;
	}





	public MergedSearchPeptideDimer getMergedSearchPeptideDimer() {
		return mergedSearchPeptideDimer;
	}





	public void setMergedSearchPeptideDimer(
			MergedSearchPeptideDimer mergedSearchPeptideDimer) {
		this.mergedSearchPeptideDimer = mergedSearchPeptideDimer;
	}
	public int getUnifiedReportedPeptideId() {
		return unifiedReportedPeptideId;
	}
	public void setUnifiedReportedPeptideId(int unifiedReportedPeptideId) {
		this.unifiedReportedPeptideId = unifiedReportedPeptideId;
	}


	public int getNumSearches() {
		return numSearches;
	}
	public void setNumSearches(int numSearches) {
		this.numSearches = numSearches;
	}



	public Collection<Integer> getSearchIds() {
		return searchIds;
	}
	public void setSearchIds(Collection<Integer> searchIds) {
		this.searchIds = searchIds;
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




	private MergedSearchPeptideCrosslink mergedSearchPeptideCrosslink;
	private MergedSearchPeptideLooplink mergedSearchPeptideLooplink;

	private MergedSearchPeptideUnlinked mergedSearchPeptideUnlinked;
	private MergedSearchPeptideDimer mergedSearchPeptideDimer;


	Map<SearchDTO, WebReportedPeptide> mappedSearches = null;

	private List<SearchBooleanWrapper> searchContainsPeptide;

	private int unifiedReportedPeptideId;
	private Collection<Integer> searchIds;



	private int numSearches;

	private int numPsms;
	/**
	 * true when SetNumPsms has been called
	 */
	private boolean numPsmsSet;


	
	/**
	 *  Used to get numPsms when they are not already set
	 */
	private SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel;






	/**
	 * Used for display on web page
	 */
	private List<AnnValuePeptPsmListsPair> peptidePsmAnnotationValueListsForEachSearch;










}
