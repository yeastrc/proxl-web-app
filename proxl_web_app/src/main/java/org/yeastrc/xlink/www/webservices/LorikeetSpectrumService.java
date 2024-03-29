package org.yeastrc.xlink.www.webservices;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.dao.PeptideDAO;
import org.yeastrc.xlink.www.dao.PsmDAO;
import org.yeastrc.xlink.www.dao.SearchScanFilenameDAO;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.enums.Get_ScanDataFromScanNumbers_IncludeParentScans;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.enums.Get_ScanData_ExcludeReturnScanPeakData;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.sub_parts.SingleScanPeak_SubResponse;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.sub_parts.SingleScan_SubResponse;
import org.yeastrc.xlink.dao.ScanFileDAO;
import org.yeastrc.xlink.dao.StaticModDAO;
import org.yeastrc.xlink.dto.IsotopeLabelDTO;
import org.yeastrc.xlink.dto.LinkerPerSearchCleavedCrosslinkMassDTO;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.dto.PsmDTO;
import org.yeastrc.xlink.dto.SearchLinkerDTO;
import org.yeastrc.xlink.dto.SearchScanFilenameDTO;
import org.yeastrc.xlink.dto.SrchRepPeptPeptDynamicModDTO;
import org.yeastrc.xlink.dto.StaticModDTO;
import org.yeastrc.xlink.utils.XLinkUtils;
import org.yeastrc.xlink.www.lorikeet_dto.LorikeetCrossLinkData;
import org.yeastrc.xlink.www.lorikeet_dto.LorikeetDimerData;
import org.yeastrc.xlink.www.lorikeet_dto.LorikeetGetSpectrumServiceResult;
import org.yeastrc.xlink.www.lorikeet_dto.LorikeetLoopLinkData;
import org.yeastrc.xlink.www.lorikeet_dto.LorikeetPerPeptideData;
import org.yeastrc.xlink.www.lorikeet_dto.LorikeetRootData;
import org.yeastrc.xlink.www.lorikeet_dto.LorikeetStaticMod;
import org.yeastrc.xlink.www.lorikeet_dto.LorikeetVariableMod;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.dto.SrchRepPeptPeptideDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;
import org.yeastrc.xlink.www.searcher.IsotopeLabelSearcher;
import org.yeastrc.xlink.www.searcher.LinkerPerSearchCleavedCrosslinkMass_Searcher;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.ProjectSearchIdsForSearchIdSearcher;
import org.yeastrc.xlink.www.searcher.SearchReportedPeptideLinkTypeSearcher;
import org.yeastrc.xlink.www.searcher.SrchRepPeptPeptDynamicModSearcher;
import org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders.Cached_SrchRepPeptPeptideDTO_ForSrchIdRepPeptId;
import org.yeastrc.xlink.www.searcher_via_cached_data.request_objects_for_searchers_for_cached_data.SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams;
import org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data.SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_Result;
import org.yeastrc.xlink.www.spectral_storage_service_interface.Call_Get_ScanDataFromScanNumbers_SpectralStorageWebservice;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;
import org.yeastrc.xlink.www.web_utils.SearchLinkerAndLinkerAbbreviationForLinkerMass_SingleSearch_Util;

/**
 * 
 *
 */
@Path("/lorikeetSpectrum")
public class LorikeetSpectrumService {
	
	private static final Logger log = LoggerFactory.getLogger( LorikeetSpectrumService.class);
	
