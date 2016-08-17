package org.yeastrc.proxl.import_xml_to_db.process_input;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dao.ProteinImporterContainerDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SearchReportedPeptideDynamicModLookupDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SrchRepPeptProtSeqIdPosUnlinkedDimerDAO;
import org.yeastrc.proxl.import_xml_to_db.dto.SearchReportedPeptideDynamicModLookupDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptProtSeqIdPosUnlinkedDimerDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptPeptDynamicModDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptPeptideDTO;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterInteralException;
import org.yeastrc.proxl.import_xml_to_db.objects.PerPeptideData;
import org.yeastrc.proxl.import_xml_to_db.objects.ProteinImporterContainer;
import org.yeastrc.proxl_import.api.xml_dto.Peptide;
import org.yeastrc.proxl_import.api.xml_dto.Peptides;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptide;
import org.yeastrc.proxl.import_xml_to_db.dto.PeptideDTO;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;
import org.yeastrc.xlink.linkable_positions.linkers.ILinker;
import org.yeastrc.xlink.utils.XLinkUtils;





/**
 * Proxl internal "Dimer" is two peptides that are not a crosslink (not linked to each other)
 *
 */
public class ProcessLinkTypeDimerAsDefinedByProxl {



	private static final Logger log = Logger.getLogger(ProcessLinkTypeDimerAsDefinedByProxl.class);

	//  private constructor
	private ProcessLinkTypeDimerAsDefinedByProxl() { }
	
	public static ProcessLinkTypeDimerAsDefinedByProxl getInstance() { return new ProcessLinkTypeDimerAsDefinedByProxl(); }
	


	/**
	 * result from GetDimerProteinMappingsSinglePeptideData method
	 *
	 */
	public static class GetDimerProteinMappingsResult {
		
		private boolean noProteinMappings;
		
		private List<GetDimerProteinMappingsSinglePeptideData> getDimerroteinMappingsSinglePeptideDataList = new ArrayList<>();
		
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
	 * Data for each peptide in the dimer
	 *
	 */
	private static class GetDimerProteinMappingsSinglePeptideData {

		private boolean noProteinMappings;
		
		private List<SrchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_Pair> srchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_PairList_Peptide;
		private PerPeptideData perPeptideData;
	}
	
	

	private static class SrchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_Pair {
		
		ProteinImporterContainer proteinImporterContainer;
		
		SrchRepPeptProtSeqIdPosUnlinkedDimerDTO srchRepPeptProtSeqIdPosUnlinkedDimerDTO;
	}
	
	
	
