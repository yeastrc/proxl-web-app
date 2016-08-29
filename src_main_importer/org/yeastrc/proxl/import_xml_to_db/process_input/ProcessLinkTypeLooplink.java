package org.yeastrc.proxl.import_xml_to_db.process_input;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dao.ProteinImporterContainerDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SearchReportedPeptideDynamicModLookupDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SrchRepPeptProtSeqIdPosLooplinkDAO;
import org.yeastrc.proxl.import_xml_to_db.dto.PeptideProteinPositionDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.SearchReportedPeptideDynamicModLookupDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptProtSeqIdPosLooplinkDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptPeptDynamicModDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptPeptideDTO;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterInteralException;
import org.yeastrc.proxl.import_xml_to_db.objects.PerPeptideData;
import org.yeastrc.proxl.import_xml_to_db.objects.ProteinImporterContainer;
import org.yeastrc.proxl.import_xml_to_db.peptide_protein_position.PeptideProteinPositionDTO_SaveToDB_NoDups;
import org.yeastrc.proxl_import.api.xml_dto.LinkedPosition;
import org.yeastrc.proxl_import.api.xml_dto.LinkedPositions;
import org.yeastrc.proxl_import.api.xml_dto.Peptide;
import org.yeastrc.proxl_import.api.xml_dto.Peptides;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptide;
import org.yeastrc.proxl.import_xml_to_db.dto.PeptideDTO;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;
import org.yeastrc.xlink.linkable_positions.linkers.ILinker;
import org.yeastrc.xlink.utils.XLinkUtils;





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
	 * @param linkerList
	 * @param linkerListStringForErrorMsgs
	 * @return
	 * @throws Exception
	 */
	public GetLooplinkProteinMappingsResult getLooplinkroteinMappings( 
			
			ReportedPeptide reportedPeptide, 

			List<ILinker> linkerList,
			String linkerListStringForErrorMsgs
			
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
		
		
		
		int[] linkedPositions = getLooplinkLinkedPositions( peptide, "1", reportedPeptide );

		int linkedPosition_1 = linkedPositions[ 0 ];
		int linkedPosition_2 = linkedPositions[ 1 ];

		//  Order linked positions so smaller one is first
		
		if ( linkedPosition_1 > linkedPosition_2  ) {
			
			int linkedPosition_temp = linkedPosition_2;
			linkedPosition_2 = linkedPosition_1;
			linkedPosition_1 = linkedPosition_temp;
		}
		
		
		
		PeptideDTO peptideDTO = perPeptideData.getPeptideDTO();
		
		
		//  Create partial SrchRepPeptPeptideDTO peptide level record

		SrchRepPeptPeptideDTO srchRepPeptPeptideDTO = new SrchRepPeptPeptideDTO();
		
		srchRepPeptPeptideDTO.setPeptideId( peptideDTO.getId() );
		srchRepPeptPeptideDTO.setPeptidePosition_1( linkedPosition_1 );
		srchRepPeptPeptideDTO.setPeptidePosition_2( linkedPosition_2 );
		
		
		perPeptideData.setSrchRepPeptPeptideDTO( srchRepPeptPeptideDTO );
		
		Collection<ProteinImporterContainer> proteinMatches_Peptide = 
				GetProteinsForPeptide.getInstance()
				.getProteinsForPeptides( peptideDTO.getSequence() );
		
		Map<ProteinImporterContainer, Collection<List<Integer>>> proteinMap = 

				GetLinkableProteinsAndPositions.getInstance()
				.getLinkableProteinsAndPositionsForLooplink( 
						peptideDTO.getSequence(), 
						linkedPosition_1, 
						linkedPosition_2 , 
						linkerList, 
						proteinMatches_Peptide );
		
			
		if( proteinMap.size() < 1 ) {
			String msg = "Could not map this peptide and link positions to any protein in the Proxl XML file for " + peptideDTO.getSequence() 
					+ " at positions " + linkedPosition_1 + "," + linkedPosition_2 
					+ ".  reportedPeptide sequence: " + reportedPeptide.getReportedPeptideString();
			log.error( "getLooplinks(...): " + msg );
			
			throw new ProxlImporterDataException( msg );
		}
		
		
		
		for( Map.Entry<ProteinImporterContainer, Collection<List<Integer>>> proteinMapEntry : proteinMap.entrySet() ) {
				
			ProteinImporterContainer proteinImporterContainer = proteinMapEntry.getKey();

			Collection<List<Integer>> proteinPositions = proteinMapEntry.getValue();
			
			for( List<Integer> proteinPositions_1_2 : proteinPositions ) {

				if ( proteinPositions_1_2.size() != 2 ) {
					
					String msg = "List<Integer> in Map<NRProteinDTO, Collection<List<Integer>>> must contain 2 entries.";
					log.error( msg );
					throw new ProxlImporterInteralException(msg);
				}
				
				// a single looplink entry
				SrchRepPeptProtSeqIdPosLooplinkDTO srchRepPeptProtSeqIdPosLooplinkDTO = new SrchRepPeptProtSeqIdPosLooplinkDTO();

				
				srchRepPeptProtSeqIdPosLooplinkDTO.setProteinSequencePosition_1( proteinPositions_1_2.get( 0 ) );
				srchRepPeptProtSeqIdPosLooplinkDTO.setProteinSequencePosition_2( proteinPositions_1_2.get( 1 ) );
				
				SrchRepPeptProtSeqIdPosLooplinkDTO_ProteinImporterContainer_Pair srchRepPeptProtSeqIdPosLooplinkDTO_ProteinImporterContainer_Pair = new SrchRepPeptProtSeqIdPosLooplinkDTO_ProteinImporterContainer_Pair();
				
				srchRepPeptProtSeqIdPosLooplinkDTO_ProteinImporterContainer_Pair.srchRepPeptProtSeqIdPosLooplinkDTO = srchRepPeptProtSeqIdPosLooplinkDTO;
				srchRepPeptProtSeqIdPosLooplinkDTO_ProteinImporterContainer_Pair.proteinImporterContainer = proteinImporterContainer;
				
				srchRepPeptProtSeqIdPosLooplinkDTO_ProteinImporterContainer_PairList.add( srchRepPeptProtSeqIdPosLooplinkDTO_ProteinImporterContainer_Pair );

			}  //end looping over protein positions
		
		}  //end looping over proteins
		

		if ( srchRepPeptProtSeqIdPosLooplinkDTO_ProteinImporterContainer_PairList == null || srchRepPeptProtSeqIdPosLooplinkDTO_ProteinImporterContainer_PairList.isEmpty() ) {

			getLooplinkMappingsResult.noProteinMappings = true; 
		}
		

		PopulateSrchRepPeptProtSeqIdPosMonolinkDTOListOnPerPeptideDataObject.getInstance()
		.populateSrchRepPeptProtSeqIdPosMonolinkDTOListOnPerPeptideDataObject( perPeptideData, linkerList, proteinMatches_Peptide );

		
		
		
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

			GetLooplinkProteinMappingsResult getLooplinkProteinMappingsResult,
			
			Set<Double> uniqueDynamicModMassesForTheSearch
			
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
						
			SrchRepPeptProtSeqIdPosLooplinkDTO srchRepPeptProtSeqIdPosLooplinkDTO = srchRepPeptProtSeqIdPosLooplinkDTO_ProteinImporterContainer_Pair.srchRepPeptProtSeqIdPosLooplinkDTO;

			srchRepPeptProtSeqIdPosLooplinkDTO.setProteinSequenceId( proteinImporterContainer.getProteinSequenceDTO().getId() );
			
			srchRepPeptProtSeqIdPosLooplinkDTO.setSearchId( searchId );
			srchRepPeptProtSeqIdPosLooplinkDTO.setReportedPeptideId( reportedPeptideDTO.getId() );
			srchRepPeptProtSeqIdPosLooplinkDTO.setSearchReportedPeptidepeptideId( searchReportedPeptidepeptideId );
			
			DB_Insert_SrchRepPeptProtSeqIdPosLooplinkDAO.getInstance().save( srchRepPeptProtSeqIdPosLooplinkDTO );
			

			//  Insert record for protein coverage
						
			int proteinSequencePosition_1 = srchRepPeptProtSeqIdPosLooplinkDTO.getProteinSequencePosition_1();
			
			int proteinStartPosition = proteinSequencePosition_1 - peptidePosition_1 + 1;
			
			int proteinEndPosition = proteinStartPosition + peptideLength - 1;
			
			PeptideProteinPositionDTO peptideProteinPositionDTO = new PeptideProteinPositionDTO();
			
			peptideProteinPositionDTO.setSearchId( searchId );
			peptideProteinPositionDTO.setReportedPeptideId( reportedPeptideDTO.getId() );
			peptideProteinPositionDTO.setPeptideId( peptideDTO.getId() );
			peptideProteinPositionDTO.setProteinSequenceId( proteinImporterContainer.getProteinSequenceDTO().getId() );
			peptideProteinPositionDTO.setProteinStartPosition( proteinStartPosition );
			peptideProteinPositionDTO.setProteinEndPosition( proteinEndPosition );
			
			PeptideProteinPositionDTO_SaveToDB_NoDups.getInstance().peptideProteinPositionDTO_SaveToDB_NoDups( peptideProteinPositionDTO );

		}

		//  Save Dynamic Mod Masses into Lookup table and into Set for Search level lookup
		
		for ( SrchRepPeptPeptDynamicModDTO srchRepPeptPeptDynamicModDTO : perPeptideData.getSrchRepPeptPeptDynamicModDTOList_Peptide() ) {
		
			SearchReportedPeptideDynamicModLookupDTO item = new SearchReportedPeptideDynamicModLookupDTO();
			
			item.setDynamicModMass( srchRepPeptPeptDynamicModDTO.getMass() );
			item.setLinkType( XLinkUtils.TYPE_LOOPLINK );
			item.setReportedPeptideId( reportedPeptideDTO.getId() );
			item.setSearchId( searchId );
			
			DB_Insert_SearchReportedPeptideDynamicModLookupDAO.getInstance().saveToDatabaseIgnoreDuplicates( item );
			
			//  Accumulate mod mass values across the search 
			uniqueDynamicModMassesForTheSearch.add( srchRepPeptPeptDynamicModDTO.getMass() );
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

			int firstProteinSequenceId = firstSrchRepPeptProtSeqIdPosLooplinkDTO.getProteinSequenceId();
			
			for ( SrchRepPeptProtSeqIdPosLooplinkDTO_ProteinImporterContainer_Pair item : srchRepPeptProtSeqIdPosLooplinkDTO_ProteinImporterContainer_PairList_Peptide ) {

				if ( firstProteinSequenceId != item.srchRepPeptProtSeqIdPosLooplinkDTO.getProteinSequenceId() ) {
					
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