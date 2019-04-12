package org.yeastrc.xlink.www.actions;

import java.io.BufferedOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.form_query_json_objects.QCPageQueryJSONRoot;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.qc_data.a_enums.ForDownload_Enum;
import org.yeastrc.xlink.www.qc_data.a_request_json_root.QCPageRequestJSONRoot;
import org.yeastrc.xlink.www.qc_data.psm_level_data.main.PeptideLengthVsPSMCount_For_PSMPeptideCutoffs;
import org.yeastrc.xlink.www.qc_data.psm_level_data.main.PeptideLengthVsPSMCount_For_PSMPeptideCutoffs.PeptideLengthVsPSMCount_For_PSMPeptideCutoffs_Method_Response;
import org.yeastrc.xlink.www.qc_data.utils.QC_DeserializeRequestJSON_To_QCPageRequestJSONRoot;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.constants.ServletOutputStreamCharacterSetConstant;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.forms.SingleRequestJSONStringFieldForm;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;

/**
 * 
 * Download data for QC Chart Peptide Length Vs PSM Count Data - Single Search Histogram
 */
public class DownloadQC_PeptideLengthVsPSMCountHistogramSingleSearchChartDataAction extends Action {
	
	private static final Logger log = LoggerFactory.getLogger( DownloadQC_PeptideLengthVsPSMCountHistogramSingleSearchChartDataAction.class);
	
