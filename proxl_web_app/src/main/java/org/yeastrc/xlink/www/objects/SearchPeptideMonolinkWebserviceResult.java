package org.yeastrc.xlink.www.objects;

import java.util.List;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;

/**
 * Result returned from web service
 *
 */
public class SearchPeptideMonolinkWebserviceResult implements SearchPeptideCommonLinkWebserviceResultIF {

	private static final Logger log = LoggerFactory.getLogger( SearchPeptideMonolinkWebserviceResult.class);
	
	//  Constructors
	
	public SearchPeptideMonolinkWebserviceResult() {}

	public SearchPeptideMonolinkWebserviceResult( SearchPeptideMonolink searchPeptideMonolink ) {
		
		try {

			this.reportedPeptide = searchPeptideMonolink.getReportedPeptide();
			this.peptide = searchPeptideMonolink.getPeptide();
			this.peptidePosition = searchPeptideMonolink.getPeptidePosition();
						
			this.numPsms = searchPeptideMonolink.getNumPsms();
			this.numUniquePsms = searchPeptideMonolink.getNumUniquePsms();

			if ( this.numUniquePsms != null ) {
				this.numNonUniquePsms = this.numPsms - this.numUniquePsms;
			}
			
		} catch ( Exception e ) {
			
			String msg = "Failed to populate SearchPeptideMonolinkWebserviceResult from SearchPeptideMonolink";
			log.error( msg, e );
			throw new RuntimeException( msg, e );
		}
	}

	private ReportedPeptideDTO reportedPeptide;
	private PeptideDTO peptide;
	private int peptidePosition = -1;
	
	
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
	public int getNumPsms() {
		return numPsms;
	}
	public Integer getNumUniquePsms() {
		return numUniquePsms;
	}
	public int getPeptidePosition() {
		return peptidePosition;
	}
	public Integer getNumNonUniquePsms() {
		return numNonUniquePsms;
	}
	
}
