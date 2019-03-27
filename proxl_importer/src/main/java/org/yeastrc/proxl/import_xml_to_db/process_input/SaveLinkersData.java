package org.yeastrc.proxl.import_xml_to_db.process_input;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_LinkerPerSearchCleavedCrosslinkMassDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_LinkerPerSearchCrosslinkMassDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_LinkerPerSearchMonolinkMassDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SearchLinkerDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SearchLinkerPerSideDefinitionDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SearchLinkerPerSideLinkableProteinTerminiDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SearchLinkerPerSideLinkableResiduesDAO;
import org.yeastrc.proxl.import_xml_to_db.dto.SearchDTO_Importer;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterInteralException;
import org.yeastrc.proxl_import.api.xml_dto.CleavedCrosslinkMass;
import org.yeastrc.proxl_import.api.xml_dto.CrosslinkMass;
import org.yeastrc.proxl_import.api.xml_dto.CrosslinkMasses;
import org.yeastrc.proxl_import.api.xml_dto.LinkedEnd;
import org.yeastrc.proxl_import.api.xml_dto.LinkedEnds;
import org.yeastrc.proxl_import.api.xml_dto.Linker;
import org.yeastrc.proxl_import.api.xml_dto.Linkers;
import org.yeastrc.proxl_import.api.xml_dto.MonolinkMass;
import org.yeastrc.proxl_import.api.xml_dto.MonolinkMasses;
import org.yeastrc.proxl_import.api.xml_dto.ProteinTermini;
import org.yeastrc.proxl_import.api.xml_dto.ProteinTerminus;
import org.yeastrc.proxl_import.api.xml_dto.ProteinTerminusDesignation;
import org.yeastrc.proxl_import.api.xml_dto.ProxlInput;
import org.yeastrc.proxl_import.api.xml_dto.Residues;
import org.yeastrc.xlink.dto.LinkerPerSearchCleavedCrosslinkMassDTO;
import org.yeastrc.xlink.dto.LinkerPerSearchCrosslinkMassDTO;
import org.yeastrc.xlink.dto.LinkerPerSearchMonolinkMassDTO;
import org.yeastrc.xlink.dto.SearchLinkerDTO;
import org.yeastrc.xlink.dto.SearchLinkerPerSideDefinitionDTO;
import org.yeastrc.xlink.dto.SearchLinkerPerSideLinkableProteinTerminiDTO;
import org.yeastrc.xlink.dto.SearchLinkerPerSideLinkableResiduesDTO;
import org.yeastrc.xlink.enum_classes.SearchLinkerProteinTerminusType;
import org.yeastrc.xlink.linker_data_processing_base.linker_db_data_per_search.LinkersDBDataSingleSearchSingleLinker;
import org.yeastrc.xlink.linker_data_processing_base.linker_db_data_per_search.LinkersDBDataSingleSearchRoot;
import org.yeastrc.xlink.linker_data_processing_base.linker_db_data_per_search.SearchLinkerPerSideDefinitionObj;
import org.yeastrc.xlink.linker_data_processing_base.linker_db_data_per_search.SearchLinkerPerSideLinkableProteinTerminiObj;

/**
 * 
 *
 */
public class SaveLinkersData {

	private static final Logger log = Logger.getLogger( SavePerPeptideData.class );
	/**
	 * private constructor
	 */
	private SaveLinkersData(){}
	public static SaveLinkersData getInstance() { return new SaveLinkersData(); }