	/**
	 * @param psmId
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getData") 
	public LorikeetGetSpectrumServiceResult getViewerData( @QueryParam( "psmId" ) int psmId,
										  @Context HttpServletRequest request )
	throws Exception {
		
		LorikeetRootData lorikeetRootData = new LorikeetRootData();
		LorikeetGetSpectrumServiceResult lorikeetGetSpectrumServiceResult = new LorikeetGetSpectrumServiceResult();
		lorikeetGetSpectrumServiceResult.setData( lorikeetRootData );
		List<LorikeetPerPeptideData> lorikeetPerPeptideDataList = new ArrayList<>();
		lorikeetGetSpectrumServiceResult.setLorikeetPerPeptideDataList( lorikeetPerPeptideDataList );
		if ( psmId == 0 ) {
			String msg = ": Provided psmId is zero";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
		    	        .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
		    	        .build()
		    	        );
		}
		try {
			PsmDTO psmDTO = null;
			//  Test access to the data
			try {
				psmDTO = PsmDAO.getInstance().getPsmDTO( psmId );
			} catch ( Exception e ) {
				String msg = "Error retrieving psm for psmId: " + psmId;
				log.error( msg, e );
				throw e;
			}
        	if ( psmDTO == null )  {
				String msg = "psm not found for psmId: " + psmId;
				log.error( msg);
			    throw new WebApplicationException(
			    	      Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
			    	        .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
			    	        .build()
			    	        );
        	}
        	int searchId = psmDTO.getSearchId();
        	//  Validate Auth access to this search id
        	//  Need access to at least one projectSearchId associated with this searchId
        	boolean allAuthHaveNoSession = true;
        	boolean accessAllowed = false;
        	List<Integer> projectSearchIdList = 
        			ProjectSearchIdsForSearchIdSearcher.getInstance().getProjectSearchIdsForSearchId( searchId );
        	for ( Integer projectSearchId : projectSearchIdList ) { 
    			//   Get the project id for this search
        		Collection<Integer> projectSearchIdsCollection = new HashSet<Integer>( );
        		projectSearchIdsCollection.add( projectSearchId );
        		List<Integer> projectIdsFromSearchIds = ProjectIdsForProjectSearchIdsSearcher.getInstance().getProjectIdsForProjectSearchIds( projectSearchIdsCollection );
        		if ( projectIdsFromSearchIds.isEmpty() ) {
        			// should never happen
        			String msg = "No project ids for projectSearchId: " + projectSearchId;
        			log.warn( msg );
        			throw new WebApplicationException(
        					Response.status( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_STATUS_CODE )  //  Send HTTP code
        					.entity( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_TEXT ) // This string will be passed to the client
        					.build()
        					);
        		}
        		if ( projectIdsFromSearchIds.size() > 1 ) {
        			//  Should never happen since query with one projectSearchId
        			//  Invalid request, searches across projects
        			throw new WebApplicationException(
        					Response.status( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_ACROSS_PROJECTS_STATUS_CODE )  //  Send HTTP code
        					.entity( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_ACROSS_PROJECTS_TEXT ) // This string will be passed to the client
        					.build()
        					);
        		}
        		int projectId = projectIdsFromSearchIds.get( 0 );
        		GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result accessAndSetupWebSessionResult =
        				GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );
        		//			UserSession userSession = accessAndSetupWebSessionResult.getUserSession();
//        		//  Test access to the project id
        		WebSessionAuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getWebSessionAuthAccessLevel();
        		if ( ( ! authAccessLevel.isPublicAccessCodeReadAllowed() ) && accessAndSetupWebSessionResult.isNoSession() ) {
        			//  No user session so not allowed
        			continue;
        		}
        		allAuthHaveNoSession = false;
        		if ( authAccessLevel.isPublicAccessCodeReadAllowed() ) {
        			accessAllowed = true;
        			break;
        		}
        	}
        	if ( allAuthHaveNoSession ) {
    			//  No User session 
    			throw new WebApplicationException(
    					Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
    					.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
    					.build()
    					);
        	}
        	if ( ! accessAllowed ) {
    			//  No Access Allowed for this project id
    			throw new WebApplicationException(
    					Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
    					.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
    					.build()
    					);
        	}
			////////   Auth complete
			//////////////////////////////////////////
        	
			int ms2ScanNumber = psmDTO.getScanNumber();
			
			if ( psmDTO.getSearchScanFilenameId() == null ) {
				String msg = "psmDTO.getSearchScanFilenameId() == null.  psmId: " + psmId;
				log.error( msg );
				throw new ProxlWebappInternalErrorException( msg );
			}
			
			SearchScanFilenameDTO searchScanFilenameDTO = 
					SearchScanFilenameDAO.getInstance().getSearchScanFilenameDTO( psmDTO.getSearchScanFilenameId() );

			if ( searchScanFilenameDTO == null ) {
				String msg = "searchScanFilenameDTO == null.  psmId: " + psmId;
				log.error( msg );
				throw new ProxlWebappInternalErrorException( msg );
			}

			if ( searchScanFilenameDTO.getScanFileId() == null ) {
				String msg = "searchScanFilenameDTO.getScanFileId() == null.  psmId: " + psmId;
				log.error( msg );
				throw new ProxlWebappInternalErrorException( msg );
			}
			
			int scanFileId = searchScanFilenameDTO.getScanFileId();

			//  Get Scan Data from Spectral Storage Service

			
			ScanDataFromSpectralStorageService_MS_2_1 scanDataFromSpectralStorageService_MS_2_1 = null;
			try {
				scanDataFromSpectralStorageService_MS_2_1 = getScanDataFromSpectralStorageService( ms2ScanNumber, scanFileId );
			} catch ( Exception e ) {
				String msg = "Failed to get scan data from Spectral Storage Service. ms2ScanNumber: " + ms2ScanNumber
						+ ", scanFileId: " + scanFileId;
				log.error( msg, e );
				throw e;
			}
			
			Double ms_2_precursor_M_Over_Z = null;
			
			if ( psmDTO.getPrecursor_MZ() != null ) {
				ms_2_precursor_M_Over_Z = psmDTO.getPrecursor_MZ().doubleValue();
			}
			
			if ( ms_2_precursor_M_Over_Z == null ) {
				//  No ms_2_precursor_M_Over_Z on PSM so get from Scan
				ms_2_precursor_M_Over_Z = scanDataFromSpectralStorageService_MS_2_1.ms_2_scanDataFromSpectralStorageService.getPrecursor_M_Over_Z();
			}
			
			if ( ms_2_precursor_M_Over_Z == null ) {
				String msg = "ms_2_precursor_M_Over_Z == null: psm id: " + psmId;
				log.error( msg );
				throw new ProxlWebappInternalErrorException(msg);
			}
			
        	///////////////////////
        	String scanFilename = searchScanFilenameDTO.getFilename();

        	lorikeetRootData.setFileName( scanFilename );
			lorikeetRootData.setPrecursorMz( ms_2_precursor_M_Over_Z );
			{
				// Add MS 2 peaks to output 
				SingleScan_SubResponse ms_2_scanDataFromSpectralStorageService = scanDataFromSpectralStorageService_MS_2_1.ms_2_scanDataFromSpectralStorageService;
				for ( SingleScanPeak_SubResponse scanPeak : ms_2_scanDataFromSpectralStorageService.getPeaks() ) {
					lorikeetRootData.addPeak( scanPeak.getMz(), scanPeak.getIntensity() );
				}
			}
			{
				//  Add MS 1 peaks if exists
				SingleScan_SubResponse ms_1_scanDataFromSpectralStorageService = scanDataFromSpectralStorageService_MS_2_1.ms_1_scanDataFromSpectralStorageService;
				if ( ms_1_scanDataFromSpectralStorageService != null )  {
					for ( SingleScanPeak_SubResponse scanPeak : ms_1_scanDataFromSpectralStorageService.getPeaks() ) {
						lorikeetRootData.addMs1Peak( scanPeak.getMz(), scanPeak.getIntensity() );
					}
				}     
			}
			
	        //
	        lorikeetRootData.setCharge( psmDTO.getCharge() );
	        lorikeetRootData.setScanNum( psmDTO.getScanNumber() );
	        List<StaticModDTO> staticModsForSearch = StaticModDAO.getInstance().getStaticModDTOForSearchId( searchId );
	        List<LorikeetStaticMod> staticModsLorikeet = new ArrayList<>( staticModsForSearch.size() );
	        for ( StaticModDTO staticModDTO : staticModsForSearch ) {
	        	LorikeetStaticMod  staticModLorikeet = new LorikeetStaticMod();
	        	staticModLorikeet.setAminoAcid( staticModDTO.getResidue() );
	        	staticModLorikeet.setModMass( staticModDTO.getMass().doubleValue() );
	        	staticModsLorikeet.add( staticModLorikeet );
	        }
	        lorikeetRootData.setStaticMods( staticModsLorikeet );
	        Integer linkTypeNumber =
	        		SearchReportedPeptideLinkTypeSearcher.getInstance()
	        		.getSearchReportedPeptideLinkTypeNumber( searchId, psmDTO.getReportedPeptideId() );
	        if ( linkTypeNumber == null ) {
	        	String msg = "Failed to get linkTypeNumber for searchId: " + searchId
	        			+ ", reportedPeptideId: " + psmDTO.getReportedPeptideId() ;
	        	log.error( msg );
	        	throw new ProxlWebappDataException(msg);
	        }
        	if ( linkTypeNumber == XLinkUtils.TYPE_CROSSLINK ) {
        		LorikeetCrossLinkData lorikeetCrossLinkData = getCrossLinkData( psmDTO );
        		lorikeetRootData.setCrossLinkDataInputFormat( lorikeetCrossLinkData );
        	} else if ( linkTypeNumber == XLinkUtils.TYPE_LOOPLINK ) {
        		LorikeetLoopLinkData lorikeetLoopLinkData = getLoopLinkData( psmDTO );
        		lorikeetRootData.setLoopLinkDataInputFormat( lorikeetLoopLinkData );
        	} else if ( linkTypeNumber == XLinkUtils.TYPE_UNLINKED ) {  //  Monolink is also unlinked on the psm
        		LorikeetPerPeptideData lorikeetPerPeptideData = getUnlinkData( psmDTO );
        		lorikeetRootData.setSequence( lorikeetPerPeptideData.getSequence() );
        		lorikeetRootData.setVariableMods( lorikeetPerPeptideData.getVariableMods() );
    	        lorikeetRootData.setNtermMod( lorikeetPerPeptideData.getNtermMod() );
    	        lorikeetRootData.setCtermMod( lorikeetPerPeptideData.getCtermMod() );
        		lorikeetRootData.setLabel( lorikeetPerPeptideData.getLabel() );
        	} else if ( linkTypeNumber == XLinkUtils.TYPE_DIMER ) {  
        		LorikeetDimerData dimerDataInputFormat = getDimerData( psmDTO );
        		lorikeetRootData.setDimerDataInputFormat( dimerDataInputFormat );
        	} else {
        		String msg = "linkTypeNumber is other than Unlinked, Cross Link or Loop Link.  linkTypeNumber: " + linkTypeNumber;
        		log.error( msg );
        		throw new Exception(msg);
        	}
			return lorikeetGetSpectrumServiceResult;
		} catch ( WebApplicationException e ) {
			throw e;
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
	 * 
	 *
	 */
	private static class ScanDataFromSpectralStorageService_MS_2_1 {
		private SingleScan_SubResponse ms_1_scanDataFromSpectralStorageService;
		private SingleScan_SubResponse ms_2_scanDataFromSpectralStorageService;
	}

