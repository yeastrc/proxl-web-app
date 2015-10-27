package org.yeastrc.xlink.www.objects;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.PeptideDAO;
import org.yeastrc.xlink.dao.UnifiedReportedPeptideLookupDAO;
import org.yeastrc.xlink.dao.UnifiedRepPepDynamicModLookupDAO;
import org.yeastrc.xlink.dao.UnifiedRepPepMatchedPeptideLookupDAO;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.dto.UnifiedReportedPeptideLookupDTO;
import org.yeastrc.xlink.dto.UnifiedRepPepDynamicModLookupDTO;
import org.yeastrc.xlink.dto.UnifiedRepPepMatchedPeptideLookupDTO;
import org.yeastrc.xlink.www.searcher.MergedSearchProteinSearcher;
import org.yeastrc.xlink.www.web_utils.FormatDynamicModsToString;



/**
 * 
 *
 */
public class MergedSearchPeptideCrosslink implements IMergedSearchLink {

	private static final Logger log = Logger.getLogger(MergedSearchPeptideCrosslink.class);
	
	

	@Override
	public Collection<SearchDTO> getSearches() {
		
		return searches;
	}

	public void setSearches(Collection<SearchDTO> searches) {
		this.searches = searches;
	}

	
	public int hashCode() {
		return (this.peptide1.getSequence() + this.peptide1Position + this.peptide2.getSequence() + this.peptide2Position).hashCode();
	}

	/**
	 * Returns true if both MergedSearchPeptideCrosslink objects describe the same two peptides at the same two positions in each respective peptide
	 */
	public boolean equals( Object o ) {
		if( !( o instanceof MergedSearchPeptideCrosslink ) ) return false;
		
		MergedSearchPeptideCrosslink mrpc = (MergedSearchPeptideCrosslink)o;
		
		try {
			if( this.getPeptide1().getSequence() != mrpc.getPeptide1().getSequence() ) return false;
			if( this.getPeptide2().getSequence() != mrpc.getPeptide2().getSequence() ) return false;
			
			if( this.getPeptide1Position() != mrpc.getPeptide1Position() ) return false;
			if( this.getPeptide2Position() != mrpc.getPeptide2Position() ) return false;
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

			unifiedRpMatchedPeptide1 = unifiedRpMatchedPeptideDTOList.get(0);
			unifiedRpMatchedPeptide2 = unifiedRpMatchedPeptideDTOList.get(1);

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


			Integer position1 = unifiedRpMatchedPeptide1.getLinkPosition1();
			Integer position2 = unifiedRpMatchedPeptide2.getLinkPosition1();

			if ( position1 == null ) {

				String msg = "ERROR: Link Position 1 is null for Crosslink. UnifiedReportedPeptideLookupDTO.id: " + UnifiedReportedPeptideLookupDTOLocal.getId();
				log.error( msg );
				throw new Exception( msg );
			}
			
			if ( position2 == null ) {

				String msg = "ERROR: Link Position 2 is null for Crosslink. UnifiedReportedPeptideLookupDTO.id: " + UnifiedReportedPeptideLookupDTOLocal.getId();
				log.error( msg );
				throw new Exception( msg );
			}


			this.setPeptide1Position( position1 );
			this.setPeptide2Position( position2 );

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
	public int getPeptide1Position() throws Exception {
		
		if( this.peptide1Position == -1 )
			this.populatePeptides();
		
		return peptide1Position;
	}
	public void setPeptide1Position(int peptide1Position) {
		this.peptide1Position = peptide1Position;
	}
	public int getPeptide2Position() throws Exception {
		if( this.peptide2Position == -1 )
			this.populatePeptides();
		
		return peptide2Position;
	}
	public void setPeptide2Position(int peptide2Position) {
		this.peptide2Position = peptide2Position;
	}
	
	public String getPeptide1ProteinPositionsString() throws Exception {
		return StringUtils.join( this.getPeptide1ProteinPositions(), ", " );
	}
	public List<MergedSearchProteinPosition> getPeptide1ProteinPositions() throws Exception {
		if( this.peptide1ProteinPositions == null ) {

			this.peptide1ProteinPositions = MergedSearchProteinSearcher.getInstance().getProteinPositions( this.getSearches(), this.getPeptide1(), this.peptide1Position);

			//  WAS
//			this.peptide1ProteinPositions = MergedSearchProteinSearcher.getInstance().getProteinPositions( this.searchCrosslinks.keySet(), this.getPeptide1(), this.peptide1Position);
		}
				
		return peptide1ProteinPositions;
	}


	public String getPeptide2ProteinPositionsString() throws Exception {
		return StringUtils.join( this.getPeptide2ProteinPositions(), ", " );
	}
	public List<MergedSearchProteinPosition> getPeptide2ProteinPositions() throws Exception {
		
		if( this.peptide2ProteinPositions == null ) { 
			
			//  TODO  New Implementation

			this.peptide2ProteinPositions = MergedSearchProteinSearcher.getInstance().getProteinPositions( this.getSearches(), this.getPeptide2(), this.peptide2Position);

			// WAS
//			this.peptide2ProteinPositions = MergedSearchProteinSearcher.getInstance().getProteinPositions( this.searchCrosslinks.keySet(), this.getPeptide2(), this.peptide2Position);
		}
				
		return peptide2ProteinPositions;
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
	private int peptide1Position = -1;
	private int peptide2Position = -1;
	
	
	private List<MergedSearchProteinPosition> peptide1ProteinPositions;
	private List<MergedSearchProteinPosition> peptide2ProteinPositions;
	
	private Collection<SearchDTO> searches;


	
}