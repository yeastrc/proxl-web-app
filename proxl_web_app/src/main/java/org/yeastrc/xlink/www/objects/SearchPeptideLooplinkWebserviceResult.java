package org.yeastrc.xlink.www.objects;

import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;

/**
 * Result returned from web service
 *
 */
public class SearchPeptideLooplinkWebserviceResult implements SearchPeptideCommonLinkWebserviceResultIF {

	private static final Logger log = Logger.getLogger(SearchPeptideLooplinkWebserviceResult.class);
	
	//  Constructors
	
	public SearchPeptideLooplinkWebserviceResult() {}

	public SearchPeptideLooplinkWebserviceResult( SearchPeptideLooplink searchPeptideLooplink ) {
		
		try {

			this.reportedPeptide = searchPeptideLooplink.getReportedPeptide();
			this.peptide = searchPeptideLooplink.getPeptide();
			this.peptidePosition1 = searchPeptideLooplink.getPeptidePosition1();
			this.peptidePosition2 = searchPeptideLooplink.getPeptidePosition2();
						
			this.numPsms = searchPeptideLooplink.getNumPsms();
			this.numUniquePsms = searchPeptideLooplink.getNumUniquePsms();

			if ( this.numUniquePsms != null ) {
				this.numNonUniquePsms = this.numPsms - this.numUniquePsms;
			}
			
		} catch ( Exception e ) {
			
			String msg = "Failed to populate SearchPeptideLooplinkWebserviceResult from SearchPeptideLooplink";
			log.error( msg, e );
			throw new RuntimeException( msg, e );
		}
	}

	private ReportedPeptideDTO reportedPeptide;
	private PeptideDTO peptide;
	private PeptideDTO peptide2;
	private int peptidePosition1 = -1;
	private int peptidePosition2 = -1;
	
	
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
	public PeptideDTO getPeptide() {
		return peptide;
	}
	public PeptideDTO getPeptide2() {
		return peptide2;
	}
	public int getPeptidePosition1() {
		return peptidePosition1;
	}
	public int getPeptidePosition2() {
		return peptidePosition2;
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
