package org.yeastrc.xlink.www.objects;

import java.util.List;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;

/**
 * Result returned from web service
 *
 */
public class SearchPeptideNoLinkInfoWebserviceResult implements SearchPeptideCommonLinkWebserviceResultIF {

	private static final Logger log = LoggerFactory.getLogger( SearchPeptideNoLinkInfoWebserviceResult.class);
	
	//  Constructors
	
	public SearchPeptideNoLinkInfoWebserviceResult() {}

	public SearchPeptideNoLinkInfoWebserviceResult( SearchPeptideNoLinkInfo searchPeptideNoLinkInfo ) {
		
		try {

			this.reportedPeptide = searchPeptideNoLinkInfo.getReportedPeptide();
						
			this.numPsms = searchPeptideNoLinkInfo.getNumPsms();
			this.numNonUniquePsms = searchPeptideNoLinkInfo.getNumNonUniquePsms();
			
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


	private Integer numNonUniquePsms;

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

	public Integer getNumNonUniquePsms() {
		return numNonUniquePsms;
	}
	
	
	
	
}
