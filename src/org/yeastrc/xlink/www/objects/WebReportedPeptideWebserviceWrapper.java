package org.yeastrc.xlink.www.objects;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * For returning WebReportedPeptide from web services
 * 
 * Formats the data into strings and numbers
 *  
 */
public class WebReportedPeptideWebserviceWrapper {
	
	private static final Logger log = Logger.getLogger(WebReportedPeptideWebserviceWrapper.class);
	
	private String linkType;

	private WebReportedPeptide webReportedPeptide;
	
	
	

	public void setWebReportedPeptide(WebReportedPeptide webReportedPeptide) {
		this.webReportedPeptide = webReportedPeptide;
	}

	public String getLinkType() {
		return linkType;
	}


	public void setLinkType(String linkType) {
		this.linkType = linkType;
	}




	//////////////////////////////////////////////////////
	
	public int getReportedPeptide_Id() throws Exception {
		
		return webReportedPeptide.getReportedPeptide().getId();
	}	
	
	
	//////////////////////////////////////////////////////
	
	public String getReportedPeptide_Sequence() throws Exception {
		
		return webReportedPeptide.getReportedPeptide().getSequence();
	}	
	


	public String getPeptide_1_Sequence() throws Exception {
		
		return webReportedPeptide.getPeptide1().getSequence();
	}	

	public String getPeptide_1_Position() throws Exception {
		
		return webReportedPeptide.getPeptide1Position();
	}	

	public String getPeptide_2_Sequence() throws Exception {
		
		if ( webReportedPeptide.getPeptide2() == null ) {
			
			return "";
		}
		
		return webReportedPeptide.getPeptide2().getSequence();
	}	

	public String getPeptide_2_Position() throws Exception {
		
		return  webReportedPeptide.getPeptide2Position();
	}	

	

	public List<WebProteinPositionWebserviceWrapper> getPeptide_1_ProteinPositions() throws Exception {
		
		List<WebProteinPosition> webProteinPositionList = webReportedPeptide.getPeptide1ProteinPositions();
		
		if ( webProteinPositionList == null || webProteinPositionList.isEmpty() ) {
			
			return null;
		}
		
		List<WebProteinPositionWebserviceWrapper> webProteinPositionWebserviceWrapperList = new ArrayList<>( webProteinPositionList.size() );
		
		for ( WebProteinPosition webProteinPosition : webProteinPositionList ) {
			
			WebProteinPositionWebserviceWrapper wrapper = new WebProteinPositionWebserviceWrapper();
			
			wrapper.setWebProteinPosition( webProteinPosition );
			
			webProteinPositionWebserviceWrapperList.add( wrapper );
		}
		
		return webProteinPositionWebserviceWrapperList;
	}	
	

	public List<WebProteinPositionWebserviceWrapper> getPeptide_2_ProteinPositions() throws Exception {
		
		List<WebProteinPosition> webProteinPositionList = webReportedPeptide.getPeptide2ProteinPositions();
		
		if ( webProteinPositionList == null || webProteinPositionList.isEmpty() ) {
			
			return null;
		}
		
		List<WebProteinPositionWebserviceWrapper> webProteinPositionWebserviceWrapperList = new ArrayList<>( webProteinPositionList.size() );
		
		for ( WebProteinPosition webProteinPosition : webProteinPositionList ) {
			
			WebProteinPositionWebserviceWrapper wrapper = new WebProteinPositionWebserviceWrapper();
			
			wrapper.setWebProteinPosition( webProteinPosition );
			
			webProteinPositionWebserviceWrapperList.add( wrapper );
		}
		
		return webProteinPositionWebserviceWrapperList;
	}	
	
	
	
	
	
	
	
	public int getNumPsms() throws Exception {
		
		return webReportedPeptide.getNumPsms();
	}
	

	public int getNumUniquePsms() throws Exception {
		
		return webReportedPeptide.getNumUniquePsms();
	}
	
	public double getBestPsmQValue() throws Exception {
		
		return webReportedPeptide.getBestPsmQValue();
	}


	/**
	 * Wrapper for WebProteinPosition
	 *
	 */
	public static class WebProteinPositionWebserviceWrapper {
		
		private WebProteinPosition webProteinPosition;

		public void setWebProteinPosition(WebProteinPosition webProteinPosition) {
			this.webProteinPosition = webProteinPosition;
		}
		

		public int getProteinId() throws Exception {
			
			try {
				return webProteinPosition.getProtein().getNrProtein().getNrseqId();
				
			} catch ( Exception e ) {
				

				String msg = "Exception in webProteinPosition.getName()";

				log.error( msg, e );
				
				throw e;
			}
		}
		
		
		public String getName() throws Exception {
			
			try {
				return webProteinPosition.getProtein().getName();
				
			} catch ( Exception e ) {
				

				String msg = "Exception in webProteinPosition.getName()";

				log.error( msg, e );
				
				throw e;
			}
		}
		

		public String getPosition_1() {
			return webProteinPosition.getPosition1();
		}

		public String getPosition_2() {
			return webProteinPosition.getPosition2();
		}
	}
}