	/**
	 * save linker data for the search
	 * 
	 * @param proxlInput
	 * @param searchDTO
	 * @throws ProxlImporterDataException
	 * @throws Exception
	 */
	public LinkersDBDataSingleSearchRoot saveLinkersData( ProxlInput proxlInput, SearchDTO_Importer searchDTO ) throws ProxlImporterDataException, Exception {
		
		LinkersDBDataSingleSearchRoot linkersDBDataSingleSearchRoot = new LinkersDBDataSingleSearchRoot();

		// Save Linker mapping for search
		Linkers proxlInputLinkers = proxlInput.getLinkers();
		if ( proxlInputLinkers == null ) {
			String msg = "at least one linker is required";
			log.error( msg );
			throw new ProxlImporterDataException(msg);
		}
		List<Linker> proxlInputLinkerList = proxlInputLinkers.getLinker();
		if ( proxlInputLinkerList.isEmpty() ) {
			String msg = "at least one linker is required";
			log.error( msg );
			throw new ProxlImporterDataException(msg);
		}
		
		List<LinkersDBDataSingleSearchSingleLinker> linkersDBDataSingleSearchPerLinkerList = new ArrayList<>( proxlInputLinkerList.size() );
		linkersDBDataSingleSearchRoot.setLinkersDBDataSingleSearchPerLinkerList( linkersDBDataSingleSearchPerLinkerList );

		for ( Linker proxlInputLinkerItem : proxlInputLinkerList ) {
			
			LinkersDBDataSingleSearchSingleLinker linkersDBDataSingleSearchPerLinker = new LinkersDBDataSingleSearchSingleLinker();
			linkersDBDataSingleSearchPerLinkerList.add( linkersDBDataSingleSearchPerLinker );
			
			String linkerAbbr = proxlInputLinkerItem.getName();
			
			SearchLinkerDTO searchLinkerDTO = new SearchLinkerDTO();
			searchLinkerDTO.setSearchId( searchDTO.getId() );
			searchLinkerDTO.setLinkerAbbr( linkerAbbr );
			if ( proxlInputLinkerItem.getSpacerArmLength() != null ) {
				searchLinkerDTO.setSpacerArmLength( proxlInputLinkerItem.getSpacerArmLength().doubleValue() );
				searchLinkerDTO.setSpacerArmLengthString( proxlInputLinkerItem.getSpacerArmLength().toString() );
			}
			DB_Insert_SearchLinkerDAO.getInstance().saveToDatabase( searchLinkerDTO );
			
			linkersDBDataSingleSearchPerLinker.setSearchLinkerDTO( searchLinkerDTO );
			
			saveMonolinkMasses( proxlInputLinkerItem, searchLinkerDTO, searchDTO, linkersDBDataSingleSearchPerLinker );
			saveCrosslinkMasses( proxlInputLinkerItem, searchLinkerDTO, searchDTO, linkersDBDataSingleSearchPerLinker );
			saveLinkedEnds( proxlInputLinkerItem, searchLinkerDTO, linkersDBDataSingleSearchPerLinker );
		}
		
		return linkersDBDataSingleSearchRoot;
	}
	
	/**
	 * @param proxlInputLinkerItem
	 * @param linkerDTO
	 * @param searchDTO
	 * @throws Exception
	 */
	private void saveMonolinkMasses( 
			Linker proxlInputLinkerItem, SearchLinkerDTO searchLinkerDTO, SearchDTO_Importer searchDTO, LinkersDBDataSingleSearchSingleLinker linkersDBDataSingleSearchPerLinker ) throws Exception {
		
		MonolinkMasses monolinkMasses = proxlInputLinkerItem.getMonolinkMasses();
		if ( monolinkMasses == null ) {
			return;  //  EARLY RETURN
		}
		List<MonolinkMass> monolinkMassList = monolinkMasses.getMonolinkMass();
		if ( monolinkMassList == null ) {
			return;  //  EARLY RETURN
		}
		List<LinkerPerSearchMonolinkMassDTO> linkerPerSearchMonolinkMassDTOList = new ArrayList<>( monolinkMassList.size() );
		linkersDBDataSingleSearchPerLinker.setLinkerPerSearchMonolinkMassDTOList( linkerPerSearchMonolinkMassDTOList );
		
		for ( MonolinkMass monolinkMass : monolinkMassList ) {
			LinkerPerSearchMonolinkMassDTO linkerPerSearchMonolinkMassDTO = new LinkerPerSearchMonolinkMassDTO();
			linkerPerSearchMonolinkMassDTO.setSearchLinkerId( searchLinkerDTO.getId() );
			linkerPerSearchMonolinkMassDTO.setSearchId(  searchDTO.getId() );
			linkerPerSearchMonolinkMassDTO.setMonolinkMassDouble( monolinkMass.getMass().doubleValue() );
			linkerPerSearchMonolinkMassDTO.setMonolinkMassString( monolinkMass.getMass().toString() );
			DB_Insert_LinkerPerSearchMonolinkMassDAO.getInstance().save( linkerPerSearchMonolinkMassDTO );
			linkerPerSearchMonolinkMassDTOList.add( linkerPerSearchMonolinkMassDTO );
		}
	}
	
