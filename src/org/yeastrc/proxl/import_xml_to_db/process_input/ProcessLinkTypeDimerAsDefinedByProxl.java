package org.yeastrc.proxl.import_xml_to_db.process_input;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SearchReportedPeptideDynamicModLookupDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SrchRepPeptNrseqIdPosUnlinkedDimerDAO;
import org.yeastrc.proxl.import_xml_to_db.dto.SearchReportedPeptideDynamicModLookupDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptNrseqIdPosUnlinkedDimerDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptPeptDynamicModDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptPeptideDTO;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.proxl.import_xml_to_db.objects.PerPeptideData;
import org.yeastrc.proxl_import.api.xml_dto.Peptide;
import org.yeastrc.proxl_import.api.xml_dto.Peptides;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptide;
import org.yeastrc.xlink.dto.NRProteinDTO;
import org.yeastrc.xlink.dto.PeptideDTO;
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
		
		private List<SrchRepPeptNrseqIdPosUnlinkedDimerDTO> srchRepPeptNrseqIdPosUnlinkedDimerDTOList_Peptide;
		private PerPeptideData perPeptideData;
	}
	
	
	
	

	/**
	 * Get Protein Mappings for dimer reported peptide
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
	public GetDimerProteinMappingsResult getDimerProteinMappings( 
			
			ReportedPeptide reportedPeptide, 

			List<ILinker> linkerList,
			String linkerListStringForErrorMsgs,
			
			List<String> proteinNameDecoyPrefixList,
			
			int nrseqDatabaseId
			
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
							nrseqDatabaseId, 
							linkerList, 
							linkerListStringForErrorMsgs, 
							proteinNameDecoyPrefixList, 
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
	 * @param nrseqDatabaseId
	 * @param linkerList
	 * @param linkerListStringForErrorMsgs
	 * @param proteinNameDecoyPrefixList
	 * @param reportedPeptide
	 * @param peptideNumber
	 * @return
	 * @throws Exception
	 */
	private GetDimerProteinMappingsSinglePeptideData getProteinMappingForSinglePeptide( 
			
			Peptide peptide, 
			
			int nrseqDatabaseId,
			List<ILinker> linkerList,
			String linkerListStringForErrorMsgs,
			
			List<String> proteinNameDecoyPrefixList,
			
			ReportedPeptide reportedPeptide,
			String peptideNumber
			) throws Exception {
		
		
		GetDimerProteinMappingsSinglePeptideData getDimerroteinMappingsSinglePeptideData = new GetDimerProteinMappingsSinglePeptideData();

		
		PerPeptideData perPeptideData = GetPerPeptideData.getInstance().getPerPeptideData( peptide );

		getDimerroteinMappingsSinglePeptideData.perPeptideData = perPeptideData;

		PeptideDTO peptideDTO = perPeptideData.getPeptideDTO();


		//  Create partial SrchRepPeptPeptideDTO peptide level record

		SrchRepPeptPeptideDTO srchRepPeptPeptideDTO = new SrchRepPeptPeptideDTO();
		
		srchRepPeptPeptideDTO.setPeptideId( peptideDTO.getId() );

		perPeptideData.setSrchRepPeptPeptideDTO( srchRepPeptPeptideDTO );

		
		
		Collection<NRProteinDTO> proteinMatches = 
				GetProteinsForPeptidesAndInsertNrseqPeptideProteinEntries.getInstance()
				.getProteinsForPeptidesAndInsertNrseqPeptideProteinEntries( peptideDTO, proteinNameDecoyPrefixList, nrseqDatabaseId );
		
		

		if( proteinMatches.size() < 1 ) {
			String msg = "getProteinMappingForSinglePeptide(...): No proteins found for " + peptide.getSequence() +
					" for "
					 + " linker.  reportedPeptide sequence: " + reportedPeptide.getReportedPeptideString();
			log.error( msg );
			
			throw new Exception( msg );
		}
		
		List <SrchRepPeptNrseqIdPosUnlinkedDimerDTO> srchRepPeptNrseqIdPosUnlinkedDimerDTOList = new ArrayList<>();
		
		for( NRProteinDTO protein : proteinMatches ) {

			SrchRepPeptNrseqIdPosUnlinkedDimerDTO srchRepPeptNrseqIdPosUnlinkedDimerDTO = new SrchRepPeptNrseqIdPosUnlinkedDimerDTO();

			srchRepPeptNrseqIdPosUnlinkedDimerDTO.setNrseqId( protein.getNrseqId() );

			srchRepPeptNrseqIdPosUnlinkedDimerDTOList.add( srchRepPeptNrseqIdPosUnlinkedDimerDTO );

		}  //end looping over proteins
		

		if ( srchRepPeptNrseqIdPosUnlinkedDimerDTOList == null || srchRepPeptNrseqIdPosUnlinkedDimerDTOList.isEmpty() ) {

			getDimerroteinMappingsSinglePeptideData.noProteinMappings = true; 
		}
		

		PopulateSrchRepPeptNrseqIdPosMonolinkDTOListOnPerPeptideDataObject.getInstance()
		.populateSrchRepPeptNrseqIdPosMonolinkDTOListOnPerPeptideDataObject( perPeptideData, linkerList, proteinMatches );

		getDimerroteinMappingsSinglePeptideData.srchRepPeptNrseqIdPosUnlinkedDimerDTOList_Peptide =
				srchRepPeptNrseqIdPosUnlinkedDimerDTOList;
		
		
		return getDimerroteinMappingsSinglePeptideData;
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
	 * @param getDimerroteinMappingsSinglePeptideData
	 * @throws Exception
	 */
	private void saveDimerDataSinglePeptide( 
			
			ReportedPeptideDTO reportedPeptideDTO, 
			int searchId, 

			GetDimerProteinMappingsSinglePeptideData getDimerroteinMappingsSinglePeptideData,

			Set<Double> uniqueDynamicModMassesForTheSearch
			
			) throws Exception {
		

		PerPeptideData perPeptideData = getDimerroteinMappingsSinglePeptideData.perPeptideData;

		//  Save SrchRepPeptPeptideDTO, SrchRepPeptPeptDynamicModDTO, SrchRepPeptNrseqIdPosMonolinkDTO
		
		SavePerPeptideData.getInstance().savePerPeptideData( perPeptideData, searchId, reportedPeptideDTO );

		
		//  srchRepPeptPeptideDTO saved in savePerPeptideData(...)
		SrchRepPeptPeptideDTO srchRepPeptPeptideDTO = perPeptideData.getSrchRepPeptPeptideDTO();

		int searchReportedPeptidepeptideId = srchRepPeptPeptideDTO.getId();

		//  Save Dimer Protein Mappings 
		
		for ( SrchRepPeptNrseqIdPosUnlinkedDimerDTO srchRepPeptNrseqIdPosUnlinkedDimerDTO : getDimerroteinMappingsSinglePeptideData.srchRepPeptNrseqIdPosUnlinkedDimerDTOList_Peptide ) {
			
			srchRepPeptNrseqIdPosUnlinkedDimerDTO.setSearchId( searchId );
			srchRepPeptNrseqIdPosUnlinkedDimerDTO.setReportedPeptideId( reportedPeptideDTO.getId() );
			srchRepPeptNrseqIdPosUnlinkedDimerDTO.setSearchReportedPeptidepeptideId( searchReportedPeptidepeptideId );
			
			DB_Insert_SrchRepPeptNrseqIdPosUnlinkedDimerDAO.getInstance().save( srchRepPeptNrseqIdPosUnlinkedDimerDTO );
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
	}
	
}
