package org.yeastrc.xlink.www.objects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.dao.PeptideDAO;
import org.yeastrc.xlink.dao.UnifiedReportedPeptideLookupDAO;
import org.yeastrc.xlink.dao.UnifiedRepPepDynamicModLookupDAO;
import org.yeastrc.xlink.dao.UnifiedRepPepMatchedPeptideLookupDAO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.dto.UnifiedReportedPeptideLookupDTO;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.dto.UnifiedRepPepDynamicModLookupDTO;
import org.yeastrc.xlink.dto.UnifiedRepPepMatchedPeptideLookupDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.searcher.SearchProteinSearcher;
import org.yeastrc.xlink.www.web_utils.FormatDynamicModsToString;



public class MergedSearchPeptideUnlinked implements IMergedSearchLink {

	private static final Logger log = Logger.getLogger(MergedSearchPeptideUnlinked.class);
	
	

	@Override
	public Collection<SearchDTO> getSearches() {
		
		return searches;
	}

	public void setSearches(Collection<SearchDTO> searches) {
		this.searches = searches;
	}


	
	
	public int hashCode() {
		return this.peptide.getSequence().hashCode();
	}

	/**
	 * Returns true if both MergedSearchPeptideUnlinked objects describe the same two peptides at the same two positions in each respective peptide
	 */
	public boolean equals( Object o ) {
		if( !( o instanceof MergedSearchPeptideUnlinked ) ) return false;
		
		MergedSearchPeptideUnlinked mrpc = (MergedSearchPeptideUnlinked)o;
		
		try {
			if( this.getPeptide().getSequence() != mrpc.getPeptide().getSequence() ) return false;
			
		} catch( Exception e ) {
			return false;
		}
		
		return true;
	}
	

	
	
	private void populatePeptides() throws Exception {
		

		try {
			UnifiedReportedPeptideLookupDTO UnifiedReportedPeptideLookupDTOLocal = this.getUnifiedReportedPeptideLookupDTO();

			List<UnifiedRepPepMatchedPeptideLookupDTO> unifiedRpMatchedPeptideDTOList 
			= UnifiedRepPepMatchedPeptideLookupDAO.getInstance().getUnifiedRpMatchedPeptideDTOForUnifiedReportedPeptideId( UnifiedReportedPeptideLookupDTOLocal.getId() );


			UnifiedRepPepMatchedPeptideLookupDTO unifiedRpMatchedPeptide = unifiedRpMatchedPeptideDTOList.get(0);

			PeptideDTO peptideDTO = PeptideDAO.getInstance().getPeptideDTOFromDatabase( unifiedRpMatchedPeptide.getPeptideId() );
			this.setPeptide( peptideDTO );



			List<UnifiedRepPepDynamicModLookupDTO> unifiedRpDynamicModListPeptide = UnifiedRepPepDynamicModLookupDAO.getInstance().getUnifiedRpDynamicModDTOForMatchedPeptideId( unifiedRpMatchedPeptide.getId() );

			modsStringPeptide = FormatDynamicModsToString.formatDynamicModsToString( unifiedRpDynamicModListPeptide );

		} catch ( Exception e ) {
			
			String msg = "Exception in populatePeptides(): " + e.toString();
			log.error( msg, e );
			
			throw e;
		}
	}
	
	
	public int getUnifiedReportedPeptideId() {
		return unifiedReportedPeptideId;
	}
	public void setUnifiedReportedPeptideId(int unifiedReportedPeptideId) {
		this.unifiedReportedPeptideId = unifiedReportedPeptideId;
	}

	public UnifiedReportedPeptideLookupDTO getUnifiedReportedPeptideLookupDTO() throws Exception {
		
		if ( UnifiedReportedPeptideLookupDTO == null ) {
			
			UnifiedReportedPeptideLookupDTO = UnifiedReportedPeptideLookupDAO.getInstance().getUnifiedReportedPeptideFromDatabase( this.unifiedReportedPeptideId );
		}
		
		return UnifiedReportedPeptideLookupDTO;
	}
	public void setUnifiedReportedPeptideLookupDTO(
			UnifiedReportedPeptideLookupDTO UnifiedReportedPeptideLookupDTO) {
		this.UnifiedReportedPeptideLookupDTO = UnifiedReportedPeptideLookupDTO;
	}



	public UnifiedRepPepMatchedPeptideLookupDTO getUnifiedRpMatchedPeptide() {
		return unifiedRpMatchedPeptide;
	}
	public List<UnifiedRepPepDynamicModLookupDTO> getUnifiedRpDynamicModListPeptide() {
		return unifiedRpDynamicModListPeptide;
	}
	public String getModsStringPeptide() {
		return modsStringPeptide;
	}

	
	public PeptideDTO getPeptide() throws Exception {
		
		if( this.peptide == null )
			this.populatePeptides();
		
		return peptide;
	}



