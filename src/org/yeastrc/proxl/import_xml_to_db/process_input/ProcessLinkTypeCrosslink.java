package org.yeastrc.proxl.import_xml_to_db.process_input;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dao.PsmPeptideDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_CrosslinkDAO;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.proxl.import_xml_to_db.objects.PerPeptideData;
import org.yeastrc.proxl.import_xml_to_db.objects.SearchProgramEntry;
import org.yeastrc.proxl.import_xml_to_db.utils.RoundDecimalFieldsIfNecessary;
import org.yeastrc.proxl_import.api.xml_dto.LinkedPosition;
import org.yeastrc.proxl_import.api.xml_dto.LinkedPositions;
import org.yeastrc.proxl_import.api.xml_dto.Linker;
import org.yeastrc.proxl_import.api.xml_dto.Peptide;
import org.yeastrc.proxl_import.api.xml_dto.Peptides;
import org.yeastrc.proxl_import.api.xml_dto.Psm;
import org.yeastrc.proxl_import.api.xml_dto.Psms;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptide;
import org.yeastrc.xlink.dto.CrosslinkDTO;
import org.yeastrc.xlink.dto.MatchedPeptideDTO;
import org.yeastrc.xlink.dto.NRProteinDTO;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.dto.PsmDTO;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;
import org.yeastrc.xlink.linkable_positions.GetLinkerFactory;
import org.yeastrc.xlink.linkable_positions.linkers.ILinker;





/**
 * 
 *
 */
public class ProcessLinkTypeCrosslink {



	private static final Logger log = Logger.getLogger(ProcessLinkTypeCrosslink.class);

	//  private constructor
	private ProcessLinkTypeCrosslink() { }
	
	public static ProcessLinkTypeCrosslink getInstance() { return new ProcessLinkTypeCrosslink(); }
	
	
	private static enum PeptideOrder { ONE_TO_ONE_AND_TWO_TO_TWO, ONE_TO_TWO_AND_TWO_TO_ONE };
	

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
	public void processCrosslink( 
			
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
			String msg = "There must be 2 peptides for Crosslink reported peptide: " + reportedPeptide.getReportedPeptideString();
			log.error( msg );
			throw new ProxlImporterDataException(msg);
		}
		
		List<Peptide> peptideList = peptides.getPeptide();

		if ( peptideList == null || peptideList.size() != 2 ) {
			String msg = "There must be 2 peptides for Crosslink for reported peptide: " + reportedPeptide.getReportedPeptideString();
			log.error( msg );
			throw new ProxlImporterDataException(msg);
		}
		
		
		Peptide peptide_1 = peptideList.get( 0 );
		Peptide peptide_2 = peptideList.get( 1 );
		
		PerPeptideData perPeptideData_1 = GetPerPeptideData.getInstance().getPerPeptideData( peptide_1, nrseqDatabaseId );
		PerPeptideData perPeptideData_2 = GetPerPeptideData.getInstance().getPerPeptideData( peptide_2, nrseqDatabaseId );

		int linkedPosition_1 = getCrosslinkLinkedPosition( peptide_1, "1", reportedPeptide );
		int linkedPosition_2 = getCrosslinkLinkedPosition( peptide_2, "2", reportedPeptide );


		
		List<ILinker> linkerList = new ArrayList<>();
		
