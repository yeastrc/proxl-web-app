package org.yeastrc.xlink.www.objects;

import java.util.ArrayList;
import java.util.List;





//import org.apache.log4j.Logger;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.www.searcher.PsmCountForSearchIdReportedPeptideIdSearcher;



public class WebReportedPeptide {
	
//	private static final Logger log = Logger.getLogger(WebReportedPeptide.class);

	

	
	public void setNumPsms(int numPsms) {
		this.numPsms = numPsms;
		numPsmsSet = true;
	}


	public int getNumPsms() throws Exception {
		
		if ( numPsmsSet ) {
			
			return numPsms;
		}

//		num psms is always based on searching psm table for: search id, reported peptide id, and q value.

		numPsms = 
				PsmCountForSearchIdReportedPeptideIdSearcher.getInstance()
				.getPsmCountForSearchIdReportedPeptideId( reportedPeptideId, searchId, psmQValueCutoff );

		numPsmsSet = true;
		
		return numPsms;
	}
		

	
	public ReportedPeptideDTO getReportedPeptide() {
		
		if ( searchPeptideCrosslink != null ) {
			
			return searchPeptideCrosslink.getReportedPeptide();
		}
		
		if ( searchPeptideLooplink != null ) {
			
			return searchPeptideLooplink.getReportedPeptide();
		}
		
		
		if ( searchPeptideUnlinked != null ) {
			
			return searchPeptideUnlinked.getReportedPeptide();
		}
		
		if ( searchPeptideDimer != null ) {
			
			return searchPeptideDimer.getReportedPeptide();
		}

		return null;		
	}

	public PeptideDTO getPeptide1() throws Exception {
		
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
	}

	public PeptideDTO getPeptide2() throws Exception {
		
		if ( searchPeptideCrosslink != null ) {
			
			return searchPeptideCrosslink.getPeptide2();
		}
		
		if ( searchPeptideDimer != null ) {
			
			return searchPeptideDimer.getPeptide2();
		}
		
		return null;	
	}

	public String getPeptide1Position() throws Exception {
		
		if ( searchPeptideCrosslink != null ) {
			
			return Integer.toString( searchPeptideCrosslink.getPeptide1Position() );
		}
		
		if ( searchPeptideLooplink != null ) {
			
			return Integer.toString( searchPeptideLooplink.getPeptidePosition1() );
		}
		
		return "";	
	}

	public String getPeptide2Position() throws Exception {
		
		if ( searchPeptideCrosslink != null ) {
			
			return Integer.toString( searchPeptideCrosslink.getPeptide2Position() );
		}
		
		if ( searchPeptideLooplink != null ) {
			
			return Integer.toString( searchPeptideLooplink.getPeptidePosition2() );
		}
		
		return "";	
	}


	public SearchDTO getSearch() {
		if ( searchPeptideCrosslink != null ) {
			
			return searchPeptideCrosslink.getSearch();
		}
		
		if ( searchPeptideLooplink != null ) {
			
			return searchPeptideLooplink.getSearch();
		}
		
		
		if ( searchPeptideUnlinked != null ) {
			
			return searchPeptideUnlinked.getSearch();
		}
		
		if ( searchPeptideDimer != null ) {
			
			return searchPeptideDimer.getSearch();
		}

		return null;	
	}

	

	public List<WebProteinPosition> getPeptide1ProteinPositions() throws Exception {
		
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
	}

	
	
