package org.yeastrc.proxl.import_xml_to_db.process_input;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.drop_peptides_psms_for_cmd_line_cutoffs.DropPeptideAndOrPSMForCmdLineCutoffs;
import org.yeastrc.proxl.import_xml_to_db.drop_peptides_psms_for_cmd_line_cutoffs.DropPeptidePSMCutoffValues;
import org.yeastrc.proxl.import_xml_to_db.drop_peptides_psms_for_cmd_line_cutoffs.DroppedPeptideCount;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.proxl.import_xml_to_db.objects.SearchProgramEntry;
import org.yeastrc.proxl_import.api.xml_dto.LinkType;
import org.yeastrc.proxl_import.api.xml_dto.Linker;
import org.yeastrc.proxl_import.api.xml_dto.Linkers;
import org.yeastrc.proxl_import.api.xml_dto.Peptide;
import org.yeastrc.proxl_import.api.xml_dto.Peptides;
import org.yeastrc.proxl_import.api.xml_dto.ProxlInput;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptide;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptides;
import org.yeastrc.xlink.dao.ReportedPeptideDAO;
import org.yeastrc.xlink.dao.SearchReportedPeptideDAO;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;
import org.yeastrc.xlink.dto.SearchReportedPeptideDTO;
import org.yeastrc.xlink.utils.XLinkUtils;


/**
 * 
 *
 */
public class ProcessReportedPeptidesAndPSMs {
	
	private static final Logger log = Logger.getLogger( ProcessReportedPeptidesAndPSMs.class );
	
	/**
	 * private constructor
	 */
	private ProcessReportedPeptidesAndPSMs(){}
	
	public static ProcessReportedPeptidesAndPSMs getInstance() {
		
		return new ProcessReportedPeptidesAndPSMs();
	}

	
	/**
	 * @param proxlInput
	 * @param nrseqDatabaseId
	 * @param proteinNameDecoyPrefixList
	 * @param searchId
	 * @param searchProgramEntryMap
	 * @param mapOfScanFilenamesMapsOfScanNumbersToScanIds
	 * @throws Exception
	 */
	public void processReportedPeptides( 
			
			ProxlInput proxlInput, 
			int nrseqDatabaseId, 
			List<String> proteinNameDecoyPrefixList, 
			int searchId, 

			DropPeptidePSMCutoffValues dropPeptidePSMCutoffValues,
			
			Map<String, SearchProgramEntry> searchProgramEntryMap,
			Map<String, Map<Integer,Integer>> mapOfScanFilenamesMapsOfScanNumbersToScanIds ) throws Exception {
		
		

		Linkers proxlInputLinkers = proxlInput.getLinkers();

		
		//  Commented out since "proxlInputLinkers" now contains a single Linker instead of a List
		
//		List<Linker> proxlInputLinkerList = proxlInputLinkers.getLinker();
//
//		if ( proxlInputLinkerList == null || proxlInputLinkerList.isEmpty() ) {
//				
//
//			String msg = "at least one linker is required";
//			log.error( msg );
//			
//			throw new ProxlImporterDataException(msg);
//		}
		
		
		List<Linker> proxlInputLinkerList = proxlInputLinkers.getLinker();

		
		//////////////
		
		
		
		ReportedPeptides reportedPeptides = proxlInput.getReportedPeptides();
		
		if ( reportedPeptides != null ) {

			List<ReportedPeptide> reportedPeptideList =
					reportedPeptides.getReportedPeptide();

			if ( reportedPeptideList != null && ( ! reportedPeptideList.isEmpty() ) ) {


				for ( ReportedPeptide reportedPeptide : reportedPeptideList ) {
					

					if ( DropPeptideAndOrPSMForCmdLineCutoffs.getInstance()
							.dropPeptideForCmdLineCutoffs( reportedPeptide, dropPeptidePSMCutoffValues ) ) {
						
						DroppedPeptideCount.incrementDroppedPeptideCount();
						
						continue;  // EARLY continue to next record
					}
					
					String reportedPeptideString =
							reportedPeptide.getReportedPeptideString();
					
					LinkType linkType = reportedPeptide.getType();

					String linkTypeName = linkType.name();
					
					String linkTypeNameLowerCase = linkTypeName.toLowerCase();

					int linkTypeNumber = XLinkUtils.getTypeNumber( linkTypeNameLowerCase );
					
					if ( linkTypeNumber < 0 ) {
						
						String msg = "Link Type name '" + linkTypeName + "' is not recognized for reported peptpide: " + reportedPeptide.getReportedPeptideString();
						log.error( msg );
						throw new ProxlImporterDataException(msg);
					}

					//  Retrieves reported_peptide record or inserts it if not in the database.
					ReportedPeptideDTO reportedPeptideDTO =
							ReportedPeptideDAO.getInstance().getReportedPeptideDTO( reportedPeptideString );

					int reportedPeptideId = reportedPeptideDTO.getId();
					
					SearchReportedPeptideDTO searchReportedPeptideDTO = new SearchReportedPeptideDTO(); 
					searchReportedPeptideDTO.setSearchId( searchId );
					searchReportedPeptideDTO.setReportedPeptideId( reportedPeptideId );
					SearchReportedPeptideDAO.getInstance().saveToDatabaseIgnoreDuplicates( searchReportedPeptideDTO );
					
					SaveSearchReportedPeptideAnnotations.getInstance().saveReportedPeptideAnnotations( reportedPeptide, searchId, reportedPeptideDTO.getId(), searchProgramEntryMap );
					
					
					//  Each of the following will save the PSM record and all it's children records

					if ( linkTypeNumber == XLinkUtils.TYPE_CROSSLINK ) {
						
						ProcessLinkTypeCrosslink.getInstance().processCrosslink( 
								
								reportedPeptide, 						// from XML input 
								proxlInputLinkerList,	// list of linkers from XML input

								proteinNameDecoyPrefixList, 

								nrseqDatabaseId, 
								linkTypeNumber, 
								reportedPeptideDTO, 
								searchId, 

								dropPeptidePSMCutoffValues,
								
								searchProgramEntryMap,
								mapOfScanFilenamesMapsOfScanNumbersToScanIds );

					} else if ( linkTypeNumber == XLinkUtils.TYPE_LOOPLINK ) {
							
						ProcessLinkTypeLooplink.getInstance().processLooplink( 

								reportedPeptide, 						// from XML input 
								proxlInputLinkerList,	// list of linkers from XML input

								proteinNameDecoyPrefixList, 

								nrseqDatabaseId, 
								linkTypeNumber, 
								reportedPeptideDTO, 
								searchId, 
								
								dropPeptidePSMCutoffValues,
								
								searchProgramEntryMap,
								mapOfScanFilenamesMapsOfScanNumbersToScanIds);
						 

					} else if ( linkTypeNumber == XLinkUtils.TYPE_UNLINKED ) {
						
						processXML_LinkType_Unlinked(
								
								reportedPeptide, 						// from XML input 
								proxlInputLinkerList,	// list of linkers from XML input

								proteinNameDecoyPrefixList, 

								nrseqDatabaseId, 
								linkTypeNumber, 
								reportedPeptideDTO, 
								searchId, 
								
								dropPeptidePSMCutoffValues,
								
								searchProgramEntryMap,
								mapOfScanFilenamesMapsOfScanNumbersToScanIds );
						 
						
					} else {
						
						String msg = "Link Type name '" + linkTypeName + "' is not recognized for reported peptpide: " + reportedPeptide.getReportedPeptideString();
						log.error( msg );
						throw new ProxlImporterDataException(msg);
					}
				}
			}
		}
	}


