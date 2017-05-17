package org.yeastrc.xlink.www.objects;

import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.dto.PeptideDTO;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;

/**
 * Result returned from web service
 *
 */
public class SearchPeptideCrosslinkWebserviceResult implements SearchPeptideCommonLinkWebserviceResultIF {

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
			
			if ( this.numUniquePsms != null ) {
				this.numNonUniquePsms = this.numPsms - this.numUniquePsms;
			}
			
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
	private Integer numNonUniquePsms;

	/**
	 * Used for display on web page
	 */
	private List<String> psmAnnotationValueList;
	
	/**
	 * Used for display on web page
	 */
	private List<String> peptideAnnotationValueList;
	

	//  In SearchPeptideCommonLinkWebserviceResultIF
	@Override
	public List<String> getPsmAnnotationValueList() {
		return psmAnnotationValueList;
	}
	@Override
	public void setPsmAnnotationValueList(List<String> psmAnnotationValueList) {
		this.psmAnnotationValueList = psmAnnotationValueList;
	}
	@Override
	public List<String> getPeptideAnnotationValueList() {
		return peptideAnnotationValueList;
	}
	@Override
	public void setPeptideAnnotationValueList(
			List<String> peptideAnnotationValueList) {
		this.peptideAnnotationValueList = peptideAnnotationValueList;
	}

	
	

	public ReportedPeptideDTO getReportedPeptide() {
		return reportedPeptide;
	}
	public PeptideDTO getPeptide1() {
		return peptide1;
	}
	public PeptideDTO getPeptide2() {
		return peptide2;
	}
	public int getPeptide1Position() {
		return peptide1Position;
	}
	public int getPeptide2Position() {
		return peptide2Position;
	}
	public int getNumPsms() {
		return numPsms;
	}
	public Integer getNumUniquePsms() {
		return numUniquePsms;
	}
	public Integer getNumNonUniquePsms() {
		return numNonUniquePsms;
	}


}
