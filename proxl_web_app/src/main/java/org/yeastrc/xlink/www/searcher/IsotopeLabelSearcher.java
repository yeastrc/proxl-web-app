package org.yeastrc.xlink.www.searcher;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yeastrc.xlink.dto.IsotopeLabelDTO;
import org.yeastrc.xlink.www.dto.SrchRepPeptPeptideDTO;
import org.yeastrc.xlink.www.dto.SrchRepPeptPeptideIsotopeLabelDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders.Cached_IsotopeLabel;
import org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders.Cached_SrchRepPeptPeptideIsotopeLabelDTO_For_SrchRepPeptPeptideId;
import org.yeastrc.xlink.www.searcher_via_cached_data.request_objects_for_searchers_for_cached_data.SrchRepPeptPeptideIsotopeLabelDTO_For_SrchRepPeptPeptideId_ReqParams;
import org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data.SrchRepPeptPeptideIsotopeLabelDTO_For_SrchRepPeptPeptideId_Result;

public class IsotopeLabelSearcher {

	private static final Log log = LogFactory.getLog(IsotopeLabelSearcher.class);

	private IsotopeLabelSearcher() { }
	private static final IsotopeLabelSearcher _INSTANCE = new IsotopeLabelSearcher();
	public static IsotopeLabelSearcher getInstance() { return _INSTANCE; }
	
	
	/**
	 * Get the IsotopeLabelDTO associated with the supplied search reported peptide peptide
	 * 
	 * @param srchRepPeptPeptideDTO
	 * @return The found IsotopeLabelDTO, null if non was found (unlabeled)
	 * @throws Exception
	 */
	public IsotopeLabelDTO getIsotopeLabelForSearchReportedPeptide_Peptide( SrchRepPeptPeptideDTO srchRepPeptPeptideDTO ) throws Exception {

		SrchRepPeptPeptideIsotopeLabelDTO_For_SrchRepPeptPeptideId_ReqParams srchRepPeptPeptideIsotopeLabelDTO_For_SrchRepPeptPeptideId_ReqParams = new SrchRepPeptPeptideIsotopeLabelDTO_For_SrchRepPeptPeptideId_ReqParams();
		srchRepPeptPeptideIsotopeLabelDTO_For_SrchRepPeptPeptideId_ReqParams.setSrchRepPeptPeptideId( srchRepPeptPeptideDTO.getId() );
		SrchRepPeptPeptideIsotopeLabelDTO_For_SrchRepPeptPeptideId_Result srchRepPeptPeptideIsotopeLabelDTO_For_SrchRepPeptPeptideId_Result =
				Cached_SrchRepPeptPeptideIsotopeLabelDTO_For_SrchRepPeptPeptideId.getInstance()
				.getSrchRepPeptPeptideIsotopeLabelDTO_For_SrchRepPeptPeptideId_Result( srchRepPeptPeptideIsotopeLabelDTO_For_SrchRepPeptPeptideId_ReqParams );
		List<SrchRepPeptPeptideIsotopeLabelDTO> srchRepPeptPeptideIsotopeLabelDTOList = srchRepPeptPeptideIsotopeLabelDTO_For_SrchRepPeptPeptideId_Result.getSrchRepPeptPeptideIsotopeLabelDTOList();

		// no labels found
		if( srchRepPeptPeptideIsotopeLabelDTOList == null || srchRepPeptPeptideIsotopeLabelDTOList.size() < 1 )
			return null;
		
		// currently we are only ever expecting to find one of these
		if( srchRepPeptPeptideIsotopeLabelDTOList.size() > 1 ) {
			String msg = "Got more than 1 label for search reported peptide peptide (" + srchRepPeptPeptideDTO.getId() + ")";
			
			log.error( msg );
			throw new ProxlWebappDataException( msg );
		}
		
		
		SrchRepPeptPeptideIsotopeLabelDTO item = srchRepPeptPeptideIsotopeLabelDTOList.get( 0 );
		IsotopeLabelDTO isotopeLabelDTO = Cached_IsotopeLabel.getInstance().getIsotopeLabelDTO( item.getIsotopeLabelId() );

		if ( isotopeLabelDTO == null ){
			String msg = "No Isotope Label record found for id: " + item.getIsotopeLabelId();
			
			log.error( msg );
			throw new ProxlWebappDataException( msg );
		}
		
		return isotopeLabelDTO;
	}
	
}
