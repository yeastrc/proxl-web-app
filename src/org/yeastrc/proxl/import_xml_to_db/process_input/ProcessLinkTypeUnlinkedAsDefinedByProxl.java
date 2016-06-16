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
		
		private List<SrchRepPeptNrseqIdPosUnlinkedDimerDTO> srchRepPeptNrseqIdPosUnlinkedDimerDTOList;


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
	 * Proxl internal "Unlinked" is a single peptide that is not a unlinked
	 * 
	 * Get Protein Mappings for unlinked reported peptide
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
	public GetUnlinkedProteinMappingsResult getUnlinkedroteinMappings( 
			
			ReportedPeptide reportedPeptide, 

			List<ILinker> linkerList,
			String linkerListStringForErrorMsgs,
			
			List<String> proteinNameDecoyPrefixList,
			
			int nrseqDatabaseId
			
			) throws Exception {
		
		

		GetUnlinkedProteinMappingsResult getUnlinkedMappingsResult = new GetUnlinkedProteinMappingsResult();
		
		List<SrchRepPeptNrseqIdPosUnlinkedDimerDTO> srchRepPeptNrseqIdPosUnlinkedDimerDTOList = new ArrayList<>();
		getUnlinkedMappingsResult.srchRepPeptNrseqIdPosUnlinkedDimerDTOList = srchRepPeptNrseqIdPosUnlinkedDimerDTOList;
		
		
		
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
		
		
		//  Create partial peptide level record

		SrchRepPeptPeptideDTO srchRepPeptPeptideDTO = new SrchRepPeptPeptideDTO();
		
		srchRepPeptPeptideDTO.setPeptideId( peptideDTO.getId() );
		
		
		perPeptideData.setSrchRepPeptPeptideDTO( srchRepPeptPeptideDTO );
		
		Collection<NRProteinDTO> proteinMatches_Peptide = 
				GetProteinsForPeptidesAndInsertNrseqPeptideProteinEntries.getInstance()
				.getProteinsForPeptidesAndInsertNrseqPeptideProteinEntries( peptideDTO, proteinNameDecoyPrefixList, nrseqDatabaseId );
		
		
					
		if( proteinMatches_Peptide.size() < 1 ) {
			String msg = "getUnlinkedroteinMappings(...): No protein positions found for " + peptideDTO.getSequence() 
					+ ".  reportedPeptide sequence: " + reportedPeptide.getReportedPeptideString();
			log.error( msg );
			
			throw new Exception( msg );
		}
		
		
		
		for( NRProteinDTO protein : proteinMatches_Peptide ) {

			// a single unlinked entry
			SrchRepPeptNrseqIdPosUnlinkedDimerDTO unlinked = new SrchRepPeptNrseqIdPosUnlinkedDimerDTO();

			unlinked.setNrseqId( protein.getNrseqId() );

			srchRepPeptNrseqIdPosUnlinkedDimerDTOList.add( unlinked );

		}  //end looping over proteins
		

		if ( srchRepPeptNrseqIdPosUnlinkedDimerDTOList == null || srchRepPeptNrseqIdPosUnlinkedDimerDTOList.isEmpty() ) {

			getUnlinkedMappingsResult.noProteinMappings = true; 
		}
		

		PopulateSrchRepPeptNrseqIdPosMonolinkDTOListOnPerPeptideDataObject.getInstance()
		.populateSrchRepPeptNrseqIdPosMonolinkDTOListOnPerPeptideDataObject( perPeptideData, linkerList, proteinMatches_Peptide );

		
		
		
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


		PerPeptideData perPeptideData = getUnlinkedProteinMappingsResult.perPeptideData;


		//  Save SrchRepPeptPeptideDTO, SrchRepPeptPeptDynamicModDTO, SrchRepPeptNrseqIdPosMonolinkDTO
		
		SavePerPeptideData.getInstance().savePerPeptideData( perPeptideData, searchId, reportedPeptideDTO );

		
		//  srchRepPeptPeptideDTO saved in savePerPeptideData(...)
		SrchRepPeptPeptideDTO srchRepPeptPeptideDTO = perPeptideData.getSrchRepPeptPeptideDTO();

		int searchReportedPeptidepeptideId = srchRepPeptPeptideDTO.getId();

		//  Save Unlinked Protein Mappings 
		
		for ( SrchRepPeptNrseqIdPosUnlinkedDimerDTO srchRepPeptNrseqIdPosUnlinkedDimerDTO : getUnlinkedProteinMappingsResult.srchRepPeptNrseqIdPosUnlinkedDimerDTOList ) {
			
			srchRepPeptNrseqIdPosUnlinkedDimerDTO.setSearchId( searchId );
			srchRepPeptNrseqIdPosUnlinkedDimerDTO.setReportedPeptideId( reportedPeptideDTO.getId() );
			srchRepPeptNrseqIdPosUnlinkedDimerDTO.setSearchReportedPeptidepeptideId( searchReportedPeptidepeptideId );
			
			DB_Insert_SrchRepPeptNrseqIdPosUnlinkedDimerDAO.getInstance().save( srchRepPeptNrseqIdPosUnlinkedDimerDTO );
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
		


		List<PerPeptideData> perPeptideDataList = new ArrayList<>( 1 );

		perPeptideDataList.add( perPeptideData );
		
		return perPeptideDataList;
	}
	
	

	}