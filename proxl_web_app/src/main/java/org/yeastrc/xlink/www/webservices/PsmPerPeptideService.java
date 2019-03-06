package org.yeastrc.xlink.www.webservices;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.ScanDAO;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.dto.PsmDTO;
import org.yeastrc.xlink.dto.PsmPerPeptideAnnotationDTO;
import org.yeastrc.xlink.dto.PsmPerPeptideDTO;
import org.yeastrc.xlink.dto.ScanDTO;
import org.yeastrc.xlink.dto.SearchScanFilenameDTO;
import org.yeastrc.xlink.dto.SrchRepPeptPeptDynamicModDTO;
import org.yeastrc.xlink.enum_classes.FilterableDescriptiveAnnotationType;
import org.yeastrc.xlink.enum_classes.PsmPeptideAnnotationType;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.PsmPerPeptideAnnotationDataSearcher;
import org.yeastrc.xlink.www.searcher.SrchRepPeptPeptDynamicModSearcher;
import org.yeastrc.xlink.www.searcher.SrchRepPeptPeptideOnSearchIdRepPeptIdSearcher;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeData;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.dao.PeptideDAO;
import org.yeastrc.xlink.www.dao.PsmDAO;
import org.yeastrc.xlink.www.dao.PsmPerPeptideDAO;
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.www.dao.SearchScanFilenameDAO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.dto.SrchRepPeptPeptideDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
import org.yeastrc.xlink.www.web_utils.RetentionTimeScalingAndRounding;


@Path("/data")
public class PsmPerPeptideService {

