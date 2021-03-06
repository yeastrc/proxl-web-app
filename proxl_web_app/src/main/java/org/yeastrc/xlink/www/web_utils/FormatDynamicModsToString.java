package org.yeastrc.xlink.www.web_utils;

import java.util.List;
import org.yeastrc.xlink.dto.UnifiedRepPepDynamicModLookupDTO;

public class FormatDynamicModsToString {
	/**
	 * @param unifiedRpDynamicModListPeptide
	 * @return
	 */
	public static String formatDynamicModsToString( List<UnifiedRepPepDynamicModLookupDTO> unifiedRpDynamicModListPeptide ) {
		if ( unifiedRpDynamicModListPeptide == null || unifiedRpDynamicModListPeptide.isEmpty() ) {
			return "";
		}
		StringBuilder modsStringPeptideSB = new StringBuilder();
		for ( UnifiedRepPepDynamicModLookupDTO UnifiedRpDynamicModDTO : unifiedRpDynamicModListPeptide ) {
			if ( modsStringPeptideSB.length() > 0 )  {
				modsStringPeptideSB.append( ", " );
			}
			if ( UnifiedRpDynamicModDTO.isIs_N_Terminal() ) {
				modsStringPeptideSB.append( "n" );
			} else if ( UnifiedRpDynamicModDTO.isIs_C_Terminal() ) {
				modsStringPeptideSB.append( "c" );
			} else {
				modsStringPeptideSB.append( UnifiedRpDynamicModDTO.getPosition() );
			}
			modsStringPeptideSB.append( "(" );
			modsStringPeptideSB.append( UnifiedRpDynamicModDTO.getMassRoundedString() );
			modsStringPeptideSB.append( ")" );
		}
		String modsStringPeptide = modsStringPeptideSB.toString();
		return modsStringPeptide;
	}
}
