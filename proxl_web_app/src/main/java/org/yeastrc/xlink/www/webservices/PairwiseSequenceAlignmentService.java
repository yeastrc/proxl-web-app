package org.yeastrc.xlink.www.webservices;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.biojava.nbio.alignment.Alignments;
import org.biojava.nbio.alignment.Alignments.PairwiseSequenceAlignerType;
import org.biojava.nbio.alignment.SimpleGapPenalty;
import org.biojava.nbio.core.alignment.matrices.SubstitutionMatrixHelper;
import org.biojava.nbio.core.alignment.template.SequencePair;
import org.biojava.nbio.core.alignment.template.SubstitutionMatrix;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.compound.AminoAcidCompound;
import org.biojava.nbio.structure.Structure;
import org.biojava.nbio.structure.io.PDBFileReader;
import org.yeastrc.xlink.www.dao.PDBAlignmentDAO;
import org.yeastrc.xlink.www.dao.PDBFileDAO;
import org.yeastrc.xlink.www.dao.ProteinSequenceDAO;
import org.yeastrc.xlink.www.dao.ProteinSequenceVersionDAO;
import org.yeastrc.xlink.www.dto.PDBAlignmentDTO;
import org.yeastrc.xlink.www.dto.PDBFileDTO;
import org.yeastrc.xlink.www.dto.ProteinSequenceDTO;
import org.yeastrc.xlink.www.dto.ProteinSequenceVersionDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.constants.PDBFileConstants;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.searcher.PDBAlignmentSearcher;
import org.yeastrc.xlink.www.user_account.UserSessionObject;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
import org.yeastrc.xlink.www.user_web_utils.GetAuthAccessLevelForWebRequest;

@Path("/psa")
public class PairwiseSequenceAlignmentService {

	private static final Logger log = LoggerFactory.getLogger( PairwiseSequenceAlignmentService.class);
	