	private static final Logger log = Logger.getLogger(PsmPerPeptideService.class);

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getPsmPerPeptide") 
	public WebserviceResult getViewerData( 
			@QueryParam( "psm_id" ) Integer psm_id,
			@QueryParam( "project_search_id" ) Integer projectSearchId,
			@Context HttpServletRequest request )
					throws Exception {

		if ( psm_id == null ) {
			String msg = "Provided psm_id is null or psm_id is missing";
			log.error( msg );
			throw new WebApplicationException(
					Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
					.entity( msg )
					.build()
					);
		}
		if ( projectSearchId == null ) {
			String msg = "Provided project_search_id is null or project_search_id is missing";
			log.error( msg );
			throw new WebApplicationException(
					Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
					.entity( msg )
					.build()
					);
		}

		try {
			// Get the session first.  
			//			HttpSession session = request.getSession();
			//   Get the project id for this search
			Collection<Integer> projectSearchIdsCollection = new HashSet<Integer>( );
			projectSearchIdsCollection.add( projectSearchId );
			List<Integer> projectIdsFromSearchIds = ProjectIdsForProjectSearchIdsSearcher.getInstance().getProjectIdsForProjectSearchIds( projectSearchIdsCollection );

			if ( projectIdsFromSearchIds.isEmpty() ) {
				// should never happen
				String msg = "No project ids for search id: " + projectSearchId;
				log.error( msg );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			if ( projectIdsFromSearchIds.size() > 1 ) {
				//  Invalid request, searches across projects
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_ACROSS_PROJECTS_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_ACROSS_PROJECTS_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			int projectId = projectIdsFromSearchIds.get( 0 );
			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );
			//			UserSessionObject userSessionObject = accessAndSetupWebSessionResult.getUserSessionObject();
			if ( accessAndSetupWebSessionResult.isNoSession() ) {
				//  No User session 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			//  Test access to the project id
			AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getAuthAccessLevel();
			//  Test access to the project id
			if ( ! authAccessLevel.isPublicAccessCodeReadAllowed() ) {
				//  No Access Allowed for this project id
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}

			////////   Auth complete
			//////////////////////////////////////////

			SearchDTO search = SearchDAO.getInstance().getSearchFromProjectSearchId( projectSearchId );
			if ( search == null ) {
				String msg = ": No search found for projectSearchId: " + projectSearchId;
				log.warn( msg );
				throw new WebApplicationException(
						Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
						.build()
						);
			}
			PsmDTO psmDTO = PsmDAO.getInstance().getPsmDTO( psm_id );
			if ( psmDTO == null ) {
				String msg = "Provided psm_id is not in DB: " + psm_id;
				log.error( msg );
				throw new WebApplicationException(
						Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
						.entity( msg )
						.build()
						);
			}

			if ( psmDTO.getSearchId() != search.getSearchId() ) {
				String msg = "Search Id from psm not match search id from project search id.  psm id: " + psm_id + ", project search id: " + projectSearchId;
				log.error( msg );
				throw new WebApplicationException(
						Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
						.entity( msg )
						.build()
						);
			}

			WebserviceResult webserviceResult = getPsmPerPeptideData( psmDTO );
			
			webserviceResult.setSearchHasScanData( search.isHasScanData() );

			return webserviceResult;

		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( ProxlWebappDataException e ) {
			String msg = "Exception processing request data, msg: " + e.toString();
			log.error( msg, e );
			throw new WebApplicationException(
					Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
					.entity( msg )
					.build()
					);			
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_TEXT ) // This string will be passed to the client
					.build()
					);
		}
	}

	/**
	 * @param psmDTO
	 * @return
	 * @throws Exception
	 */
	private WebserviceResult getPsmPerPeptideData( PsmDTO psmDTO ) throws Exception {

		int searchId = psmDTO.getSearchId();
		Integer searchIdObj = searchId;
		int reportedPeptideId = psmDTO.getReportedPeptideId();
		
		WebserviceResult webserviceResult = new WebserviceResult();
		
		Collection<Integer> searchIds = new ArrayList<Integer>( 1 );
		searchIds.add( searchId );

		//  Get annotation type ids for default display for PSM Per Peptide

		List<AnnotationTypeDTO> annotationTypeDefaultDisplayList = new ArrayList<>( 20 );

		//  Get Annotation Type records for PSM Per Peptide
		//    Filterable annotations
		Map<Integer, Map<Integer, AnnotationTypeDTO>> psmPerPeptideFilterableAnnotationTypeDTOListPerSearchIdMap =
				GetAnnotationTypeData.getInstance()
				.getAnnTypeForSearchIdsFiltDescPsmPeptide( searchIds, FilterableDescriptiveAnnotationType.FILTERABLE, PsmPeptideAnnotationType.PSM_PER_PEPTIDE );
		//    Descriptive annotations
		Map<Integer, Map<Integer, AnnotationTypeDTO>> psmPerPeptideDescriptiveAnnotationTypeDTOListPerSearchIdMap =
				GetAnnotationTypeData.getInstance()
				.getAnnTypeForSearchIdsFiltDescPsmPeptide( searchIds, FilterableDescriptiveAnnotationType.DESCRIPTIVE, PsmPeptideAnnotationType.PSM_PER_PEPTIDE );
		
		if ( psmPerPeptideFilterableAnnotationTypeDTOListPerSearchIdMap != null ) {
			Map<Integer, AnnotationTypeDTO> psmPerPeptideFilterableAnnotationTypeDTOPerAnnType = psmPerPeptideFilterableAnnotationTypeDTOListPerSearchIdMap.get( searchIdObj );
			if ( psmPerPeptideFilterableAnnotationTypeDTOPerAnnType != null ) {
				for ( Map.Entry<Integer, AnnotationTypeDTO> entry : psmPerPeptideFilterableAnnotationTypeDTOPerAnnType.entrySet() ) {
					AnnotationTypeDTO annotationTypeDTO = entry.getValue();
					if ( annotationTypeDTO.isDefaultVisible() ) {
						annotationTypeDefaultDisplayList.add( annotationTypeDTO );
					}
				}
			}
		}
		if ( psmPerPeptideDescriptiveAnnotationTypeDTOListPerSearchIdMap != null ) {
			Map<Integer, AnnotationTypeDTO> psmPerPeptideDescriptiveAnnotationTypeDTOPerAnnType = psmPerPeptideDescriptiveAnnotationTypeDTOListPerSearchIdMap.get( searchIdObj );
			if ( psmPerPeptideDescriptiveAnnotationTypeDTOPerAnnType != null ) {
				for ( Map.Entry<Integer, AnnotationTypeDTO> entry : psmPerPeptideDescriptiveAnnotationTypeDTOPerAnnType.entrySet() ) {
					AnnotationTypeDTO annotationTypeDTO = entry.getValue();
					if ( annotationTypeDTO.isDefaultVisible() ) {
						annotationTypeDefaultDisplayList.add( annotationTypeDTO );
					}
				}
			}
		}
				
		// Sort on ann type id
		Collections.sort( annotationTypeDefaultDisplayList, new Comparator<AnnotationTypeDTO>() {
			@Override
			public int compare(AnnotationTypeDTO o1, AnnotationTypeDTO o2) {
				if ( o1.getId() < o2.getId() ) {
					return -1;
				}
				if ( o1.getId() > o2.getId() ) {
					return 1;
				}
				return 0;
			}
		} );
		
		//  Build column header annotation names
		
		List<String> annotationLabels = new ArrayList<>( annotationTypeDefaultDisplayList.size() );
		webserviceResult.setAnnotationLabels( annotationLabels );
		for ( AnnotationTypeDTO annotationTypeDTO : annotationTypeDefaultDisplayList ) {
			annotationLabels.add( annotationTypeDTO.getName() );
		}

		//  Get SrchRepPeptPeptideDTO (1 or 2 records) for searchId, reportedPeptideId
		
		List<SrchRepPeptPeptideDTO> srchRepPeptPeptideDTOList = 
				SrchRepPeptPeptideOnSearchIdRepPeptIdSearcher.getInstance().getForSearchIdReportedPeptideId( searchId, reportedPeptideId );

		Collections.sort( srchRepPeptPeptideDTOList, new Comparator<SrchRepPeptPeptideDTO>() {
			@Override
			public int compare(SrchRepPeptPeptideDTO o1, SrchRepPeptPeptideDTO o2) {
				if ( o1.getPeptideId() < o2.getPeptideId() ) {
					return -1;
				}
				if ( o1.getPeptideId() > o2.getPeptideId() ) {
					return 1;
				}
				if ( o1.getId() < o2.getId() ) {
					return -1;
				}
				if ( o1.getId() > o2.getId() ) {
					return 1;
				}
				return 0;
			}
		});

		List<SinglePeptideRow> peptideRows = new ArrayList<>( srchRepPeptPeptideDTOList.size() );
		webserviceResult.setPeptideRows( peptideRows );
		
		for ( SrchRepPeptPeptideDTO srchRepPeptPeptideDTO : srchRepPeptPeptideDTOList ) {

			SinglePeptideRow singlePeptideRow = new SinglePeptideRow();
			peptideRows.add( singlePeptideRow );
			
			singlePeptideRow.setPsmId( psmDTO.getId() );
			singlePeptideRow.setSrchRepPeptPeptideId( srchRepPeptPeptideDTO.getId() );
			
			PeptideDTO peptideDTO = PeptideDAO.getInstance().getPeptideDTOFromDatabase( srchRepPeptPeptideDTO.getPeptideId() );
			if ( peptideDTO == null ) {
				String msg = "Peptide record not found for srchRepPeptPeptideDTO.  srchRepPeptPeptideDTO.id: " + srchRepPeptPeptideDTO.getId()
				+ ", srchRepPeptPeptideDTO.getPeptideId(): " + srchRepPeptPeptideDTO.getPeptideId();
				log.error( msg );
				throw new ProxlWebappInternalErrorException( msg );
			}
			singlePeptideRow.setPeptideSequence( peptideDTO.getSequence() );
			
			singlePeptideRow.setPeptideLinkPosition_1( srchRepPeptPeptideDTO.getPeptidePosition_1() );
			singlePeptideRow.setPeptideLinkPosition_2( srchRepPeptPeptideDTO.getPeptidePosition_2() );

			{ //  Get Dynamic Mod List
				
				List<SrchRepPeptPeptDynamicModDTO> srchRepPeptPeptDynamicModDTOList = 
						SrchRepPeptPeptDynamicModSearcher.getInstance().getSrchRepPeptPeptDynamicModForSrchRepPeptPeptideId( srchRepPeptPeptideDTO.getId() );
	
				Collections.sort( srchRepPeptPeptDynamicModDTOList, new Comparator<SrchRepPeptPeptDynamicModDTO>() {
					@Override
					public int compare(SrchRepPeptPeptDynamicModDTO o1, SrchRepPeptPeptDynamicModDTO o2) {
						if ( o1.getPosition() < o2.getPosition() ) {
							return -1;
						}
						if ( o1.getPosition() > o2.getPosition() ) {
							return 1;
						}
						if ( o1.getMass() < o2.getMass() ) {
							return -1;
						}
						if ( o1.getMass() > o2.getMass() ) {
							return 1;
						}					
						return 0;
					}
				});
	
				List<SingleMod> mods = new ArrayList<>( srchRepPeptPeptDynamicModDTOList.size() );
				singlePeptideRow.setMods( mods );
				
				for ( SrchRepPeptPeptDynamicModDTO srchRepPeptPeptDynamicModDTO : srchRepPeptPeptDynamicModDTOList ) {
					SingleMod singleMod = new SingleMod();
					mods.add( singleMod );
					singleMod.setMass( srchRepPeptPeptDynamicModDTO.getMass() );
					singleMod.setPosition( srchRepPeptPeptDynamicModDTO.getPosition() );
				}
			}
			
			{ //  Get data for psm_per_peptide record, This will not exist for some data since was added after the per annotation tables
				
				PsmPerPeptideDTO psmPerPeptideDTO =
						PsmPerPeptideDAO.getInstance()
						.getOnePsmPerPeptideDTOForPsmIdAndSrchRepPeptPeptideId( psmDTO.getId(), srchRepPeptPeptideDTO.getId() );
				
				if ( psmPerPeptideDTO != null ) {
					
					singlePeptideRow.setScanNumber( psmPerPeptideDTO.getScanNumber() );
					
					//  Get Scan Filename
					if ( psmPerPeptideDTO.getSearchScanFilenameId() != null ) {
						SearchScanFilenameDTO searchScanFilenameDTO =
								SearchScanFilenameDAO.getInstance().getSearchScanFilenameDTO( psmPerPeptideDTO.getSearchScanFilenameId() );
						if ( searchScanFilenameDTO == null ) {
							String msg = "No searchScanFilenameDTO for id: " 
									+ psmPerPeptideDTO.getSearchScanFilenameId()
									+ ", psmId: " + psmDTO.getId();
							log.error( msg );
							throw new ProxlWebappInternalErrorException(msg);
						}
						singlePeptideRow.setScanFilename( searchScanFilenameDTO.getFilename() );
					}
					
					//  Get Scan Data
					if ( psmPerPeptideDTO.getScanId() != null ) {
						//  Have scan data
						singlePeptideRow.setShowViewSpectrumLink(true);
						ScanDTO scanDTO = ScanDAO.getScanFromId( psmPerPeptideDTO.getScanId() );
						if ( scanDTO == null ) {
							String msg = "No scanDTO for id: " 
									+ psmPerPeptideDTO.getScanId()
									+ ", psmId: " + psmDTO.getId();
							log.error( msg );
							throw new ProxlWebappInternalErrorException(msg);
						}
						BigDecimal retentionTime = scanDTO.getRetentionTime();
						if ( retentionTime != null ) {
							singlePeptideRow.setRetentionTime( retentionTime );
							//  Get the retention time in minutes
							BigDecimal retentionInMinutesRounded = RetentionTimeScalingAndRounding.retentionTimeToMinutesRounded( retentionTime );
							singlePeptideRow.setRetentionTimeMinutesRounded( retentionInMinutesRounded );
							singlePeptideRow.setRetentionTimeMinutesRoundedString( retentionInMinutesRounded.toString() );
						}
						BigDecimal preMZ = scanDTO.getPreMZ();
						if ( preMZ != null ) {
							singlePeptideRow.setPreMZ( preMZ );
							//  Round the preMZ
							String preMZRoundedString = null;
							if ( preMZ != null ) {
								// first param to setScale is the number of decimal places to keep  
								BigDecimal preMZRounded = preMZ.setScale( 5, RoundingMode.HALF_UP );
								preMZRoundedString = preMZRounded.toString();  // convert to string so trailing zeros are preserved
							}
							singlePeptideRow.setPreMZRounded( preMZRoundedString );
						}
					}
				}
			}
			
			{ //  Get annotation values
			
				List<Integer> annotationTypeIdList = new ArrayList<>( annotationTypeDefaultDisplayList.size() );
				for ( AnnotationTypeDTO annotationTypeDTO : annotationTypeDefaultDisplayList ) {
					annotationTypeIdList.add( annotationTypeDTO.getId() );
				}
	
				List<PsmPerPeptideAnnotationDTO> psmPerPeptideAnnotationDTOList = 
						PsmPerPeptideAnnotationDataSearcher.getInstance().getPsmPerPeptideAnnotationDTOList( psmDTO.getId(), srchRepPeptPeptideDTO.getId(), annotationTypeIdList );
	
				Map<Integer,PsmPerPeptideAnnotationDTO> psmPerPeptideAnnotationDTO_Key_AnnTypeId = new HashMap<>();
				for ( PsmPerPeptideAnnotationDTO psmPerPeptideAnnotationDTO : psmPerPeptideAnnotationDTOList ) {
					psmPerPeptideAnnotationDTO_Key_AnnTypeId.put( psmPerPeptideAnnotationDTO.getAnnotationTypeId(), psmPerPeptideAnnotationDTO );
				}
				
				List<String> annotationValues = new ArrayList<String>( annotationTypeDefaultDisplayList.size() );
				singlePeptideRow.setAnnotationValues( annotationValues );
				
				for ( AnnotationTypeDTO annotationTypeDTO : annotationTypeDefaultDisplayList ) {
					PsmPerPeptideAnnotationDTO psmPerPeptideAnnotationDTO = psmPerPeptideAnnotationDTO_Key_AnnTypeId.get( annotationTypeDTO.getId() );
					if ( psmPerPeptideAnnotationDTO == null ) {
						String msg = "psmPerPeptideAnnotationDTO not found for annotation type id: " + annotationTypeDTO.getId()
						+ ", psm id: " + psmDTO.getId();
						log.error( msg );
						throw new ProxlWebappDataException( msg );
					}
					annotationValues.add( psmPerPeptideAnnotationDTO.getValueString() );
				}
			}
		}
		
		return webserviceResult;
	}

	/**
	 * Result from this webservice
	 *
	 */
	public static class WebserviceResult {

		private boolean searchHasScanData; //  Set in root method after returned from sub method
		
		private List<String> annotationLabels;
		
		private List<SinglePeptideRow> peptideRows;

		public List<SinglePeptideRow> getPeptideRows() {
			return peptideRows;
		}
		public void setPeptideRows(List<SinglePeptideRow> peptideRows) {
			this.peptideRows = peptideRows;
		}
		public List<String> getAnnotationLabels() {
			return annotationLabels;
		}
		public void setAnnotationLabels(List<String> annotationLabels) {
			this.annotationLabels = annotationLabels;
		}
		public boolean isSearchHasScanData() {
			return searchHasScanData;
		}
		public void setSearchHasScanData(boolean searchHasScanData) {
			this.searchHasScanData = searchHasScanData;
		}
	}

	/**
	 * Part of Result from this webservice
	 *
	 */
	public static class SinglePeptideRow {
		
		private int psmId;
		private int srchRepPeptPeptideId;

		private String peptideSequence;
		private Integer peptideLinkPosition_1;
		private Integer peptideLinkPosition_2;
		private List<SingleMod> mods;
		
		private Integer scanNumber;
		private String scanFilename;

		//  From scan
		
		private boolean showViewSpectrumLink;
		private BigDecimal retentionTime;
		private BigDecimal retentionTimeMinutesRounded;
		private String retentionTimeMinutesRoundedString;

		private BigDecimal preMZ;
		private String preMZRounded; 
		
		private List<String> annotationValues;

		public String getPeptideSequence() {
			return peptideSequence;
		}
		public void setPeptideSequence(String peptideSequence) {
			this.peptideSequence = peptideSequence;
		}
		public List<SingleMod> getMods() {
			return mods;
		}
		public void setMods(List<SingleMod> mods) {
			this.mods = mods;
		}
		public List<String> getAnnotationValues() {
			return annotationValues;
		}
		public void setAnnotationValues(List<String> annotationValues) {
			this.annotationValues = annotationValues;
		}
		public Integer getPeptideLinkPosition_1() {
			return peptideLinkPosition_1;
		}
		public void setPeptideLinkPosition_1(Integer peptideLinkPosition_1) {
			this.peptideLinkPosition_1 = peptideLinkPosition_1;
		}
		public Integer getPeptideLinkPosition_2() {
			return peptideLinkPosition_2;
		}
		public void setPeptideLinkPosition_2(Integer peptideLinkPosition_2) {
			this.peptideLinkPosition_2 = peptideLinkPosition_2;
		}
		public int getPsmId() {
			return psmId;
		}
		public void setPsmId(int psmId) {
			this.psmId = psmId;
		}
		public int getSrchRepPeptPeptideId() {
			return srchRepPeptPeptideId;
		}
		public void setSrchRepPeptPeptideId(int srchRepPeptPeptideId) {
			this.srchRepPeptPeptideId = srchRepPeptPeptideId;
		}
		public Integer getScanNumber() {
			return scanNumber;
		}
		public void setScanNumber(Integer scanNumber) {
			this.scanNumber = scanNumber;
		}
		public String getScanFilename() {
			return scanFilename;
		}
		public void setScanFilename(String scanFilename) {
			this.scanFilename = scanFilename;
		}
		public BigDecimal getRetentionTime() {
			return retentionTime;
		}
		public void setRetentionTime(BigDecimal retentionTime) {
			this.retentionTime = retentionTime;
		}
		public BigDecimal getRetentionTimeMinutesRounded() {
			return retentionTimeMinutesRounded;
		}
		public void setRetentionTimeMinutesRounded(BigDecimal retentionTimeMinutesRounded) {
			this.retentionTimeMinutesRounded = retentionTimeMinutesRounded;
		}
		public String getRetentionTimeMinutesRoundedString() {
			return retentionTimeMinutesRoundedString;
		}
		public void setRetentionTimeMinutesRoundedString(String retentionTimeMinutesRoundedString) {
			this.retentionTimeMinutesRoundedString = retentionTimeMinutesRoundedString;
		}
		public BigDecimal getPreMZ() {
			return preMZ;
		}
		public void setPreMZ(BigDecimal preMZ) {
			this.preMZ = preMZ;
		}
		public String getPreMZRounded() {
			return preMZRounded;
		}
		public void setPreMZRounded(String preMZRounded) {
			this.preMZRounded = preMZRounded;
		}
		public boolean isShowViewSpectrumLink() {
			return showViewSpectrumLink;
		}
		public void setShowViewSpectrumLink(boolean showViewSpectrumLink) {
			this.showViewSpectrumLink = showViewSpectrumLink;
		}
	}

	/**
	 * Part of Result from this webservice
	 *
	 */
	public static class SingleMod {

		private int position;
		private double mass;

		public int getPosition() {
			return position;
		}
		public void setPosition(int position) {
			this.position = position;
		}
		public double getMass() {
			return mass;
		}
		public void setMass(double mass) {
			this.mass = mass;
		}
	}
}