	/**
	 * Get Protein Mappings for dimer reported peptide
	 * 
	 * The PeptideDTO entries are saved to the DB in this step since used for Protein Mappings
	 * 
	 * @param reportedPeptide
	 * @param linkerList
	 * @param linkerListStringForErrorMsgs
	 * @return
	 * @throws Exception
	 */
	public GetDimerProteinMappingsResult getDimerProteinMappings( 
			
			ReportedPeptide reportedPeptide, 

			List<ILinker> linkerList,
			String linkerListStringForErrorMsgs
			
			) throws Exception {
		
		

		GetDimerProteinMappingsResult getDimerMappingsResult = new GetDimerProteinMappingsResult();
		
		Peptides peptides =
				reportedPeptide.getPeptides();

		if ( peptides == null ) {
			String msg = "There must be 2 peptides for Dimer reported peptide: " + reportedPeptide.getReportedPeptideString();
			log.error( msg );
			throw new ProxlImporterDataException(msg);
		}
		
		List<Peptide> peptideList = peptides.getPeptide();

		if ( peptideList == null || peptideList.size() != 2 ) {
			String msg = "There must be 2 peptides for Dimer for reported peptide: " + reportedPeptide.getReportedPeptideString();
			log.error( msg );
			throw new ProxlImporterDataException(msg);
		}
		
		int peptideNumberInt = 0;
		
		for ( Peptide peptide : peptideList ) {
		
			peptideNumberInt++;
			
			GetDimerProteinMappingsSinglePeptideData getDimerroteinMappingsSinglePeptideData =
					getProteinMappingForSinglePeptide( 
							peptide, 
							linkerList, 
							linkerListStringForErrorMsgs, 
							reportedPeptide, 
							Integer.toString( peptideNumberInt ) );
			
			getDimerMappingsResult.getDimerroteinMappingsSinglePeptideDataList.add( getDimerroteinMappingsSinglePeptideData );
			
			if ( getDimerroteinMappingsSinglePeptideData.noProteinMappings ) {
				
				getDimerMappingsResult.noProteinMappings = true;
			}
		}
		
		return getDimerMappingsResult;
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
	private GetDimerProteinMappingsSinglePeptideData getProteinMappingForSinglePeptide( 
			
			Peptide peptide, 
			
			List<ILinker> linkerList,
			String linkerListStringForErrorMsgs,
			
			ReportedPeptide reportedPeptide,
			String peptideNumber
			) throws Exception {
		
		
		GetDimerProteinMappingsSinglePeptideData getDimerProteinMappingsSinglePeptideData = new GetDimerProteinMappingsSinglePeptideData();

		
		PerPeptideData perPeptideData = GetPerPeptideData.getInstance().getPerPeptideData( peptide );

		getDimerProteinMappingsSinglePeptideData.perPeptideData = perPeptideData;

		PeptideDTO peptideDTO = perPeptideData.getPeptideDTO();


		//  Create partial SrchRepPeptPeptideDTO peptide level record

		SrchRepPeptPeptideDTO srchRepPeptPeptideDTO = new SrchRepPeptPeptideDTO();
		
		srchRepPeptPeptideDTO.setPeptideId( peptideDTO.getId() );

		perPeptideData.setSrchRepPeptPeptideDTO( srchRepPeptPeptideDTO );

		

		Collection<ProteinImporterContainer> proteinMatches_Peptide = 
				GetProteinsForPeptide.getInstance()
				.getProteinsForPeptides( peptideDTO.getSequence() );
				

		if( proteinMatches_Peptide.size() < 1 ) {
			String msg = "No proteins found for " + peptide.getSequence() +
					" for "
					 + " linker.  reportedPeptide sequence: " + reportedPeptide.getReportedPeptideString();
			log.error( "getProteinMappingForSinglePeptide(...): " + msg );
			
			throw new ProxlImporterDataException( msg );
		}
		
		List <SrchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_Pair> srchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_PairList = new ArrayList<>();
		
		for( ProteinImporterContainer proteinImporterContainer : proteinMatches_Peptide ) {

			SrchRepPeptProtSeqIdPosUnlinkedDimerDTO srchRepPeptProtSeqIdPosUnlinkedDimerDTO = new SrchRepPeptProtSeqIdPosUnlinkedDimerDTO();

			SrchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_Pair srchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_Pair =
					new SrchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_Pair();

			srchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_Pair.proteinImporterContainer = proteinImporterContainer;
			srchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_Pair.srchRepPeptProtSeqIdPosUnlinkedDimerDTO = srchRepPeptProtSeqIdPosUnlinkedDimerDTO;
			
			srchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_PairList.add( srchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_Pair );

		}  //end looping over proteins
		

		if ( srchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_PairList == null || srchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_PairList.isEmpty() ) {

			getDimerProteinMappingsSinglePeptideData.noProteinMappings = true; 
		}
		

		PopulateSrchRepPeptProtSeqIdPosMonolinkDTOListOnPerPeptideDataObject.getInstance()
		.populateSrchRepPeptProtSeqIdPosMonolinkDTOListOnPerPeptideDataObject( perPeptideData, linkerList, proteinMatches_Peptide );

		getDimerProteinMappingsSinglePeptideData.srchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_PairList_Peptide =
				srchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_PairList;
		
		
		return getDimerProteinMappingsSinglePeptideData;
	}
		

	/**
	 * Save dimer data to DB
	 * 
	 * @param reportedPeptideDTO
	 * @param searchId
	 * @param getDimerMappingsResult
	 * @throws Exception
	 */
	public  List<PerPeptideData> saveDimerData( 
			
			ReportedPeptideDTO reportedPeptideDTO, 
			int searchId, 

			GetDimerProteinMappingsResult getDimerMappingsResult,
			
			Set<Double> uniqueDynamicModMassesForTheSearch
			
			) throws Exception {

		List<PerPeptideData> perPeptideDataList = new ArrayList<>( 2 );

		for ( GetDimerProteinMappingsSinglePeptideData getDimerroteinMappingsSinglePeptideData : 
			getDimerMappingsResult.getDimerroteinMappingsSinglePeptideDataList ) {
			
			saveDimerDataSinglePeptide( reportedPeptideDTO, searchId, getDimerroteinMappingsSinglePeptideData, uniqueDynamicModMassesForTheSearch );

			perPeptideDataList.add( getDimerroteinMappingsSinglePeptideData.perPeptideData );
		}

		return perPeptideDataList;
	}
	

	/**
	 * @param reportedPeptideDTO
	 * @param searchId
	 * @param getDimerProteinMappingsSinglePeptideData
	 * @throws Exception
	 */
	private void saveDimerDataSinglePeptide( 
			
			ReportedPeptideDTO reportedPeptideDTO, 
			int searchId, 

			GetDimerProteinMappingsSinglePeptideData getDimerProteinMappingsSinglePeptideData,

			Set<Double> uniqueDynamicModMassesForTheSearch
			
			) throws Exception {
		

		//  Save ProteinImporterContainer if needed first since used in SavePerPeptideData.getInstance().savePerPeptideData(...)

		for ( SrchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_Pair srchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_Pair 
				: getDimerProteinMappingsSinglePeptideData.srchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_PairList_Peptide ) {

			ProteinImporterContainer proteinImporterContainer = srchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_Pair.proteinImporterContainer;
			
			proteinImporterContainer.setSearchId( searchId );
			
			ProteinImporterContainerDAO.getInstance().saveProteinImporterContainerIfNeeded( proteinImporterContainer );
		}
		
		

		PerPeptideData perPeptideData = getDimerProteinMappingsSinglePeptideData.perPeptideData;

		//  Save SrchRepPeptPeptideDTO, SrchRepPeptPeptDynamicModDTO, SrchRepPeptProtSeqIdPosMonolinkDTO
		
		SavePerPeptideData.getInstance().savePerPeptideData( perPeptideData, searchId, reportedPeptideDTO );

		
		//  srchRepPeptPeptideDTO saved in savePerPeptideData(...)
		SrchRepPeptPeptideDTO srchRepPeptPeptideDTO = perPeptideData.getSrchRepPeptPeptideDTO();

		int searchReportedPeptidepeptideId = srchRepPeptPeptideDTO.getId();

		//  Save Dimer Protein Mappings 
		
		for ( SrchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_Pair srchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_Pair 
				: getDimerProteinMappingsSinglePeptideData.srchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_PairList_Peptide ) {

			ProteinImporterContainer proteinImporterContainer = srchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_Pair.proteinImporterContainer;
			
			SrchRepPeptProtSeqIdPosUnlinkedDimerDTO srchRepPeptProtSeqIdPosUnlinkedDimerDTO = srchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_Pair.srchRepPeptProtSeqIdPosUnlinkedDimerDTO;

			srchRepPeptProtSeqIdPosUnlinkedDimerDTO.setProteinSequenceId( proteinImporterContainer.getProteinSequenceDTO().getId() );
			
			srchRepPeptProtSeqIdPosUnlinkedDimerDTO.setSearchId( searchId );
			srchRepPeptProtSeqIdPosUnlinkedDimerDTO.setReportedPeptideId( reportedPeptideDTO.getId() );
			srchRepPeptProtSeqIdPosUnlinkedDimerDTO.setSearchReportedPeptidepeptideId( searchReportedPeptidepeptideId );
			
			DB_Insert_SrchRepPeptProtSeqIdPosUnlinkedDimerDAO.getInstance().save( srchRepPeptProtSeqIdPosUnlinkedDimerDTO );
		}
		

		//  Save Dynamic Mod Masses into Lookup table and into Set for Search level lookup
		
		for ( SrchRepPeptPeptDynamicModDTO srchRepPeptPeptDynamicModDTO : perPeptideData.getSrchRepPeptPeptDynamicModDTOList_Peptide() ) {
		
			SearchReportedPeptideDynamicModLookupDTO item = new SearchReportedPeptideDynamicModLookupDTO();
			
			item.setDynamicModMass( srchRepPeptPeptDynamicModDTO.getMass() );
			item.setLinkType( XLinkUtils.TYPE_DIMER );
			item.setReportedPeptideId( reportedPeptideDTO.getId() );
			item.setSearchId( searchId );
			
			DB_Insert_SearchReportedPeptideDynamicModLookupDAO.getInstance().saveToDatabaseIgnoreDuplicates( item );
			
			//  Accumulate mod mass values across the search 
			uniqueDynamicModMassesForTheSearch.add( srchRepPeptPeptDynamicModDTO.getMass() );
		}

		
		//   Determine if peptide is only mapped to one protein and save that to perPeptideData
		
		List<SrchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_Pair> srchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_PairList_Peptide =
				getDimerProteinMappingsSinglePeptideData.srchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_PairList_Peptide;	
		
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

			SrchRepPeptProtSeqIdPosUnlinkedDimerDTO firstSrchRepPeptProtSeqIdPosUnlinkedDimerDTO = firstSrchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_Pair.srchRepPeptProtSeqIdPosUnlinkedDimerDTO;

			int firstProteinSequenceId = firstSrchRepPeptProtSeqIdPosUnlinkedDimerDTO.getProteinSequenceId();
			
			for ( SrchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_Pair item : srchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_PairList_Peptide ) {

				if ( firstProteinSequenceId != item.srchRepPeptProtSeqIdPosUnlinkedDimerDTO.getProteinSequenceId() ) {
					
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

		if ( getDimerProteinMappingsSinglePeptideData.srchRepPeptProtSeqIdPosUnlinkedDimerDTO_ProteinImporterContainer_PairList_Peptide.size() 
				==  1 ) {
			
			//  Peptide only maps to 1 protein so set petpideUnique to true
			
			perPeptideData.setPeptideIdMapsToOnlyOneProtein( true );
		}
	}
	
}
