package org.yeastrc.xlink.www.objects;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.base_searcher.PsmCountForSearchIdReportedPeptideIdSearcher;
import org.yeastrc.xlink.base_searcher.PsmCountForUniquePSM_SearchIdReportedPeptideId_Searcher;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;
import org.yeastrc.xlink.www.dto.PeptideDTO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders.Cached_ReportedPeptideDTO;



/**
 * Class for data for row in Peptide page
 *
 */
public class WebReportedPeptide implements SearchPeptideCommonLinkWebserviceResultIF{

	private static final Logger log = Logger.getLogger(WebReportedPeptide.class);




	public void setNumPsms(int numPsms) {
		this.numPsms = numPsms;
		numPsmsSet = true;
	}


	public int getNumPsms() throws Exception {


		try {

			if ( numPsmsSet ) {

				return numPsms;
			}

			//		num psms is always based on searching psm table for: search id, reported peptide id, and psm cutoff values.

			numPsms = 
					PsmCountForSearchIdReportedPeptideIdSearcher.getInstance()
					.getPsmCountForSearchIdReportedPeptideId( reportedPeptideId, searchId, searcherCutoffValuesSearchLevel );

			numPsmsSet = true;

			return numPsms;

		} catch ( Exception e ) {

			log.error( "getNumPsms() Exception: " + e.toString(), e );

			throw e;
		}
	}


	public int getNumNonUniquePsms() throws Exception {
		try {
			int numPsms = this.getNumPsms();
			int numUniquePsms = this.getNumUniquePsms();
			int nonUniquePSMs = numPsms - numUniquePsms;
			return nonUniquePSMs;
			
		} catch ( Exception e ) {
			log.error( "getNumNonUniquePsms() Exception: " + e.toString(), e );
			throw e;
		}
	}

	public void setNumUniquePsms(int numUniquePsms) {
		this.numUniquePsms = numUniquePsms;
		numUniquePsmsSet = true;
	}

	public int getNumUniquePsms() throws Exception {
		try {
			if ( numUniquePsmsSet ) {
				return numUniquePsms;
			}
			
			this.numUniquePsms = PsmCountForUniquePSM_SearchIdReportedPeptideId_Searcher.getInstance()
					.getPsmCountForUniquePSM_SearchIdReportedPeptideId( reportedPeptideId, searchId, searcherCutoffValuesSearchLevel );
			
			numUniquePsmsSet = true;
			
			return numUniquePsms;
			
		} catch ( Exception e ) {

			log.error( "getNumUniquePsms() Exception: " + e.toString(), e );

			throw e;
		}
	}





	public ReportedPeptideDTO getReportedPeptide() throws Exception {
		
		try {
			if ( reportedPeptide == null ) {

				reportedPeptide = 
						Cached_ReportedPeptideDTO.getInstance().getReportedPeptideDTO( reportedPeptideId );
			}

			return reportedPeptide;

		} catch ( Exception e ) {

			log.error( "getReportedPeptide() Exception: " + e.toString(), e );

			throw e;
		}
			
	}


	public void setReportedPeptide(ReportedPeptideDTO reportedPeptide) {
		this.reportedPeptide = reportedPeptide;
	}




	public PeptideDTO getPeptide1() throws Exception {

		try {

			if ( searchPeptideCrosslink != null ) {

				return searchPeptideCrosslink.getPeptide1();
			}

			if ( searchPeptideLooplink != null ) {

				return searchPeptideLooplink.getPeptide();
			}


			if ( searchPeptideUnlinked != null ) {

				return searchPeptideUnlinked.getPeptide();
			}

			if ( searchPeptideDimer != null ) {

				return searchPeptideDimer.getPeptide1();
			}

			return null;	


		} catch ( Exception e ) {

			log.error( "getPeptide1() Exception: " + e.toString(), e );

			throw e;
		}
	}

	public PeptideDTO getPeptide2() throws Exception {

		try {

			if ( searchPeptideCrosslink != null ) {

				return searchPeptideCrosslink.getPeptide2();
			}

			if ( searchPeptideDimer != null ) {

				return searchPeptideDimer.getPeptide2();
			}

			return null;	


		} catch ( Exception e ) {

			log.error( "getPeptide2() Exception: " + e.toString(), e );

			throw e;
		}
	}

	public String getPeptide1Position() throws Exception {

		try {

			if ( searchPeptideCrosslink != null ) {

				return Integer.toString( searchPeptideCrosslink.getPeptide1Position() );
			}

			if ( searchPeptideLooplink != null ) {

				return Integer.toString( searchPeptideLooplink.getPeptidePosition1() );
			}

			return "";	


		} catch ( Exception e ) {

			log.error( "getPeptide1Position() Exception: " + e.toString(), e );

			throw e;
		}
	}

