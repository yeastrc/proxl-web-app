package org.yeastrc.proxl.import_xml_to_db.process_input;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dao.PsmPeptideDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_UnlinkedDAO;
import org.yeastrc.proxl.import_xml_to_db.drop_peptides_psms_for_cmd_line_cutoffs.DropPeptideAndOrPSMForCmdLineCutoffs;
import org.yeastrc.proxl.import_xml_to_db.drop_peptides_psms_for_cmd_line_cutoffs.DropPeptidePSMCutoffValues;
import org.yeastrc.proxl.import_xml_to_db.drop_peptides_psms_for_cmd_line_cutoffs.DroppedPeptideCount;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterInteralException;
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
import org.yeastrc.xlink.dto.UnlinkedDTO;
import org.yeastrc.xlink.dto.NRProteinDTO;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.dto.PsmDTO;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;
import org.yeastrc.xlink.linkable_positions.GetLinkerFactory;
import org.yeastrc.xlink.linkable_positions.linkers.ILinker;





/**
 * 
 * Proxl internal "Unlinked" is a single peptide that is not a unlinked
 */
public class ProcessLinkTypeUnlinkedAsDefinedByProxl {



	private static final Logger log = Logger.getLogger(ProcessLinkTypeUnlinkedAsDefinedByProxl.class);

	//  private constructor
	private ProcessLinkTypeUnlinkedAsDefinedByProxl() { }
	
	public static ProcessLinkTypeUnlinkedAsDefinedByProxl getInstance() { return new ProcessLinkTypeUnlinkedAsDefinedByProxl(); }
	
	

	/**
	 * Proxl internal "Unlinked" is a single peptide that is not a unlinked
	 * 
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
	public void processUnlinked( 
			
			ReportedPeptide reportedPeptide, 
			
			List<Linker> proxlInputLinkerList,

			List<String> proteinNameDecoyPrefixList,
			
			int nrseqDatabaseId,
			
			int linkTypeNumber, 
			ReportedPeptideDTO reportedPeptideDTO, 
			int searchId, 
			
			DropPeptidePSMCutoffValues dropPeptidePSMCutoffValues,
			
			Map<String, SearchProgramEntry> searchProgramEntryMap,
			
			Map<String, Map<Integer,Integer>> mapOfScanFilenamesMapsOfScanNumbersToScanIds
			
			) throws Exception {
		
		Peptides peptides =
				reportedPeptide.getPeptides();

		//  Already validated so just throw illegal argument exception here
		
		if ( peptides == null ) {
			String msg = "'peptides' parameter cannot be null.  reported peptide: " + reportedPeptide.getReportedPeptideString();
			log.error( msg );
			throw new IllegalArgumentException(msg);
		}
		
		List<Peptide> peptideList = peptides.getPeptide();

		if ( peptideList == null || ( peptideList.size() != 1 ) ) {
			String msg = "'peptideList' parameter cannot be null and must have one element.  reported peptide: " + reportedPeptide.getReportedPeptideString();
			log.error( msg );
			throw new IllegalArgumentException(msg);
		}
		
		Peptide peptide = peptideList.get( 0 );

		PerPeptideData perPeptideData = GetPerPeptideData.getInstance().getPerPeptideData( peptide, nrseqDatabaseId );

		LinkedPositions linkedPositions = peptide.getLinkedPositions();

		if ( linkedPositions != null ) {

			List<LinkedPosition> LinkedPositionList = linkedPositions.getLinkedPosition();
			if ( LinkedPositionList != null && ( ! LinkedPositionList.isEmpty() ) ) {

				String msg = "Unlinked:  There must be NO linked positions " 
						+ " for peptide sequence '" + peptide.getSequence() + "'"
						+ " for Unlinked reported peptide: " + reportedPeptide.getReportedPeptideString();
				log.error( msg );
				throw new ProxlImporterDataException(msg);
			}
		}

		
		List<ILinker> linkerList = new ArrayList<>();
		
		String linkerListStringForErrorMsgs = null;
		
		
		for ( Linker proxlInputLinker : proxlInputLinkerList ) {

			String proxlInputLinkerName = proxlInputLinker.getName();

			ILinker linker = GetLinkerFactory.getLinkerForAbbr( proxlInputLinkerName );
			if( linker == null ) {
				String msg = "processUnlinked(...): Could not get an ILinker for linker abbreviation: " 
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
		
		GetUnlinkedResult getUnlinkedResult =
				
				getUnlinked( 
						nrseqDatabaseId, 
						linkerList,
						linkerListStringForErrorMsgs,
						proteinNameDecoyPrefixList, 
						perPeptideData );




		List<GetUnlinkedResultItem> getUnlinkedResultItemList = getUnlinkedResult.getUnlinkedResultItemList();
		
		if ( getUnlinkedResultItemList == null || getUnlinkedResultItemList.isEmpty() ) {
			
			log.warn( "No Mapped Proteins for this reported peptide so not inserting any PSMs: " + 
					reportedPeptide.getReportedPeptideString() );
			
			return;  //  EARLY EXIT   No unlinked records for this reported peptide in this search 
		}
		
		
		Psms psms =	reportedPeptide.getPsms();

		List<Psm> psmList = psms.getPsm();

		boolean saveAnyPSMs = false;
		
		for ( Psm psm : psmList ) {
			
			if ( DropPeptideAndOrPSMForCmdLineCutoffs.getInstance()
					.dropPSMForCmdLineCutoffs( psm, dropPeptidePSMCutoffValues ) ) {
				
				DroppedPeptideCount.incrementDroppedPsmCount();

				continue;  // EARLY continue to next record
			}
			
			PsmDTO psmDTO = 
					PopulateAndSavePsmDTO.getInstance().populateAndSavePSMDTO( 
							
							searchId, 
							mapOfScanFilenamesMapsOfScanNumbersToScanIds, 
							linkTypeNumber, 
							reportedPeptideDTO, 
							psm );
			
			SavePsmAnnotations.getInstance().savePsmAnnotations( psm, psmDTO, searchProgramEntryMap );

			savePSMChildrenAndUnlinkedDTORecords( 
					psm, 
					psmDTO, 						
					perPeptideData, 
					getUnlinkedResult );
			
			
			//  Save PsmDTO.id PeptideDTO.id mapping
			
			PsmPeptideDAO.getInstance().saveToDatabase( psmDTO.getId(), perPeptideData.getPeptideDTO().getId() );

			saveAnyPSMs = true;
		}
		
		if ( ! saveAnyPSMs ) {
			
			String msg = "No PSMs saved for this reported peptide: " + 
					reportedPeptide.getReportedPeptideString();
			log.error( msg );
			throw new ProxlImporterInteralException(msg);
		}
	}
	
	
	

	/**
	 * @param psm
	 * @param psmDTO
	 * @param perPeptideData_1
	 * @param perPeptideData_2
	 * @param getUnlinkedResult
	 * @throws Exception
	 */
	private void savePSMChildrenAndUnlinkedDTORecords(
			
			Psm psm,
			PsmDTO psmDTO,
			PerPeptideData perPeptideData,
			GetUnlinkedResult getUnlinkedResult
			
			) throws Exception {
		
		//  Save PSM children
		
		SavePerPeptideDataForPSM.getInstance().savePerPeptideDataForPSM( psmDTO, perPeptideData );

//		MatchedPeptideDTO matchedPeptideDTO = perPeptideData.getMatchedPeptideDTO();

		List<GetUnlinkedResultItem> getUnlinkedResultItemList = getUnlinkedResult.getUnlinkedResultItemList();
		
		for ( GetUnlinkedResultItem getUnlinkedResultItem : getUnlinkedResultItemList ) {
			
			UnlinkedDTO unlinkedDTO = getUnlinkedResultItem.getUnlinkedDTO();
			
			unlinkedDTO.setPsm( psmDTO );
			
			DB_Insert_UnlinkedDAO.getInstance().save( unlinkedDTO );
		}
	}
	
	

