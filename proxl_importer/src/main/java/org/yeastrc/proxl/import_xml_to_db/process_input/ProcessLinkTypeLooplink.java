package org.yeastrc.proxl.import_xml_to_db.process_input;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;import org.slf4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dao.ProteinImporterContainerDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SearchReportedPeptideDynamicModLookupDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SrchRepPeptProtSeqIdPosLooplinkDAO;
import org.yeastrc.proxl.import_xml_to_db.dto.ProteinCoverageDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.ProteinSequenceVersionDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.SearchReportedPeptideDynamicModLookupDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptProtSeqIdPosLooplinkDTO;
import org.yeastrc.xlink.dto.SrchRepPeptPeptDynamicModDTO;
import org.yeastrc.xlink.linker_data_processing_base.ILinkers_Main_ForSingleSearch;
import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptPeptideDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptProtSeqIdPosMonolinkDTO;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterInteralException;
import org.yeastrc.proxl.import_xml_to_db.objects.MonolinkContainer;
import org.yeastrc.proxl.import_xml_to_db.objects.PerPeptideData;
import org.yeastrc.proxl.import_xml_to_db.objects.ProteinImporterContainer;
import org.yeastrc.proxl.import_xml_to_db.peptide_protein_position.ProteinCoverageDTO_SaveToDB_NoDups;
import org.yeastrc.proxl_import.api.xml_dto.LinkedPosition;
import org.yeastrc.proxl_import.api.xml_dto.LinkedPositions;
import org.yeastrc.proxl_import.api.xml_dto.Peptide;
import org.yeastrc.proxl_import.api.xml_dto.Peptides;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptide;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;
import org.yeastrc.xlink.utils.XLinkUtils;

/**
 * 
 *
 */
public class ProcessLinkTypeLooplink {
	
	private static final Logger log = LoggerFactory.getLogger( ProcessLinkTypeLooplink.class);
	//  private constructor
	private ProcessLinkTypeLooplink() { }
	public static ProcessLinkTypeLooplink getInstance() { return new ProcessLinkTypeLooplink(); }
	/**
	 * result from getLooplinkMappings method
	 *
	 */
	public static class GetLooplinkProteinMappingsResult {
		private boolean noProteinMappings;
		private PerPeptideData perPeptideData;
		private List<SrchRepPeptProtSeqIdPosLooplinkDTO_ProteinImporterContainer_Pair> srchRepPeptProtSeqIdPosLooplinkDTO_ProteinImporterContainer_PairList;
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
	private static class SrchRepPeptProtSeqIdPosLooplinkDTO_ProteinImporterContainer_Pair {
		ProteinImporterContainer proteinImporterContainer;
		SrchRepPeptProtSeqIdPosLooplinkDTO srchRepPeptProtSeqIdPosLooplinkDTO;
	}
	
