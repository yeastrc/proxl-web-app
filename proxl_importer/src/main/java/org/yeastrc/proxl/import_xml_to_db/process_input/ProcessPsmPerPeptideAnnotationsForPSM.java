package org.yeastrc.proxl.import_xml_to_db.process_input;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;import org.slf4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_PsmPerPeptideDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SearchScanFilenameDAO;
import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptPeptideDTO;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterInteralException;
import org.yeastrc.proxl.import_xml_to_db.objects.PerPeptideData;
import org.yeastrc.proxl.import_xml_to_db.objects.ScanFilenameScanNumberScanIdScanFileId_Mapping;
import org.yeastrc.proxl.import_xml_to_db.utils.RoundDecimalFieldsIfNecessary;
import org.yeastrc.proxl_import.api.xml_dto.Psm;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptide;
import org.yeastrc.proxl_import.api.xml_dto.Psm.PerPeptideAnnotations;
import org.yeastrc.proxl_import.api.xml_dto.Psm.PerPeptideAnnotations.PsmPeptide;
import org.yeastrc.xlink.dto.PsmDTO;
import org.yeastrc.xlink.dto.PsmPerPeptideDTO;
import org.yeastrc.xlink.dto.SearchScanFilenameDTO;

/**
 * Process <per_peptide_annotations> under psm if exist
 * 
 * It has more data than just Annotation data.
 * Also process annotations on the <psm_peptide> element
 *
 */
public class ProcessPsmPerPeptideAnnotationsForPSM {

	private static final Logger log = LoggerFactory.getLogger(  ProcessPsmPerPeptideAnnotationsForPSM.class );
	/**
	 * private constructor
	 */
	private ProcessPsmPerPeptideAnnotationsForPSM(){}
	public static ProcessPsmPerPeptideAnnotationsForPSM getInstance() {
		return new ProcessPsmPerPeptideAnnotationsForPSM();
	}
	

	/**
	 * process <per_peptide_annotations> under psm if exist
	 * It has more data than just Annotation data.
	 * Also process annotations on the <psm_peptide> element
	 * 
	 * @param psm
	 * @param perPeptideDataMap_Key_UniqueId
	 * @param savePsmPerPeptideAnnotations
	 * @param psmDTO
	 * @param mapOfScanFilenamesMapsOfScanNumbersToScanIds
	 * @param reportedPeptide - For Error Reporting
	 * @throws Exception
	 */
	public void processPsmPerPeptideAnnotationsForPSM( 
			Psm psm, 
			Map<String, PerPeptideData> perPeptideDataMap_Key_UniqueId, 
			SavePsmPerPeptideAnnotations savePsmPerPeptideAnnotations,
			PsmDTO psmDTO,
			Map<String, ScanFilenameScanNumberScanIdScanFileId_Mapping> mapOfScanFilenamesMapsOfScanNumbersToScanIds,
			Map<String, SearchScanFilenameDTO> scanFilenamesOnPSMsKeyedOnScanFilename,
			ReportedPeptide reportedPeptide // For Error Reporting
			) throws Exception {

		//  process per_peptide_annotations (More than just annotations now) under psm if exist
		
		PerPeptideAnnotations perPeptideAnnotations = psm.getPerPeptideAnnotations();
		if ( perPeptideAnnotations != null ) {
			List<PsmPeptide> psmPeptideList = perPeptideAnnotations.getPsmPeptide();
			for ( PsmPeptide psmPeptide : psmPeptideList ) {
				String psmPeptideUniqueId =	psmPeptide.getUniqueId();
				PerPeptideData perPeptideData =  perPeptideDataMap_Key_UniqueId.get( psmPeptideUniqueId );
				if ( perPeptideData == null ) {
					String psmScanNumber = "";
					if ( psm.getScanNumber() != null ) {
						psmScanNumber = ", psm scan number: " + psm.getScanNumber();
					}
					String msg = "peptide unique_id NOT found for psm_peptide. unique_id: " + psmPeptideUniqueId
							+ psmScanNumber
							+ ", reported peptide: " + reportedPeptide.getReportedPeptideString();
					log.error( msg );
					throw new ProxlImporterInteralException(msg);
				}
				
				populateAndSave_psmPerPeptide( psmPeptide, perPeptideData, psmDTO, mapOfScanFilenamesMapsOfScanNumbersToScanIds, scanFilenamesOnPSMsKeyedOnScanFilename );
				
				//  Save Filterable and Descriptive Annotation Data
				savePsmPerPeptideAnnotations.savePsmPerPeptideAnnotations( psmPeptide, perPeptideData, psmDTO );
			}
		}
	}
	
