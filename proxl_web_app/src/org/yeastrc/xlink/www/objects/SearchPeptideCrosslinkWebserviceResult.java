package org.yeastrc.xlink.www.objects;

import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.dto.PeptideDTO;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;

/**
 * Result returned from web service
 *
 */
public class SearchPeptideCrosslinkWebserviceResult {

	private static final Logger log = Logger.getLogger(SearchPeptideCrosslinkWebserviceResult.class);
	
	//  Constructors
	
	public SearchPeptideCrosslinkWebserviceResult() {}

	public SearchPeptideCrosslinkWebserviceResult( SearchPeptideCrosslink searchPeptideCrosslink ) {
		
		try {

			this.reportedPeptide = searchPeptideCrosslink.getReportedPeptide();
			this.peptide1 = searchPeptideCrosslink.getPeptide1();
			this.peptide2 = searchPeptideCrosslink.getPeptide2();
			this.peptide1Position = searchPeptideCrosslink.getPeptide1Position();
			this.peptide2Position = searchPeptideCrosslink.getPeptide2Position();

			this.numPsms = searchPeptideCrosslink.getNumPsms();
			this.numUniquePsms = searchPeptideCrosslink.getNumUniquePsms();
			
		} catch ( Exception e ) {
			
			String msg = "Failed to populate SearchPeptideCrosslinkWebserviceResult from SearchPeptideCrosslink";
			log.error( msg, e );
			throw new RuntimeException( msg, e );
		}
	}

	private ReportedPeptideDTO reportedPeptide;
	private PeptideDTO peptide1;
	private PeptideDTO peptide2;
	private int peptide1Position = -1;
	private int peptide2Position = -1;
	
	
	private int numPsms;


	private Integer numUniquePsms;

	/**
	 * Used for display on web page
	 */
	private List<String> psmAnnotationValueList;
	
	

	/**
	 * Used for display on web page
	 */
	private List<String> peptideAnnotationValueList;
	
	
	
	
	

	public ReportedPeptideDTO getReportedPeptide() {
		return reportedPeptide;
	}

	public void setReportedPeptide(ReportedPeptideDTO reportedPeptide) {
		this.reportedPeptide = reportedPeptide;
	}

	public PeptideDTO getPeptide1() {
		return peptide1;
	}

	public void setPeptide1(PeptideDTO peptide1) {
		this.peptide1 = peptide1;
	}

	public PeptideDTO getPeptide2() {
		return peptide2;
	}

	public void setPeptide2(PeptideDTO peptide2) {
		this.peptide2 = peptide2;
	}

	public int getPeptide1Position() {
		return peptide1Position;
	}

	public void setPeptide1Position(int peptide1Position) {
		this.peptide1Position = peptide1Position;
	}

	public int getPeptide2Position() {
		return peptide2Position;
	}

	public void setPeptide2Position(int peptide2Position) {
		this.peptide2Position = peptide2Position;
	}

	public int getNumPsms() {
		return numPsms;
	}

	public void setNumPsms(int numPsms) {
		this.numPsms = numPsms;
	}

	public Integer getNumUniquePsms() {
		return numUniquePsms;
	}

	public void setNumUniquePsms(Integer numUniquePsms) {
		this.numUniquePsms = numUniquePsms;
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


}
