package org.yeastrc.proxl.import_xml_to_db.process_input;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SearchReportedPeptideDynamicModLookupDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SrchRepPeptNrseqIdPosCrosslinkDAO;
import org.yeastrc.proxl.import_xml_to_db.dto.SearchReportedPeptideDynamicModLookupDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptNrseqIdPosCrosslinkDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptPeptDynamicModDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptPeptideDTO;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.proxl.import_xml_to_db.objects.PerPeptideData;
import org.yeastrc.proxl_import.api.xml_dto.LinkedPosition;
import org.yeastrc.proxl_import.api.xml_dto.LinkedPositions;
import org.yeastrc.proxl_import.api.xml_dto.Peptide;
import org.yeastrc.proxl_import.api.xml_dto.Peptides;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptide;
import org.yeastrc.xlink.dto.NRProteinDTO;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;
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

		public void setNoProteinMappings(boolean noProteinMappings) {
			this.noProteinMappings = noProteinMappings;
		}

	}
	
	/**
	 * Data for each peptide in the crosslink
	 *
	 */
	private static class GetCrosslinkProteinMappingsSinglePeptideData {

		private boolean noProteinMappings;
		
		private List<SrchRepPeptNrseqIdPosCrosslinkDTO> srchRepPeptNrseqIdPosCrosslinkDTOList_Peptide;
		private PerPeptideData perPeptideData;
	}

	


	/**
	 * Get Protein Mappings for crosslink reported peptide
	 * 
	 * The PeptideDTO entries are saved to the DB in this step since used for Protein Mappings
	 * 
	 * @param reportedPeptide
	 * @param linkerList
	 * @param linkerListStringForErrorMsgs
	 * @param proteinNameDecoyPrefixList
	 * @param nrseqDatabaseId
	 * @return
	 * @throws Exception
	 */
	public GetCrosslinkProteinMappingsResult getCrosslinkProteinMappings( 
			
			ReportedPeptide reportedPeptide, 

			List<ILinker> linkerList,
			String linkerListStringForErrorMsgs,
			
			List<String> proteinNameDecoyPrefixList,
			
			int nrseqDatabaseId
			
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
							nrseqDatabaseId, 
							linkerList, 
							linkerListStringForErrorMsgs, 
							proteinNameDecoyPrefixList, 
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
	 * @param nrseqDatabaseId
	 * @param linkerList
	 * @param linkerListStringForErrorMsgs
	 * @param proteinNameDecoyPrefixList
	 * @param reportedPeptide
	 * @param peptideNumber
	 * @return
	 * @throws Exception
	 */
	private GetCrosslinkProteinMappingsSinglePeptideData getProteinMappingForSinglePeptide( 
			
			Peptide peptide, 
			
			int nrseqDatabaseId,
			List<ILinker> linkerList,
			String linkerListStringForErrorMsgs,
			
			List<String> proteinNameDecoyPrefixList,
			
			ReportedPeptide reportedPeptide,
			String peptideNumber
			) throws Exception {
		
		
		GetCrosslinkProteinMappingsSinglePeptideData getCrosslinkroteinMappingsSinglePeptideData = new GetCrosslinkProteinMappingsSinglePeptideData();

		
		PerPeptideData perPeptideData = GetPerPeptideData.getInstance().getPerPeptideData( peptide );
		
		getCrosslinkroteinMappingsSinglePeptideData.perPeptideData = perPeptideData;

		PeptideDTO peptideDTO = perPeptideData.getPeptideDTO();
		
		int linkedPosition = getCrosslinkLinkedPosition( peptide, peptideNumber, reportedPeptide );
		

		//  Create partial SrchRepPeptPeptideDTO peptide level record

		SrchRepPeptPeptideDTO srchRepPeptPeptideDTO = new SrchRepPeptPeptideDTO();
		
		srchRepPeptPeptideDTO.setPeptideId( peptideDTO.getId() );
		srchRepPeptPeptideDTO.setPeptidePosition_1( linkedPosition );

		perPeptideData.setSrchRepPeptPeptideDTO( srchRepPeptPeptideDTO );

		
		
		Collection<NRProteinDTO> proteinMatches = 
				GetProteinsForPeptidesAndInsertNrseqPeptideProteinEntries.getInstance()
				.getProteinsForPeptidesAndInsertNrseqPeptideProteinEntries( peptideDTO, proteinNameDecoyPrefixList, nrseqDatabaseId );
		
		
		// get proteins and linkable positions in those proteins that are mapped to by the given peptides and positions
		Map<NRProteinDTO, Collection<Integer>> proteinMap = 
				GetLinkableProteinsAndPositions.getInstance()
					.getLinkableProteinsAndPositions( peptideDTO, linkedPosition, linkerList, proteinMatches );
		
		

		if( proteinMap.size() < 1 ) {
			String msg = "getCrosslinks(...): No linkable protein positions found for " + peptide.getSequence() +
					" at position " + linkedPosition + " for "
					 + " linker.  reportedPeptide sequence: " + reportedPeptide.getReportedPeptideString();
			log.error( msg );
			
			throw new Exception( msg );
		}
		
		List <SrchRepPeptNrseqIdPosCrosslinkDTO> srchRepPeptNrseqIdPosCrosslinkDTOList = new ArrayList<>();
		
		for( Map.Entry<NRProteinDTO, Collection<Integer>> proteinMapEntry : proteinMap.entrySet() ) {

			NRProteinDTO protein = proteinMapEntry.getKey();
			
			Collection<Integer> proteinPositions = proteinMapEntry.getValue();
			
			for( Integer proteinPosition : proteinPositions ) {

				SrchRepPeptNrseqIdPosCrosslinkDTO srchRepPeptNrseqIdPosCrosslinkDTO = new SrchRepPeptNrseqIdPosCrosslinkDTO();

				srchRepPeptNrseqIdPosCrosslinkDTO.setNrseqId( protein.getNrseqId() );
				srchRepPeptNrseqIdPosCrosslinkDTO.setNrseqPosition( proteinPosition );
				
				srchRepPeptNrseqIdPosCrosslinkDTOList.add( srchRepPeptNrseqIdPosCrosslinkDTO );

			}  //end looping over proteinpositions
		
		}  //end looping over proteins
		

		if ( srchRepPeptNrseqIdPosCrosslinkDTOList == null || srchRepPeptNrseqIdPosCrosslinkDTOList.isEmpty() ) {

			getCrosslinkroteinMappingsSinglePeptideData.noProteinMappings = true; 
		}
		

		PopulateSrchRepPeptNrseqIdPosMonolinkDTOListOnPerPeptideDataObject.getInstance()
		.populateSrchRepPeptNrseqIdPosMonolinkDTOListOnPerPeptideDataObject( perPeptideData, linkerList, proteinMatches );

		getCrosslinkroteinMappingsSinglePeptideData.srchRepPeptNrseqIdPosCrosslinkDTOList_Peptide =
				srchRepPeptNrseqIdPosCrosslinkDTOList;
		
		
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
		
		
		PerPeptideData perPeptideData = getCrosslinkroteinMappingsSinglePeptideData.perPeptideData;

		//  Save SrchRepPeptPeptideDTO, SrchRepPeptPeptDynamicModDTO, SrchRepPeptNrseqIdPosMonolinkDTO
		
		SavePerPeptideData.getInstance().savePerPeptideData( perPeptideData, searchId, reportedPeptideDTO );

		
		//  srchRepPeptPeptideDTO saved in savePerPeptideData(...)
		SrchRepPeptPeptideDTO srchRepPeptPeptideDTO = perPeptideData.getSrchRepPeptPeptideDTO();

		int searchReportedPeptidepeptideId = srchRepPeptPeptideDTO.getId();

		//  Save Crosslink Protein Mappings 
		
		for ( SrchRepPeptNrseqIdPosCrosslinkDTO srchRepPeptNrseqIdPosCrosslinkDTO : getCrosslinkroteinMappingsSinglePeptideData.srchRepPeptNrseqIdPosCrosslinkDTOList_Peptide ) {
			
			srchRepPeptNrseqIdPosCrosslinkDTO.setSearchId( searchId );
			srchRepPeptNrseqIdPosCrosslinkDTO.setReportedPeptideId( reportedPeptideDTO.getId() );
			srchRepPeptNrseqIdPosCrosslinkDTO.setSearchReportedPeptidepeptideId( searchReportedPeptidepeptideId );
			
			DB_Insert_SrchRepPeptNrseqIdPosCrosslinkDAO.getInstance().save( srchRepPeptNrseqIdPosCrosslinkDTO );
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