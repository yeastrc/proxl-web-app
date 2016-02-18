package org.yeastrc.proxl.import_xml_to_db.process_input;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dao.PsmPeptideDAO;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.proxl.import_xml_to_db.objects.PerPeptideData;
import org.yeastrc.proxl.import_xml_to_db.objects.SearchProgramEntry;
import org.yeastrc.proxl_import.api.xml_dto.Linker;
import org.yeastrc.proxl_import.api.xml_dto.Peptide;
import org.yeastrc.proxl_import.api.xml_dto.Peptides;
import org.yeastrc.proxl_import.api.xml_dto.Psm;
import org.yeastrc.proxl_import.api.xml_dto.Psms;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptide;
import org.yeastrc.xlink.dao.DimerDAO;
import org.yeastrc.xlink.dto.DimerDTO;
import org.yeastrc.xlink.dto.NRProteinDTO;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.dto.PsmDTO;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;
import org.yeastrc.xlink.linkable_positions.GetLinkerFactory;
import org.yeastrc.xlink.linkable_positions.linkers.ILinker;
import org.yeastrc.xlink.utils.XLinkUtils;





/**
 * Proxl internal "Dimer" is two peptides that are not a dimer (not linked to each other)
 *
 */
public class ProcessLinkTypeDimerAsDefinedByProxl {



	private static final Logger log = Logger.getLogger(ProcessLinkTypeDimerAsDefinedByProxl.class);

	//  private constructor
	private ProcessLinkTypeDimerAsDefinedByProxl() { }
	
