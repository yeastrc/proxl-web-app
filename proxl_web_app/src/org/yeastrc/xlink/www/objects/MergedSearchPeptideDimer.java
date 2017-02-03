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
import org.yeastrc.xlink.www.dto.PeptideDTO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.dto.UnifiedReportedPeptideLookupDTO;
import org.yeastrc.xlink.dto.UnifiedRepPepDynamicModLookupDTO;
import org.yeastrc.xlink.dto.UnifiedRepPepMatchedPeptideLookupDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.searcher.SearchProteinSearcher;
import org.yeastrc.xlink.www.web_utils.FormatDynamicModsToString;



public class MergedSearchPeptideDimer implements IMergedSearchLink {

	private static final Logger log = Logger.getLogger(MergedSearchPeptideDimer.class);
	
	

	@Override
	public Collection<SearchDTO> getSearches() {
		
		return searches;
	}

	public void setSearches(Collection<SearchDTO> searches) {
		this.searches = searches;
	}

	
	
	public int hashCode() {
		return (this.peptide1.getSequence() + this.peptide2.getSequence() ).hashCode();
	}

	/**
	 * Returns true if both MergedSearchPeptideDimer objects describe the same two peptides at the same two positions in each respective peptide
	 */
	public boolean equals( Object o ) {
		if( !( o instanceof MergedSearchPeptideDimer ) ) return false;
		
		MergedSearchPeptideDimer mrpc = (MergedSearchPeptideDimer)o;
		
		try {
			if( this.getPeptide1().getSequence() != mrpc.getPeptide1().getSequence() ) return false;
			if( this.getPeptide2().getSequence() != mrpc.getPeptide2().getSequence() ) return false;
			
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

			//  Sort on peptideOrder
			Collections.sort( unifiedRpMatchedPeptideDTOList, new Comparator<UnifiedRepPepMatchedPeptideLookupDTO>() {

				@Override
				public int compare(UnifiedRepPepMatchedPeptideLookupDTO o1,
						UnifiedRepPepMatchedPeptideLookupDTO o2) {

					return o1.getPeptideOrder() - o2.getPeptideOrder();
				}
			});

			UnifiedRepPepMatchedPeptideLookupDTO unifiedRpMatchedPeptide1 = unifiedRpMatchedPeptideDTOList.get(0);
			UnifiedRepPepMatchedPeptideLookupDTO unifiedRpMatchedPeptide2 = unifiedRpMatchedPeptideDTOList.get(1);

			if( unifiedRpMatchedPeptide1.getPeptideId() == unifiedRpMatchedPeptide2.getPeptideId() ) {

				// Same peptide so only load it once.

				PeptideDTO peptideDTO = PeptideDAO.getInstance().getPeptideDTOFromDatabase( unifiedRpMatchedPeptide1.getPeptideId() );

				this.setPeptide1( peptideDTO );
				this.setPeptide2( peptideDTO );

			} else {

				PeptideDTO peptideDTO1 = PeptideDAO.getInstance().getPeptideDTOFromDatabase( unifiedRpMatchedPeptide1.getPeptideId() );
				PeptideDTO peptideDTO2 = PeptideDAO.getInstance().getPeptideDTOFromDatabase( unifiedRpMatchedPeptide2.getPeptideId() );

				this.setPeptide1( peptideDTO1 );
				this.setPeptide2( peptideDTO2 );
			}


			List<UnifiedRepPepDynamicModLookupDTO> unifiedRpDynamicModListPeptide1 = UnifiedRepPepDynamicModLookupDAO.getInstance().getUnifiedRpDynamicModDTOForMatchedPeptideId( unifiedRpMatchedPeptide1.getId() );
			List<UnifiedRepPepDynamicModLookupDTO> unifiedRpDynamicModListPeptide2 = UnifiedRepPepDynamicModLookupDAO.getInstance().getUnifiedRpDynamicModDTOForMatchedPeptideId( unifiedRpMatchedPeptide2.getId() );


			modsStringPeptide1 = FormatDynamicModsToString.formatDynamicModsToString( unifiedRpDynamicModListPeptide1 );
			modsStringPeptide2 = FormatDynamicModsToString.formatDynamicModsToString( unifiedRpDynamicModListPeptide2 );

		} catch ( Exception e ) {
			
			String msg = "Exception in populatePeptides(): " + e.toString();
			log.error( msg, e );
			
			throw e;
		}

	}
	
	
	