	/**
	 * Get Protein Mappings for looplink reported peptide
	 * 
	 * The PeptideDTO is saved to the DB in this step since used for Protein Mappings
	 * 
	 * @param reportedPeptide
	 * @param linkers_Main_ForSingleSearch
	 * @return
	 * @throws Exception
	 */
	public GetLooplinkProteinMappingsResult getLooplinkroteinMappings( 
			ReportedPeptide reportedPeptide, 
			ILinkers_Main_ForSingleSearch linkers_Main_ForSingleSearch
			) throws Exception {
		
		GetLooplinkProteinMappingsResult getLooplinkMappingsResult = new GetLooplinkProteinMappingsResult();
		List<SrchRepPeptProtSeqIdPosLooplinkDTO_ProteinImporterContainer_Pair> srchRepPeptProtSeqIdPosLooplinkDTO_ProteinImporterContainer_PairList = new ArrayList<>();
		getLooplinkMappingsResult.srchRepPeptProtSeqIdPosLooplinkDTO_ProteinImporterContainer_PairList = srchRepPeptProtSeqIdPosLooplinkDTO_ProteinImporterContainer_PairList;
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
		PerPeptideData perPeptideData = GetPerPeptideData.getInstance().getPerPeptideData( peptide );
		getLooplinkMappingsResult.perPeptideData = perPeptideData;
		int[] peptideLinkedPositions = getLooplinkLinkedPositions( peptide, "1", reportedPeptide );
		int peptideLinkedPosition_1 = peptideLinkedPositions[ 0 ];
		int peptideLinkedPosition_2 = peptideLinkedPositions[ 1 ];
		//  Order linked positions so smaller one is first
		if ( peptideLinkedPosition_1 > peptideLinkedPosition_2  ) {
			int linkedPosition_temp = peptideLinkedPosition_2;
			peptideLinkedPosition_2 = peptideLinkedPosition_1;
			peptideLinkedPosition_1 = linkedPosition_temp;
		}
		PeptideDTO peptideDTO = perPeptideData.getPeptideDTO();
		Set<Integer> peptideMonolinkPositions = 
				GetPeptideMonolinkPositions.getInstance().getPeptideMonolinkPositions( peptide );
		//  Create partial SrchRepPeptPeptideDTO peptide level record
		SrchRepPeptPeptideDTO srchRepPeptPeptideDTO = new SrchRepPeptPeptideDTO();
		srchRepPeptPeptideDTO.setPeptideId( peptideDTO.getId() );
		srchRepPeptPeptideDTO.setPeptidePosition_1( peptideLinkedPosition_1 );
		srchRepPeptPeptideDTO.setPeptidePosition_2( peptideLinkedPosition_2 );
		perPeptideData.setSrchRepPeptPeptideDTO( srchRepPeptPeptideDTO );
		Collection<ProteinImporterContainer> proteinMatches_Peptide = 
				GetProteinsForPeptide.getInstance()
				.getProteinsForPeptide( peptide, reportedPeptide /* for error reporting */ );
		Map<ProteinImporterContainer, Collection<List<Integer>>> proteinMap = 
				GetLinkableProteinsAndPositions.getInstance()
				.getLooplinkLinkableProteinsAndPositionsForLooplink( 
						peptideDTO.getSequence(), 
						peptideLinkedPosition_1, 
						peptideLinkedPosition_2,
						peptideMonolinkPositions,
						linkers_Main_ForSingleSearch, 
						proteinMatches_Peptide,
						reportedPeptide // For error reporting only
						);
		
		if ( proteinMap.isEmpty() ) {

			List<Integer> peptideLinkPositions = new ArrayList<>();
			peptideLinkPositions.add( peptideLinkedPosition_1 );
			peptideLinkPositions.add( peptideLinkedPosition_2 );
			
			if ( peptideMonolinkPositions != null && ( ! peptideMonolinkPositions.isEmpty() ) ) {
				
				for ( Integer peptideMonolinkPosition : peptideMonolinkPositions ) {
					if ( ! peptideLinkPositions.contains( peptideMonolinkPosition ) ) {
						peptideLinkPositions.add( peptideMonolinkPosition );
					}
				}
			}

			Collections.sort( peptideLinkPositions );
			

			if ( ! linkers_Main_ForSingleSearch.isAllLinkersHave_LinkablePositions() ) {
				//  Not all Linkers have linkable positions

				String msg = "Could not import this Proxl XML file. No protein in the Proxl XML contained this peptide sequence (" 
						+ peptide.getSequence()
						+ "). The whole reported peptide string was: "
						+ reportedPeptide.getReportedPeptideString()
						+ "  \n\nThis is most-probably caused by specifying the incorrect FASTA file when generating the Proxl XML file.";
				
				log.error( "getLooplinks(...): Msg thrown in ProxlImporterDataException: " + msg );
				throw new ProxlImporterDataException( msg );
				
			} else {

				String linkersToString = linkers_Main_ForSingleSearch.getLinkerAbbreviationsCommaDelim();
	
	
				
				String msg = "Could not import this Proxl XML file. Either no protein in the Proxl XML contained this peptide sequence (" 
						+ peptide.getSequence()
						+ ") or the linked position(s) reported for the peptide (positions " 
						+ StringUtils.join( peptideLinkPositions, ", " )
						+ ") was not a linkable position in the matched protein for the given cross-linker(s) (["
						+ linkersToString
						+ "]). The whole reported peptide string was: "
						+ reportedPeptide.getReportedPeptideString()
						+ "  \n\nThis is most-probably caused by specifying the incorrect cross-linker or the incorrect FASTA file when generating the Proxl XML file.";
				
				log.error( "getLooplinks(...): Msg thrown in ProxlImporterDataException: " + msg );
				throw new ProxlImporterDataException( msg );
			}
		}
		///  Data in perPeptideData for Monolinks
		List<MonolinkContainer> monolinkContainerList = new ArrayList<>();
		perPeptideData.setMonolinkContainerList( monolinkContainerList );
		List<Integer> peptideMonolinkPositionList = perPeptideData.getMonolinkPositionList();
		for( Map.Entry<ProteinImporterContainer, Collection<List<Integer>>> proteinMapEntry : proteinMap.entrySet() ) {
			ProteinImporterContainer proteinImporterContainer = proteinMapEntry.getKey();
			Collection<List<Integer>> proteinPositions = proteinMapEntry.getValue();
			for( List<Integer> proteinPositions_1_2 : proteinPositions ) {
				if ( proteinPositions_1_2.size() != 2 ) {
					String msg = "List<Integer> in Map<NRProteinDTO, Collection<List<Integer>>> must contain 2 entries.";
					log.error( msg );
					throw new ProxlImporterInteralException(msg);
				}
				int proteinPositions_1 = proteinPositions_1_2.get( 0 );
				int proteinPositions_2 = proteinPositions_1_2.get( 1 );
				// a single looplink entry
				SrchRepPeptProtSeqIdPosLooplinkDTO srchRepPeptProtSeqIdPosLooplinkDTO = new SrchRepPeptProtSeqIdPosLooplinkDTO();
				srchRepPeptProtSeqIdPosLooplinkDTO.setProteinSequencePosition_1( proteinPositions_1 );
				srchRepPeptProtSeqIdPosLooplinkDTO.setProteinSequencePosition_2( proteinPositions_2 );
				SrchRepPeptProtSeqIdPosLooplinkDTO_ProteinImporterContainer_Pair srchRepPeptProtSeqIdPosLooplinkDTO_ProteinImporterContainer_Pair = new SrchRepPeptProtSeqIdPosLooplinkDTO_ProteinImporterContainer_Pair();
				srchRepPeptProtSeqIdPosLooplinkDTO_ProteinImporterContainer_Pair.srchRepPeptProtSeqIdPosLooplinkDTO = srchRepPeptProtSeqIdPosLooplinkDTO;
				srchRepPeptProtSeqIdPosLooplinkDTO_ProteinImporterContainer_Pair.proteinImporterContainer = proteinImporterContainer;
				srchRepPeptProtSeqIdPosLooplinkDTO_ProteinImporterContainer_PairList.add( srchRepPeptProtSeqIdPosLooplinkDTO_ProteinImporterContainer_Pair );
				//  Process the monolink positions
				if ( peptideMonolinkPositionList != null && ( ! peptideMonolinkPositionList.isEmpty() ) ) {
					for ( Integer peptideMonolinkPosition : peptideMonolinkPositionList ) {
						//  Convert peptide monolink position to protein position
						int proteinMonolinkPosition = proteinPositions_1 - peptideLinkedPosition_1 + peptideMonolinkPosition; 
						SrchRepPeptProtSeqIdPosMonolinkDTO srchRepPeptProtSeqIdPosMonolinkDTO = new SrchRepPeptProtSeqIdPosMonolinkDTO();
						srchRepPeptProtSeqIdPosMonolinkDTO.setPeptidePosition( peptideMonolinkPosition );
						srchRepPeptProtSeqIdPosMonolinkDTO.setProteinSequencePosition( proteinMonolinkPosition );
						MonolinkContainer monolinkContainer = new MonolinkContainer();
						monolinkContainer.setProteinImporterContainer( proteinImporterContainer );
						monolinkContainer.setSrchRepPeptProtSeqIdPosMonolinkDTO( srchRepPeptProtSeqIdPosMonolinkDTO );
						monolinkContainerList.add( monolinkContainer );
					}
				}
			}  //end looping over protein positions
		}  //end looping over proteins
		if ( srchRepPeptProtSeqIdPosLooplinkDTO_ProteinImporterContainer_PairList == null || srchRepPeptProtSeqIdPosLooplinkDTO_ProteinImporterContainer_PairList.isEmpty() ) {
			getLooplinkMappingsResult.noProteinMappings = true; 
		}
		return getLooplinkMappingsResult;
	}
	