	public String getPeptide2Position() throws Exception {

		try {

			if ( searchPeptideCrosslink != null ) {

				return Integer.toString( searchPeptideCrosslink.getPeptide2Position() );
			}

			if ( searchPeptideLooplink != null ) {

				return Integer.toString( searchPeptideLooplink.getPeptidePosition2() );
			}

			return "";	


		} catch ( Exception e ) {

			log.error( "getPeptide2Position() Exception: " + e.toString(), e );

			throw e;
		}
	}




	public List<WebProteinPosition> getPeptide1ProteinPositions() throws Exception {

		try {

			if ( searchPeptideCrosslink != null ) {

				return getPeptideProteinPositionsFromSearchProteinPositionList( searchPeptideCrosslink.getPeptide1ProteinPositions() );
			}

			if ( searchPeptideLooplink != null ) {

				return getPeptideProteinPositionsFromSearchProteinDoublePositionList( searchPeptideLooplink.getPeptideProteinPositions() );
			}

			if ( searchPeptideUnlinked != null ) {

				return getPeptideProteinsWithoutPositions( searchPeptideUnlinked.getPeptideProteinPositions() );
			}


			if ( searchPeptideDimer != null ) {

				return getPeptideProteinsWithoutPositions( searchPeptideDimer.getPeptide1ProteinPositions() );
			}

			return null;	


		} catch ( Exception e ) {

			log.error( "getPeptide1ProteinPositions() Exception: " + e.toString(), e );

			throw e;
		}
	}



	public List<WebProteinPosition> getPeptide2ProteinPositions() throws Exception {

		try {

			if ( searchPeptideCrosslink != null ) {

				return getPeptideProteinPositionsFromSearchProteinPositionList( searchPeptideCrosslink.getPeptide2ProteinPositions() );

			}


			if ( searchPeptideDimer != null ) {

				return getPeptideProteinsWithoutPositions( searchPeptideDimer.getPeptide2ProteinPositions() );
			}

			return null;	


		} catch ( Exception e ) {

			log.error( "getPeptide2ProteinPositions() Exception: " + e.toString(), e );

			throw e;
		}

	}


	private List<WebProteinPosition> getPeptideProteinsWithoutPositions( List<SearchProteinPosition> searchProteinPositionPositionList ) {

		try {

			List<WebProteinPosition> webProteinPositionList = new ArrayList<>();

			for ( SearchProteinPosition searchProteinPosition : searchProteinPositionPositionList ) {

				WebProteinPosition  webProteinPosition = new WebProteinPosition();
				webProteinPositionList.add( webProteinPosition );

				webProteinPosition.setProtein(  searchProteinPosition.getProtein() );

				//  Position is not really a value
				//			webProteinPosition.setPosition1( Integer.toString( searchProteinPosition.getPosition() ) );
			}

			return webProteinPositionList;


		} catch ( Exception e ) {

			log.error( "getPeptideProteinsWithoutPositions() Exception: " + e.toString(), e );

			throw e;
		}
	}


	/**
	 * @param searchProteinDoublePositionList
	 * @return
	 */
	private List<WebProteinPosition> getPeptideProteinPositionsFromSearchProteinDoublePositionList( List<SearchProteinDoublePosition> searchProteinDoublePositionList ) {

		try {

			List<WebProteinPosition> webProteinPositionList = new ArrayList<>();

			for ( SearchProteinDoublePosition searchProteinDoublePosition : searchProteinDoublePositionList ) {

				WebProteinPosition  webProteinPosition = new WebProteinPosition();
				webProteinPositionList.add( webProteinPosition );

				webProteinPosition.setProtein(  searchProteinDoublePosition.getProtein() );
				webProteinPosition.setPosition1( Integer.toString( searchProteinDoublePosition.getPosition1() ) );
				webProteinPosition.setPosition2( Integer.toString( searchProteinDoublePosition.getPosition2() ) );
			}

			return webProteinPositionList;


		} catch ( Exception e ) {

			log.error( "getPeptideProteinPositionsFromSearchProteinDoublePositionList() Exception: " + e.toString(), e );

			throw e;
		}
	}



	/**
	 * @param searchProteinPositionList
	 * @return
	 */
	private List<WebProteinPosition> getPeptideProteinPositionsFromSearchProteinPositionList( List<SearchProteinPosition> searchProteinPositionList ) {

		try {

			List<WebProteinPosition> webProteinPositionList = new ArrayList<>();

			for ( SearchProteinPosition searchProteinPosition : searchProteinPositionList ) {

				WebProteinPosition  webProteinPosition = new WebProteinPosition();
				webProteinPositionList.add( webProteinPosition );

				webProteinPosition.setProtein(  searchProteinPosition.getProtein() );
				webProteinPosition.setPosition1( Integer.toString( searchProteinPosition.getPosition() ) );

			}

			return webProteinPositionList;


		} catch ( Exception e ) {

			log.error( "getPeptideProteinPositionsFromSearchProteinPositionList() Exception: " + e.toString(), e );

			throw e;
		}
	}



