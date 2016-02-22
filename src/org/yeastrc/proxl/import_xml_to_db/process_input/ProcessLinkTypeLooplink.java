package org.yeastrc.proxl.import_xml_to_db.process_input;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dao.PsmPeptideDAO;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.proxl.import_xml_to_db.objects.PerPeptideData;
import org.yeastrc.proxl.import_xml_to_db.objects.SearchProgramEntry;
import org.yeastrc.proxl_import.api.xml_dto.LinkedPosition;
import org.yeastrc.proxl_import.api.xml_dto.LinkedPositions;
import org.yeastrc.proxl_import.api.xml_dto.Linker;
import org.yeastrc.proxl_import.api.xml_dto.Peptide;
import org.yeastrc.proxl_import.api.xml_dto.Peptides;
import org.yeastrc.proxl_import.api.xml_dto.Psm;
import org.yeastrc.proxl_import.api.xml_dto.Psms;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptide;
import org.yeastrc.xlink.dao.LooplinkDAO;
import org.yeastrc.xlink.dto.LooplinkDTO;
import org.yeastrc.xlink.dto.NRProteinDTO;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.dto.PsmDTO;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;
import org.yeastrc.xlink.linkable_positions.GetLinkerFactory;
import org.yeastrc.xlink.linkable_positions.linkers.ILinker;
import org.yeastrc.xlink.utils.IsDynamicModMassAMonolink;





/**
 * 
 *
 */
public class ProcessLinkTypeLooplink {



	private static final Logger log = Logger.getLogger(ProcessLinkTypeLooplink.class);

	//  private constructor
	private ProcessLinkTypeLooplink() { }
	
	public static ProcessLinkTypeLooplink getInstance() { return new ProcessLinkTypeLooplink(); }
	
	

	/**
	 * @param reportedPeptide
	 * @param proxlInputLinkerList
	 * @param proteinNameDecoyPrefix
	 * @param nrseqDatabaseId
	 * @param linkTypeNumber
	 * @param reportedPeptideDTO
	 * @param searchId
	 * @param searchProgramEntryMap
	 * @throws Exception
	 */
	public void processLooplink( 
			
			ReportedPeptide reportedPeptide, 
			
			List<Linker> proxlInputLinkerList,

			List<String> proteinNameDecoyPrefixList,
			
			int nrseqDatabaseId,
			
			int linkTypeNumber, 
			ReportedPeptideDTO reportedPeptideDTO, 
			int searchId, 
			
			Map<String, SearchProgramEntry> searchProgramEntryMap,
			
			Map<String, Map<Integer,Integer>> mapOfScanFilenamesMapsOfScanNumbersToScanIds
			
			) throws Exception {
		
		Peptides peptides =
				reportedPeptide.getPeptides();

		if ( peptides == null ) {
			String msg = "There must be 1 peptide for Looplink reported peptide: " + reportedPeptide.getReportedPeptideString();
			log.error( msg );
			throw new ProxlImporterDataException(msg);
		}
		
		List<Peptide> peptideList = peptides.getPeptide();

		if ( peptideList == null || peptideList.size() != 1 ) {
			String msg = "There must be 1 peptide for Looplink for reported peptide: " + reportedPeptide.getReportedPeptideString();
			log.error( msg );
			throw new ProxlImporterDataException(msg);
		}
		
		
		Peptide peptide = peptideList.get( 0 );
		
		PerPeptideData perPeptideData = GetPerPeptideData.getInstance().getPerPeptideData( peptide, nrseqDatabaseId );

		int[] linkedPositions = getLooplinkLinkedPositions( peptide, "1", reportedPeptide );

		int linkedPosition_1 = linkedPositions[ 0 ];
		int linkedPosition_2 = linkedPositions[ 1 ];

		
		List<ILinker> linkerList = new ArrayList<>();
		
		String linkerListStringForErrorMsgs = null;
		
		
		for ( Linker proxlInputLinker : proxlInputLinkerList ) {

			String proxlInputLinkerName = proxlInputLinker.getName();

			ILinker linker = GetLinkerFactory.getLinkerForAbbr( proxlInputLinkerName );
			if( linker == null ) {
				String msg = "processLooplink(...): Could not get an ILinker for linker abbreviation: " 
						+ proxlInputLinkerName;
				log.error( msg );

				throw new Exception( msg );
			}

			linkerList.add( linker );
			
			if ( linkerListStringForErrorMsgs == null ) {
				
				linkerListStringForErrorMsgs = proxlInputLinkerName;
			} else {
				
				linkerListStringForErrorMsgs += ", " + proxlInputLinkerName;
			}
		}
		
		GetLooplinksResult getLooplinksResult =
				
				getLooplinks( 
						nrseqDatabaseId, 
						linkerList, 
						linkerListStringForErrorMsgs,
						proteinNameDecoyPrefixList, 
						perPeptideData, 
						linkedPosition_1, 
						linkedPosition_2 );




		List<GetLooplinksResultItem> getLooplinksResultItemList = getLooplinksResult.getLooplinkResultItemList();
		
		if ( getLooplinksResultItemList == null || getLooplinksResultItemList.isEmpty() ) {
		
			log.warn( "No Mapped Proteins for this reported peptide so not inserting any PSMs: " + 
					reportedPeptide.getReportedPeptideString() );

			return;  //  EARLY EXIT   No looplink records for this reported peptide in this search 
		}
		

		Psms psms =	reportedPeptide.getPsms();

		List<Psm> psmList = psms.getPsm();

		for ( Psm psm : psmList ) {

			PsmDTO psmDTO = 
					PopulateAndSavePsmDTO.getInstance().populateAndSavePSMDTO( 
							
							searchId, 
							mapOfScanFilenamesMapsOfScanNumbersToScanIds, 
							linkTypeNumber, 
							reportedPeptideDTO, 
							psm );
			
			
			SavePsmAnnotations.getInstance().savePsmAnnotations( psm, psmDTO, searchProgramEntryMap );

			savePSMChildrenAndLooplinkDTORecords( 
					psm, 
					psmDTO, 						
					perPeptideData, 
					getLooplinksResult );
			

			
			//  Save PsmDTO.id PeptideDTO.id mapping
			
			PsmPeptideDAO.getInstance().saveToDatabase( psmDTO.getId(), perPeptideData.getPeptideDTO().getId() );
		}
	}
	
	
	