	public static ProcessLinkTypeDimerAsDefinedByProxl getInstance() { return new ProcessLinkTypeDimerAsDefinedByProxl(); }
	

//	private static enum PeptideOrder { ONE_TO_ONE_AND_TWO_TO_TWO, ONE_TO_TWO_AND_TWO_TO_ONE };
	

	
	/**
	 * Proxl internal "Dimer" is two peptides that are not a dimer (not linked to each other)
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
	public void processDimer( 
			
			ReportedPeptide reportedPeptide, 
			
			List<Linker> proxlInputLinkerList,	//  Keep for Monolinks

			List<String> proteinNameDecoyPrefixList,
			
			int nrseqDatabaseId,
			
			int linkTypeNumberParam, 
			ReportedPeptideDTO reportedPeptideDTO, 
			int searchId, 
			
			Map<String, SearchProgramEntry> searchProgramEntryMap,
			
			Map<String, Map<Integer,Integer>> mapOfScanFilenamesMapsOfScanNumbersToScanIds
			
			) throws Exception {
		
		
		Peptides peptides =
				reportedPeptide.getPeptides();

		if ( peptides == null ) {
			String msg = "There must be 2 peptides for Dimer reported peptide: " + reportedPeptide.getReportedPeptideString();
			log.error( msg );
			throw new ProxlImporterDataException(msg);
		}
		
		List<Peptide> peptideList = peptides.getPeptide();

		if ( peptideList == null || peptideList.size() != 2 ) {
			String msg = "There must be 2 peptides for Dimer for reported peptide: " + reportedPeptide.getReportedPeptideString();
			log.error( msg );
			throw new ProxlImporterDataException(msg);
		}
		
		
		Peptide peptide_1 = peptideList.get( 0 );
		Peptide peptide_2 = peptideList.get( 1 );
		
		PerPeptideData perPeptideData_1 = GetPerPeptideData.getInstance().getPerPeptideData( peptide_1, nrseqDatabaseId );
		PerPeptideData perPeptideData_2 = GetPerPeptideData.getInstance().getPerPeptideData( peptide_2, nrseqDatabaseId );


		//  Keep for Monolinks
		List<ILinker> linkerList = new ArrayList<>();
		
		String linkerListStringForErrorMsgs = null;
		
		
		for ( Linker proxlInputLinker : proxlInputLinkerList ) {

			String proxlInputLinkerName = proxlInputLinker.getName();

			ILinker linker = GetLinkerFactory.getLinkerForAbbr( proxlInputLinkerName );
			if( linker == null ) {
				String msg = "saveDimers(...): Could not get an ILinker for linker abbreviation: " 
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
		
		GetDimersResult getDimersResult =
				
				getDimers( 
						nrseqDatabaseId, 
						linkerList, 
						linkerListStringForErrorMsgs,
						proteinNameDecoyPrefixList, 
						perPeptideData_1, 
						perPeptideData_2 );


		List<GetDimersResultItem> getDimersResultItemList = getDimersResult.getDimerResultItemList();
		
		if ( getDimersResultItemList == null || getDimersResultItemList.isEmpty() ) {
		
			log.warn( "No Mapped Proteins for this reported peptide so not inserting any PSMs: " + 
					reportedPeptide.getReportedPeptideString() );

			return;  //  EARLY EXIT   No dimer records for this reported peptide in this search 
		}
		
		
		//  Hard code link type number to dimer since parameter to method is for UNLINKED 
		int linkTypeNumberForDimer = XLinkUtils.TYPE_DIMER;
		

		Psms psms =	reportedPeptide.getPsms();

		List<Psm> psmList = psms.getPsm();

		for ( Psm psm : psmList ) {


			PsmDTO psmDTO = 
					PopulateAndSavePsmDTO.getInstance().populateAndSavePSMDTO( 
							
							searchId, 
							mapOfScanFilenamesMapsOfScanNumbersToScanIds, 
							linkTypeNumberForDimer, 
							reportedPeptideDTO, 
							psm );
			
			SavePsmAnnotations.getInstance().savePsmAnnotations( psm, psmDTO, searchProgramEntryMap );

			savePSMChildrenAndDimerDTORecords( 
					psm, 
					psmDTO, 						
					perPeptideData_1, 
					perPeptideData_2, 
					getDimersResult );
			

			
			//  Save PsmDTO.id PeptideDTO.id mapping
			
			PsmPeptideDAO.getInstance().saveToDatabase( psmDTO.getId(), perPeptideData_1.getPeptideDTO().getId() );
			PsmPeptideDAO.getInstance().saveToDatabase( psmDTO.getId(), perPeptideData_2.getPeptideDTO().getId() );

		}
	}
	
	
	

	/**
	 * @param psm
	 * @param psmDTO
	 * @param perPeptideData_1
	 * @param perPeptideData_2
	 * @param getDimersResult
	 * @throws Exception
	 */
	private void savePSMChildrenAndDimerDTORecords(
			
			Psm psm,
			PsmDTO psmDTO,
			PerPeptideData perPeptideData_1,
			PerPeptideData perPeptideData_2,
			GetDimersResult getDimersResult
			
			) throws Exception {
		
		//  Save PSM children
		
		SavePerPeptideDataForPSM.getInstance().savePerPeptideDataForPSM( psmDTO, perPeptideData_1 );

		SavePerPeptideDataForPSM.getInstance().savePerPeptideDataForPSM( psmDTO, perPeptideData_2 );

//		MatchedPeptideDTO matchedPeptideDTO_1 = perPeptideData_1.getMatchedPeptideDTO();
//
//		MatchedPeptideDTO matchedPeptideDTO_2 = perPeptideData_2.getMatchedPeptideDTO();

		List<GetDimersResultItem> getDimersResultItemList = getDimersResult.getDimerResultItemList();
		
		for ( GetDimersResultItem getDimersResultItem : getDimersResultItemList ) {
			
			DimerDTO dimerDTO = getDimersResultItem.getDimerDTO();
			
//			PeptideOrder peptideOrder = getDimersResultItem.getPeptideOrder();
			
			dimerDTO.setPsm( psmDTO );
			
			DimerDAO.getInstance().save( dimerDTO );
			
		}
		
	}
	
	