	public void setPeptide(PeptideDTO peptide) {
		this.peptide = peptide;
	}


	
	public List<MergedSearchProteinPosition> getProteinPositions() throws Exception {
				
		if( this.proteinPositions == null ) {

			//  Get proteins for the peptide
			
			this.proteinPositions = getUnlinkedOrDimerPeptideProteinPositions( this.getPeptide() );
			
			//  WAS  
//			this.proteinPositions = MergedSearchProteinSearcher.getInstance().getProteinForUnlinked( this.getSearches(), this.getPeptide());
			
		}
		
		
		return this.proteinPositions;
	}
	

	private List<MergedSearchProteinPosition> getUnlinkedOrDimerPeptideProteinPositions( PeptideDTO peptide ) throws Exception {
		
		try {

			Map<Integer, MergedSearchProteinPosition> mergedSearchProteinPosition_MappedOn_ProtId = new HashMap<>();

			for ( SearchDTO search : searches ) {

				Integer reportedPeptideIdForSearchId =
						reportedPeptideIds_KeyedOnSearchId_Map.get( search.getSearchId() );

				if ( reportedPeptideIdForSearchId == null ) {

					String msg = "reportedPeptideIdForSearchId == null for getCrosslinkPeptideProteinPositions()."
							+ ", search.getId(): " + search.getSearchId();
					log.error( msg );
					throw new ProxlWebappDataException(msg);
				}

				List<SearchProteinPosition> resultPerSearch =
						SearchProteinSearcher.getInstance()
						.getProteinForUnlinked( search, reportedPeptideIdForSearchId, peptide.getId() );

				for ( SearchProteinPosition searchProteinPosition : resultPerSearch ) {

					Integer proteinId = searchProteinPosition.getProtein().getProteinSequenceVersionObject().getProteinSequenceVersionId();

					//  get MergedSearchProteinPosition for protein id, position
					
					
					MergedSearchProteinPosition mergedSearchProteinPosition = mergedSearchProteinPosition_MappedOn_ProtId.get( proteinId );

					if ( mergedSearchProteinPosition == null ) {
						
						mergedSearchProteinPosition = new MergedSearchProteinPosition();
						mergedSearchProteinPosition_MappedOn_ProtId.put( proteinId, mergedSearchProteinPosition );
						
						List<SearchDTO> searches = new ArrayList<>();
						MergedSearchProtein mergedSearchProtein = new MergedSearchProtein( searches, searchProteinPosition.getProtein().getProteinSequenceVersionObject() );
						mergedSearchProteinPosition.setProtein( mergedSearchProtein );
					}
					
					//  If search is not in searches, add it
						
					Collection<SearchDTO> searches = mergedSearchProteinPosition.getProtein().getSearchs();
					
					boolean searchFound = false;
					
					for ( SearchDTO searchInSearches : searches ) {
						
						if ( search.getSearchId() == searchInSearches.getSearchId() ) {
							
							searchFound = true;
							break;
						}
					}
					
					if ( ! searchFound ) {
						
						searches.add( search );
					}
				}
			}
			
			//  Transfer to a list;

			List<MergedSearchProteinPosition> mergedSearchProteinPositionList = new ArrayList<>();

			for ( Map.Entry<Integer, MergedSearchProteinPosition> mergedSearchProteinPosition_MappedOn_ProtIdEntry :
				mergedSearchProteinPosition_MappedOn_ProtId.entrySet() ) {
				
				MergedSearchProteinPosition mergedSearchProteinPosition = mergedSearchProteinPosition_MappedOn_ProtIdEntry.getValue();
					
				mergedSearchProteinPositionList.add( mergedSearchProteinPosition );
			}
			
			//  Sort list
			
			Collections.sort( mergedSearchProteinPositionList, new Comparator<MergedSearchProteinPosition>() {

				@Override
				public int compare(MergedSearchProteinPosition o1, MergedSearchProteinPosition o2) {
					
					return o1.getProtein().getProteinSequenceVersionObject().getProteinSequenceVersionId() - o2.getProtein().getProteinSequenceVersionObject().getProteinSequenceVersionId();
				}
			});

			return mergedSearchProteinPositionList;

		} catch ( Exception e ) {
			
			String msg = "Exception in populatePeptides(): " + e.toString();
			log.error( msg, e );
			
			throw e;
		}
		
	}

	public Map<Integer, Integer> getReportedPeptideIds_KeyedOnSearchId_Map() {
		return reportedPeptideIds_KeyedOnSearchId_Map;
	}

	public void setReportedPeptideIds_KeyedOnSearchId_Map(
			Map<Integer, Integer> reportedPeptideIds_KeyedOnSearchId_Map) {
		this.reportedPeptideIds_KeyedOnSearchId_Map = reportedPeptideIds_KeyedOnSearchId_Map;
	}



	private int unifiedReportedPeptideId;

	private UnifiedReportedPeptideLookupDTO UnifiedReportedPeptideLookupDTO;

	private UnifiedRepPepMatchedPeptideLookupDTO unifiedRpMatchedPeptide;
	
	private List<UnifiedRepPepDynamicModLookupDTO> unifiedRpDynamicModListPeptide;

	private String modsStringPeptide;

	private PeptideDTO peptide;


	private List<MergedSearchProteinPosition> proteinPositions; 

	private Collection<SearchDTO> searches;
	
	private Map<Integer, Integer> reportedPeptideIds_KeyedOnSearchId_Map;




}
