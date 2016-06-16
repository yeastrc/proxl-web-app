package org.yeastrc.proxl.import_xml_to_db.process_input;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SearchReportedPeptideDynamicModLookupDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SrchRepPeptNrseqIdPosLooplinkDAO;
import org.yeastrc.proxl.import_xml_to_db.dto.SearchReportedPeptideDynamicModLookupDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptNrseqIdPosLooplinkDTO;
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
		
		private List<SrchRepPeptNrseqIdPosLooplinkDTO> srchRepPeptNrseqIdPosLooplinkDTOList;


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
	
	
	

	/**
	 * Get Protein Mappings for looplink reported peptide
	 * 
	 * The PeptideDTO is saved to the DB in this step since used for Protein Mappings
	 * 
	 * @param reportedPeptide
	 * @param linkerList
	 * @param linkerListStringForErrorMsgs
	 * @param proteinNameDecoyPrefixList
	 * @param nrseqDatabaseId
	 * @return
	 * @throws Exception
	 */
	public GetLooplinkProteinMappingsResult getLooplinkroteinMappings( 
			
			ReportedPeptide reportedPeptide, 

			List<ILinker> linkerList,
			String linkerListStringForErrorMsgs,
			
			List<String> proteinNameDecoyPrefixList,
			
			int nrseqDatabaseId
			
			) throws Exception {
		
		

		GetLooplinkProteinMappingsResult getLooplinkMappingsResult = new GetLooplinkProteinMappingsResult();
		
		List<SrchRepPeptNrseqIdPosLooplinkDTO> srchRepPeptNrseqIdPosLooplinkDTOList = new ArrayList<>();
		getLooplinkMappingsResult.srchRepPeptNrseqIdPosLooplinkDTOList = srchRepPeptNrseqIdPosLooplinkDTOList;
		
		
		
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
			
			int linkedPosition_temp = linkedPosition_1;
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
		
		Collection<NRProteinDTO> proteinMatches_Peptide = 
				GetProteinsForPeptidesAndInsertNrseqPeptideProteinEntries.getInstance()
				.getProteinsForPeptidesAndInsertNrseqPeptideProteinEntries( peptideDTO, proteinNameDecoyPrefixList, nrseqDatabaseId );
		
		
		
		Map<NRProteinDTO, Collection<List<Integer>>> proteinMap = 

				GetLinkableProteinsAndPositions.getInstance()
				.getLinkableProteinsAndPositionsForLooplink( 
						peptideDTO, 
						linkedPosition_1, 
						linkedPosition_2 , 
						linkerList, 
						proteinMatches_Peptide );
		
			
		if( proteinMap.size() < 1 ) {
			String msg = "getLooplinks(...): No linkable protein positions found for " + peptideDTO.getSequence() 
					+ " at positions " + linkedPosition_1 + "," + linkedPosition_2 
					+ ".  reportedPeptide sequence: " + reportedPeptide.getReportedPeptideString();
			log.error( msg );
			
			throw new Exception( msg );
		}
		
		
		
		for( Map.Entry<NRProteinDTO, Collection<List<Integer>>> proteinMapEntry : proteinMap.entrySet() ) {
				
			NRProteinDTO protein = proteinMapEntry.getKey();

			Collection<List<Integer>> proteinPositions = proteinMapEntry.getValue();
			
			for( List<Integer> proteinPositions_1_2 : proteinPositions ) {

				if ( proteinPositions_1_2.size() != 2 ) {
					
					String msg = "List<Integer> in Map<NRProteinDTO, Collection<List<Integer>>> must contain 2 entries.";
					log.error( msg );
					throw new Exception(msg);
				}
				
				// a single looplink entry
				SrchRepPeptNrseqIdPosLooplinkDTO looplink = new SrchRepPeptNrseqIdPosLooplinkDTO();

				looplink.setNrseqId( protein.getNrseqId() );
				looplink.setNrseqPosition_1( proteinPositions_1_2.get( 0 ) );
				looplink.setNrseqPosition_2( proteinPositions_1_2.get( 1 ) );
				
				srchRepPeptNrseqIdPosLooplinkDTOList.add( looplink );

			}  //end looping over proteinpositions
		
		}  //end looping over proteins
		

		if ( srchRepPeptNrseqIdPosLooplinkDTOList == null || srchRepPeptNrseqIdPosLooplinkDTOList.isEmpty() ) {

			getLooplinkMappingsResult.noProteinMappings = true; 
		}
		

		PopulateSrchRepPeptNrseqIdPosMonolinkDTOListOnPerPeptideDataObject.getInstance()
		.populateSrchRepPeptNrseqIdPosMonolinkDTOListOnPerPeptideDataObject( perPeptideData, linkerList, proteinMatches_Peptide );

		
		
		
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

		PerPeptideData perPeptideData = getLooplinkProteinMappingsResult.perPeptideData;


		
		//  Save SrchRepPeptPeptideDTO, SrchRepPeptPeptDynamicModDTO, SrchRepPeptNrseqIdPosMonolinkDTO
		
		SavePerPeptideData.getInstance().savePerPeptideData( perPeptideData, searchId, reportedPeptideDTO );

		
		//  srchRepPeptPeptideDTO saved in savePerPeptideData(...)
		SrchRepPeptPeptideDTO srchRepPeptPeptideDTO = perPeptideData.getSrchRepPeptPeptideDTO();

		int searchReportedPeptidepeptideId = srchRepPeptPeptideDTO.getId();

		//  Save Looplink Protein Mappings 
		
		for ( SrchRepPeptNrseqIdPosLooplinkDTO srchRepPeptNrseqIdPosLooplinkDTO : getLooplinkProteinMappingsResult.srchRepPeptNrseqIdPosLooplinkDTOList ) {
			
			srchRepPeptNrseqIdPosLooplinkDTO.setSearchId( searchId );
			srchRepPeptNrseqIdPosLooplinkDTO.setReportedPeptideId( reportedPeptideDTO.getId() );
			srchRepPeptNrseqIdPosLooplinkDTO.setSearchReportedPeptidepeptideId( searchReportedPeptidepeptideId );
			
			DB_Insert_SrchRepPeptNrseqIdPosLooplinkDAO.getInstance().save( srchRepPeptNrseqIdPosLooplinkDTO );
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