	public List<MergedSearchProteinPosition> getPeptide1ProteinPositions() throws Exception {
		if( this.peptide1ProteinPositions == null ) {

			this.peptide1ProteinPositions = getUnlinkedOrDimerPeptideProteinPositions( this.getPeptide1() );
			
			//  WAS
//			this.peptide1ProteinPositions = MergedSearchProteinSearcher.getInstance().getProteinForDimer( this.getSearches(), this.getPeptide1());
		}
			
				
		return this.peptide1ProteinPositions;
	}
	
	
	public List<MergedSearchProteinPosition> getPeptide2ProteinPositions() throws Exception {
		if( this.peptide2ProteinPositions == null ) {

			this.peptide2ProteinPositions = getUnlinkedOrDimerPeptideProteinPositions( this.getPeptide2() );
			
			//  WAS
			// this.peptide2ProteinPositions = MergedSearchProteinSearcher.getInstance().getProteinForDimer( getSearches(), this.getPeptide2());
		}
		
		return this.peptide2ProteinPositions;
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

					Integer proteinId = searchProteinPosition.getProtein().getProteinSequenceObject().getProteinSequenceId();

					//  get MergedSearchProteinPosition for protein id, position
					
					
					MergedSearchProteinPosition mergedSearchProteinPosition = mergedSearchProteinPosition_MappedOn_ProtId.get( proteinId );

					if ( mergedSearchProteinPosition == null ) {
						
						mergedSearchProteinPosition = new MergedSearchProteinPosition();
						mergedSearchProteinPosition_MappedOn_ProtId.put( proteinId, mergedSearchProteinPosition );
						
						List<SearchDTO> searches = new ArrayList<>();
						MergedSearchProtein mergedSearchProtein = new MergedSearchProtein( searches, searchProteinPosition.getProtein().getProteinSequenceObject() );
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
					
					return o1.getProtein().getProteinSequenceObject().getProteinSequenceId() - o2.getProtein().getProteinSequenceObject().getProteinSequenceId();
				}
			});

			return mergedSearchProteinPositionList;

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

	
	public PeptideDTO getPeptide1() throws Exception {
		
		if( this.peptide1 == null )
			this.populatePeptides();
		
		return this.peptide1;
	}
	public void setPeptide1(PeptideDTO peptide1) {
		this.peptide1 = peptide1;
	}
	public PeptideDTO getPeptide2() throws Exception {
		
		if( this.peptide1 == null )
			this.populatePeptides();
		
		return peptide2;
	}
	public void setPeptide2(PeptideDTO peptide2) {
		this.peptide2 = peptide2;
	}
	




	public UnifiedRepPepMatchedPeptideLookupDTO getUnifiedRpMatchedPeptide1() {
		return unifiedRpMatchedPeptide1;
	}
	public UnifiedRepPepMatchedPeptideLookupDTO getUnifiedRpMatchedPeptide2() {
		return unifiedRpMatchedPeptide2;
	}
	public List<UnifiedRepPepDynamicModLookupDTO> getUnifiedRpDynamicModListPeptide1() {
		return unifiedRpDynamicModListPeptide1;
	}
	public List<UnifiedRepPepDynamicModLookupDTO> getUnifiedRpDynamicModListPeptide2() {
		return unifiedRpDynamicModListPeptide2;
	}
	public String getModsStringPeptide1() {
		return modsStringPeptide1;
	}
	public String getModsStringPeptide2() {
		return modsStringPeptide2;
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

	private UnifiedRepPepMatchedPeptideLookupDTO unifiedRpMatchedPeptide1;
	private UnifiedRepPepMatchedPeptideLookupDTO unifiedRpMatchedPeptide2;
	
	private List<UnifiedRepPepDynamicModLookupDTO> unifiedRpDynamicModListPeptide1;
	private List<UnifiedRepPepDynamicModLookupDTO> unifiedRpDynamicModListPeptide2;

	private String modsStringPeptide1;
	private String modsStringPeptide2;


	private PeptideDTO peptide1;
	private PeptideDTO peptide2;
	

	private List<MergedSearchProteinPosition> peptide1ProteinPositions;
	private List<MergedSearchProteinPosition> peptide2ProteinPositions;
	
	private Collection<SearchDTO> searches;

	private Map<Integer, Integer> reportedPeptideIds_KeyedOnSearchId_Map;




}