	/**
	 * @param proxlInputLinkerItem
	 * @param linkerDTO
	 * @param searchDTO
	 * @throws Exception
	 */
	private void saveCrosslinkMasses(
			Linker proxlInputLinkerItem, SearchLinkerDTO searchLinkerDTO, SearchDTO_Importer searchDTO, LinkersDBDataSingleSearchSingleLinker linkersDBDataSingleSearchPerLinker ) throws Exception {
		
		CrosslinkMasses crosslinkMasses = proxlInputLinkerItem.getCrosslinkMasses();
		if ( crosslinkMasses == null ) {
			return;  //  EARLY RETURN
		}
		{
			List<CrosslinkMass> crosslinkMassList = crosslinkMasses.getCrosslinkMass();
			if ( crosslinkMassList != null && ( ! crosslinkMassList.isEmpty() ) ) {
				
				List<LinkerPerSearchCrosslinkMassDTO> linkerPerSearchCrosslinkMassDTOList = new ArrayList<>( crosslinkMassList.size() );
				linkersDBDataSingleSearchPerLinker.setLinkerPerSearchCrosslinkMassDTOList( linkerPerSearchCrosslinkMassDTOList );
				
				for ( CrosslinkMass crosslinkMass : crosslinkMassList ) {
					LinkerPerSearchCrosslinkMassDTO linkerPerSearchCrosslinkMassDTO = new LinkerPerSearchCrosslinkMassDTO();
					linkerPerSearchCrosslinkMassDTO.setSearchLinkerId( searchLinkerDTO.getId() );
					linkerPerSearchCrosslinkMassDTO.setSearchId(  searchDTO.getId() );
					linkerPerSearchCrosslinkMassDTO.setCrosslinkMassDouble( crosslinkMass.getMass().doubleValue() );
					linkerPerSearchCrosslinkMassDTO.setCrosslinkMassString( crosslinkMass.getMass().toString() );
					linkerPerSearchCrosslinkMassDTO.setChemicalFormula( crosslinkMass.getChemicalFormula() );
					DB_Insert_LinkerPerSearchCrosslinkMassDAO.getInstance().save( linkerPerSearchCrosslinkMassDTO );
				}
			}
		}
		{
			List<CleavedCrosslinkMass> cleavedCrosslinkMassList = crosslinkMasses.getCleavedCrosslinkMass();
			if ( cleavedCrosslinkMassList != null && ( ! cleavedCrosslinkMassList.isEmpty() ) ) {
				
				List<LinkerPerSearchCleavedCrosslinkMassDTO> linkerPerSearchCleavedCrosslinkMassDTOList = new ArrayList<>( cleavedCrosslinkMassList.size() );
				linkersDBDataSingleSearchPerLinker.setLinkerPerSearchCleavedCrosslinkMassDTOList( linkerPerSearchCleavedCrosslinkMassDTOList );
				
				for ( CleavedCrosslinkMass cleavedCrosslinkMass : cleavedCrosslinkMassList ) {
					LinkerPerSearchCleavedCrosslinkMassDTO linkerPerSearchCleavedCrosslinkMassDTO = new LinkerPerSearchCleavedCrosslinkMassDTO();
					linkerPerSearchCleavedCrosslinkMassDTO.setSearchLinkerId( searchLinkerDTO.getId() );
					linkerPerSearchCleavedCrosslinkMassDTO.setSearchId(  searchDTO.getId() );
					linkerPerSearchCleavedCrosslinkMassDTO.setCleavedCrosslinkMassDouble( cleavedCrosslinkMass.getMass().doubleValue() );
					linkerPerSearchCleavedCrosslinkMassDTO.setCleavedCrosslinkMassString( cleavedCrosslinkMass.getMass().toString() );
					linkerPerSearchCleavedCrosslinkMassDTO.setChemicalFormula( cleavedCrosslinkMass.getChemicalFormula() );
					DB_Insert_LinkerPerSearchCleavedCrosslinkMassDAO.getInstance().save( linkerPerSearchCleavedCrosslinkMassDTO );
					linkerPerSearchCleavedCrosslinkMassDTOList.add( linkerPerSearchCleavedCrosslinkMassDTO );
				}
			}
		}
	}