	/**
	 * @param nrseqDatabaseId
	 * @param linkerList
	 * @param linkerListStringForErrorMsgs
	 * @param proteinNameDecoyPrefix
	 * @param perPeptideData_1
	 * @param perPeptideData_2
	 * @return
	 * @throws Exception
	 */
	private GetDimersResult getDimers(
			
			int nrseqDatabaseId,
			
			List<ILinker> linkerList,
			String linkerListStringForErrorMsgs,
			
			List<String> proteinNameDecoyPrefixList,
			
			PerPeptideData perPeptideData_1,
			PerPeptideData perPeptideData_2


			) throws Exception {
		
		
		GetDimersResult getDimersResult = new GetDimersResult();
		
		List<GetDimersResultItem> dimerResultItemList = new ArrayList<>();
		getDimersResult.setDimerResultItemList( dimerResultItemList );
		
		
		
		PeptideDTO peptide_1 = perPeptideData_1.getPeptideDTO();
		PeptideDTO peptide_2 = perPeptideData_2.getPeptideDTO();
		
		
		Collection<NRProteinDTO> proteinMatches_Peptide_1 = 
				GetProteinsForPeptidesAndInsertNrseqPeptideProteinEntries.getInstance()
				.getProteinsForPeptidesAndInsertNrseqPeptideProteinEntries( peptide_1, proteinNameDecoyPrefixList, nrseqDatabaseId );
		
		Collection<NRProteinDTO> proteinMatches_Peptide_2 = null;
		
		if ( peptide_1.getSequence().equals( peptide_2.getSequence() ) ) {
			
			proteinMatches_Peptide_2 = proteinMatches_Peptide_1;
			
		} else {
			
			proteinMatches_Peptide_2 =
				GetProteinsForPeptidesAndInsertNrseqPeptideProteinEntries.getInstance()
				.getProteinsForPeptidesAndInsertNrseqPeptideProteinEntries( peptide_2, proteinNameDecoyPrefixList, nrseqDatabaseId );
		}
		
		PopulateMonolinkDTOListOnPerPeptideDataObject.getInstance()
		.populateMonolinkDTOListOnPerPeptideDataObject( perPeptideData_1, linkerList, proteinMatches_Peptide_1 );

		
		PopulateMonolinkDTOListOnPerPeptideDataObject.getInstance()
		.populateMonolinkDTOListOnPerPeptideDataObject( perPeptideData_2, linkerList, proteinMatches_Peptide_2 );
		
		
		
		
		for( NRProteinDTO protein1 : proteinMatches_Peptide_1 ) {
						
			for( NRProteinDTO protein2 : proteinMatches_Peptide_2 ) {

				// a single dimer entry
				DimerDTO dimer = new DimerDTO();

				//  peptideOrder default to peptide 1 is going into dimer.peptide1Id
				//    and peptide 2 is going into dimer.peptide2Id

//				PeptideOrder peptideOrder = PeptideOrder.ONE_TO_ONE_AND_TWO_TO_TWO;

				// insert the proteins in order of their nrseq_id
				if( protein1.getNrseqId() <= protein2.getNrseqId() ) {
					dimer.setPeptide1Id( peptide_1.getId() );    			    			
					dimer.setPeptide2Id( peptide_2.getId() );

					dimer.setProtein1( protein1 );
					dimer.setProtein2( protein2 );
				} else {
					

    				//  peptideOrder changed since peptide 1 on dimer is set to peptide 2 
    				//                              and peptide 2 on dimer is set to peptide 1
    				
//    				peptideOrder = PeptideOrder.ONE_TO_TWO_AND_TWO_TO_ONE;
    				
    				
					dimer.setPeptide1Id( peptide_2.getId() );    			    			
					dimer.setPeptide2Id( peptide_1.getId() );

					dimer.setProtein1( protein2 );
					dimer.setProtein2( protein1 );
				}
				

				
	    		GetDimersResultItem getDimersResultItem = new GetDimersResultItem(); 
	    		
	    		getDimersResultItem.setDimerDTO( dimer );
//	    		getDimersResultItem.setPeptideOrder( peptideOrder );
	    					    		
	    		dimerResultItemList.add( getDimersResultItem );
	    		
				
			}  //end looping over protein2
			
		}  //end looping over protein1
		
		
		return getDimersResult;
	
	}





	/**
	 * Internal result from getDimers method
	 *
	 */
	private class GetDimersResult {
		

		private List<GetDimersResultItem> dimerResultItemList;

		public List<GetDimersResultItem> getDimerResultItemList() {
			return dimerResultItemList;
		}

		public void setDimerResultItemList(
				List<GetDimersResultItem> dimerResultItemList) {
			this.dimerResultItemList = dimerResultItemList;
		}

	}
	
	
	
	/**
	 * Single entry in Internal result from getDimers method
	 *
	 */
	private class GetDimersResultItem {
		
		private DimerDTO dimerDTO;
		

//		private PeptideOrder peptideOrder;
//		
//
//		
//		public PeptideOrder getPeptideOrder() {
//			return peptideOrder;
//		}
//
//		public void setPeptideOrder(PeptideOrder peptideOrder) {
//			this.peptideOrder = peptideOrder;
//		}

		public DimerDTO getDimerDTO() {
			return dimerDTO;
		}

		public void setDimerDTO(DimerDTO dimerDTO) {
			this.dimerDTO = dimerDTO;
		}

	}
}
