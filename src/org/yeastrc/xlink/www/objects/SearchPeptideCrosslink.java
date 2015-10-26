package org.yeastrc.xlink.www.objects;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.CrosslinkDAO;
import org.yeastrc.xlink.dao.PeptideDAO;
import org.yeastrc.xlink.dto.CrosslinkDTO;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.www.searcher.SearchProteinSearcher;
import org.yeastrc.xlink.www.searcher.SearchPsmSearcher;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SearchPeptideCrosslink {
	
	private static final Logger log = Logger.getLogger(SearchPeptideCrosslink.class);


	private void populatePeptides() throws Exception {
		
		Integer psmId = getSinglePsmId();
		
		if ( psmId == null ) {
			
			log.warn( "No PSMs for search.id : " + search.getId() 
					+ ", this.getReportedPeptide().getId(): " + this.getReportedPeptide().getId() 
					+ ", this.getReportedPeptide().getSequence(): " + this.getReportedPeptide().getSequence() );
			
			return;
		}
		
		try {
			
			//  Get crosslink table entry for a psm.  assume the peptide position is the same for all.
			

			CrosslinkDTO crosslinkDTO = CrosslinkDAO.getInstance().getCrosslinkDTOByPsmId( psmId );
			
			if ( crosslinkDTO == null ) {
				

				String msg = "crosslinkDTO == null for psmId: " + psmId;

				log.error( msg );

				throw new Exception( msg );
				
			}
			
			if( crosslinkDTO.getPeptide1Id() == crosslinkDTO.getPeptide2Id() ) {
				
				//  Same peptide
				
				int position1 = crosslinkDTO.getPeptide1Position();
				int position2 = crosslinkDTO.getPeptide2Position();

				PeptideDTO peptideDTO = PeptideDAO.getInstance().getPeptideDTOFromDatabase( crosslinkDTO.getPeptide1Id() );
				
				this.setPeptide1( peptideDTO );
				this.setPeptide2( peptideDTO );

				if( position1 <= position2 ) {
					this.setPeptide1Position( position1 );
					this.setPeptide2Position( position2 );
				} else {
					this.setPeptide1Position( position2 );
					this.setPeptide2Position( position1 );
				}
				
			} else {
				
				//  different peptides

				PeptideDTO peptideDTO1 = PeptideDAO.getInstance().getPeptideDTOFromDatabase( crosslinkDTO.getPeptide1Id() );
				PeptideDTO peptideDTO2 = PeptideDAO.getInstance().getPeptideDTOFromDatabase( crosslinkDTO.getPeptide2Id() );

				int position1 = crosslinkDTO.getPeptide1Position();
				int position2 = crosslinkDTO.getPeptide2Position();


				
				if( peptideDTO1.getId() <= peptideDTO2.getId() ) {
					this.setPeptide1( peptideDTO1 );
					this.setPeptide1Position( position1 );

					this.setPeptide2( peptideDTO2 );
					this.setPeptide2Position( position2 );

				} else {
					this.setPeptide1( peptideDTO2 );
					this.setPeptide1Position( position2 );

					this.setPeptide2( peptideDTO1 );
					this.setPeptide2Position( position1 );
				}
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
	public ReportedPeptideDTO getReportedPeptide() {
		return reportedPeptide;
	}
	public void setReportedPeptide(ReportedPeptideDTO reportedPeptide) {
		this.reportedPeptide = reportedPeptide;
	}
	public PeptideDTO getPeptide1() throws Exception {
		
		try {

			if( this.peptide1 == null )
				this.populatePeptides();

			return this.peptide1;

		} catch ( Exception e ) {

			String msg = "Exception in getPeptide1()";

			log.error( msg, e );

			throw e;
		}
	}
	public void setPeptide1(PeptideDTO peptide1) {
		this.peptide1 = peptide1;
	}
	public PeptideDTO getPeptide2() throws Exception {
		
		try {

			if( this.peptide1 == null )
				this.populatePeptides();

			return peptide2;

		} catch ( Exception e ) {

			String msg = "Exception in getPeptide2()";

			log.error( msg, e );

			throw e;
		}
	}
	public void setPeptide2(PeptideDTO peptide2) {
		this.peptide2 = peptide2;
	}
	public int getPeptide1Position() throws Exception {
		
		try {

			if( this.peptide1Position == -1 )
				this.populatePeptides();

			return peptide1Position;

		} catch ( Exception e ) {

			String msg = "Exception in getPeptide1Position()";

			log.error( msg, e );

			throw e;
		}
	}
	public void setPeptide1Position(int peptide1Position) {
		this.peptide1Position = peptide1Position;
	}
	public int getPeptide2Position() throws Exception {
		
		try {

			if( this.peptide2Position == -1 )
				this.populatePeptides();

			return peptide2Position;

		} catch ( Exception e ) {

			String msg = "Exception in getPeptide2Position()";

			log.error( msg, e );

			throw e;
		}
	}
	public void setPeptide2Position(int peptide2Position) {
		this.peptide2Position = peptide2Position;
	}
	public Double getQValue() {
		return qValue;
	}
	public void setQValue(Double qValue) {
		this.qValue = qValue;
	}

	
	public String getPeptide1ProteinPositionsString() throws Exception {
		return StringUtils.join( this.getPeptide1ProteinPositions(), ", " );
	}
	public List<SearchProteinPosition> getPeptide1ProteinPositions() throws Exception {
		
		try {

			if( this.peptide1ProteinPositions == null )
				this.peptide1ProteinPositions = SearchProteinSearcher.getInstance().getProteinPositions( this.search, this.peptide1, this.peptide1Position);

			return peptide1ProteinPositions;

		} catch ( Exception e ) {

			String msg = "Exception in getPeptide1ProteinPositions()";

			log.error( msg, e );

			throw e;
		}
	}
	public void setPeptide1ProteinPositions(
			List<SearchProteinPosition> peptide1ProteinPositions) {
		this.peptide1ProteinPositions = peptide1ProteinPositions;
	}

	public String getPeptide2ProteinPositionsString() throws Exception {
		
		try {

			return StringUtils.join( this.getPeptide2ProteinPositions(), ", " );

		} catch ( Exception e ) {

			String msg = "Exception in getPeptide2ProteinPositionsString()";

			log.error( msg, e );

			throw e;
		}
	}
	public List<SearchProteinPosition> getPeptide2ProteinPositions() throws Exception {
		
		try {

			if( this.peptide2ProteinPositions == null )
				this.peptide2ProteinPositions = SearchProteinSearcher.getInstance().getProteinPositions( this.search, this.peptide2, this.peptide2Position);

			return peptide2ProteinPositions;

		} catch ( Exception e ) {

			String msg = "Exception in getPeptide2ProteinPositions()";

			log.error( msg, e );

			throw e;
		}
	}
	public void setPeptide2ProteinPositions(
			List<SearchProteinPosition> peptide2ProteinPositions) {
		this.peptide2ProteinPositions = peptide2ProteinPositions;
	}
	public int getNumPsms() {
		return numPsms;
	}
	public void setNumPsms(int numPsms) {
		this.numPsms = numPsms;
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


	
	/**
	 * @return the psmId for a random psm record associated with this Peptide, null if none found
	 * @throws Exception
	 */
	public Integer getSinglePsmId() throws Exception {

		Integer psmId = SearchPsmSearcher.getInstance().getSinglePsmId( this );
		
		return psmId;
	}
	
	

	private ReportedPeptideDTO reportedPeptide;
	private PeptideDTO peptide1;
	private PeptideDTO peptide2;
	private int peptide1Position = -1;
	private int peptide2Position = -1;

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
	private List<SearchProteinPosition> peptide1ProteinPositions;
	private List<SearchProteinPosition> peptide2ProteinPositions;
	
	private int numPsms;
	private double bestPsmQValue;
	
}
