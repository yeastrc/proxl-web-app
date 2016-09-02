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
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SrchRepPeptProtSeqIdPosCrosslinkDAO;
import org.yeastrc.proxl.import_xml_to_db.dto.PeptideProteinPositionDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.SearchReportedPeptideDynamicModLookupDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptProtSeqIdPosCrosslinkDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptPeptDynamicModDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptPeptideDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptProtSeqIdPosMonolinkDTO;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterInteralException;
import org.yeastrc.proxl.import_xml_to_db.objects.MonolinkContainer;
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
import org.yeastrc.xlink.exceptions.ProxlBaseDataException;
import org.yeastrc.xlink.linkable_positions.linkers.ILinker;
import org.yeastrc.xlink.utils.XLinkUtils;





/**
 * 
 *
 */
public class ProcessLinkTypeCrosslink {



	private static final Logger log = Logger.getLogger(ProcessLinkTypeCrosslink.class);

	//  private constructor
	private ProcessLinkTypeCrosslink() { }
	
	public static ProcessLinkTypeCrosslink getInstance() { return new ProcessLinkTypeCrosslink(); }
	

	/**
	 * result from getCrosslinkProteinMappings method
	 *
	 */
	public static class GetCrosslinkProteinMappingsResult {
		
		private boolean noProteinMappings;
		
		private List<GetCrosslinkProteinMappingsSinglePeptideData> getCrosslinkroteinMappingsSinglePeptideDataList = new ArrayList<>();
		
		/** 
		 * No Protein mappings (Either peptide has No Protein mappings) 
		 * @return
		 */
		public boolean isNoProteinMappings() {
			return noProteinMappings;
		}
	}
	

	/**
	 * Data for each peptide in the crosslink
	 *
	 */
	private static class GetCrosslinkProteinMappingsSinglePeptideData {

		private boolean noProteinMappings;
		
		private List<SrchRepPeptProtSeqIdPosCrosslinkDTO_ProteinImporterContainer_Pair> srchRepPeptProtSeqIdPosCrosslinkDTO_ProteinImporterContainer_PairList_Peptide;
		private PerPeptideData perPeptideData;
	}

	
	
	private static class SrchRepPeptProtSeqIdPosCrosslinkDTO_ProteinImporterContainer_Pair {
		
		ProteinImporterContainer proteinImporterContainer;
		
		SrchRepPeptProtSeqIdPosCrosslinkDTO srchRepPeptProtSeqIdPosCrosslinkDTO;
	}
	
	


