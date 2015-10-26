package org.yeastrc.xlink.www.objects;

import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.MatchedPeptideDAO;
import org.yeastrc.xlink.dao.PeptideDAO;
import org.yeastrc.xlink.dto.MatchedPeptideDTO;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.www.searcher.SearchProteinSearcher;
import org.yeastrc.xlink.www.searcher.SearchPsmSearcher;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SearchPeptideUnlink {
	
	private static final Logger log = Logger.getLogger(SearchPeptideUnlink.class);

	private void populatePeptides() throws Exception {
		
		Integer psmId = getSinglePsmId();
		
		if ( psmId == null ) {
			
			log.warn( "No PSMs for search.id : " + search.getId() 
					+ ", this.getReportedPeptide().getId(): " + this.getReportedPeptide().getId() 
					+ ", this.getReportedPeptide().getSequence(): " + this.getReportedPeptide().getSequence() );
			
			return;
		}
		
		try {
			//  Get MatchedPeptide table entries for a psm.
			List<MatchedPeptideDTO> results = MatchedPeptideDAO.getInstance().getMatchedPeptideDTOForPsmId(  psmId );

			if ( results.size() < 1 ) {

				String msg = "results.size() < 1 for psmId: " + psmId;

				log.error( msg );

				throw new Exception( msg );
				
			}

			PeptideDTO peptideDTO = PeptideDAO.getInstance().getPeptideDTOFromDatabase( results.get(0).getPeptide_id() );

			this.setPeptide( peptideDTO );

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
	
	public List<SearchProteinPosition> getPeptideProteinPositions() throws Exception {
		
		try {

			if( this.peptideProteinPositions == null )
				this.peptideProteinPositions = SearchProteinSearcher.getInstance().getProteinForUnlinked( this.search, this.peptide);

			return peptideProteinPositions;

		} catch ( Exception e ) {

			String msg = "Exception in getPeptideProteinPositions()";

			log.error( msg, e );

			throw e;
		}
	}


	
	private ReportedPeptideDTO reportedPeptide;
	private PeptideDTO peptide;

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
	private List<SearchProteinPosition> peptideProteinPositions;
	
	private int numPsms;
	private double bestPsmQValue;
	
}
