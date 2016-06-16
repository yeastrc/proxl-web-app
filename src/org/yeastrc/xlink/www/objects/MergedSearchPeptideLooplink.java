package org.yeastrc.xlink.www.objects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.PeptideDAO;
import org.yeastrc.xlink.dao.UnifiedReportedPeptideLookupDAO;
import org.yeastrc.xlink.dao.UnifiedRepPepDynamicModLookupDAO;
import org.yeastrc.xlink.dao.UnifiedRepPepMatchedPeptideLookupDAO;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.dto.UnifiedReportedPeptideLookupDTO;
import org.yeastrc.xlink.dto.UnifiedRepPepDynamicModLookupDTO;
import org.yeastrc.xlink.dto.UnifiedRepPepMatchedPeptideLookupDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.searcher.SearchProteinSearcher;
import org.yeastrc.xlink.www.web_utils.FormatDynamicModsToString;



public class MergedSearchPeptideLooplink implements IMergedSearchLink {
	
	private static final Logger log = Logger.getLogger(MergedSearchPeptideLooplink.class);
			
			


	@Override
	public Collection<SearchDTO> getSearches() {
		
		return searches;
	}


	public void setSearches(Collection<SearchDTO> searches) {
		this.searches = searches;
	}
	
	
	public int hashCode() {
		return (this.peptide.getSequence() + this.peptidePosition1 + this.peptidePosition2).hashCode();
	}