	/**
	 * @param proxlInputLinker
	 * @param searchLinkerDTO
	 * @throws Exception 
	 */
	private void saveLinkedEnds( 
			Linker proxlInputLinker, SearchLinkerDTO searchLinkerDTO, LinkersDBDataSingleSearchSingleLinker linkersDBDataSingleSearchPerLinker) throws Exception {

		// String proxlInputLinkerName = proxlInputLinker.getName();

		LinkedEnds linkedEnds = proxlInputLinker.getLinkedEnds();
		
		if ( linkedEnds != null ) {
			List<LinkedEnd> linkedEndList = linkedEnds.getLinkedEnd();
			
			if ( ! linkedEndList.isEmpty() ) {
				
				List<SearchLinkerPerSideDefinitionObj> searchLinkerPerSideDefinitionObjList = new ArrayList<>( linkedEndList.size() );
				linkersDBDataSingleSearchPerLinker.setSearchLinkerPerSideDefinitionObjList( searchLinkerPerSideDefinitionObjList );
			
				for ( LinkedEnd linkedEnd : linkedEndList ) {
					
					SearchLinkerPerSideDefinitionDTO searchLinkerPerSideDefinitionDTO = new SearchLinkerPerSideDefinitionDTO();
					searchLinkerPerSideDefinitionDTO.setSearchLinkerId( searchLinkerDTO.getId() );
					DB_Insert_SearchLinkerPerSideDefinitionDAO.getInstance().saveToDatabase( searchLinkerPerSideDefinitionDTO );
					
					SearchLinkerPerSideDefinitionObj searchLinkerPerSideDefinitionObj = new SearchLinkerPerSideDefinitionObj();
					searchLinkerPerSideDefinitionObjList.add( searchLinkerPerSideDefinitionObj );
					
					saveLinkedEndProteinTermini( linkedEnd, searchLinkerPerSideDefinitionDTO, searchLinkerPerSideDefinitionObj, proxlInputLinker );
					
					saveLinkedEndResidues( linkedEnd, searchLinkerPerSideDefinitionDTO, searchLinkerPerSideDefinitionObj, proxlInputLinker );
				}
			}
		}
	}
	
