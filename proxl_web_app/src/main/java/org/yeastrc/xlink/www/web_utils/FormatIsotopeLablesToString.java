package org.yeastrc.xlink.www.web_utils;

import java.util.List;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.dto.IsotopeLabelDTO;
import org.yeastrc.xlink.dto.UnifiedRepPepIsotopeLabelLookupDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders.Cached_IsotopeLabel;

/**
 * 
 *
 */
public class FormatIsotopeLablesToString {
	
	private static final Logger log = LoggerFactory.getLogger(  FormatIsotopeLablesToString.class );
	
	/**
	 * @param unifiedRpDynamicModListPeptide
	 * @return
	 * @throws Exception 
	 */
	public static String formatIsotopeLabelsToString( List<UnifiedRepPepIsotopeLabelLookupDTO> unifiedRepPepIsotopeLabelListPeptide ) throws Exception {
		if ( unifiedRepPepIsotopeLabelListPeptide == null || unifiedRepPepIsotopeLabelListPeptide.isEmpty() ) {
			return "";
		}
		StringBuilder resultStringPeptideSB = new StringBuilder();
		for ( UnifiedRepPepIsotopeLabelLookupDTO item : unifiedRepPepIsotopeLabelListPeptide ) {
			if ( resultStringPeptideSB.length() > 0 )  {
				resultStringPeptideSB.append( ", " );
			}
			IsotopeLabelDTO isotopeLabelDTO = Cached_IsotopeLabel.getInstance().getIsotopeLabelDTO( item.getIsotopeLabelId() );
			if ( isotopeLabelDTO == null ) {
				String msg = "No Isotope label found for record UnifiedRepPepIsotopeLabelLookupDTO.  UnifiedRepPepIsotopeLabelLookupDTO.id: "
						+ item.getId()
						+ ", item.getIsotopeLabelId(): "
						+ item.getIsotopeLabelId();
				log.error( msg );
				throw new ProxlWebappDataException( msg );
			}
			resultStringPeptideSB.append( isotopeLabelDTO.getName() );
		}
		String modsStringPeptide = resultStringPeptideSB.toString();
		return modsStringPeptide;
	}
}
