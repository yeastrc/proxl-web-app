package org.yeastrc.xlink.www.objects;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.LooplinkDAO;
import org.yeastrc.xlink.dao.PeptideDAO;
import org.yeastrc.xlink.dto.LooplinkDTO;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.www.searcher.PsmCountForUniquePSM_SearchIdReportedPeptideId_Searcher;
import org.yeastrc.xlink.www.searcher.SearchProteinSearcher;
import org.yeastrc.xlink.www.searcher.SearchPsmSearcher;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SearchPeptideLooplink {
	
	private static final Logger log = Logger.getLogger(SearchPeptideLooplink.class);

	private void populatePeptides() throws Exception {
		
		Integer psmId = getSinglePsmId();
		
		
		if ( psmId == null ) {
			
			log.warn( "No PSMs for search.id : " + search.getId() 
					+ ", this.getReportedPeptide().getId(): " + this.getReportedPeptide().getId() 
					+ ", this.getReportedPeptide().getSequence(): " + this.getReportedPeptide().getSequence() );
			
			return;
		}
		
		try {

			//  Get looplink table entry for first psm.  assume the peptide position is the same for all.
			
			LooplinkDTO looplinkDTO = LooplinkDAO.getInstance().getLooplinkDTOByPsmId( psmId );

			if ( looplinkDTO == null ) {
				

				String msg = "looplinkDTO == null for psmId: " + psmId;

				log.error( msg );

				throw new Exception( msg );
				
			}

			int position1 = looplinkDTO.getPeptidePosition1();
			int position2 = looplinkDTO.getPeptidePosition2();

			PeptideDTO peptideDTO = PeptideDAO.getInstance().getPeptideDTOFromDatabase( looplinkDTO.getPeptideId() );

			this.setPeptide( peptideDTO );

			if( position1 <= position2 ) {
				this.setPeptidePosition1( position1 );
				this.setPeptidePosition2( position2 );
			} else {
				this.setPeptidePosition1( position2 );
				this.setPeptidePosition2( position1 );
			}

		} catch ( Exception e ) {

			String msg = "Exception in populatePeptides()";

			log.error( msg, e );

			throw e;
		}
	}
	
	@JsonIgnore // Don't serialize to JSON
	public SearchDTO getSearch() {
		return search;
	}
	public void setSearch(SearchDTO search) {
		this.search = search;
	}
	
	
	public ReportedPeptideDTO getReportedPeptide() {
		return reportedPeptide;
	}
	public void setReportedPeptide(ReportedPeptideDTO reportedPeptide) {
		this.reportedPeptide = reportedPeptide;
	}
	public PeptideDTO getPeptide() throws Exception {
		
		try {

			if( this.peptide == null )
				populatePeptides();

			return peptide;

		} catch ( Exception e ) {

			String msg = "Exception in getPeptide()";

			log.error( msg, e );

			throw e;
		}
	}
	public void setPeptide(PeptideDTO peptide) {
		this.peptide = peptide;
	}
	public int getPeptidePosition1() throws Exception {
		
		try {

			if( this.peptide == null )
				populatePeptides();

			return peptidePosition1;

		} catch ( Exception e ) {

			String msg = "Exception in getPeptidePosition1()";

			log.error( msg, e );

			throw e;
		}
	}
	public void setPeptidePosition1(int peptidePosition1) {
		this.peptidePosition1 = peptidePosition1;
	}
	public int getPeptidePosition2() throws Exception {
		
		try {

			if( this.peptide == null )
				populatePeptides();

			return peptidePosition2;

		} catch ( Exception e ) {

			String msg = "Exception in getPeptidePosition2()";

			log.error( msg, e );

			throw e;
		}
	}
	public void setPeptidePosition2(int peptidePosition2) {
		this.peptidePosition2 = peptidePosition2;
	}
	public Double getQValue() {
		return qValue;
	}
	public void setQValue(Double qValue) {
		this.qValue = qValue;
	}
	public double getPsmQValueCutoff() {
		return psmQValueCutoff;
	}
	public void setPsmQValueCutoff(double psmQValueCutoff) {
		this.psmQValueCutoff = psmQValueCutoff;
	}
	public double getPeptideQValueCutoff() {
		return peptideQValueCutoff;
	}
	public void setPeptideQValueCutoff(double peptideQValueCutoff) {
		this.peptideQValueCutoff = peptideQValueCutoff;
	}




	//  Percolator only


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



	/**
	 * @return null when no scan data for search
	 * @throws Exception
	 */
	public Integer getNumUniquePsms() throws Exception {
		
		try {

			if ( numUniquePsmsSet ) {

				return numUniquePsms;
			}
			
			
			if ( this.getSearch().isNoScanData() ) {
				
				numUniquePsms = null;
				
				numUniquePsmsSet = true;
				
				return numUniquePsms;
			}




			numUniquePsms = 
					PsmCountForUniquePSM_SearchIdReportedPeptideId_Searcher.getInstance()
					.getPsmCountForUniquePSM_SearchIdReportedPeptideId( this.getReportedPeptide().getId(), this.getSearch().getId(), peptideQValueCutoff, psmQValueCutoff );

			numUniquePsmsSet = true;

			return numUniquePsms;
			
		} catch ( Exception e ) {
			
			log.error( "getNumUniquePsms() Exception: " + e.toString(), e );
			
			throw e;
		}
	}
	
	public int getNumPsms() {
		return numPsms;
	}
	public void setNumPsms(int numPsms) {
		this.numPsms = numPsms;
	}
	
	
	
	/**
	 * @return the psmId for a random psm record associated with this Peptide, null if none found
	 * @throws Exception
	 */
	public Integer getSinglePsmId() throws Exception {

		Integer psmId = SearchPsmSearcher.getInstance().getSinglePsmId( this );
		
		return psmId;
	}
	
	public double getBestPsmQValue() {
		return bestPsmQValue;
	}
	public void setBestPsmQValue(double bestPsmQValue) {
		this.bestPsmQValue = bestPsmQValue;
	}
	
	public List<SearchProteinDoublePosition> getPeptideProteinPositions() throws Exception {
		
		try {

			if( this.peptideProteinPositions == null )
				this.peptideProteinPositions = SearchProteinSearcher.getInstance().getProteinDoublePositions( this.getSearch(), this.getPeptide(), this.getPeptidePosition1(), this.getPeptidePosition2() );

			return peptideProteinPositions;

		} catch ( Exception e ) {

			String msg = "Exception in getPeptideProteinPositions()";

			log.error( msg, e );

			throw e;
		}
	}
	
	public String getProteinPositionsString() throws Exception {
		
		try {

			return StringUtils.join( this.getPeptideProteinPositions(), ", " );

		} catch ( Exception e ) {

			String msg = "Exception in getProteinPositionsString()";

			log.error( msg, e );

			throw e;
		}
	}

	
	private ReportedPeptideDTO reportedPeptide;
	private PeptideDTO peptide;
	private int peptidePosition1 = -1;
	private int peptidePosition2 = -1;

	private Double qValue;
	


	//  Percolator only

	private boolean pValuePopulated = false;
	private boolean svmScorePopulated = false;
	private boolean pepPopulated = false;
	
	private double pValue;
	private double svmScore;
	private double pep;
	
	
	
	private double psmQValueCutoff;
	private double peptideQValueCutoff;
	
	private SearchDTO search;
	private List<SearchProteinDoublePosition> peptideProteinPositions;
	
	private int numPsms;
	private double bestPsmQValue;
	
	private Integer numUniquePsms;
	private boolean numUniquePsmsSet;
	
}