	public List<WebProteinPosition> getPeptide2ProteinPositions() throws Exception {
		
		if ( searchPeptideCrosslink != null ) {
			
			return getPeptideProteinPositionsFromSearchProteinPositionList( searchPeptideCrosslink.getPeptide2ProteinPositions() );

		}
		
		
		if ( searchPeptideDimer != null ) {
			
			return getPeptideProteinsWithoutPositions( searchPeptideDimer.getPeptide2ProteinPositions() );
		}

		return null;	

	}
	
	
	private List<WebProteinPosition> getPeptideProteinsWithoutPositions( List<SearchProteinPosition> searchProteinPositionPositionList ) {
		
		List<WebProteinPosition> webProteinPositionList = new ArrayList<>();
		
		for ( SearchProteinPosition searchProteinPosition : searchProteinPositionPositionList ) {
			
			WebProteinPosition  webProteinPosition = new WebProteinPosition();
			webProteinPositionList.add( webProteinPosition );
			
			webProteinPosition.setProtein(  searchProteinPosition.getProtein() );
			
			//  Position is not really a value
//			webProteinPosition.setPosition1( Integer.toString( searchProteinPosition.getPosition() ) );
		}
		
		return webProteinPositionList;
	}
	
	
	/**
	 * @param searchProteinDoublePositionList
	 * @return
	 */
	private List<WebProteinPosition> getPeptideProteinPositionsFromSearchProteinDoublePositionList( List<SearchProteinDoublePosition> searchProteinDoublePositionList ) {
		
		List<WebProteinPosition> webProteinPositionList = new ArrayList<>();
		
		for ( SearchProteinDoublePosition searchProteinDoublePosition : searchProteinDoublePositionList ) {
			
			WebProteinPosition  webProteinPosition = new WebProteinPosition();
			webProteinPositionList.add( webProteinPosition );
			
			webProteinPosition.setProtein(  searchProteinDoublePosition.getProtein() );
			webProteinPosition.setPosition1( Integer.toString( searchProteinDoublePosition.getPosition1() ) );
			webProteinPosition.setPosition2( Integer.toString( searchProteinDoublePosition.getPosition2() ) );
		}
		
		return webProteinPositionList;
	}
	
	
	
	/**
	 * @param searchProteinPositionList
	 * @return
	 */
	private List<WebProteinPosition> getPeptideProteinPositionsFromSearchProteinPositionList( List<SearchProteinPosition> searchProteinPositionList ) {
		
		List<WebProteinPosition> webProteinPositionList = new ArrayList<>();
		
		for ( SearchProteinPosition searchProteinPosition : searchProteinPositionList ) {
			
			WebProteinPosition  webProteinPosition = new WebProteinPosition();
			webProteinPositionList.add( webProteinPosition );
			
			webProteinPosition.setProtein(  searchProteinPosition.getProtein() );
			webProteinPosition.setPosition1( Integer.toString( searchProteinPosition.getPosition() ) );
			
		}
		
		return webProteinPositionList;
	}
	
	

	public Double getqValue() {
		return qValue;
	}
	public void setqValue(Double qValue) {
		this.qValue = qValue;
	}
	

	public double getBestPsmQValue() {
		return bestPsmQValue;
	}
	public void setBestPsmQValue(double bestPsmQValue) {
		this.bestPsmQValue = bestPsmQValue;
	}


	
	//  Percolator only

	public double getpValue() {
		return pValue;
	}
	public void setpValue(double pValue) {
		this.pValue = pValue;
	}
	public double getSvmScore() {
		return svmScore;
	}
	public void setSvmScore(double svmScore) {
		this.svmScore = svmScore;
	}
	public double getPep() {
		return pep;
	}
	public void setPep(double pep) {
		this.pep = pep;
	}

	public boolean ispValuePopulated() {
		return pValuePopulated;
	}


	public void setpValuePopulated(boolean pValuePopulated) {
		this.pValuePopulated = pValuePopulated;
	}


	public boolean isSvmScorePopulated() {
		return svmScorePopulated;
	}


	public void setSvmScorePopulated(boolean svmScorePopulated) {
		this.svmScorePopulated = svmScorePopulated;
	}


	public boolean isPepPopulated() {
		return pepPopulated;
	}


	public void setPepPopulated(boolean pepPopulated) {
		this.pepPopulated = pepPopulated;
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

	public double getPsmQValueCutoff() {
		return psmQValueCutoff;
	}
	public void setPsmQValueCutoff(double psmQValueCutoff) {
		this.psmQValueCutoff = psmQValueCutoff;
	}



	
	

	private SearchPeptideCrosslink searchPeptideCrosslink;
	private SearchPeptideLooplink searchPeptideLooplink;

	private SearchPeptideUnlink searchPeptideUnlinked;
	private SearchPeptideDimer searchPeptideDimer;

	
	private int searchId;
	private int reportedPeptideId;
	

	private Double qValue;
	
	private double bestPsmQValue;

	//  Percolator only



	private boolean pValuePopulated = false;
	private boolean svmScorePopulated = false;
	private boolean pepPopulated = false;

	private double pValue;
	private double svmScore;
	private double pep;
	


	private int numPsms;
	/**
	 * true when SetNumPsms has been called
	 */
	private boolean numPsmsSet;

	/**
	 * Used to get numPsms when they are not already set
	 */
	private double psmQValueCutoff;


}