	/**
	 * @param linkedEnd
	 * @param searchLinkerPerSideDefinitionDTO
	 * @param proxlInputLinker
	 * @throws ProxlImporterInteralException
	 * @throws Exception
	 */
	private void saveLinkedEndProteinTermini(
			LinkedEnd linkedEnd,
			SearchLinkerPerSideDefinitionDTO searchLinkerPerSideDefinitionDTO,
			SearchLinkerPerSideDefinitionObj searchLinkerPerSideDefinitionObj,
			Linker proxlInputLinker )throws ProxlImporterInteralException, Exception {
		
		ProteinTermini proteinTermini = linkedEnd.getProteinTermini();
		if ( proteinTermini == null  ) {
			// NO data so exit
			return; // EARLY EXIT
		}

		List<ProteinTerminus> proteinTerminusList = proteinTermini.getProteinTerminus();
		if ( proteinTerminusList.isEmpty()  ) {
			// NO data so exit
			return; // EARLY EXIT
		}
		
		List<SearchLinkerPerSideLinkableProteinTerminiObj> proteinTerminiList = new ArrayList<>( proteinTerminusList.size() );
		searchLinkerPerSideDefinitionObj.setProteinTerminiList( proteinTerminiList );
	
		for ( ProteinTerminus proteinTerminus : proteinTerminusList ) {
			
			ProteinTerminusDesignation terminusEndXMLFile = proteinTerminus.getTerminusEnd();
			SearchLinkerProteinTerminusType proteinTerminus_c_n = null;
			
			if ( terminusEndXMLFile == null ) {
				String msg = "terminusEndXMLFile == null. linker abbr: " + proxlInputLinker.getName();
				log.error( msg );
				throw new ProxlImporterInteralException(msg);
			}
			if ( terminusEndXMLFile == ProteinTerminusDesignation.C ) {
				proteinTerminus_c_n = SearchLinkerProteinTerminusType.C;
			} else if ( terminusEndXMLFile == ProteinTerminusDesignation.N ) {
				proteinTerminus_c_n = SearchLinkerProteinTerminusType.N;
			} else {
				String msg = "terminusEndXMLFile is not '" 
						+ ProteinTerminusDesignation.C.value()
						+ "' or '"
						+ ProteinTerminusDesignation.N.value()
						+ "'.  value: " + terminusEndXMLFile.value()
						+ ", toString: " + terminusEndXMLFile.toString()
						+ ", linker abbr: " + proxlInputLinker.getName();
				throw new ProxlImporterInteralException(msg);
			}
			
			SearchLinkerPerSideLinkableProteinTerminiDTO item = new SearchLinkerPerSideLinkableProteinTerminiDTO();
			item.setSearchLinkerPerSideDefinitionId( searchLinkerPerSideDefinitionDTO.getId() );
			item.setProteinTerminus_c_n( proteinTerminus_c_n );
			item.setDistanceFromTerminus(  proteinTerminus.getDistanceFromTerminus().intValue() );
			DB_Insert_SearchLinkerPerSideLinkableProteinTerminiDAO.getInstance().saveToDatabase( item );
			
			SearchLinkerPerSideLinkableProteinTerminiObj searchLinkerPerSideLinkableProteinTerminiObj = new SearchLinkerPerSideLinkableProteinTerminiObj();
			proteinTerminiList.add( searchLinkerPerSideLinkableProteinTerminiObj );
			searchLinkerPerSideLinkableProteinTerminiObj.setProteinTerminus_c_n( item.getProteinTerminus_c_n() );
			searchLinkerPerSideLinkableProteinTerminiObj.setDistanceFromTerminus( item.getDistanceFromTerminus() );
		}
	}

	/**
	 * @param linkedEnd
	 * @param searchLinkerPerSideDefinitionDTO
	 * @param proxlInputLinker
	 * @throws ProxlImporterInteralException
	 * @throws Exception
	 */
	private void saveLinkedEndResidues(
			LinkedEnd linkedEnd,
			SearchLinkerPerSideDefinitionDTO searchLinkerPerSideDefinitionDTO,
			SearchLinkerPerSideDefinitionObj searchLinkerPerSideDefinitionObj,
			Linker proxlInputLinker )throws ProxlImporterInteralException, Exception {

		Residues residues = linkedEnd.getResidues();

		if ( residues == null  ) {
			// NO data so exit
			return; // EARLY EXIT
		}
		
		List<String> residueList = residues.getResidue();
		
		if ( residueList.isEmpty()  ) {
			// NO data so exit
			return; // EARLY EXIT
		}
		
		for ( String residue : residueList ) {
			
			SearchLinkerPerSideLinkableResiduesDTO item = new SearchLinkerPerSideLinkableResiduesDTO();
			item.setSearchLinkerPerSideDefinitionId( searchLinkerPerSideDefinitionDTO.getId() );
			item.setResidue( residue );
			DB_Insert_SearchLinkerPerSideLinkableResiduesDAO.getInstance().saveToDatabase( item );
		}
		
		searchLinkerPerSideDefinitionObj.setResidues( residueList );
	}
}