	/**
	 * @param psmPeptide
	 * @param perPeptideData
	 * @param mapOfScanFilenamesMapsOfScanNumbersToScanIds
	 * @throws Exception 
	 */
	private void populateAndSave_psmPerPeptide( 
			PsmPeptide psmPeptide,
			PerPeptideData perPeptideData,
			PsmDTO psmDTO,
			Map<String, ScanFilenameScanNumberScanIdScanFileId_Mapping> mapOfScanFilenamesMapsOfScanNumbersToScanIds,
			Map<String, SearchScanFilenameDTO> scanFilenamesOnPSMsKeyedOnScanFilename
			) throws Exception {
		
		if ( psmPeptide.getLinkerMass() == null && StringUtils.isEmpty( psmPeptide.getScanFileName() ) && psmPeptide.getScanNumber() == null ) {
			//  No Data in any property that would be stored in this record so exit
			return; // EARLY RETURN
		}

		//  TODO Write code to store these attributes
		if ( psmPeptide.getLinkerMass() != null || StringUtils.isNotEmpty( psmPeptide.getScanFileName() ) || psmPeptide.getScanNumber() != null ) {
			String msg = "PsmPeptide has one of LinkerMass, ScanFilename, or ScanNumber Set and they are currently not being processed so this is an Internal Error.  Code to handle this is in progress";
			log.error( msg );
			throw new ProxlImporterInteralException(msg);
		}

		SrchRepPeptPeptideDTO srchRepPeptPeptideDTO = perPeptideData.getSrchRepPeptPeptideDTO();
		String scanFilename = psmPeptide.getScanFileName();
		if ( "".equals( scanFilename ) ) {
			scanFilename = null; // Is empty string so set to null
		}
		
		
		PsmPerPeptideDTO psmPerPeptideDTO = new PsmPerPeptideDTO();
		
		psmPerPeptideDTO.setPsmId( psmDTO.getId() );
		psmPerPeptideDTO.setSrchRepPeptPeptideId( srchRepPeptPeptideDTO.getId() );

		if ( psmPeptide.getScanNumber() != null ) {
			psmPerPeptideDTO.setScanNumber( psmPeptide.getScanNumber().intValue() );
		}
		
		if ( psmPeptide.getLinkerMass() != null ) {
			BigDecimal linkerMass = RoundDecimalFieldsIfNecessary.roundDecimalFieldsIfNecessary_18comma9( psmPeptide.getLinkerMass() );
			psmPerPeptideDTO.setLinkerMass( linkerMass );
		}
		
		//  These may be set or changed as process info from processing of uploaded scan file(s)

		//  Get Scan File Id
		
		Integer scanFileId = null;
		
		if ( psmPeptide.getScanNumber() != null
				&& mapOfScanFilenamesMapsOfScanNumbersToScanIds != null 
				&& ( ! mapOfScanFilenamesMapsOfScanNumbersToScanIds.isEmpty() ) ) {
			//  Have scan files so map the scan number to the scan id and put the scan id on the psmDTO
			ScanFilenameScanNumberScanIdScanFileId_Mapping scanFilenameScanNumberScanIdScanFileId_Mapping = null;
			String scanFilenameFromMapEntry = null;
			if ( mapOfScanFilenamesMapsOfScanNumbersToScanIds.size() == 1 ) {
				//  If only one scan file, just use the map entry
				Map.Entry<String, ScanFilenameScanNumberScanIdScanFileId_Mapping> mapOfScanFilenamesMapsOfScanNumbersToScanIdsEntry =
						mapOfScanFilenamesMapsOfScanNumbersToScanIds.entrySet().iterator().next();
				scanFilenameFromMapEntry = mapOfScanFilenamesMapsOfScanNumbersToScanIdsEntry.getKey();
				scanFilenameScanNumberScanIdScanFileId_Mapping = mapOfScanFilenamesMapsOfScanNumbersToScanIdsEntry.getValue();
				//  Save scan filename for display on PSMs
				scanFilename = scanFilenameFromMapEntry;
			} else {
				//  More than one scan file so get the scan file entry for this PSM
				scanFilenameScanNumberScanIdScanFileId_Mapping = mapOfScanFilenamesMapsOfScanNumbersToScanIds.get( psmPeptide.getScanFileName() );
				if ( scanFilenameScanNumberScanIdScanFileId_Mapping == null ) {
					String msg = "No Scan Numbers to Scan Ids Mapping for Scan File: " + psmPeptide.getScanFileName();
					log.error( msg );
					throw new ProxlImporterDataException(msg);
				}
			}
			
			scanFileId = scanFilenameScanNumberScanIdScanFileId_Mapping.getScanFileId();
			
			Map<Integer,Integer> mapOfScanNumbersToScanIds = scanFilenameScanNumberScanIdScanFileId_Mapping.getMapOfScanNumbersToScanIds();

			int scanNumberInPSM = psmPeptide.getScanNumber().intValue();
			Integer scanId = mapOfScanNumbersToScanIds.get( scanNumberInPSM );
			if ( scanId == null ) {
				String msg = "No Scan Id Mapping for Scan Number: " + scanNumberInPSM
						+ ", psm_peptide Scan File: " + psmPeptide.getScanFileName()
						+ ", Scan Filename From command line: " + scanFilenameFromMapEntry;
				log.error( msg );
				throw new ProxlImporterDataException(msg);
			}
			psmPerPeptideDTO.setScanId( scanId );
		}

		if ( StringUtils.isNotEmpty( scanFilename ) ) {
			SearchScanFilenameDTO searchScanFilenameDTO = scanFilenamesOnPSMsKeyedOnScanFilename.get( scanFilename );
			if ( searchScanFilenameDTO == null ) {
				searchScanFilenameDTO = new SearchScanFilenameDTO();
				searchScanFilenameDTO.setSearchId( psmDTO.getSearchId() );
				searchScanFilenameDTO.setFilename( scanFilename );
				if ( scanFileId != null ) {
					searchScanFilenameDTO.setScanFileId( scanFileId ); //  May be set, may be null
				}
				DB_Insert_SearchScanFilenameDAO.getInstance().saveToDatabase( searchScanFilenameDTO );
				scanFilenamesOnPSMsKeyedOnScanFilename.put( scanFilename, searchScanFilenameDTO );
			}
			psmPerPeptideDTO.setSearchScanFilenameId( searchScanFilenameDTO.getId() );
		}
		
		DB_Insert_PsmPerPeptideDAO.getInstance().saveToDatabase( psmPerPeptideDTO );
	}
	
}