	/**
	 * Get Protein Mappings for crosslink reported peptide
	 * 
	 * The PeptideDTO entries are saved to the DB in this step, but could be delayed
	 * 
	 * @param reportedPeptide
	 * @param linkerList
	 * @param linkerListStringForErrorMsgs
	 * @return
	 * @throws Exception
	 */
	public GetCrosslinkProteinMappingsResult getCrosslinkProteinMappings( 
			
			ReportedPeptide reportedPeptide, 

			List<ILinker> linkerList,
			String linkerListStringForErrorMsgs
			
			) throws Exception {
		
		

		GetCrosslinkProteinMappingsResult getCrosslinkMappingsResult = new GetCrosslinkProteinMappingsResult();
		
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
		
		int peptideNumberInt = 0;
		
		for ( Peptide peptide : peptideList ) {
		
			peptideNumberInt++;
			
			GetCrosslinkProteinMappingsSinglePeptideData getCrosslinkroteinMappingsSinglePeptideData =
					getProteinMappingForSinglePeptide( 
							peptide, 
							linkerList, 
							linkerListStringForErrorMsgs, 
							reportedPeptide, 
							Integer.toString( peptideNumberInt ) );
			
			getCrosslinkMappingsResult.getCrosslinkroteinMappingsSinglePeptideDataList.add( getCrosslinkroteinMappingsSinglePeptideData );
			
			if ( getCrosslinkroteinMappingsSinglePeptideData.noProteinMappings ) {
				
				getCrosslinkMappingsResult.noProteinMappings = true;
			}
		}
		
		return getCrosslinkMappingsResult;
	}
	
	
	/**
	 * @param peptide
	 * @param linkerList
	 * @param linkerListStringForErrorMsgs
	 * @param reportedPeptide
	 * @param peptideNumber
	 * @return
	 * @throws Exception
	 */
	private GetCrosslinkProteinMappingsSinglePeptideData getProteinMappingForSinglePeptide( 
			
			Peptide peptide, 
			
			List<ILinker> linkerList,
			String linkerListStringForErrorMsgs,
			
			ReportedPeptide reportedPeptide,
			String peptideNumber
			) throws Exception {
		
		
		GetCrosslinkProteinMappingsSinglePeptideData getCrosslinkroteinMappingsSinglePeptideData = new GetCrosslinkProteinMappingsSinglePeptideData();

		
		PerPeptideData perPeptideData = GetPerPeptideData.getInstance().getPerPeptideData( peptide );
		
		getCrosslinkroteinMappingsSinglePeptideData.perPeptideData = perPeptideData;

		PeptideDTO peptideDTO = perPeptideData.getPeptideDTO();
		
		//  Get peptideLinkedPosition from peptide object
		
		int peptideCrossLinkPosition = getCrosslinkLinkedPosition( peptide, peptideNumber, reportedPeptide );
		
		Set<Integer> peptideMonolinkPositions = 
				GetPeptideMonolinkPositions.getInstance().getPeptideMonolinkPositions( peptide );
		

		//  Create partial SrchRepPeptPeptideDTO peptide level record

		SrchRepPeptPeptideDTO srchRepPeptPeptideDTO = new SrchRepPeptPeptideDTO();
		
		srchRepPeptPeptideDTO.setPeptideId( peptideDTO.getId() );
		srchRepPeptPeptideDTO.setPeptidePosition_1( peptideCrossLinkPosition );

		perPeptideData.setSrchRepPeptPeptideDTO( srchRepPeptPeptideDTO );

		//  Get all proteins that the peptide is found in.
		
		Collection<ProteinImporterContainer> proteinMatches_Peptide = 
				GetProteinsForPeptide.getInstance()
				.getProteinsForPeptides( peptideDTO.getSequence() );
		
		//  Process proteinMatches_Peptide and return the proteins where the links in the peptide are linkable
		//  in the proteins
		
		// get proteins and linkable positions in those proteins that are mapped to by the given peptides and positions
		Map<ProteinImporterContainer, Collection<Integer>> proteinMap = 

				GetLinkableProteinsAndPositions.getInstance()
				.getCrosslinkLinkableProteinsAndPositions( 
						reportedPeptide,
						peptide,
						peptideDTO.getSequence(), 
						peptideCrossLinkPosition,
						peptideMonolinkPositions,
						linkerList, 
						proteinMatches_Peptide );
		

		if( proteinMap.size() < 1 ) {
			
			String msg = null;
			
			if ( peptideMonolinkPositions != null && ( ! peptideMonolinkPositions.isEmpty() ) ) {

				msg = "Could not map this peptide and link position and monolink positions to any protein in the Proxl XML file for peptide " 
					+ peptide.getSequence()
					+ " at crosslink position: " + peptideCrossLinkPosition 
					+ ", monolink position(s): " + StringUtils.join( peptideMonolinkPositions, ", " )
					+ " for "
					 + " linker(s).  reportedPeptide sequence: " + reportedPeptide.getReportedPeptideString();
				
			} else {

				msg = "Could not map this peptide and link position to any protein in the Proxl XML file for " 
					+ peptide.getSequence()
					+ " at crosslink position: " + peptideCrossLinkPosition 
					+ " for "
					 + " linker(s).  reportedPeptide sequence: " + reportedPeptide.getReportedPeptideString();
			}
			
			log.error( msg );
			
			throw new ProxlBaseDataException( "getCrosslinks(...): " + msg );
		}
		
		///  Data in perPeptideData for Monolinks
		
		List<MonolinkContainer> monolinkContainerList = new ArrayList<>();
		perPeptideData.setMonolinkContainerList( monolinkContainerList );

		List<Integer> peptideMonolinkPositionList = perPeptideData.getMonolinkPositionList();
		
		
		
		List <SrchRepPeptProtSeqIdPosCrosslinkDTO_ProteinImporterContainer_Pair> srchRepPeptProtSeqIdPosCrosslinkDTO_ProteinImporterContainer_PairList = new ArrayList<>();
		
		for( Map.Entry<ProteinImporterContainer, Collection<Integer>> proteinMapEntry : proteinMap.entrySet() ) {

			ProteinImporterContainer proteinImporterContainer = proteinMapEntry.getKey();
			
			Collection<Integer> proteinCrosslinkPositions = proteinMapEntry.getValue();
			
			for( Integer proteinCrosslinkPosition : proteinCrosslinkPositions ) {

				SrchRepPeptProtSeqIdPosCrosslinkDTO srchRepPeptProtSeqIdPosCrosslinkDTO = new SrchRepPeptProtSeqIdPosCrosslinkDTO();

				srchRepPeptProtSeqIdPosCrosslinkDTO.setProteinSequencePosition( proteinCrosslinkPosition );
				
				SrchRepPeptProtSeqIdPosCrosslinkDTO_ProteinImporterContainer_Pair srchRepPeptProtSeqIdPosCrosslinkDTO_ProteinImporterContainer_Pair =
						new SrchRepPeptProtSeqIdPosCrosslinkDTO_ProteinImporterContainer_Pair();
				
				srchRepPeptProtSeqIdPosCrosslinkDTO_ProteinImporterContainer_Pair.srchRepPeptProtSeqIdPosCrosslinkDTO = srchRepPeptProtSeqIdPosCrosslinkDTO;
				srchRepPeptProtSeqIdPosCrosslinkDTO_ProteinImporterContainer_Pair.proteinImporterContainer = proteinImporterContainer;
				
				srchRepPeptProtSeqIdPosCrosslinkDTO_ProteinImporterContainer_PairList.add( srchRepPeptProtSeqIdPosCrosslinkDTO_ProteinImporterContainer_Pair );

				//  Process the monolink positions

				for ( Integer peptideMonolinkPosition : peptideMonolinkPositionList ) {

					//  Convert peptide monolink position to protein position
					
					int proteinMonolinkPosition = proteinCrosslinkPosition - peptideCrossLinkPosition + peptideMonolinkPosition; 
					
					SrchRepPeptProtSeqIdPosMonolinkDTO srchRepPeptProtSeqIdPosMonolinkDTO = new SrchRepPeptProtSeqIdPosMonolinkDTO();

					srchRepPeptProtSeqIdPosMonolinkDTO.setPeptidePosition( peptideMonolinkPosition );

					srchRepPeptProtSeqIdPosMonolinkDTO.setProteinSequencePosition( proteinMonolinkPosition );
					
					MonolinkContainer monolinkContainer = new MonolinkContainer();
					
					monolinkContainer.setProteinImporterContainer( proteinImporterContainer );
					monolinkContainer.setSrchRepPeptProtSeqIdPosMonolinkDTO( srchRepPeptProtSeqIdPosMonolinkDTO );

					monolinkContainerList.add( monolinkContainer );
				}

			}  //end looping over protein positions
		
		}  //end looping over proteins
		

		if ( srchRepPeptProtSeqIdPosCrosslinkDTO_ProteinImporterContainer_PairList == null || srchRepPeptProtSeqIdPosCrosslinkDTO_ProteinImporterContainer_PairList.isEmpty() ) {

			getCrosslinkroteinMappingsSinglePeptideData.noProteinMappings = true; 
		}
		

		getCrosslinkroteinMappingsSinglePeptideData.srchRepPeptProtSeqIdPosCrosslinkDTO_ProteinImporterContainer_PairList_Peptide =
				srchRepPeptProtSeqIdPosCrosslinkDTO_ProteinImporterContainer_PairList;
		
		
		return getCrosslinkroteinMappingsSinglePeptideData;
	}
		