	/* (non-Javadoc)
	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public ActionForward execute( ActionMapping mapping,
			  ActionForm actionForm,
			  HttpServletRequest request,
			  HttpServletResponse response )
					  throws Exception {
		try {
			// our form
			SingleRequestJSONStringFieldForm form = (SingleRequestJSONStringFieldForm)actionForm;
			
			//  Form Parameter Name.  JSON encoded data
			String requestJSONString = form.getRequestJSONString();
			
			if ( StringUtils.isEmpty( requestJSONString ) ) {
				//  Invalid request, searches across projects
				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_SEARCHES_ACROSS_PROJECTS );
			}
			
			QCPageRequestJSONRoot qcPageRequestJSONRoot = null;
			try {
				qcPageRequestJSONRoot =
						QC_DeserializeRequestJSON_To_QCPageRequestJSONRoot.getInstance().deserializeRequestJSON_To_QCPageRequestJSONRoot( requestJSONString );
			} catch ( Exception e ) {
				String msg = "parse request failed";
				log.warn( msg );
				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_DATA );
			}
			
			List<Integer> projectSearchIds = qcPageRequestJSONRoot.getProjectSearchIds();
			QCPageQueryJSONRoot qcPageQueryJSONRoot = qcPageRequestJSONRoot.getQcPageQueryJSONRoot();
			
			if ( projectSearchIds == null || projectSearchIds.isEmpty() ) {
				log.warn( "projectSearchIds == null || projectSearchIds.isEmpty().  requestJSONString: " + requestJSONString );
				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_DATA );
			}
			
			if ( projectSearchIds.size() > 1 ) {
				// Only one Project Search Id allowed
				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_DATA );
			}
			
			int projectSearchId = projectSearchIds.get(0);

			//   Get the project id for these searches
			Set<Integer> projectSearchIdsSet = new HashSet<Integer>( );
			for ( int searchId : projectSearchIds ) {
				projectSearchIdsSet.add( searchId );
			}
			List<Integer> projectIdsFromSearchIds = ProjectIdsForProjectSearchIdsSearcher.getInstance().getProjectIdsForProjectSearchIds( projectSearchIdsSet );
			if ( projectIdsFromSearchIds.isEmpty() ) {
				// should never happen
				String msg = "No project ids for search ids: ";
				for ( int searchId : projectSearchIds ) {
					msg += searchId + ", ";
				}
				log.error( msg );
				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_DATA );
			}
			if ( projectIdsFromSearchIds.size() > 1 ) {
				//  Invalid request, searches across projects
				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_SEARCHES_ACROSS_PROJECTS );
			}
			int projectId = projectIdsFromSearchIds.get( 0 );
			request.setAttribute( "projectId", projectId ); 
			///////////////////////
			GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result accessAndSetupWebSessionResult =
					GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );
			if ( accessAndSetupWebSessionResult.isNoSession() ) {
				//  No User session 
				return mapping.findForward( StrutsGlobalForwardNames.NO_USER_SESSION );
			}
			//  Test access to the project id
			WebSessionAuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getWebSessionAuthAccessLevel();
			if ( ! authAccessLevel.isPublicAccessCodeReadAllowed() ) {
				//  No Access Allowed for this project id
				return mapping.findForward( StrutsGlobalForwardNames.INSUFFICIENT_ACCESS_PRIVILEGE );
			}
			request.setAttribute( WebConstants.REQUEST_AUTH_ACCESS_LEVEL, authAccessLevel );
			
			///    Done Processing Auth Check and Auth Level
			//////////////////////////////
			
			SearchDTO search = SearchDAO.getInstance().getSearchFromProjectSearchId( projectSearchId );
			if ( search == null ) {
				String msg = "projectSearchId '" + projectSearchId + "' not found in the database. User taken to home page.";
				log.warn( msg );
				//  Search not found, the data on the page they are requesting does not exist.
				//  The data on the user's previous page no longer reflects what is in the database.
				//  Take the user to the home page
				return mapping.findForward( StrutsGlobalForwardNames.HOME );  //  EARLY EXIT from Method
			}
			
			int searchId = search.getSearchId();
			
			OutputStreamWriter writer = null;
			try {
				
				////////     Get Download Data

				PeptideLengthVsPSMCount_For_PSMPeptideCutoffs_Method_Response methodResponse =
						PeptideLengthVsPSMCount_For_PSMPeptideCutoffs.getInstance()
						.getPeptideLengthVsPSMCount_For_PSMPeptideCutoffs( 
								ForDownload_Enum.YES,
								qcPageQueryJSONRoot, search );

			//  Get peptideLength and PSM counts mapped by link type
				Map<String, Map<Integer,MutableInt>> psmCount_Map_KeyedOnPpeptideLength_Map_KeyedOnLinkType = 
						methodResponse.getPsmCount_Map_KeyedOnPpeptideLength_Map_KeyedOnLinkType();
				
				List<String> linkTypesList = new ArrayList<>( psmCount_Map_KeyedOnPpeptideLength_Map_KeyedOnLinkType.size() );
				for ( String linkType : psmCount_Map_KeyedOnPpeptideLength_Map_KeyedOnLinkType.keySet() ) {
					linkTypesList.add( linkType );
				}
				
				DateTime dt = new DateTime();
				DateTimeFormatter fmt = DateTimeFormat.forPattern( "yyyy-MM-dd");

				// generate file name
				String filename = "proxl-qc-psm-peptide-length-" 
						+ StringUtils.join( linkTypesList, '-' ) 
						+ "-search-"
						+ search.getSearchId()
						+ "-" + fmt.print( dt )
						+ ".txt";
				
				response.setContentType("application/x-download");
				response.setHeader("Content-Disposition", "attachment; filename=" + filename);
				ServletOutputStream out = response.getOutputStream();
				BufferedOutputStream bos = new BufferedOutputStream(out);
				writer = new OutputStreamWriter( bos , ServletOutputStreamCharacterSetConstant.outputStreamCharacterSet );
								
				//  Write header line
				writer.write( "PEPTIDE LENGTH\tPSM COUNT\tLINK TYPE\tSEARCH ID" );
				writer.write( "\n" );
				
				for ( Map.Entry<String, Map<Integer,MutableInt>> entryPerLinkType : psmCount_Map_KeyedOnPpeptideLength_Map_KeyedOnLinkType.entrySet() ) {
					String linkType = entryPerLinkType.getKey();
					Map<Integer,MutableInt> peptideLengthCount_KeyPeptideLength = entryPerLinkType.getValue();
					// First sort on peptide length
					List<Map.Entry<Integer,MutableInt>> peptideLengthCount_KeyPeptideLength_List = new ArrayList<>( peptideLengthCount_KeyPeptideLength.size() );
					for ( Map.Entry<Integer,MutableInt> entryPerPeptideLength : peptideLengthCount_KeyPeptideLength.entrySet() ) {
						peptideLengthCount_KeyPeptideLength_List.add( entryPerPeptideLength );
					}
					Collections.sort( peptideLengthCount_KeyPeptideLength_List, new Comparator<Map.Entry<Integer,MutableInt>>() {
						@Override
						public int compare(Entry<Integer, MutableInt> o1, Entry<Integer, MutableInt> o2) {
							return o1.getKey() - o2.getKey();
						}
					} );

					for ( Map.Entry<Integer,MutableInt> entryPerPeptideLength : peptideLengthCount_KeyPeptideLength_List ) {
						//  1 output record for each PSM.  entryPerPeptideLength.getValue() is PSM count
						int psmCount = entryPerPeptideLength.getValue().intValue();
						writer.write( Integer.toString( entryPerPeptideLength.getKey() ) );
						writer.write( "\t" );
						writer.write( Integer.toString( psmCount ) );
						writer.write( "\t" );
						writer.write( linkType );
						writer.write( "\t" );
						writer.write( Integer.toString( searchId ) );
						writer.write( "\t" );
						writer.write( "\n" );
					}
				}
			} finally {
				try {
					if ( writer != null ) {
						writer.close();
					}
				} catch ( Exception ex ) {
					log.error( "writer.close():Exception " + ex.toString(), ex );
				}
				try {
					response.flushBuffer();
				} catch ( Exception ex ) {
					log.error( "response.flushBuffer():Exception " + ex.toString(), ex );
				}
			}
			return null;
		} catch ( Exception e ) {
			String msg = "Exception:  RemoteAddr: " + request.getRemoteAddr()  
					+ ", Exception caught: " + e.toString();
			log.error( msg, e );
			throw e;
		}
	}
	
}