	/**
	 * @param nrseqDatabaseId
	 * @param linkerList
	 * @param linkerListStringForErrorMsgs
	 * @param proteinNameDecoyPrefixList
	 * @param perPeptideData
	 * @return
	 * @throws Exception
	 */
	private GetUnlinkedResult getUnlinked(
			
			int nrseqDatabaseId,
			
			List<ILinker> linkerList, //  Used for Monolinks
			String linkerListStringForErrorMsgs,
			
			List<String> proteinNameDecoyPrefixList,
			
			PerPeptideData perPeptideData
			
			) throws Exception {
		
		
		GetUnlinkedResult getUnlinkedResult = new GetUnlinkedResult();
		
		List<GetUnlinkedResultItem> unlinkedResultItemList = new ArrayList<>();
		getUnlinkedResult.setUnlinkedResultItemList( unlinkedResultItemList );
		
		
		
		PeptideDTO peptide = perPeptideData.getPeptideDTO();
		
		
		Collection<NRProteinDTO> proteinMatches_Peptide = 
				GetProteinsForPeptidesAndInsertNrseqPeptideProteinEntries.getInstance()
				.getProteinsForPeptidesAndInsertNrseqPeptideProteinEntries( peptide, proteinNameDecoyPrefixList, nrseqDatabaseId );
		
		
		PopulateMonolinkDTOListOnPerPeptideDataObject.getInstance()
		.populateMonolinkDTOListOnPerPeptideDataObject( perPeptideData, linkerList, proteinMatches_Peptide );

		
		for( NRProteinDTO protein : proteinMatches_Peptide ) {

			// a single unlinked entry
			UnlinkedDTO unlinked = new UnlinkedDTO();

			unlinked.setPeptideId( peptide.getId() );

			unlinked.setProtein( protein );


			GetUnlinkedResultItem getUnlinkedResultItem = new GetUnlinkedResultItem(); 

			getUnlinkedResultItem.setUnlinkedDTO( unlinked );


			unlinkedResultItemList.add( getUnlinkedResultItem );

		}  //end looping over proteins
		
		
		return getUnlinkedResult;
	
	}





	/**
	 * Internal result from getUnlinked method
	 *
	 */
	private class GetUnlinkedResult {
		

		private List<GetUnlinkedResultItem> unlinkedResultItemList;

		public List<GetUnlinkedResultItem> getUnlinkedResultItemList() {
			return unlinkedResultItemList;
		}

		public void setUnlinkedResultItemList(
				List<GetUnlinkedResultItem> unlinkedResultItemList) {
			this.unlinkedResultItemList = unlinkedResultItemList;
		}

	}
	
	
	
	/**
	 * Single entry in Internal result from getUnlinked method
	 *
	 */
	private class GetUnlinkedResultItem {
		

		private UnlinkedDTO unlinkedDTO;
		
				
		public UnlinkedDTO getUnlinkedDTO() {
			return unlinkedDTO;
		}

		public void setUnlinkedDTO(UnlinkedDTO unlinkedDTO) {
			this.unlinkedDTO = unlinkedDTO;
		}

	}
}