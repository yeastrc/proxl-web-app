package org.yeastrc.xlink.www.webservices;



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

import org.apache.log4j.Logger;
import org.yeastrc.xlink.base.spectrum.common.dto.Peak;
import org.yeastrc.xlink.base.spectrum.common.utils.StringToPeaks;
import org.yeastrc.xlink.dao.CrosslinkDAO;
import org.yeastrc.xlink.dao.DynamicModDAO;
import org.yeastrc.xlink.dao.LooplinkDAO;
import org.yeastrc.xlink.dao.MatchedPeptideDAO;
import org.yeastrc.xlink.dao.PeptideDAO;
import org.yeastrc.xlink.dao.PsmDAO;
import org.yeastrc.xlink.dao.ScanDAO;
import org.yeastrc.xlink.dao.ScanFileDAO;
import org.yeastrc.xlink.dao.StaticModDAO;
import org.yeastrc.xlink.dto.CrosslinkDTO;
import org.yeastrc.xlink.dto.DynamicModDTO;
import org.yeastrc.xlink.dto.LooplinkDTO;
import org.yeastrc.xlink.dto.MatchedPeptideDTO;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.dto.PsmDTO;
import org.yeastrc.xlink.dto.ScanDTO;
import org.yeastrc.xlink.dto.ScanFileDTO;
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
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.searcher.ProjectIdsForSearchIdsSearcher;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;



@Path("/lorikeetSpectrum")
public class LorikeetSpectrumService {

	private static final Logger log = Logger.getLogger(LorikeetSpectrumService.class);
	
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
			

			// Get the session first.  
//			HttpSession session = request.getSession();


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

			//   Get the project id for this search
			
			Collection<Integer> searchIdsCollection = new HashSet<Integer>( );

			searchIdsCollection.add( searchId );
			
			
			List<Integer> projectIdsFromSearchIds = ProjectIdsForSearchIdsSearcher.getInstance().getProjectIdsForSearchIds( searchIdsCollection );
			