	/**
	 * Get Scan Data from Spectral Storage Service
	 * 
	 * @param scanDTO_ms_2
	 * @param scanDTO_ms_1
	 * @throws Exception 
	 */
	private ScanDataFromSpectralStorageService_MS_2_1 getScanDataFromSpectralStorageService( int ms2ScanNumber, int scanFileId ) throws Exception {
		
		String scanFileAPIKey = ScanFileDAO.getInstance().getSpectralStorageAPIKeyById( scanFileId );
		if ( StringUtils.isEmpty( scanFileAPIKey ) ) {
			String msg = "No value for scanFileAPIKey for scan file id: " + scanFileId;
			log.error( msg );
			throw new ProxlWebappDataException( msg );
		}


		//  Get scans from Spectral Storage Service.
		//  Send ms2 scan number and request parent scan as well
		
		List<Integer> scanNumbers = new ArrayList<>( 3 );
		scanNumbers.add(  ms2ScanNumber  );

		List<SingleScan_SubResponse> scans = 
				Call_Get_ScanDataFromScanNumbers_SpectralStorageWebservice.getSingletonInstance()
				.getScanDataFromSpectralStorageService(
						scanNumbers, 
						Get_ScanDataFromScanNumbers_IncludeParentScans.IMMEDIATE_PARENT,
						Get_ScanData_ExcludeReturnScanPeakData.NO,
						scanFileAPIKey );
		
		ScanDataFromSpectralStorageService_MS_2_1 scanDataFromSpectralStorageService_MS_2_1 = new ScanDataFromSpectralStorageService_MS_2_1();
		
		//  Get ms2 scan from returned list
		for ( SingleScan_SubResponse scan : scans ) {
			if ( scan.getScanNumber() == ms2ScanNumber ) {
				scanDataFromSpectralStorageService_MS_2_1.ms_2_scanDataFromSpectralStorageService = scan;
				break;
			}
		}

		if ( scanDataFromSpectralStorageService_MS_2_1.ms_2_scanDataFromSpectralStorageService == null ) {
			//  ms2 scan number not found in returned list
			String msg = "No ms2 scan found in spectral storage service for scan number: " 
					+ ms2ScanNumber
					+ ", API Key: " + scanFileAPIKey
					+ ", scan file id: " + scanFileId;
			log.error( msg );
			throw new ProxlWebappDataException(msg);
		}

		//  Get ms1 scan from returned list

		if ( scanDataFromSpectralStorageService_MS_2_1.ms_2_scanDataFromSpectralStorageService.getParentScanNumber() == null ) {
			//  ms2 scan parent scan number not populated
			String msg = "The ms2 scan retrieved from spectral storage service"
					+ " did not have a parent scan number"
					+ " for ms2 scan number: " + scanDataFromSpectralStorageService_MS_2_1.ms_2_scanDataFromSpectralStorageService.getScanNumber()
					+ ", API Key: " + scanFileAPIKey
					+ ", scan file id: " + scanFileId;
			log.error( msg );
			throw new ProxlWebappDataException(msg);
		}
		
		int ms1_ScanNumber = scanDataFromSpectralStorageService_MS_2_1.ms_2_scanDataFromSpectralStorageService.getParentScanNumber();
		
		for ( SingleScan_SubResponse scan : scans ) {
			if ( scan.getScanNumber() == ms1_ScanNumber ) {
				scanDataFromSpectralStorageService_MS_2_1.ms_1_scanDataFromSpectralStorageService = scan;
				break;
			}
		}

		if ( scanDataFromSpectralStorageService_MS_2_1.ms_1_scanDataFromSpectralStorageService == null ) {
			//  ms1 scan number not found in returned list
			String msg = "No ms1 scan found in spectral storage service for scan number: " 
					+ ms1_ScanNumber
					+ ", API Key: " + scanFileAPIKey
					+ ", scan file id: " + scanFileId;
			log.error( msg );
//			throw new ProxlWebappDataException(msg);
		}
		
		return scanDataFromSpectralStorageService_MS_2_1;
	}
	
	
	/**
	 * @param psmDTO
	 * @return
	 * @throws Exception
	 */
	private LorikeetCrossLinkData getCrossLinkData( PsmDTO psmDTO ) throws Exception {
		
		if ( psmDTO.getLinkerMass() == null ) {
			String msg = "getCrossLinkData(...): psmDTO.getLinkerMass() is null for psm id: " + psmDTO.getId();
			log.error(msg);
			throw new ProxlWebappDataException(msg);
		}
		
		LorikeetCrossLinkData lorikeetCrossLinkData = new LorikeetCrossLinkData();
		lorikeetCrossLinkData.setLinkerMass(  psmDTO.getLinkerMass() );
		
		SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams srchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams = new SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams();
		srchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams.setSearchId( psmDTO.getSearchId() );
		srchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams.setReportedPeptideId( psmDTO.getReportedPeptideId() );
		SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_Result srchRepPeptPeptideDTO_ForSrchIdRepPeptId_Result =
				Cached_SrchRepPeptPeptideDTO_ForSrchIdRepPeptId.getInstance()
				.getSrchRepPeptPeptideDTO_ForSrchIdRepPeptId_Result( srchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams );
		List<SrchRepPeptPeptideDTO> srchRepPeptPeptideDTOList = srchRepPeptPeptideDTO_ForSrchIdRepPeptId_Result.getSrchRepPeptPeptideDTOList();

		if ( srchRepPeptPeptideDTOList.size() != 2 ) {
			String msg = "List<SrchRepPeptPeptideDTO> results.size() != 2. SearchId: " + psmDTO.getSearchId()
					+ ", ReportedPeptideId: " + psmDTO.getReportedPeptideId() ;
			log.error( msg );
			throw new ProxlWebappDataException( msg );
		}
		SrchRepPeptPeptideDTO srchRepPeptPeptideDTO_1 = srchRepPeptPeptideDTOList.get( 0 );
		SrchRepPeptPeptideDTO srchRepPeptPeptideDTO_2 = srchRepPeptPeptideDTOList.get( 1 );
		lorikeetCrossLinkData.setCrossLinkPos1( srchRepPeptPeptideDTO_1.getPeptidePosition_1() );
		lorikeetCrossLinkData.setCrossLinkPos2( srchRepPeptPeptideDTO_2.getPeptidePosition_1() );
		LorikeetPerPeptideData peptideData1 = getLorikeetPerPeptideData( srchRepPeptPeptideDTO_1 );
		lorikeetCrossLinkData.setPeptideData1( peptideData1 );
		LorikeetPerPeptideData peptideData2 = getLorikeetPerPeptideData( srchRepPeptPeptideDTO_2 );
		lorikeetCrossLinkData.setPeptideData2( peptideData2 );
		
		{	
			//  Get cleaved_crosslink_mass records for search id
			List<LinkerPerSearchCleavedCrosslinkMassDTO> cleavedCrosslinkMassList_FromDB = LinkerPerSearchCleavedCrosslinkMass_Searcher.getInstance().getForSearchId( psmDTO.getSearchId() );
			if ( ! cleavedCrosslinkMassList_FromDB.isEmpty() ) {
				
				
				SearchLinkerAndLinkerAbbreviationForLinkerMass_SingleSearch_Util searchLinkerAndLinkerAbbreviationForLinkerMass_SingleSearch_Util = 
						SearchLinkerAndLinkerAbbreviationForLinkerMass_SingleSearch_Util.getInstanceForSearchId( psmDTO.getSearchId() );
				
				SearchLinkerDTO searchLinkerDTO = searchLinkerAndLinkerAbbreviationForLinkerMass_SingleSearch_Util.getSearchLinkerDTOForLinkerMass( psmDTO.getLinkerMass() );
				
				if ( searchLinkerDTO == null ) {
					String msg = "No searchLinkerDTO for psm id: " 
							+ psmDTO.getId() 
							+ ", search id: " + psmDTO.getSearchId();
					log.error( msg );
					throw new ProxlWebappDataException( msg );
				}
				
				//  Get cleaved_crosslink_mass records for linker id
				List<LinkerPerSearchCleavedCrosslinkMassDTO> cleavedCrosslinkMassList_ForLinkerId = new ArrayList<>( cleavedCrosslinkMassList_FromDB.size() );
				for ( LinkerPerSearchCleavedCrosslinkMassDTO entry : cleavedCrosslinkMassList_FromDB ) {
					if ( entry.getSearchLinkerId() == searchLinkerDTO.getId() ) {
						cleavedCrosslinkMassList_ForLinkerId.add( entry );
					}
				}
				if ( ! cleavedCrosslinkMassList_ForLinkerId.isEmpty() ) {
					//  Found entries for cleavedCrosslinkMassList_ForLinkerId for Linker id
					
					//  Set linker abbr
					lorikeetCrossLinkData.setLinkerAbbr( searchLinkerDTO.getLinkerAbbr() );
					
					//  Populate cleavedCrosslinkMassList in result
					List<BigDecimal> cleavedLinkerMassList = new ArrayList<>( cleavedCrosslinkMassList_ForLinkerId.size() );
					for ( LinkerPerSearchCleavedCrosslinkMassDTO entry : cleavedCrosslinkMassList_ForLinkerId ) {
						BigDecimal cleavedLinkerMassBD = new BigDecimal( entry.getCleavedCrosslinkMassString() );
						cleavedLinkerMassList.add( cleavedLinkerMassBD );
					}
					lorikeetCrossLinkData.setCleavedLinkerMassList( cleavedLinkerMassList );
				}
			}
		}
		return lorikeetCrossLinkData;
	}
	