	/**
	 * @param psm
	 * @param psmDTO
	 * @param perPeptideData_1
	 * @param perPeptideData_2
	 * @param getLooplinksResult
	 * @throws Exception
	 */
	private void savePSMChildrenAndLooplinkDTORecords(
			
			Psm psm,
			PsmDTO psmDTO,
			PerPeptideData perPeptideData,
			GetLooplinksResult getLooplinksResult
			
			) throws Exception {
		
		//  Save PSM children
		
		SavePerPeptideDataForPSM.getInstance().savePerPeptideDataForPSM( psmDTO, perPeptideData );

		List<GetLooplinksResultItem> getLooplinksResultItemList = getLooplinksResult.getLooplinkResultItemList();
		
		for ( GetLooplinksResultItem getLooplinksResultItem : getLooplinksResultItemList ) {
			
			LooplinkDTO looplinkDTO = getLooplinksResultItem.getLooplinkDTO();

			looplinkDTO.setLinkerId( IsDynamicModMassAMonolink.getInstance().getLinkerDTO().getId() );
			
			BigDecimal linkerMass = psm.getLinkerMass();

			if ( linkerMass == null ) {
				
				String msg = "Linker Mass cannot be null or empty for Looplink. PSM Scan Number: " + psm.getScanNumber();
				log.error( msg );
				throw new ProxlImporterDataException(msg);
			}
			
			looplinkDTO.setLinkerMass( linkerMass );
			
			looplinkDTO.setPsm( psmDTO );
			
			LooplinkDAO.getInstance().save( looplinkDTO );
			
		}
		
	}
	
	


