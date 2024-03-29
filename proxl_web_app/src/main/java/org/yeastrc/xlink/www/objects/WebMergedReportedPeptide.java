package org.yeastrc.xlink.www.objects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.dto.UnifiedReportedPeptideLookupDTO;
import org.yeastrc.xlink.utils.XLinkUtils;


/**
 * 
 *
 */
public class WebMergedReportedPeptide implements IMergedSearchLink {
	
	private static final Logger log = LoggerFactory.getLogger( WebMergedReportedPeptide.class);

	/**
	 * @return
	 */
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
	
	/**
	 * @param numPsms
	 */
	public void setNumPsms(int numPsms) {
		this.numPsms = numPsms;
		numPsmsSet = true;
	}

	/**
	 * @return
	 * @throws Exception
	 */
	public int getNumPsms() throws Exception {
		
		if ( numPsmsSet ) {
			return numPsms;
		}
		try {
			throw new ProxlWebappInternalErrorException( "setNumPsms(int numPsms) not called" );
		} catch ( Exception e ) {
			String msg = "Error getting num psms";
			log.error( msg, e );
			throw e;
		}
	}

	/**
	 * @return
	 * @throws Exception
	 */
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

	public String getModsStringPeptide1() throws Exception {

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
	public String getModsStringPeptide2() throws Exception {
		
		if ( mergedSearchPeptideCrosslink != null ) {
			
			return mergedSearchPeptideCrosslink.getModsStringPeptide2();
		}

		if ( mergedSearchPeptideDimer != null ) {
			
			return mergedSearchPeptideDimer.getModsStringPeptide2();
		}
		
		
		return "";	
	}


	public String getIsotopeLabelsStringPeptide1() throws Exception {

		if ( mergedSearchPeptideCrosslink != null ) {
			
			return mergedSearchPeptideCrosslink.getIsotopeLabelsStringPeptide1();
		}
		
		if ( mergedSearchPeptideLooplink != null ) {
			
			return mergedSearchPeptideLooplink.getIsotopeLabelsStringPeptide();
		}
		
		
		if ( mergedSearchPeptideUnlinked != null ) {
			
			return mergedSearchPeptideUnlinked.getIsotopeLabelsStringPeptide();
		}
		
		if ( mergedSearchPeptideDimer != null ) {
			
			return mergedSearchPeptideDimer.getIsotopeLabelsStringPeptide1();
		}
		
		return "";	
	}
	public String getIsotopeLabelsStringPeptide2() throws Exception {
		
		if ( mergedSearchPeptideCrosslink != null ) {
			
			return mergedSearchPeptideCrosslink.getIsotopeLabelsStringPeptide2();
		}

		if ( mergedSearchPeptideDimer != null ) {
			
			return mergedSearchPeptideDimer.getIsotopeLabelsStringPeptide2();
		}
		
		
		return "";	
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
	

	public List<AnnValuePeptPsmListsPair> getPeptidePsmAnnotationValueListsForEachSearch() {
		return peptidePsmAnnotationValueListsForEachSearch;
	}



	public void setPeptidePsmAnnotationValueListsForEachSearch(
			List<AnnValuePeptPsmListsPair> peptidePsmAnnotationValueListsForEachSearch) {
		this.peptidePsmAnnotationValueListsForEachSearch = peptidePsmAnnotationValueListsForEachSearch;
	}



	@Override
	public Collection<SearchDTO> getSearches() {
		return searches;
	}
	public void setSearches(Collection<SearchDTO> searches) {
		this.searches = searches;
	}




	private MergedSearchPeptideCrosslink mergedSearchPeptideCrosslink;
	private MergedSearchPeptideLooplink mergedSearchPeptideLooplink;

	private MergedSearchPeptideUnlinked mergedSearchPeptideUnlinked;
	private MergedSearchPeptideDimer mergedSearchPeptideDimer;


	Map<SearchDTO, WebReportedPeptide> mappedSearches = null;

	private List<SearchBooleanWrapper> searchContainsPeptide;

	private int unifiedReportedPeptideId;
	private Collection<Integer> searchIds;
	private Collection<SearchDTO> searches;

	private int numSearches;

	private int numPsms;
	/**
	 * true when SetNumPsms has been called
	 */
	private boolean numPsmsSet;


	/**
	 * Used for display on web page
	 */
	private List<AnnValuePeptPsmListsPair> peptidePsmAnnotationValueListsForEachSearch;



}