	public void setSearch(SearchDTO search) {
		this.search = search;
	}

	public SearchDTO getSearch() {

		return this.search;

	}




	public SearchPeptideDimer getSearchPeptideDimer() {
		return searchPeptideDimer;
	}

	public void setSearchPeptideDimer(SearchPeptideDimer searchPeptideDimer) {
		this.searchPeptideDimer = searchPeptideDimer;
	}

	public SearchPeptideUnlink getSearchPeptideUnlinked() {
		return searchPeptideUnlinked;
	}

	public void setSearchPeptideUnlinked(SearchPeptideUnlink searchPeptideUnlinked) {
		this.searchPeptideUnlinked = searchPeptideUnlinked;
	}

	public SearchPeptideLooplink getSearchPeptideLooplink() {
		return searchPeptideLooplink;
	}

	public void setSearchPeptideLooplink(SearchPeptideLooplink searchPeptideLooplink) {
		this.searchPeptideLooplink = searchPeptideLooplink;
	}

	public SearchPeptideCrosslink getSearchPeptideCrosslink() {
		return searchPeptideCrosslink;
	}
	public void setSearchPeptideCrosslink(
			SearchPeptideCrosslink searchPeptideCrosslink) {
		this.searchPeptideCrosslink = searchPeptideCrosslink;
	}


	public int getSearchId() {
		return searchId;
	}
	public void setSearchId(int searchId) {
		this.searchId = searchId;
	}


	public int getReportedPeptideId() {
		return reportedPeptideId;
	}
	public void setReportedPeptideId(int reportedPeptideId) {
		this.reportedPeptideId = reportedPeptideId;
	}

	public SearcherCutoffValuesSearchLevel getSearcherCutoffValuesSearchLevel() {
		return searcherCutoffValuesSearchLevel;
	}


	public void setSearcherCutoffValuesSearchLevel(
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel) {
		this.searcherCutoffValuesSearchLevel = searcherCutoffValuesSearchLevel;
	}

	
	
	public List<String> getPsmAnnotationValueList() {
		return psmAnnotationValueList;
	}


	public void setPsmAnnotationValueList(List<String> psmAnnotationValueList) {
		this.psmAnnotationValueList = psmAnnotationValueList;
	}


	public List<String> getPeptideAnnotationValueList() {
		return peptideAnnotationValueList;
	}



	public void setPeptideAnnotationValueList(
			List<String> peptideAnnotationValueList) {
		this.peptideAnnotationValueList = peptideAnnotationValueList;
	}


	public int getUnifiedReportedPeptideId() {
		return unifiedReportedPeptideId;
	}


	public void setUnifiedReportedPeptideId(int unifiedReportedPeptideId) {
		this.unifiedReportedPeptideId = unifiedReportedPeptideId;
	}



	public String getLinkType() {
		return linkType;
	}


	public void setLinkType(String linkType) {
		this.linkType = linkType;
	}

	public SearchPeptideMonolink getSearchPeptideMonolink() {
		return searchPeptideMonolink;
	}
	public void setSearchPeptideMonolink(SearchPeptideMonolink searchPeptideMonolink) {
		this.searchPeptideMonolink = searchPeptideMonolink;
	}

	
	/////////////////////////////////////////////////
	

	private SearchDTO search;



	private SearchPeptideCrosslink searchPeptideCrosslink;
	private SearchPeptideLooplink searchPeptideLooplink;

	private SearchPeptideUnlink searchPeptideUnlinked;
	private SearchPeptideDimer searchPeptideDimer;
	
	private SearchPeptideMonolink searchPeptideMonolink;


	private int searchId = -999;
	
	private int reportedPeptideId = -999;
	
	private ReportedPeptideDTO reportedPeptide;






	private int unifiedReportedPeptideId = -999;



	private int numPsms = -999;
	/**
	 * true when SetNumPsms has been called
	 */
	private boolean numPsmsSet;




	private int numUniquePsms = -999;
	/**
	 * true when SetNumUniquePsms has been called
	 */
	private boolean numUniquePsmsSet;



	/**
	 *  Used to get numPsms when they are not already set
	 */
	private SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel;





	/**
	 * Used for display on web page
	 */
	private List<String> psmAnnotationValueList;
	

	/**
	 * Used for display on web page
	 */
	private List<String> peptideAnnotationValueList;





	private String linkType;






}