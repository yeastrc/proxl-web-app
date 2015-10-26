package org.yeastrc.xlink.www.objects;

import java.util.Collection;
import java.util.List;

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
				
		if( this.proteinPositions == null ) 
			this.proteinPositions = MergedSearchProteinSearcher.getInstance().getProteinForUnlinked( this.getSearches(), this.getPeptide());
				
		return this.proteinPositions;
	}



	private int unifiedReportedPeptideId;

	private UnifiedReportedPeptideLookupDTO UnifiedReportedPeptideLookupDTO;

	private UnifiedRepPepMatchedPeptideLookupDTO unifiedRpMatchedPeptide;
	
	private List<UnifiedRepPepDynamicModLookupDTO> unifiedRpDynamicModListPeptide;

	private String modsStringPeptide;

	private PeptideDTO peptide;


	private List<MergedSearchProteinPosition> proteinPositions; 

	private Collection<SearchDTO> searches;

}