	/**
	 * Returns true if both MergedSearchPeptideLooplink objects describe the same two peptides at the same two positions in each respective peptide
	 */
	public boolean equals( Object o ) {
		if( !( o instanceof MergedSearchPeptideLooplink ) ) return false;
		
		MergedSearchPeptideLooplink mrpc = (MergedSearchPeptideLooplink)o;
		
		try {
			if( this.getPeptide().getSequence() != mrpc.getPeptide().getSequence() ) return false;
			
			if( this.getPeptidePosition1() != mrpc.getPeptidePosition1() ) return false;
			if( this.getPeptidePosition2() != mrpc.getPeptidePosition2() ) return false;

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


			Integer position1 = unifiedRpMatchedPeptide.getLinkPosition1();
			Integer position2 = unifiedRpMatchedPeptide.getLinkPosition2();
			
			if ( position1 == null ) {

				String msg = "ERROR: Link Position 1 is null for Looplink. UnifiedReportedPeptideLookupDTO.id: " + UnifiedReportedPeptideLookupDTOLocal.getId();
				log.error( msg );
				throw new Exception( msg );
			}
			
			if ( position2 == null ) {

				String msg = "ERROR: Link Position 2 is null for Looplink. UnifiedReportedPeptideLookupDTO.id: " + UnifiedReportedPeptideLookupDTOLocal.getId();
				log.error( msg );
				throw new Exception( msg );
			}

			this.setPeptidePosition1( position1 );
			this.setPeptidePosition2( position2 );


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


	public PeptideDTO getPeptide() throws Exception {
		
		if( this.peptide == null )
			this.populatePeptides();
		
		return this.peptide;
	}
	public void setPeptide(PeptideDTO peptide) {
		this.peptide = peptide;
	}
	

	public int getPeptidePosition1() throws Exception {
		
		if( this.peptidePosition1 == -1 )
			this.populatePeptides();
		
		return peptidePosition1;
	}
	public void setPeptidePosition1(int peptidePosition) {
		this.peptidePosition1 = peptidePosition;
	}
	public int getPeptidePosition2() throws Exception {
		if( this.peptidePosition2 == -1 )
			this.populatePeptides();
		
		return peptidePosition2;
	}
	public void setPeptidePosition2(int peptidePosition) {
		this.peptidePosition2 = peptidePosition;
	}
	
	public String getProteinPositionsString() throws Exception {
		return StringUtils.join( this.getProteinPositions(), ", " );
	}
	public List<MergedSearchProteinDoublePosition> getProteinPositions() throws Exception {
		if( this.proteinPositions == null )  {
			
			this.proteinPositions = getLooplinkPeptideProteinPositions( this.getPeptide(), this.peptidePosition1, this.peptidePosition2 );
			
			//  WAS
//			this.proteinPositions = MergedSearchProteinSearcher.getInstance().getProteinPositions( this.getSearches(), this.getPeptide(), this.peptidePosition1, this.peptidePosition2);
		}
		return this.proteinPositions;
	}
	

	private List<MergedSearchProteinDoublePosition> getLooplinkPeptideProteinPositions( PeptideDTO peptide, int position1, int position2 ) throws Exception {
		
		try {

			Map<Integer, Map<Integer, Map<Integer, MergedSearchProteinDoublePosition>>> mergedSearchProteinDoublePosition_MappedOn_ProtIdPos1Pos2 = new HashMap<>();

			for ( SearchDTO search : searches ) {

				Integer reportedPeptideIdForSearchId =
						reportedPeptideIds_KeyedOnSearchId_Map.get( search.getId() );

				if ( reportedPeptideIdForSearchId == null ) {

					String msg = "reportedPeptideIdForSearchId == null for getPeptide1ProteinPositions()."
							+ ", search.getId(): " + search.getId();
					log.error( msg );
					throw new ProxlWebappDataException(msg);
				}

				List<SearchProteinDoublePosition> resultPerSearch =
						SearchProteinSearcher.getInstance()
						.getProteinDoublePositions( search, reportedPeptideIdForSearchId, peptide.getId(), position1, position2 );

				for ( SearchProteinDoublePosition searchProteinDoublePosition : resultPerSearch ) {

					Integer proteinId = searchProteinDoublePosition.getProtein().getNrProtein().getNrseqId();
					Integer proteinPosition_1 = searchProteinDoublePosition.getPosition1();
					Integer proteinPosition_2 = searchProteinDoublePosition.getPosition2();

					//  get MergedSearchProteinDoublePosition for protein id, position 1, position 2
					
					

					Map<Integer, Map<Integer, MergedSearchProteinDoublePosition>> mergedSearchProteinDoublePosition_MappedOn_Pos1Pos2 = 
							mergedSearchProteinDoublePosition_MappedOn_ProtIdPos1Pos2.get( proteinId );

					if ( mergedSearchProteinDoublePosition_MappedOn_Pos1Pos2 == null ) {
						
						mergedSearchProteinDoublePosition_MappedOn_Pos1Pos2 = new HashMap<>();
						mergedSearchProteinDoublePosition_MappedOn_ProtIdPos1Pos2.put( proteinId, mergedSearchProteinDoublePosition_MappedOn_Pos1Pos2 );
					}

					Map<Integer, MergedSearchProteinDoublePosition> mergedSearchProteinDoublePosition_MappedOn_Pos2 = 
							mergedSearchProteinDoublePosition_MappedOn_Pos1Pos2.get( proteinPosition_1 );

					if ( mergedSearchProteinDoublePosition_MappedOn_Pos2 == null ) {
						
						mergedSearchProteinDoublePosition_MappedOn_Pos2 = new HashMap<>();
						mergedSearchProteinDoublePosition_MappedOn_Pos1Pos2.put( proteinPosition_1, mergedSearchProteinDoublePosition_MappedOn_Pos2 );
					}

					MergedSearchProteinDoublePosition mergedSearchProteinDoublePosition = mergedSearchProteinDoublePosition_MappedOn_Pos2.get( proteinPosition_2 );

					if ( mergedSearchProteinDoublePosition == null ) {
						
						mergedSearchProteinDoublePosition = new MergedSearchProteinDoublePosition();
						mergedSearchProteinDoublePosition_MappedOn_Pos2.put( proteinPosition_2, mergedSearchProteinDoublePosition );
						
						List<SearchDTO> searches = new ArrayList<>();
						MergedSearchProtein mergedSearchProtein = new MergedSearchProtein( searches, searchProteinDoublePosition.getProtein().getNrProtein() );
						mergedSearchProteinDoublePosition.setProtein( mergedSearchProtein );
						mergedSearchProteinDoublePosition.setPosition1( proteinPosition_1 );
						mergedSearchProteinDoublePosition.setPosition2( proteinPosition_2 );
					}
					
					//  If search is not in searches, add it
						
					Collection<SearchDTO> searches = mergedSearchProteinDoublePosition.getProtein().getSearchs();
					
					boolean searchFound = false;
					
					for ( SearchDTO searchInSearches : searches ) {
						
						if ( search.getId() == searchInSearches.getId() ) {
							
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

			List<MergedSearchProteinDoublePosition> mergedSearchProteinDoublePositionList = new ArrayList<>();

			for ( Map.Entry<Integer, Map<Integer, Map<Integer, MergedSearchProteinDoublePosition>>> mergedSearchProteinDoublePosition_MappedOn_ProtIdPos1Pos2Entry :
				mergedSearchProteinDoublePosition_MappedOn_ProtIdPos1Pos2.entrySet() ) {

				for ( Map.Entry<Integer,Map<Integer, MergedSearchProteinDoublePosition>> mergedSearchProteinDoublePosition_MappedOn_Pos1Pos2Entry :
					mergedSearchProteinDoublePosition_MappedOn_ProtIdPos1Pos2Entry.getValue().entrySet() ) {

					for ( Map.Entry<Integer, MergedSearchProteinDoublePosition> mergedSearchProteinDoublePosition_MappedOn_PosEntry :
						mergedSearchProteinDoublePosition_MappedOn_Pos1Pos2Entry.getValue().entrySet() ) {

						MergedSearchProteinDoublePosition mergedSearchProteinDoublePosition = mergedSearchProteinDoublePosition_MappedOn_PosEntry.getValue();

						mergedSearchProteinDoublePositionList.add( mergedSearchProteinDoublePosition );
					}
				}
			}
			
			//  Sort list
			
			Collections.sort( mergedSearchProteinDoublePositionList, new Comparator<MergedSearchProteinDoublePosition>() {

				@Override
				public int compare(MergedSearchProteinDoublePosition o1, MergedSearchProteinDoublePosition o2) {
					
					if ( o1.getProtein().getNrProtein().getNrseqId() != o2.getProtein().getNrProtein().getNrseqId() ) {
						
						return o1.getProtein().getNrProtein().getNrseqId() - o2.getProtein().getNrProtein().getNrseqId();
					}
					
					if ( o1.getPosition1() != o2.getPosition1() ) {
						
						return o1.getPosition1() - o2.getPosition1();
					}
					return o1.getPosition2() - o2.getPosition2();
				}
			});

			return mergedSearchProteinDoublePositionList;

		} catch ( Exception e ) {
			
			String msg = "Exception in populatePeptides(): " + e.toString();
			log.error( msg, e );
			
			throw e;
		}
		
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
	private int peptidePosition1 = -1;
	private int peptidePosition2 = -1;
	
	private List<MergedSearchProteinDoublePosition> proteinPositions;
	
	
	private Collection<SearchDTO> searches;

	private Map<Integer, Integer> reportedPeptideIds_KeyedOnSearchId_Map;



	
}