	/**
	 * @param psmDTO
	 * @return
	 * @throws Exception
	 */
	private LorikeetDimerData getDimerData( PsmDTO psmDTO ) throws Exception {
		LorikeetDimerData lorikeetDimerData = new LorikeetDimerData();
		
		SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams srchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams = new SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams();
		srchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams.setSearchId( psmDTO.getSearchId() );
		srchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams.setReportedPeptideId( psmDTO.getReportedPeptideId() );
		SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_Result srchRepPeptPeptideDTO_ForSrchIdRepPeptId_Result =
				Cached_SrchRepPeptPeptideDTO_ForSrchIdRepPeptId.getInstance()
				.getSrchRepPeptPeptideDTO_ForSrchIdRepPeptId_Result( srchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams );
		List<SrchRepPeptPeptideDTO> srchRepPeptPeptideDTOList = srchRepPeptPeptideDTO_ForSrchIdRepPeptId_Result.getSrchRepPeptPeptideDTOList();

		if ( srchRepPeptPeptideDTOList.size() != 2 ) {
			String msg = "List<SrchRepPeptPeptideDTO> results.size() != 2. SearchId: " + psmDTO.getSearchId()
					+ ", ReportedPeptideId: " + psmDTO.getReportedPeptideId() ;
			log.error( msg );
			throw new ProxlWebappDataException( msg );
		}
		SrchRepPeptPeptideDTO srchRepPeptPeptideDTO_1 = srchRepPeptPeptideDTOList.get( 0 );
		SrchRepPeptPeptideDTO srchRepPeptPeptideDTO_2 = srchRepPeptPeptideDTOList.get( 1 );
		LorikeetPerPeptideData peptideData1 = getLorikeetPerPeptideData( srchRepPeptPeptideDTO_1 );
		lorikeetDimerData.setPeptideData1( peptideData1 );
		LorikeetPerPeptideData peptideData2 = getLorikeetPerPeptideData( srchRepPeptPeptideDTO_2 );
		lorikeetDimerData.setPeptideData2( peptideData2 );
		return lorikeetDimerData;
	}
	
