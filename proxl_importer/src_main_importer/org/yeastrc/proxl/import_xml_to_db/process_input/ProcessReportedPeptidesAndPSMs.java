package org.yeastrc.proxl.import_xml_to_db.process_input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dao.ReportedPeptideDAO_Importer;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_PsmFilterableAnnotationGenericLookupDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SearchDynamicModMassDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SearchReportedPeptideDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_UnifiedRepPep_Search_ReportedPeptide_BestPsmValue_Generic_Lookup__DAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_UnifiedRepPep_Search_ReportedPeptide_PeptideValue_Generic_Lookup__DAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_UnifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DAO;
import org.yeastrc.proxl.import_xml_to_db.drop_peptides_psms_for_cutoffs.DropPeptideAndOrPSMForCutoffs;
import org.yeastrc.proxl.import_xml_to_db.drop_peptides_psms_for_cutoffs.DropPeptidePSMCutoffValues;
import org.yeastrc.proxl.import_xml_to_db.drop_peptides_psms_for_cutoffs.DroppedPeptideCount;
import org.yeastrc.proxl.import_xml_to_db.dto.SearchDTO_Importer;
import org.yeastrc.proxl.import_xml_to_db.dto.SearchReportedPeptideDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.SearchScanFilenameDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.UnifiedRepPep_Search_ReportedPeptide_BestPsmValue_Generic_Lookup__DTO;
import org.yeastrc.proxl.import_xml_to_db.dto.UnifiedRepPep_Search_ReportedPeptide_PeptideValue_Generic_Lookup__DTO;
import org.yeastrc.proxl.import_xml_to_db.dto.UnifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DTO;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterInteralException;
import org.yeastrc.proxl.import_xml_to_db.objects.PerPeptideData;
import org.yeastrc.proxl.import_xml_to_db.objects.SearchProgramEntry;
import org.yeastrc.proxl.import_xml_to_db.process_input.ProcessProxlInput.ReportedPeptideAndPsmFilterableAnnotationTypesOnId;
import org.yeastrc.proxl.import_xml_to_db.unified_reported_peptide.main.InsertIfNotInDBUnifiedReportedPeptideAndChildren;
import org.yeastrc.proxl_import.api.xml_dto.LinkType;
import org.yeastrc.proxl_import.api.xml_dto.Linker;
import org.yeastrc.proxl_import.api.xml_dto.Linkers;
import org.yeastrc.proxl_import.api.xml_dto.MatchedProteins;
import org.yeastrc.proxl_import.api.xml_dto.Peptide;
import org.yeastrc.proxl_import.api.xml_dto.Peptides;
import org.yeastrc.proxl_import.api.xml_dto.ProxlInput;
import org.yeastrc.proxl_import.api.xml_dto.Psm;
import org.yeastrc.proxl_import.api.xml_dto.Psms;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptide;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptides;
import org.yeastrc.proxl_import.api.xml_dto.Psm.PerPeptideAnnotations;
import org.yeastrc.proxl_import.api.xml_dto.Psm.PerPeptideAnnotations.PsmPeptide;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.AnnotationTypeFilterableDTO;
import org.yeastrc.xlink.dto.PsmAnnotationDTO;
import org.yeastrc.xlink.dto.PsmDTO;
import org.yeastrc.xlink.dto.PsmFilterableAnnotationGenericLookupDTO;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;
import org.yeastrc.xlink.dto.SearchReportedPeptideAnnotationDTO;
import org.yeastrc.xlink.dto.UnifiedReportedPeptideLookupDTO;
import org.yeastrc.xlink.enum_classes.FilterDirectionType;
import org.yeastrc.xlink.enum_classes.Yes_No__NOT_APPLICABLE_Enum;
import org.yeastrc.xlink.exceptions.ProxlBaseDataException;
import org.yeastrc.xlink.linkable_positions.GetLinkerFactory;
import org.yeastrc.xlink.linkable_positions.linkers.ILinker;
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
	 * @param search
	 * @param dropPeptidePSMCutoffValues
	 * @param searchProgramEntryMap
	 * @param mapOfScanFilenamesMapsOfScanNumbersToScanIds
	 * @throws Exception
	 */
	public void processReportedPeptides( 
			ProxlInput proxlInput, 
			SearchDTO_Importer search,
			DropPeptidePSMCutoffValues dropPeptidePSMCutoffValues,
			Map<String, SearchProgramEntry> searchProgramEntryMap,
			ReportedPeptideAndPsmFilterableAnnotationTypesOnId reportedPeptideAndPsmFilterableAnnotationTypesOnId,
			Map<String, Map<Integer,Integer>> mapOfScanFilenamesMapsOfScanNumbersToScanIds ) throws Exception {
		
		int searchId = search.getId();
		// Put MatchedProteins in Singleton class GetProteinsForPeptides
		MatchedProteins matchedProteinsFromProxlXML = proxlInput.getMatchedProteins();
		GetProteinsForPeptide.getInstance().setMatchedProteinsFromProxlXML( matchedProteinsFromProxlXML );
		Linkers proxlInputLinkers = proxlInput.getLinkers();
		List<Linker> proxlInputLinkerList = proxlInputLinkers.getLinker();
		List<ILinker> linkerList = new ArrayList<>();
		String linkerListStringForErrorMsgs = null;
		for ( Linker proxlInputLinker : proxlInputLinkerList ) {
			String proxlInputLinkerName = proxlInputLinker.getName();
			
			ILinker linker = null;
			try {
				linker = GetLinkerFactory.getLinkerForAbbr( proxlInputLinkerName );
			} catch ( ProxlBaseDataException e ) {
				log.error( "GetLinkerFactory.getLinkerForAbbr( linkerAbbr ); threw ProxlBaseDataException. Abbr: " + proxlInputLinkerName, e );
				throw e;
			} catch ( Exception e ) {
				log.error( "GetLinkerFactory.getLinkerForAbbr( linkerAbbr ); threw Exception.  Abbr: " + proxlInputLinkerName, e );
				throw e;
			}
			if( linker == null ) {
				String msg = "Could not get an ILinker for linker abbreviation: " 
						+ proxlInputLinkerName;
				log.error( "processReportedPeptides(...): " + msg );
				throw new ProxlImporterDataException( msg );
			}
			linkerList.add( linker );
			if ( linkerListStringForErrorMsgs == null ) {
				linkerListStringForErrorMsgs = proxlInputLinkerName;
			} else {
				linkerListStringForErrorMsgs += ", " + proxlInputLinkerName;
			}
		}
		
		Map<Integer, AnnotationTypeDTO> filterableReportedPeptideAnnotationTypesOnId = 
				reportedPeptideAndPsmFilterableAnnotationTypesOnId.getFilterableReportedPeptideAnnotationTypesOnId();
		Map<Integer, AnnotationTypeDTO> filterablePsmAnnotationTypesOnId = 
				reportedPeptideAndPsmFilterableAnnotationTypesOnId.getFilterablePsmAnnotationTypesOnId();
		Map<Integer, AnnotationTypeDTO> filterablePsmPerPeptideAnnotationTypesOnId = 
				reportedPeptideAndPsmFilterableAnnotationTypesOnId.getFilterablePsmPerPeptideAnnotationTypesOnId();
		
		Map<String, SearchScanFilenameDTO> scanFilenamesOnPSMsKeyedOnScanFilename = new HashMap<>();
		//////////////
		ReportedPeptides reportedPeptides = proxlInput.getReportedPeptides();
		if ( reportedPeptides != null ) {
			List<ReportedPeptide> reportedPeptideList =
					reportedPeptides.getReportedPeptide();
			if ( reportedPeptideList != null && ( ! reportedPeptideList.isEmpty() ) ) {
				//  Accumulate Unique Dynamic Mod Masses to insert into a lookup table
				Set<Double> uniqueDynamicModMassesForTheSearch = new HashSet<>();
				for ( ReportedPeptide reportedPeptide : reportedPeptideList ) {
					processSingleReportedPeptide( 
							reportedPeptide, 
							searchId, 
							linkerList, 
							linkerListStringForErrorMsgs, 
							dropPeptidePSMCutoffValues, 
							searchProgramEntryMap, 
							filterableReportedPeptideAnnotationTypesOnId,
							filterablePsmAnnotationTypesOnId,
							filterablePsmPerPeptideAnnotationTypesOnId,
							mapOfScanFilenamesMapsOfScanNumbersToScanIds,
							uniqueDynamicModMassesForTheSearch,
							scanFilenamesOnPSMsKeyedOnScanFilename );
				}
				insertUniqueDynamicModMassesForTheSearch( uniqueDynamicModMassesForTheSearch, searchId );
			}
		}
	}
	
	/**
	 * @param uniqueDynamicModMassesForTheSearch
	 * @throws Exception
	 */
	private void insertUniqueDynamicModMassesForTheSearch( 
			Set<Double> uniqueDynamicModMassesForTheSearch, int searchId ) throws Exception {
		for ( Double dynamicModMass : uniqueDynamicModMassesForTheSearch ) {
			DB_Insert_SearchDynamicModMassDAO.getInstance().saveSearchDynamicModMass( searchId, dynamicModMass );
		}	
	}
	
	/**
	 * @param reportedPeptide
	 * @param searchId
	 * @param linkerList
	 * @param linkerListStringForErrorMsgs
	 * @param dropPeptidePSMCutoffValues
	 * @param searchProgramEntryMap
	 * @param filterableReportedPeptideAnnotationTypesOnId
	 * @param filterablePsmAnnotationTypesOnId
	 * @param mapOfScanFilenamesMapsOfScanNumbersToScanIds
	 * @param uniqueDynamicModMassesForTheSearch
	 * @throws Exception
	 */
	private void processSingleReportedPeptide(
			ReportedPeptide reportedPeptide,
			int searchId, 
			List<ILinker> linkerList,
			String linkerListStringForErrorMsgs,
			DropPeptidePSMCutoffValues dropPeptidePSMCutoffValues,
			Map<String, SearchProgramEntry> searchProgramEntryMap,
			Map<Integer, AnnotationTypeDTO> filterableReportedPeptideAnnotationTypesOnId,
			Map<Integer, AnnotationTypeDTO> filterablePsmAnnotationTypesOnId,
			Map<Integer, AnnotationTypeDTO> filterablePsmPerPeptideAnnotationTypesOnId,
			Map<String, Map<Integer,Integer>> mapOfScanFilenamesMapsOfScanNumbersToScanIds,
			Set<Double> uniqueDynamicModMassesForTheSearch,
			Map<String, SearchScanFilenameDTO> scanFilenamesOnPSMsKeyedOnScanFilename
			) throws Exception {
		
		if ( filterablePsmAnnotationTypesOnId == null ) {
			String msg = "filterablePsmAnnotationTypesOnId == null";
			log.error( msg );
			throw new ProxlImporterInteralException(msg);
		}
		if ( filterablePsmAnnotationTypesOnId.isEmpty() ) {
			String msg = "filterablePsmAnnotationTypesOnId.isEmpty() ";
			log.error( msg );
			throw new ProxlImporterInteralException(msg);
		}
		if ( DropPeptideAndOrPSMForCutoffs.getInstance()
				.dropPeptideForCmdLineCutoffs( reportedPeptide, dropPeptidePSMCutoffValues ) ) {
			DroppedPeptideCount.incrementDroppedPeptideCount();
			return;  // EARLY EXIT  to skip to next record
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
		boolean noProteinMappings = false;
		ProcessLinkTypeCrosslink.GetCrosslinkProteinMappingsResult getCrosslinkProteinMappingsResult = null;
		ProcessLinkTypeLooplink.GetLooplinkProteinMappingsResult getLooplinkProteinMappingsResult = null;
		GetProteinMappings_For_XML_LinkType_Unlinked_Result getProteinMappings_For_XML_LinkType_Unlinked_Result = null;
		//   Get Peptide to Protein Mappings per link type
		//     The PeptideDTO is saved to the DB in this step since used for Protein Mappings
		if ( linkTypeNumber == XLinkUtils.TYPE_CROSSLINK ) {
			getCrosslinkProteinMappingsResult =
					ProcessLinkTypeCrosslink.getInstance()
					.getCrosslinkProteinMappings( 
							reportedPeptide, 				// from XML input 
							linkerList, 
							linkerListStringForErrorMsgs );
			if ( getCrosslinkProteinMappingsResult.isNoProteinMappings() ) {
				noProteinMappings = true;
			}
		} else if ( linkTypeNumber == XLinkUtils.TYPE_LOOPLINK ) {
			getLooplinkProteinMappingsResult =
					ProcessLinkTypeLooplink.getInstance()
					.getLooplinkroteinMappings( 
							reportedPeptide, 				// from XML input 
							linkerList, 
							linkerListStringForErrorMsgs );
			if ( getLooplinkProteinMappingsResult.isNoProteinMappings() ) {
				noProteinMappings = true;
			}
		} else if ( linkTypeNumber == XLinkUtils.TYPE_UNLINKED ) {
			getProteinMappings_For_XML_LinkType_Unlinked_Result =
					getProteinMappings_For_XML_LinkType_Unlinked( reportedPeptide, linkerList, linkerListStringForErrorMsgs) ;
			if ( getProteinMappings_For_XML_LinkType_Unlinked_Result.noProteinMappings ) {
				noProteinMappings = true;
			}
			if ( getProteinMappings_For_XML_LinkType_Unlinked_Result.getDimerProteinMappingsResult != null ) {
				//  getProteinMappings_For_XML_LinkType_Unlinked(...) has returned Dimer data so
				//  Reported Peptide is a Dimer for Proxl processing:   update linkTypeNumber to Dimer
				linkTypeNumber = XLinkUtils.TYPE_DIMER;
			}
		} else {
			String msg = "Link Type name '" + linkTypeName + "' is not recognized for reported peptpide: " + reportedPeptide.getReportedPeptideString();
			log.error( msg );
			throw new ProxlImporterDataException(msg);
		}
		//  If no proteins Mapped, skip processing this reported peptide
		if ( noProteinMappings ) {
			//  No Proteins Mapped, so skip processing this reported peptide
			log.warn( "No Mapped Proteins for this reported peptide so not inserting Reported Peptide or PSMs: " + 
					reportedPeptide.getReportedPeptideString() );
			return;  // EARLY EXIT  to skip to next record
		}
		//  Retrieves reported_peptide record or inserts it if not in the database.
		ReportedPeptideDTO reportedPeptideDTO =
				ReportedPeptideDAO_Importer.getInstance().getReportedPeptideDTO_OrSave( reportedPeptideString );
		int reportedPeptideId = reportedPeptideDTO.getId();
		SearchReportedPeptideDTO searchReportedPeptideDTO = new SearchReportedPeptideDTO(); 
		searchReportedPeptideDTO.setSearchId( searchId );
		searchReportedPeptideDTO.setReportedPeptideId( reportedPeptideId );
		searchReportedPeptideDTO.setLinkType( linkTypeNumber );
		DB_Insert_SearchReportedPeptideDAO.getInstance().saveToDatabaseIgnoreDuplicates( searchReportedPeptideDTO );
		List<SearchReportedPeptideAnnotationDTO> searchReportedPeptideFilterableAnnotationDTOList = 
				SaveSearchReportedPeptideAnnotations.getInstance().saveReportedPeptideAnnotations( reportedPeptide, searchId, reportedPeptideDTO.getId(), searchProgramEntryMap, filterableReportedPeptideAnnotationTypesOnId );
		List<PerPeptideData> perPeptideDataList = null;
		//  Each of the following will save the srch_rep_pept__peptide (1 or 2 records)
		//     and child records for type and monolinks
		if ( linkTypeNumber == XLinkUtils.TYPE_CROSSLINK ) {
			perPeptideDataList = 
					ProcessLinkTypeCrosslink.getInstance().saveCrosslinkData( 
							reportedPeptideDTO, 
							searchId, 
							getCrosslinkProteinMappingsResult,
							uniqueDynamicModMassesForTheSearch );
		} else if ( linkTypeNumber == XLinkUtils.TYPE_LOOPLINK ) {
			perPeptideDataList = 
					ProcessLinkTypeLooplink.getInstance().saveLooplinkData(
							reportedPeptideDTO, 
							searchId, 
							getLooplinkProteinMappingsResult,
							uniqueDynamicModMassesForTheSearch );
		} else if ( linkTypeNumber == XLinkUtils.TYPE_UNLINKED ) {
			perPeptideDataList = 
						ProcessLinkTypeUnlinkedAsDefinedByProxl.getInstance().saveUnlinkedData(
								reportedPeptideDTO, 
								searchId, 
								getProteinMappings_For_XML_LinkType_Unlinked_Result.getUnlinkedProteinMappingsResult,
								uniqueDynamicModMassesForTheSearch  );
		} else if ( linkTypeNumber == XLinkUtils.TYPE_DIMER ) {
			//  Can now test for DIMER since linkTypeNumber = XLinkUtils.TYPE_DIMER for Dimer in above code.
			perPeptideDataList = 
					ProcessLinkTypeDimerAsDefinedByProxl.getInstance().saveDimerData(
							reportedPeptideDTO, 
							searchId, 
							getProteinMappings_For_XML_LinkType_Unlinked_Result.getDimerProteinMappingsResult,
							uniqueDynamicModMassesForTheSearch  );
		} else {
			String msg = "Link Type name '" + linkTypeName + "' is not recognized for reported peptpide: " + reportedPeptide.getReportedPeptideString();
			log.error( msg );
			throw new ProxlImporterDataException(msg);
		}
		
		PsmStatisticsAndBestValues psmStatisticsAndBestValues =
				savePSMs( 
						reportedPeptide, 
						searchId, 
						mapOfScanFilenamesMapsOfScanNumbersToScanIds, 
						linkTypeNumber, 
						reportedPeptideDTO, 
						perPeptideDataList,
						dropPeptidePSMCutoffValues, 
						searchProgramEntryMap,
						filterablePsmAnnotationTypesOnId,
						filterablePsmPerPeptideAnnotationTypesOnId,
						scanFilenamesOnPSMsKeyedOnScanFilename );
		
		saveUnifiedReportedPeptideAndPsmAndReportedPeptideLookupRecords( 
				searchId,
				reportedPeptideId,
				linkTypeNumber, 
				searchReportedPeptideFilterableAnnotationDTOList,
				psmStatisticsAndBestValues, 
				perPeptideDataList,
				filterableReportedPeptideAnnotationTypesOnId );
	}
	
	/**
	 * @param fastaFileDatabaseId
	 * @param searchId
	 * @param reportedPeptideId
	 * @param linkTypeNumber
	 * @param psmStatisticsAndBestValues
	 * @param searchReportedPeptideFilterableAnnotationDTOList
	 * @param perPeptideDataList
	 * @throws Exception
	 */
	private void saveUnifiedReportedPeptideAndPsmAndReportedPeptideLookupRecords( 
			int searchId,
			int reportedPeptideId,
			int linkTypeNumber,
			List<SearchReportedPeptideAnnotationDTO> searchReportedPeptideFilterableAnnotationDTOList,
			PsmStatisticsAndBestValues psmStatisticsAndBestValues,
			List<PerPeptideData> perPeptideDataList,
			Map<Integer, AnnotationTypeDTO> filterableReportedPeptideAnnotationTypesOnId
			) throws Exception {
		
		//  Create Unified Reported Peptide data and insert to DB if necessary or get id otherwise.
		UnifiedReportedPeptideLookupDTO unifiedReportedPeptideLookupDTO =
				InsertIfNotInDBUnifiedReportedPeptideAndChildren.getInstance().insertIfNotInDBUnifiedReportedPeptideAndChildren( linkTypeNumber, perPeptideDataList );
		boolean allRelatedPeptidesUniqueForSearch = true;
		boolean hasDynamicModifications = false;
		boolean hasMonolinks = false;
		for ( PerPeptideData perPeptideData : perPeptideDataList ) {
			if ( ! perPeptideData.isPeptideIdMapsToOnlyOneProtein() ) {
				allRelatedPeptidesUniqueForSearch = false;
			}
			if ( ! perPeptideData.getSrchRepPeptPeptDynamicModDTOList_Peptide().isEmpty() ) {
				hasDynamicModifications = true;
			}
			if ( ! perPeptideData.getMonolinkPositionList().isEmpty() ) {
				hasMonolinks = true;
			}
		}
		//  Determine statistic for reported peptide
		/////////   One is: Does Reported Peptide pass default cutoffs
		//  peptideMeetsDefaultCutoffs values:
		//           Not Applicable:  No filterable annotations or no default filterable annotations:
		//           No:              Does NOT meet default cutoffs
		//           Yes:             Does meet default cutoffs
		Yes_No__NOT_APPLICABLE_Enum peptideMeetsDefaultCutoffs = Yes_No__NOT_APPLICABLE_Enum.NOT_APPLICABLE;
		for ( SearchReportedPeptideAnnotationDTO searchReportedPeptideAnnotationDTO : searchReportedPeptideFilterableAnnotationDTOList ) {
			AnnotationTypeDTO annotationTypeDTO = filterableReportedPeptideAnnotationTypesOnId.get( searchReportedPeptideAnnotationDTO.getAnnotationTypeId() );
			if ( annotationTypeDTO == null ) {
				String msg = "annotationTypeDTO not found (== null) for AnnotationTypeId: " 
						+ searchReportedPeptideAnnotationDTO.getAnnotationTypeId() 
						+ ", reportedPeptideId: " + reportedPeptideId;
				log.error( msg );
				throw new ProxlImporterInteralException(msg);
			}
			AnnotationTypeFilterableDTO annotationTypeFilterableDTO = annotationTypeDTO.getAnnotationTypeFilterableDTO();
			if ( annotationTypeFilterableDTO == null ) {
				String msg = "annotationTypeFilterableDTO not found (== null) on annotationTypeDTO for AnnotationTypeId: " 
						+ searchReportedPeptideAnnotationDTO.getAnnotationTypeId() 
						+ ", reportedPeptideId: " + reportedPeptideId;
				log.error( msg );
				throw new ProxlImporterInteralException(msg);
			}
			if ( annotationTypeFilterableDTO.isDefaultFilterAtDatabaseLoad() ) {
				//  Found at least one default filter, set peptideMeetsDefaultCutoffs to YES if still NOT_APPLICABLE
				if ( peptideMeetsDefaultCutoffs == Yes_No__NOT_APPLICABLE_Enum.NOT_APPLICABLE ) {
					peptideMeetsDefaultCutoffs = Yes_No__NOT_APPLICABLE_Enum.YES;
				}
				//  Compare value to default
				if ( annotationTypeFilterableDTO.getFilterDirectionType() == FilterDirectionType.ABOVE ) {
					if ( searchReportedPeptideAnnotationDTO.getValueDouble() 
							< annotationTypeFilterableDTO.getDefaultFilterValueAtDatabaseLoad() ) {
						peptideMeetsDefaultCutoffs = Yes_No__NOT_APPLICABLE_Enum.NO;
					}
				} else if ( annotationTypeFilterableDTO.getFilterDirectionType() == FilterDirectionType.BELOW ) {
					if ( searchReportedPeptideAnnotationDTO.getValueDouble() 
							> annotationTypeFilterableDTO.getDefaultFilterValueAtDatabaseLoad() ) {
						peptideMeetsDefaultCutoffs = Yes_No__NOT_APPLICABLE_Enum.NO;
					}
				} else {
					String msg = " Unexpected FilterDirectionType value:  " + annotationTypeFilterableDTO.getFilterDirectionType()
							+ ", for annotationTypeId: " + searchReportedPeptideAnnotationDTO.getAnnotationTypeId();
					log.error( msg );
					throw new ProxlImporterDataException(msg);
				}
			}
		}
		//  Per Search Reported Peptide record
		UnifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DTO unifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DTO =
				new UnifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DTO();
		unifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DTO.setSearchId( searchId );
		unifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DTO.setUnifiedReportedPeptideId( unifiedReportedPeptideLookupDTO.getId() );
		unifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DTO.setReportedPeptideId( reportedPeptideId );
		unifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DTO.setAllRelatedPeptidesUniqueForSearch( allRelatedPeptidesUniqueForSearch );
		unifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DTO.setHasDynamicModifications( hasDynamicModifications );
		unifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DTO.setHasMonolinks( hasMonolinks );
		unifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DTO.setLinkType( linkTypeNumber );
		unifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DTO.setPsmNumAtDefaultCutoff( psmStatisticsAndBestValues.psmCountPassDefaultCutoffs );
		unifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DTO.setPeptideMeetsDefaultCutoffs( peptideMeetsDefaultCutoffs );
		DB_Insert_UnifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DAO.getInstance().saveToDatabase( unifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DTO );
		for ( SearchReportedPeptideAnnotationDTO searchReportedPeptideAnnotationDTO : searchReportedPeptideFilterableAnnotationDTOList ) {
			//   Peptide Annotation Values
			UnifiedRepPep_Search_ReportedPeptide_PeptideValue_Generic_Lookup__DTO unifiedRepPep_Search_ReportedPeptide_PeptideValue_Generic_Lookup__DTO =
					new UnifiedRepPep_Search_ReportedPeptide_PeptideValue_Generic_Lookup__DTO();
			unifiedRepPep_Search_ReportedPeptide_PeptideValue_Generic_Lookup__DTO.setSearchId( searchId );
			unifiedRepPep_Search_ReportedPeptide_PeptideValue_Generic_Lookup__DTO.setUnifiedReportedPeptideId( unifiedReportedPeptideLookupDTO.getId() );
			unifiedRepPep_Search_ReportedPeptide_PeptideValue_Generic_Lookup__DTO.setReportedPeptideId( reportedPeptideId );
			unifiedRepPep_Search_ReportedPeptide_PeptideValue_Generic_Lookup__DTO.setAnnotationTypeId( searchReportedPeptideAnnotationDTO.getAnnotationTypeId() );
			unifiedRepPep_Search_ReportedPeptide_PeptideValue_Generic_Lookup__DTO.setHasDynamicModifications( hasDynamicModifications );
			unifiedRepPep_Search_ReportedPeptide_PeptideValue_Generic_Lookup__DTO.setHasMonolinks( hasMonolinks );
			unifiedRepPep_Search_ReportedPeptide_PeptideValue_Generic_Lookup__DTO.setLinkType(linkTypeNumber);
			unifiedRepPep_Search_ReportedPeptide_PeptideValue_Generic_Lookup__DTO.setPeptideValueForAnnTypeId( searchReportedPeptideAnnotationDTO.getValueDouble() );
			DB_Insert_UnifiedRepPep_Search_ReportedPeptide_PeptideValue_Generic_Lookup__DAO.getInstance().saveToDatabase( unifiedRepPep_Search_ReportedPeptide_PeptideValue_Generic_Lookup__DTO );
		}
		//  Best PSM Annotation Values
		BestPsmAnnotationProcessing bestPsmAnnotationProcessing = psmStatisticsAndBestValues.bestPsmAnnotationProcessing;
		List<UnifiedRepPep_Search_ReportedPeptide_BestPsmValue_Generic_Lookup__DTO> unifiedRepPep_Search_ReportedPeptide_BestPsmValue_Generic_Lookup__DTO_List = 
				bestPsmAnnotationProcessing.getBestPsmValues( unifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DTO );
		for ( UnifiedRepPep_Search_ReportedPeptide_BestPsmValue_Generic_Lookup__DTO unifiedRepPep_Search_ReportedPeptide_BestPsmValue_Generic_Lookup__DTO :
			unifiedRepPep_Search_ReportedPeptide_BestPsmValue_Generic_Lookup__DTO_List ) {
			DB_Insert_UnifiedRepPep_Search_ReportedPeptide_BestPsmValue_Generic_Lookup__DAO.getInstance().saveToDatabase( unifiedRepPep_Search_ReportedPeptide_BestPsmValue_Generic_Lookup__DTO );
		}
	}
	
	/**
	 * 
	 * returned from method getProteinMappings_For_XML_LinkType_Unlinked in this class
	 */
	private static class GetProteinMappings_For_XML_LinkType_Unlinked_Result {
		boolean noProteinMappings;
		ProcessLinkTypeUnlinkedAsDefinedByProxl.GetUnlinkedProteinMappingsResult getUnlinkedProteinMappingsResult;
		ProcessLinkTypeDimerAsDefinedByProxl.GetDimerProteinMappingsResult getDimerProteinMappingsResult;
	}
	
	/**
	 * @param reportedPeptide
	 * @param linkerList
	 * @param linkerListStringForErrorMsgs
	 * @return
	 * @throws Exception
	 */
	private GetProteinMappings_For_XML_LinkType_Unlinked_Result   getProteinMappings_For_XML_LinkType_Unlinked( 
			ReportedPeptide reportedPeptide, 
			List<ILinker> linkerList,
			String linkerListStringForErrorMsgs
			) throws Exception {
		
		GetProteinMappings_For_XML_LinkType_Unlinked_Result getProteinMappings_For_XML_LinkType_Unlinked_Result = new GetProteinMappings_For_XML_LinkType_Unlinked_Result();
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
			getProteinMappings_For_XML_LinkType_Unlinked_Result.getUnlinkedProteinMappingsResult =
					ProcessLinkTypeUnlinkedAsDefinedByProxl.getInstance()
					.getUnlinkedroteinMappings( 
							reportedPeptide, 
							linkerList, 
							linkerListStringForErrorMsgs );
			if ( getProteinMappings_For_XML_LinkType_Unlinked_Result.getUnlinkedProteinMappingsResult.isNoProteinMappings() ) {
				getProteinMappings_For_XML_LinkType_Unlinked_Result.noProteinMappings = true;
			}
		} else {
			//  Proxl internal "Dimer" is 2 peptides that is not a crosslink
			getProteinMappings_For_XML_LinkType_Unlinked_Result.getDimerProteinMappingsResult =
					ProcessLinkTypeDimerAsDefinedByProxl.getInstance()
					.getDimerProteinMappings( 
							reportedPeptide, 
							linkerList, 
							linkerListStringForErrorMsgs );
			if ( getProteinMappings_For_XML_LinkType_Unlinked_Result.getDimerProteinMappingsResult.isNoProteinMappings() ) {
				getProteinMappings_For_XML_LinkType_Unlinked_Result.noProteinMappings = true;
			}
		}
		return getProteinMappings_For_XML_LinkType_Unlinked_Result;
	}
	
	/**
	 * @param reportedPeptide
	 * @param searchId
	 * @param mapOfScanFilenamesMapsOfScanNumbersToScanIds
	 * @param linkTypeNumber
	 * @param reportedPeptideDTO
	 * @param dropPeptidePSMCutoffValues
	 * @param searchProgramEntryMap
	 * @throws ProxlImporterDataException
	 * @throws Exception
	 */
	private PsmStatisticsAndBestValues savePSMs( 
			ReportedPeptide reportedPeptide, 
			int searchId, 
			Map<String, Map<Integer,Integer>> mapOfScanFilenamesMapsOfScanNumbersToScanIds, 
			int linkTypeNumber, 
			ReportedPeptideDTO reportedPeptideDTO, 
			List<PerPeptideData> perPeptideDataList,
			DropPeptidePSMCutoffValues dropPeptidePSMCutoffValues,
			Map<String, SearchProgramEntry> searchProgramEntryMap,
			Map<Integer, AnnotationTypeDTO> filterablePsmAnnotationTypesOnId,
			Map<Integer, AnnotationTypeDTO> filterablePsmPerPeptideAnnotationTypesOnId,
			Map<String, SearchScanFilenameDTO> scanFilenamesOnPSMsKeyedOnScanFilename
			) throws ProxlImporterDataException, Exception {
		
		if ( filterablePsmAnnotationTypesOnId == null ) {
			String msg = "filterablePsmAnnotationTypesOnId == null";
			log.error( msg );
			throw new ProxlImporterInteralException(msg);
		}
		if ( filterablePsmAnnotationTypesOnId.isEmpty() ) {
			String msg = "filterablePsmAnnotationTypesOnId.isEmpty() ";
			log.error( msg );
			throw new ProxlImporterInteralException(msg);
		}
		
		//  copy PerPeptideData perPeptideDataList to map
		Map<String, PerPeptideData> perPeptideDataMap_Key_UniqueId = new HashMap<>();
		for ( PerPeptideData perPeptideData : perPeptideDataList ) {
			if ( StringUtils.isNotEmpty( perPeptideData.getUniqueId() ) ) {
				perPeptideDataMap_Key_UniqueId.put( perPeptideData.getUniqueId(), perPeptideData );
			}
		}
		
		
		Psms psms =	reportedPeptide.getPsms();
		List<Psm> psmList = psms.getPsm();
		
		SavePsmAnnotations savePsmAnnotations = SavePsmAnnotations.getInstance( searchProgramEntryMap, filterablePsmAnnotationTypesOnId );
		
		BestPsmAnnotationProcessing bestPsmAnnotationProcessing = BestPsmAnnotationProcessing.getInstance( filterablePsmAnnotationTypesOnId );
		
		SavePsmPerPeptideAnnotations savePsmPerPeptideAnnotations = SavePsmPerPeptideAnnotations.getInstance( searchProgramEntryMap, filterablePsmPerPeptideAnnotationTypesOnId );
		
		int psmCountPassDefaultCutoffs = 0;
		boolean saveAnyPSMs = false;
		for ( Psm psm : psmList ) {
			if ( DropPeptideAndOrPSMForCutoffs.getInstance()
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
							psm,
							scanFilenamesOnPSMsKeyedOnScanFilename );
			List<PsmAnnotationDTO> currentPsm_psmAnnotationDTO_Filterable_List = 
					savePsmAnnotations.savePsmAnnotations( psm, psmDTO );
			//  Save PSM Lookup version
			for ( PsmAnnotationDTO psmAnnotationDTO : currentPsm_psmAnnotationDTO_Filterable_List ) {
				PsmFilterableAnnotationGenericLookupDTO psmFilterableAnnotationGenericLookupDTO = new PsmFilterableAnnotationGenericLookupDTO();
				psmFilterableAnnotationGenericLookupDTO.setSearchId( searchId );
				psmFilterableAnnotationGenericLookupDTO.setReportedPeptideId( reportedPeptideDTO.getId() );
				psmFilterableAnnotationGenericLookupDTO.setType( linkTypeNumber );
				psmFilterableAnnotationGenericLookupDTO.setValueDouble( psmAnnotationDTO.getValueDouble() );
				psmFilterableAnnotationGenericLookupDTO.setValueString( psmAnnotationDTO.getValueString() );
				psmFilterableAnnotationGenericLookupDTO.setPsmAnnotationId( psmAnnotationDTO.getId() );
				psmFilterableAnnotationGenericLookupDTO.setPsmId( psmDTO.getId() );
				psmFilterableAnnotationGenericLookupDTO.setAnnotationTypeId( psmAnnotationDTO.getAnnotationTypeId() );
				DB_Insert_PsmFilterableAnnotationGenericLookupDAO.getInstance().saveToDatabase( psmFilterableAnnotationGenericLookupDTO );
			}
			boolean doesPsmPassDefaultCutoffs = 
					doesPsmPassDefaultCutoffs( 
							currentPsm_psmAnnotationDTO_Filterable_List, 
							filterablePsmAnnotationTypesOnId );
			if ( doesPsmPassDefaultCutoffs ) {
				psmCountPassDefaultCutoffs++;
			}
			bestPsmAnnotationProcessing.updateForCurrentPsmAnnotationData( currentPsm_psmAnnotationDTO_Filterable_List );
			
			// process per_peptide_annotations under psm if exist
			processPsmPerPeptideAnnotationsForPSM( psm, perPeptideDataMap_Key_UniqueId, savePsmPerPeptideAnnotations, psmDTO, reportedPeptide );
			
			saveAnyPSMs = true;
		}
		if ( ! saveAnyPSMs ) {
			String msg = "No PSMs saved for this reported peptide: " + 
					reportedPeptide.getReportedPeptideString();
			log.error( msg );
			throw new ProxlImporterInteralException(msg);
		}
		PsmStatisticsAndBestValues psmStatisticsAndBestValues = new PsmStatisticsAndBestValues();
		psmStatisticsAndBestValues.psmCountPassDefaultCutoffs = psmCountPassDefaultCutoffs;
		psmStatisticsAndBestValues.bestPsmAnnotationProcessing = bestPsmAnnotationProcessing;
		return psmStatisticsAndBestValues;
	}
	
	/**
	 * process per_peptide_annotations under psm if exist
	 * 
	 * @param psm
	 * @param perPeptideDataMap_Key_UniqueId
	 * @param reportedPeptide
	 * @throws ProxlImporterInteralException 
	 */
	private void processPsmPerPeptideAnnotationsForPSM( 
			Psm psm, 
			Map<String, PerPeptideData> perPeptideDataMap_Key_UniqueId, 
			SavePsmPerPeptideAnnotations savePsmPerPeptideAnnotations,
			PsmDTO psmDTO,
			ReportedPeptide reportedPeptide ) throws Exception {

		//  process per_peptide_annotations under psm if exist
		
		List<PerPeptideAnnotations> perPeptideAnnotationsList = psm.getPerPeptideAnnotations();
		if ( perPeptideAnnotationsList != null && ( ! perPeptideAnnotationsList.isEmpty() ) ) {
			for ( PerPeptideAnnotations perPeptideAnnotations : perPeptideAnnotationsList  ) {
				PsmPeptide psmPeptide = perPeptideAnnotations.getPsmPeptide();
				String psmPeptideUniqueId =	psmPeptide.getUniqueId();
				PerPeptideData perPeptideData =  perPeptideDataMap_Key_UniqueId.get( psmPeptideUniqueId );
				if ( perPeptideData == null ) {
					String psmScanNumber = "";
					if ( psm.getScanNumber() != null ) {
						psmScanNumber = ", psm scan number: " + psm.getScanNumber();
					}
					String msg = "peptide unique_id found for psm_peptide. unique_id: " + psmPeptideUniqueId
							+ psmScanNumber
							+ ", reported peptide: " + reportedPeptide.getReportedPeptideString();
					log.error( msg );
					throw new ProxlImporterInteralException(msg);
				}
				
				savePsmPerPeptideAnnotations.savePsmPerPeptideAnnotations( psmPeptide, perPeptideData, psmDTO );
			}
		}
	}
	
	/**
	 * returned from method savePSMs
	 *
	 */
	private static class PsmStatisticsAndBestValues {
		int psmCountPassDefaultCutoffs = 0;
		BestPsmAnnotationProcessing bestPsmAnnotationProcessing;
	}
	
	/**
	 * Does Psm Pass DefaultCutoffs
	 * 
	 * @param currentPsm_psmAnnotationDTO_Filterable_List
	 * @param bestPsmAnnotationDTO_KeyedOn_AnnotationTypeId
	 * @param filterablePsmAnnotationTypesOnId
	 * @return
	 * @throws ProxlImporterDataException
	 * @throws ProxlImporterInteralException
	 */
	private boolean doesPsmPassDefaultCutoffs( 
			List<PsmAnnotationDTO> currentPsm_psmAnnotationDTO_Filterable_List,
			Map<Integer, AnnotationTypeDTO> filterablePsmAnnotationTypesOnId
			) throws ProxlImporterDataException, ProxlImporterInteralException {
		
		if ( filterablePsmAnnotationTypesOnId == null ) {
			String msg = "filterablePsmAnnotationTypesOnId == null";
			log.error( msg );
			throw new ProxlImporterInteralException(msg);
		}
		if ( filterablePsmAnnotationTypesOnId.isEmpty() ) {
			String msg = "filterablePsmAnnotationTypesOnId.isEmpty() ";
			log.error( msg );
			throw new ProxlImporterInteralException(msg);
		}
		boolean doesPsmPassDefaultCutoffs = true;
		for ( PsmAnnotationDTO currentPsm_psmAnnotationDTO_Filterable : currentPsm_psmAnnotationDTO_Filterable_List ) {
			Integer currentPsm_annotationTypeId = currentPsm_psmAnnotationDTO_Filterable.getAnnotationTypeId();
			AnnotationTypeDTO currentPsm_annotationType =   filterablePsmAnnotationTypesOnId.get( currentPsm_annotationTypeId );
			if ( currentPsm_annotationType == null ) {
				String msg = "currentPsm_annotationType == null for currentPsm_annotationTypeId: " + currentPsm_annotationTypeId;
				log.error( msg );
				throw new ProxlImporterInteralException(msg);
			}
			AnnotationTypeFilterableDTO currentPsm_AnnotationTypeFilterableDTO = currentPsm_annotationType.getAnnotationTypeFilterableDTO();
			if ( currentPsm_AnnotationTypeFilterableDTO == null ) {
				String msg = "currentPsm_AnnotationTypeFilterableDTO == null for currentPsm_annotationTypeId: " + currentPsm_annotationTypeId;
				log.error( msg );
				throw new ProxlImporterInteralException(msg);
			}
			/////////   Does PSM pass default cutoffs
			if ( currentPsm_AnnotationTypeFilterableDTO.isDefaultFilter() ) {
				if ( currentPsm_AnnotationTypeFilterableDTO.getFilterDirectionType() == FilterDirectionType.ABOVE ) {
					if ( currentPsm_psmAnnotationDTO_Filterable.getValueDouble() 
							< currentPsm_AnnotationTypeFilterableDTO.getDefaultFilterValueAtDatabaseLoad() ) {
						doesPsmPassDefaultCutoffs = false;
					}
				} else if ( currentPsm_AnnotationTypeFilterableDTO.getFilterDirectionType() == FilterDirectionType.BELOW ) {
					if ( currentPsm_psmAnnotationDTO_Filterable.getValueDouble() 
							> currentPsm_AnnotationTypeFilterableDTO.getDefaultFilterValueAtDatabaseLoad() ) {
						doesPsmPassDefaultCutoffs = false;
					}
				} else {
					String msg = " Unexpected FilterDirectionType value:  " + currentPsm_AnnotationTypeFilterableDTO.getFilterDirectionType()
							+ ", for currentPsm_annotationTypeId: " + currentPsm_annotationTypeId;
					log.error( msg );
					throw new ProxlImporterDataException(msg);
				}
			}
		}	
		return doesPsmPassDefaultCutoffs;
	}
	
}
