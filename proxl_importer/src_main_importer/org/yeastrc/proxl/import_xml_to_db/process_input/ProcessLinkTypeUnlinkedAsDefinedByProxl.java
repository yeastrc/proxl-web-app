package org.yeastrc.proxl.import_xml_to_db.process_input;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dao.ProteinImporterContainerDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SearchReportedPeptideDynamicModLookupDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SrchRepPeptProtSeqIdPosUnlinkedDAO;
import org.yeastrc.proxl.import_xml_to_db.dto.PeptideProteinPositionDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.ProteinSequenceDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.SearchReportedPeptideDynamicModLookupDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptProtSeqIdPosMonolinkDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptProtSeqIdPosUnlinkedDTO;
import org.yeastrc.xlink.dto.SrchRepPeptPeptDynamicModDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptPeptideDTO;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterInteralException;
import org.yeastrc.proxl.import_xml_to_db.objects.MonolinkContainer;
import org.yeastrc.proxl.import_xml_to_db.objects.PerPeptideData;
import org.yeastrc.proxl.import_xml_to_db.objects.ProteinImporterContainer;
import org.yeastrc.proxl.import_xml_to_db.peptide_protein_position.PeptideProteinPositionDTO_SaveToDB_NoDups;
import org.yeastrc.proxl_import.api.xml_dto.Peptide;
import org.yeastrc.proxl_import.api.xml_dto.Peptides;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptide;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;
import org.yeastrc.xlink.linkable_positions.linkers.ILinker;
import org.yeastrc.xlink.utils.XLinkUtils;

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
	 * result from getUnlinkedMappings method
	 *
	 */
	public static class GetUnlinkedProteinMappingsResult {
		private boolean noProteinMappings;
		private PerPeptideData perPeptideData;
		private List<SrchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_Pair> srchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_PairList;
		/** 
		 * No Protein mappings for peptide
		 * @return
		 */
		public boolean isNoProteinMappings() {
			return noProteinMappings;
		}
		public void setNoProteinMappings(boolean noProteinMappings) {
			this.noProteinMappings = noProteinMappings;
		}
	}
	private static class SrchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_Pair {
		ProteinImporterContainer proteinImporterContainer;
		SrchRepPeptProtSeqIdPosUnlinkedDTO srchRepPeptProtSeqIdPosUnlinkedDimer;
		Collection<Integer> peptidePositionsInProteinCollection;
	}
	
	/**
	 * Proxl internal "Unlinked" is a single peptide that is not a unlinked
	 * 
	 * Get Protein Mappings for unlinked reported peptide
	 * 
	 * The PeptideDTO is saved to the DB in this step since used for Protein Mappings
	 * 
	 * @param reportedPeptide
	 * @param linkerList
	 * @param linkerListStringForErrorMsgs
	 * @return
	 * @throws Exception
	 */
	public GetUnlinkedProteinMappingsResult getUnlinkedroteinMappings( 
			ReportedPeptide reportedPeptide, 
			List<ILinker> linkerList,
			String linkerListStringForErrorMsgs
			) throws Exception {
		
		GetUnlinkedProteinMappingsResult getUnlinkedMappingsResult = new GetUnlinkedProteinMappingsResult();
		List<SrchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_Pair> srchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_PairList = new ArrayList<>();
		getUnlinkedMappingsResult.srchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_PairList = srchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_PairList;
		Peptides peptides =
				reportedPeptide.getPeptides();
		if ( peptides == null ) {
			String msg = "There must be 1 peptide for Unlinked reported peptide: " + reportedPeptide.getReportedPeptideString();
			log.error( msg );
			throw new ProxlImporterDataException(msg);
		}
		List<Peptide> peptideList = peptides.getPeptide();
		if ( peptideList == null || peptideList.size() != 1 ) {
			String msg = "There must be 1 peptide for Unlinked for reported peptide: " + reportedPeptide.getReportedPeptideString();
			log.error( msg );
			throw new ProxlImporterDataException(msg);
		}
		Peptide peptide = peptideList.get( 0 );
		PerPeptideData perPeptideData = GetPerPeptideData.getInstance().getPerPeptideData( peptide );
		getUnlinkedMappingsResult.perPeptideData = perPeptideData;
		PeptideDTO peptideDTO = perPeptideData.getPeptideDTO();
		Set<Integer> peptideMonolinkPositions = 
				GetPeptideMonolinkPositions.getInstance().getPeptideMonolinkPositions( peptide );
		//  Create partial peptide level record
		SrchRepPeptPeptideDTO srchRepPeptPeptideDTO = new SrchRepPeptPeptideDTO();
		srchRepPeptPeptideDTO.setPeptideId( peptideDTO.getId() );
		perPeptideData.setSrchRepPeptPeptideDTO( srchRepPeptPeptideDTO );
		Collection<ProteinImporterContainer> proteinMatches_Peptide = 
				GetProteinsForPeptide.getInstance()
				.getProteinsForPeptides( peptideDTO.getSequence() );
		// get proteins and linkable positions in those proteins that are mapped to by the given peptides and positions
		Map<ProteinImporterContainer, Collection<Integer>> proteinMap = 
				GetLinkableProteinsAndPositions.getInstance()
				.get_Unlinked_Dimer_PeptidePositionsInProteins( 
						peptideDTO.getSequence(), 
						peptideMonolinkPositions, 
						linkerList, 
						proteinMatches_Peptide,
						reportedPeptide // For error reporting only
						);
		if( proteinMap.size() < 1 ) {
			String msg = null;
			if ( peptideMonolinkPositions != null && ( ! peptideMonolinkPositions.isEmpty() ) ) {
				msg = "Could not map this peptide and monolink positions to any protein in the Proxl XML file for peptide " 
					+ peptide.getSequence()
					+ ", monolink position(s): " + StringUtils.join( peptideMonolinkPositions, ", " )
					+ " for "
					 + " linker(s).  reportedPeptide sequence: " + reportedPeptide.getReportedPeptideString();
			} else {
				msg = "Could not map this peptide to any protein in the Proxl XML file for " 
					+ peptide.getSequence()
					 + ".  reportedPeptide sequence: " + reportedPeptide.getReportedPeptideString();
			}
			log.error( "getUnlinkedroteinMappings(...): " + msg );
			throw new ProxlImporterDataException( msg );
		}
		///  Data in perPeptideData for Monolinks
		List<MonolinkContainer> monolinkContainerList = new ArrayList<>();
		perPeptideData.setMonolinkContainerList( monolinkContainerList );
		List<Integer> peptideMonolinkPositionList = perPeptideData.getMonolinkPositionList();
		for( Map.Entry<ProteinImporterContainer, Collection<Integer>> proteinMapEntry : proteinMap.entrySet() ) {
			ProteinImporterContainer proteinImporterContainer = proteinMapEntry.getKey();
			Collection<Integer> peptidePositionsInProteinCollection = proteinMapEntry.getValue();
			// a single unlinked entry
			SrchRepPeptProtSeqIdPosUnlinkedDTO srchRepPeptProtSeqIdPosUnlinkedDimerDTO = new SrchRepPeptProtSeqIdPosUnlinkedDTO();
			SrchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_Pair srchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_Pair = new SrchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_Pair(); 
			srchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_Pair.proteinImporterContainer = proteinImporterContainer;
			srchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_Pair.srchRepPeptProtSeqIdPosUnlinkedDimer = srchRepPeptProtSeqIdPosUnlinkedDimerDTO;
			srchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_Pair.peptidePositionsInProteinCollection = peptidePositionsInProteinCollection;
			srchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_PairList.add( srchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_Pair );
			for ( Integer peptidePositionsInProtein : peptidePositionsInProteinCollection ) {
				//  Process the monolink positions
				for ( Integer peptideMonolinkPosition : peptideMonolinkPositionList ) {
					//  Convert peptide monolink position to protein position
					int proteinMonolinkPosition = peptidePositionsInProtein + peptideMonolinkPosition - 1; 
					SrchRepPeptProtSeqIdPosMonolinkDTO srchRepPeptProtSeqIdPosMonolinkDTO = new SrchRepPeptProtSeqIdPosMonolinkDTO();
					srchRepPeptProtSeqIdPosMonolinkDTO.setPeptidePosition( peptideMonolinkPosition );
					srchRepPeptProtSeqIdPosMonolinkDTO.setProteinSequencePosition( proteinMonolinkPosition );
					MonolinkContainer monolinkContainer = new MonolinkContainer();
					monolinkContainer.setProteinImporterContainer( proteinImporterContainer );
					monolinkContainer.setSrchRepPeptProtSeqIdPosMonolinkDTO( srchRepPeptProtSeqIdPosMonolinkDTO );
					monolinkContainerList.add( monolinkContainer );
				}
			}
		}  //end looping over proteins
		if ( srchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_PairList == null || srchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_PairList.isEmpty() ) {
			getUnlinkedMappingsResult.noProteinMappings = true; 
		}
		return getUnlinkedMappingsResult;
	}
	
	/**
	 * Save unlinked data to DB
	 * 
	 * @param reportedPeptideDTO
	 * @param searchId
	 * @param getUnlinkedProteinMappingsResult
	 * @throws Exception
	 */
	public List<PerPeptideData> saveUnlinkedData( 
			ReportedPeptideDTO reportedPeptideDTO, 
			int searchId, 
			GetUnlinkedProteinMappingsResult getUnlinkedProteinMappingsResult,
			Set<Double> uniqueDynamicModMassesForTheSearch
			) throws Exception {
		
		//  Save ProteinImporterContainer if needed first since used in SavePerPeptideData.getInstance().savePerPeptideData(...)
		for ( SrchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_Pair srchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_Pair
				: getUnlinkedProteinMappingsResult.srchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_PairList ) {
			ProteinImporterContainer proteinImporterContainer = srchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_Pair.proteinImporterContainer;
			proteinImporterContainer.setSearchId( searchId );
			ProteinImporterContainerDAO.getInstance().saveProteinImporterContainerIfNeeded( proteinImporterContainer );
		}
		PerPeptideData perPeptideData = getUnlinkedProteinMappingsResult.perPeptideData;
		//  Save SrchRepPeptPeptideDTO, SrchRepPeptPeptDynamicModDTO, SrchRepPeptProtSeqIdPosMonolinkDTO
		SavePerPeptideData.getInstance().savePerPeptideData( perPeptideData, searchId, reportedPeptideDTO );
		//  srchRepPeptPeptideDTO saved in savePerPeptideData(...)
		SrchRepPeptPeptideDTO srchRepPeptPeptideDTO = perPeptideData.getSrchRepPeptPeptideDTO();
		int searchReportedPeptidepeptideId = srchRepPeptPeptideDTO.getId();
		PeptideDTO peptideDTO = perPeptideData.getPeptideDTO();
		int peptideLength = peptideDTO.getSequence().length();
		//  Save Unlinked Protein Mappings 
		for ( SrchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_Pair srchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_Pair
				: getUnlinkedProteinMappingsResult.srchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_PairList ) {
			ProteinImporterContainer proteinImporterContainer = srchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_Pair.proteinImporterContainer;
			ProteinSequenceDTO proteinSequenceDTO = proteinImporterContainer.getProteinSequenceDTO();
			SrchRepPeptProtSeqIdPosUnlinkedDTO srchRepPeptProtSeqIdPosUnlinkedDimerDTO = srchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_Pair.srchRepPeptProtSeqIdPosUnlinkedDimer;
			srchRepPeptProtSeqIdPosUnlinkedDimerDTO.setProteinSequenceId( proteinSequenceDTO.getId() );
			srchRepPeptProtSeqIdPosUnlinkedDimerDTO.setSearchId( searchId );
			srchRepPeptProtSeqIdPosUnlinkedDimerDTO.setReportedPeptideId( reportedPeptideDTO.getId() );
			srchRepPeptProtSeqIdPosUnlinkedDimerDTO.setSearchReportedPeptidepeptideId( searchReportedPeptidepeptideId );
			DB_Insert_SrchRepPeptProtSeqIdPosUnlinkedDAO.getInstance().save( srchRepPeptProtSeqIdPosUnlinkedDimerDTO );
			//  Insert PeptideProteinPositionDTO record for protein coverage
			Collection<Integer> peptidePositionsInProteinCollection = srchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_Pair.peptidePositionsInProteinCollection;
			for ( Integer peptidePositionInProtein : peptidePositionsInProteinCollection ) {
				PeptideProteinPositionDTO peptideProteinPositionDTO = new PeptideProteinPositionDTO();
				peptideProteinPositionDTO.setSearchId( searchId );
				peptideProteinPositionDTO.setReportedPeptideId( reportedPeptideDTO.getId() );
				peptideProteinPositionDTO.setPeptideId( peptideDTO.getId() );
				peptideProteinPositionDTO.setProteinSequenceId( proteinSequenceDTO.getId() );
				peptideProteinPositionDTO.setProteinStartPosition( peptidePositionInProtein );
				peptideProteinPositionDTO.setProteinEndPosition( peptidePositionInProtein + peptideLength - 1 );
				PeptideProteinPositionDTO_SaveToDB_NoDups.getInstance().peptideProteinPositionDTO_SaveToDB_NoDups( peptideProteinPositionDTO );
			}
		}
		//  Save Dynamic Mod Masses into Lookup table and into Set for Search level lookup
		for ( SrchRepPeptPeptDynamicModDTO srchRepPeptPeptDynamicModDTO : perPeptideData.getSrchRepPeptPeptDynamicModDTOList_Peptide() ) {
			SearchReportedPeptideDynamicModLookupDTO item = new SearchReportedPeptideDynamicModLookupDTO();
			item.setDynamicModMass( srchRepPeptPeptDynamicModDTO.getMass() );
			item.setLinkType( XLinkUtils.TYPE_UNLINKED );
			item.setReportedPeptideId( reportedPeptideDTO.getId() );
			item.setSearchId( searchId );
			DB_Insert_SearchReportedPeptideDynamicModLookupDAO.getInstance().saveToDatabaseIgnoreDuplicates( item );
			//  Accumulate mod mass values across the search 
			uniqueDynamicModMassesForTheSearch.add( srchRepPeptPeptDynamicModDTO.getMass() );
		}
		//   Determine if peptide is only mapped to one protein and save that to perPeptideData
		List<SrchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_Pair> srchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_PairList_Peptide =
				getUnlinkedProteinMappingsResult.srchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_PairList;	
		if ( srchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_PairList_Peptide == null 
				|| srchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_PairList_Peptide.isEmpty() ) {
			String msg = "ERROR: srchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_PairList_Peptide is null or is empty.";
			log.error( msg );
			throw new ProxlImporterInteralException(msg);
		}
		if ( srchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_PairList_Peptide.size() == 1 ) {
			//  Only one mapped protein record so peptide is unique
			perPeptideData.setPeptideIdMapsToOnlyOneProtein( true );
		} else {
			//  More than one mapped protein record so they just have to all have the same protein sequence id
			//  in order for the peptide to be unique
			boolean peptideIdMapsToOnlyOneProtein = true;
			SrchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_Pair firstSrchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_Pair =
					srchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_PairList_Peptide.get( 0 );
			SrchRepPeptProtSeqIdPosUnlinkedDTO firstSrchRepPeptProtSeqIdPosUnlinkedDimerDTO = firstSrchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_Pair.srchRepPeptProtSeqIdPosUnlinkedDimer;
			int firstProteinSequenceId = firstSrchRepPeptProtSeqIdPosUnlinkedDimerDTO.getProteinSequenceId();
			for ( SrchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_Pair item : srchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_PairList_Peptide ) {
				if ( firstProteinSequenceId != item.srchRepPeptProtSeqIdPosUnlinkedDimer.getProteinSequenceId() ) {
					//  Found record with different protein sequence id so peptide is not unique
					peptideIdMapsToOnlyOneProtein = false;
					break;
				}
			}
			if ( peptideIdMapsToOnlyOneProtein ) {
				//  Peptide only maps to 1 protein so set petpideUnique to true
				perPeptideData.setPeptideIdMapsToOnlyOneProtein( peptideIdMapsToOnlyOneProtein );
			}
		}
		List<PerPeptideData> perPeptideDataList = new ArrayList<>( 1 );
		perPeptideDataList.add( perPeptideData );
		return perPeptideDataList;
	}
}