	/**
	 * @param psmDTO
	 * @return
	 * @throws Exception
	 */
	private LorikeetLoopLinkData getLoopLinkData( PsmDTO psmDTO ) throws Exception {
		LorikeetLoopLinkData lorikeetLoopLinkData = new LorikeetLoopLinkData();
		lorikeetLoopLinkData.setLinkerMass(  psmDTO.getLinkerMass() );
		
		SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams srchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams = new SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams();
		srchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams.setSearchId( psmDTO.getSearchId() );
		srchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams.setReportedPeptideId( psmDTO.getReportedPeptideId() );
		SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_Result srchRepPeptPeptideDTO_ForSrchIdRepPeptId_Result =
				Cached_SrchRepPeptPeptideDTO_ForSrchIdRepPeptId.getInstance()
				.getSrchRepPeptPeptideDTO_ForSrchIdRepPeptId_Result( srchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams );
		List<SrchRepPeptPeptideDTO> srchRepPeptPeptideDTOList = srchRepPeptPeptideDTO_ForSrchIdRepPeptId_Result.getSrchRepPeptPeptideDTOList();

		if ( srchRepPeptPeptideDTOList.size() != 1 ) {
			String msg = "List<SrchRepPeptPeptideDTO> results.size() != 1. SearchId: " + psmDTO.getSearchId()
					+ ", ReportedPeptideId: " + psmDTO.getReportedPeptideId() ;
			log.error( msg );
			throw new ProxlWebappDataException( msg );
		}
		SrchRepPeptPeptideDTO srchRepPeptPeptideDTO = srchRepPeptPeptideDTOList.get( 0 );
		lorikeetLoopLinkData.setLoopLinkPos1( srchRepPeptPeptideDTO.getPeptidePosition_1() );
		lorikeetLoopLinkData.setLoopLinkPos2( srchRepPeptPeptideDTO.getPeptidePosition_2() );
		LorikeetPerPeptideData peptideData = getLorikeetPerPeptideData( srchRepPeptPeptideDTO );
		lorikeetLoopLinkData.setPeptideData( peptideData );
		return lorikeetLoopLinkData;
	}
	
