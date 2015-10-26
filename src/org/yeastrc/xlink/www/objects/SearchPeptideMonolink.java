package org.yeastrc.xlink.www.objects;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.PeptideDAO;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.dto.PsmDTO;
import org.yeastrc.xlink.www.searcher.SearchProteinSearcher;
import org.yeastrc.xlink.www.searcher.SearchPsmSearcher;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SearchPeptideMonolink {
	
	private static final Logger log = Logger.getLogger(SearchPeptideMonolink.class);

	
	public ReportedPeptideDTO getReportedPeptide() {
		return reportedPeptide;
	}
	public void setReportedPeptide(ReportedPeptideDTO reportedPeptide) {
		this.reportedPeptide = reportedPeptide;
	}
	public PeptideDTO getPeptide() throws Exception {

		try {


			if ( peptide == null ) {
				
				peptide = PeptideDAO.getInstance().getPeptideDTOFromDatabase( monolinkPeptideId );
			}

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
	
	@JsonIgnore // Don't serialize to JSON
	public SearchDTO getSearch() {
		return search;
	}
	public void setSearch(SearchDTO search) {
		this.search = search;
	}
	

	
	
	public int getPeptidePosition() throws Exception {
		
		return monolinkPeptidePosition;

	}
//	public void setPeptidePosition( int peptidePosition ) {
//		this.peptidePosition = peptidePosition;
//	}

	public Double getQValue() {
		return qValue;
	}
	public void setQValue(Double qValue) {
		this.qValue = qValue;
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
	
	
	@JsonIgnore // Don't serialize to JSON
	public List<PsmDTO> getPsms() throws Exception {
		
		try {

			if( this.psms == null )
				this.psms = SearchPsmSearcher.getInstance().getPsms( this );

			return this.psms;

		} catch ( Exception e ) {

			String msg = "Exception in getNumPeptides()";

			log.error( msg, e );

			throw e;
		}
	}
	
	public void setPsms(List<PsmDTO> psms) {
		this.psms = psms;
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
				this.peptideProteinPositions = SearchProteinSearcher.getInstance().getProteinPositions( this.search, this.peptide, this.peptidePosition );

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
	
	

	public int getMonolinkId() {
		return monolinkId;
	}
	public void setMonolinkId(int monolinkId) {
		this.monolinkId = monolinkId;
	}
	public int getMonolinkPsmId() {
		return monolinkPsmId;
	}
	public void setMonolinkPsmId(int monolinkPsmId) {
		this.monolinkPsmId = monolinkPsmId;
	}
	public int getMonolinkNrseqProteinId() {
		return monolinkNrseqProteinId;
	}
	public void setMonolinkNrseqProteinId(int monolinkNrseqProteinId) {
		this.monolinkNrseqProteinId = monolinkNrseqProteinId;
	}
	public int getMonolinkProteinPosition() {
		return monolinkProteinPosition;
	}
	public void setMonolinkProteinPosition(int monolinkProteinPosition) {
		this.monolinkProteinPosition = monolinkProteinPosition;
	}
	public int getMonolinkPeptideId() {
		return monolinkPeptideId;
	}
	public void setMonolinkPeptideId(int monolinkPeptideId) {
		this.monolinkPeptideId = monolinkPeptideId;
	}
	public int getMonolinkPeptidePosition() {
		return monolinkPeptidePosition;
	}
	public void setMonolinkPeptidePosition(int monolinkPeptidePosition) {
		this.monolinkPeptidePosition = monolinkPeptidePosition;
	}

	/// From monolink table:
	
//	CREATE TABLE monolink (
//	  id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
//	  psm_id INT(10) UNSIGNED NOT NULL,
//	  nrseq_id INT(10) UNSIGNED NOT NULL,
//	  protein_position INT(10) UNSIGNED NOT NULL,
//	  peptide_id INT(10) UNSIGNED NOT NULL,
//	  peptide_position INT(10) UNSIGNED NOT NULL,

	private int monolinkId;
	private int monolinkPsmId;
	private int monolinkNrseqProteinId;
	private int monolinkProteinPosition;
	private int monolinkPeptideId;
	private int monolinkPeptidePosition;
	


	private ReportedPeptideDTO reportedPeptide;
	private PeptideDTO peptide;
	private int peptidePosition = -1;

	private Double qValue;
	private double pValue;
	private double svmScore;
	private double pep;
	
	private double psmQValueCutoff;
	private double peptideQValueCutoff;
	
	private SearchDTO search;
	private List<SearchProteinPosition> peptideProteinPositions;
	
	private int numPsms;
	private List<PsmDTO> psms;
	private double bestPsmQValue;
	
}