	/**
	 * Save looplink data to DB
	 * 
	 * @param reportedPeptideDTO
	 * @param searchId
	 * @param getLooplinkProteinMappingsResult
	 * @throws Exception
	 */
	public List<PerPeptideData> saveLooplinkData( 
			ReportedPeptideDTO reportedPeptideDTO, 
			int searchId, 
			GetLooplinkProteinMappingsResult getLooplinkProteinMappingsResult
			) throws Exception {
		
		//  Save ProteinImporterContainer if needed first since used in SavePerPeptideData.getInstance().savePerPeptideData(...)
		for ( SrchRepPeptProtSeqIdPosLooplinkDTO_ProteinImporterContainer_Pair srchRepPeptProtSeqIdPosLooplinkDTO_ProteinImporterContainer_Pair
				: getLooplinkProteinMappingsResult.srchRepPeptProtSeqIdPosLooplinkDTO_ProteinImporterContainer_PairList ) {
			ProteinImporterContainer proteinImporterContainer = srchRepPeptProtSeqIdPosLooplinkDTO_ProteinImporterContainer_Pair.proteinImporterContainer;
			proteinImporterContainer.setSearchId( searchId );
			ProteinImporterContainerDAO.getInstance().saveProteinImporterContainerIfNeeded( proteinImporterContainer );
		}
		PerPeptideData perPeptideData = getLooplinkProteinMappingsResult.perPeptideData;
		//  Save SrchRepPeptPeptideDTO, SrchRepPeptPeptDynamicModDTO, SrchRepPeptProtSeqIdPosMonolinkDTO
		SavePerPeptideData.getInstance().savePerPeptideData( perPeptideData, searchId, reportedPeptideDTO );
		//  srchRepPeptPeptideDTO saved in savePerPeptideData(...)
		SrchRepPeptPeptideDTO srchRepPeptPeptideDTO = perPeptideData.getSrchRepPeptPeptideDTO();
		int searchReportedPeptidepeptideId = srchRepPeptPeptideDTO.getId();
		PeptideDTO peptideDTO = perPeptideData.getPeptideDTO();
		int peptideLength = peptideDTO.getSequence().length();
		int peptidePosition_1 = srchRepPeptPeptideDTO.getPeptidePosition_1();
		//  Save Looplink Protein Mappings 
		for ( SrchRepPeptProtSeqIdPosLooplinkDTO_ProteinImporterContainer_Pair srchRepPeptProtSeqIdPosLooplinkDTO_ProteinImporterContainer_Pair
				: getLooplinkProteinMappingsResult.srchRepPeptProtSeqIdPosLooplinkDTO_ProteinImporterContainer_PairList ) {
			ProteinImporterContainer proteinImporterContainer = srchRepPeptProtSeqIdPosLooplinkDTO_ProteinImporterContainer_Pair.proteinImporterContainer;
			ProteinSequenceVersionDTO proteinSequenceVersionDTO = proteinImporterContainer.getProteinSequenceVersionDTO();
			SrchRepPeptProtSeqIdPosLooplinkDTO srchRepPeptProtSeqIdPosLooplinkDTO = srchRepPeptProtSeqIdPosLooplinkDTO_ProteinImporterContainer_Pair.srchRepPeptProtSeqIdPosLooplinkDTO;
			srchRepPeptProtSeqIdPosLooplinkDTO.setProteinSequenceVersionId( proteinSequenceVersionDTO.getId() );
			srchRepPeptProtSeqIdPosLooplinkDTO.setSearchId( searchId );
			srchRepPeptProtSeqIdPosLooplinkDTO.setReportedPeptideId( reportedPeptideDTO.getId() );
			srchRepPeptProtSeqIdPosLooplinkDTO.setSearchReportedPeptidepeptideId( searchReportedPeptidepeptideId );
			DB_Insert_SrchRepPeptProtSeqIdPosLooplinkDAO.getInstance().save( srchRepPeptProtSeqIdPosLooplinkDTO );
			//  Insert record for protein coverage
			int proteinSequencePosition_1 = srchRepPeptProtSeqIdPosLooplinkDTO.getProteinSequencePosition_1();
			int proteinStartPosition = proteinSequencePosition_1 - peptidePosition_1 + 1;
			int proteinEndPosition = proteinStartPosition + peptideLength - 1;
			ProteinCoverageDTO proteinCoverageDTO = new ProteinCoverageDTO();
			proteinCoverageDTO.setSearchId( searchId );
			proteinCoverageDTO.setReportedPeptideId( reportedPeptideDTO.getId() );
			proteinCoverageDTO.setPeptideIdInfoOnly( peptideDTO.getId() );
			proteinCoverageDTO.setProteinSequenceVersionId( proteinSequenceVersionDTO.getId() );
			proteinCoverageDTO.setProteinStartPosition( proteinStartPosition );
			proteinCoverageDTO.setProteinEndPosition( proteinEndPosition );
			ProteinCoverageDTO_SaveToDB_NoDups.getInstance().proteinCoverageDTO_SaveToDB_NoDups( proteinCoverageDTO );
		}
		//  Save Dynamic Mod Masses into Lookup table
		if ( perPeptideData.getSrchRepPeptPeptDynamicModDTOList_Peptide() != null && ( ! perPeptideData.getSrchRepPeptPeptDynamicModDTOList_Peptide().isEmpty() ) ) {
			for ( SrchRepPeptPeptDynamicModDTO srchRepPeptPeptDynamicModDTO : perPeptideData.getSrchRepPeptPeptDynamicModDTOList_Peptide() ) {
				SearchReportedPeptideDynamicModLookupDTO item = new SearchReportedPeptideDynamicModLookupDTO();
				item.setDynamicModMass( srchRepPeptPeptDynamicModDTO.getMass() );
				item.setLinkType( XLinkUtils.TYPE_LOOPLINK );
				item.setReportedPeptideId( reportedPeptideDTO.getId() );
				item.setSearchId( searchId );
				DB_Insert_SearchReportedPeptideDynamicModLookupDAO.getInstance().saveToDatabaseIgnoreDuplicates( item );
			}
		}
		//   Determine if peptide is only mapped to one protein and save that to perPeptideData
		List<SrchRepPeptProtSeqIdPosLooplinkDTO_ProteinImporterContainer_Pair> srchRepPeptProtSeqIdPosLooplinkDTO_ProteinImporterContainer_PairList_Peptide =
				getLooplinkProteinMappingsResult.srchRepPeptProtSeqIdPosLooplinkDTO_ProteinImporterContainer_PairList;	
		if ( srchRepPeptProtSeqIdPosLooplinkDTO_ProteinImporterContainer_PairList_Peptide == null 
				|| srchRepPeptProtSeqIdPosLooplinkDTO_ProteinImporterContainer_PairList_Peptide.isEmpty() ) {
			String msg = "ERROR: srchRepPeptProtSeqIdPosLooplinkDTO_ProteinImporterContainer_PairList_Peptide is null or is empty.";
			log.error( msg );
			throw new ProxlImporterInteralException(msg);
		}
		if ( srchRepPeptProtSeqIdPosLooplinkDTO_ProteinImporterContainer_PairList_Peptide.size() == 1 ) {
			//  Only one mapped protein record so peptide is unique
			perPeptideData.setPeptideIdMapsToOnlyOneProtein( true );
		} else {
			//  More than one mapped protein record so they just have to all have the same protein sequence id
			//  in order for the peptide to be unique
			boolean peptideIdMapsToOnlyOneProtein = true;
			SrchRepPeptProtSeqIdPosLooplinkDTO_ProteinImporterContainer_Pair firstSrchRepPeptProtSeqIdPosLooplinkDTO_ProteinImporterContainer_Pair =
					srchRepPeptProtSeqIdPosLooplinkDTO_ProteinImporterContainer_PairList_Peptide.get( 0 );
			SrchRepPeptProtSeqIdPosLooplinkDTO firstSrchRepPeptProtSeqIdPosLooplinkDTO = firstSrchRepPeptProtSeqIdPosLooplinkDTO_ProteinImporterContainer_Pair.srchRepPeptProtSeqIdPosLooplinkDTO;
			int firstProteinSequenceVersionId = firstSrchRepPeptProtSeqIdPosLooplinkDTO.getProteinSequenceVersionId();
			for ( SrchRepPeptProtSeqIdPosLooplinkDTO_ProteinImporterContainer_Pair item : srchRepPeptProtSeqIdPosLooplinkDTO_ProteinImporterContainer_PairList_Peptide ) {
				if ( firstProteinSequenceVersionId != item.srchRepPeptProtSeqIdPosLooplinkDTO.getProteinSequenceVersionId() ) {
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
}