	/**
	 * @param psmDTO
	 * @return
	 * @throws Exception
	 */
	private LorikeetPerPeptideData getUnlinkData( PsmDTO psmDTO ) throws Exception {
		
		SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams srchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams = new SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams();
		srchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams.setSearchId( psmDTO.getSearchId() );
		srchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams.setReportedPeptideId( psmDTO.getReportedPeptideId() );
		SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_Result srchRepPeptPeptideDTO_ForSrchIdRepPeptId_Result =
				Cached_SrchRepPeptPeptideDTO_ForSrchIdRepPeptId.getInstance()
				.getSrchRepPeptPeptideDTO_ForSrchIdRepPeptId_Result( srchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams );
		List<SrchRepPeptPeptideDTO> srchRepPeptPeptideDTOList = srchRepPeptPeptideDTO_ForSrchIdRepPeptId_Result.getSrchRepPeptPeptideDTOList();

		if ( srchRepPeptPeptideDTOList.size() != 1 ) {
			String msg = "List<SrchRepPeptPeptideDTO> results.size() != 1. SearchId: " + psmDTO.getSearchId()
					+ ", ReportedPeptideId: " + psmDTO.getReportedPeptideId() ;
			log.error( msg );
			throw new ProxlWebappDataException( msg );
		}
		SrchRepPeptPeptideDTO srchRepPeptPeptideDTO = srchRepPeptPeptideDTOList.get( 0 );
		LorikeetPerPeptideData peptideData = getLorikeetPerPeptideData( srchRepPeptPeptideDTO );
		return peptideData;
	}
	