			if ( projectIdsFromSearchIds.isEmpty() ) {
				
				// should never happen
				
				String msg = "No project ids for search id: " + searchId;
				
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

			if ( ! authAccessLevel.isPublicAccessCodeReadAllowed() ) {

				//  No Access Allowed for this project id

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}


			
			
			ScanDTO scanDTO = null;
			
			int scanId = psmDTO.getScanId();

			try {

				 scanDTO = ScanDAO.getScanFromId( scanId );
				
			} catch ( Exception e ) {
				
				String msg = "Error retrieving scan for scanId: " + scanId;
				
				log.error( msg, e );
				
				throw e;
			}
			

        	if ( scanDTO == null )  {

        		
				String msg = "scan not found for scanId: " + scanId;
				
				log.error( msg);
				

				//  TODO  Return something else instead
				
			    throw new WebApplicationException(
			    	      Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
			    	        .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
			    	        .build()
			    	        );
				
        	}
        	
        	///////////////////////
        	
        	

			ScanDTO scanDTO_Ms1 = null;
			
			int scanId_Ms1 = scanDTO.getPrecursorScanId();

			try {

				scanDTO_Ms1 = ScanDAO.getScanFromId( scanId_Ms1 );
				
			} catch ( Exception e ) {
				
				String msg = "Error retrieving scan for scanId_Ms1: " + scanId_Ms1;
				
				log.error( msg, e );
				
				throw e;
			}
			

			//  Ignore if not found
//        	if ( scanDTO_Ms1 == null )  {
//
//        		
//				String msg = "scan not found for scanId_Ms1: " + scanId_Ms1;
//				
//				log.error( msg);
//				
//
//				//  TODO  Return something else instead
//				
//			    throw new WebApplicationException(
//			    	      Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
//			    	        .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
//			    	        .build()
//			    	        );
//				
//        	}
			
   	
        	
        	
        	///////////////////////
        	
        	ScanFileDTO scanFileDTO = null;

			try {

				scanFileDTO = ScanFileDAO.getInstance().getScanFileDTOById( scanDTO.getScanFileId() );

			} catch ( Exception e ) {
				
				String msg = "Error retrieving scan_file for scanFileId: " + scanDTO.getScanFileId();
				
				log.error( msg, e );
				
				throw e;
			}
			

        	if ( scanFileDTO == null )  {

        		
				String msg = "scan_file not found for scanFileId: " + scanDTO.getScanFileId();
				
				log.error( msg);
				

				//  TODO  Return something else instead
				
			    throw new WebApplicationException(
			    	      Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
			    	        .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
			    	        .build()
			    	        );
				
        	}
        	
        	lorikeetRootData.setFileName( scanFileDTO.getFilename() );

			
			lorikeetRootData.setPrecursorMz( scanDTO.getPreMZ() );


	        String mzIntListAsString = scanDTO.getMzIntListAsString();
	        
	        List<Peak> peakList = StringToPeaks.peakStringToList( mzIntListAsString );

	        for ( Peak peak : peakList ) {

	        	lorikeetRootData.addPeak( peak.getMz(), peak.getIntensity() );
	        }
	        
		    //  Add ms1 peak if exists

        	if ( scanDTO_Ms1 != null )  {

    	        String mzIntListAsString_Ms1 = scanDTO_Ms1.getMzIntListAsString();
    	        
    	        List<Peak> peakList_Ms1 = StringToPeaks.peakStringToList( mzIntListAsString_Ms1 );

    	        for ( Peak peak_Ms1 : peakList_Ms1 ) {

    	        	lorikeetRootData.addMs1Peak( peak_Ms1.getMz(), peak_Ms1.getIntensity() );
    	        }
        	}     
	        
	        
	        //
	        
	        lorikeetRootData.setCharge( psmDTO.getCharge() );
	        
	        lorikeetRootData.setScanNum( scanDTO.getStartScanNumber() );
	        
	        
	        List<StaticModDTO> staticModsForSearch = StaticModDAO.getInstance().getStaticModDTOForSearchId( searchId );
	        
	        List<LorikeetStaticMod> staticModsLorikeet = new ArrayList<>( staticModsForSearch.size() );
	        
	        for ( StaticModDTO staticModDTO : staticModsForSearch ) {
	        	
	        	LorikeetStaticMod  staticModLorikeet = new LorikeetStaticMod();
	        	
	        	staticModLorikeet.setAminoAcid( staticModDTO.getResidue() );
	        	staticModLorikeet.setModMass( staticModDTO.getMass().doubleValue() );
	        	
	        	staticModsLorikeet.add( staticModLorikeet );
	        }
	        
	        lorikeetRootData.setStaticMods( staticModsLorikeet );
	        
	        
	        
	        
        	
        	if ( psmDTO.getType() == XLinkUtils.TYPE_CROSSLINK ) {
        		
        		LorikeetCrossLinkData lorikeetCrossLinkData = getCrossLinkData( psmDTO );
        		
        		lorikeetRootData.setCrossLinkDataInputFormat( lorikeetCrossLinkData );
        		
        	} else if ( psmDTO.getType() == XLinkUtils.TYPE_LOOPLINK ) {
        		
        		LorikeetLoopLinkData lorikeetLoopLinkData = getLoopLinkData( psmDTO );
        		
        		lorikeetRootData.setLoopLinkDataInputFormat( lorikeetLoopLinkData );
        	

        	} else if ( psmDTO.getType() == XLinkUtils.TYPE_UNLINKED ) {  //  Monolink is also unlinked on the psm
        		
        		LorikeetPerPeptideData lorikeetPerPeptideData = getUnlinkData( psmDTO );
        		
        		lorikeetRootData.setSequence( lorikeetPerPeptideData.getSequence() );
        		lorikeetRootData.setVariableMods( lorikeetPerPeptideData.getVariableMods() );
        		

        	} else if ( psmDTO.getType() == XLinkUtils.TYPE_DIMER ) {  
        		

        		LorikeetDimerData dimerDataInputFormat = getDimerData( psmDTO );
        		
        		lorikeetRootData.setDimerDataInputFormat( dimerDataInputFormat );
        		
        	} else {
        		
        		String msg = "psmDTO.getType() is other than Unlinked, Cross Link or Loop Link.  psmDTO.getType(): " + psmDTO.getType();
        		
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
	 * @param psmDTO
	 * @return
	 * @throws Exception
	 */
	private LorikeetCrossLinkData getCrossLinkData( PsmDTO psmDTO ) throws Exception {
		
		LorikeetCrossLinkData lorikeetCrossLinkData = new LorikeetCrossLinkData();
		
		int psmId = psmDTO.getId();
		
		CrosslinkDTO crosslinkDTO = CrosslinkDAO.getInstance().getARandomCrosslinkDTOForPsmId( psmId );
		
		
		lorikeetCrossLinkData.setCrossLinkPos1( crosslinkDTO.getPeptide1Position() );
		lorikeetCrossLinkData.setCrossLinkPos2( crosslinkDTO.getPeptide2Position() );

		lorikeetCrossLinkData.setLinkerMass(  crosslinkDTO.getLinkerMass() );
		
		LorikeetPerPeptideData peptideData1 = getLorikeetPerPeptideData( crosslinkDTO.getPeptide1MatchedPeptideId(), psmId );
		lorikeetCrossLinkData.setPeptideData1( peptideData1 );

		LorikeetPerPeptideData peptideData2 = getLorikeetPerPeptideData( crosslinkDTO.getPeptide2MatchedPeptideId(), psmId );
		lorikeetCrossLinkData.setPeptideData2( peptideData2 );

		return lorikeetCrossLinkData;
	}
	
	
	
	/**
	 * @param psmDTO
	 * @return
	 * @throws Exception
	 */
	private LorikeetDimerData getDimerData( PsmDTO psmDTO ) throws Exception {
		
		LorikeetDimerData lorikeetDimerData = new LorikeetDimerData();
		
		int psmId = psmDTO.getId();
		
//		DimerDTO dimerDTO = DimerDAO.getInstance().getDimerDTOByPsmId( psmId );
		
		List<MatchedPeptideDTO> matchedPeptideDTOList = MatchedPeptideDAO.getInstance().getMatchedPeptideDTOForPsmId( psmId );
		
		if ( matchedPeptideDTOList.size() != 2 ) {
			
			String msg = "Dimer requires 2 entries in MatchedPeptide table, number of records found: " + matchedPeptideDTOList.size()
					+ ", psmId = " + psmId;
			
			log.error( msg );
			
			throw new Exception( msg );
		}
		
		MatchedPeptideDTO matchedPeptideDTO_1 = matchedPeptideDTOList.get( 0 );
		MatchedPeptideDTO matchedPeptideDTO_2 = matchedPeptideDTOList.get( 1 );
		
		int matchedPeptideId_1 = matchedPeptideDTO_1.getId();
		int matchedPeptideId_2 = matchedPeptideDTO_2.getId();
		
		
		
		LorikeetPerPeptideData peptideData1 = getLorikeetPerPeptideData( matchedPeptideId_1, psmId );
		lorikeetDimerData.setPeptideData1( peptideData1 );

		LorikeetPerPeptideData peptideData2 = getLorikeetPerPeptideData( matchedPeptideId_2, psmId );
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
		
		int psmId = psmDTO.getId();
		
		LooplinkDTO looplinkDTO = LooplinkDAO.getInstance().getARandomLooplinkDTOForPsmId( psmId );
		
		
		lorikeetLoopLinkData.setLoopLinkPos1( looplinkDTO.getPeptidePosition1() );
		lorikeetLoopLinkData.setLoopLinkPos2( looplinkDTO.getPeptidePosition2() );

		lorikeetLoopLinkData.setLinkerMass(  looplinkDTO.getLinkerMass() );
		
		List<MatchedPeptideDTO> matchedPeptideDTOList = MatchedPeptideDAO.getInstance().getMatchedPeptideDTOForPsmId( psmId );
		
		if ( matchedPeptideDTOList.isEmpty() ) {
			String msg = "getLoopLinkData(...): Unable to find matched peptide record for psmId: " + psmId;
			log.error( msg );
			throw new Exception(msg);
		}
		
		if ( matchedPeptideDTOList.size() > 1 ) {
			String msg = "getLoopLinkData(...): Found more than one matched peptide record for Loop Link using psmId: " + psmId;
			log.error( msg );
			throw new Exception(msg);
		}

		MatchedPeptideDTO matchedPeptideDTO = matchedPeptideDTOList.get( 0 );
		
		LorikeetPerPeptideData peptideData = getLorikeetPerPeptideData( matchedPeptideDTO.getId(), psmId );
		lorikeetLoopLinkData.setPeptideData( peptideData );

		return lorikeetLoopLinkData;
	}
	

	/**
	 * @param psmDTO
	 * @return
	 * @throws Exception
	 */
	private LorikeetPerPeptideData getUnlinkData( PsmDTO psmDTO ) throws Exception {
		
		int psmId = psmDTO.getId();
		
		List<MatchedPeptideDTO> matchedPeptideDTOList = MatchedPeptideDAO.getInstance().getMatchedPeptideDTOForPsmId( psmId );
		
		if ( matchedPeptideDTOList.isEmpty() ) {
			String msg = "getUnlinkData(...): Unable to find matched peptide record for psmId: " + psmId;
			log.error( msg );
			throw new Exception(msg);
		}
		
		if ( matchedPeptideDTOList.size() > 1 ) {
			String msg = "getUnlinkData(...): Found more than one matched peptide record for Unlinked Link using psmId: " + psmId;
			log.error( msg );
			throw new Exception(msg);
		}

		MatchedPeptideDTO matchedPeptideDTO = matchedPeptideDTOList.get( 0 );
		
		LorikeetPerPeptideData peptideData = getLorikeetPerPeptideData( matchedPeptideDTO.getId(), psmId );

		return peptideData;
	}
	
	
	
	
	/**
	 * @param matchedPeptideId
	 * @return
	 * @throws Exception
	 */
	private LorikeetPerPeptideData getLorikeetPerPeptideData( int matchedPeptideId, int psmId ) throws Exception {
		
		
		MatchedPeptideDTO matchedPeptideDTO = MatchedPeptideDAO.getInstance().getMatchedPeptideDTOForId( matchedPeptideId );

		if ( matchedPeptideDTO == null ) {
			
			String msg = "Unable to find matchedPeptideDTO for matchedPeptideId: " + matchedPeptideId + ", psmId: " + psmId;
			log.error( msg );
			throw new Exception(msg);
		}
		
		LorikeetPerPeptideData lorikeetPerPeptideData = getLorikeetPerPeptideData( matchedPeptideDTO );
		
		return lorikeetPerPeptideData;
	}
	
	
	
	
	
	/**
	 * @param matchedPeptideDTO
	 * @return
	 * @throws Exception
	 */
	private LorikeetPerPeptideData getLorikeetPerPeptideData( MatchedPeptideDTO matchedPeptideDTO ) throws Exception {
		
		LorikeetPerPeptideData lorikeetPerPeptideData = new LorikeetPerPeptideData();

		int peptideId = matchedPeptideDTO.getPeptide_id();

		List<DynamicModDTO> dynamicModResults = null;

		dynamicModResults = 
				DynamicModDAO.getInstance().getDynamicModDTOForMatchedPeptideId( matchedPeptideDTO.getId() );

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

        List<LorikeetVariableMod> variableMods = new ArrayList<>( dynamicModResults.size() );
        
        for ( DynamicModDTO dynamicModDTO : dynamicModResults ) {
        	
        	int dynamicModPosition = dynamicModDTO.getPosition();
        	
        	String aminoAcid = sequence.substring( dynamicModPosition - 1 /* chg to zero based */, dynamicModPosition );

        	
        	LorikeetVariableMod lorikeetVariableMod = new LorikeetVariableMod();
        	
        	lorikeetVariableMod.setIndex( dynamicModDTO.getPosition() );
        	lorikeetVariableMod.setModMass( dynamicModDTO.getMass() );
        	lorikeetVariableMod.setAminoAcid( aminoAcid );
        	
        	variableMods.add( lorikeetVariableMod );
        }
        

		lorikeetPerPeptideData.setSequence( sequence );
		lorikeetPerPeptideData.setVariableMods( variableMods );
		
		return lorikeetPerPeptideData;
	}

}
