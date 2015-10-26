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

public class SearchPeptideDimer {
	
	private static final Logger log = Logger.getLogger(SearchPeptideDimer.class);

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

			if ( results.size() < 2 ) {
				

				String msg = "results.size() < 2 for psmId: " + psmId;

				log.error( msg );

				throw new Exception( msg );
				
			}
			

			PeptideDTO peptideDTO1 = PeptideDAO.getInstance().getPeptideDTOFromDatabase( results.get(0).getPeptide_id() );

			this.setPeptide1( peptideDTO1 );
			

			PeptideDTO peptideDTO2 = PeptideDAO.getInstance().getPeptideDTOFromDatabase( results.get(1).getPeptide_id() );

			this.setPeptide2( peptideDTO2 );


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
	public PeptideDTO getPeptide1() throws Exception {
		
		try {

			if( this.peptide1 == null )
				populatePeptides();

			return peptide1;

		} catch ( Exception e ) {

			String msg = "Exception in getPeptide1()";

			log.error( msg, e );

			throw e;
		}
	}
	public void setPeptide1(PeptideDTO peptide) {
		this.peptide1 = peptide;
	}
	
	public PeptideDTO getPeptide2() throws Exception {
		
		try {

			if( this.peptide2 == null )
				populatePeptides();

			return peptide2;

		} catch ( Exception e ) {

			String msg = "Exception in getPeptide2()";

			log.error( msg, e );

			throw e;
		}
	}
	public void setPeptide2(PeptideDTO peptide) {
		this.peptide2 = peptide;
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
	
	
	
	public List<SearchProteinPosition> getPeptide1ProteinPositions() throws Exception {
		
		try {

			if( this.peptide1ProteinPositions == null )
				this.peptide1ProteinPositions = SearchProteinSearcher.getInstance().getProteinForDimer( this.search, this.peptide1);

			return peptide1ProteinPositions;

		} catch ( Exception e ) {

			String msg = "Exception in getPeptide1ProteinPositions()";

			log.error( msg, e );

			throw e;
		}
	}
	
	
	public List<SearchProteinPosition> getPeptide2ProteinPositions() throws Exception {
		
		try {

			if( this.peptide2ProteinPositions == null )
				this.peptide2ProteinPositions = SearchProteinSearcher.getInstance().getProteinForDimer( this.search, this.peptide2);

			return peptide2ProteinPositions;

		} catch ( Exception e ) {

			String msg = "Exception in getPeptide2ProteinPositions()";

			log.error( msg, e );

			throw e;
		}
	}
	
	private List<SearchProteinPosition> peptide1ProteinPositions;
	private List<SearchProteinPosition> peptide2ProteinPositions;
	
	private ReportedPeptideDTO reportedPeptide;
	private PeptideDTO peptide1;
	private PeptideDTO peptide2;


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
//	private List<SearchProteinDoublePosition> peptideProteinPositions;
	
	private int numPsms;
	private double bestPsmQValue;
	
}