	/**
	 * @param matchedPeptideDTO
	 * @return
	 * @throws Exception
	 */
	private LorikeetPerPeptideData getLorikeetPerPeptideData( SrchRepPeptPeptideDTO srchRepPeptPeptideDTO ) throws Exception {
		LorikeetPerPeptideData lorikeetPerPeptideData = new LorikeetPerPeptideData();
		int peptideId = srchRepPeptPeptideDTO.getPeptideId();
		List<SrchRepPeptPeptDynamicModDTO> dynamicModList = 
				SrchRepPeptPeptDynamicModSearcher.getInstance()
				.getSrchRepPeptPeptDynamicModForSrchRepPeptPeptideId( srchRepPeptPeptideDTO.getId() );
		// Load our peptide
		PeptideDTO peptide = PeptideDAO.getInstance().getPeptideDTOFromDatabase( peptideId );
		if ( peptide == null ) {
			String msg = "Unable to find peptide record for peptide id = " + peptideId;
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
		    	        .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
		    	        .build()
		    	        );
		}
		String sequence = peptide.getSequence();
        
		List<LorikeetVariableMod> variableMods = new ArrayList<>( dynamicModList.size() );

		double ntermMod = 0; // additional mass to be added to the n-term
		double ctermMod = 0; // additional mass to be added to the c-term
		