	/**
	 * @param chain
	 * @param pdbFileId
	 * @param proteinId
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/alignSequences")
	public PDBAlignmentDTO alignSequences ( 
			@QueryParam("chain") String chain,
			@QueryParam("pdbFileId") int pdbFileId,
			@QueryParam("proteinId") int proteinId,
			@Context HttpServletRequest request ) throws Exception {
		try {
			// Get the session first.  
//			HttpSession session = request.getSession();
			PDBFileDTO pdbFile = PDBFileDAO.getInstance().getPDBFile( pdbFileId );
			if ( pdbFile == null ) {
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			int projectId = pdbFile.getProjectId();
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
			if ( PDBFileConstants.VISIBILITY_PUBLIC.equals( pdbFile.getVisibility() ) ) {
			} else if ( PDBFileConstants.VISIBILITY_PROJECT.equals( pdbFile.getVisibility() ) ) {
				// pdb file restricted to this project
				//  Test access to the project id, admin users are also allowed
				AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getAuthAccessLevel();
				if ( ! authAccessLevel.isPublicAccessCodeReadAllowed() ) {
					//  No Access Allowed for this project id
					throw new WebApplicationException(
							Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
							.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
							.build()
							);
				}
			} else {
				String msg = "Unknown value for visibility: " + pdbFile.getVisibility();
				log.error( msg );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			// get sequence for PDB chain
			PDBFileReader pdbReader = new PDBFileReader();
			Structure structure = pdbReader.getStructure( new ByteArrayInputStream( pdbFile.getContent().getBytes() ) );
			
			if( structure.getChains() != null && structure.getChains().size() == 1 ) {
				if( structure.getChains().get( 0 ).getChainID().equals( " " ) )
					structure.getChains().get( 0 ).setChainID( "_" );
			}
			
			String pdbSequence = structure.getChainByPDB( chain ).getAtomSequence();
			// get sequence for protein protein
			ProteinSequenceVersionDTO proteinSequenceVersionDTO = ProteinSequenceVersionDAO.getInstance().getFromId( proteinId );
			if ( proteinSequenceVersionDTO == null ) {
				String msg = "No proteinSequenceVersionDTO found for proteinId: " + proteinId;
				log.error( msg );
				throw new ProxlWebappDataException(msg);
			}
			String proteinSequence = null;
			ProteinSequenceDTO proteinSequenceDTO = 
					ProteinSequenceDAO.getInstance().getProteinSequenceDTOFromDatabase( proteinSequenceVersionDTO.getproteinSequenceId() );
			if ( proteinSequenceDTO != null ) {
				proteinSequence = proteinSequenceDTO.getSequence();
			}
			// do alignment
			ProteinSequence pdbPS = new ProteinSequence( pdbSequence );
			ProteinSequence experimentalProteinPS = new ProteinSequence( proteinSequence );
			PDBAlignmentDTO pa = new PDBAlignmentDTO();
			double ratio = (double)pdbSequence.length() / (double)proteinSequence.length();
			if( ratio < 0.5 ) {
				// pdb sequence is less than half as long as the experimental protein sequence
				// set experimental protein sequence as target and pdb sequence as query
				// use Needleman-Wunsch (global) alignment
				SubstitutionMatrix<AminoAcidCompound> matrix = SubstitutionMatrixHelper.getBlosum62();
				SequencePair<ProteinSequence, AminoAcidCompound> pair = Alignments.getPairwiseAlignment(pdbPS, experimentalProteinPS,
		                PairwiseSequenceAlignerType.GLOBAL, new SimpleGapPenalty(), matrix);
				pa.setPdbFileId( pdbFileId );
				pa.setChainId( chain );
				pa.setProteinSequenceVersionId( proteinId );
				pa.setAlignedPDBSequence( pair.getQuery().toString() );
				pa.setAlignedExperimentalSequence( pair.getTarget().toString() );
			} else if( ratio > 2.0 ) {
				// pdb sequence is more than twice as long as the experimental protein sequence
				// set pdb sequence as target, experimental protein sequence as query
				// use Needleman-Wunsch (global) alignment
				SubstitutionMatrix<AminoAcidCompound> matrix = SubstitutionMatrixHelper.getBlosum62();
				SequencePair<ProteinSequence, AminoAcidCompound> pair = Alignments.getPairwiseAlignment(experimentalProteinPS, pdbPS,
		                PairwiseSequenceAlignerType.GLOBAL, new SimpleGapPenalty(), matrix);
				pa.setPdbFileId( pdbFileId );
				pa.setChainId( chain );
				pa.setProteinSequenceVersionId( proteinId );
				pa.setAlignedExperimentalSequence( pair.getQuery().toString() );
				pa.setAlignedPDBSequence( pair.getTarget().toString() );
			} else {
				// neither sequence is more than twice as long as the other
				// use Needleman-Wunsch (global) alignment
				// use the longer sequence as the target, shorter as the query
				if( ratio > 1 ) {
					// use pdb as target
					SubstitutionMatrix<AminoAcidCompound> matrix = SubstitutionMatrixHelper.getBlosum85();
					SequencePair<ProteinSequence, AminoAcidCompound> pair = Alignments.getPairwiseAlignment(experimentalProteinPS, pdbPS,
			                PairwiseSequenceAlignerType.GLOBAL, new SimpleGapPenalty(), matrix);
					pa.setPdbFileId( pdbFileId );
					pa.setChainId( chain );
					pa.setProteinSequenceVersionId( proteinId );
					pa.setAlignedExperimentalSequence( pair.getQuery().toString() );
					pa.setAlignedPDBSequence( pair.getTarget().toString() );
				} else {
					// use experimental protein sequence as target
					SubstitutionMatrix<AminoAcidCompound> matrix = SubstitutionMatrixHelper.getBlosum85();
					SequencePair<ProteinSequence, AminoAcidCompound> pair = Alignments.getPairwiseAlignment(pdbPS, experimentalProteinPS,
			                PairwiseSequenceAlignerType.GLOBAL, new SimpleGapPenalty(), matrix);
					pa.setPdbFileId( pdbFileId );
					pa.setChainId( chain );
					pa.setProteinSequenceVersionId( proteinId );
					pa.setAlignedPDBSequence( pair.getQuery().toString() );
					pa.setAlignedExperimentalSequence( pair.getTarget().toString() );
				}
			}			
			return pa;
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
	 * @param id
	 * @param pdbFileId
	 * @param chainId
	 * @param alignedPDBSequence
	 * @param proteinSequenceVersionId
	 * @param alignedExperimentalSequence
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/saveAlignment")
	public PDBAlignmentDTO saveAlignment( 
			@FormParam("id") int id, 
			@FormParam("pdbFileId") int pdbFileId,
			@FormParam("chainId") String chainId,
			@FormParam("alignedPDBSequence") String alignedPDBSequence,
			@FormParam("proteinSequenceVersionId") int proteinSequenceVersionId,
			@FormParam("alignedExperimentalSequence") String alignedExperimentalSequence,
			@Context HttpServletRequest request ) throws Exception {
		try {
			// Get the session first.  
			HttpSession session = request.getSession();
			UserSessionObject userSessionObject 
			= (UserSessionObject) session.getAttribute( WebConstants.SESSION_CONTEXT_USER_LOGGED_IN );
			if ( userSessionObject == null ) {
				//  No User session 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			PDBFileDTO pdbFile = PDBFileDAO.getInstance().getPDBFileNoContent( pdbFileId );
			if ( pdbFile == null ) {
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			// pdb file restricted to this project
			//  Test access to the project id, admin users are also allowed
//			AuthAccessLevel authAccessLevel = GetAuthAccessLevelForWebRequest.getInstance().getAuthAccessLevelForWebRequestProjectId( userSessionObject, pdbFile.getProjectId() );
//			if ( ! authAccessLevel.isWriteAllowed() ) {
//				//  No Access Allowed for this project id
//				throw new WebApplicationException(
//						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
//						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
//						.build()
//						);
//			}
			//  Restrict access to person who uploaded the PDB file, admin users are also allowed
			AuthAccessLevel authAccessLevel = GetAuthAccessLevelForWebRequest.getInstance().getAuthAccessLevelForWebRequest_NonProjectUsageOnly( userSessionObject );
			if ( ( ! authAccessLevel.isAdminAllowed() ) 
					&&  userSessionObject.getUserDBObject().getAuthUser().getId() != pdbFile.getUploadedBy() ) {
				//  No Access Allowed for this user
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			PDBAlignmentDTO pa = new PDBAlignmentDTO();
			pa.setId( id );
			pa.setAlignedExperimentalSequence( alignedExperimentalSequence );
			pa.setAlignedPDBSequence( alignedPDBSequence );
			pa.setChainId( chainId );
			pa.setProteinSequenceVersionId( proteinSequenceVersionId );
			pa.setPdbFileId( pdbFileId );
			PDBAlignmentDAO.getInstance().savePDBAlignment( pa );
			return pa;
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw e;
		}
	}
	
	/**
	 * @param pdbFileId
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getAlignmentsForPDBFile")
	public Map<String, List<PDBAlignmentDTO>> getAlignmentsForPDBFile ( 
			@QueryParam("pdbFileId") int pdbFileId,
			@Context HttpServletRequest request ) throws Exception {
		try {
			// Get the session first.  
			HttpSession session = request.getSession();
			UserSessionObject userSessionObject 
			= (UserSessionObject) session.getAttribute( WebConstants.SESSION_CONTEXT_USER_LOGGED_IN );
			if ( userSessionObject == null ) {
				//  No User session 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			PDBFileDTO pdbFile = PDBFileDAO.getInstance().getPDBFileNoContent( pdbFileId );
			if ( pdbFile == null ) {
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			if ( PDBFileConstants.VISIBILITY_PUBLIC.equals( pdbFile.getVisibility() ) ) {
			} else if ( PDBFileConstants.VISIBILITY_PROJECT.equals( pdbFile.getVisibility() ) ) {
				// pdb file restricted to this project
				//  Test access to the project id, admin users are also allowed
				AuthAccessLevel authAccessLevel = GetAuthAccessLevelForWebRequest.getInstance().getAuthAccessLevelForWebRequestProjectId( userSessionObject, pdbFile.getProjectId() );
				if ( ! authAccessLevel.isPublicAccessCodeReadAllowed() ) {
					//  No Access Allowed for this project id
					throw new WebApplicationException(
							Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
							.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
							.build()
							);
				}
			} else {
				String msg = "Unknown value for visibility: " + pdbFile.getVisibility();
				log.error( msg );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			return PDBAlignmentSearcher.getInstance().getAlignmentsForPDBFile( pdbFileId );
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
	 * @param alignmentId
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/deleteAlignment")
	public String deleteAlignment ( 
			@FormParam("alignmentId") int alignmentId,
			@Context HttpServletRequest request ) throws Exception {
		try {
			// Get the session first.  
			HttpSession session = request.getSession();
			UserSessionObject userSessionObject 
			= (UserSessionObject) session.getAttribute( WebConstants.SESSION_CONTEXT_USER_LOGGED_IN );
			if ( userSessionObject == null ) {
				//  No User session 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			PDBAlignmentDTO pdbAlignmentDTO = PDBAlignmentDAO.getInstance().getPDBAlignment( alignmentId );
			if ( pdbAlignmentDTO == null ) {
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			PDBFileDTO pdbFile = PDBFileDAO.getInstance().getPDBFileNoContent( pdbAlignmentDTO.getPdbFileId() );
			if ( pdbFile == null ) {
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			// pdb file restricted to this project
			//  Test access to the project id, admin users are also allowed
//			AuthAccessLevel authAccessLevel = GetAuthAccessLevelForWebRequest.getInstance().getAuthAccessLevelForWebRequestProjectId( userSessionObject, pdbFile.getProjectId() );
//			if ( ! authAccessLevel.isWriteAllowed() ) {
//				//  No Access Allowed for this project id
//				throw new WebApplicationException(
//						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
//						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
//						.build()
//						);
//			}
			//  Restrict access to person who uploaded the PDB file, admin users are also allowed
			AuthAccessLevel authAccessLevel = GetAuthAccessLevelForWebRequest.getInstance().getAuthAccessLevelForWebRequest_NonProjectUsageOnly( userSessionObject );
			if ( ( ! authAccessLevel.isAdminAllowed() ) 
					&&  userSessionObject.getUserDBObject().getAuthUser().getId() != pdbFile.getUploadedBy() ) {
				//  No Access Allowed for this user
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			PDBAlignmentDAO.getInstance().deletePDBAlignment( alignmentId );
			return "";
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
}