	/**
	 * @param nrseqDatabaseId
	 * @param linkerList
	 * @param linkerListStringForErrorMsgs
	 * @param proteinNameDecoyPrefix
	 * @param perPeptideData
	 * @param linkedPosition_1
	 * @param linkedPosition_2
	 * @return
	 * @throws Exception
	 */
	private GetLooplinksResult getLooplinks(
			
			int nrseqDatabaseId,
			
			List<ILinker> linkerList,
			String linkerListStringForErrorMsgs,
			
			List<String> proteinNameDecoyPrefixList,
			
			PerPeptideData perPeptideData,
			
			int linkedPosition_1,
			int linkedPosition_2


			) throws Exception {
		
		
		GetLooplinksResult getLooplinksResult = new GetLooplinksResult();
		
		List<GetLooplinksResultItem> looplinkResultItemList = new ArrayList<>();
		getLooplinksResult.setLooplinkResultItemList( looplinkResultItemList );
		
		
		
		PeptideDTO peptide = perPeptideData.getPeptideDTO();
		
		
		Collection<NRProteinDTO> proteinMatches_Peptide = 
				GetProteinsForPeptidesAndInsertNrseqPeptideProteinEntries.getInstance()
				.getProteinsForPeptidesAndInsertNrseqPeptideProteinEntries( peptide, proteinNameDecoyPrefixList, nrseqDatabaseId );
		
		
		PopulateMonolinkDTOListOnPerPeptideDataObject.getInstance()
		.populateMonolinkDTOListOnPerPeptideDataObject( perPeptideData, linkerList, proteinMatches_Peptide );

		
		
		
		Map<NRProteinDTO, Collection<List<Integer>>> proteinMap = 

				GetLinkableProteinsAndPositions.getInstance()
				.getLinkableProteinsAndPositionsForLooplink( 
						peptide, 
						linkedPosition_1, 
						linkedPosition_2 , 
						linkerList, 
						proteinMatches_Peptide );
		
			
		if( proteinMap.keySet().size() < 1 ) {
			String msg = "getLooplinks(...): No linkable protein positions found for " + peptide.getSequence() +
					" at positions " + linkedPosition_1 + "," + linkedPosition_2 + " for " +
					IsDynamicModMassAMonolink.getInstance().getLinkerDTO().getAbbr() + " linker.";
			log.error( msg );
			
			throw new Exception( msg );
		}
		
		
		
		for( NRProteinDTO protein : proteinMap.keySet() ) {

			Collection<List<Integer>> proteinPositions = proteinMap.get( protein );
			
			for( List<Integer> matches : proteinPositions ) {

				// a single looplink entry
				LooplinkDTO looplink = new LooplinkDTO();

				looplink.setLinkerId( IsDynamicModMassAMonolink.getInstance().getLinkerDTO().getId() );

				looplink.setPeptideId( peptide.getId() );
				
				looplink.setPeptidePosition1( linkedPosition_1 );
				looplink.setPeptidePosition2( linkedPosition_2 );
				
				looplink.setProtein( protein );
				looplink.setProteinPosition1( matches.get( 0 ) );
				looplink.setProteinPosition2( matches.get( 1 ) );


				GetLooplinksResultItem getLooplinksResultItem = new GetLooplinksResultItem(); 

				getLooplinksResultItem.setLooplinkDTO( looplink );

				
				looplinkResultItemList.add( getLooplinksResultItem );


			}  //end looping over proteinpositions
		
		}  //end looping over proteins
		
		
		return getLooplinksResult;
	
	}




	/**
	 * @param peptide
	 * @param peptideNumber
	 * @return
	 * @throws ProxlImporterDataException 
	 */
	private int[] getLooplinkLinkedPositions( Peptide peptide, String peptideNumber, ReportedPeptide reportedPeptide ) throws ProxlImporterDataException {
		
		LinkedPositions linkedPositions = peptide.getLinkedPositions();

		if ( linkedPositions == null ) {
			
			String msg = "Looplink:  There must be exactly 2 linked positions for peptide " + peptideNumber 
					+ " for peptide sequence '" + peptide.getSequence() + "'"
					+ " for Looplink reported peptide: " + reportedPeptide.getReportedPeptideString();
			log.error( msg );
			throw new ProxlImporterDataException(msg);
		}
		
		List<LinkedPosition> LinkedPositionList = linkedPositions.getLinkedPosition();
		if ( LinkedPositionList == null || LinkedPositionList.size() != 2 ) {
			
			String msg = "Looplink:  There must be exactly 2 linked positions for peptide " + peptideNumber 
					+ " for peptide sequence '" + peptide.getSequence() + "'"
					+ " for Looplink reported peptide: " + reportedPeptide.getReportedPeptideString();
			log.error( msg );
			throw new ProxlImporterDataException(msg);
		}
		
		int linkPosition_1 = LinkedPositionList.get( 0 ).getPosition().intValue();
		int linkPosition_2 = LinkedPositionList.get( 1 ).getPosition().intValue();
		
		int[] linkPositions = { linkPosition_1, linkPosition_2 };
		
		return linkPositions;
	}
	
	


	/**
	 * Internal result from getLooplinks method
	 *
	 */
	private class GetLooplinksResult {
		

		private List<GetLooplinksResultItem> looplinkResultItemList;

		public List<GetLooplinksResultItem> getLooplinkResultItemList() {
			return looplinkResultItemList;
		}

		public void setLooplinkResultItemList(
				List<GetLooplinksResultItem> looplinkResultItemList) {
			this.looplinkResultItemList = looplinkResultItemList;
		}

	}
	
	
	
	/**
	 * Single entry in Internal result from getLooplinks method
	 *
	 */
	private class GetLooplinksResultItem {
		

		private LooplinkDTO looplinkDTO;
		
				
		public LooplinkDTO getLooplinkDTO() {
			return looplinkDTO;
		}

		public void setLooplinkDTO(LooplinkDTO looplinkDTO) {
			this.looplinkDTO = looplinkDTO;
		}

	}
}