	/**
	 * Save crosslink data to DB
	 * 
	 * @param reportedPeptideDTO
	 * @param searchId
	 * @param getCrosslinkProteinMappingsResult
	 * @throws Exception
	 */
	public List<PerPeptideData> saveCrosslinkData( 
			
			ReportedPeptideDTO reportedPeptideDTO, 
			int searchId, 

			GetCrosslinkProteinMappingsResult getCrosslinkProteinMappingsResult,
			
			Set<Double> uniqueDynamicModMassesForTheSearch
			
			) throws Exception {

		List<PerPeptideData> perPeptideDataList = new ArrayList<>( 2 );

		for ( GetCrosslinkProteinMappingsSinglePeptideData getCrosslinkroteinMappingsSinglePeptideData : 
			getCrosslinkProteinMappingsResult.getCrosslinkroteinMappingsSinglePeptideDataList ) {
			
			saveCrosslinkDataSinglePeptide( reportedPeptideDTO, searchId, getCrosslinkroteinMappingsSinglePeptideData, uniqueDynamicModMassesForTheSearch );
			
			perPeptideDataList.add( getCrosslinkroteinMappingsSinglePeptideData.perPeptideData );
		}
		
		return perPeptideDataList;
	}
	

	/**
	 * @param reportedPeptideDTO
	 * @param searchId
	 * @param getCrosslinkroteinMappingsSinglePeptideData
	 * @throws Exception
	 */
	private void saveCrosslinkDataSinglePeptide( 
			
			ReportedPeptideDTO reportedPeptideDTO, 
			int searchId, 

			GetCrosslinkProteinMappingsSinglePeptideData getCrosslinkroteinMappingsSinglePeptideData,
			
			Set<Double> uniqueDynamicModMassesForTheSearch
			
			) throws Exception {
		
		//  Save ProteinImporterContainer if needed first since used in SavePerPeptideData.getInstance().savePerPeptideData(...)

		for ( SrchRepPeptProtSeqIdPosCrosslinkDTO_ProteinImporterContainer_Pair srchRepPeptProtSeqIdPosCrosslinkDTO_ProteinImporterContainer_Pair : 
			getCrosslinkroteinMappingsSinglePeptideData.srchRepPeptProtSeqIdPosCrosslinkDTO_ProteinImporterContainer_PairList_Peptide ) {
			
			ProteinImporterContainer proteinImporterContainer = srchRepPeptProtSeqIdPosCrosslinkDTO_ProteinImporterContainer_Pair.proteinImporterContainer;
			
			proteinImporterContainer.setSearchId( searchId );
			
			ProteinImporterContainerDAO.getInstance().saveProteinImporterContainerIfNeeded( proteinImporterContainer );
		}
			
		
		PerPeptideData perPeptideData = getCrosslinkroteinMappingsSinglePeptideData.perPeptideData;

		//  Save SrchRepPeptPeptideDTO, SrchRepPeptPeptDynamicModDTO, SrchRepPeptProtSeqIdPosMonolinkDTO
		
		SavePerPeptideData.getInstance().savePerPeptideData( perPeptideData, searchId, reportedPeptideDTO );

		
		//  srchRepPeptPeptideDTO saved in savePerPeptideData(...)
		SrchRepPeptPeptideDTO srchRepPeptPeptideDTO = perPeptideData.getSrchRepPeptPeptideDTO();

		int searchReportedPeptidepeptideId = srchRepPeptPeptideDTO.getId();
		
		PeptideDTO peptideDTO = perPeptideData.getPeptideDTO();
		
		int peptideLength = peptideDTO.getSequence().length();
		
		int peptidePosition = srchRepPeptPeptideDTO.getPeptidePosition_1();
		

		//  Save Crosslink Protein Mappings 
		
		
		
		for ( SrchRepPeptProtSeqIdPosCrosslinkDTO_ProteinImporterContainer_Pair srchRepPeptProtSeqIdPosCrosslinkDTO_ProteinImporterContainer_Pair : 
			getCrosslinkroteinMappingsSinglePeptideData.srchRepPeptProtSeqIdPosCrosslinkDTO_ProteinImporterContainer_PairList_Peptide ) {
			

			ProteinImporterContainer proteinImporterContainer = srchRepPeptProtSeqIdPosCrosslinkDTO_ProteinImporterContainer_Pair.proteinImporterContainer;
						
			SrchRepPeptProtSeqIdPosCrosslinkDTO srchRepPeptProtSeqIdPosCrosslinkDTO = srchRepPeptProtSeqIdPosCrosslinkDTO_ProteinImporterContainer_Pair.srchRepPeptProtSeqIdPosCrosslinkDTO;
			
			srchRepPeptProtSeqIdPosCrosslinkDTO.setProteinSequenceId( proteinImporterContainer.getProteinSequenceDTO().getId() );
			
			srchRepPeptProtSeqIdPosCrosslinkDTO.setSearchId( searchId );
			srchRepPeptProtSeqIdPosCrosslinkDTO.setReportedPeptideId( reportedPeptideDTO.getId() );
			srchRepPeptProtSeqIdPosCrosslinkDTO.setSearchReportedPeptidepeptideId( searchReportedPeptidepeptideId );
			
			DB_Insert_SrchRepPeptProtSeqIdPosCrosslinkDAO.getInstance().save( srchRepPeptProtSeqIdPosCrosslinkDTO );
			
			
			
			//  Insert PeptideProteinPositionDTO record for protein coverage
						
			int proteinSequencePosition = srchRepPeptProtSeqIdPosCrosslinkDTO.getProteinSequencePosition();
			
			int proteinStartPosition = proteinSequencePosition - peptidePosition + 1;
			
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
			item.setLinkType( XLinkUtils.TYPE_CROSSLINK );
			item.setReportedPeptideId( reportedPeptideDTO.getId() );
			item.setSearchId( searchId );
			
			DB_Insert_SearchReportedPeptideDynamicModLookupDAO.getInstance().saveToDatabaseIgnoreDuplicates( item );
			
			//  Accumulate mod mass values across the search 
			uniqueDynamicModMassesForTheSearch.add( srchRepPeptPeptDynamicModDTO.getMass() );
		}
		
		
		//   Determine if peptide is only mapped to one protein and save that to perPeptideData
		
		List<SrchRepPeptProtSeqIdPosCrosslinkDTO_ProteinImporterContainer_Pair> srchRepPeptProtSeqIdPosCrosslinkDTO_ProteinImporterContainer_PairList_Peptide =
				getCrosslinkroteinMappingsSinglePeptideData.srchRepPeptProtSeqIdPosCrosslinkDTO_ProteinImporterContainer_PairList_Peptide;	
		
		if ( srchRepPeptProtSeqIdPosCrosslinkDTO_ProteinImporterContainer_PairList_Peptide == null 
				|| srchRepPeptProtSeqIdPosCrosslinkDTO_ProteinImporterContainer_PairList_Peptide.isEmpty() ) {
			
			String msg = "ERROR: srchRepPeptProtSeqIdPosCrosslinkDTO_ProteinImporterContainer_PairList_Peptide is null or is empty.";
			log.error( msg );
			throw new ProxlImporterInteralException(msg);
		}
		
		if ( srchRepPeptProtSeqIdPosCrosslinkDTO_ProteinImporterContainer_PairList_Peptide.size() == 1 ) {
			
			//  Only one mapped protein record so peptide is unique
			
			perPeptideData.setPeptideIdMapsToOnlyOneProtein( true );
		
		} else {
			
			//  More than one mapped protein record so they just have to all have the same protein sequence id
			//  in order for the peptide to be unique
			
			boolean peptideIdMapsToOnlyOneProtein = true;

			SrchRepPeptProtSeqIdPosCrosslinkDTO_ProteinImporterContainer_Pair firstSrchRepPeptProtSeqIdPosCrosslinkDTO_ProteinImporterContainer_Pair =
					srchRepPeptProtSeqIdPosCrosslinkDTO_ProteinImporterContainer_PairList_Peptide.get( 0 );

			SrchRepPeptProtSeqIdPosCrosslinkDTO firstSrchRepPeptProtSeqIdPosCrosslinkDTO = firstSrchRepPeptProtSeqIdPosCrosslinkDTO_ProteinImporterContainer_Pair.srchRepPeptProtSeqIdPosCrosslinkDTO;

			int firstProteinSequenceId = firstSrchRepPeptProtSeqIdPosCrosslinkDTO.getProteinSequenceId();
			
			for ( SrchRepPeptProtSeqIdPosCrosslinkDTO_ProteinImporterContainer_Pair item : srchRepPeptProtSeqIdPosCrosslinkDTO_ProteinImporterContainer_PairList_Peptide ) {

				if ( firstProteinSequenceId != item.srchRepPeptProtSeqIdPosCrosslinkDTO.getProteinSequenceId() ) {
					
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
	
	


}