        for ( SrchRepPeptPeptDynamicModDTO dynamicMod : dynamicModList ) {
        	if ( dynamicMod.isIs_N_Terminal() ) {
        		ntermMod += dynamicMod.getMass();
        	} else if ( dynamicMod.isIs_C_Terminal() ) {
        		ctermMod += dynamicMod.getMass();
        	} else {
        		int dynamicModPosition = dynamicMod.getPosition();
        		String aminoAcid = sequence.substring( dynamicModPosition - 1 /* chg to zero based */, dynamicModPosition );
        		LorikeetVariableMod lorikeetVariableMod = new LorikeetVariableMod();
        		lorikeetVariableMod.setIndex( dynamicMod.getPosition() );
        		lorikeetVariableMod.setModMass( dynamicMod.getMass() );
        		lorikeetVariableMod.setAminoAcid( aminoAcid );
        		variableMods.add( lorikeetVariableMod );
        	}
        }
		lorikeetPerPeptideData.setSequence( sequence );
		lorikeetPerPeptideData.setVariableMods( variableMods );
		lorikeetPerPeptideData.setNtermMod( ntermMod );
		lorikeetPerPeptideData.setCtermMod( ctermMod );
		
		// get any stable isotope label
		IsotopeLabelDTO labelDTO = IsotopeLabelSearcher.getInstance().getIsotopeLabelForSearchReportedPeptide_Peptide( srchRepPeptPeptideDTO );
		if( labelDTO != null )
			lorikeetPerPeptideData.setLabel( labelDTO.getName() );
		
		return lorikeetPerPeptideData;
	}
}
