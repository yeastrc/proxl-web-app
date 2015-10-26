package org.yeastrc.xlink.www.objects;

import java.util.Collection;
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
		if( this.proteinPositions == null ) 
			this.proteinPositions = MergedSearchProteinSearcher.getInstance().getProteinPositions( this.getSearches(), this.getPeptide(), this.peptidePosition1, this.peptidePosition2);
				
		return this.proteinPositions;
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
}
