package org.yeastrc.xlink.www.objects;

import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;

/**
 * Result returned from web service
 *
 */
public class SearchPeptideNoLinkInfoWebserviceResult implements SearchPeptideCommonLinkWebserviceResultIF {

	private static final Logger log = Logger.getLogger(SearchPeptideNoLinkInfoWebserviceResult.class);
	
	//  Constructors
	
	public SearchPeptideNoLinkInfoWebserviceResult() {}

	public SearchPeptideNoLinkInfoWebserviceResult( SearchPeptideNoLinkInfo searchPeptideNoLinkInfo ) {
		
		try {

			this.reportedPeptide = searchPeptideNoLinkInfo.getReportedPeptide();
						
			this.numPsms = searchPeptideNoLinkInfo.getNumPsms();
			this.numUniquePsms = searchPeptideNoLinkInfo.getNumUniquePsms();
			
		} catch ( Exception e ) {
			
			String msg = "Failed to populate SearchPeptideNoLinkInfoWebserviceResult from SearchPeptideNoLinkInfo";
			log.error( msg, e );
			throw new RuntimeException( msg, e );
		}
	}

	private boolean linkTypeCrosslink;
	private boolean linkTypeLooplink;
	private boolean linkTypeUnlinked;
	private boolean linkTypeDimer;
	
	private ReportedPeptideDTO reportedPeptide;
	
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

	public boolean isLinkTypeCrosslink() {
		return linkTypeCrosslink;
	}

	public void setLinkTypeCrosslink(boolean linkTypeCrosslink) {
		this.linkTypeCrosslink = linkTypeCrosslink;
	}

	public boolean isLinkTypeLooplink() {
		return linkTypeLooplink;
	}

	public void setLinkTypeLooplink(boolean linkTypeLooplink) {
		this.linkTypeLooplink = linkTypeLooplink;
	}

	public boolean isLinkTypeUnlinked() {
		return linkTypeUnlinked;
	}

	public void setLinkTypeUnlinked(boolean linkTypeUnlinked) {
		this.linkTypeUnlinked = linkTypeUnlinked;
	}

	public boolean isLinkTypeDimer() {
		return linkTypeDimer;
	}

	public void setLinkTypeDimer(boolean linkTypeDimer) {
		this.linkTypeDimer = linkTypeDimer;
	}
	
	
	
	
}