	/**
	 * @param reportedPeptide
	 * @param proxlInputLinkerList
	 * @param proteinNameDecoyPrefixList
	 * @param nrseqDatabaseId
	 * @param linkTypeNumber
	 * @param reportedPeptideDTO
	 * @param searchId
	 * @param dropPeptidePSMCutoffValues
	 * @param searchProgramEntryMap
	 * @param mapOfScanFilenamesMapsOfScanNumbersToScanIds
	 * @throws Exception
	 */
	public void processXML_LinkType_Unlinked( 
			
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

		if ( peptides == null ) {
			String msg = "There must be 1 or 2 peptides for Unlinked reported peptide: " + reportedPeptide.getReportedPeptideString();
			log.error( msg );
			throw new ProxlImporterDataException(msg);
		}
		
		List<Peptide> peptideList = peptides.getPeptide();

		if ( peptideList == null || ( peptideList.size() != 1 && peptideList.size() != 2 ) ) {
			String msg = "There must be 1 or 2 peptides for Unlinked for reported peptide: " + reportedPeptide.getReportedPeptideString();
			log.error( msg );
			throw new ProxlImporterDataException(msg);
		}
		
		
		if ( peptideList.size() ==  1 ) {
			
			//  Proxl internal "Unlinked" is a single peptide that is not a looplink
			
			ProcessLinkTypeUnlinkedAsDefinedByProxl.getInstance()
			.processUnlinked(
					reportedPeptide, 
					proxlInputLinkerList, 
					proteinNameDecoyPrefixList, 
					nrseqDatabaseId, 
					linkTypeNumber, 
					reportedPeptideDTO, 
					searchId, 
					
					dropPeptidePSMCutoffValues,
					
					searchProgramEntryMap,
					mapOfScanFilenamesMapsOfScanNumbersToScanIds );
			
			
		} else {
			
			//  Proxl internal "Dimer" is 2 peptides that is not a crosslink
			
			ProcessLinkTypeDimerAsDefinedByProxl.getInstance()
			.processDimer(
					reportedPeptide, 
					proxlInputLinkerList, 
					proteinNameDecoyPrefixList, 
					nrseqDatabaseId, 
					linkTypeNumber, 
					reportedPeptideDTO, 
					searchId, 
					
					dropPeptidePSMCutoffValues,
					
					searchProgramEntryMap,
					mapOfScanFilenamesMapsOfScanNumbersToScanIds );
		}
		
		
	}
}