		String linkerListStringForErrorMsgs = null;
		
		
		for ( Linker proxlInputLinker : proxlInputLinkerList ) {

			String proxlInputLinkerName = proxlInputLinker.getName();

			ILinker linker = GetLinkerFactory.getLinkerForAbbr( proxlInputLinkerName );
			if( linker == null ) {
				String msg = "saveCrosslinks(...): Could not get an ILinker for linker abbreviation: " 
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
		
		
		GetCrosslinksResult getCrosslinksResult =
				
				getCrosslinks( 
						nrseqDatabaseId, 
						linkerList, 
						linkerListStringForErrorMsgs,
						proteinNameDecoyPrefixList, 
						perPeptideData_1, 
						perPeptideData_2, 
						linkedPosition_1, 
						linkedPosition_2 );


		List<GetCrosslinksResultItem> getCrosslinksResultItemList = getCrosslinksResult.getCrosslinkResultItemList();
		
		if ( getCrosslinksResultItemList == null || getCrosslinksResultItemList.isEmpty() ) {
			
			log.warn( "No Mapped Proteins for this reported peptide so not inserting any PSMs: " + 
					reportedPeptide.getReportedPeptideString() );

			return;  //  EARLY EXIT   No crosslink records for this reported peptide in this search 
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

			savePSMChildrenAndCrosslinkDTORecords( 
					psm, 
					psmDTO, 						
					perPeptideData_1, 
					perPeptideData_2, 
					getCrosslinksResult );
			
			
			
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
	 * @param getCrosslinksResult
	 * @throws Exception
	 */
	private void savePSMChildrenAndCrosslinkDTORecords(
			
			Psm psm,
			PsmDTO psmDTO,
			PerPeptideData perPeptideData_1,
			PerPeptideData perPeptideData_2,
			GetCrosslinksResult getCrosslinksResult
			
			) throws Exception {
		
		//  Save PSM children
		
		SavePerPeptideDataForPSM.getInstance().savePerPeptideDataForPSM( psmDTO, perPeptideData_1 );

		SavePerPeptideDataForPSM.getInstance().savePerPeptideDataForPSM( psmDTO, perPeptideData_2 );

		MatchedPeptideDTO matchedPeptideDTO_1 = perPeptideData_1.getMatchedPeptideDTO();

		MatchedPeptideDTO matchedPeptideDTO_2 = perPeptideData_2.getMatchedPeptideDTO();

		List<GetCrosslinksResultItem> getCrosslinksResultItemList = getCrosslinksResult.getCrosslinkResultItemList();
		
		for ( GetCrosslinksResultItem getCrosslinksResultItem : getCrosslinksResultItemList ) {
			
			CrosslinkDTO crosslinkDTO = getCrosslinksResultItem.getCrosslinkDTO();
			
			PeptideOrder peptideOrder = getCrosslinksResultItem.getPeptideOrder();
			
			if ( peptideOrder == PeptideOrder.ONE_TO_ONE_AND_TWO_TO_TWO ) {
				
				crosslinkDTO.setPeptide1MatchedPeptideId( matchedPeptideDTO_1.getId() );
				crosslinkDTO.setPeptide2MatchedPeptideId( matchedPeptideDTO_2.getId() );
				
			} else {
				
				crosslinkDTO.setPeptide1MatchedPeptideId( matchedPeptideDTO_2.getId() );
				crosslinkDTO.setPeptide2MatchedPeptideId( matchedPeptideDTO_1.getId() );
				
			}
			
			BigDecimal linkerMass = psm.getLinkerMass();
			
			if ( linkerMass == null ) {
				
				String msg = "Linker Mass cannot be null or empty for Crosslink. PSM Scan Number: " + psm.getScanNumber();
				log.error( msg );
				throw new ProxlImporterDataException(msg);
			}
			
			linkerMass = RoundDecimalFieldsIfNecessary.roundDecimalFieldsIfNecessary( linkerMass );
			
			
			crosslinkDTO.setLinkerMass( linkerMass );
			
			crosslinkDTO.setPsm( psmDTO );
			
			DB_Insert_CrosslinkDAO.getInstance().save( crosslinkDTO );
			
		}
		
	}
	
	


	/**
	 * @param nrseqDatabaseId
	 * @param linkerList
	 * @param linkerListStringForErrorMsgs
	 * @param proteinNameDecoyPrefix
	 * @param perPeptideData_1
	 * @param perPeptideData_2
	 * @param linkedPosition_1
	 * @param linkedPosition_2
	 * @return
	 * @throws Exception
	 */
	private GetCrosslinksResult getCrosslinks(
			
			int nrseqDatabaseId,
			
			List<ILinker> linkerList,
			String linkerListStringForErrorMsgs,
			
			List<String> proteinNameDecoyPrefixList,
			
			PerPeptideData perPeptideData_1,
			PerPeptideData perPeptideData_2,
			
			int linkedPosition_1,
			int linkedPosition_2


			) throws Exception {
		
		
		GetCrosslinksResult getCrosslinksResult = new GetCrosslinksResult();
		
		List<GetCrosslinksResultItem> crosslinkResultItemList = new ArrayList<>();
		getCrosslinksResult.setCrosslinkResultItemList( crosslinkResultItemList );
		
		
		
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
		
		
		
		// get proteins and linkable positions in those proteins that are mapped to by the given peptides and positions
		Map<NRProteinDTO, Collection<Integer>> protein1Map = 
				GetLinkableProteinsAndPositions.getInstance()
					.getLinkableProteinsAndPositions( peptide_1, linkedPosition_1, linkerList, proteinMatches_Peptide_1 );
		
		
		if( protein1Map.keySet().size() < 1 ) {
			String msg = "getCrosslinks(...): No linkable protein positions found for " + peptide_1.getSequence() +
					" at position " + linkedPosition_1 + " for "
					 + " linker.";
			log.error( msg );
			
			throw new Exception( msg );
		}
		
		Map<NRProteinDTO, Collection<Integer>> protein2Map = 
				GetLinkableProteinsAndPositions.getInstance()
					.getLinkableProteinsAndPositions( peptide_2, linkedPosition_2, linkerList, proteinMatches_Peptide_2 );
		
		if( protein2Map.keySet().size() < 1 ) {
			String msg = "getCrosslinks(...): No linkable protein positions found for " + peptide_2.getSequence() +
					" at position " + linkedPosition_2 + " for "
					 + " linkers: " + linkerListStringForErrorMsgs;
			log.error( msg );
			
			throw new Exception( msg );
		}
		
		for( NRProteinDTO protein1 : protein1Map.keySet() ) {
						
			for( int protein1Position : protein1Map.get( protein1 ) ) {
			
				for( NRProteinDTO protein2 : protein2Map.keySet() ) {
										
					for( int protein2Position : protein2Map.get( protein2 ) ) {
												
						// a single crosslink entry
			    		CrosslinkDTO crosslink = new CrosslinkDTO();
			    		
			    		//  peptideOrder default to peptide 1 is going into crosslink.peptide1Id
			    		//    and peptide 2 is going into crosslink.peptide2Id
			    		
			    		PeptideOrder peptideOrder = PeptideOrder.ONE_TO_ONE_AND_TWO_TO_TWO;
			    		
			    		
			    		// insert the proteins in order of their nrseq_id
			    		if( protein1.getNrseqId() < protein2.getNrseqId() ) {
			    			crosslink.setPeptide1Id( peptide_1.getId() );
			    			crosslink.setPeptide1Position( linkedPosition_1 );
//			    			crosslink.setPeptide1MatchedPeptideId( peptide1MatchedPeptideId );
			    			
			    			crosslink.setPeptide2Id( peptide_2.getId() );
			    			crosslink.setPeptide2Position( linkedPosition_2 );
//			    			crosslink.setPeptide2MatchedPeptideId( peptide2MatchedPeptideId );
			    			
			    			crosslink.setProtein1( protein1 );
			    			crosslink.setProtein1Position( protein1Position );
			    			
			    			crosslink.setProtein2( protein2 );
			    			crosslink.setProtein2Position( protein2Position );	
			    			
			    		} else if( protein1.getNrseqId() == protein2.getNrseqId() ) {
			    			crosslink.setProtein1( protein1 );
			    			crosslink.setProtein2( protein2 );
			    			
			    			// if it is a link between the same protein, put in order of the link position on the protein
			    			if( protein1Position <= protein2Position ) {
			    				crosslink.setProtein1Position( protein1Position );
				    			crosslink.setPeptide1Id( peptide_1.getId() );
					    		crosslink.setPeptide1Position( linkedPosition_1 );
//				    			crosslink.setPeptide1MatchedPeptideId( peptide1MatchedPeptideId );

					    		
					    		crosslink.setProtein2Position( protein2Position );
				    			crosslink.setPeptide2Id( peptide_2.getId() );
				    			crosslink.setPeptide2Position( linkedPosition_2 );
//				    			crosslink.setPeptide2MatchedPeptideId( peptide2MatchedPeptideId );

			    			} else {
			    				
			    				//  peptideOrder changed since peptide 1 on crosslink is set to peptide 2 
			    				//                              and peptide 2 on crosslink is set to peptide 1
			    				
			    				peptideOrder = PeptideOrder.ONE_TO_TWO_AND_TWO_TO_ONE;
			    				
			    				crosslink.setProtein1Position( protein2Position );
				    			crosslink.setPeptide1Id( peptide_2.getId() );
					    		crosslink.setPeptide1Position( linkedPosition_2 );
//				    			crosslink.setPeptide1MatchedPeptideId( peptide2MatchedPeptideId );

					    		crosslink.setProtein2Position( protein1Position );
				    			crosslink.setPeptide2Id( peptide_1.getId() );
				    			crosslink.setPeptide2Position( linkedPosition_1 );
//				    			crosslink.setPeptide2MatchedPeptideId( peptide1MatchedPeptideId );

			    			}
				    			
			    		} else {
			    			
		    				//  peptideOrder changed since peptide 1 on crosslink is set to peptide 2 
		    				//                              and peptide 2 on crosslink is set to peptide 1
		    				
			    			peptideOrder = PeptideOrder.ONE_TO_TWO_AND_TWO_TO_ONE;
			    			
			    			crosslink.setPeptide1Id( peptide_2.getId() );
			    			crosslink.setPeptide1Position( linkedPosition_2 );
//			    			crosslink.setPeptide1MatchedPeptideId( peptide2MatchedPeptideId );

			    			crosslink.setPeptide2Id( peptide_1.getId() );
			    			crosslink.setPeptide2Position( linkedPosition_1 );
//			    			crosslink.setPeptide2MatchedPeptideId( peptide1MatchedPeptideId );

			    			crosslink.setProtein1( protein2 );
			    			crosslink.setProtein1Position( protein2Position );
			    			
			    			crosslink.setProtein2( protein1 );
			    			crosslink.setProtein2Position( protein1Position );	
			    		}

						
			    		GetCrosslinksResultItem getCrosslinksResultItem = new GetCrosslinksResultItem(); 
			    		
			    		getCrosslinksResultItem.setCrosslinkDTO( crosslink );
			    		getCrosslinksResultItem.setPeptideOrder( peptideOrder );
			    		
		    			crosslinkResultItemList.add( getCrosslinksResultItem );


					}  //end looping over protein2positions
				
				}  //end looping over protein2s
			
			}  //end looping over protein1positions
		
		}  //end looping over protein1s
		
		
		return getCrosslinksResult;
	
	}




	/**
	 * @param peptide
	 * @param peptideNumber
	 * @return
	 * @throws ProxlImporterDataException 
	 */
	private int getCrosslinkLinkedPosition( Peptide peptide, String peptideNumber, ReportedPeptide reportedPeptide ) throws ProxlImporterDataException {
		
		LinkedPositions linkedPositions = peptide.getLinkedPositions();

		if ( linkedPositions == null ) {
			
			String msg = "There must be exactly 1 linked position for peptide " + peptideNumber 
					+ " for peptide sequence '" + peptide.getSequence() + "'"
					+ " for Crosslink reported peptide: " + reportedPeptide.getReportedPeptideString();
			log.error( msg );
			throw new ProxlImporterDataException(msg);
		}
		
		List<LinkedPosition> LinkedPositionList = linkedPositions.getLinkedPosition();
		if ( LinkedPositionList == null || LinkedPositionList.size() != 1 ) {
			
			String msg = "There must be 1 exactly linked position for peptide " + peptideNumber 
					+ " for peptide sequence '" + peptide.getSequence() + "'"
					+ " for Crosslink reported peptide: " + reportedPeptide.getReportedPeptideString();
			log.error( msg );
			throw new ProxlImporterDataException(msg);
		}
		
		int linkPosition = LinkedPositionList.get(0).getPosition().intValue();
		
		return linkPosition;
	}
	
	


	/**
	 * Internal result from getCrosslinks method
	 *
	 */
	private class GetCrosslinksResult {
		

		private List<GetCrosslinksResultItem> crosslinkResultItemList;

		public List<GetCrosslinksResultItem> getCrosslinkResultItemList() {
			return crosslinkResultItemList;
		}

		public void setCrosslinkResultItemList(
				List<GetCrosslinksResultItem> crosslinkResultItemList) {
			this.crosslinkResultItemList = crosslinkResultItemList;
		}

	}
	
	
	
	/**
	 * Single entry in Internal result from getCrosslinks method
	 *
	 */
	private class GetCrosslinksResultItem {
		
		private CrosslinkDTO crosslinkDTO;

		private PeptideOrder peptideOrder;
		
		
		
		public CrosslinkDTO getCrosslinkDTO() {
			return crosslinkDTO;
		}

		public void setCrosslinkDTO(CrosslinkDTO crosslinkDTO) {
			this.crosslinkDTO = crosslinkDTO;
		}


		public PeptideOrder getPeptideOrder() {
			return peptideOrder;
		}

		public void setPeptideOrder(PeptideOrder peptideOrder) {
			this.peptideOrder = peptideOrder;
		}

	}
}